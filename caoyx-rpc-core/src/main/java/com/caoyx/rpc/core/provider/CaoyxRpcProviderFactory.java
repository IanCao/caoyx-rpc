package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.filter.CaoyxRpcFilterManager;
import com.caoyx.rpc.core.net.api.Server;
import com.caoyx.rpc.core.net.param.ServerInvokerArgs;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.utils.NetUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

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
    private CaoyxRpcProviderHandler rpcProviderHandler;
    @Setter
    private String accessToken = null;

    private CaoyxRpcFilterManager rpcFilterManager;

    public CaoyxRpcProviderFactory(String applicationName,
                                   Server server,
                                   RegisterConfig registerConfig,
                                   String applicationVersion,
                                   List<CaoyxRpcFilter> rpcFilters) {
        this.applicationName = applicationName;
        this.server = server;
        this.registerConfig = registerConfig;
        this.applicationVersion = applicationVersion;
        this.rpcFilterManager = new CaoyxRpcFilterManager();
        this.rpcFilterManager.addAllUserFilters(rpcFilters);

        this.rpcProviderHandler = new CaoyxRpcProviderHandler();
    }

    public void init() throws CaoyxRpcException {
        server.start(this);
        if (registerConfig != null) {
            CaoyxRpcRegister register = (CaoyxRpcRegister) ExtensionLoader.getExtension(CaoyxRpcRegister.class, registerConfig.getRegisterName()).getValidExtensionInstance();
            register.initRegister(applicationName, applicationVersion);
            register.initRegisterConnect(registerConfig.getRegisterAddress());
            register.register(NetUtils.getLocalAddress(), port);
        }

        rpcFilterManager.addSystemFilterLast(rpcProviderHandler);
    }

    public void onExportSuccess() {
        log.info("applicationName:[" + applicationName + "],applicationVersion:[" + applicationVersion + "], port:[" + this.port + "] export successfully");
    }

    public void onExportFail() {
        log.info("applicationName:[" + applicationName + "],applicationVersion:[" + applicationVersion + "], port:[" + this.port + "] export fail");
    }

    public void addServiceBean(String className, String implVersion, Object service) {
        rpcProviderHandler.addServiceBean(className, implVersion, service);
        log.info("className:[" + className + "],implVersion:[" + implVersion + "] export successfully");

    }

    public CaoyxRpcResponse invoke(ServerInvokerArgs serverInvokerArgs) throws Exception {
        try {
            CaoyxRpcContext caoyxRpcContext = CaoyxRpcContext.getContext();
            caoyxRpcContext.setRemoteAddress(serverInvokerArgs.getRemoteAddress());
            caoyxRpcContext.setMetaData(serverInvokerArgs.getRequestPacket().getMetaData());
            CaoyxRpcResponse caoyxRpcResponse = new CaoyxRpcResponse();
            rpcFilterManager.invoke(serverInvokerArgs.getRequestPacket(), caoyxRpcResponse);
            return caoyxRpcResponse;
        } finally {
            CaoyxRpcContext.removeContext();
        }
    }
}