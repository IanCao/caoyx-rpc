package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.config.CaoyxRpcProviderConfig;
import com.caoyx.rpc.core.context.CaoyxRpcContext;
import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.net.api.Server;
import com.caoyx.rpc.core.net.netty.server.NettyServer;
import com.caoyx.rpc.core.net.param.ServerInvokerArgs;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.utils.CollectionUtils;
import com.caoyx.rpc.core.utils.NetUtils;
import com.caoyx.rpc.core.utils.StringUtils;
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

    private Server server;

    private RegisterConfig registerConfig;

    private String applicationName;
    private int port = 1118;
    private String applicationVersion;
    private String accessToken;

    private final CaoyxRpcProviderHandler rpcProviderHandler;

    private final List<CaoyxRpcFilter> caoyxRpcFilters = new ArrayList<>();

    public CaoyxRpcProviderFactory(CaoyxRpcProviderConfig providerConfig) {
        this.applicationName = providerConfig.getApplicationName();
        this.registerConfig = providerConfig.getRegisterConfig();
        this.applicationVersion = providerConfig.getApplicationVersion();
        this.accessToken = providerConfig.getAccessToken();
        if (providerConfig.getPort() > 0) {
            this.port = providerConfig.getPort();
        }

        if (CollectionUtils.isNotEmpty(providerConfig.getRpcFilters())) {
            caoyxRpcFilters.addAll(providerConfig.getRpcFilters());
        }

        this.rpcProviderHandler = new CaoyxRpcProviderHandler();
        this.server = new NettyServer();
    }

    public void init() throws CaoyxRpcException {
        server.start(port, this);
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
}