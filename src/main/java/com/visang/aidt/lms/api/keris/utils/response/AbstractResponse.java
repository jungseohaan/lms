package com.visang.aidt.lms.api.keris.utils.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

@Data
@EqualsAndHashCode(callSuper=false)
public class AbstractResponse {

    public String code;
    public String message;
    public Map<String, Object> data;
    public Throwable throwable;

}