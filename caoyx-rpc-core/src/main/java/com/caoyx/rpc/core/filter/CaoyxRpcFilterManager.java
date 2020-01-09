package com.caoyx.rpc.core.filter;

import com.caoyx.rpc.core.data.CaoyxRpcRequest;
import com.caoyx.rpc.core.data.CaoyxRpcResponse;
import com.caoyx.rpc.core.enums.CaoyxRpcStatus;
import com.caoyx.rpc.core.utils.CollectionUtils;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-06 18:40
 */
//TODO 重构
public class CaoyxRpcFilterManager {

    private LinkedList<CaoyxRpcFilter> systemFirstRpcFilters = new LinkedList<CaoyxRpcFilter>();
    private LinkedList<CaoyxRpcFilter> systemLastRpcFilters = new LinkedList<CaoyxRpcFilter>();
    private LinkedList<CaoyxRpcFilter> userRpcFilters = new LinkedList<CaoyxRpcFilter>();

    public void invoke(CaoyxRpcRequest rpcRequest, CaoyxRpcResponse rpcResponse) throws Exception {

        LinkedList<CaoyxRpcFilter> filters = new LinkedList<CaoyxRpcFilter>();
        filters.addAll(systemFirstRpcFilters);
        filters.addAll(userRpcFilters);
        filters.addAll(systemLastRpcFilters);

        if (CollectionUtils.isNotEmpty(filters)) {
            for (int i = 0; i < filters.size(); i++) {
                filters.get(i).invokeRequestHandler(rpcRequest);
            }

            for (int i = filters.size() - 1; i >= 0; i--) {
                CaoyxRpcFilter rpcFilter = filters.get(i);
                rpcFilter.doProcess(rpcRequest, rpcResponse);
                if (rpcResponse.getStatus() == CaoyxRpcStatus.ASYNC) {
                    continue;
                }
                rpcFilter.invokeResponseHandler(rpcResponse);
            }
        }
    }

    public void addSystemFilterFirst(CaoyxRpcFilter filter) {
        if (filter == null) {
            return;
        }
        systemFirstRpcFilters.addFirst(filter);
    }

    public void addSystemFilterLast(CaoyxRpcFilter filter) {
        if (filter == null) {
            return;
        }
        systemLastRpcFilters.addLast(filter);
    }

    public void addUserFilterFirst(CaoyxRpcFilter filter) {
        if (filter == null) {
            return;
        }
        userRpcFilters.addFirst(filter);
    }

    public void addUserFilterLast(CaoyxRpcFilter filter) {
        if (filter == null) {
            return;
        }
        userRpcFilters.addLast(filter);
    }

    public void addAllUserFilters(List<CaoyxRpcFilter> caoyxRpcFilters) {
        if (CollectionUtils.isEmpty(caoyxRpcFilters)) {
            return;
        }
        userRpcFilters.addAll(caoyxRpcFilters);
    }
}