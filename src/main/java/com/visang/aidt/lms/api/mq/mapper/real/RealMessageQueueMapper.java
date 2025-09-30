package com.visang.aidt.lms.api.mq.mapper.real;

import com.visang.aidt.lms.api.mq.dto.real.LearningProgressDto;
import com.visang.aidt.lms.api.mq.dto.real.LearningProgressVO;
import com.visang.aidt.lms.api.mq.dto.real.RealClassStartDto;
import com.visang.aidt.lms.api.mq.dto.real.RealMqReqDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RealMessageQueueMapper {
    RealClassStartDto findClassStatusByClaIdx(RealMqReqDto paramData);
    List<LearningProgressDto> findLearningProgressList(LearningProgressVO paramData);
    List<String> selectTcLastlessonInfo(RealMqReqDto paramData);
}
