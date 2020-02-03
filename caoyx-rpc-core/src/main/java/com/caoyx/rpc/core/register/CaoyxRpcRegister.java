package com.caoyx.rpc.core.register;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.enums.ExtensionType;
import com.caoyx.rpc.core.extension.annotation.SPI;
import com.caoyx.rpc.core.utils.CollectionUtils;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @Author: caoyixiong
 * @Date: 2019-12-30 16:16
 */
@SPI(type = ExtensionType.REGISTER)
public abstract class CaoyxRpcRegister implements Register {

    private static final long FETCH_INTERVAL_IN_MILLS = 5 * 1000L;

    private volatile CopyOnWriteArraySet<Address> remoteAddressesCache = new CopyOnWriteArraySet<>();
    @Getter
    private volatile CopyOnWriteArraySet<Address> loadAddressCache = new CopyOnWriteArraySet<>();

    private final CopyOnWriteArraySet<Address> allAddressesCache = new CopyOnWriteArraySet<>();

    private CopyOnWriteArrayList<RegisterOnChangeCallBack> changeCallBacks = new CopyOnWriteArrayList<>();

    private volatile long lastUpdatedTimeMills;
    private volatile boolean updatedFinish = true;
    private final Object lock = new Object();
    private volatile ScheduledFuture<?> addressFetchFuture;

    protected String applicationName;

    protected String applicationVersion;

    protected abstract Set<Address> fetchAllAddress(String applicationName, String applicationVersion);

    protected abstract void doStop();

    @Override
    public final void initRegister(String applicationName, String applicationVersion) {
        this.applicationName = applicationName;
        this.applicationVersion = applicationVersion;
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
                        fetchAll(applicationName, applicationVersion);

                    }
                }, 0, FETCH_INTERVAL_IN_MILLS, TimeUnit.MILLISECONDS);
    }

    @Override
    public final Set<Address> getAllRegister(String applicationName, String applicationVersion) {
        if (isValidCache()) {
            return allAddressesCache;
        }
        synchronized (lock) {
            if (isValidCache()) {
                return allAddressesCache;
            }
            fetchAll(applicationName, applicationVersion);
            return allAddressesCache;
        }
    }

    @Override
    public final void loadAddress(Address address) {
        loadAddressCache.add(address);
    }

    @Override
    public final void stop() {
        if (addressFetchFuture != null) {
            addressFetchFuture.cancel(true);
        }
        doStop();
    }

    public final void addOnChangeCallBack(RegisterOnChangeCallBack changeCallBack) {
        changeCallBacks.add(changeCallBack);
    }

    protected final synchronized void fetchAll(String applicationName, String version) {
        if (isValidCache()) {
            return;
        }
        updatedFinish = false;


        Set<Address> latestRemoteAddresses = fetchAllAddress(applicationName, version);

        if (CollectionUtils.isEmpty(latestRemoteAddresses)) {
            remoteAddressesCache = new CopyOnWriteArraySet();
        } else {
            remoteAddressesCache = new CopyOnWriteArraySet<>(latestRemoteAddresses);
        }
        lastUpdatedTimeMills = System.currentTimeMillis();
        allAddressesCache.clear();
        allAddressesCache.addAll(remoteAddressesCache);
        allAddressesCache.addAll(loadAddressCache);

        updatedFinish = true;

        Set<Address> deletedAddresses = new HashSet<>();
        for (Address address : remoteAddressesCache) {
            if (!latestRemoteAddresses.contains(address)) {
                deletedAddresses.add(address);
            }
        }
        for (RegisterOnChangeCallBack changeCallBack : changeCallBacks) {
            changeCallBack.onAddressesDeleted(deletedAddresses);  //TODO 切换为线程池操作
        }
    }

    private boolean isValidUpdated() {
        return System.currentTimeMillis() - lastUpdatedTimeMills <= FETCH_INTERVAL_IN_MILLS;
    }

    private boolean isValidCache() {
        return isValidUpdated() && !allAddressesCache.isEmpty() && updatedFinish;
    }
}