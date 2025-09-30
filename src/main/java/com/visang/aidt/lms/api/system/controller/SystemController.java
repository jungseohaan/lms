package com.visang.aidt.lms.api.system.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import javax.servlet.http.HttpSession;

import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.visang.aidt.lms.global.vo.ResponseVO;
import com.visang.aidt.lms.api.system.dto.AuthVO;
import com.visang.aidt.lms.api.system.dto.BrandVO;
import com.visang.aidt.lms.api.system.dto.MetaExtensionMapVO;
import com.visang.aidt.lms.api.system.dto.MetaExtensionVO_request;
import com.visang.aidt.lms.api.system.dto.MetaMapVO;
import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.system.dto.MetaVO_request;
import com.visang.aidt.lms.api.system.dto.UserVO_request;
import com.visang.aidt.lms.api.system.dto.baseVO_request;
import com.visang.aidt.lms.api.system.service.SystemService;

@Slf4j
@Controller
@RequestMapping(path = "/api/system", produces = MediaType.APPLICATION_JSON_VALUE)
//@PreAuthorize("hasRole('VIEW')")
public class SystemController {

	@Autowired
	private SystemService systemService;

	@GetMapping("/")
	public String getAllList(@RequestParam Map<String, String> params) {
		return "redirect:/api/system/brandlist";
	}

	@GetMapping("/brandlist")
	public @ResponseBody ResponseVO getAllBrand(@RequestParam Map<String, String> param) {
		ResponseVO response = new ResponseVO();
		try {
			com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			String json = objectMapper.writeValueAsString(param);

			UserVO_request vo = objectMapper.readValue(json, UserVO_request.class);
			response.vo = systemService.getAllBrand(vo);
		} catch (Exception e) {
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "브랜드 조회 오류";
		}
//
//
//		try {
//			response.vo = systemService.getAllBrand(params.get("uid"));
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			response.errorMessage = e.toString();
//		}
		return response;
	}
	@PostMapping("/brandlist")
	public @ResponseBody ResponseVO allBrand(@RequestBody baseVO_request vo) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.getAllBrand(vo);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "브랜드 조회 오류";
		}
		return response;
	}



	@PostMapping(value = "/brand/add")
	public @ResponseBody ResponseVO brandadd(@RequestBody BrandVO brandVO) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.brandadd(brandVO);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "브랜드 등록 오류";
		}
		return response;
//		BrandVO obj = systemService.brandadd(brandVO);
//		return obj;
	}
	@PostMapping(value = "/brand/update")
	public @ResponseBody ResponseVO brandupdate(@RequestBody BrandVO brandVO) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.brandupdate(brandVO);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "브랜드 업데이트 오류";
		}
		return response;
//		BrandVO obj = systemService.brandadd(brandVO);
//		return obj;
	}

	@PostMapping(value = "/brand/search")
	public @ResponseBody ResponseVO brandsearch(@RequestBody BrandVO brandVO) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.brandsearch(brandVO);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "브랜드 검색 오류";
		}
		return response;
//		List<BrandVO> list = systemService.brandsearch(brandVO);
//		return list;
	}

	@GetMapping(value = "/authlist")
	public @ResponseBody ResponseVO getAuthlist(@RequestParam Map<String, String> param) {
		ResponseVO response = new ResponseVO();
		try {
			com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			String json = objectMapper.writeValueAsString(param);

			baseVO_request vo = objectMapper.readValue(json, baseVO_request.class);
			response.vo = systemService.getAuthList(vo);
		} catch (Exception e) {
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "권한 조회 오류";
		}


		return response;
	}
	@PostMapping(value = "/authlist")
	public @ResponseBody ResponseVO authlist(@RequestBody baseVO_request vo) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.getAuthList(vo);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "권한 조회 오류";
		}
		return response;
	}

	@PostMapping(value = "/authInfo")
    public @ResponseBody ResponseVO authInfo(@RequestBody UserVO_request vo) {

    	ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.authInfo(vo);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "권한 정보 조회 오류";
		}
		return response;
	}

	@PostMapping(value = "/authgroup/add")
	public @ResponseBody ResponseVO authgroupadd(@RequestBody AuthVO authVO)
	{
		ResponseVO response = new ResponseVO();
		try {
			response.vo =systemService.authgroupadd(authVO);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "권한 그룹 등록 오류";
		}
		return response;
	}

	@PostMapping(value = "/authgroup/update")
	public @ResponseBody ResponseVO authgroupupdate(@RequestBody AuthVO authVO)
	{
		ResponseVO response = new ResponseVO();
		try {
			response.vo =systemService.updateAuthGroup(authVO);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "권한 그룹 수정 오류";
		}
		return response;
	}
	@PostMapping(value = "/authgroup/search")
	public @ResponseBody ResponseVO authgroupsearch(@RequestBody AuthVO authVO) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo =systemService.authgroupsearch(authVO);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "권한 그룹 수정 오류";
		}
		return response;
	}

//	@GetMapping(value = "/authgroup/branduser")
//	public @ResponseBody ResponseVO authgroupbyid(@RequestParam String brand_id, String uid) {
//
//		ResponseVO response = new ResponseVO();
//		try {
//			response.vo = systemService.getAuthGroupByUserBrand(brand_id, uid);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			response.errorMessage = e.toString();
//		}
//		return response;
//		//return systemService.getAuthGroupByUserBrand(brand, uid);
//	}

//	@GetMapping(value = "/authgroup/user")
//	public @ResponseBody ResponseVO authgroupbyuid(@RequestParam String uid) {
//
//		ResponseVO response = new ResponseVO();
//		try {
//			response.vo = systemService.getAuthGroupByUserId(uid);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			response.errorMessage = e.toString();
//		}
//		return response;
//		//return systemService.getAuthGroupByUserBrand(brand, uid);
//	}

	@PostMapping(value = "/useradd")
	public @ResponseBody ResponseVO useradd(@RequestBody UserVO_request vo) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.userAdd(vo);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "사용자 등록 오류";
		}
		return response;
	}


	@PostMapping(value = "/userupdate")
	public @ResponseBody ResponseVO userupdate(@RequestBody UserVO_request vo) {


		try {
			return systemService.userupdate(vo, true);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			ResponseVO response = new ResponseVO();
			response.errorMessage = "사용자 수정 오류";
			return response;
		}


	}

//    @PreAuthorize("hasRole('VIEW')")
	@GetMapping(value = "/userlist")
	public @ResponseBody ResponseVO getUserlist(@RequestParam Map<String, String> param) {

		ResponseVO response = new ResponseVO();

		try {
			com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			String json = objectMapper.writeValueAsString(param);

			UserVO_request vo = objectMapper.readValue(json, UserVO_request.class);
			response.vo = systemService.getUserList(vo);
		} catch (Exception e) {
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "사용자 조회 오류";
		}



//		try {
//			response.vo = systemService.getUserList();
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			response.errorMessage = e.toString();
//		}
		return response;
		//return systemService.getUserList();
	}
	@PostMapping(value = "/userlist")
	public @ResponseBody ResponseVO userlist(@RequestBody UserVO_request vo) {

		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.getUserList(vo);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "사용자 목록 조회 오류";
		}
		return response;
		//return systemService.getUserList();
	}

	@GetMapping(value = "/user")
	public @ResponseBody ResponseVO user(@RequestParam String uid) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.getUserById(uid);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "사용자 정보 조회 오류";
		}
		return response;
		//return systemService.getUserById(uid);
	}

	@GetMapping("/meta/list")
	public @ResponseBody ResponseVO getAllMeta1(@RequestParam Map<String, String> params) {


		ResponseVO response = new ResponseVO();
		try {

			com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
			String json = objectMapper.writeValueAsString(params);

			MetaExtensionVO_request vo = objectMapper.readValue(json, MetaExtensionVO_request.class);
			return getAllMeta(vo);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 정보 조회 오류";
		}
		return response;
	}
	@PostMapping("/meta/list")
	public @ResponseBody ResponseVO getAllMeta(@RequestBody MetaExtensionVO_request vo) {


		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.getAllMeta(vo);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 정보 조회 오류";
		}
		return response;
	}


	@PostMapping(value = "/meta/add")
	public @ResponseBody ResponseVO metaadd(@RequestBody List<MetaVO> listMetaVO) {

		ResponseVO response = new ResponseVO();
		try {

			response.vo = systemService.metaaddList(listMetaVO);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 정보 등록 오류";
		}
		return response;


//		List<MetaVO> resultMeta = new ArrayList<MetaVO>();
//
//		listMetaVO.forEach((metaVO) -> {
//			MetaVO obj = systemService.metaadd(metaVO);
//			resultMeta.add(obj);
//		});
//
//		return resultMeta;
	}


	@PostMapping(value = "/meta/addMetaEx")
	public @ResponseBody ResponseVO addMetaEx(@RequestBody MetaExtensionMapVO metaMapVO) {

		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.addMetaEx(metaMapVO);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 확장 정보 등록 오류";
		}
		return response;
	}
	@PostMapping(value = "/meta/updateMetaEx")
	public @ResponseBody ResponseVO updateMetaEx(@RequestBody MetaExtensionMapVO metaMapVO) {

		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.updateMetaEx(metaMapVO);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 확장 정보 수정 오류";
		}
		return response;
	}
	@GetMapping("/meta/id/{id}")
	public @ResponseBody ResponseVO getMetaParent(@PathVariable String id) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.getMetaById(id);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 정보 조회 오류 (by id)";
		}
		return response;
//
//		MetaVO obj = systemService.getMetaById(id);
//		return obj;
	}

	@GetMapping("/meta/code/{code}")
	public @ResponseBody ResponseVO getMetaByCode(@PathVariable String code) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.getMetaByCode(code);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 정보 조회 오류 (by code)";
		}
		return response;
//
//		MetaVO obj = systemService.getMetaByCode(code);
//		return obj;
	}


	@GetMapping("/meta/children/{id}")
	public @ResponseBody ResponseVO getMetaChildrenById(@PathVariable String id) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.getMetaChildrenById(id.replaceAll("[^a-zA-Z0-9]", ""));
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 정보 조회 오류 (by children)";
		}
		return response;

//		List<MetaVO> list = systemService.getMetaChildrenById(id);
//		return list;
	}
	@PostMapping("/meta/children")
	public @ResponseBody ResponseVO metaChildrenById(@RequestBody Map<String, String> params) {
		ResponseVO response = new ResponseVO();
		try {
			String id = (String) params.values().toArray()[0];
			response.vo = systemService.getMetaChildrenById(id.replaceAll("[^a-zA-Z0-9]", ""));
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 하위 정보 조회 오류";
		}
		return response;

//		List<MetaVO> list = systemService.getMetaChildrenById(id);
//		return list;
	}
	@GetMapping("/meta/metaEx/{id}")
	public @ResponseBody ResponseVO getMetaExById(@PathVariable String id) {
		ResponseVO response = new ResponseVO();
		try {

			response.vo = systemService.getMetaExById(id);


		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 확장 정보 조회 오류 (by id)";
		}
		return response;
	}

	@GetMapping("/meta/descendants/{code}")
	public @ResponseBody ResponseVO getMetaDescendantsByCode(@PathVariable String code) {
		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.getMetaDescendantsByCode(code);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 확장 정보 조회 오류 (by code)";
		}
		return response;

//		List<MetaVO> list = systemService.getMetaDescendantsByCode(code);
//		return list;
	}
	@GetMapping("/meta/curriculumList")
	public @ResponseBody ResponseVO getCurriculumList(@RequestParam Map<String, String> params) {

		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.curriculumList(params);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 과정 정보 조회 오류";
		}
		return response;
	}
	@PostMapping("/meta/curriculumList")
	public @ResponseBody ResponseVO curriculumList(@RequestBody Map<String, String> params) {

		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.curriculumList(params);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 과정 정보 조회 오류";
		}
		return response;
	}

	@PostMapping(value = "/meta/update")
	public @ResponseBody ResponseVO updateAndGetMeta(@RequestBody MetaVO vo) {

		ResponseVO response = new ResponseVO();
		try {
			if(vo.getId() != null && vo.getId() > 0)
			{
				response.vo = systemService.updateAndGetMeta(vo);
			}
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 정보 수정 오류";
		}
		return response;

//		if(vo.getId() != null && vo.getId() > 0)
//		{
//			return systemService.updateAndGetMeta(vo);
//		}
//
//		return 0;
	}

	@PostMapping(value = "/meta/search/curriBook")
	public @ResponseBody ResponseVO searchCurriBook(@RequestBody HashMap<String, String> map) {

		ResponseVO response = new ResponseVO();
		try {

			String limit_page = map.get("limit_page");
			//if(limit_page == null) limit_page = map.get("page");

			String limit_pageItemCnt =  map.get("limit_pageItemCnt");
			//if(limit_pageItemCnt == null) limit_pageItemCnt =map.get("pageItemCnt");

			if(limit_page != null && limit_page != "" && limit_pageItemCnt != null && limit_pageItemCnt != "")
	    	{
				String limitQuery = ""+ (((Integer.valueOf(limit_page) -1) * Integer.valueOf(limit_pageItemCnt))) + ", " +  limit_pageItemCnt;
				map.put("limitQuery", limitQuery);
	    	}

			response.vo = systemService.searchCurriBook(map);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 교육과정 정보 조회 오류";
		}
		return response;
	}

	@PostMapping(value = "/meta/curriBook")
	public @ResponseBody ResponseVO curriBook(@RequestBody MetaVO_request vo) {




		ResponseVO response = new ResponseVO();
		try {
			List<MetaMapVO> curriBookEx = new ArrayList<MetaMapVO>();


			vo.setName("curriBook");
			List<MetaVO> curriBookList = systemService.getMetaList(vo);


			curriBookList.forEach(meta->{
                List<MetaVO> metaMapList = null;
                try {
                    metaMapList = systemService.getMetaExById(""+meta.getId());
					MetaMapVO metamapVo = new MetaMapVO();
					metamapVo.setMeta((MetaVO)meta);
					metamapVo.setMetamapList(metaMapList);
					curriBookEx.add(metamapVo);
                } catch (Exception e) {
					log.error(CustomLokiLog.errorLog(e));
                }
			});

			response.vo = curriBookEx;
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "메타 교육과정 확장 정보 조회 오류";
		}
		return response;
	}



	//학습맵
	@PostMapping("/meta/addStudyMap")
	public @ResponseBody ResponseVO addStudyMap(@RequestBody MetaExtensionMapVO vo) {

		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.addStudyMap(vo);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "학습맵 등록 오류";
		}
		return response;
	}

	@PostMapping("/meta/deleteStudyMap")
	public @ResponseBody ResponseVO deleteStudyMap(@RequestBody MetaVO vo) {

		ResponseVO response = new ResponseVO();
		try {
			response.vo = systemService.deleteStudyMap(vo);
		}
		catch(Exception e)
		{
			log.error(CustomLokiLog.errorLog(e));
			response.errorMessage = "학습맵 삭제 오류";
		}
		return response;
	}
}
