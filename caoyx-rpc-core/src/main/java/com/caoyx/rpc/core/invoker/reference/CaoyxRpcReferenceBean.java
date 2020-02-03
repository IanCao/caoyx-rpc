package com.caoyx.rpc.core.invoker.reference;

import com.caoyx.rpc.core.compress.CompressType;
import com.caoyx.rpc.core.config.CaoyxRpcInvokerConfig;
import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.invoker.CaoyxRpcFuture;
import com.caoyx.rpc.core.invoker.CaoyxRpcFutureResponse;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerCallBack;
import com.caoyx.rpc.core.invoker.CaoyxRpcPendingInvokerPool;
import com.caoyx.rpc.core.invoker.failback.CaoyxRpcInvokerFailBack;
import com.caoyx.rpc.core.invoker.generic.CaoyxRpcGenericInvoker;
import com.caoyx.rpc.core.loadbalance.LoadBalance;
import com.caoyx.rpc.core.loadbalance.LoadBalanceType;
import com.caoyx.rpc.core.net.api.Client;
import com.caoyx.rpc.core.net.api.ClientManager;
import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.net.netty.client.NettyClient;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.serialization.SerializerType;
import com.caoyx.rpc.core.utils.CollectionUtils;
import com.caoyx.rpc.core.utils.MethodUtils;
import com.caoyx.rpc.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author caoyixiong
 */
@Slf4j
public class CaoyxRpcReferenceBean {

    private static final Map<String, Object> BASIC_DATA_TYPE_2_DEFAULT_VALUE = new HashMap<String, Object>() {{
        put("boolean", Boolean.FALSE);
        put("int", Integer.MIN_VALUE);
        put("float", Float.MIN_VALUE);
        put("double", Double.MIN_VALUE);
        put("long", Long.MIN_VALUE);
        put("short", Short.MIN_VALUE);
        put("byte", Byte.MIN_VALUE);
        put("char", Character.MIN_VALUE);
    }};


    private Class<?> iFace;
    private String remoteApplicationName;
    private RegisterConfig registerConfig;

    private CallType callType;
    private Class<? extends Client> client = NettyClient.class;
    private String remoteApplicationVersion;
    private String remoteImplVersion;
    private int retryTimes;
    private long timeout;
    private LoadBalance loadBalance;
    private SerializerType serializerType;
    private CompressType compressType;
    private CaoyxRpcInvokerCallBack caoyxRpcInvokerCallBack;
    private String accessToken;
    private CaoyxRpcInvokerFailBack caoyxRpcInvokerFailBack;
    private List<CaoyxRpcFilter> rpcFilters = new ArrayList<>();

    private ClientManager clientManager;
    private CaoyxRpcRegister register;

    public CaoyxRpcReferenceBean(CaoyxRpcInvokerConfig config) throws CaoyxRpcException {
        if (config.getIFace() == null) {
            throw new CaoyxRpcException("iFace cant be null");
        }
        if (StringUtils.isBlank(config.getRemoteApplicationName())) {
            throw new CaoyxRpcException("remoteApplicationName cant be null");
        }
        if (config.getRegisterConfig() == null) {
            throw new CaoyxRpcException("registerConfig cant be null");

        }
        this.iFace = config.getIFace();
        this.remoteApplicationName = config.getRemoteApplicationName();
        this.registerConfig = config.getRegisterConfig();
        this.callType = config.getCallType() == null ? CallType.SYNC : config.getCallType();
        this.remoteApplicationVersion = StringUtils.isBlank(config.getRemoteApplicationVersion()) ? "0" : config.getRemoteApplicationVersion();
        this.remoteImplVersion = StringUtils.isBlank(config.getRemoteImplVersion()) ? "0" : config.getRemoteImplVersion();
        this.retryTimes = config.getRetryTimes();
        this.serializerType = config.getSerializerType() == null ? SerializerType.PROTOSTUFF : config.getSerializerType();
        this.compressType = config.getCompressType() == null ? CompressType.LZ4 : config.getCompressType();
        this.timeout = config.getTimeout() == 0 ? 3 * 1000L : config.getTimeout();
        this.accessToken = config.getAccessToken();
        this.caoyxRpcInvokerCallBack = config.getCaoyxRpcInvokerCallBack();
        this.caoyxRpcInvokerFailBack = config.getCaoyxRpcInvokerFailBack();

        if (CollectionUtils.isNotEmpty(config.getRpcFilters())) {
            this.rpcFilters.addAll(config.getRpcFilters());
        }
        this.loadBalance = (LoadBalance) ExtensionLoader.getExtension(LoadBalance.class,
                config.getLoadBalanceType() == null ? LoadBalanceType.RANDOM.getValue() : config.getLoadBalanceType().getValue()).getValidExtensionInstance();
    }


    public CaoyxRpcReferenceBean init() throws CaoyxRpcException {
        if (registerConfig != null) {
            register = (CaoyxRpcRegister) ExtensionLoader.getExtension(CaoyxRpcRegister.class, registerConfig.getRegisterName()).getValidExtensionInstance();
            register.initRegister(remoteApplicationName, remoteApplicationVersion);
            register.initRegisterConnect(registerConfig.getRegisterAddress());

            List<String> loadAddresses = registerConfig.getLoadAddresses();
            if (CollectionUtils.isNotEmpty(loadAddresses)) {
                for (String addressString : loadAddresses) {
                    if (StringUtils.isBlank(addressString)) {
                        continue;
                    }
                    String[] address = addressString.split(":");
                    register.loadAddress(new Address(address[0], Integer.valueOf(address[1])));
                }
            }
            register.startRegisterLoopFetch();
        }

        this.clientManager = new ClientManager();
        register.addOnChangeCallBack(clientManager);

        return this;
    }

    public Object getObject() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
                , new Class[]{iFace}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (Object.class == method.getDeclaringClass()) {
                            String name = method.getName();
                            if ("equals".equals(name)) {
                                return proxy == args[0];
                            } else if ("hashCode".equals(name)) {
                                return System.identityHashCode(proxy);
                            } else if ("toString".equals(name)) {
                                return proxy.getClass().getName() + "@" +
                                        Integer.toHexString(System.identityHashCode(proxy)) +
                                        ", with InvocationHandler " + this;
                            } else {
                                throw new IllegalStateException(String.valueOf(method));
                            }
                        }
                        String className = method.getDeclaringClass().getName();
                        String methodName = method.getName();
                        String implVersion = CaoyxRpcReferenceBean.this.remoteImplVersion;
                        String[] parameterTypes;
                        Object[] arguments = args;
                        long timeout = CaoyxRpcReferenceBean.this.timeout;
                        if (className.equals(CaoyxRpcGenericInvoker.class.getName())) {
                            if (method.getName().equals("invoke")) {
                                className = (String) args[0];
                                implVersion = (String) args[1];
                                methodName = (String) args[2];
                                parameterTypes = (String[]) args[3];
                                arguments = (Object[]) args[4];
                            } else {
                                throw new IllegalStateException(String.valueOf(method));
                            }
                        } else {
                            Class<?>[] parameterClassTypes = method.getParameterTypes();
                            parameterTypes = new String[parameterClassTypes.length];
                            for (int i = 0; i < parameterClassTypes.length; i++) {
                                parameterTypes[i] = parameterClassTypes[i].getName();
                            }

                        }

                        CaoyxRpcRequest rpcRequest = new CaoyxRpcRequest();
                        CaoyxRpcResponse rpcResponse = new CaoyxRpcResponse();

                        try {
                            rpcRequest.setRequestId(UUID.randomUUID().toString());
                            rpcRequest.setImplVersion(implVersion);
                            rpcRequest.setSerializerType(serializerType.getType());
                            rpcRequest.setCompressType(compressType.getType());
                            rpcRequest.setClassName(className);
                            rpcRequest.setMethodKey(MethodUtils.generateMethodKey(methodName, parameterTypes));
                            rpcRequest.setParameters(arguments);
                            rpcRequest.setCreatedTimeMills(System.currentTimeMillis());
                            rpcRequest.setTimeout(timeout);
                            rpcRequest.setAccessToken(accessToken);

                            for (int i = 0; i < retryTimes + 1; i++) {
                                Address remoteAddress = loadBalance.loadBalance(rpcRequest.getInvokerInfo(), register.getAllRegister(remoteApplicationName, remoteApplicationVersion));
                                if (remoteAddress == null) {
                                    throw new CaoyxRpcException("LoadBalanceFilter - targetAddress is null");
                                }
                                Client clientInstance = clientManager.getOrCreateClient(remoteAddress, client);
                                rpcRequest.setMetaData(CaoyxRpcContext.getContext().getMetaData());

                                for (int j = 0; j < rpcFilters.size(); j++) {
                                    rpcFilters.get(j).invokeRequestHandler(rpcRequest);
                                }

                                CaoyxRpcFutureResponse futureResponse = new CaoyxRpcFutureResponse(rpcRequest);
                                CaoyxRpcPendingInvokerPool.INSTANCE.setPendingInvoke(rpcRequest.getRequestId(), futureResponse);
                                clientInstance.doSend(rpcRequest);

                                switch (callType) {
                                    case SYNC:
                                        CaoyxRpcResponse caoyxRpcResponse = futureResponse.get(rpcRequest.getTimeout(), TimeUnit.MILLISECONDS);
                                        rpcResponse.setStatus(caoyxRpcResponse.getStatus());
                                        rpcResponse.setErrorMsg(caoyxRpcResponse.getErrorMsg());
                                        rpcResponse.setResult(caoyxRpcResponse.getResult());
                                        break;
                                    case FUTURE:
                                        CaoyxRpcFuture caoyxRpcFuture = new CaoyxRpcFuture();
                                        caoyxRpcFuture.setFutureResponse(futureResponse);
                                        caoyxRpcFuture.setTimeout(rpcRequest.getTimeout());
                                        caoyxRpcFuture.setUnit(TimeUnit.MILLISECONDS);
                                        CaoyxRpcFuture.setFuture(caoyxRpcFuture);
                                        rpcResponse.setStatus(CaoyxRpcStatus.SUCCESS);
                                        break;
                                    case CALLBACK:
                                        rpcResponse.setStatus(CaoyxRpcStatus.SUCCESS);
                                        futureResponse.setCaoyxRpcInvokerCallBack(caoyxRpcInvokerCallBack);
                                }
                                if (rpcResponse.isSuccess()) {
                                    for (int j = rpcFilters.size() - 1; j >= 0; j--) {
                                        rpcFilters.get(j).invokeResponseHandler(rpcResponse);
                                    }
                                    break;
                                } else {
                                    log.error("remoteInvoker:[" + rpcRequest.getInvokerInfo() + "]" + "caoyxRpc RetryTimes is" + ++i);
                                }
                            }
                        } finally {
                            CaoyxRpcContext.removeContext();
                        }

                        if (callType == CallType.FUTURE || callType == CallType.CALLBACK) {
                            Class<?> returnType = method.getReturnType();
                            if (BASIC_DATA_TYPE_2_DEFAULT_VALUE.containsKey(returnType.getName())) {
                                return BASIC_DATA_TYPE_2_DEFAULT_VALUE.get(returnType.getName());
                            }
                            return null;
                        }

                        if (rpcResponse.getStatus() == null) {
                            throw CaoyxRpcException.buildByMsg("rpcResponse is not exist, please check remote invoker");
                        }

                        if (rpcResponse.isSuccess()) {
                            return rpcResponse.getResult();
                        }

                        if (caoyxRpcInvokerFailBack != null) {
                            if (rpcResponse.getStatus() == CaoyxRpcStatus.FAIL) {
                                return caoyxRpcInvokerFailBack.onFail(rpcResponse.getErrorMsg());
                            }
                            if (rpcResponse.getStatus() == CaoyxRpcStatus.TIMEOUT) {
                                return caoyxRpcInvokerFailBack.onTimeout();
                            }
                        }
                        throw CaoyxRpcException.buildByStatusAndMsg(rpcResponse.getStatus(), rpcResponse.getErrorMsg());
                    }
                });
    }
}