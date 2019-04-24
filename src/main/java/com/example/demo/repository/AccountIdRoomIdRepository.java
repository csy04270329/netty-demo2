package com.example.demo.repository;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountIdRoomIdRepository{
    private final Map<String,String> accountIdRoomIdMap= new ConcurrentHashMap<>();
    public Map<String,String> getAccountIdRoomIdMap(){
        return accountIdRoomIdMap;
    }
}