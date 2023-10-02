package com.currency.gateway.repository;

import com.currency.gateway.model.Rate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {

    @Query(value = "SELECT * FROM rate_table WHERE currency_code = :baseCurrency ORDER BY rate_timestamp DESC LIMIT 1;", nativeQuery = true)
    Optional<Rate> findCurrentRateByBaseCurrency(@Param("baseCurrency") String baseCurrency);

    List<Rate> findByBaseCurrencyAndRateTimestampBetween(String baseCurrency, ZonedDateTime start, ZonedDateTime end);
}
