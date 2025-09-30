package com.visang.aidt.lms.api.assessment.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface StntReportMapper {

    Map<String, Object> findStntReportLastActivity(Map<String, Object> paramData);
}
