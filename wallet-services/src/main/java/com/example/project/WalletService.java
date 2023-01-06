package com.example.project;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletService {

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @KafkaListener(topics = {"create_wallet"},groupId = "avengers")
    public void createWallet(String message) throws JsonProcessingException{

        JSONObject jsonObject=objectMapper.readValue(message,JSONObject.class);
        String userName= (String) jsonObject.get("userName");
        Wallet wallet=Wallet.builder()
                .userName(userName)
                .balance(2000).build();
        walletRepository.save(wallet);
    }

    @KafkaListener(topics = {"create_transaction"},groupId = "avengers")
    public void updateWallet(String message) throws JsonProcessingException{
        JSONObject jsonObject=objectMapper.readValue(message,JSONObject.class);
        String fromUser= (String) jsonObject.get("fromUser");
        String toUser= (String) jsonObject.get("toUser");
        int amount= (int) jsonObject.get("amount");
        String transactionId= (String) jsonObject.get("transactionId");

        Wallet sender=walletRepository.findByUserName(fromUser);
        int balance= sender.getBalance();

        JSONObject transactionObject=new JSONObject();
        if(balance>=amount){
            // can withdraw
            sender.setBalance(balance-amount);
            walletRepository.save(sender);

            Wallet receiver=walletRepository.findByUserName("toUser");
            receiver.setBalance(receiver.getBalance()+amount);
            walletRepository.save(receiver);

            transactionObject.put("status","SUCCESS");
            transactionObject.put("transactionId",transactionId);
        }else {
            transactionObject.put("status","FAILED");
            transactionObject.put("transactionId",transactionId);
        }

        String ack=transactionObject.toString();
        kafkaTemplate.send("update_transaction",ack);
    }
}
