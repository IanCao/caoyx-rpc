package com.caoyx.rpc.core.reference;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.invoker.CaoyxRpcFutureResponse;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerFactory;
import com.caoyx.rpc.core.netty.client.Client;
import com.caoyx.rpc.core.netty.client.ClientManager;
import com.caoyx.rpc.core.rebalance.Rebalance;
import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.register.Register;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.serializer.SerializerAlgorithm;
import lombok.Data;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author caoyixiong
 */
@Data
public class CaoyxRpcReferenceBean {
    private Class<? extends Client> client;

    private CallType callType = CallType.DIRECT;

    private String applicationName;
    private String version;
    private Class<?> iFace;

    private RegisterConfig registerConfig;
    private Rebalance rebalance;
    private SerializerAlgorithm serializerAlgorithm;

    private CaoyxRpcInvokerFactory invokerFactory;

    public CaoyxRpcReferenceBean(Class<?> iFace,
                                 String version,
                                 String applicationName,
                                 RegisterConfig registerConfig,
                                 Class<? extends Client> client,
                                 SerializerAlgorithm serializerAlgorithm) {
        this.iFace = iFace;
        this.version = version;
        this.applicationName = applicationName;
        this.client = client;
        this.serializerAlgorithm = serializerAlgorithm;
        this.registerConfig = registerConfig;
    }

    private ClientManager clientManager = null;

    public CaoyxRpcReferenceBean init() throws Exception {
        clientManager = new ClientManager();

        if (invokerFactory == null) {
            invokerFactory = CaoyxRpcInvokerFactory.getInstance();
        }
        if (registerConfig != null) {
            Register register = registerConfig.getRegister();
            register.initRegister(applicationName, version);
            register.initRegisterConnect(registerConfig.getRegisterAddress());
            register.startRegisterLoopFetch();
        }
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

                        CaoyxRpcRequest rpcRequestPacket = new CaoyxRpcRequest();
                        rpcRequestPacket.setRequestId(UUID.randomUUID().toString());
                        rpcRequestPacket.setApplicationName(applicationName);
                        rpcRequestPacket.setVersion(version);
                        rpcRequestPacket.setSerializerAlgorithm(serializerAlgorithm.getAlgorithmId());
                        rpcRequestPacket.setClassName(className);
                        rpcRequestPacket.setMethodName(methodName);
                        rpcRequestPacket.setParameters(args);
                        rpcRequestPacket.setParameterTypes(parameterTypes);
                        rpcRequestPacket.setCreatedTimeMills(System.currentTimeMillis());

                        //负载均衡
                        Address targetAddress = rebalance.rebalance(registerConfig.getRegister().getAllRegister(applicationName, version));
                        if (targetAddress == null) {
                            throw new CaoyxRpcException("targetAddress is null");
                        }
                        Client clientInstance = clientManager.getOrCreateClient(targetAddress, client, invokerFactory);

                        clientInstance.doSend(rpcRequestPacket);

                        CaoyxRpcFutureResponse futureResponse = new CaoyxRpcFutureResponse(invokerFactory, rpcRequestPacket);
                        CaoyxRpcResponse rpcResponse = futureResponse.get(3000L, TimeUnit.MILLISECONDS);
                        return rpcResponse.getResult();
                    }
                });
    }
}