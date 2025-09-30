package com.visang.aidt.lms.api.dashboard.model;

import lombok.Data;

@Data
public class VivaClassApiDto {

    public Boolean success;
    public String code;
    public String message;
    public String messageType;
    public String redirectURL;
    public Object response;
}
