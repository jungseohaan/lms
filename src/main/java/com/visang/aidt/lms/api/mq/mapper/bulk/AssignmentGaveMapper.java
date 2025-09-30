package com.visang.aidt.lms.api.mq.mapper.bulk;

import com.visang.aidt.lms.api.mq.dto.assignment.AssignmentGaveSaveDto;
import com.visang.aidt.lms.api.mq.dto.assignment.AssignmentRegistInfoDto;
import com.visang.aidt.lms.api.mq.dto.real.RealMqReqDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface AssignmentGaveMapper {
    List<AssignmentRegistInfoDto> findAssignmentRegistrationInfo(RealMqReqDto paramData);

    List<String> findCurriculumStandardIds(int taskId);

    void insertBulkTaskMqTrnLog(AssignmentGaveSaveDto param);

    int updateBulkTaskMqTrnLog();

    Map<String, String> getUserInfo(String userId);
}
