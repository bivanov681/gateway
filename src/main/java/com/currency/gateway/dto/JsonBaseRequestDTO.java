package com.currency.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JsonBaseRequestDTO {
    private String requestId;
    private Long timestamp;
    private String client;
    private String currency;
}
