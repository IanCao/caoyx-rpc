## 基准测试

本基准测试基本使用 [rpc-benchmark](https://github.com/hank-whu/rpc-benchmark) 思路进行测试

> 测试环境： 
1. MacBook Pro (13-inch, 2018, Four Thunderbolt 3 Ports) 16GB MacOS Mojave-10.14.3
2. Java8

主要分为四个方法：
```
public interface UserService {
    boolean existUser(String email); // 小输入，小输出

    boolean createUser(User user);   // 大输入，小输出

    User getUser(long id);           // 小输入，大输出

    Page<User> listUser(int pageNo); // 小输入，超大输出
}
```
主要测试了目前主流的三种RPC框架`Dubbo`,`Thrift`,`Grpc`和本项目`CaoyxRpc`

#### existUser
| framework | thrpt (ops/ms) | avgt (ms) | p90 (ms)	 | p99 (ms)| p999 (ms)|
| --- | --- | --- | --- | --- | --- |
| thrift | 72.089 | 0.444 | 0.890 | 2.114 |4.162 |
| caoyxRpc | 40.063 | 0.703 | 0.964 | 1.669 | 3.203 |
| dubbo | 39.192 | 0.762 | 0.919 | 1.264 | 4.243 |
| grpc | 36.108 | 0.859 | 1.002 | 1.720 | 2.698 | 


#### createUser
| framework | thrpt (ops/ms) | avgt (ms) | p90 (ms)	 | p99 (ms)| p999 (ms)|
| --- | --- | --- | --- | --- | --- |
| thrift | 65.847 | 0.470 | 0.950 | 2.273 |4.112 |
| dubbo | 34.953 | 0.832 | 1.063 | 1.866 | 4.628|
| caoyxRpc | 33.838 | 0.821 | 1.104 | 2.146 | 3.527 |
| grpc | 30.144 | 0.924 | 1.161 | 1.878 | 3.143 | 


#### getUser
| framework | thrpt (ops/ms) | avgt (ms) | p90 (ms)	 | p99 (ms)| p999 (ms)|
| --- | --- | --- | --- | --- | --- |
| thrift | 62.169 | 0.468 | 0.952 | 2.294 |4.284 |
| dubbo | 38.646 | 0.799 | 0.989 | 1.356 | 4.407 |
| caoyxRpc | 38.327 | 0.763 | 1.004 | 1.896 | 3.281 |
| grpc | 35.867 | 0.901 | 1.071 | 1.843 | 2.920 | 


#### listUser
| framework | thrpt (ops/ms) | avgt (ms) | p90 (ms)	 | p99 (ms)| p999 (ms)|
| --- | --- | --- | --- | --- | --- |
| thrift | 66.082 | 0.471 | 0.990 | 2.531 |5.087 |
| caoyxRpc | 41.521 | 0.849 | 1.073 | 2.154 | 3.359  |
| grpc | 36.118 | 0.896 | 1.110 | 1.866 | 3.199 | 
| dubbo | 35.822 | 0.917 | 1.165 | 1.653 | 4.530  |
