package com.visang.aidt.lms.global.vo;

import com.visang.aidt.lms.api.keris.utils.response.AbstractResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResponseVO extends AbstractResponse {

	//public Exception e;
    public String errorMessage;
    public Object vo;
}
