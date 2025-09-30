package com.visang.aidt.lms.api.system.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class RoleVO {

    public Long authgroup_id;
    public String authgroup_code;
    public String authgroup_name;
    public String authgroup_auth;
    public Long brand_id;
    public String brand_code;
    public String brand_name;

}
