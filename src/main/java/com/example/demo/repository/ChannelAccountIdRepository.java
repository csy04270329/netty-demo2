package com.example.demo.repository;

import io.netty.channel.ChannelId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChannelAccountIdRepository{
    private final Map<ChannelId,String> channelIdAccountIdMap=new ConcurrentHashMap<>();

    public Map<ChannelId,String> getChannelIdAccountIdMap() {
        return channelIdAccountIdMap;

    }
}