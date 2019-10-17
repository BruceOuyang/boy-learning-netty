# Netty 对常用编解码的支持

* 为什么需要 “二次” 解码

* 常用的 “二次” 编解码方式

* 选择编解码方式的要点

* Protobuf 简介与使用

* 源码解读：Netty 对常用编解码的支持

## 为什么需要 “二次” 解码

假设我们把解决粘包、半包问题的常见三种解码器叫一次解码器，那么，我们在项目中，除了可选的压缩解压缩之外，还需要一层解码，因为一次解码的结果是字节，需要和项目中所使用的的对象做转化，方便使用，这层解码器可以称为“二次解码器”，相应的，对应的编码器是为了将 Java 对象转化为字节流方便存储或传输。

**一次解码器的结果是字节，二次解码器的结果是对象**

* 一次解码器：ByteToMessageDecoder

    * io.netty.buffer.ByteBuf（原始数据流） -> io.netty.buffer.ByteBuf （用户数据流）

* 二次解码器：MessageToMessageDecoder<I>

    * io.netty.buffer.ByteBuf （用户数据） -> Java Object
    
**思考：是不是可以一步到位？合并一次解码（解决粘包、半包）和二次解码（解决可操作性问题）**  

可以，但是不建议  

* 没有分层，不够清晰

* 耦合性高，不容易置换方案

## 常用的 “二次” 编解码方式

* Java 序列化：只有 java 能用，比较占空间

* Marshaling：jboss

* XML

* JSON

* MessagePack

* Protobuf：性能好，可读性差

* 其他

## 选择编解码方式的要点

* 空间：编码后占用空间大小，需要比较不同原始数据大小情况

* 时间：编码后占用时间大小，需要比较不同原始数据大小情况

* 是否追求可读性

* 对多语言的支持

## Protobuf 简介与使用

**简介**

* Protobuf 是一个灵活的、高效的用于序列化数据的协议。

* 相比较 XML 和 JSON 格式，Protobuf 更小、更快、更便捷。

* Protobuf 是跨语言的，并且自带了一个编译器（protoc），只需要用它进行编译，可以自动生成Java、python、C++等代码，不需要在写其他代码。

**使用**

* 下载对应操作系统的 protoc

* 编写 .proto 文件

* 生成代码
```
protoc.exe --java_out=. demo.proto
protoc.exe --python_out=. demo.proto
protoc.exe --cpp_out=. demo.proto
```

## 源码解读：Netty 对常用编解码的支持

* netty 原代码的 netty-codec 模块下可以看到 netty 支持的编解码方式

* netty 使用 Protobuf 编解码示例  
```java
// 参考源代码：WorldClockClientInitializer.java
public void initChannel(SocketChannel ch) {
    ChannelPipeline p = ch.pipeline();
    if (sslCtx != null) {
        p.addLast(sslCtx.newHandler(ch.alloc(), WorldClockClient.HOST, WorldClockClient.PORT));
    }
    // varint 为可变长度
    p.addLast(new ProtobufVarint32FrameDecoder());
    p.addLast(new ProtobufDecoder(WorldClockProtocol.LocalTimes.getDefaultInstance()));

    p.addLast(new ProtobufVarint32LengthFieldPrepender());
    p.addLast(new ProtobufEncoder());

    p.addLast(new WorldClockClientHandler());
}
```

