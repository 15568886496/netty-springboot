package com.socket.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    //存储用户连接
    private static ChannelGroup clients=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-mm-dd hh:MM:ss");

    // 当Channel中有新的事件消息会自动调用
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame) throws Exception {
        String text=textWebSocketFrame.text();
        System.out.println("接收到消息："+text);

        // 获取客户端发送过来的文本消息
        for (Channel client :clients) {
            // 将消息发送到所有的客户端
//            client.writeAndFlush(new TextWebSocketFrame(sdf.format(new Date())+":"+text));
            client.writeAndFlush(new TextWebSocketFrame(text));
        }
    }

    // 当有新的客户端连接服务器之后，会自动调用这个方法
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        clients.add(ctx.channel());
    }

    // 端口连接处理
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        System.out.println("断开连接");
        clients.remove(ctx.channel());
    }
}