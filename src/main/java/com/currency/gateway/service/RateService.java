package com.currency.gateway.service;

import com.currency.gateway.dto.JsonBaseRequestDTO;
import com.currency.gateway.dto.JsonHistoricalRequestDTO;
import com.currency.gateway.dto.XmlCommandDTO;
import com.currency.gateway.model.Rate;

import java.util.List;

public interface RateService {
    Rate handleJsonCurrentRequest(JsonBaseRequestDTO requestDTO);
    List<Rate> handleJsonHistoricalRequest(JsonHistoricalRequestDTO requestDTO);
    Rate handleXmlCurrentRequest(XmlCommandDTO requestDTO);
    List<Rate> handleXmlHistoricalRequest(XmlCommandDTO requestDTO);
}
