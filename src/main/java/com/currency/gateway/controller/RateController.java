package com.currency.gateway.controller;

import com.currency.gateway.dto.XmlCommandDTO;
import com.currency.gateway.dto.JsonBaseRequestDTO;
import com.currency.gateway.dto.JsonHistoricalRequestDTO;
import com.currency.gateway.model.Rate;
import com.currency.gateway.service.RateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RateController {

    private final RateService rateService;

    @Autowired
    public RateController(RateService rateService) {
        this.rateService = rateService;
    }

    @PostMapping("/json_api/current")
    public ResponseEntity<Rate> getLatestRate(@RequestBody JsonBaseRequestDTO request) {
        Rate rate = rateService.handleJsonCurrentRequest(request);
        return ResponseEntity.ok(rate);
    }

    @PostMapping("/json_api/history")
    public ResponseEntity<List<Rate>> getHistoryRate(@RequestBody JsonHistoricalRequestDTO requestDTO) {
         List<Rate> rates = rateService.handleJsonHistoricalRequest(requestDTO);
        return ResponseEntity.ok(rates);
    }

    @PostMapping("/xml_api/command")
    public ResponseEntity<?> getXmlData(@RequestBody XmlCommandDTO xmlCommandDto) {
        return xmlCommandDto.getGet() != null
                ? ResponseEntity.ok(rateService.handleXmlCurrentRequest(xmlCommandDto))
                : ResponseEntity.ok(rateService.handleXmlHistoricalRequest(xmlCommandDto));
    }
}