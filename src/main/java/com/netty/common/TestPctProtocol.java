package com.netty.common;


import lombok.Data;

@Data
public class TestPctProtocol {

    private short header;//（1—2）固定标识
    private int length;//（3-6）数据长度
    private byte[] data;//业务报文

}
