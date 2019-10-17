# keepalive 与 idle 监测

* 为什么需要 keepalive？

* 怎么设计 keepalive？以 TCP keepalive 为例

* 为什么还需要应用层 keepalive？

* Idle 监测是什么？

* 如何在 Netty 中开启 TCP keepalive 和 Idle 监测

## 为什么需要 keepalive？

**生活场景**  

假设你开了一个饭店，别人打电话来订餐，电话通了后，客户说了一堆订餐需求，说着说着就，对方就不讲话了（可能忘记挂机/出去办事/线路故障等）。

* 这个时候你会一直握着电话等么？

    * 不会

* 如果不会，那你一般怎么去做？
    
    * 会确认一句“你还在么？”，如果对方没有回复，那就挂机。这套机制即 “keepalive”。
    
**类比服务器应用**  

订餐电话场景 | 服务器应用  
:-- | :--  
电话线路 | 数据连接 （TCP连接）  
交谈的话语 | 数据  
通话双方 | 数据发送方和接收方  

对比场景 | 订餐电话场景 | 应用服务器  
:-- | :-- | :--  
需要 keepalive 的场景 | 对方临时着急离开 | 对端异常“崩溃”  
需要 keepalive 的场景 | 对方在，但是很忙，不知道什么时候忙完 | 对端在，但是处理不过来  
需要 keepalive 的场景 | 电话线路故障 | 对端在，但是不可达  
不做 keepalive 的后果 | 线路占用，耽误其他人订餐 | 连接已坏，但是还浪费资源维持，下次用会直接报错  

## 怎么设计 keepalive？以 TCP keepalive 为例

TCP keepalive 核心参数  
```
# sysctl -a | grep tcp_keepalive
net.ipv4.tcp_keepalive_time=7200   # 问题出现概率小 -> 没有必要频繁
net.ipv4.tcp_keepalive_intvl=75
net.ipv4.tcp_keepalive_probes=9    # 判断需“谨慎” -> 不能武断
```

当启动（默认关闭） keepalive 时，TCP 在连接没有数据通过的 7200 秒后发送 keepalive 消息，当探测没有确认时，按 75 秒的重试频率重发，一直发 9 个探测包都没有确认，就认定连接失败。

所以总耗时一般为：2 小时 11 分钟（7200 秒 + 75 秒 * 9次）

## 为什么还需要应用层 keepalive？

* 协议分层，各层关注点不同：

    传输层关注是否“通”，应用层关注是否可服务。类比前面的电话订餐例子，电话能通，不代表有人接；服务器连接在，但是不一定可以服务（如服务不过来等）。
    
* TCP 层的 keepalive 默认关闭，且经过路由等中转设备 keepalive 包可能会被丢弃。

* TCP 层的 keepalive 时间太长，默认 > 2 小时，虽然可以改，但属于系统参数，改动影响所有应用。

**提示：**

HTTP 属于应用层协议，但是常常听到名词“HTTP Keep-Alive”指的是对长连接和短连接的选择：  

* Connection：Keep-Alive 长连接（HTTP/1.1 默认长连接，不需要带这个 header）

* Connection：Close 短连接

## Idle 监测是什么？

**重现生活场景**

假设你开了一个饭店，别人打电话来订餐，电话通了后，客户说了一堆订餐需求，说着说着就，对方就不讲话了（可能忘记挂机/出去办事/线路故障等）。

* 这个时候你会一直握着电话等么？

    * 不会
    
    * 一般你会稍微等待一定的时间，在这个时间内看看对方还会不会说话（Idle检测），如果还不说，认定对方存在问题（Idle），于是开始发问“你还在么？”（keepalive），或者问都不问干脆直接挂机。（关闭连接）
    
**Idle检测**

Idle 监测，只是负责诊断，诊断后，做出不同的行为，决定 Idle 检测的最终用途：

* 发送 keepalive：一般用来配合 keepalive，减少 keepalive 消息。

    Keepalive 设计演进： V1 定时 keepalive 消息 -> V2 空闲监控 + 判定为 Idle 时才发 keepalive
    
    * V1：keepalive 消息与服务器正常信息交换完全不关联，定时就发送；
    
    * V2：有其他数据传输的时候，不发送 keepalive，无数据传输超过一定时间，判定为 Idle，再发 keepalive。
    
* 直接关闭连接：

    * 快速释放损坏的、恶意的、很久不用的连接，让系统时刻保持最好的状态。
    
    * 简单粗暴，客户端可能需要重连。
    
实际应用中：结合起来用。按需 keepalive，保证不会空闲，如果空闲，关闭连接。

## 如何在 Netty 中开启 TCP keepalive 和 Idle 监测

开启不同的 Idle Check：  

* ch.pipeline().addLast("idleCheckHandler", new IdleStateHandler(0, 20, 0, TimeUnit.Seconds));