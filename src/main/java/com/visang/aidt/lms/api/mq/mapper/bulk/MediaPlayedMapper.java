package com.visang.aidt.lms.api.mq.mapper.bulk;

import com.visang.aidt.lms.api.mq.dto.media.MediaDto;
import com.visang.aidt.lms.api.mq.dto.media.MediaLogResultDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MediaPlayedMapper {
    List<MediaDto> findStudentsFromMediaLog(@Param("startTime") String startTime,
                                            @Param("endTime") String endTime,
                                            @Param("userId") String userId);

    List<MediaLogResultDto> findMediaUsageHistory(@Param("startTime") String startTime,
                                                  @Param("endTime") String endTime,
                                                  @Param("userId") String userId);

    Integer modifyMediaPlayedUpdate(@Param("startTime") String startTime,
                                 @Param("endTime") String endTime);

    String getUserId(String userId);
}
