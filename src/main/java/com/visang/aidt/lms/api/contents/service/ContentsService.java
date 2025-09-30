package com.visang.aidt.lms.api.contents.service;

import com.visang.aidt.lms.api.contents.dto.*;
import com.visang.aidt.lms.api.contents.repository.*;
import com.visang.aidt.lms.api.system.service.SystemService;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class ContentsService {

    private SetsRepositoryX setsRepository;
    private KeywordRepository keywordRepository;

    @Autowired
    private SetSummaryRepository setSummaryRepository;

    @Autowired
    private SystemService systemService;

    /**
     * 메소드명을 setForSave -> saveSetInfo 로 변경(트랜젝션 처리 때문)
     *
     * @param vo
     * @return
     */
    public String saveSetInfo(SetsSaveRequestVO vo) throws Exception {

        // NCP일 경우 lms user를 조회하여 저작자로 등록하는 프로세스를 수행한다
        SetsSaveVO saveVO = vo.getSets();
        if (saveVO != null) {
            systemService.addLmsUser(saveVO.getCreator_id(), saveVO.getCreator(), saveVO.getLoginUserId());
        }

        if (vo.getSets() == null) return "";

        String targetId = "";

        if (vo.saveTypeEqualInsert) {
            vo.getSets().setVersion(1L);

            // 세트지 생성전 신규id 가져옴
            String newSetsId = setsRepository.getNewSetsId();
            vo.getSets().setId(newSetsId);

            int insertCnt = setsRepository.insertSets(vo.getSets()); // addSets -> insertSets 로 명칭 변경(동일한 메소드명 존재)
            if (insertCnt == 0) {
                return "0";
            }
            targetId = vo.getSets().getId();
        } else {
            targetId = vo.getSets().getId();
            if (vo.getIs_temp() != null && vo.getIs_temp() == true) {
                setsRepository.updateTempSets(vo.getSets());
                return targetId; //임시저장이므로 연관테이블은 변경하지 않음.
            }
            if (setsRepository.updateSets(vo.getSets()) <= 0) {
                return "";
            }
        }
        final String setsId = targetId;
        {
            //keyword
            List<KeywordMapVO> keywordId = new ArrayList<KeywordMapVO>();
            if (vo.getSets().getHashTag() != null) {
                keywordRepository.deleteSetsKeywordMap(targetId); //keyword

                for (String keyword : vo.getSets().getHashTag()) {
                    if (StringUtils.isEmpty(keyword)) {
                        continue;
                    }
                    Long kId = keywordRepository.getKeywordId(keyword);
                    if (kId == null || kId == 0) {
                        KeywordVO keywordVO = new KeywordVO();
                        keywordVO.setVal(keyword);
                        try { // 쿼리에서 not exists가 아닌 테이블의 unique 정합성 처리로 구현
                            keywordRepository.insertKeywordCms(keywordVO); // visang cms 소스 가져오면서 중복된 메소드명에 Cms 붙임
                            kId = keywordVO.getId();
                        } catch (Exception e) {
                            log.error(CustomLokiLog.errorLog(e));
                            log.error("insertKeywordCms unique 오류:", e);
                        }
                    }
                    if (kId != null && kId > 0) {
                        KeywordMapVO kVo = new KeywordMapVO();
                        kVo.mapping_id = setsId;
                        kVo.keyword_id = kId;
                        keywordId.add(kVo);
                    }
                }
            }
            if (keywordId.size() > 0) {
                keywordRepository.addSetsKeywordMap(keywordId);
            }
        }
        if (vo.getSets().getMetaList() != null && vo.getSets().getMetaList().size() > 0) {
            setsRepository.deleteSetsMetaMap(targetId);
            SetsMetaMapVO map = new SetsMetaMapVO();

            vo.getSets().getMetaList().forEach(meta -> {
                map.setSets_id(setsId);
                map.setMeta_id(meta.getId());
                map.setMeta_name(meta.getName());
                try {
                    setsRepository.addSetsMetaMap(map);
                } catch (Exception e) {
                    log.error(CustomLokiLog.errorLog(e));
                }
            });
        }

        if (vo.getArticles() != null) {
            setsRepository.deleteSetsArticleMap(targetId);

            vo.getArticles().forEach(map -> {

                map.setSets_id(setsId);
                try {
                    setsRepository.addSetsArticleMap(map);
                } catch (Exception e) {
                    log.error(CustomLokiLog.errorLog(e));
                }
            });
        }

        return setsId;
    }

    /**
     * setSummaryForSave -> saveSetSummary 로 메소드명 변경(트랜젝션 때문)
     *
     * @param vo
     * @return
     */
    public Object saveSetSummary(SetSummarySaveRequestVO vo) throws Exception {
        if (vo == null || vo.getSet_id() == null || vo.getSetSummary() == null) {
            return null;
        }

        List<Object> ret = new ArrayList<Object>();
        String set_id = vo.getSet_id();


//		if(vo.saveTypeEqualInsert)
//		{
        //항상 delete & insert
        setSummaryRepository.deleteSetSummaryBySetId(set_id); //삭제
        int qnum = 0;
        for (SetSummaryVO k : vo.getSetSummary()) {
            if (k.getSet_id() == null) {
                k.setSet_id(set_id);
            }
            k.setQnum(++qnum);

            setSummaryRepository.addSetSummary(k);
            Long targetId = k.getId();
            if (targetId == null || targetId == 0L) {
                continue;
            }
            ret.add(k);
        }

        return ret;
    }
}
