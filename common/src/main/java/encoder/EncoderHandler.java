package encoder;

import common.TestPctProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.log4j.Logger;

public class EncoderHandler extends MessageToByteEncoder {
    private Logger logger = Logger.getLogger(this.getClass());

    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        if (msg instanceof TestPctProtocol){
            TestPctProtocol protocol = (TestPctProtocol) msg;

            out.writeShortLE(protocol.getHeader());
            out.writeIntLE(protocol.getLength());
            out.writeBytes(protocol.getData());
            logger.debug("数据编码成功："+out);
        }else {
            logger.info("不支持的数据协议："+msg.getClass()+"\t期待的数据协议类是："+ TestPctProtocol.class);
        }
    }

}
