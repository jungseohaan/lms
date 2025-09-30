package com.visang.aidt.lms.api.media.service;

import com.visang.aidt.lms.api.media.dto.MediaLogRequestDTO;
import com.visang.aidt.lms.api.media.mapper.StntMediaMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StntMediaService {

    StntMediaMapper stntMediaMapper;

    public Integer saveStudentMediaLearningLogResults(MediaLogRequestDTO paramData) throws Exception {
        String crculId = paramData.getCrculId();

        if (NumberUtils.toInt(crculId, 0) == 0) {
            return 0;
        }

        return stntMediaMapper.saveStudentMediaLearningLogResults(paramData);
    }


}
