# Netty 的现状与趋势

## 社区现状

* https://github.com/netty/netty

* Stars: 20000+ (Top 25 repository on java)

* 维护者

    22 members (core: Trustin Lee and Norman Maurer)

* 分支

    * 4.1 master （16年5月创建，hours/days ago） 支持 Android
    
    * 4.0 （13年7月创建， 最有一次提交时2018年2月13号）线程模型优化、包结构、命名

* 最新版本

    * Netty 4.1.39.Final (2019/08)
    
    * Netty 4.0.56.Final (2018/02)
    
    * Netty 3.10.6.Final (2016/06)
    
## 应用现状

    * 截止 2019/09,30000+项目在使用
    
    * 统计方法：在 github 上 pom.xml 中声明了 io.netty:netty-all
    
    * 为考虑情况：非开源软件和 Netty3.x 使用者
    
* 一些典型项目：

    * 数据库：Cassandra
    
    * 大数据处理：Spark、Hadoop
    
    * 消息队列：RocketMQ
    
    * 检索：ElasticSearch
    
    * 框架：gRPC、Apache Dubbo、Spring5(Spring Web Flux)
    
    * 分布式协调器：Zookeeper
    
    * 工具类：async-http-client
    
    * 其他参考：https://netty.io/wiki/adopters.html 
    
## 趋势

* 更多流行协议的支持

* 跟紧 JDK 的更新步伐

* 更多易用、人性化的功能：黑白名单、流量整形

* 应用越来越多