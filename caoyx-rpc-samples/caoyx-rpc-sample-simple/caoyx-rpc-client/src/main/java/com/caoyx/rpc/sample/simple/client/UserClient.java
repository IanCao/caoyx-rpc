package com.caoyx.rpc.sample.simple.client;

import com.caoyx.rpc.core.config.CaoyxRpcInvokerConfig;
import com.caoyx.rpc.core.invoker.failback.CaoyxRpcInvokerFailBack;
import com.caoyx.rpc.core.invoker.generic.CaoyxRpcGenericInvoker;
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
        IUser user = (IUser) init(IUser.class, CallType.SYNC, null, true);
        System.out.println(user.addUser(userDto));
    }

    private static void testFuture() throws Exception {
        IUser user = (IUser) init(IUser.class, CallType.FUTURE, null, true);
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
        }, true);
        user.getUsers();
    }

    private static void testGenericCall() throws Exception {
        CaoyxRpcGenericInvoker invoker = (CaoyxRpcGenericInvoker) init(CaoyxRpcGenericInvoker.class, CallType.SYNC, null, false);
        Object object = invoker.invoke("com.caoyx.rpc.sample.simple.api.IUser", 0, "getUsers", null, null);
        System.out.println("testGenericCall : " + object.toString());
    }

    private static Object init(Class clazz, CallType callType, CaoyxRpcInvokerCallBack callBack, boolean nacos) throws Exception {
        CaoyxRpcInvokerConfig config = new CaoyxRpcInvokerConfig();
        config.setIFace(clazz);
        config.setProviderImplVersion(0);
        config.setProviderApplicationName("caoyxRpc-sample-simple-server");
        config.setApplicationName("caoyxRpc-sample-simple-client");
        if (nacos) {
            config.setRegisterConfig(new RegisterConfig("127.0.0.1:8848", RegisterType.NACOS));
        } else {
            config.setRegisterConfig(new RegisterConfig("127.0.0.1:1118", RegisterType.DIRECT));
        }
        config.setCallType(callType);
        config.setAccessToken("caoyx");
        config.setCaoyxRpcInvokerCallBack(callBack);
        config.setCaoyxRpcInvokerFailBack(new CaoyxRpcInvokerFailBack() {
            @Override
            public Object onFail(String errorMsg) {
                List<UserDto> list = new ArrayList<>();
                UserDto userDto = new UserDto("fail");
                list.add(userDto);
                return list;
            }

            @Override
            public Object onTimeout() {
                List<UserDto> list = new ArrayList<>();
                UserDto userDto = new UserDto("onTimeout");
                list.add(userDto);
                return list;
            }

            @Override
            public Object onRateLimit() {
                List<UserDto> list = new ArrayList<>();
                UserDto userDto = new UserDto("onRateLimit");
                list.add(userDto);
                return list;
            }
        });
        config.setCaoyxRpcInvokerCallBack(new CaoyxRpcInvokerCallBack() {
            @Override
            public void onSuccess(Object result) {
                System.err.println("setCaoyxRpcInvokerCallBack onSuccess");
            }

            @Override
            public void onFail(String errorMsg) {
                System.err.println("errorMsg");
            }
        });
        CaoyxRpcReferenceBean rpcReferenceBean = new CaoyxRpcReferenceBean(config);
        return rpcReferenceBean.getObject();
    }
}