package com.visang.aidt.lms.api.media.mapper;

import com.visang.aidt.lms.api.media.dto.MediaLogRequestDTO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StntMediaMapper {

    Integer saveStudentMediaLearningLogResults(MediaLogRequestDTO paramData) throws Exception;
}
