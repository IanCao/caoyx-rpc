package com.caoyx.rpc.core.invoker.reference;

import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.filter.CaoyxRpcFilterManager;
import com.caoyx.rpc.core.filter.invokerFilter.InvokerRetryFilter;
import com.caoyx.rpc.core.filter.invokerFilter.LoadBalanceInvokerFilter;
import com.caoyx.rpc.core.filter.invokerFilter.RemoteInvokerFilter;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerCallBack;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerFactory;
import com.caoyx.rpc.core.invoker.failback.CaoyxRpcInvokerFailBack;
import com.caoyx.rpc.core.invoker.generic.CaoyxRpcGenericInvoker;
import com.caoyx.rpc.core.loadbalance.LoadBalance;
import com.caoyx.rpc.core.loadbalance.LoadBalanceType;
import com.caoyx.rpc.core.net.api.Client;
import com.caoyx.rpc.core.net.api.ClientManager;
import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.serialization.api.SerializerAlgorithm;
import com.caoyx.rpc.core.utils.CollectionUtils;
import com.caoyx.rpc.core.utils.NetUtils;
import com.caoyx.rpc.core.utils.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

    @Setter
    private Class<? extends Client> client;
    @Setter
    private CallType callType = CallType.SYNC;
    @Setter
    private String applicationName;
    @Setter
    private String applicationVersion;
    @Setter
    private String implVersion;
    @Setter
    private int retryTimes = 1;
    @Setter
    private long timeout = 3 * 1000L;
    @Setter
    private Class<?> iFace;
    @Setter
    private RegisterConfig registerConfig;

    private LoadBalance loadBalance;
    @Setter
    private SerializerAlgorithm serializerAlgorithm;
    @Setter
    private CaoyxRpcRegister register;
    @Setter
    private CaoyxRpcInvokerCallBack caoyxRpcInvokerCallBack;
    @Setter
    private String accessToken;
    @Setter
    private CaoyxRpcInvokerFailBack caoyxRpcInvokerFailBack;

    private CaoyxRpcFilterManager rpcFilterManager;

    public CaoyxRpcReferenceBean(Class<?> iFace,
                                 String implVersion,
                                 String applicationVersion,
                                 String applicationName,
                                 RegisterConfig registerConfig,
                                 Class<? extends Client> client,
                                 SerializerAlgorithm serializerAlgorithm,
                                 LoadBalanceType loadBalanceType,
                                 List<CaoyxRpcFilter> rpcFilters) throws CaoyxRpcException {
        this.iFace = iFace;
        this.implVersion = implVersion;
        this.applicationVersion = applicationVersion;
        this.applicationName = applicationName;
        this.client = client;
        this.serializerAlgorithm = serializerAlgorithm;
        this.registerConfig = registerConfig;

        this.rpcFilterManager = new CaoyxRpcFilterManager();
        rpcFilterManager.addAllUserFilters(rpcFilters);

        this.loadBalance = (LoadBalance) ExtensionLoader.getExtension(LoadBalance.class, loadBalanceType.getValue()).getValidExtensionInstance();
    }


    public CaoyxRpcReferenceBean init() throws CaoyxRpcException {
        if (registerConfig != null) {
            register = (CaoyxRpcRegister) ExtensionLoader.getExtension(CaoyxRpcRegister.class, registerConfig.getRegisterName()).getValidExtensionInstance();
            register.initRegister(applicationName, applicationVersion);
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

        ClientManager clientManager = new ClientManager();
        register.addOnChangeCallBack(clientManager);

        //负载均衡 远程调用
        LoadBalanceInvokerFilter loadBalanceInvokerFilter = new LoadBalanceInvokerFilter(
                new RemoteInvokerFilter(clientManager, client, CaoyxRpcInvokerFactory.getInstance()),
                loadBalance,
                register);

        //失败重试
        InvokerRetryFilter retryFilter = new InvokerRetryFilter(loadBalanceInvokerFilter, retryTimes);

        rpcFilterManager.addSystemFilterLast(retryFilter);
        rpcFilterManager.addSystemFilterLast(loadBalanceInvokerFilter);

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
                        String implVersion = CaoyxRpcReferenceBean.this.implVersion;
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

                        CaoyxRpcContext.getContext().setCallType(callType);
                        CaoyxRpcContext.getContext().setCallBack(caoyxRpcInvokerCallBack);
                        try {
                            rpcRequest.setRequestId(UUID.randomUUID().toString());
                            rpcRequest.setApplicationName(applicationName);
                            rpcRequest.setApplicationVersion(applicationVersion);
                            rpcRequest.setImplVersion(implVersion);
                            rpcRequest.setSerializerAlgorithm(serializerAlgorithm.getAlgorithmId());
                            rpcRequest.setClassName(className);
                            rpcRequest.setMethodName(methodName);
                            rpcRequest.setParameters(arguments);
                            rpcRequest.setParameterTypes(parameterTypes);
                            rpcRequest.setCreatedTimeMills(System.currentTimeMillis());
                            rpcRequest.setTimeout(timeout);
                            rpcRequest.setAccessToken(accessToken);
                            rpcRequest.setInvokerAddress(NetUtils.getLocalAddress());
                            rpcFilterManager.invoke(rpcRequest, rpcResponse);
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