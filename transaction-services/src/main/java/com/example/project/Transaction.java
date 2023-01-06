package com.example.project;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String transactionId= UUID.randomUUID().toString();
    private String fromUser;
    private String toUser;
    private int amount;
    private TransactionStatus status;
    private String transactionTime;
}
