package com.visang.aidt.lms.api.mq.mapper.bulk;

import com.visang.aidt.lms.api.mq.dto.assignment.AssignmentFinishedInfoDto;
import com.visang.aidt.lms.api.mq.dto.assignment.AssignmentFinishedSaveDto;
import com.visang.aidt.lms.api.mq.dto.real.RealMqReqDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AssignmentFinishedMapper {
    void insertAssignmentSubmissionInfo(AssignmentFinishedSaveDto param);

    List<AssignmentFinishedInfoDto> findAssignmentSubmissionInfo(RealMqReqDto paramData);

    int updateAssignmentFinishedSendAt();

    Map<String, String> getUserInfo(String userId);
}
