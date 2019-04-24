package com.example.demo.netty.server.service;

import com.example.demo.domain.Account;
import com.example.demo.domain.repository.AccountsRepository;
import com.example.demo.repository.AccountIdChannelRepository;
import com.example.demo.repository.AccountIdRoomIdRepository;
import com.example.demo.repository.ChannelAccountIdRepository;
import com.example.demo.repository.RoomIdAccountIdRepository;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class RoomService{
    @Autowired
    private AccountsRepository accountRepository;
    @Autowired
    private AccountIdChannelRepository accountIdChannelIdRepository;
    @Autowired
    private ChannelAccountIdRepository channelIdAccountIdRepository;
    @Autowired
    private AccountIdRoomIdRepository accountIdRoomIdRepository;
    @Autowired
    private RoomIdAccountIdRepository roomIdAccountIdRepository;
    @Autowired
    private MessageService messageService;
    @Autowired
    private LoginService loginService;

    public void create(Channel channel, String method, Map<String,Object> result) throws Exception{
        String accountId= channelIdAccountIdRepository.getChannelIdAccountIdMap().get(channel.id());
        if(accountIdRoomIdRepository.getAccountIdRoomIdMap().containsKey(accountId)){
            messageService.returnMessage(channel,result,new Exception("룸에 이미 입장한 사용자입니다."),"1006");
            return;
        }

        String roomId= UUID.randomUUID().toString();

        roomIdAccountIdRepository.getRoomIdAccountIdMap().put(roomId,accountId);
        accountIdRoomIdRepository.getAccountIdRoomIdMap().put(accountId,roomId);
        result.put("method",method);
        result.put("roomId",roomId);
        messageService.returnMessage(channel,result,method);
    }

    public void enter(Channel channel, String method, Map<String,Object> data,Map<String,Object> result) throws Exception{
        String accountId=channelIdAccountIdRepository.getChannelIdAccountIdMap().get(channel.id());
        if(accountIdRoomIdRepository.getAccountIdRoomIdMap().containsKey(accountId)){
            messageService.returnMessage(channel,result,new Exception("룸에 입장해있는 사용자입니다."),"1006");
            return;
        }
        String roomId= (String)data.get("roomId");
        if(!roomIdAccountIdRepository.getRoomIdAccountIdMap().containsKey(roomId)){
            messageService.returnMessage(channel,result,new Exception("존재하지 않는 룸입니다."),"1007");
            return;
        }
        roomIdAccountIdRepository.getRoomIdAccountIdMap().put(roomId,accountId);
        accountIdRoomIdRepository.getAccountIdRoomIdMap().put(accountId,roomId);
        result.put("method",method);
        result.put("roomId",roomId);
        messageService.returnMessage(channel,result,method);
    }

    public void exit(Channel channel, String method, Map<String,Object> result) throws Exception{
        String accountId= channelIdAccountIdRepository.getChannelIdAccountIdMap().get(channel.id());
        if(!accountIdRoomIdRepository.getAccountIdRoomIdMap().containsKey(accountId)){
            messageService.returnMessage(channel,result,new Exception("룸에 존재하지 않는 사용자입니다."),"1008");
            return;
        }

        String roomId= accountIdRoomIdRepository.getAccountIdRoomIdMap().get(accountId);

        roomIdAccountIdRepository.getRoomIdAccountIdMap().remove(roomId,accountId);
        accountIdRoomIdRepository.getAccountIdRoomIdMap().remove(accountId);
        result.put("method",method);
        messageService.returnMessage(channel,result,method);
    }

    public void send(Channel channel,String method,Map<String,Object> data,Map<String,Object> result) throws Exception{
        String accountId= channelIdAccountIdRepository.getChannelIdAccountIdMap().get(channel.id());
        if(!accountIdRoomIdRepository.getAccountIdRoomIdMap().containsKey(accountId)){
            messageService.returnMessage(channel,result,new Exception("룸에 존재하지 않는 사용자입니다."),"1008");
            return;
        }
        Account account= accountRepository.getOne(accountId);
        if(account==null) messageService.returnMessage(channel,result,new Exception("사용자 정보를 조회할 수 없습니다."),"1009");
        String accountName= account.getAccountName();
        result.put("method",method);
        result.put("accountId",accountId);
        result.put("accountName",accountName);
        result.put("content",data.get("content"));

        String roomId= accountIdRoomIdRepository.getAccountIdRoomIdMap().get(accountId);

        roomIdAccountIdRepository.getRoomIdAccountIdMap().getCollection(roomId).parallelStream().forEach(otherAccountId->{
            Channel otherChannel= accountIdChannelIdRepository.getAccountIdChannelMap().get(otherAccountId);
            if(!otherChannel.isActive()) {
                loginService.removeAccount(otherChannel);
                return;
            }
            try {
                messageService.returnMessage(otherChannel,result,method);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}