package com.visang.aidt.lms.api.stress;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.repository.dto.TabInfoDTO;
import com.visang.aidt.lms.api.repository.entity.TcCurriculumEntity;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TcCurriculumDTO2 {
    /** ID */
    private Long id;

    // 교사 ID
    private String userId;

    // 교사 ID
    private String wrterId;

    // 학급 ID
    private String claId;

    // 교과서 ID
    private Long textbkId;

    // 목차 ID
    private Long textbkIdxId;

    // 순서
    private Integer order;

    // 커리큘럼 key
    private Long key;

    // 커리큘럼 parent key
    private Long parent;

    private Integer depth;

    // PDF 페이지 번호
    private Integer startPage;

    private Integer endPage;

    // 내용
    private String text;

    // 사용여부
    private String useAt;

    // 삭제여부
    private String delAt;

    // 컨텐츠 등록 가능 여부
    // - 하위에 tab 존재하는 경우
    private String addconAt;

    private Long curriUnit1;

    private Long curriUnit2;

    private Long curriUnit3;

    private Long curriUnit4;

    private Long curriUnit5;

    @Builder.Default
    private Boolean lastPosition = false; // 마지막 수업위치 여부

    private String rgtr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date regDt;

    private String mdfr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date mdfyDt;

    // 커리큘럼 key
    private Long crculId;

    private Long ebkId;

    private String pdfUrl;

    private List<TabInfoDTO> textbookTabList;

    public String getUserId() {
        if(StringUtils.isEmpty(this.userId) && !StringUtils.isEmpty(this.wrterId)) {
            return this.wrterId;
        }
        return this.userId;
    }

    public void setWrterId(String wrterId) {
        if(StringUtils.isEmpty(this.wrterId) && !StringUtils.isEmpty(this.userId)) {
            this.wrterId = this.userId;
        }

        this.wrterId = wrterId;
    }

    public void setCrculId(Long crculId) {
        if(ObjectUtils.isEmpty(this.crculId) && !ObjectUtils.isEmpty(this.key)) {
            this.crculId = this.key;
        }

        this.crculId = crculId;
    }

    public void addTextbookTab(TabInfoDTO textbookTab) {
        if(CollectionUtils.isEmpty(textbookTabList)) {
            this.textbookTabList = new ArrayList<>();
        }

        this.textbookTabList.add(textbookTab);
    }

    public static TcCurriculumDTO2 mapToDto(Map<String,Object> data) {
        ObjectMapper objectMapper = new ObjectMapper();
        TcCurriculumDTO2 dto = objectMapper
                .convertValue(data, TcCurriculumDTO2.class);
        return dto;
    }

    public static TcCurriculumEntity2 mapToEntity(Map<String,Object> data) {
        ObjectMapper objectMapper = new ObjectMapper();
        TcCurriculumEntity2 entity = objectMapper
                .convertValue(data, TcCurriculumEntity2.class);
        return entity;
    }

    public static TcCurriculumDTO2 toDTO(TcCurriculumEntity2 entity) {
        return TcCurriculumDTO2.builder()
                .id(entity.getId())
                .wrterId(entity.getWrterId())
                .claId(entity.getClaId())
                .parent(entity.getParent())
                .textbkId(entity.getTextbkId())
                .textbkIdxId(entity.getTextbkIdxId())
                .key(entity.getKey())
                .order(entity.getOrder())
                .depth(entity.getDepth())
                .startPage(entity.getStartPage())
                .endPage(entity.getEndPage())
                .text(entity.getText())
                .useAt(entity.getUseAt())
                .delAt(entity.getDelAt())
                .addconAt(entity.getAddconAt())
                .curriUnit1(entity.getCurriUnit1())
                .curriUnit2(entity.getCurriUnit2())
                .curriUnit3(entity.getCurriUnit3())
                .curriUnit4(entity.getCurriUnit4())
                .curriUnit5(entity.getCurriUnit5())
                .regDt(entity.getRegDt())
                .rgtr(entity.getRgtr())
                .mdfyDt(entity.getMdfyDt())
                .mdfr(entity.getMdfr())
                .build();
    }
}
