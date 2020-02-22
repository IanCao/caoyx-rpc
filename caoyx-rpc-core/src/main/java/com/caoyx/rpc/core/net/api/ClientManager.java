package com.caoyx.rpc.core.net.api;

import com.caoyx.rpc.core.exception.CaoyxRpcException;
import com.caoyx.rpc.core.url.URL;
import com.caoyx.rpc.core.url.register.ProviderURL;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 15:55
 */
@Slf4j
public class ClientManager {

    private static volatile ConcurrentHashMap<String, Client> clientPool = new ConcurrentHashMap<>();

    public Client getOrCreateClient(ProviderURL providerURL, Class<? extends Client> clientImpl) throws CaoyxRpcException {
        Client client = clientPool.get(providerURL.getHostPort());
        if (client != null && client.isValid()) {
            return client;
        }
        synchronized (providerURL.getHostPort().intern()) {
            //double check
            client = clientPool.get(providerURL.getHostPort());
            if (client != null && client.isValid()) {
                return client;
            }

            if (client != null) {
                client.close();
                clientPool.remove(providerURL.getHostPort());
            }
            Client clientInstance = null;
            try {
                clientInstance = clientImpl.newInstance();
                clientInstance.init(providerURL.getHostPort());
                clientPool.put(providerURL.getHostPort(), clientInstance);
            } catch (Exception e) {
                if (clientInstance != null) {
                    clientInstance.close();
                }
                log.info(e.getMessage(), e);
                throw new CaoyxRpcException(e);
            }
            return clientInstance;
        }
    }
}