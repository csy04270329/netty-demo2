package com.example.demo;

import com.example.demo.domain.Account;
import com.example.demo.domain.repository.AccountsRepository;
import com.example.demo.netty.server.NettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DemoApplication {

    @Autowired
    NettyServer nettyServer;
    @Autowired
    private AccountsRepository accountRepository;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    public void run(String... strings) throws Exception{
        Account account=new Account();
        account.setAccountId("test1");
        account.setAccountName("테스트1");
        account.setPassword("123456");
        accountRepository.save(account);

        account=new Account();
        account.setAccountId("test2");
        account.setAccountName("테스트2");
        account.setPassword("123456");
        accountRepository.save(account);

        nettyServer.start();
    }
}
