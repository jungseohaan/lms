package com.visang.aidt.lms.api.mq.mapper.bulk;

import com.visang.aidt.lms.api.mq.dto.teaching.ReorganizedInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TeachingReorganizedMapper {

    List<ReorganizedInfo> findReorganizedInfoList(ReorganizedInfo reorganizedInfo);
    Map<String, String> getUserInfo(String userId);

}
