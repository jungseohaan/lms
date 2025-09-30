package com.visang.aidt.lms.api.utility.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.support.JdbcUtils;

import java.util.LinkedHashMap;

public class CamelHashMap extends LinkedHashMap<String,Object> {
    private static final long serialVersionUID = 1L;

    public Object put(String key, Object value) {

        // '_' 가 포함되지 않은 경우에는 그냥 그대로 반환
        if (!StringUtils.contains(key, "_")) {
            return super.put(key, value);
        }
        return super.put(JdbcUtils.convertUnderscoreNameToPropertyName(key), value);
    }
}
