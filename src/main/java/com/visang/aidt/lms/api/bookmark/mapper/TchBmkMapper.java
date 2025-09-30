package com.visang.aidt.lms.api.bookmark.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName : com.visang.aidt.lms.api.bookmark.mapper
 * fileName : Mapper
 */
@Mapper
public interface TchBmkMapper {
    // /tch/mdul/bmk/list
    List<Map> findBkmkList(Map<String, Object> paramData) throws Exception;
    List<Map> findKbmkTagList(List<LinkedHashMap<Object, Object>> list, Map<String, Object> paramData) throws Exception;

    // /tch/mdul/bmk/share
    int modifyBkmkCocnrAt(Map<String, Object> paramData) throws Exception;
    
    // 굥유취소
    int modifyBkmkClearCocnrAt(Map<String, Object> paramData) throws Exception;
        
    int insertTagInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findBmkShare(Map<String, Object> paramData) throws Exception;
    int insertBkmkTagMapng(Map<String, Object> paramData) throws Exception;

    // /tch/mdul/bmk/save
    int insertBkmkInfo(Map<String, Object> paramData) throws Exception;

    // /tch/mdul/bmk/delete
    int deleteTagInfo(Map<String, Object> paramData) throws Exception;
    int deleteBkmkTagMapng(Map<String, Object> paramData) throws Exception;
    int deleteBkmkInfo(Map<String, Object> paramData) throws Exception;

    // /tch/mdul/bmk/tag/save
    int insertBmkTagSave(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findTagInfo(Map<String, Object> paramData) throws Exception;
    int createTchMdulBmkTagSave(Map<String, Object> paramData) throws Exception;
    int createTchMdulBmkTagTagsave(Map<String, Object> paramData) throws Exception;
    int createTchMdulBmkTagMappingSave(Map<String, Object> paramData) throws Exception;

    // /tch/mdul/bmk/tag/modify
    Map<String, Object> findTagInfoById(Map<String, Object> paramData) throws Exception;
    int updateTagInfo(Map<String, Object> paramData) throws Exception;

    // /tch/mdul/bmk/tag/delete
    int deleteTagInfoByTagId(Map<String, Object> paramData) throws Exception;
    int deleteBkmkTagMapngByTagId(Map<String, Object> paramData) throws Exception;
    int deleteBkmkTagMapngBybkmkIdByTagId(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTagSaveTagInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchMdulBmkInfo(Map<String, Object> paramData) throws Exception;
    Map<String, Object> findStntMdulBmkInfo(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findTchMdulBmkTagList_currClorNum(Map<String, Object> paramData) throws Exception;
    List<Map> findTchMdulBmkTagList_tagList(Map<String, Object> paramData) throws Exception;

    Map<String, Object> createShareBmk_TagCount(Map<String, Object> paramData) throws Exception;

    Map<String, Object> createShareBmk_BassTagId(Map<String, Object> paramData) throws Exception;

    int deleteBkmkTagMapng2(Map<String, Object> paramData) throws Exception;
    int deleteTagInfo2(Map<String, Object> paramData) throws Exception;

    Map<String, Object> findUserInfo(Map<String, Object> paramData) throws Exception;

}
