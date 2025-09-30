package com.visang.aidt.lms.api.mq.mapper.bulk;

import com.visang.aidt.lms.api.mq.dto.teaching.ReorganizedInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StdLessonReconMapper {
    void seveStdMqTrnLog(int tabId);

    void updateStdMqTrnLog(ReorganizedInfo reorganizedInfo);
}
