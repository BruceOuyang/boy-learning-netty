# Netty 如何支持三种 Reactor

* 什么是 Reactor 及三种版本

* 如何在 Netty 中使用 Reactor 模式

* 解析 Netty 对 Reactor 模式支持的常见疑问


## 什么是 Reactor 及三种版本

**生活场景：返点规模变化**

* 一个人包揽所有：迎宾、点菜、做饭、上菜、送客等；

* 多招几个伙计：大家一起做上面的事

* 进一步分工：搞一个或多个人专门做迎宾

**生活场景类比**

* 饭店伙计：线程

* 迎宾工作：接入连接

* 点菜：请求

* 做菜：业务处理

* 上菜： 响应

* 送客： 断连

生活场景 | Reactor 模式  
:-- | :--  
一个人包揽所有：迎宾、点菜、做饭、上菜、送客 | Reactor 单线程模式  
多招几个伙计：大家一起做上面的事 | Reactor 多线程模式  
进一步分工：搞一个或多个专门做迎宾 | 主从 Reactor 多线程模式  

**Reactor 与三种 I/O 模式对应关系**

I/O 模式 | Reactor 版本  
:-- | :--  
BIO | Thread-Per-Connection  
NIO | Reactor  
AIO | Proactor

Reactor 是一种开发模式，模式的核心流程：  

注册感兴趣的事件 -> 扫描是否有感兴趣的事件发生 -> 事件发生后做出相应的处理。  

client/server | SocketChannel/ServerSocketChannel | OP_ACCEPT | OP_CONNECT | OP_WRITE | OP_READ  
:-- | :-- | :--: | :--: | :--: | :--:  
client | SocketChannel | - | Y | Y | Y  
server | ServerSocketChannel | Y | - | - | -  
server | SocketChannel | - | - | Y | Y

## 如何在 Netty 中使用 Reactor 模式

**Reactor 单线程模式**  
```java
EventLoopGroup group = new NioEventLoopGroup(1);

ServerBootstrap bootstrap = new ServerBootstrap();
bootstrap.group(group);
```  

**非主从 Reactor 多线程模式**  
```java
EventLoopGroup group = new NioEventLoopGroup();

ServerBootstrap bootstrap = new ServerBootstrap();
bootstrap.group(group);
```  

**主从 Reactor 多线程模式**  
```java
EventLoopGroup bossGroup = new NioEventLoopGroup(); // 这里没有 1，系统会根据cpu核数自动计算合适的数字
EventLoopGroup workerGroup = new NioEventLoopGroup();

ServerBootstrap bootstrap = new ServerBootstrap();
bootstrap.group(bossGroup, workerGroup);
```  

## 解析 Netty 对 Reactor 模式支持的常见疑问

* Netty 如何支持主从 Reactor 模式的？源码上理解

* 为什么说 Netty 的 main reactor 大多并不能用到一个线程组，只能线程组里边的一个？源码上理解

* Netty 给 Channel 分配 NIO event loop 的规则是什么？源码上理解

* 通用模式的 NIO 实现多路复用器是怎么跨平台的？源码上理解

SeletorProvider.provider()  
loadProviderAsService()  
DefaultProviderSelector.create(); // 这里就是不用的平台的实现了
