package com.fengxuechao.io.demo01;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private BufferedReader reader;
    private BufferedWriter writer;
    private Socket socket;
    //发送消息给服务器
    public void sendToServer(String msg) throws IOException {
        //发送之前，判断socket的输出流是否关闭
        if (!socket.isOutputShutdown()) {
            //如果没有关闭就把用户键入的消息放到writer里面
            writer.write(msg + "\n");
            writer.flush();
        }
    }
    //从服务器接收消息
    public String receive() throws IOException {
        String msg = null;
        //判断socket的输入流是否关闭
        if (!socket.isInputShutdown()) {
            //没有关闭的话就可以通过reader读取服务器发送来的消息。注意：如果没有读取到消息线程会阻塞在这里
            msg = reader.readLine();
        }
        return msg;
    }

    public void start() {
        //和服务创建连接
        try {
            socket = new Socket("127.0.0.1", 8888);
            reader=new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            writer=new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            //新建一个线程去监听用户输入的消息
            new Thread(new UserInputHandler(this)).start();
            /**
             * 不停的读取服务器转发的其他客户端的信息
             * 记录一下之前踩过的小坑：
             * 这里一定要创建一个msg接收信息，如果直接用receive()方法判断和输出receive()的话会造成有的消息不会显示
             * 因为receive()获取时，在返回之前是阻塞的，一旦接收到消息才会返回，也就是while这里是阻塞的，一旦有消息就会进入到while里面
             * 这时候如果输出的是receive(),那么上次获取的信息就会丢失，然后阻塞在System.out.println
             * */
            String msg=null;
            while ((msg=receive())!=null){
                System.out.println(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
               if(writer!=null){
                   writer.close();
               }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatClient().start();
    }
}