package com.visang.aidt.lms.api.repository.dto;

import com.visang.aidt.lms.api.repository.entity.LibraryEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibraryDTO {
    /** ID */
    private Long id;
    private String name;
    private String description;
    /** 값 */
    private String type1;
    private String type2;
    private String url;
    private String thumbnail;
    private String usedIn;
    private Integer version;
    private Boolean isDeleted;
    /** 사용여부 */
    private Boolean isActive;
    /** 공개여부(배공개인 경우 본인만 사용) */
    private Boolean isPublicOpen;
    private Long curriBook;
    private Long curriUnit1;
    private Long curriUnit2;
    private Long curriUnit3;
    private Long curriUnit4;
    private Long curriUnit5;

    public static LibraryDTO toDTO(LibraryEntity entity) {
        return LibraryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .type1(entity.getType1())
                .type2(entity.getType2())
                .url(entity.getUrl())
                .thumbnail(entity.getThumbnail())
                .usedIn(entity.getUsedIn())
                .isDeleted(entity.getIsDeleted())
                .isActive(entity.getIsActive())
                .isPublicOpen(entity.getIsPublicOpen())
                .curriBook(entity.getCurriBook())
                .curriUnit1(entity.getCurriUnit1())
                .curriUnit2(entity.getCurriUnit2())
                .curriUnit3(entity.getCurriUnit3())
                .curriUnit4(entity.getCurriUnit4())
                .curriUnit5(entity.getCurriUnit5())
                .build();
    }
}
