package com.visang.aidt.lms.api.user.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.shop.mapper
 * fileName : ShopMapper
 * USER : lsm
 * date : 2024-01-30
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-30         lsm          최초 생성
 */
@Mapper
public interface TchRewardMapper {
    Map<String, Object> findStntRewardStatus(Map<String, Object> paramData) throws Exception;

    List<Map<String, Object>>  findStntRwdInfo(Map<String, Object> paramData) throws Exception;

}
