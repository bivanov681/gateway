package com.currency.gateway.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ErrorResponseDTO {
    private int status;
    private String error;
    private String message;
}
