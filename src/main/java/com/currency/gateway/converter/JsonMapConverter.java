package com.currency.gateway.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Map;

@Converter
public class JsonMapConverter implements AttributeConverter<Map<String, Double>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Double> rates) {
        try {
            return objectMapper.writeValueAsString(rates);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting map to JSON string", e);
        }
    }

    @Override
    public Map<String, Double> convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON string to map", e);
        }
    }
}

