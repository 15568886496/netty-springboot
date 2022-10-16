package decoder;

import common.TestPctProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import org.apache.log4j.Logger;

public class DecoderHabdler extends ByteToMessageDecoder {
    private Logger logger = Logger.getLogger(this.getClass());

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //标记读写位置
        in.markReaderIndex();

        TestPctProtocol readTestPctProtocol = new TestPctProtocol();
        readTestPctProtocol.setHeader(in.readShortLE());

        readTestPctProtocol.setLength(in.readIntLE());

        //解决tcp传输报文过长 自动拆包问题
        if(readTestPctProtocol.getLength()>in.readableBytes()){
            logger.debug(String.format("数据长度不够，数据协议len长度为：%1$d,数据包实际可读内容为：%2$d正在等待处理拆包……",readTestPctProtocol.getLength(),in.readableBytes()));
            in.resetReaderIndex();
        }

        byte[] dataByte = new byte[readTestPctProtocol.getLength()];
        in.readBytes(dataByte);
        readTestPctProtocol.setData(dataByte);
        out.add(readTestPctProtocol);

        // 回收已读字节
        in.discardReadBytes();
    }

}
