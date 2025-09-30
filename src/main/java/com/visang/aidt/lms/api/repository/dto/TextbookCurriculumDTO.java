package com.visang.aidt.lms.api.repository.dto;

import com.visang.aidt.lms.api.repository.entity.TextbookCurriculumEntity;
import lombok.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextbookCurriculumDTO {
    /** ID */
    private Long id;

    private Integer key;

    private Long parent;

    private Long textbookIndexId;

    private Integer order;

    private Integer depth;

    private Integer page;

    private String text;

    private Boolean isActive;

    private Long curriUnit1;
    private Long curriUnit2;
    private Long curriUnit3;
    private Long curriUnit4;
    private Long curriUnit5;

    @Builder.Default
    private Boolean lastPosition = false; // 마지막 수업위치 여부

    private Long crculId;

    public Integer getCrculId() {
        return this.key;
    }

    private List<TextbookTabDTO> textbookTabList;

    public void addTextbookTab(TextbookTabDTO textbookTab) {
        if(CollectionUtils.isEmpty(textbookTabList)) {
            this.textbookTabList = new ArrayList<>();
        }

        this.textbookTabList.add(textbookTab);
    }

    public static TextbookCurriculumDTO toDTO(TextbookCurriculumEntity entity) {
        return TextbookCurriculumDTO.builder()
                .id(entity.getId())
                .parent(entity.getParent())
                .textbookIndexId(entity.getTextbookIndexId())
                .order(entity.getOrder())
                .depth(entity.getDepth())
                .page(entity.getPage())
                .text(entity.getText())
                .isActive(entity.getIsActive())
                .curriUnit1(entity.getCurriUnit1())
                .curriUnit2(entity.getCurriUnit2())
                .curriUnit3(entity.getCurriUnit3())
                .curriUnit4(entity.getCurriUnit4())
                .curriUnit5(entity.getCurriUnit5())
                .build();
    }
}
