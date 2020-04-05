package com.caoyx.rpc.core.invoker.reference;

import com.caoyx.rpc.core.compress.CompressType;
import com.caoyx.rpc.core.config.CaoyxRpcInvokerConfig;
import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.data.ClassKey;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.invoker.CaoyxRpcFuture;
import com.caoyx.rpc.core.invoker.CaoyxRpcFutureResponse;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerCallBack;
import com.caoyx.rpc.core.invoker.CaoyxRpcPendingInvokerPool;
import com.caoyx.rpc.core.invoker.Invocation;
import com.caoyx.rpc.core.invoker.failback.CaoyxRpcInvokerFailBack;
import com.caoyx.rpc.core.invoker.generic.CaoyxRpcGenericInvoker;
import com.caoyx.rpc.core.loadbalance.LoadBalance;
import com.caoyx.rpc.core.loadbalance.LoadBalanceType;
import com.caoyx.rpc.core.net.api.Client;
import com.caoyx.rpc.core.net.api.ClientManager;
import com.caoyx.rpc.core.net.netty.client.NettyClient;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.NotifyListener;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.register.RegisterType;
import com.caoyx.rpc.core.serialization.SerializerType;
import com.caoyx.rpc.core.url.register.InvokerURL;
import com.caoyx.rpc.core.url.register.ProviderURL;
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
import java.util.concurrent.ConcurrentHashMap;
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


    private ConcurrentHashMap<String, List<ProviderURL>> classKey2ProviderUrl = new ConcurrentHashMap<String, List<ProviderURL>>();

    private Class<?> iFace;
    private String providerApplicationName;
    private int providerImplVersion;
    private String applicationName;

    private RegisterConfig registerConfig;

    private CallType callType;
    private Class<? extends Client> client = NettyClient.class;
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
    private CaoyxRpcInvoker invoker;

    public CaoyxRpcReferenceBean(CaoyxRpcInvokerConfig config) throws CaoyxRpcException {
        if (config.getIFace() == null) {
            throw new CaoyxRpcException("iFace cant be null");
        }
        if (StringUtils.isBlank(config.getApplicationName())) {
            throw new CaoyxRpcException("applicationName can not be null");
        }
        if (StringUtils.isBlank(config.getProviderApplicationName())) {
            throw new CaoyxRpcException("providerApplicationName can not be null");
        }
        if (config.getRegisterConfig() == null) {
            throw new CaoyxRpcException("registerConfig can not be null");

        }
        this.iFace = config.getIFace();
        this.applicationName = config.getApplicationName();
        this.providerApplicationName = config.getProviderApplicationName();
        this.registerConfig = config.getRegisterConfig();
        this.callType = config.getCallType() == null ? CallType.SYNC : config.getCallType();
        this.providerImplVersion = config.getProviderImplVersion();
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
        init();
        invoker = new CaoyxRpcInvoker();
    }


    private CaoyxRpcReferenceBean init() throws CaoyxRpcException {
        CaoyxRpcRegister register = (CaoyxRpcRegister) ExtensionLoader.getExtension(CaoyxRpcRegister.class, registerConfig.getRegisterType().getValue()).getValidExtensionInstance();
        register.initInvokerRegister(registerConfig.getAddress(), applicationName, providerApplicationName);

        InvokerURL url = register.registerInvoker(new ClassKey(iFace.getName(), providerImplVersion));

        List<ProviderURL> providerURLs = register.getProviderURLsByInvokerURL(url);
        classKey2ProviderUrl.put(url.getClassKey(), providerURLs);

        register.subscribe(url, new NotifyListener() {
            @Override
            public void onChange(String classKey, List<ProviderURL> providers) {
                synchronized (classKey.intern()) {
                    log.info("classKey:[" + classKey + "] latest providers: " + providers.toString());
                    classKey2ProviderUrl.put(classKey, providers);
                }
            }
        });
        this.clientManager = new ClientManager();
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
                        int implVersion = CaoyxRpcReferenceBean.this.providerImplVersion;
                        String[] parameterTypes;
                        Object[] arguments = args;
                        long timeout = CaoyxRpcReferenceBean.this.timeout;

                        String classKey;
                        if (className.equals(CaoyxRpcGenericInvoker.class.getName())) {
                            // 泛化调用不支持注册中心
                            if (CaoyxRpcReferenceBean.this.registerConfig.getRegisterType() != RegisterType.DIRECT) {
                                throw new CaoyxRpcException("generic invoker only support direct type");
                            }
                            if (method.getName().equals("invoke")) {
                                className = (String) args[0];
                                implVersion = args[1] == null ? 0 : (Integer) args[1];
                                methodName = (String) args[2];
                                parameterTypes = (String[]) args[3];
                                arguments = (Object[]) args[4];
                                classKey = CaoyxRpcGenericInvoker.class.getName() + "_" + CaoyxRpcReferenceBean.this.providerImplVersion;
                            } else {
                                throw new IllegalStateException(String.valueOf(method));
                            }
                        } else {
                            Class<?>[] parameterClassTypes = method.getParameterTypes();
                            parameterTypes = new String[parameterClassTypes.length];
                            for (int i = 0; i < parameterClassTypes.length; i++) {
                                parameterTypes[i] = parameterClassTypes[i].getName();
                            }
                            classKey = className + "_" + implVersion;
                        }

                        CaoyxRpcRequest rpcRequest = new CaoyxRpcRequest();
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
                        rpcRequest.setCallType(callType.getValue());


                        CaoyxRpcResponse rpcResponse = null;

                        try {
                            rpcRequest.setMetaData(CaoyxRpcContext.getContext().getMetaData());

                            for (int i = 0; i < retryTimes + 1; i++) {
                                ProviderURL providerURL = loadBalance.loadBalance(classKey, classKey2ProviderUrl.get(classKey));
                                if (providerURL == null) {
                                    throw new CaoyxRpcException("LoadBalanceFilter - providerURL is null");
                                }
                                Client clientInstance = clientManager.getOrCreateClient(providerURL, client);

                                Invocation invocation = new Invocation();
                                invocation.setCallType(callType);
                                invocation.setCaoyxRpcInvokerCallBack(caoyxRpcInvokerCallBack);
                                invocation.setCaoyxRpcInvokerFailBack(caoyxRpcInvokerFailBack);
                                invocation.setClientInstance(clientInstance);
                                invocation.setRpcFilters(rpcFilters);

                                rpcResponse = invoker.doInvoke(rpcRequest, invocation);
                                if (rpcResponse.isSuccess()) {
                                    break;
                                }
                                log.error("classKey:[" + classKey + "]" + "caoyxRpc RetryTimes is" + ++i);
                            }
                        } finally {
                            CaoyxRpcContext.removeContext();
                        }

                        if (callType == CallType.FUTURE
                                || callType == CallType.CALLBACK
                                || callType == CallType.ONE_WAY) {
                            Class<?> returnType = method.getReturnType();
                            if (BASIC_DATA_TYPE_2_DEFAULT_VALUE.containsKey(returnType.getName())) {
                                return BASIC_DATA_TYPE_2_DEFAULT_VALUE.get(returnType.getName());
                            }
                            return null;
                        }

                        if (rpcResponse == null || rpcResponse.getStatus() == null) {
                            throw CaoyxRpcException.buildByMsg("rpcResponse is not exist, please check remote invoker");
                        }

                        if (rpcResponse.isSuccess()) {
                            return rpcResponse.getResult();
                        }
                        throw CaoyxRpcException.buildByStatusAndMsg(rpcResponse.getStatus(), rpcResponse.getErrorMsg());
                    }
                });
    }
}