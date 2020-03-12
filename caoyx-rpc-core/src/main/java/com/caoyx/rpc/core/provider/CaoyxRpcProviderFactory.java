package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.config.CaoyxRpcProviderConfig;
import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.data.ClassKey;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.net.api.Server;
import com.caoyx.rpc.core.net.netty.server.NettyServer;
import com.caoyx.rpc.core.net.param.ServerInvokerArgs;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.shutdown.GraceFullyShutDownCallBack;
import com.caoyx.rpc.core.shutdown.GracefullyShutDown;
import com.caoyx.rpc.core.utils.CollectionUtils;
import com.caoyx.rpc.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author caoyixiong
 */
@Slf4j
public class CaoyxRpcProviderFactory implements GraceFullyShutDownCallBack {

    private String accessToken;

    private final CaoyxRpcProviderHandler rpcProviderHandler;

    private CaoyxRpcRegister register;

    private final int port;

    private CaoyxRpcProviderConfig providerConfig;

    private final List<CaoyxRpcFilter> caoyxRpcFilters = new ArrayList<>();

    private final CopyOnWriteArrayList<ClassKey> exportServices = new CopyOnWriteArrayList<ClassKey>();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                GracefullyShutDown.INSTANCE.onShutDown();
            }
        }));
    }

    public CaoyxRpcProviderFactory(CaoyxRpcProviderConfig providerConfig) {
        if (StringUtils.isBlank(providerConfig.getApplicationName())) {
            throw new CaoyxRpcException("applicationName can not be empty");
        }
        if (providerConfig.getPort() <= 0) {
            throw new CaoyxRpcException("port can not be 0");
        }
        this.providerConfig = providerConfig;
        this.accessToken = providerConfig.getAccessToken();
        if (CollectionUtils.isNotEmpty(providerConfig.getRpcFilters())) {
            caoyxRpcFilters.addAll(providerConfig.getRpcFilters());
        }
        port = providerConfig.getPort();
        Server server = new NettyServer();
        server.start(providerConfig.getPort(), this);
        if (providerConfig.getRegisterConfig() != null) {
            register = (CaoyxRpcRegister) ExtensionLoader.getExtension(CaoyxRpcRegister.class, providerConfig.getRegisterConfig().getRegisterType().getValue()).getValidExtensionInstance();
            register.initProviderRegister(providerConfig.getRegisterConfig().getAddress(), providerConfig.getApplicationName(), providerConfig.getPort());
        }
        this.rpcProviderHandler = new CaoyxRpcProviderHandler();
        GracefullyShutDown.INSTANCE.addCallBack(this);
        GracefullyShutDown.INSTANCE.addCallBack(server);
    }

    public void exportService(Class clazz, Object service) {
        exportService(clazz.getName(), service);
    }

    public void exportService(String className, Object service) {
        exportService(className, 0, service);
    }

    public void exportService(String className, int implVersion, Object service) {
        boolean success = rpcProviderHandler.exportService(className, implVersion, service);
        if (success) {
            ClassKey classKey = new ClassKey(className, implVersion);
            exportServices.addIfAbsent(classKey);
            if (register != null) {
                register.registerProvider(classKey, port);
            }
        }
        log.info("exportService: className[" + className + "] implVersion:[" + implVersion + "] success:[" + success + "]");
    }

    public CaoyxRpcResponse invoke(ServerInvokerArgs serverInvokerArgs) throws Exception {
        try {
            CaoyxRpcRequest request = serverInvokerArgs.getRequestPacket();
            if (StringUtils.isNotBlank(accessToken)) {
                if (!accessToken.equals(request.getAccessToken())) {
                    return CaoyxRpcResponse.buildIllegalResponse("accessToken is not legal，with accessToken is " + request.getAccessToken());
                }
            }
            for (int i = 0; i < caoyxRpcFilters.size(); i++) {
                caoyxRpcFilters.get(i).invokeRequestHandler(request);
            }
            CaoyxRpcResponse response = rpcProviderHandler.invoke(request);
            for (int i = caoyxRpcFilters.size() - 1; i >= 0; i--) {
                caoyxRpcFilters.get(i).invokeResponseHandler(response);
            }
            return response;
        } finally {
            CaoyxRpcContext.removeContext();
        }
    }

    @Override
    public void shutdownGracefully() {
        if (register != null) {
            for (ClassKey classKey : exportServices) {
                register.unRegisterProvider(classKey, providerConfig.getPort());
                log.info("unExportService: className[" + classKey.getClassName() + "] implVersion:[" + classKey.getVersion() + "] success on ShutDown");
            }
        }
    }
}