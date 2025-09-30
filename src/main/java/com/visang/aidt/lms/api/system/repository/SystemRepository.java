package com.visang.aidt.lms.api.system.repository;

import com.visang.aidt.lms.api.member.dto.LoginVO;
import org.apache.ibatis.annotations.Mapper;
import com.visang.aidt.lms.api.system.dto.AuthVO;
import com.visang.aidt.lms.api.system.dto.BrandVO;
import com.visang.aidt.lms.api.system.dto.MetaExtensionVO;
import com.visang.aidt.lms.api.system.dto.MetaExtensionVO_request;
import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.system.dto.MetaVO_request;
import com.visang.aidt.lms.api.system.dto.RoleVO;
import com.visang.aidt.lms.api.system.dto.UserVO;
import com.visang.aidt.lms.api.system.dto.UserVO_request;
import com.visang.aidt.lms.api.system.dto.baseVO_request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface SystemRepository {

    public List<BrandVO> getAllBrand(baseVO_request BrandVO) throws Exception;

    public BrandVO insertAndGetBrand(BrandVO brandVO) throws Exception;

    public List<BrandVO> getBrandSearch(BrandVO brandVO) throws Exception;

    public Long brandupdate(BrandVO brandVO) throws Exception;

    public List<AuthVO> getAuthList(baseVO_request vo) throws Exception;

    public AuthVO insertAndGetAuthGroup(AuthVO authVO) throws Exception;

    public Long updateAuthGroup(AuthVO authVO) throws Exception;

    public List<AuthVO> getAuthGroupSearch(AuthVO authVO) throws Exception;

    public AuthVO getAuthGroupById(Long id) throws Exception;

    public List<RoleVO> getBrandList(String authgroupId) throws Exception;

    UserVO authInfo(UserVO_request vo) throws Exception;

    //public AuthVO getAuthGroupByUserBrand(Map<String, String> map) throws Exception;

    public List<UserVO> getUserList(UserVO_request vo) throws Exception;

    public UserVO getUserById(String uid) throws Exception;

    public void insertUser(UserVO_request user) throws Exception;

    public UserVO insertAndGetUser(UserVO_request user) throws Exception;

    public Integer userupdate(UserVO_request user) throws Exception;

    public List<MetaExtensionVO> getAllMeta(MetaExtensionVO_request vo) throws Exception;

    public List<MetaVO> getMetaList(MetaVO_request vo) throws Exception;


    public MetaVO insertAndGetMeta(MetaVO vo) throws Exception;

    public MetaExtensionVO getMetaById(String id) throws Exception;

    public MetaExtensionVO getMetaByCode(String code) throws Exception;

    public List<MetaExtensionVO> getMetaChildrenById(String id) throws Exception;

    //public List<MetaVO> getMetaList(String id) throws Exception;
    public List<MetaVO> getMetaExList(String id) throws Exception;

    public int updateAndGetMeta(MetaVO obj) throws Exception;

    public int deleteMetaMetaMap(String id) throws Exception;

    public List<MetaExtensionVO> getMetaDescendantsByCode(String code) throws Exception;

    public MetaVO metametamapadd(Map<String, Long> map) throws Exception;

    //메타 확장
    public MetaVO addMetaExtension(MetaVO vo) throws Exception;

    public Long updateMetaExtension(MetaVO vo) throws Exception;

    //public void insertUserAuthGroup(Map<String, String> map) throws Exception;
    //public int deleteUserAuthGroup(String uid) throws Exception;
    public List<MetaVO> searchCurriBook(HashMap<String, String> map) throws Exception;


    LoginVO selectUserInfo(LoginVO loginVO) throws Exception;

    LoginVO selectLmsUserInfo(LoginVO loginVO) throws Exception;
}
