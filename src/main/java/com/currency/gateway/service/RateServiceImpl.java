package com.currency.gateway.service;

import com.currency.gateway.dao.RateRedisDao;
import com.currency.gateway.dto.JsonBaseRequestDTO;
import com.currency.gateway.dto.JsonHistoricalRequestDTO;
import com.currency.gateway.dto.XmlCommandDTO;
import com.currency.gateway.enums.CurrencyCode;
import com.currency.gateway.exception.CurrencyNotFoundException;
import com.currency.gateway.exception.DuplicateRequestException;
import com.currency.gateway.model.Rate;
import com.currency.gateway.model.RequestLog;
import com.currency.gateway.repository.RateRepository;
import com.currency.gateway.repository.RequestLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class RateServiceImpl implements RateService{

    public static final String EXT_SERVICE_1 = "EXT_SERVICE_1";
    public static final String EXT_SERVICE_2 = "EXT_SERVICE_2";
    private final RequestLogRepository requestLogRepository;
    private final RateRepository rateRepository;
    private final RateRedisDao rateRedisDao;

    @Autowired
    public RateServiceImpl(RequestLogRepository requestLogRepository, RateRepository rateRepository, RateRedisDao rateRedisDao) {
        this.requestLogRepository = requestLogRepository;
        this.rateRepository = rateRepository;
        this.rateRedisDao = rateRedisDao;
    }

    @Override
    public Rate handleJsonCurrentRequest(JsonBaseRequestDTO requestDTO) throws DuplicateRequestException {
        checkForExceptions(requestDTO.getRequestId(),requestDTO.getCurrency());
        ZonedDateTime currentRequestTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(requestDTO.getTimestamp()), ZoneId.systemDefault());
        saveRequestLog(EXT_SERVICE_1, requestDTO.getRequestId(),currentRequestTimestamp, requestDTO.getClient());
        return getLatestCurrencyRate(requestDTO.getCurrency());
    }

    @Override
    public List<Rate> handleJsonHistoricalRequest(JsonHistoricalRequestDTO requestDTO) {
        checkForExceptions(requestDTO.getRequestId(),requestDTO.getCurrency());
        ZonedDateTime currentRequestTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(requestDTO.getTimestamp()), ZoneId.systemDefault());
        saveRequestLog(EXT_SERVICE_1, requestDTO.getRequestId(),currentRequestTimestamp, requestDTO.getClient());
        return getHistoricalRate(requestDTO.getPeriod(), requestDTO.getCurrency());
      }

      @Override
    public Rate handleXmlCurrentRequest(XmlCommandDTO requestDTO) throws DuplicateRequestException {
        checkForExceptions(requestDTO.getId(),requestDTO.getGet().getCurrency());
        saveRequestLog(EXT_SERVICE_2, requestDTO.getId(), ZonedDateTime.now(), requestDTO.getGet() .getConsumer());
        return getLatestCurrencyRate(requestDTO.getGet().getCurrency());
    }

    @Override
    public List<Rate> handleXmlHistoricalRequest(XmlCommandDTO requestDTO) {
        checkForExceptions(requestDTO.getId(),requestDTO.getHistory().getCurrency());
        saveRequestLog(EXT_SERVICE_2, requestDTO.getId(), ZonedDateTime.now(), requestDTO.getHistory().getConsumer());
        return getHistoricalRate(requestDTO.getHistory().getPeriod(), requestDTO.getHistory().getCurrency());
    }

    private void checkForExceptions(String id, String currency) {
        if (requestLogRepository.findByRequestId(id).isPresent()) {
            throw new DuplicateRequestException("Duplicate request!");
        }

        if (isNotValidCurrencyCode(currency)) {
            throw new CurrencyNotFoundException("Currency not found!");
        }
    }

    private boolean isNotValidCurrencyCode(String currencyCode) {
        return !Arrays.stream(CurrencyCode.values())
                .anyMatch(e -> e.toString().equals(currencyCode));
    }

    private void saveRequestLog(String serviceName, String requestId, ZonedDateTime timestamp, String clientId) {
        RequestLog requestLog = RequestLog.builder()
                .serviceName(serviceName)
                .requestId(requestId)
                .timestamp(timestamp)
                .endClientId(clientId)
                .build();
        requestLogRepository.save(requestLog);
    }

    private Rate getLatestCurrencyRate(String currencyCode) {
        Rate rate = rateRedisDao.getRate(currencyCode);
        if (rate == null) {
            Optional<Rate> optionalRate = rateRepository.findCurrentRateByBaseCurrency(currencyCode);
            rate = optionalRate.get();
        }
        return rate;
    }

    private List<Rate> getHistoricalRate(Integer period, String currencyCode) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime startDateTime = now.minusHours(period);
        List<Rate> rates = rateRepository.findByBaseCurrencyAndRateTimestampBetween(currencyCode, startDateTime, now);
        return rates;
    }
}
