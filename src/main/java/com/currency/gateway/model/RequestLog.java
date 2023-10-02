package com.currency.gateway.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestLog {
    @Id
    @SequenceGenerator(
            name = "request_log_sequence",
            sequenceName = "request_log_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "request_log_sequence"
    )
    private Long id;
    @Column(name = "service_name", nullable = false)
    private String serviceName;
    @Column(name = "request_id", nullable = false, unique = true)
    private String requestId;
    @Column(name = "timestamp", nullable = false)
    private ZonedDateTime timestamp;
    @Column(name = "end_client_id", nullable = false)
    private String endClientId;
}
