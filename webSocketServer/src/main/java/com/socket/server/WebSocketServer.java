package com.socket.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javax.annotation.PreDestroy;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

/*
 * Netty Reactor模型 -
 * 1.单线程模型:一个用户一个线程来处理，线程有极限
 * 2.多线程模型：加入线程池,线程池线程轮询执行任务
 * 3.主从多线程模型：俩个线程池，一个线程池接收请求，一个线程池处理IO（推荐，适用高并发环境）
 *
 * 以下代码为主从多线程模型
 * */
@Component
@Log4j
public class WebSocketServer {
	NioEventLoopGroup mainGrp=new NioEventLoopGroup();//主线程池
	NioEventLoopGroup subGrp=new NioEventLoopGroup();//从线程池
	public  void init() {
		try {
			//1.创建netty服务器启动对象
			ServerBootstrap serverBootstrap=new ServerBootstrap();

			//2.初始化
			serverBootstrap
					//指定使用的线程池
					.group(mainGrp,subGrp)
					//指定netty通道类型
					.channel(NioServerSocketChannel.class)
					// 指定通道初始化器用来加载当Channel收到事件消息后，
					// 如何进行业务处理
					.childHandler(new WebSocketChannelInitializer());

			//3.绑定端口，以同步的方式启动
			ChannelFuture future=serverBootstrap.bind(9090).sync();

			//4.等待服务关闭
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();

			//5.异常关闭服务器
			mainGrp.shutdownGracefully();
			subGrp.shutdownGracefully();
		}
	}
	@PreDestroy
	public void destory() throws InterruptedException {
		mainGrp.shutdownGracefully().sync();
		subGrp.shutdownGracefully().sync();
		log.info("关闭tomcat的时候同时--》关闭Netty-------------------------------------------");
	}
}