package com.caoyx.rpc.core.reference;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.invoker.CaoyxRpcFutureResponse;
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
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author caoyixiong
 */
@Slf4j
@Data
public class CaoyxRpcReferenceBean {
    private Class<? extends Client> client;

    private CallType callType = CallType.DIRECT;

    private String applicationName;
    private String version;
    private int retryTimes = 1;
    private long timeout = 3 * 1000L;

    private Class<?> iFace;

    private RegisterConfig registerConfig;
    private LoadBalance loadBalance;
    private SerializerAlgorithm serializerAlgorithm;

    private CaoyxRpcInvokerFactory invokerFactory;

    private CaoyxRpcRegister register;

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

                        CaoyxRpcResponse rpcResponse = null;
                        for (int i = 0; i <= (retryTimes < 0 ? 0 : retryTimes); i++) {
                            if (i > 0) {
                                log.info("applicationName:[" + applicationName + "]-className:[" + className + "]-method:[" + methodName + "]==> retry " + i);
                            }
                            Address targetAddress = loadBalance.loadBalance(new ArrayList<>(register.getAllRegister(applicationName, version)));
                            if (targetAddress == null) {
                                throw new CaoyxRpcException("targetAddress is null");
                            }
                            Client clientInstance = clientManager.getOrCreateClient(targetAddress, client, invokerFactory);

                            clientInstance.doSend(rpcRequestPacket);

                            CaoyxRpcFutureResponse futureResponse = new CaoyxRpcFutureResponse(invokerFactory, rpcRequestPacket);
                            rpcResponse = futureResponse.get(timeout, TimeUnit.MILLISECONDS);

                            if (!rpcResponse.isSuccess()) {
                                if (i != retryTimes) {
                                    log.error("errorMsg:[" + rpcResponse.getErrorMsg() + "]" + "caoyxRpc RetryTimes is" + i);
                                }
                                continue;
                            }
                            return rpcResponse.getResult();
                        }
                        if (!rpcResponse.isSuccess()) {
                            throw CaoyxRpcException.buildByStatusAndMsg(rpcResponse.getStatus(), rpcResponse.getErrorMsg());
                        }
                        return rpcResponse.getResult();
                    }
                });
    }
}