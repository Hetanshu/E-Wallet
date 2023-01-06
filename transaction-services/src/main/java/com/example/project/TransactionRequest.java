package com.example.project;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    private String fromUser;
    private String toUser;
    private int amount;
}
