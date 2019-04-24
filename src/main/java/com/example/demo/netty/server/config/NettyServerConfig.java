package com.example.demo.netty.server.config;

import com.example.demo.netty.server.handler.JsonHandler;
import com.example.demo.netty.server.initalizer.NettyChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;

@Configuration
public class NettyServerConfig{
    @Value("${netty.server.transfer.type}")
    private String transferType;
    @Value("${netty.server.transfer.port}")
    private int transferPort;
    @Value("${netty.server.thread.count.boss}")
    private int threadCountBoss;
    @Value("${netty.server.thread.count.worker}")
    private int threadCountWorker;
    @Value("${netty.server.log.level.bootstrap}")
    private String logLevelBootstrap;

    @Autowired
    private NettyChannelInitializer nettyChannelInitializer;

    @Bean(destroyMethod= "shutdownGracefully")
    public NioEventLoopGroup bossGroup(){
        switch (transferType){
            case "tcp":
            default:
                return new NioEventLoopGroup(threadCountBoss);
        }
    }

    @Bean(destroyMethod= "shutdownGracefully")
    public NioEventLoopGroup workerGroup(){
        switch (transferType){
            case "tcp":
            default:
                return new NioEventLoopGroup(threadCountWorker);
        }
    }

    @Bean
    public InetSocketAddress port(){
        return new InetSocketAddress(transferPort);
    }

    @Bean
    public ServerBootstrap serverBootstrap(){
        ServerBootstrap serverBootstrap= new ServerBootstrap();
        serverBootstrap.group(bossGroup(),workerGroup())
                .handler(new LoggingHandler(LogLevel.valueOf(logLevelBootstrap)))
                .childHandler(nettyChannelInitializer);

        switch (transferType){
            case "websocket":
            case "tcp":
            default:
                serverBootstrap.channel(NioServerSocketChannel.class);
        }
        return serverBootstrap;
    }

    @Bean
    public ChannelInboundHandlerAdapter handler(){
        return new JsonHandler();
    }
}