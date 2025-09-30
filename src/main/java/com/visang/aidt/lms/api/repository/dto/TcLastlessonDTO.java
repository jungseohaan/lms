package com.visang.aidt.lms.api.repository.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.repository.entity.TcLastlessonEntity;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * 마지막 수업위치 정보
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TcLastlessonDTO {
    private Long id;

    /** 교사 ID */
    private String userId;

    /** 교사 ID */
    private String wrterId;

    // 학급 ID
    private String claId;

    private Long tabId;
    // 교과서 ID
    private Long textbkId;

    // 목차 ID
    private Long textbkIdxId;

    // 커리큘럼 key
    private Long crculId;

    // 브랜드 ID
    private Long brand_id;

    private String rgtr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date regDt;

    private String mdfr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date mdfyDt;

    public String getUserId() {
        if(StringUtils.isEmpty(this.userId) && !StringUtils.isEmpty(this.wrterId)) {
            return this.wrterId;
        }
        return this.userId;
    }

    /*
    public void setUserId(String userId) {
        if(StringUtils.isEmpty(this.userId) && !StringUtils.isEmpty(this.wrterId)) {
            this.userId = this.wrterId;
        }

        this.userId = userId;
    }

    public String getWrterId() {
        if(StringUtils.isEmpty(this.wrterId) && !StringUtils.isEmpty(this.userId)) {
            return this.userId;
        }
        return this.wrterId;
    }*/

    public void setWrterId(String wrterId) {
        if(StringUtils.isEmpty(this.wrterId) && !StringUtils.isEmpty(this.userId)) {
            this.wrterId = this.userId;
        }

        this.wrterId = wrterId;
    }

    public static TcLastlessonDTO mapToDto(Map<String,Object> data) {
        ObjectMapper objectMapper = new ObjectMapper();
        TcLastlessonDTO dto = objectMapper
                .convertValue(data, TcLastlessonDTO.class);
        if(!data.containsKey("wrterId")) {
            dto.setWrterId(dto.getUserId());
        }
        return dto;
    }

    public static TcLastlessonEntity mapToEntity(Map<String,Object> data) {
        ObjectMapper objectMapper = new ObjectMapper();
        TcLastlessonEntity entity = objectMapper
                .convertValue(data, TcLastlessonEntity.class);
        if(!data.containsKey("wrterId")) {
            entity.setWrterId(entity.getUserId());
        }
        return entity;
    }

    public static Map<String,Object> entityToMap(TcLastlessonEntity entity) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper
                .convertValue(entity, new TypeReference<Map<String, Object>>() {});
        return map;
    }

    public static TcLastlessonDTO toDTO(TcLastlessonEntity entity) {
        return TcLastlessonDTO.builder()
                .id(entity.getId())
                .wrterId(entity.getWrterId())
                .claId(entity.getClaId())
                .tabId(entity.getTabId())
                .textbkId(entity.getTextbkId())
                .textbkIdxId(entity.getTextbkIdxId())
                .crculId(entity.getCrculId())
                .regDt(entity.getRegDt())
                .rgtr(entity.getRgtr())
                .mdfyDt(entity.getMdfyDt())
                .mdfr(entity.getMdfr())
                .build();
    }
}
