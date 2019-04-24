package com.example.demo.netty.server.handler;

import com.example.demo.netty.server.service.LoginService;
import com.example.demo.netty.server.service.MessageService;
import com.example.demo.netty.server.service.RoomService;
import com.example.demo.netty.server.service.SendService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("jsonHandler")
@ChannelHandler.Sharable
public class JsonHandler extends SimpleChannelInboundHandler<String> {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MessageService messageService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private SendService sendService;
    @Autowired
    private RoomService roomService;


    protected void channelRead0(ChannelHandlerContext ctx, String s) throws  Exception{
        Map<String,Object> result=new HashMap<>();
        //연결 정보, 접속자 채널 정보
        Channel channel= ctx.channel();
        Map<String,Object> data;
        data=objectMapper.readValue(s,new TypeReference<Map<String,Object>>(){
        });
        messageService.execute(channel,data,result);
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception{
        loginService.removeAccount(ctx.channel());
        ctx.close();
    }

    public void exceptionCaught(ChannelHandlerContext ctx,Throwable cause) throws Exception{
        super.exceptionCaught(ctx,cause);
    }
}