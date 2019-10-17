# 为什么舍近求远：不直接使用 JDK NIO

## Netty 做的更多

* 支持常用应用协议层(http/tcp/udp)

* 解决传输问题：粘包、半包现象(tcp 容易出现这两个问题，nio不会处理这些问题，netty会)

* 支持流量整形（流量控制、黑白名单）

* 完善的断连、Idle（空闲）等异常处理

## Netty 做的更好

### 1、规避JDK NIO bug

* 经典的 epoll bug：异常唤醒空转导致 CPU 100%  
jdk6 linux 2.4 _ jdk 不修复，netty 规避

* IP_TOS 参数（IP 包的优先级和 QoS 选项）使用时抛出异常
jdk12 解决，netty 规避

### 2、API 更友好更强大

* JDK 的 NIO 一些 API 不够友好，功能薄弱，例如 ByteBuffer -> Netty's ByteBuf  
ByteBuffer: 内部实现是一个final的字节数组，不可以扩容，只有一个指针维护他的状态，在读写切换操作的时候需要一个额外的操作(sleep)
ByteBuf: 有两个指针对应读写操作，可扩容

* 除了 NIO 外，也提供了其他一些增强：ThreadLocal -> Netty's FastThreadLocal

### 3、隔离变化、屏蔽细节

* 隔离 JDK NIO 的实现变化： nio -> nio2(aio) -> ...

* 屏蔽 JDK NIO 的实现细节

## 自己直接使用 JDK NIO 实现的可能性？

* 大概些多少行代码
Netty Transport Package: source code lines 18584

* 可能面对的问题： 400 open， 4347 closed（2019/09 统计）

* "踏平" 多少 JDK NIO bug：5654

* 未来能维护多久？Netty 已经维护 15 年（from 2004 to 2019）

## 直接使用 NIO = 一个人在战斗