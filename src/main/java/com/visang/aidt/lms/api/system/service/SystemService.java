package com.visang.aidt.lms.api.system.service;

import com.visang.aidt.lms.api.member.dto.LoginVO;
import com.visang.aidt.lms.api.utility.utils.CommonUtils;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.visang.aidt.lms.global.vo.ResponseVO;
import com.visang.aidt.lms.api.system.dto.AuthVO;
import com.visang.aidt.lms.api.system.dto.BrandVO;
import com.visang.aidt.lms.api.system.dto.MetaExtensionMapVO;
import com.visang.aidt.lms.api.system.dto.MetaExtensionVO;
import com.visang.aidt.lms.api.system.dto.MetaExtensionVO_request;
import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.system.dto.MetaVO_request;
import com.visang.aidt.lms.api.system.dto.RoleVO;
import com.visang.aidt.lms.api.system.dto.UserVO;
import com.visang.aidt.lms.api.system.dto.UserVO_request;
import com.visang.aidt.lms.api.system.dto.baseVO_request;
import com.visang.aidt.lms.api.system.repository.SystemRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SystemService {

    @Autowired
    private SystemRepository repository;

	@Value("${spring.profiles.active}")
	private String springProfileActive;

    public List<BrandVO> getAllBrand(baseVO_request vo) throws Exception {



//    	public List<RoleVO> getBrandList(String authgroupId) {
//    		return repository.getBrandList(authgroupId);
//    	}




        List<BrandVO> list = repository.getAllBrand(vo);
        return list;
    }

    @Transactional
    public BrandVO brandadd(BrandVO brandVO) throws Exception {
    	return repository.insertAndGetBrand(brandVO);
    }
    @Transactional
    public Long brandupdate(BrandVO brandVO) throws Exception {
		return repository.brandupdate(brandVO);
	}

    public List<BrandVO> brandsearch(BrandVO authVO) throws Exception {
    	return repository.getBrandSearch(authVO);
    }

    public List<AuthVO> getAuthList(baseVO_request vo) throws Exception {
        List<AuthVO> list = repository.getAuthList(vo);
        return list;
    }

    @Transactional
    public AuthVO authgroupadd(AuthVO authVO) throws Exception {
    	return repository.insertAndGetAuthGroup(authVO);
    }
    @Transactional
    public Long updateAuthGroup(AuthVO authVO) throws Exception {
    	return repository.updateAuthGroup(authVO);
    }

    public List<AuthVO> authgroupsearch(AuthVO authVO) throws Exception {
    	return repository.getAuthGroupSearch(authVO);
    }



//    public AuthVO getAuthGroupByUserBrand(String brand_id, String uid) {
//    	Map<String, String> map = new HashMap<String, String>();
//		map.put("brand_id", brand_id.replaceAll("[^a-zA-Z0-9]", ""));
//		map.put("uid", uid);
//    	return repository.getAuthGroupByUserBrand(map);
//    }
//
//    public List<AuthVO> getAuthGroupByUserId(String uid) {
//    	return repository.getAuthGroupByUserId(uid);
//    }


    public List<UserVO> getUserList(UserVO_request vo) throws Exception {
    	List<UserVO> listUserVO = repository.getUserList(vo);
        return listUserVO;
    }

    public UserVO getUserById(String uid)throws Exception  {
        return repository.getUserById(uid);
    }

    @Transactional
    public UserVO userAdd(UserVO_request userVO) throws Exception
    {


    	if(userVO.getPassword() == null || userVO.getPassword().length() < 3)
    	{
    		return null;
    	}
		String encPwd = CommonUtils.encryptString(userVO.getPassword());
		userVO.setPassword(encPwd);
		UserVO obj = repository.insertAndGetUser(userVO);

		userVO.setPassword("");

//		List<AuthVO> listAuthVO = vo.getAuthgroup();
//
//		listAuthVO.forEach((authVO) -> {
//			if(authVO.getId() != null && authVO.getId()>0)
//			{
//				Map<String, String> map = new HashMap<String, String>();
//				map.put("uid", obj.getUid());
//				map.put("authgroup_id", authVO.getId().toString());
//				map.put("brand_id", ""+authVO.getBrand_id());
//				repository.insertUserAuthGroup(map);
//			}
//		});

		return obj;
    }

    @Transactional
    public ResponseVO userupdate(UserVO_request userVO, Boolean admin) throws Exception
    {
    	ResponseVO response = new ResponseVO();

		String user_id = SecurityContextHolder.getContext().getAuthentication().getName();

		if(!admin && !userVO.getUid().equals(user_id))
		{
			// TODO 관리자 - 임시비번 만드는 기능 만들어야 함.

			final Boolean _freePass = false; //DEBUG

			if(_freePass)
			{
				user_id = userVO.getUid();
			}
			else
			{
				response.errorMessage = "변경하려는 계정으로 로그인 후 이용하세요.";
				return response; //로그인한 사용자와, 변경하려는 사용자의 아이디가 다름.
			}
		}

		if(userVO.getPassword() != null && userVO.getPassword().length() > 3)
		{
			String encPwd = CommonUtils.encryptString(userVO.getPassword());
			userVO.setPassword(encPwd);
		}
		else
		{
			//비번을 변경하지 않음.
			userVO.setPassword(null);
		}
		int result = repository.userupdate(userVO);

		response.vo = result;
		if(result <= 0)
		{

			response.errorMessage = "계정을 찾을 수 없습니다.";
		}
//		else
//		{
//			repository.deleteUserAuthGroup(user_id); //user_brand_authgroup_map 의 모든 uid를 제거.
//
//			List<AuthVO> listAuthVO = voParam.getAuthgroup();
//			final String uid = user_id;
//			listAuthVO.forEach((authVO) -> {
//				if(authVO.getId() != null && authVO.getId()>0)
//				{
//					Map<String, String> map = new HashMap<String, String>();
//					map.put("uid", uid);
//					map.put("authgroup_id", authVO.getId().toString());
//					map.put("brand_id", ""+authVO.getBrand_id());
//					repository.insertUserAuthGroup(map);
//				}
//			});
//		}

		return response;
    }
//    public Integer userupdate(UserVO user) {
//    	return repository.userupdate(user);
////        return loginDao.insertUser(user);
//    }

    public List<MetaExtensionVO> getAllMeta(MetaExtensionVO_request vo) throws Exception {
        List<MetaExtensionVO> list = repository.getAllMeta(vo);
        return list;
    }
    public List<MetaVO> getMetaList(MetaVO_request vo) throws Exception {
        List<MetaVO> list = repository.getMetaList(vo);
        return list;
    }


    @Transactional
    public List<MetaVO> metaaddList(List<MetaVO> list) throws Exception {

    	List<MetaVO> resultMeta = new ArrayList<MetaVO>();

    	list.forEach((vo) -> {
            MetaVO obj = null;
            try {
                obj = repository.insertAndGetMeta(vo);
            	resultMeta.add(obj);
            } catch (Exception e) {
				log.error(CustomLokiLog.errorLog(e));
            }
		});
		return resultMeta;
	}
    @Transactional
    public MetaVO addMetaEx(MetaExtensionMapVO vo) throws Exception {
    	if(vo.getMeta() == null) return null;
    	if(vo.getMeta().is_extensionData())
		{
			MetaVO ext = addMetaExtension(vo.getMeta());
			vo.getMeta().setMeta_extension_id(ext.getMeta_extension_id());
		}
		MetaVO newMeta = metaadd(vo.getMeta());


		Map<String, Long> map = new HashMap<String, Long>();
		List<MetaVO> listMetaVO = vo.getMetamapList();
		listMetaVO.forEach((metaVO) -> {
            try {
				map.put("a", newMeta.getId());
				map.put("b", metaVO.getId());
                metametamapadd(map);
            } catch (Exception e) {
				log.error(CustomLokiLog.errorLog(e));
            }
        });
    	return newMeta;
    }
    @Transactional
    public Object updateMetaEx(MetaExtensionMapVO metaMapVO) throws Exception {

    	MetaExtensionVO vo = metaMapVO.getMeta();
    	if(vo == null) return null;
    	{
    		MetaExtensionVO meta = null;
	    	if(vo.is_extensionData())
			{

	    		if(vo.getId() != null)
	    		{
	    			meta = getMetaById(""+vo.getId());
	    		}
	    		if(meta == null && vo.getCode() != null)
	    		{
	    			meta = getMetaByCode(""+vo.getCode());
	    		}

	    		if(meta != null)
	    		{
	    			if(meta.getMeta_extension_id() == null)
	    			{
	    				MetaVO ext = addMetaExtension(vo);
	    				vo.setMeta_extension_id(ext.getMeta_extension_id());
	    			}
	    			else
	    			{
	    				vo.setMeta_extension_id(meta.getMeta_extension_id());
	    				updateMetaExtension(vo);
	    			}
				}
			}
    	}

    	int result = updateAndGetMeta(vo);
    	if(result > 0)
    	{
    		List<MetaVO> listMetaVO = metaMapVO.getMetamapList();
    		if(listMetaVO != null)
    		{
    			Long metaId = vo.getId();
		    	repository.deleteMetaMetaMap(""+metaId);
				Map<String, Long> map = new HashMap<String, Long>();
				listMetaVO.forEach((metaVO) -> {
                    try {
						map.put("a", metaId);
						map.put("b", metaVO.getId());
                        metametamapadd(map);
                    } catch (Exception e) {
						log.error(CustomLokiLog.errorLog(e));
                    }
                });
    		}
			return vo;
    	}
		//bookVO = metaMapVO.getBook();
    	return null;
	}
	public MetaVO metaadd(MetaVO obj) throws Exception {
    	return repository.insertAndGetMeta(obj);
	}

	public MetaExtensionVO getMetaById(String id) throws Exception {
		return repository.getMetaById(id.replaceAll("[^a-zA-Z0-9]", ""));
	}

	public MetaExtensionVO getMetaByCode(String code) throws Exception {
		//return repository.getMetaByCode(code.replaceAll("[^a-zA-Z0-9-]", ""));
		return repository.getMetaByCode(code);
	}

	public List<MetaExtensionVO> getMetaChildrenById(String id) throws Exception {
		return repository.getMetaChildrenById(id.replaceAll("[^a-zA-Z0-9]", ""));
	}
	public List<MetaVO> getMetaExById(String id) throws Exception {

		return repository.getMetaExList(id.replaceAll("[^a-zA-Z0-9]", ""));
	}
	public List<MetaExtensionVO> getMetaDescendantsByCode(String code) throws Exception {
    	//return repository.getMetaDescendantsByCode(code.replaceAll("[^a-zA-Z0-9-]", ""));
		return repository.getMetaDescendantsByCode(code);
	}

	public void metametamapadd(Map<String, Long> map) throws Exception {
		repository.metametamapadd(map);
	}

	public MetaVO addMetaExtension(MetaVO vo) throws Exception {
		return repository.addMetaExtension(vo);
	}
	public Long updateMetaExtension(MetaVO vo) throws Exception {
		return repository.updateMetaExtension(vo);
	}


//	public Integer userauthgroupdelete(String uid) {
//		return repository.deleteUserAuthGroup(uid);
//	}


	public List<MetaExtensionVO> curriculumList(Map<String, String> params) throws Exception
	{
		// curriBook=39&limit=curriUnit5

		if(params.get("curriBook") == null) return null; //파라이터 없음.

		MetaVO meta = getMetaById(params.get("curriBook"));
		if(meta == null) return null; //메타에 요청 아이디가 없음.

		if(meta.getName() == null) return null; //잘못된 메타 데이터
		if(!"curriBook".equals(meta.getName())) return null; //입력(curriBook)이 교재 번호가 아님.


		List<MetaExtensionVO> list = getMetaDescendantsByCode(meta.getCode());
		String limit = params.get("limit");
		if(limit == null) return list; //제한없음. 전체리턴.

		for(int i = 0 ; i < list.size(); i++ )
		{
			String meta_name =  list.get(i).getName();
			if(meta_name != null && meta_name.startsWith("curriUnit"))
			{
				if(limit.compareTo(meta_name) < 0)
				{
					list.remove(i);
					--i;
				}
			}
		}

		return list;
	}
//    public List<LibraryVO> getAllImageList() {
//        List<LibraryVO> list = libraryRepository.getAllLibrary("image");
//        return list;
//    }
//
//    public List<LibraryVO> getAllAudioList() {
//        List<LibraryVO> list = libraryRepository.getAllLibrary("audio");
//        return list;
//    }
//
//    public List<LibraryVO> getAllVideoList() {
//        List<LibraryVO> list = libraryRepository.getAllLibrary("video");
//        return list;
//    }
//
//    public List<LibraryVO> getAllTextList() {
//        List<LibraryVO> list = libraryRepository.getAllLibrary("text");
//        return list;
//    }
//
//    public List<LibraryVO> getLibraryByCreator(String creator) {
//        List<LibraryVO> list = libraryRepository.getLibraryByCreator(creator);
//        return list;
//    }
//
//    @Transactional
//    public void insertLibrary(LibraryVO libraryVO) {
//
//    }

	public int updateAndGetMeta(MetaVO vo) throws Exception {
		return repository.updateAndGetMeta(vo);
	}

	public List<MetaVO> searchCurriBook(HashMap<String, String> map) throws Exception {
		return repository.searchCurriBook(map);
	}

	public List<RoleVO> getBrandList(String authgroupId) throws Exception {
		return repository.getBrandList(authgroupId);
	}

	public Object authInfo(UserVO_request vo) throws Exception {
		UserVO newLoginVO = repository.authInfo(vo);
		if(newLoginVO != null)
		{
			List<RoleVO> authvo = repository.getBrandList(newLoginVO.getAuthgroup());
	    	//newLoginVO.setArrayOfBrand(authvo.stream().map(k->k.getBrand_id).toArray());
	    	//newLoginVO.setBrandRole(authvo);
	    	//return newLoginVO;
			return authvo;
		}
		return null;
	}


	//학습맵
	public Object addStudyMap(MetaExtensionMapVO vo) throws Exception
	{
		if(vo == null || vo.getMeta() == null || vo.getMetamapList() == null || vo.getMetamapList().size() == 0) return null;

		//MetaVO studyMapRoot = repository.insertAndGetMeta(vo.getMeta()); //vo.getMeta(); //debug

//		MetaVO studyMapRoot = repository.insertAndGetMeta(vo.getMeta()); //vo.getMeta(); //debug
		MetaVO studyMapRoot = addMetaEx(vo);

		if(studyMapRoot == null) return null;


		//MetaVO curriBook = vo.getMetamapList().stream().filter(m ->"curriBook".equals(m.getName())).findAny().orElse(null);
		MetaVO curriBook = getMetaById(""+vo.getMetamapList().stream().filter(m ->"curriBook".equals(m.getName())).findAny().orElse(null).id);
		if(curriBook == null) return null;

		List<MetaExtensionVO> list = getMetaDescendantsByCode(curriBook.getCode());
		if(list != null)
		{
			List<MetaVO> studyMapList = new  ArrayList<MetaVO>();
			studyMapList.add(studyMapRoot);
			for(int i = 0 ; i < list.size(); i++ )
			{
				MetaVO curriUnit = list.get(i);
				curriUnit.clean();

				if(curriUnit.getName() != null &&  curriUnit.getName().startsWith("curriUnit"))
				{
					if(curriUnit.getDepth() <= curriBook.getMax_depth())
					{

						List<String> codes = Arrays.asList(curriUnit.getCode().split("-"));

						String code = studyMapRoot.getCode() + "-" + String.join("-", codes.subList(1,  codes.size()));
						String pcode = studyMapRoot.getCode() + ((codes.size() <= 2)? "":("-" + String.join("-", codes.subList(1,  codes.size()-1))));

						curriUnit.setCode(code);

						curriUnit.setName("studyMap"+curriUnit.getName().substring(curriUnit.getName().length()-1, curriUnit.getName().length()));


						MetaVO p = studyMapList.stream().filter(m->m.getCode().equals(pcode)).findAny().orElse(null);
						if(p != null)
						{
							curriUnit.setParent_id(p.getId());
						}
						else
						{
							curriUnit.setParent_id(studyMapRoot.getId());
						}
						MetaVO studyMap = metaadd(curriUnit);

						studyMapList.add(studyMap);
						//list.set(i,studyMap);
					}
				}
			}
			Long fullCount = (long) studyMapList.size();
			studyMapList.forEach(m->m.setFull_count(fullCount));
			return studyMapList;
		}

		return null;
	}

	public Object deleteStudyMap(MetaVO vo) throws Exception
	{
		if(vo == null) return null;
		MetaVO curriBook = null;

		if(curriBook == null && vo.getId() != null)
		{
			curriBook = getMetaById(""+vo.getId());
		}
		if(curriBook == null && vo.getCode() != null)
		{
			curriBook = getMetaByCode(""+vo.getCode());
		}
		if(curriBook == null || curriBook.getCode() == null) return null;

		List<MetaExtensionVO> list = getMetaDescendantsByCode(curriBook.getCode());
		if(list != null)
		{
			List<Object> ret = new  ArrayList<Object>();

			for(int i = 0 ; i < list.size(); i++ )
			{
				MetaVO item = list.get(i);

				repository.deleteMetaMetaMap(""+item.getId());
				item.setIs_active(false);
				ret.add(updateAndGetMeta(item));
			}

			return ret;
		}

		return null;
	}

	public void addLmsUser(String loginUserId) throws Exception {
		addLmsUserAndReturnId(null, null, loginUserId);
	}

	public void addLmsUser(Long creator_id, String creator, String loginUserId) throws Exception {
		addLmsUserAndReturnId(creator_id, creator, loginUserId);
	}

	private Long addLmsUserAndReturnId(Long creator_id, String creator, String loginUserId) throws Exception {
		// NCP가 아닐 경우 그냥 리턴 (aws의 경우 cms에만 저작자가 있음)
		if (StringUtils.endsWith(springProfileActive, "dev") || StringUtils.endsWith(springProfileActive, "prod")) {
			return creator_id;
		}
		LoginVO loginVO = new LoginVO();
		loginVO.setCreator_id(creator_id);
		loginVO.setCreator(creator);
		loginVO.setLoginUserId(loginUserId);
		LoginVO cmsUserInfo = repository.selectUserInfo(loginVO);
		if (cmsUserInfo == null) {
			// lms user에서 데이터 탐색
			LoginVO lmsUserInfo = null;
			UserVO_request userParamVO = new UserVO_request();
			/* cbs에서 아래 로직 실행하면 오류 발생하기 때문에 try catch
				- spring profile로 조건 처리 하기 때문에 오류 발생 염려는 없지만 만일을 위해 try catch 처리 */
			try {
				lmsUserInfo = repository.selectLmsUserInfo(loginVO);
				// lms 에도 저작자 없으면 오류
				if (lmsUserInfo == null) {
					return null;
				}
				userParamVO.setUid(lmsUserInfo.getUid());
				userParamVO.setName(lmsUserInfo.getName());
				// 하드코딩 (LMS에서 들어가는 정보는 pass워드가 의미 없음)
				userParamVO.setPassword("e941e808846e10729bc2f5f87fb6c0fc6ae4e734a248b904dadc5fd0bbfd45c2");
				userParamVO.setTeam("lms-user-insert-auto");
				userParamVO.setCreator(lmsUserInfo.getUid());
				userParamVO.setCreator_name(lmsUserInfo.getName());
				userParamVO.setIs_active(true); // default true로 세팅
				repository.insertUser(userParamVO);
			} catch (Exception e) {
				log.error(CustomLokiLog.errorLog(e));
				// 만일 cbs에서 이 로직 수행하면 db가 없으므로 오류 발생하여 0으로 return 하도록 처리
				return null;
			}
			return userParamVO.getId();
		}
		// 기존 데이터가 있으면 기존 id return
		return cmsUserInfo.getId();
	}
}
