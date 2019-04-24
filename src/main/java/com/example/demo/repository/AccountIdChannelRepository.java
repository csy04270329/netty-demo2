package com.example.demo.repository;

import com.example.demo.netty.server.service.LoginService;
import com.example.demo.netty.server.service.MessageService;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountIdChannelRepository{
    @Autowired
    private LoginService loginService;
    @Autowired
    private MessageService messageService;
    private final Map<String, Channel> accountIdChannelMap= new ConcurrentHashMap<>();

    public Map<String,Channel> getAccountIdChannelMap() {
        return accountIdChannelMap;
    }

    public void writeAndFlush(String returnMessage) throws Exception{
        accountIdChannelMap.values().parallelStream().forEach(channel->{
            if(!channel.isActive()){
                loginService.removeAccount(channel);
                channel.close();
                return;
            }
            try {
                channel.writeAndFlush(messageService.returnMessage(returnMessage));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}