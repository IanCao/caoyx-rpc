[![Build Status](https://travis-ci.com/IanCao/caoyx-rpc.svg?branch=master)](https://travis-ci.com/IanCao/caoyx-rpc) 
![license](https://img.shields.io/github/license/IanCao/caoyx-rpc.svg)
![maven](https://img.shields.io/nexus/s/com.github.iancao/caoyx-rpc?server=https%3A%2F%2Foss.sonatype.org)

# caoyx-rpc
caoyx-rpc是一个基于Java语言开发的开源RPC服务框架，提供高可用，高可用的远程调用能力。

### Features特性：
1. 面向接口代理：调用方和提供方通过一个接口jar包进行耦合，系统封装了远程通讯的实现，用户在使用时就跟调用本地实现一样。
2. 支持多种调用方式：目前支持 **同步调用**, **Future异步调用**, **CallBack**
3. 支持隐式参数：用户可以在 CaoyxRpcContext 上下文中放入自定义信息，这些信息也会随着调用发送到服务提供方
4. 支持泛化调用：调用放可以不依赖于服务提供方的接口jar包而进行调用服务提供方的服务
5. 负载均衡：提供丰富的负载均衡策略，目前包括：**随机**，**一致性Hash**
6. 服务注册与发现：支持服务自动注册和手动注册
   - 自动注册：目前支持Zookeeper
   - 手动注册：用户在配置中增加具体的服务提供方的ip+port,可以是多个服务提供方
   - caoyx-rpc会收集自动注册组件中的服务提供方地址 + 手动注册的服务提供方地址进行负载均衡
7. 高度扩展能力：通过自定义SPI进行高度扩展
8. 多版本能力：服务提供方提供同一接口多版本实现，调用方选择某个版本进行使用
9. 多种序列化选择方式：目前支持**JDK**，**Hessian2**，**ProtoStuff**序列化方式
10. 支持用户自定义调用链中的filter：用户可以自定义filter并加入到调用链之中
11. 与SpringBoot高度集成
12. 支持调用方设置**超时时间**和**失败重试次数**
13. 支持服务版本与实现版本调用：服务提供方可以设置其服务版本和其实现实现版本，调用方同时设置提供方的服务版本和实现版本进行调用
14. 支持调用方的failCallBack
15. 支持LZ4压缩
16. 支持调用方与提供方之间的鉴权，服务提供方配置一个accessToken，即只接受具有相同accessToken的Request请求。


### 高级使用
[高级使用](doc/Advanced_Use.md)

### 如何接入
#### 1. SpringBoot接入

[Caoyx-Rpc SpringBoot Demo](https://github.com/IanCao/caoyx-rpc/tree/master/caoyx-rpc-samples/caoyx-rpc-sample-springboot)
##### a.服务调用方
**Maven dependency**
```xml
 <dependency>
   <artifactId>caoyx-rpc-spring-invoker</artifactId>
   <groupId>com.github.iancao</groupId>
   <version>1.0.0-SNAPSHOT</version>
</dependency>
```
在期望使用远程调用的地方，使用 `@CaoyxRpcReference` 进行注解服务提供方提供的接口Bean
并增加`@CaoyxRpcReference`注解参数

```
 // 控制序列话方式，默认使用PROTOSTUFF，选填
 SerializerType serializer() default SerializerType.PROTOSTUFF;
 // 控制调用的服务提供方的实现版本号，选填
 String implVersion() default "0";
 // 控制调用的服务提供方的版本号（会在注册中心使用此服务版本号进行选择），选填
 String applicationVersion() default "0";
 // 手动注册需要传入的调用方的地址，选填
 String[] loadAddress() default {};
 // 控制调用方式，默认使用同步调用方式，选填
 CallType callType() default CallType.SYNC;
 // 控制自动注册的使用情况，默认不使用注册中心，选填
 RegisterType register() default RegisterType.NO_REGISTER;
 // 如果上面使用了注册方式，填写注册中心地址
 String registerAddress() default "";
 // 控制负载均衡算法，默认随机，选填
 LoadBalanceType loadBalance() default LoadBalanceType.RANDOM;
 // 服务提供方的服务名，必填
 String remoteApplicationName() default "";
 // 失败重试次数，选填
 int retryTimes() default 0;
 // 超时时间，选填
 long timeout() default 3000L;
 // 用户自定义的filter的BeanName，选填
 String[] filters() default {};
 // 用户自定义的失败/超时回调，填写用户实现`com.caoyx.rpc.core.invoker.failback.CaoyxRpcInvokerFailBack`的Bean的beanName
 String failCallBack() default "";
 // 用作调用方与提供方之间的鉴权使用
 String accessToken() default "";
```

##### a.服务提供方
**Maven dependency**

```xml
 <dependency>
   <artifactId>caoyx-rpc-spring-provider</artifactId>
   <groupId>com.github.iancao</groupId>
   <version>1.0.0-SNAPSHOT</version>
</dependency>
```
在实现远程调用接口的类上添加 `@CaoyxRpcService`注解

并在 application.properties 或者 application.yml 中增加

```
 // 服务提供方的名称
 caoyxRpc.server.applicationName=caoyxRpc-sample-springboot-client，必填
 // 服务提供方暴露的端口，默认1118，必填
 caoyxRpc.server.port=1118 
 // 服务提供方自动注册的方式，默认为noRegister，可以选择noRegister不使用自动注册方式，选填
 caoyxRpc.server.register.type=zookeeper
 // 自动注册的组件的地址 ，选填
 caoyxRpc.server.register.address=127.0.0.1:2181
 // 服务提供方的版本，默认为0， 选填
 caoyxRpc.server.applicationVersion=0
 // 服务提供方的鉴权Token，默认无鉴权，选填
 caoyxRpc.server.accessToken=xxxx
```

#### 2. 原生接入

[Caoyx-Rpc Simple Demo](https://github.com/IanCao/caoyx-rpc/tree/master/caoyx-rpc-samples/caoyx-rpc-sample-simple)

**Maven dependency**

```xml
<dependency>
    <groupId>com.github.iancao</groupId>
    <artifactId>caoyx-rpc-core</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

服务提供方定义的接口如下：
```java
public interface IUser {
    
    boolean addUser(UserDto userDto);

    List<UserDto> getUsers();
}
```

##### a.服务调用方


```
    public static void main(String[] args) {
        CaoyxRpcInvokerConfig config = new CaoyxRpcInvokerConfig();
        config.setIFace(IUser.class);
        config.setRemoteApplicationName("caoyxRpc-sample-simple-server");
        config.setRegisterConfig(new RegisterConfig(
                RegisterType.NO_REGISTER.getValue(),
                "",
                Arrays.asList("127.0.0.1:1118")));

        CaoyxRpcReferenceBean rpcReferenceBean = new CaoyxRpcReferenceBean(config);
        rpcReferenceBean.init();

        IUser user = (IUser) rpcReferenceBean.getObject();  // 获取代理的IUser对象，进行使用即可。
        user.getUsers();
    }
```

##### b.服务提供方
```
   public static void main(String[] args) throws CaoyxRpcException {
          CaoyxRpcProviderConfig config = new CaoyxRpcProviderConfig();
          config.setApplicationName("caoyxRpc-sample-simple-server");
          config.setApplicationVersion("0");
          config.setRegisterConfig(new RegisterConfig(
                  "noRegister",
                  "",
                  null
          ));
          config.setPort(1118);
  
          CaoyxRpcProviderFactory caoyxRpcProviderFactory = new CaoyxRpcProviderFactory(config);
          caoyxRpcProviderFactory.addServiceProvider(IUser.class.getName(), "0", new UserImpl());
          caoyxRpcProviderFactory.init();
      }
```


### 如何联系
- 在github提Issue
- 发送邮件至caoyixiong@apache.org

### 贡献
欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 Issue 讨论新特性或者变更。

### License
caoyx-rpc is under the Apache 2.0 license. See the LICENSE file for details.

产品开源免费，并且将持续提供免费的社区技术支持。