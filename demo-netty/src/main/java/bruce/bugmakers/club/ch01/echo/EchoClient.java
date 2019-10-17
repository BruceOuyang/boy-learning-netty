package bruce.bugmakers.club.ch01.echo;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * EchoClient
 *
 * @Author Bruce
 * @Date 2019/10/16 20:46
 * @Version 1.0
 **/
public class EchoClient {

    static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    public static void main(String [] args) throws InterruptedException {
        startEchoClient();
    }

    public static void startEchoClient() throws InterruptedException {
        // configure the client..
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            channelPipeline.addLast(new EchoClientHandler());
                        }
                    });

            // start the client
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090).sync();

            // wait until the connect is closed.
            channelFuture.channel().closeFuture().sync();
        } finally {
            // shutdown the event loop to terminate all threads
            group.shutdownGracefully();
        }
    }
}
