package com.example.project;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WalletRequest {

    private String userName;
    private int amount;
}
