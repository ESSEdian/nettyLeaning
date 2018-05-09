import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.CharsetUtil;

/**
 * @Author: ESSE
 * @Date: 18-5-7 下午6:05
 */
public class ResponseServerHandler extends ChannelHandlerAdapter {

    /**
     * 这里我们覆盖了chanelRead()事件处理方法。
     * 每当从客户端收到新的数据时，
     * 这个方法会在收到消息时被调用，
     *ChannelHandlerContext对象提供了许多操作，
     * 使你能够触发各种各样的I/O事件和操作。
     * 这里我们调用了write(Object)方法来逐字地把接受到的消息写入
     * @param ctx 通道处理的上下文信息
     * @param msg 接收的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        System.out.println(in.toString(CharsetUtil.UTF_8));
        ctx.write(msg);
        //cxt.writeAndFlush(msg)
        //请注意，这里我并不需要显式的释放，因为在进入的时候netty已经自动释放
        // ReferenceCountUtil.release(msg);
    }
    /**
     * ctx.write(Object)方法不会使消息写入到通道上，
     * 他被缓冲在了内部，你需要调用ctx.flush()方法来把缓冲区中数据强行输出。
     * 或者你可以在channelRead方法中用更简洁的cxt.writeAndFlush(msg)以达到同样的目的
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }
    /**
     * 这个方法会在发生异常时触发
     *
     * @param ctx
     * @param cause
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        /***
         * 发生异常后，关闭连接
         */
        cause.printStackTrace();
        ctx.close();
    }
}
