package com.caoyx.rpc.core.shutdown;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-12 22:23
 */
public enum GracefullyShutDown {
    INSTANCE;

    private CopyOnWriteArrayList<GraceFullyShutDownCallBack> callBacks = new CopyOnWriteArrayList<>();

    public synchronized void addCallBack(GraceFullyShutDownCallBack callBack) {
        callBacks.addIfAbsent(callBack);
    }

    public synchronized void onShutDown() {
        for (GraceFullyShutDownCallBack callBack : callBacks) {
            callBack.shutdownGracefully();
        }
    }
}