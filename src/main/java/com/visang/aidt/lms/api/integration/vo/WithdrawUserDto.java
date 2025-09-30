package com.visang.aidt.lms.api.integration.vo;

import lombok.Data;

@Data
public class WithdrawUserDto {

    public String userId;
    public String userSeCd;
    public String platform;
    public String integUserId;
    public String withdrawDt;
    public String syncAt;
    public String syncDt;
    public String rgtr;
}
