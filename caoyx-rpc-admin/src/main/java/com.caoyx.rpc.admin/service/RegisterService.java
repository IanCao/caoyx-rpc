package com.caoyx.rpc.admin.service;

import com.caoyx.rpc.admin.data.ClassInfo;
import com.caoyx.rpc.admin.data.InvokerInfo;
import com.caoyx.rpc.admin.data.ProviderInfo;

import java.util.List;

/**
 * @Author: caoyixiong
 * @Date: 2020-03-14 00:42
 * /CaoyxRpc/caoyxRpc-server/className-1/provider or invoker /ip:port
 */
public interface RegisterService {

    List<String> getAllExportServices();

    List<ClassInfo> getClassInfosByServiceName(String serviceName);

    List<ProviderInfo> getProviderInfosByServiceNameAndClassInfo(String serviceName, ClassInfo classInfo);

    List<InvokerInfo> getInvokerInfosByServiceNameAndClassInfo(String serviceName, ClassInfo classInfo);
}