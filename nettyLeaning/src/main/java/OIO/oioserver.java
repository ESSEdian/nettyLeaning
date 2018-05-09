package OIO;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * @Author: ESSE
 * @Date: 18-5-8 下午5:17
 */
public class oioserver {

    public void serve (int port) throws IOException{
        final ServerSocket socket = new ServerSocket(port);
        try{
            for(;;){
                final Socket clientsocket = socket.accept();
                System.out.println("Accepted connection from" + clientsocket);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        OutputStream out;
                        try {
                            out = clientsocket.getOutputStream();
                            out.write("Hi\r\n".getBytes(Charset.forName("UTF-8")));
                            out.flush();
                            clientsocket.close();
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                        finally {
                            try{
                                clientsocket.close();
                            }
                            catch (IOException ex){
                                //ignore on close
                            }
                        }
                    }
                }).start();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        final int port = 8888;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new oioserver().serve(port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
