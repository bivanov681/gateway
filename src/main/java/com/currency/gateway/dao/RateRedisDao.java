package com.currency.gateway.dao;

import com.currency.gateway.model.Rate;


public interface RateRedisDao {
    void saveRate(Rate rate);
    Rate getRate(String currencyCode);
}
