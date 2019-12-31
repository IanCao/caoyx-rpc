package com.caoyx.rpc.core.register;

import com.caoyx.rpc.core.data.Address;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-30 16:16
 */
public abstract class CaoyxRpcRegister implements Register {

    private static final long FETCH_INTERVAL_IN_MILLS = 5 * 1000L;
    private CopyOnWriteArrayList<Address> addressesCache = new CopyOnWriteArrayList<>();

    private volatile long lastUpdatedTimeMills;
    private final Object lock = new Object();
    private volatile ScheduledFuture<?> addressFetchFuture;

    protected String applicationName;
    protected String version;

    protected abstract List<Address> fetchAllAddress(String applicationName, String version);

    protected abstract void doStop();

    @Override
    public void initRegister(String applicationName, String version) {
        this.applicationName = applicationName;
        this.version = version;
    }

    @Override
    public void startRegisterLoopFetch() {
        addressFetchFuture = Executors
                .newSingleThreadScheduledExecutor()
                .scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        if (isValidUpdated()) {
                            return;
                        }
                        fetch(applicationName, version);
                    }
                }, 0, FETCH_INTERVAL_IN_MILLS, TimeUnit.MILLISECONDS);
    }

    @Override
    public List<Address> getAllRegister(String applicationName, String version) {
        if (isValidCache()) {
            return addressesCache;
        }
        synchronized (lock) {
            if (isValidCache()) {
                return addressesCache;
            }
            addressesCache = new CopyOnWriteArrayList<>(fetch(applicationName, version));
            return addressesCache;
        }
    }

    @Override
    public void stop() {
        if (addressFetchFuture != null) {
            addressFetchFuture.cancel(true);
        }
        doStop();
    }

    protected List<Address> fetch(String applicationName, String version) {
        lastUpdatedTimeMills = System.currentTimeMillis();
        return fetchAllAddress(applicationName, version);
    }

    private boolean isValidUpdated() {
        return System.currentTimeMillis() - lastUpdatedTimeMills <= FETCH_INTERVAL_IN_MILLS;
    }

    private boolean isValidCache() {
        return addressesCache != null && !addressesCache.isEmpty() && isValidUpdated();
    }
}