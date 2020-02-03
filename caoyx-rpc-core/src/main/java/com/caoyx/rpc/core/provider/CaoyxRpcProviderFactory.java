package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.net.api.Server;
import com.caoyx.rpc.core.net.param.ServerInvokerArgs;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.utils.CollectionUtils;
import com.caoyx.rpc.core.utils.NetUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caoyixiong
 */
@Slf4j
public class CaoyxRpcProviderFactory {

    @Setter
    private Server server;
    @Setter
    private RegisterConfig registerConfig;
    @Setter
    private String applicationName;
    @Setter
    @Getter
    private int port = 1118;
    @Setter
    private String applicationVersion;
    @Setter
    private String accessToken = null;

    private final CaoyxRpcProviderHandler rpcProviderHandler;
    private final List<CaoyxRpcFilter> caoyxRpcFilters = new ArrayList<>();

    public CaoyxRpcProviderFactory(String applicationName,
                                   Server server,
                                   RegisterConfig registerConfig,
                                   String applicationVersion,
                                   List<CaoyxRpcFilter> rpcFilters) {
        this.applicationName = applicationName;
        this.server = server;
        this.registerConfig = registerConfig;
        this.applicationVersion = applicationVersion;
        this.rpcProviderHandler = new CaoyxRpcProviderHandler();
        if (CollectionUtils.isNotEmpty(rpcFilters)) {
            caoyxRpcFilters.addAll(rpcFilters);
        }
    }

    public void init() throws CaoyxRpcException {
        server.start(this);
        if (registerConfig != null) {
            CaoyxRpcRegister register = (CaoyxRpcRegister) ExtensionLoader.getExtension(CaoyxRpcRegister.class, registerConfig.getRegisterName()).getValidExtensionInstance();
            register.initRegister(applicationName, applicationVersion);
            register.initRegisterConnect(registerConfig.getRegisterAddress());
            register.register(NetUtils.getLocalAddress(), port);
        }

    }

    public void addServiceProvider(String className, String implVersion, Object service) {
        rpcProviderHandler.addServiceMethodProvider(className, implVersion, service);
        log.info("className:[" + className + "],implVersion:[" + implVersion + "] export successfully");

    }

    public CaoyxRpcResponse invoke(ServerInvokerArgs serverInvokerArgs) throws Exception {
        try {
            CaoyxRpcRequest request = serverInvokerArgs.getRequestPacket();
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
}