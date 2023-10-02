package com.currency.gateway.dao;

import com.currency.gateway.model.Rate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class RateRedisDaoImpl implements RateRedisDao {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void saveRate(Rate rate) {
        try {
            String valueAsString = objectMapper.writeValueAsString(rate);
            redisTemplate.opsForValue().set(rate.getBaseCurrency(), valueAsString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Rate getRate(String currencyCode) {
        try {
            Rate rate = objectMapper.readValue(redisTemplate.opsForValue().get(currencyCode).toString(), Rate.class);
            return rate;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
