package com.visang.aidt.lms.api.utility.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
class HmacKeyLoader {

    @Value("${hmac.secret.key}")
    private String secretKey;

    @PostConstruct
    public void init() {
        CompressUtil.setSecretKey(secretKey);
    }
}