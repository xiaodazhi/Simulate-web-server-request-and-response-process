package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 注意  通常来说  服务器只有一个  而客户端有多个
 * 多个客户端同时访问服务器时  我们该如何处理  使用线程
 */

/**
 * 端口号 我们可以不写死  而是将端口号写入配置文件server.properties中  我们需要时再从文件中去读取
 */
public class Server {

    public void startServer(){
        try {
            System.out.println("====启动服务器====");
            //获取配置文件中的port端口号
            int port = Integer.parseInt(ServerFileReader.getValue("port"));
            //自己创建一个服务  开放一个接口
            ServerSocket server = new ServerSocket(port);
            while(true) {
                //等待某一个客户端连接  如果连接成功  就会有一个socket对象  通过这个对象启动一个线程
                //负责处理当前浏览器发送过来的消息
                Socket socket = server.accept();
                new ServerHandler(socket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
