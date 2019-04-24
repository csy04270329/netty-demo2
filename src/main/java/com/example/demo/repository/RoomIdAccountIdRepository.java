package com.example.demo.repository;

import org.springframework.stereotype.Component;
import org.apache.commons.collections.map.MultiValueMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class RoomIdAccountIdRepository{
    private final MultiValueMap roomIdAccountIdMap = new MultiValueMap();
    public MultiValueMap getRoomIdAccountIdMap(){
        return roomIdAccountIdMap;
    }
}