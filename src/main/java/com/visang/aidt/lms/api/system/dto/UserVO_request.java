package com.visang.aidt.lms.api.system.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = "password")
public class UserVO_request extends baseVO_request{

	public Long id;

    private Long creator_id;
    private String creator;
    private String creator_name;



    private String regdate;
    private String updatedate;

    private Boolean is_active;


    //private int id;
    private String uid;
    private String password;
    private String name;
    private String email;
    private String team;
   // private String regdate;
   // private Boolean is_active;

//    private String creator;
//    //private int creator_id;
//    private String creator_name;

    private String updater;
    private int updater_id;
    private String updater_name;
    //private String updatedate;

    private String authgroup;


    private String role;
    private String token;



	private Long full_count;
}
