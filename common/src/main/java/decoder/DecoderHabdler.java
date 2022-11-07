package decoder;

import common.TestPctProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;

@Slf4j
public class DecoderHabdler extends ByteToMessageDecoder {
    private Logger logger = Logger.getLogger(this.getClass());

    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if(in.readableBytes()>6){
            in.markReaderIndex();
            TestPctProtocol readTestPctProtocol = new TestPctProtocol();
            readTestPctProtocol.setHeader(in.readShortLE());
            readTestPctProtocol.setLength(in.readIntLE());
            log.info("数组长度 "+ readTestPctProtocol.getLength());
            byte[] dataByte = new byte[readTestPctProtocol.getLength()];
            if(dataByte.length > in.readableBytes()){
                in.resetReaderIndex();
                // 回收已读字节
                in.discardReadBytes();
            }else {
                in.readBytes(dataByte);
                readTestPctProtocol.setData(dataByte);
                out.add(readTestPctProtocol);
            }
        }
    }

}
