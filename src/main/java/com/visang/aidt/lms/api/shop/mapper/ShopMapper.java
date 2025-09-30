package com.visang.aidt.lms.api.shop.mapper;

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
public interface ShopMapper {
    // /shop/userinfo
    Map<String, Object> findShopUserInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemProfileInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemGameInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemSkinInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object>  findShopItemProfileInfoDefault(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemGameInfoDefault(Map<String, Object> paramData) throws Exception;
    Map<String, Object>  findShopItemSkinInfoDefault(Map<String, Object> paramData) throws Exception;


    // /shop/item-list
    List<Map> findShopItemInfo(Map<String, Object> paramData) throws Exception;

    List<Map> findSpTmeInfoCtgry(Map<String, Object> paramData) throws Exception;
    // /shop/item-list
    List<Map> findShopItemInfoP(Map<String, Object> paramData) throws Exception;
    List<Map> findShopItemInfoS(Map<String, Object> paramData) throws Exception;
    List<Map> findShopItemInfoG(Map<String, Object> paramData) throws Exception;

    // /shop/item-Detail
    Map<String, Object> findShopItemProfileDetail(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findShopItemGameDetail(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findShopItemSkinDetail(Map<String, Object> paramData) throws Exception;

    // /shop/use_item
    int updateSpPrchsInfoRgi(Map<String, Object> paramData) throws Exception;
    int insertSpPrchsHist(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findSpPrchsHistHtStSum(Map<String, Object> paramData) throws Exception;
    int updateSpPrchsInfoHt(Map<String, Object> paramData) throws Exception;
    int updateSpPrchsInfoSt(Map<String, Object> paramData) throws Exception;


    // /shop/myroom
    List<Map> findShopClassMate(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findClassMateConnStatus(Map<String, Object> paramData) throws Exception;
    List<Map> selectMyroomClassMate(Map<String, Object> paramData) throws Exception;

    // /shop/change-item-inv
    int updateSpPrchsHistInvSeCd(Map<String, Object> paramData) throws Exception;

    // /shop/mdul-info
    Map<String, Object> findShopTabSetsInfo(Map<String, Object> paramData) throws Exception;
    List<Map> findShopSetsAtcInfo(Map<String, Object> paramData) throws Exception;


    // /shop/buy-item
    List<Map> findSpPrchsHistItmChk(Map<String, Object> paramData) throws Exception;
    Map<String, Object> selectChkPrchsInfo(Map<String, Object> paramData) throws Exception;
    int insertSpPrchsInfo(Map<String, Object> paramData) throws Exception;
    int updateSpPrchsInfoUserMsg(Map<String, Object> paramData) throws Exception;
    int selectSpPrchsInfoCheck(Map<String, Object> paramData) throws Exception;

    // /batch/ShopPrchsHistBatchJob
    int insertSpPrchsHistBatch() throws Exception;
    int insertSkPrchsHistBatch() throws Exception;
    int insertGmPrchsHistBatch() throws Exception;
    int insertSpPrchsInfoBatch() throws Exception;
    int insertSkPrchsInfoBatch() throws Exception;
    int insertGmPrchsInfoBatch() throws Exception;


}
