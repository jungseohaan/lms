package com.visang.aidt.lms.api.log.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface BtchExcnLogMapper {

    // 배치 생성 유무 확인
    Map<String, Object> checkBatchInfoExist(String btchNm);
    
    // 배치정보 생성
    int createBtchInfo(Map<String, Object> paramData);

    // 배치상세 생성 유무 확인
    List<Map> checkBatchDetailExist(Map<String, Object> paramData);

    // 배치정보 상세 생성
    int createBtchDetailInfo(Map<String, Object> paramData);

    // 배치정보 상세 수정
    int modifyBtchDetailInfo(Map<String, Object> paramData);
}
