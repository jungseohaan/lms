package com.visang.aidt.lms.api.user.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
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
public interface StntRewardMapper {

    // /shop/use_item
    int insertRwdEarnHist(Map<String, Object> paramData) throws Exception;
    int deleteRwdEarnHist(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectRwdEarnInfo(Map<String, Object> paramData) throws Exception;
    int insertRwdEarnInfo(Map<String, Object> paramData) throws Exception;
    List<Map<String, Object>> selectRwdEarnHistHtStSum(Map<String, Object> paramData) throws Exception;
    int updateRwdEarnInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectRwdEarnPcy(Map<String, Object> paramData) throws Exception;

    Object findRewardStatusInfo(Map<String, Object> paramData) throws Exception;
    List<Map>  findStntMyRewardInfoList(PagingParam<?> pagingParam) throws Exception;

    Map<String, Object> findRewardSendInfo(Map<String, Object> paramData) throws Exception;
}
