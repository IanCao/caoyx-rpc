package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.netty.server.Server;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.serializer.Serializer;
import com.caoyx.rpc.core.utils.NetUtils;
import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author caoyixiong
 */
@Data
public class CaoyxRpcProviderFactory {

    private Server server;
    private Serializer serializer;
    private RegisterConfig registerConfig;

    private String applicationName;
    private int port = 1118;
    private String version;

    private String accessToken = null;


    public CaoyxRpcProviderFactory(String applicationName,
                                   Server server,
                                   Serializer serializer,
                                   RegisterConfig registerConfig,
                                   String version) {
        this.applicationName = applicationName;
        this.server = server;
        this.serializer = serializer;
        this.registerConfig = registerConfig;
        this.version = version;
    }

    public void init() throws InterruptedException, CaoyxRpcException {
        server.start(this);
        if (registerConfig != null) {
            CaoyxRpcRegister register = (CaoyxRpcRegister) ExtensionLoader.getExtension(CaoyxRpcRegister.class, registerConfig.getRegisterName()).getValidExtensionInstance();
            register.initRegister(applicationName, version);
            register.initRegisterConnect(registerConfig.getRegisterAddress());
            register.register(NetUtils.getLocalAddress(), port);
        }
    }

    private ConcurrentHashMap<String, Object> serviceBeanMap = new ConcurrentHashMap<String, Object>();

    public void addServiceBean(String className, String version, Object service) {
        String key = className + "@" + version;
        serviceBeanMap.putIfAbsent(key, service);
    }

    public Object getServiceBean(String className, String version) {
        String key = className + "@" + version;
        return serviceBeanMap.get(key);
    }

    public CaoyxRpcResponse invokeService(CaoyxRpcRequest requestPacket) {
        System.out.println("invokeService CaoyxRpcRequest: " + requestPacket.getClassName());
        CaoyxRpcResponse responsePacket = new CaoyxRpcResponse();
        responsePacket.setRequestId(requestPacket.getRequestId());

        Object serviceBean = getServiceBean(requestPacket.getClassName(), requestPacket.getVersion());

        Class clazz = serviceBean.getClass();
        String methodName = requestPacket.getMethodName();
        Class<?>[] parameterTypes = requestPacket.getParameterTypes();
        Object[] parameters = requestPacket.getParameters();

        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            Object result = method.invoke(serviceBean, parameters);
            responsePacket.setResult(result);
        } catch (InvocationTargetException e) {
            responsePacket.setStatus(CaoyxRpcStatus.FAIL);
            responsePacket.setErrorMsg(e.getTargetException().getMessage());
        } catch (Throwable e) {
            responsePacket.setStatus(CaoyxRpcStatus.FAIL);
            responsePacket.setErrorMsg(e.getMessage());
        }
        return responsePacket;
    }
}