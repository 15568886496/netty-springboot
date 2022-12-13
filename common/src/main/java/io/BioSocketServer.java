package io;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TODO
 * <p>
 * User: Kevin_Li1 DATE: 12/1/2022 9:23 PM
 **/
public class BioSocketServer {



  public static void main(String[] args) throws IOException {
    //Creates a server socket, bound to the specified port
    ServerSocket bioSocketServer = new ServerSocket(9000);
    while (true){
      System.out.print("等待连接。。。");
      //Listens for a connection to be made to this socket and accepts
      //it. The method blocks until a connection is made.
      Socket clientSocket = bioSocketServer.accept();
      System.out.print("有客户端连接了");
      handler(clientSocket);
    }
  }
  private static void handler(Socket clientSocket) throws IOException {
     byte[] bytes = new byte[1024];
     System.out.println("准备read。。");
     //接收客户端的数据，阻塞方法，没有数据可读时就阻塞
     int read = clientSocket.getInputStream().read(bytes);
     System.out.println("read完毕。。");
     if (read != -1){
       System.out.println("接收到客户端的数据：" + new String(bytes, 0, read));
       clientSocket.getOutputStream().write("HelloClient".getBytes());
       clientSocket.getOutputStream().flush();
     }
 }

}
