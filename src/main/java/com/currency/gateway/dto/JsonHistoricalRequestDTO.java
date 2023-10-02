package com.currency.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Parent;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class JsonHistoricalRequestDTO extends JsonBaseRequestDTO {
    private int period;

    public JsonHistoricalRequestDTO( String requestId, Long timestamp, String client,String currency, int period) {
        super(requestId, timestamp, client, currency);
        this.period = period;
    }
}
