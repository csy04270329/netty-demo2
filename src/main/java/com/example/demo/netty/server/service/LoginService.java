package com.example.demo.netty.server.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.repository.AccountsRepository;
import com.example.demo.repository.AccountIdChannelRepository;
import com.example.demo.repository.AccountIdRoomIdRepository;
import com.example.demo.repository.ChannelAccountIdRepository;
import com.example.demo.repository.RoomIdAccountIdRepository;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;

@Service
public class LoginService{

    @Autowired
    private AccountsRepository accountRepository;
    @Autowired
    private ChannelAccountIdRepository channelIdAccountIdRepository;
    @Autowired
    private AccountIdChannelRepository accountIdChannelRepository;
    @Autowired
    private AccountIdRoomIdRepository accountIdRoomIdRepository;
    @Autowired
    private RoomIdAccountIdRepository roomIdAccountIdRepository;
    @Autowired
    private MessageService messageService;

    public void login(Channel channel, String method, Map<String,Object> data, Map<String,Object> result) throws Exception{
        String accountId = (String) data.get("accountId");
        String password= (String)data.get("password");
        if(accountId==null || password==null){
            messageService.returnMessage(channel,result,new Exception("사용자 아이디 혹은 비밀번호가 비었습니다."),"1002");
            return;
        }
        Account account= accountRepository.getOne(accountId);
        if(account==null){
            messageService.returnMessage(channel,result,new Exception("사용자 아이디가 존재하지 않습니다."),"1003");
        }else if(!password.equals(account.getPassword())){
            messageService.returnMessage(channel,result, new Exception("비밀번호가 일치하지 않습니다."),"1004");
        }
        channelIdAccountIdRepository.getChannelIdAccountIdMap().put(channel.id(),accountId);
        accountIdChannelRepository.getAccountIdChannelMap().put(accountId,channel);
        messageService.returnMessage(channel,result,method);
    }

    public void removeAccount(Channel channel){
        ChannelId channelId= channel.id();
        Map<ChannelId,String> channelIdStringMap=channelIdAccountIdRepository.getChannelIdAccountIdMap();
        String accountId=channelIdStringMap.get(channelId);
        if(!StringUtils.isEmpty(accountId)){
            accountIdChannelRepository.getAccountIdChannelMap().remove(accountId);
            String roomId=accountIdRoomIdRepository.getAccountIdRoomIdMap().get(accountId);
            if(!StringUtils.isEmpty(roomId)){
                roomIdAccountIdRepository.getRoomIdAccountIdMap().remove(roomId,accountId);
                accountIdRoomIdRepository.getAccountIdRoomIdMap().remove(accountId);
            }
            channelIdAccountIdRepository.getChannelIdAccountIdMap().remove(channelId);
        }
    }
}