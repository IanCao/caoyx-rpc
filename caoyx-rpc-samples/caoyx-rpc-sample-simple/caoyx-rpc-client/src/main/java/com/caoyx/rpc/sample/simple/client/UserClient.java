package com.caoyx.rpc.sample.simple.client;

import com.caoyx.rpc.core.compress.CompressType;
import com.caoyx.rpc.core.config.CaoyxRpcInvokerConfig;
import com.caoyx.rpc.core.invoker.generic.CaoyxRpcGenericInvoker;
import com.caoyx.rpc.core.loadbalance.LoadBalanceType;
import com.caoyx.rpc.core.serialization.SerializerType;
import com.caoyx.rpc.sample.simple.api.IUser;
import com.caoyx.rpc.sample.simple.api.UserDto;
import com.caoyx.rpc.core.enums.CallType;
import com.caoyx.rpc.core.invoker.CaoyxRpcFuture;
import com.caoyx.rpc.core.invoker.CaoyxRpcInvokerCallBack;
import com.caoyx.rpc.core.invoker.reference.CaoyxRpcReferenceBean;
import com.caoyx.rpc.core.register.RegisterConfig;
import com.caoyx.rpc.core.register.RegisterType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author caoyixiong
 */
public class UserClient {

    public static void main(String[] args) throws Exception {
        testSync();
        testFuture();
        testCallBack();
        testGenericCall();
    }

    private static void testSync() throws Exception {
        UserDto userDto = new UserDto("testSync : UserDto==> testSync");
        IUser user = (IUser) init(IUser.class, CallType.SYNC, null);
        System.out.println(user.addUser(userDto));
    }

    private static void testFuture() throws Exception {
        IUser user = (IUser) init(IUser.class, CallType.FUTURE, null);
        user.getUsers();
        System.out.println(CaoyxRpcFuture.getFuture().get().toString());
        UserDto userDto = new UserDto("testFuture : UserDto==> testFuture");
        user.addUserVoid(userDto);
    }

    private static void testCallBack() throws Exception {
        IUser user = (IUser) init(IUser.class, CallType.CALLBACK, new CaoyxRpcInvokerCallBack() {
            @Override
            public void onSuccess(Object result) {
                System.out.println("testCallBack: " + result);
            }

            @Override
            public void onFail(String errorMsg) {
                System.out.println("testCallBack: " + errorMsg);
            }
        });
        user.getUsers();
    }

    private static void testGenericCall() throws Exception {
        CaoyxRpcGenericInvoker invoker = (CaoyxRpcGenericInvoker) init(CaoyxRpcGenericInvoker.class, CallType.SYNC, null);
        Object object = invoker.invoke("com.caoyx.rpc.sample.simple.api.IUser", "0", "getUsers", null, null);
        System.out.println("testGenericCall : " + object.toString());
    }

    private static Object init(Class clazz, CallType callType, CaoyxRpcInvokerCallBack callBack) throws Exception {
        List<String> loadAddresses = new ArrayList<String>();
        loadAddresses.add("127.0.0.1:1118");
        CaoyxRpcInvokerConfig config = new CaoyxRpcInvokerConfig();
        config.setIFace(clazz);
        config.setRemoteApplicationVersion("0");
        config.setRemoteImplVersion("0");
        config.setRemoteApplicationName("caoyxRpc-sample-simple-server");
        config.setRegisterConfig(new RegisterConfig(
                RegisterType.NO_REGISTER.getValue(),
                "",
                loadAddresses));
        config.setSerializerType(SerializerType.HESSIAN2);
//        config.setCompressType(CompressType.LZ4);
        config.setLoadBalanceType(LoadBalanceType.RANDOM);
        config.setCallType(callType);
        config.setCaoyxRpcInvokerCallBack(callBack);

        CaoyxRpcReferenceBean rpcReferenceBean = new CaoyxRpcReferenceBean(config);
        rpcReferenceBean.init();
        return rpcReferenceBean.getObject();
    }
}