package com.visang.aidt.lms.api.materials.mapper;

import com.visang.aidt.lms.api.utility.utils.PagingParam;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TchMaterialsMapper {
    int createMaterialsSaveSets(Map<String, Object> paramData) throws Exception;
    int createMaterialsSaveExtLearnCnts(Map<String, Object> paramData) throws Exception;
    int createMaterialsDetailSaveSetsummary(Map<String, Object> paramData) throws Exception;
    int createMaterialsDetailSaveExtLearnCnts(Map<String, Object> paramData) throws Exception;
    List<Map> findMaterialsList(PagingParam<?> paramData) throws Exception;
    List<Map<String, Object>> findMaterialsDetail(Map<String, Object> paramData);

    int checkDuplicateName(Map<String, Object> paramData) throws Exception;

    int selecTcMaterialsInfoCount(Map<String, Object> paramData);

    int createMaterialsClassTaks(Map<String, Object> paramData);
    int selecMaterialsClassTasksCount(Map<String, Object> paramData);

    List<Map> findMaterialsClassTasksList(PagingParam<?> paramData) throws Exception;

    int findIsTchMetrialsYn(Map<String, Object> paramData);

    int updateMaterialsTitle(Map<String, Object> paramData);

    int deleteMaterialsDetailByMaterialsInfoId(Map<String, Object> paramData) throws Exception;

    int deleteMaterialsClassTasksByMaterialsInfoId(Map<String, Object> paramData) throws Exception;

    int deleteMaterialsInfo(Map<String, Object> paramData) throws Exception;

    int findSetSummaryForMaterials(Map<String, Object> paramData) throws Exception;

    String selectSetsIdByTcMaterialsInfoId(Map<String, Object> paramData) throws Exception;

    int deleteScrapInfoBySetsId(Map<String, Object> paramData) throws Exception;
}
