package com.netty.nettyServer;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @description:
 */

@Component
@Slf4j
@Order(1)
public class BaseDataInit implements CommandLineRunner {


    final TcpServer tcpServer;

    public BaseDataInit(TcpServer tcpServer) {
        this.tcpServer = tcpServer;
    }


    @SneakyThrows
    @Override
    @Async
    public void run(String... strings){
        //注意 一定要等到bean加载完之后再加载netty  否则netty里面使用的时候会无响应
        log.info("开始启动netty服务端");
        tcpServer.init();
    }
}
