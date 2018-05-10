package NIO;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @version 1.0
 * @Author: ESSE
 * @Date: 18-5-9
 * @Time: 下午3:40
 */
public class NettyNioServer {

    public void serve(int port) throws InterruptedException {
        final ByteBuf buffer = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi\r\n", Charset.forName("UTF-8")));
        // 第一步，创建线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try{
            // 第二步，创建启动类
            ServerBootstrap b = new ServerBootstrap();
            // 第三步，配置各组件
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(buffer.duplicate()).addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });
            // 第四步，开启监听
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        NettyNioServer server = new NettyNioServer();
        server.serve(5555);
    }
}
