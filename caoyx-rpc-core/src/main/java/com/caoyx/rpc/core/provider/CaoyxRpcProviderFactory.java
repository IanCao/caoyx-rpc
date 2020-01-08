package com.caoyx.rpc.core.provider;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.extension.ExtensionLoader;
import com.caoyx.rpc.core.filter.CaoyxRpcFilter;
import com.caoyx.rpc.core.filter.CaoyxRpcFilterManager;
import com.caoyx.rpc.core.filter.providerFilter.ProviderContenxtFilter;
import com.caoyx.rpc.core.netty.server.Server;
import com.caoyx.rpc.core.register.CaoyxRpcRegister;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.utils.NetUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * @author caoyixiong
 */
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
    private String version;
    @Setter
    private CaoyxRpcProviderHandler rpcProviderHandler;
    @Setter
    private String accessToken = null;

    private CaoyxRpcFilterManager rpcFilterManager;

    public CaoyxRpcProviderFactory(String applicationName,
                                   Server server,
                                   RegisterConfig registerConfig,
                                   String version,
                                   List<CaoyxRpcFilter> rpcFilters) {
        this.applicationName = applicationName;
        this.server = server;
        this.registerConfig = registerConfig;
        this.version = version;
        this.rpcFilterManager = new CaoyxRpcFilterManager();
        this.rpcFilterManager.addAll(rpcFilters);

        this.rpcProviderHandler = new CaoyxRpcProviderHandler();
        ProviderContenxtFilter contenxtFilter = new ProviderContenxtFilter();

        rpcFilterManager.addLast(rpcProviderHandler);
        rpcFilterManager.addFirst(contenxtFilter);
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

    public void addServiceBean(String className, String version, Object service) {
        rpcProviderHandler.addServiceBean(className, version, service);
    }

    public CaoyxRpcResponse invoke(CaoyxRpcRequest requestPacket) throws Exception {
        CaoyxRpcResponse caoyxRpcResponse = new CaoyxRpcResponse();
        rpcFilterManager.invoke(requestPacket, caoyxRpcResponse);
        return caoyxRpcResponse;
    }
}