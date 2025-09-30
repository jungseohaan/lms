package com.visang.aidt.lms.api.repository.dto;

import com.visang.aidt.lms.api.repository.entity.LibtextEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LibtextDTO {
    /** ID */
    private Long id;
    private String name;
    private String description;

    private String contentsEntry;
    private String contentsMultiLang;
    private String contentsDef;
    private String contentsPron;
    private String contentsImage;
    private String contentsAudio;
    private String contentsVideo;

    /** 값 */
    private String type1;
    private String type2;

    // 신규 추가 컬럼 시작
    private String parts;
    private String chunk1;
    private String chunk2;
    private String info1;
    private String info2;
    private String keyDesc;
    private String keyInfo;
    private String keyTitle;
    private String keyType;
    private String speaker;
    // 신규 추가 컬럼 끝

    private Integer version;
    /** 사용여부 */
    private Boolean isActive;
    private Long curriBook;
    private Long curriUnit1;
    private Long curriUnit2;
    private Long curriUnit3;
    private Long curriUnit4;
    private Long curriUnit5;

    public static LibtextDTO toDTO(LibtextEntity entity) {
        return LibtextDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .contentsEntry(entity.getContentsEntry())
                .contentsMultiLang(entity.getContentsMultiLang())
                .contentsDef(entity.getContentsDef())
                .contentsPron(entity.getContentsPron())
                .contentsImage(entity.getContentsImage())
                .contentsAudio(entity.getContentsAudio())
                .contentsVideo(entity.getContentsVideo())
                .type1(entity.getType1())
                .type2(entity.getType2())
                .parts(entity.getParts())
                .chunk1(entity.getChunk1())
                .chunk2(entity.getChunk2())
                .info1(entity.getInfo1())
                .info2(entity.getInfo2())
                .keyDesc(entity.getKeyDesc())
                .keyInfo(entity.getKeyInfo())
                .keyTitle(entity.getKeyTitle())
                .keyType(entity.getKeyType())
                .speaker(entity.getSpeaker())
                .isActive(entity.getIsActive())
                .curriBook(entity.getCurriBook())
                .curriUnit1(entity.getCurriUnit1())
                .curriUnit2(entity.getCurriUnit2())
                .curriUnit3(entity.getCurriUnit3())
                .curriUnit4(entity.getCurriUnit4())
                .curriUnit5(entity.getCurriUnit5())
                .build();
    }
}
