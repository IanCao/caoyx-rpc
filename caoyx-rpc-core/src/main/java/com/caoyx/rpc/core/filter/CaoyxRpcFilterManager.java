package com.caoyx.rpc.core.filter;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.utils.CollectionUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 18:40
 */
public class CaoyxRpcFilterManager {

    private LinkedList<CaoyxRpcFilter> rpcFilters = new LinkedList<CaoyxRpcFilter>();

    public void invoke(CaoyxRpcRequest rpcRequest, CaoyxRpcResponse rpcResponse) throws Exception {
        if (CollectionUtils.isNotEmpty(rpcFilters)) {
            for (int i = 0; i < rpcFilters.size(); i++) {
                rpcFilters.get(i).invokeRequestHandler(rpcRequest);
            }
            for (int i = rpcFilters.size() - 1; i >= 0; i--) {
                CaoyxRpcFilter rpcFilter = rpcFilters.get(i);
                rpcFilter.doProcess(rpcRequest, rpcResponse);
                rpcFilter.invokeResponseHandler(rpcResponse);
            }
        }
    }

    public void addFirst(CaoyxRpcFilter filter) {
        if (filter == null) {
            return;
        }
        rpcFilters.addFirst(filter);
    }

    public void addLast(CaoyxRpcFilter filter) {
        if (filter == null) {
            return;
        }
        rpcFilters.addLast(filter);
    }

    public void addAll(List<CaoyxRpcFilter> rpcFilters) {
        if (CollectionUtils.isEmpty(rpcFilters)) {
            return;
        }
        rpcFilters.addAll(rpcFilters);
    }

}