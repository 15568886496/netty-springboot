package client.echo;

import common.TestPctProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestBusinessHandler extends ChannelInboundHandlerAdapter {


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        TestPctProtocol testPctProtocolReveive = new TestPctProtocol();
        if(msg instanceof Object){
            log.info("customer receive message {}",new String(((TestPctProtocol) msg).getData(),"utf-8"));
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

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
