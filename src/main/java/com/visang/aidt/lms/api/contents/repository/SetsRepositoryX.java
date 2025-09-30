package com.visang.aidt.lms.api.contents.repository;

import com.visang.aidt.lms.api.contents.dto.*;
import org.apache.ibatis.annotations.Mapper;

import com.visang.aidt.lms.api.system.dto.MetaVO;

import java.util.List;


@Mapper
public interface SetsRepositoryX {

    public SetsVO addSets(SetsVO vo) throws Exception;

    public Long updateSets(SetsVO article) throws Exception;

    public Long updateTempSets(SetsExVO article) throws Exception;


    public void addSetsArticleMap(SetsArticleMapVO map) throws Exception;

    public void deleteSetsArticleMap(String targetId) throws Exception;

    public SetsVO getSetsById(SetsInfoVO vo) throws Exception;

    public SetsExVO getSetsExById(SetsInfoVO vo) throws Exception;


    public void addSetsMetaMap(SetsMetaMapVO metamap) throws Exception;

    public void deleteSetsMetaMap(String targetId) throws Exception;

    public List<MetaVO> getSetsMetaList(String sets_id) throws Exception;

    public List<SetsVO> getSetsByArticleId(String id) throws Exception;

    public int insertSets(SetsSaveVO sets) throws Exception;

    public String getNewSetsId();

    int getNewSetsIdChk(String newSetsId) throws Exception;
}
