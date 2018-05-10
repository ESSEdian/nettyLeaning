package OIO;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.oio.OioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @version 1.0
 * @Author: ESSE
 * @Date: 18-5-9
 * @Time: 下午3:39
 */
public class NettyOioServer {

    public void serves(int port) throws InterruptedException {
        final ByteBuf buffer = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello\r\n", Charset.forName("UTF-8")));
        // 第一步，创建线程池
        EventLoopGroup bossGroup = new OioEventLoopGroup(1);
        EventLoopGroup workerGroup = new OioEventLoopGroup();

        try{
            // 第二步，创建启动类
            ServerBootstrap b = new ServerBootstrap();
            // 第三步，配置各组件
            b.group(bossGroup, workerGroup)
                    .channel(OioServerSocketChannel.class)
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
        NettyOioServer server = new NettyOioServer();
        server.serves(5556);
    }

}
