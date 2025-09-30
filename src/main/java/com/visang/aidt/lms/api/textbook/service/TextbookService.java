package com.visang.aidt.lms.api.textbook.service;

import com.visang.aidt.lms.api.textbook.mapper.CrcuMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class TextbookService {
    private final CrcuMapper crcuMapper;

    public List<Map<String, Object>> getTextbookCrcuList(Map<String, Object> paramData) throws Exception {
        return crcuMapper.findTextbookCrcuList(paramData);
    }

    /**
     * 메타(aidt_lcms.meta) 테이블에 저장되어 있는 교과과정 커리큘럼 목록 조회
     *
     * @param paramData
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTextbookCrcuListByMeta(Map<String, Object> paramData) throws Exception {
        /* 수학: 1, 영어: 3 */
        int brandId = this.getTextbookBrandId(paramData);
        paramData.put("brandId",brandId);

        String textbookType = "";

        int[] elementaryMathTextbooks = {1175, 1197, 1198, 1199, 7036, 7040, 7041, 7042};
        int[] elementaryEnglishTextBooks = {6981, 6982};

        long textbookId = Long.parseLong(paramData.get("textbookId").toString());

        // 초등 수학 교과서인지 확인
        boolean isElementaryMath = Arrays.stream(elementaryMathTextbooks)
                .anyMatch(id -> id == textbookId);

        // 초등 영어 교과서인지 확인
        boolean isElementaryEnglish = Arrays.stream(elementaryEnglishTextBooks)
                .anyMatch(id -> id == textbookId);

        if (isElementaryMath) {
            textbookType = "elementaryMath";
        } else if (isElementaryEnglish) {
            textbookType = "elementaryEnglish";
        }

        paramData.put("textbookType", textbookType);

        return crcuMapper.findTextbookCrcuListByMeta(paramData);
    }

    /**
     * 교과서의 브랜드 ID 조회
     *
     * @param param
     * @return
     * @throws Exception
     */
    @Transactional(readOnly = true)
    public int getTextbookBrandId(Map<String,Object> param) throws Exception {
        return crcuMapper.findTextbookBrandId(param);
    }
}
