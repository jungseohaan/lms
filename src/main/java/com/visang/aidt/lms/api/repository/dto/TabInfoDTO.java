package com.visang.aidt.lms.api.repository.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.visang.aidt.lms.api.repository.entity.TabInfoEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TabInfoDTO {
    private Long stdCntsMapId;
    private Long cntsType;
    private String cntsExt;
    private String url;

    /** ID */
    private Long id;

    // 탭 ID (요청 파라미터 처리용으로 추가)
/*  private Long tabId; */

    // 교사 ID
    private String userId;

    private String wrterId;

    // 학급 ID
    private String claId;

    // 교과서 ID
    // textbkId
    private Long textbkId;

    // 커리큘럼 ID
    private Long crculId;

    // 커리큘럼 정보
    private TcCurriculumDTO tcCurriculum;

    // 탭명
    // tabNm
    private String tabNm;

    // 탭순서
    private int tabSeq;

    // 셋트지 ID
    private String setsId;

    // 셋트지 명
    private String setsNm;

    // 셋트지 유형(코드)
    private String setsCode;

    // 셋트지 유형(명)
    private String setsVal;

    // 문항 갯수
    private int mdulCnt;

    // 사용여부(Y/N)
    private String useAt;

    // 노출여부(Y/N)
    private String exposAt;

    private String tabAddAt;

    private String aiCstmzdStdCrtAt;

    private String categoryCd;

    private String categoryNm;

    private String aiCstmzdStdMthdSeCd;

    private String aiCstmzdStdMthdSeNm;

    private String editable;

    // e북 textbook ID
    private Integer ebkId;

    // e북 사용여부 (Y/N)
    private String ebkUseAt;

    // PDF Url
    private String pdfUrl;

    // PDF 시작 페이지
    private Integer startPage;

    // PDF 종료 페이지
    private Integer endPage;

    // 테마설정코드
    private String thmCd;

    // 테마설정경로
    private String thmPath;

    // 테마설명
    private String thmDesc;

    // 출제방법
    private Integer eamMth;

    // 출제방법(명)
    private String eamMthNm;

    // 학습자료정보 ID
    // - 교사가 추가한 탭의 경우에만 값이 존재함.
    private Integer stdId;

    // 셋트지 난이도
    private String difyNm;

    // 셋트지 썸네일
    private String thumbnail;

    // 모듈유형정보
    private List<LinkedHashMap<Object,Object>> articleTypeList;

    private String rgtr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime regDt;

    private String mdfr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime mdfyDt;

    private SetsDTO set;

    public static TabInfoDTO mapToDto(Map<String,Object> data) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        TabInfoDTO dto = objectMapper
                .convertValue(data, TabInfoDTO.class);
        return dto;
    }

    public static Map<String,Object> entityToMap(TabInfoEntity entity) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper
                .convertValue(entity, new TypeReference<Map<String, Object>>() {});
        return map;
    }

    public static Map<String,Object> dtoToMap(TabInfoDTO dto) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = objectMapper
                .convertValue(dto, new TypeReference<Map<String, Object>>() {});
        return map;
    }

    public static TabInfoDTO toDTO(TabInfoEntity entity) {
        return TabInfoDTO.builder()
                .id(entity.getId())
                .wrterId(entity.getWrterId())
                .claId(entity.getClaId())
                .textbkId(entity.getTextbkId())
                .crculId(entity.getCrculId())
                .tabNm(entity.getTabNm())
                .tabSeq(entity.getTabSeq())
                .useAt(entity.getUseAt())
                .exposAt(entity.getExposAt())
                .tabAddAt(entity.getTabAddAt())
                .build();
    }
}
