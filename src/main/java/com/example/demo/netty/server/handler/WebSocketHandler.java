package com.example.demo.netty.server.handler;

import com.example.demo.netty.server.service.LoginService;
import com.example.demo.netty.server.service.MessageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Qualifier("webSocketHandler")
@ChannelHandler.Sharable
public class WebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    private final ObjectMapper objectMapper=new ObjectMapper();

    @Autowired
    private MessageService messageService;
    @Autowired
    private LoginService loginService;

    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws  Exception{
        Map<String,Object> result=new HashMap<>();
        //연결 정보, 접속자 채널 정보
        Channel channel= ctx.channel();
        Map<String,Object> data;
        if(!(frame instanceof TextWebSocketFrame)){
            throw new UnsupportedOperationException("unsupported frame type: "+frame.getClass().getName());
        }
        data=objectMapper.readValue(((TextWebSocketFrame)frame).text(),new TypeReference<Map<String,Object>>(){

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