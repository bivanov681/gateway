package com.currency.gateway.repository;

import com.currency.gateway.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestLogRepository extends JpaRepository<RequestLog, Long> {
    Optional<RequestLog> findByRequestId(String requestId);
}
