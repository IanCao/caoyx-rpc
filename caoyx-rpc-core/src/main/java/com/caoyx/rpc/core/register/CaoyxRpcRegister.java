package com.caoyx.rpc.core.register;

import com.caoyx.rpc.core.data.Address;
import com.caoyx.rpc.core.enums.ExtensionType;
import com.caoyx.rpc.core.extension.annotation.SPI;
import com.caoyx.rpc.core.utils.CollectionUtils;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
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
    private CopyOnWriteArraySet<Address> addressesCache = new CopyOnWriteArraySet<>();

    @Getter
    private CopyOnWriteArraySet<Address> loadAddressCache = new CopyOnWriteArraySet<>();

    private volatile long lastUpdatedTimeMills;
    private final Object lock = new Object();
    private volatile ScheduledFuture<?> addressFetchFuture;

    protected String applicationName;
    protected String version;

    protected abstract Set<Address> fetchAllAddress(String applicationName, String version);

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
    public Set<Address> getAllRegister(String applicationName, String version) {
        if (isValidCache()) {
            return addressesCache;
        }
        synchronized (lock) {
            if (isValidCache()) {
                return addressesCache;
            }
            addressesCache = new CopyOnWriteArraySet<>(fetch(applicationName, version));
            return addressesCache;
        }
    }

    @Override
    public void loadAddress(Address address) {
        loadAddressCache.add(address);
    }

    @Override
    public void stop() {
        if (addressFetchFuture != null) {
            addressFetchFuture.cancel(true);
        }
        doStop();
    }

    protected Set<Address> fetch(String applicationName, String version) {
        lastUpdatedTimeMills = System.currentTimeMillis();
        Set<Address> addresses = (Set<Address>) CollectionUtils.defaultIfEmpty(fetchAllAddress(applicationName, version), new HashSet());
        addresses.addAll(loadAddressCache);
        return addresses;
    }

    private boolean isValidUpdated() {
        return System.currentTimeMillis() - lastUpdatedTimeMills <= FETCH_INTERVAL_IN_MILLS;
    }

    private boolean isValidCache() {
        return addressesCache != null && !addressesCache.isEmpty() && isValidUpdated();
    }
}