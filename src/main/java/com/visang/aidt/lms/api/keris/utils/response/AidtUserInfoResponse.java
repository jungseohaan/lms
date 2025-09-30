package com.visang.aidt.lms.api.keris.utils.response;

import lombok.Data;

import java.util.List;

@Data
public class AidtUserInfoResponse extends AbstractResponse {

    public String user_id;
    public String user_name;
    public String user_class;
    public String user_division;
    public String user_gender;
    public String user_grade;
    public String user_number;
    public String school_id;
    public String school_name;

    public List<AidtClassInfoVo> class_info;
    public List<AidtMemberInfoVo> member_info;
    public List<AidtScheduleInfoVo> schedule_info;
    public List<AidtLectureInfoVo> lecture_info;

}
