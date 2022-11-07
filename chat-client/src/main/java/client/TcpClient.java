package client;

import client.echo.TestBusinessHandler;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.TestPctProtocol;
import decoder.DecoderHabdler;
import encoder.EncoderHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import java.util.Arrays;
import java.util.Scanner;

public class TcpClient {

    private String ip;
    private int port;
    public  void init() throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_KEEPALIVE,true);
        bootstrap.handler(new ChannelInitializer() {
            @Override
            protected void initChannel(Channel ch) throws Exception {
                ch.pipeline().addLast("logging",new LoggingHandler("DEBUG"));
                ch.pipeline().addLast("encode",new EncoderHandler());//编码器。发送消息时候用
                ch.pipeline().addLast("decode",new DecoderHabdler());//解码器，接收消息时候用
                ch.pipeline().addLast("handle",new TestBusinessHandler());
            }
        });
        bootstrap.remoteAddress(ip,port);
        ChannelFuture channelFuture = bootstrap.connect().sync();
        Channel channel = channelFuture.channel();
        //客户端发送消息
            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()){
                String msg = scanner.nextLine();
                //通过客户端把输入内容发送到服务端
                TestPctProtocol testPctProtocol = new TestPctProtocol();

                testPctProtocol.setHeader((short)100);

                testPctProtocol.setLength(msg.getBytes(CharsetUtil.UTF_8).length);

                testPctProtocol.setData(msg.getBytes(CharsetUtil.UTF_8));
                channel.writeAndFlush(testPctProtocol).sync();
                if(msg.equals("quit")) {
                    channel.close().sync();
                    break;
                }
            }
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    public TcpClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }
    public static void main(String[] args) throws InterruptedException {
        if(args.length > 0){
            new TcpClient(args[0],20000).init();
        }else{
            new TcpClient("127.0.0.1",20000).init();
        }
    }
}
