package com.netty.client.echo;

import com.netty.common.TestPctProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

public class TestBusinessHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TestPctProtocol testPctProtocolReveive = new TestPctProtocol();
        if(msg instanceof Object){
            testPctProtocolReveive = (TestPctProtocol)msg;
        }else{
            return;
        }
    }

    //连接成功后发送消息测试
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        TestPctProtocol testPctProtocol = new TestPctProtocol();

        testPctProtocol.setHeader((short)100);

        String message = "哈哈！！客户端连接上了";
        System.out.print(message);

        testPctProtocol.setLength(message.getBytes(CharsetUtil.UTF_8).length);

        testPctProtocol.setData(message.getBytes(CharsetUtil.UTF_8));

        ctx.writeAndFlush(testPctProtocol);
    }

}
