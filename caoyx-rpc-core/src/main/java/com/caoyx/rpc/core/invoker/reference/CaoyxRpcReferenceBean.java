package com.caoyx.rpc.core.invoker.reference;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.filter.CaoyxRpcFilterManager;
import com.caoyx.rpc.core.filter.invokerFilter.InvokerContextFilter;
import com.caoyx.rpc.core.filter.invokerFilter.InvokerRetryFilter;
import com.caoyx.rpc.core.filter.invokerFilter.LoadBalanceInvokerFilter;
import com.caoyx.rpc.core.filter.invokerFilter.RemoteInvokerFilter;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerFactory;
import com.caoyx.rpc.core.loadbalance.LoadBalance;
import com.caoyx.rpc.core.netty.client.Client;
import com.caoyx.rpc.core.netty.client.ClientManager;
import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.serializer.SerializerAlgorithm;
import com.caoyx.rpc.core.utils.CollectionUtils;
import com.caoyx.rpc.core.utils.StringUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.UUID;

/**
 * @author caoyixiong
 */
@Slf4j
public class CaoyxRpcReferenceBean {

    @Setter
    private Class<? extends Client> client;
    @Setter
    private CallType callType = CallType.DIRECT;

    @Setter
    private String applicationName;
    @Setter
    private String version;
    @Setter
    private int retryTimes = 1;
    @Setter
    private long timeout = 3 * 1000L;
    @Setter
    private Class<?> iFace;
    @Setter
    private RegisterConfig registerConfig;
    @Setter
    private LoadBalance loadBalance;
    @Setter
    private SerializerAlgorithm serializerAlgorithm;
    @Setter
    private CaoyxRpcRegister register;

    private CaoyxRpcFilterManager rpcFilterManager;

    public CaoyxRpcReferenceBean(Class<?> iFace,
                                 String version,
                                 String applicationName,
                                 RegisterConfig registerConfig,
                                 Class<? extends Client> client,
                                 SerializerAlgorithm serializerAlgorithm,
                                 List<CaoyxRpcFilter> rpcFilters) {
        this.iFace = iFace;
        this.version = version;
        this.applicationName = applicationName;
        this.client = client;
        this.serializerAlgorithm = serializerAlgorithm;
        this.registerConfig = registerConfig;

        this.rpcFilterManager = new CaoyxRpcFilterManager();
        rpcFilterManager.addAll(rpcFilters);
    }


    public CaoyxRpcReferenceBean init() throws Exception {
        if (registerConfig != null) {
            register = (CaoyxRpcRegister) ExtensionLoader.getExtension(CaoyxRpcRegister.class, registerConfig.getRegisterName()).getValidExtensionInstance();
            register.initRegister(applicationName, version);
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

        //负载均衡 远程调用
        LoadBalanceInvokerFilter loadBalanceInvokerFilter = new LoadBalanceInvokerFilter(
                new RemoteInvokerFilter(new ClientManager(), client, CaoyxRpcInvokerFactory.getInstance()),
                loadBalance,
                register);

        //失败重试
        InvokerRetryFilter retryFilter = new InvokerRetryFilter(loadBalanceInvokerFilter, retryTimes);
        //CaoyxRpc Context
        InvokerContextFilter invokerContextFilter = new InvokerContextFilter();

        rpcFilterManager.addFirst(invokerContextFilter);
        rpcFilterManager.addLast(retryFilter);
        rpcFilterManager.addLast(loadBalanceInvokerFilter);

        return this;
    }

    public Object getObject() {
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader()
                , new Class[]{iFace}, new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        String className = method.getDeclaringClass().getName();
                        String methodName = method.getName();
                        Class<?>[] parameterTypes = method.getParameterTypes();

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

                        CaoyxRpcRequest rpcRequest = new CaoyxRpcRequest();
                        rpcRequest.setRequestId(UUID.randomUUID().toString());
                        rpcRequest.setApplicationName(applicationName);
                        rpcRequest.setVersion(version);
                        rpcRequest.setSerializerAlgorithm(serializerAlgorithm.getAlgorithmId());
                        rpcRequest.setClassName(className);
                        rpcRequest.setMethodName(methodName);
                        rpcRequest.setParameters(args);
                        rpcRequest.setParameterTypes(parameterTypes);
                        rpcRequest.setCreatedTimeMills(System.currentTimeMillis());
                        rpcRequest.setTimeout(timeout);

                        CaoyxRpcResponse rpcResponse = new CaoyxRpcResponse();

                        rpcFilterManager.invoke(rpcRequest, rpcResponse);

                        if (rpcResponse.getStatus() == null) {
                            throw CaoyxRpcException.buildByMsg("rpcResponse is not exist, please check remote invoker");
                        }
                        if (!rpcResponse.isSuccess()) {
                            throw CaoyxRpcException.buildByStatusAndMsg(rpcResponse.getStatus(), rpcResponse.getErrorMsg());
                        }
                        return rpcResponse.getResult();
                    }
                });
    }
}