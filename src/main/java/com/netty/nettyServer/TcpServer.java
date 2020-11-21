package com.netty.nettyServer;

import com.netty.nettyServer.businessHandler.BusinessHandler;
import com.netty.nettyServer.decoder.DecoderHabdler;
import com.netty.nettyServer.encoder.EncoderHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j
public class TcpServer {

    @Autowired
    BusinessHandler testBusinessHandler;

    @Value("${netty.tcp.listener.port:20000}")
    private  int port=20000;
    private static Map<String, Channel> map = new ConcurrentHashMap<String, Channel>();
    NioEventLoopGroup boss = new NioEventLoopGroup();//主线程组
    NioEventLoopGroup work = new NioEventLoopGroup();//工作线程组

    private Channel serverChannel;
    public  void init(){
        log.info("正在启动tcp服务器……");
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();//引导对象
            bootstrap.group(boss,work);//配置工作线程组
            bootstrap.channel(NioServerSocketChannel.class);//配置为NIO的socket通道
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel ch) throws Exception {//绑定通道参数
                    //在官方提供的示例中，Length是0x000C，高位在前，低位在后 但是报文给的是低位在前 高位在后
                    //解决粘包问题  释义：读第72个字节后面的4个字节 来截取报文的长度
                    ch.pipeline().addLast("chaiBao",new LengthFieldBasedFrameDecoder(ByteOrder.LITTLE_ENDIAN,10*1024,72,4,0,0,true));
                    ch.pipeline().addLast("logging",new LoggingHandler("DEBUG"));//设置log监听器，并且日志级别为debug，方便观察运行流程
                    ch.pipeline().addLast("encode",new EncoderHandler());//编码器。发送消息时候用
                    ch.pipeline().addLast("decode",new DecoderHabdler());//解码器，接收消息时候用
                    ch.pipeline().addLast("handler",testBusinessHandler);//业务处理类，最终的消息会在这个handler中进行业务处理
                }
            });
            bootstrap.option(ChannelOption.SO_BACKLOG,1024);//缓冲区
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE,true);//ChannelOption对象设置TCP套接字的参数，非必须步骤
            ChannelFuture future = bootstrap.bind(port).sync();//使用了Future来启动线程，并绑定了端口
            log.info("启动tcp服务器启动成功，正在监听端口:"+port);
            future.channel().closeFuture().sync();//以异步的方式关闭端口
            serverChannel =  bootstrap.bind(port).sync().channel().closeFuture().sync().channel();

        }catch (InterruptedException e) {
            log.info("启动出现异常："+e);
        }finally {
            work.shutdownGracefully();
            boss.shutdownGracefully();//出现异常后，关闭线程组
            log.info("tcp服务器已经关闭");
        }

    }

    public static void main(String[] args) {
        new TcpServer().init();
    }
    public static Map<String, Channel> getMap() {
        return map;
    }

    public static void setMap(Map<String, Channel> map) {
        TcpServer.map = map;
    }

    @PreDestroy
    public void destory() throws InterruptedException {
        boss.shutdownGracefully().sync();
        work.shutdownGracefully().sync();
        log.info("关闭tomcat的时候同时--》关闭Netty-------------------------------------------");
    }
}
