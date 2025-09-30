package com.visang.aidt.lms.api.member.dto;

import com.visang.aidt.lms.api.system.dto.RoleVO;
import com.visang.aidt.lms.api.system.dto.UserVO;

import java.util.List;

public class LoginVO extends UserVO {
//    private Long id;
//    private String uid;
//    private String password;
//    private String name;
//    private String email;
//    private String team;
//    private String regdate;
//    private Boolean is_active;

    //    private String role;
//    private String token;
    private String tokenExpireDate;

    //private List<Long> arrayOfBrand;
    //private List<Map<String, Object>> brandRole;

    //private Object[] arrayOfBrand;
    private List<RoleVO> brandRole ;
}
