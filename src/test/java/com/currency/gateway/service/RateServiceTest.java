package com.currency.gateway.service;

import com.currency.gateway.dao.RateRedisDao;
import com.currency.gateway.dto.*;
import com.currency.gateway.exception.CurrencyNotFoundException;
import com.currency.gateway.exception.DuplicateRequestException;
import com.currency.gateway.model.Rate;
import com.currency.gateway.model.RequestLog;
import com.currency.gateway.repository.RateRepository;
import com.currency.gateway.repository.RequestLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;



@ExtendWith(MockitoExtension.class)
class RateServiceTest {

    @Mock
    private RequestLogRepository requestLogRepository;

    @Mock
    private RateRepository rateRepository;

    @Mock
    private RateRedisDao rateRedisDao;

    private RateServiceImpl rateService;

    @BeforeEach
    public void setup() {
        rateService = new RateServiceImpl(requestLogRepository, rateRepository, rateRedisDao);
    }

    @Test
    public void shouldThrowDuplicateExceptionForJsonCurrent() {
        JsonBaseRequestDTO requestDTO = new JsonBaseRequestDTO("123", Instant.now().toEpochMilli(), "testClient", "ALL");
        given(requestLogRepository.findByRequestId(any())).willReturn(Optional.of(RequestLog.builder().build()));
        assertThatThrownBy(() -> rateService.handleJsonCurrentRequest(requestDTO)).isInstanceOf(DuplicateRequestException.class);
    }

    @Test
    public void shouldThrowCurrencyNotFoundExceptionForJsonCurrent() {
        JsonBaseRequestDTO requestDTO = new JsonBaseRequestDTO("123", Instant.now().toEpochMilli(), "testClient", "TEST");
        assertThatThrownBy(() -> rateService.handleJsonCurrentRequest(requestDTO)).isInstanceOf(CurrencyNotFoundException.class);
    }

    @Test
    public void shouldThrowDuplicateExceptionForJsonHistory() {
        JsonHistoricalRequestDTO requestDTO = new JsonHistoricalRequestDTO("123", Instant.now().toEpochMilli(), "testClient", "ALL", 12);
        given(requestLogRepository.findByRequestId(any())).willReturn(Optional.of(RequestLog.builder().build()));
        assertThatThrownBy(() -> rateService.handleJsonHistoricalRequest(requestDTO)).isInstanceOf(DuplicateRequestException.class);
    }

    @Test
    public void shouldThrowCurrencyNotFoundExceptionForJsonHistory() {
        JsonHistoricalRequestDTO requestDTO = new JsonHistoricalRequestDTO("123", Instant.now().toEpochMilli(), "testClient", "TEST", 12);
        assertThatThrownBy(() -> rateService.handleJsonHistoricalRequest(requestDTO)).isInstanceOf(CurrencyNotFoundException.class);
    }

    @Test
    public void shouldThrowDuplicateExceptionForXmlCurrent() {
        XmlCommandDTO requestDTO = new XmlCommandDTO("123", new XmlCurrentDto(), new XmlHistoryDTO());
        given(requestLogRepository.findByRequestId(any())).willReturn(Optional.of(RequestLog.builder().build()));
        assertThatThrownBy(() -> rateService.handleXmlCurrentRequest(requestDTO)).isInstanceOf(DuplicateRequestException.class);
    }

    @Test
    public void shouldThrowDuplicateExceptionForXmlHistory() {
        XmlCommandDTO requestDTO = new XmlCommandDTO("123", new XmlCurrentDto(), new XmlHistoryDTO());
        given(requestLogRepository.findByRequestId(any())).willReturn(Optional.of(RequestLog.builder().build()));
        assertThatThrownBy(() -> rateService.handleXmlHistoricalRequest(requestDTO)).isInstanceOf(DuplicateRequestException.class);
    }

    @Test
    public void shouldThrowCurrencyNotFoundExceptionForXmlCurrent() {
        XmlCommandDTO requestDTO = new XmlCommandDTO("123", new XmlCurrentDto("", "TEST"), new XmlHistoryDTO());
        assertThatThrownBy(() -> rateService.handleXmlHistoricalRequest(requestDTO)).isInstanceOf(CurrencyNotFoundException.class);
    }

    @Test
    public void shouldThrowCurrencyNotFoundExceptionForXmlHistory() {
        XmlCommandDTO requestDTO = new XmlCommandDTO("123", new XmlCurrentDto(), new XmlHistoryDTO("","TEST", 123));
        assertThatThrownBy(() -> rateService.handleXmlHistoricalRequest(requestDTO)).isInstanceOf(CurrencyNotFoundException.class);
    }

    @Test
    public void shouldReturnRateValidJsonCurrent() {
        JsonBaseRequestDTO requestDTO = new JsonBaseRequestDTO("123", Instant.now().toEpochMilli(), "testClient", "ALL");
        Rate expectedRate = Rate.builder().baseCurrency("ALL").rateTimestamp( ZonedDateTime.now()).build();
        given(rateRedisDao.getRate(any())).willReturn(null);
        given(rateRepository.findCurrentRateByBaseCurrency(any())).willReturn(Optional.of(expectedRate));
        Rate actualRate = rateService.handleJsonCurrentRequest(requestDTO);
        assertThat(expectedRate.getRateTimestamp()).isEqualTo(actualRate.getRateTimestamp());
    }

    @Test
    public void shouldReturnRateValidJsonHistory() {
        JsonHistoricalRequestDTO requestDTO = new JsonHistoricalRequestDTO("123", Instant.now().toEpochMilli(), "testClient", "ALL", 12);
        Rate expectedRate = Rate.builder().baseCurrency("ALL").rateTimestamp( ZonedDateTime.now()).build();
        List<Rate> expectedRateList = List.of(expectedRate);
        given(rateRepository.findByBaseCurrencyAndRateTimestampBetween(any(), any(), any())).willReturn(expectedRateList);
        List<Rate> actualRateList = rateService.handleJsonHistoricalRequest(requestDTO);
        assertThat(expectedRateList.get(0).getRateTimestamp()).isEqualTo(actualRateList.get(0).getRateTimestamp());
    }

    @Test
    public void shouldReturnRateValidXmlCurrent() {
        XmlCommandDTO requestDTO = new XmlCommandDTO("123", new XmlCurrentDto("", "ALL"), new XmlHistoryDTO());
        Rate expectedRate = Rate.builder().baseCurrency("ALL").rateTimestamp( ZonedDateTime.now()).build();
        given(rateRedisDao.getRate(any())).willReturn(null);
        given(rateRepository.findCurrentRateByBaseCurrency(any())).willReturn(Optional.of(expectedRate));
        Rate actualRate = rateService.handleXmlCurrentRequest(requestDTO);
        assertThat(expectedRate.getRateTimestamp()).isEqualTo(actualRate.getRateTimestamp());
    }

    @Test
    public void shouldReturnRateValidXmlHistory() {
        XmlCommandDTO requestDTO = new XmlCommandDTO("123", new XmlCurrentDto(), new XmlHistoryDTO("","ALL", 123));
        Rate expectedRate = Rate.builder().baseCurrency("ALL").rateTimestamp( ZonedDateTime.now()).build();
        List<Rate> expectedRateList = List.of(expectedRate);
        given(rateRepository.findByBaseCurrencyAndRateTimestampBetween(any(), any(), any())).willReturn(expectedRateList);
        List<Rate> actualRateList = rateService.handleXmlHistoricalRequest(requestDTO);
        assertThat(expectedRateList.get(0).getRateTimestamp()).isEqualTo(actualRateList.get(0).getRateTimestamp());
    }




}