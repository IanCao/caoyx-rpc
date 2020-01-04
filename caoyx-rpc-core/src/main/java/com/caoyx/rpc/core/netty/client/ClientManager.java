package com.caoyx.rpc.core.netty.client;

import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerFactory;
import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-27 15:55
 */
@Slf4j
public class ClientManager {

    private static volatile ConcurrentHashMap<Address, Client> clientPool = new ConcurrentHashMap<>();

    public Client getOrCreateClient(Address address, Class<? extends Client> clientImpl, CaoyxRpcInvokerFactory invokerFactory) throws Exception {
        Client client = clientPool.get(address);
        if (client != null && client.isValid()) {
            return client;
        }
        synchronized (address.toString()) {
            //double check
            client = clientPool.get(address);
            if (client != null && client.isValid()) {
                return client;
            }

            if (client != null) {
                client.close();
                clientPool.remove(address);
            }
            Client clientInstance = null;
            try {
                clientInstance = clientImpl.newInstance();
                clientInstance.init(address, invokerFactory);
                clientPool.put(address, clientInstance);
            } catch (Exception e) {
                if (clientInstance != null) {
                    clientInstance.close();
                }
                log.info(e.getMessage(), e);
                throw e;
            }
            return clientInstance;
        }
    }

    public void removeClient(Address address) {
        synchronized (address.toString()) {
            clientPool.remove(address);
        }
    }
}