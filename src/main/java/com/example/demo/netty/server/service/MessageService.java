package com.example.demo.netty.server.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MessageService{
    private final ObjectMapper objectMapper= new ObjectMapper();

    @Value("${netty.server.transfer.type}")
    private String transferType;

    @Autowired
    private LoginService loginService;

    @Autowired
    private SendService sendService;

    @Autowired
    private RoomService roomService;

    public void execute(Channel channel, Map<String,Object> data, Map<String,Object> result) throws Exception{
        String method= (String)data.getOrDefault("method","");
        switch (method){
            case "login":
                loginService.login(channel,method,data,result);
                break;
            case "send":
                sendService.send(channel,method,data,result);
            case "create_room":
                roomService.enter(channel,method,data,result);
            case "exit_room":
                roomService.exit(channel,method,result);
            case "send_room":
                roomService.send(channel,method,data,result);
            default:
                returnMessage(channel,result,new Exception("메세지 구분이 정확하지 않습니다."),"1005");
                return;
        }
    }

    public void returnMessage(Channel channel,Map<String,Object>result, Throwable throwable, String status) throws Exception{
        result.put("status",status);
        result.put("message", ExceptionUtils.getStackTrace(throwable));
        channel.writeAndFlush(returnMessage(result));
    }

    public void returnMessage(Channel channel,Map<String,Object>result, String method) throws Exception{
        result.put("status","0");
        result.put("method",method);
        channel.writeAndFlush(returnMessage(result));
    }

    public Object returnMessage(Map<String,Object>result) throws Exception{
        switch (transferType){
            case "websocket":
                return new TextWebSocketFrame(objectMapper.writeValueAsString(result));
            case "tcp":
            default:
                return objectMapper.writeValueAsString(result)+System.lineSeparator();
        }
    }

    public Object returnMessage(String message) throws Exception{
        switch (transferType){
            case "websocket":
                return new TextWebSocketFrame(message);
            case "tcp":
            default:
                return message+System.lineSeparator();
        }
    }
}