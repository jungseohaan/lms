package com.visang.aidt.lms.api.repository.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.repository.entity.TextbookEntity;
import com.visang.aidt.lms.api.repository.entity.TextbookTabEntity;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextbookTabDTO {
    /** ID */
    private Long id;

    private Long textbookIndexId;

    private TextbookCurriculumDTO textbookCurriculum;

    private String name;

    /** 사용여부 */
    private Boolean isActive;

    /** 공개여부(배공개인 경우 본인만 사용) */
    private Boolean isPublicOpen;

    private Integer accessLevel;

    private String source;

    private SetsDTO set;

    public static Map<String,Object> entityToMap(TextbookEntity entity) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper
                .convertValue(entity, new TypeReference<Map<String, Object>>() {});
        return map;
    }

    public static Map<String,Object> dtoToMap(TextbookTabDTO dto) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper
                .convertValue(dto, new TypeReference<Map<String, Object>>() {});
        return map;
    }

    public static TextbookTabDTO toDTO(TextbookTabEntity entity) {
        return TextbookTabDTO.builder()
                .id(entity.getId())
                .textbookIndexId(entity.getTextbookIndexId())
                .name(entity.getName())
                .isActive(entity.getIsActive())
                .isPublicOpen(entity.getIsPublicOpen())
                .build();
    }
}
