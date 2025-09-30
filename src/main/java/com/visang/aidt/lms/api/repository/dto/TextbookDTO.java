package com.visang.aidt.lms.api.repository.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.repository.entity.TextbookEntity;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextbookDTO {
    private Long id;

    private Long textbookIndexId;

    private String type;

    private String name;

    private String pdfUrl;

    private Boolean isActive;

    public static Map<String,Object> entityToMap(TextbookEntity entity) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper
                .convertValue(entity, new TypeReference<Map<String, Object>>() {});
        return map;
    }

    public static TextbookDTO toDTO(TextbookEntity entity) {
        return TextbookDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .pdfUrl(entity.getPdfUrl())
                .textbookIndexId(entity.getTextbookIndexId())
                .build();
    }


}
