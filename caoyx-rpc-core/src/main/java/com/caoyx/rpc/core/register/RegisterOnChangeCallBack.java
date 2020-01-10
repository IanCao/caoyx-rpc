package com.caoyx.rpc.core.register;

import com.caoyx.rpc.core.data.Address;

import java.util.Set;

/**
 * @Author: caoyixiong
 * @Date: 2020-01-10 14:27
 */
public interface RegisterOnChangeCallBack {
   void onAddressesDeleted(Set<Address> addresses);
}