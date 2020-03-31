[![Build Status](https://travis-ci.com/IanCao/caoyx-rpc.svg?branch=master)](https://travis-ci.com/IanCao/caoyx-rpc) 
![license](https://img.shields.io/github/license/IanCao/caoyx-rpc.svg)
![maven](https://img.shields.io/nexus/s/com.github.iancao/caoyx-rpc?server=https%3A%2F%2Foss.sonatype.org)
![maven](https://img.shields.io/nexus/r/com.github.iancao/caoyx-rpc?server=https%3A%2F%2Foss.sonatype.org)
# caoyx-rpc
caoyx-rpc是一个基于Java语言开发的开源RPC服务框架，提供高可用，高可用的远程调用能力。

### Features特性：
1. 面向接口代理：调用方和提供方通过一个接口jar包进行耦合，系统封装了远程通讯的实现，用户在使用时就跟调用本地实现一样。
2. 支持多种调用方式：目前支持 **同步调用**, **Future异步调用**, **CallBack**,**OneWay**
3. 支持隐式参数：用户可以在 CaoyxRpcContext 上下文中放入自定义信息，这些信息也会随着调用发送到服务提供方
4. 支持泛化调用：调用放可以不依赖于服务提供方的接口jar包而进行调用服务提供方的服务
5. 负载均衡：提供丰富的负载均衡策略，目前包括：**随机**，**一致性Hash**
6. 服务注册与发现：支持服务自动注册和手动注册
   - 自动注册：目前支持Zookeeper
   - 手动注册：用户在配置中增加具体的服务提供方的hostPorts,可以是多个服务提供方
7. 高度扩展能力：通过自定义SPI进行高度扩展
8. 多版本能力：服务提供方提供同一接口多版本实现，调用方选择某个版本进行使用
9. 多种序列化选择方式：目前支持**JDK**，**Hessian2**，**ProtoStuff**序列化方式
10. 支持用户自定义调用链中的filter：用户可以自定义filter并加入到调用链之中
11. 与SpringBoot高度集成
12. 支持调用方设置**超时时间**和**失败重试次数**
13. 支持服务版本与实现版本调用：服务提供方可以设置其服务版本和其实现实现版本，调用方同时设置提供方的服务版本和实现版本进行调用
14. 支持调用方的**失败回调**和**超时回调**
15. 支持LZ4压缩
16. 支持调用方与提供方之间的鉴权，服务提供方配置一个accessToken，即只接受具有相同accessToken的Request请求。
17. 支持kill pid（非kill -9 ）的优雅停机
18. 支持Provider方的限流，以及增加Invoker被限流后的回调接口

### 如何接入
#### 1. [SpringBoot接入](doc/SpringBoot接入.md)

[Caoyx-Rpc SpringBoot Demo](https://github.com/IanCao/caoyx-rpc/tree/master/caoyx-rpc-samples/caoyx-rpc-sample-springboot)


#### 2. [原生接入](doc/原生方式接入.md)

[Caoyx-Rpc Simple Demo](https://github.com/IanCao/caoyx-rpc/tree/master/caoyx-rpc-samples/caoyx-rpc-sample-simple)


### Benchmark基准测试
[CaoyxRpc/Dubbo/Grpc/Thrift 基准测试](doc/Benchmark.md)

### 如何联系
- 在github提Issue
- 发送邮件至caoyixiong@apache.org

### 贡献
欢迎参与项目贡献！比如提交PR修复一个bug，或者新建 Issue 讨论新特性或者变更。

### License
caoyx-rpc is under the Apache 2.0 license. See the LICENSE file for details.

产品开源免费，并且将持续提供免费的社区技术支持。