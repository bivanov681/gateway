package com.currency.gateway.service;

import com.currency.gateway.dao.RateRedisDao;
import com.currency.gateway.dto.RateDataDTO;
import com.currency.gateway.enums.CurrencyCode;
import com.currency.gateway.model.Rate;
import com.currency.gateway.repository.RateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;


import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;

@Service
@EnableScheduling
public class FixerServiceImpl implements FixerService {

    @Value("${fixer.api.key}")
    private String apiKey;

    private final WebClient webClient;
    private final RateRepository rateRepository;
    private final RateRedisDao rateRedisDao;

    @Autowired
    public FixerServiceImpl(WebClient.Builder webClientBuilder, RateRepository rateRepository, RateRedisDao rateRedisDao) {
        this.webClient = webClientBuilder.build();
        this.rateRepository = rateRepository;
        this.rateRedisDao = rateRedisDao;
    }

    @Scheduled(fixedRateString = "${fixer.update.interval}")
    private void currencyGetRequest() {
        Arrays.stream(CurrencyCode.values())
                .forEach(currency -> currencyGetRequest(currency.toString()));
    }

    private void currencyGetRequest(String currencyCode) {
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("access_key", apiKey)
                        .queryParam("base", currencyCode)  // Assuming we're fetching USD rates. Adjust as needed.
                        .build())
                .retrieve()
                .bodyToMono(RateDataDTO.class)
                .subscribe(this::updateData);
    }

    private void updateData(RateDataDTO rateDataDTO) {
        Rate rate = Rate.builder()
                .baseCurrency(rateDataDTO.getBase())
                .rates(rateDataDTO.getRates())
                .rateDate(LocalDate.parse(rateDataDTO.getDate()))
                .rateTimestamp(Instant.ofEpochSecond(rateDataDTO.getTimestamp()).atZone(ZoneId.systemDefault()))
                .build();

        rateRedisDao.saveRate(rate);
        rateRepository.save(rate);
    };
}
