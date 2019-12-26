package com.caoyx.rpc.core.reference;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.invoker.CaoyxRpcFutureResponse;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerFactory;
import com.caoyx.rpc.core.netty.client.Client;
import com.caoyx.rpc.core.netty.client.NettyClient;
import com.caoyx.rpc.core.serializer.Serializer;
import com.caoyx.rpc.core.serializer.impl.JDKSerializerImpl;
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
    private Class<? extends Client> client = NettyClient.class;
    private Class<? extends Serializer> serializer = JDKSerializerImpl.class;
    private String ip;
    private int port;
    private int version;
    private Class<?> iFace;
    //TODO 负载均衡

    private CaoyxRpcInvokerFactory invokerFactory;


    public CaoyxRpcReferenceBean(String ip, int port, Class<?> iFace, int version) {
        this.ip = ip;
        this.port = port;
        this.iFace = iFace;
        this.version = version;
    }

    private Client clientInstance = null;
    private Serializer serializerInstance = null;

    public CaoyxRpcReferenceBean init() throws IllegalAccessException, InstantiationException, InterruptedException {
        clientInstance = client.newInstance();
        serializerInstance = serializer.newInstance();
        if (invokerFactory == null) {
            invokerFactory = CaoyxRpcInvokerFactory.getInstance();
        }
        clientInstance.init(this);
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
                        rpcRequestPacket.setVersion(version);
                        rpcRequestPacket.setClassName(className);
                        rpcRequestPacket.setMethodName(methodName);
                        rpcRequestPacket.setParameters(args);
                        rpcRequestPacket.setParameterTypes(parameterTypes);
                        rpcRequestPacket.setCreatedTimeMills(System.currentTimeMillis());

                        clientInstance.doSend(rpcRequestPacket);

                        CaoyxRpcFutureResponse futureResponse = new CaoyxRpcFutureResponse(invokerFactory, rpcRequestPacket);
                        CaoyxRpcResponse rpcResponse = futureResponse.get(3000L, TimeUnit.MILLISECONDS);
                        return rpcResponse.getResult();
                    }
                });
    }
}