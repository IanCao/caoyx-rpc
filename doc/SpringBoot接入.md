### 1. 声名服务接口
 - a. 新建一个maven的module
  
 - b. 添加需要远程调用的接口声名以及这些接口中所需要的类
  
  举个例子
  ```
  public interface IUser {
      boolean addUser(UserDto userDto);
      List<UserDto> getUsers();
      void addUserVoid(UserDto userDto);
  }

  ```
 - c. 打包
  

### 2. 服务提供方（Provider）

**a. Maven dependency**

```xml
 <!-- CaoyxRpc springProvider 的依赖 -->
 <dependency>
   <artifactId>caoyx-rpc-spring-provider</artifactId>
   <groupId>com.github.iancao</groupId>
   <version>${caoyxRpc.version}</version> 
 </dependency>
 <!-- 步骤1中的声名接口依赖 -->
 <dependency>
    <artifactId>xxxxx</artifactId>
    <groupId>xxxx</groupId>
    <version>xxxxx</version> 
  </dependency>
```
**b. 实现接口声名并注解**

实现步骤一的接口声名，并在接口类上添加 `@CaoyxRpcService`注解

**c. 添加配置参数**

在 `application.properties`或者 `application.yml` 中增加

```
 caoyxRpc.provider.applicationName=caoyxRpc-sample-springboot-client // 服务提供方的名称必填
 caoyxRpc.provider.port=1118  // 服务提供方暴露的端口，默认1118，选填
```
**d. 启动服务**

这条日志代表对应className的实现启动成功

`exportService: className[xxxx] implVersion:[x] success:[true]`

### 3. 服务调用方（Invoker）
**a. Maven dependency**

```xml
 <!-- CaoyxRpc springInvoker 的依赖 -->
 <dependency>
   <artifactId>caoyx-rpc-spring-invoker</artifactId>
   <groupId>com.github.iancao</groupId>
   <version>${caoyxRpc.version}</version>
 </dependency>
 <!-- 步骤1中的声名接口依赖 -->
 <dependency>
    <artifactId>xxxxx</artifactId>
    <groupId>xxxx</groupId>
    <version>xxxxx</version> 
  </dependency>
```

**b. 如何使用**

- 在声名接口属性上添加`@CaoyxRpcReference`，并设置服务端提供方名称
```
@CaoyxRpcReference(providerApplicationName = "caoyxRpc-sample-springboot-server")
private IUser user;
```
- 在`application.properties`或者`application.yml`中增加
```
caoyxRpc.invoker.applicationName=caoyxRpc-sample-springboot-client //当前invoker的服务名称
caoyxRpc.invoker.register.address=127.0.0.1:1118                   //服务注册地址，direct模式下为provider服务端的地址
caoyxRpc.invoker.register.type=direct                              //服务注册类型, direct代表直接连接provider服务端
```
**c. 启动服务即可调用**