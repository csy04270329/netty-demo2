package com.example.demo.netty.server.service;

import com.example.demo.domain.repository.AccountsRepository;
import com.example.demo.repository.AccountIdChannelRepository;
import com.example.demo.repository.ChannelAccountIdRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SendService{
    private final ObjectMapper objectMapper=new ObjectMapper();
    @Autowired
    private ChannelAccountIdRepository channelIdAccountIdRepository;
    @Autowired
    private AccountIdChannelRepository accountIdChannelIdRepository;
    @Autowired
    private AccountsRepository accountRepository;

    public void send(Channel channel, String method, Map<String, Object> data, Map<String,Object> result) throws Exception{
        String accountId= channelIdAccountIdRepository.getChannelIdAccountIdMap().get(channel.id());
        result.put("method",method);
        result.put("accountId",accountId);
        result.put("accountName",accountRepository.getOne(accountId).getAccountName());
        result.put("content",data.get("content"));

        String resultMessage= objectMapper.writeValueAsString(result);
        accountIdChannelIdRepository.writeAndFlush(resultMessage);
    }
}