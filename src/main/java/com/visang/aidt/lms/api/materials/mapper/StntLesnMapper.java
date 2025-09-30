package com.visang.aidt.lms.api.materials.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface StntLesnMapper {
    Map<String,Object> getStntLesnProgRate(Map<String,Object> param) throws Exception;

    Map<String, Object>  checkLesnProgStd(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findLesnProgStdRate(Map<String, Object> paramData) throws Exception;

}
