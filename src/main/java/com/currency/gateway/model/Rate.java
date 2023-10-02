package com.currency.gateway.model;

import com.currency.gateway.converter.JsonMapConverter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;


import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "RATE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Rate implements Serializable {
    @Id
    @SequenceGenerator(
            name = "rate_sequence",
            sequenceName =  "rate_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "rate_sequence"
    )
    private Long id;
    @Column(name = "base_currency", nullable = false)
    private String baseCurrency;
    @Convert(converter = JsonMapConverter.class)
    @Column(name = "rates", columnDefinition = "TEXT")
    private Map<String, Double> rates;
    @Column(name = "rate_date", nullable = false)
    private LocalDate rateDate;

    @Column(name = "rate_timestamp", nullable = false)
    private ZonedDateTime rateTimestamp;

//    public static void main(String[] args) {
//        long timestamp = 1696107423L;
//
//        Instant instant = Instant.ofEpochSecond(timestamp);
//        ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
//
//        System.out.println(zonedDateTime);
//    }
}
