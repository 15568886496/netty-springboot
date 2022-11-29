package com.netty.nettyServer.businessHandler;

import com.netty.nettyServer.TcpServer;
import common.TestPctProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;


@io.netty.channel.ChannelHandler.Sharable
@Slf4j
public class BusinessHandler extends ChannelInboundHandlerAdapter {

    /*
    * todo 根据业务注入需要的bean
    * */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        System.out.print("客户端"+getRemoteAddress(ctx)+" 接入连接");
        //往channel map中添加channel信息
        TcpServer.getMap().put(getIPString(ctx), ctx.channel());
    }

    public static String getIPString(ChannelHandlerContext ctx){
        String ipString = "";
        String socketString = ctx.channel().remoteAddress().toString();
        int colonAt = socketString.indexOf(":");
        ipString = socketString.substring(1, colonAt);
        return ipString;
    }

    public static String getRemoteAddress(ChannelHandlerContext ctx){
        String socketString = "";
        socketString = ctx.channel().remoteAddress().toString();
        return socketString;
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //删除Channel Map中的失效Client
        TcpServer.getMap().remove(getIPString(ctx));
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TestPctProtocol testPctProtocolReveive = new TestPctProtocol();
        String strString = "";
        if(msg instanceof Object){
            testPctProtocolReveive = (TestPctProtocol)msg;
             strString = new String(((TestPctProtocol) msg).getData(),"utf-8");
        }else{
            return;
        }
        testPctProtocolReveive.setHeader((short)100);
        String message = "服务端收到了"+getIPString(ctx)+"发过来的消息["+strString+"],并说了句你好";
        log.info("receive message {}", message);
        testPctProtocolReveive.setLength(message.length());
        this.constructHeader((short)100,message,ctx);
    }



    public void constructHeader(short command,String message,ChannelHandlerContext ctx){
        TestPctProtocol testPctProtocol = new TestPctProtocol();

        testPctProtocol.setHeader((short)100);

        byte[] message1 = message.getBytes(CharsetUtil.UTF_8);

        testPctProtocol.setLength(message1.length);

        testPctProtocol.setData(message1);

        ctx.writeAndFlush(testPctProtocol);
    }
}
