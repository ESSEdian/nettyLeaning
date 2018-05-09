package NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: ESSE
 * @Date: 18-5-8 下午5:43
 */
public class nioServer {

    public void service(int port) throws IOException {
        // 打开selector 处理channel
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        ServerSocket socket = serverChannel.socket();
        InetSocketAddress adress = new InetSocketAddress(port);
        socket.bind(adress);
        Selector selector = Selector.open();
        //注册selector
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        final ByteBuffer msg = ByteBuffer.wrap("hello".getBytes());
        for(;;){
            //等待新的处理事件
            try {
                selector.select();
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            //所有的selectionKey实例
            Set<SelectionKey> readKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readKeys.iterator();
            while (iterator.hasNext()){
                SelectionKey key = iterator.next();
                iterator.remove();;
                //查看它是否是等待接受的新连接

                try {
                    if(key.isAcceptable()){
                        ServerSocketChannel server = (ServerSocketChannel) key.channel();
                        SocketChannel client = server.accept();
                        client.configureBlocking(false);
                        client.register(selector, SelectionKey.OP_WRITE|SelectionKey.OP_READ, msg.duplicate());
                        System.out.println("a new Connetion" + client);
                    }
                    //准备好写
                    if(key.isWritable()){
                        SocketChannel client = (SocketChannel) key.channel();
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        while (buffer.hasRemaining()){
                            if(client.write(buffer) == 0){
                                break;
                            }
                        }
                        client.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    key.cancel();
                    try {
                        key.channel().close();
                    }catch (IOException cex){
                        cex.printStackTrace();
                    }
                }


            }

        }


    }

    public static void main(String[] args) {
        final int port = 8889;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new nioServer().service(port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
