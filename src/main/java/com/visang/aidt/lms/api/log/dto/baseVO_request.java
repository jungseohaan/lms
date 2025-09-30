
package com.visang.aidt.lms.api.log.dto;

import lombok.EqualsAndHashCode;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.Data;

@Data
@EqualsAndHashCode(callSuper = false)
public class baseVO_request {

	public String loginUserId;

	public String getLoginUserId() {
		// TODO
		//request에서 일부러 loginUserId=""로 쿼리할 수 있고, 이 경우 로그인 계정은 무시해야 함.
		//요청이 없는 경우, 즉 null일때만 로그인 아이디를 구해야 함.
		if (loginUserId != null) {
			return loginUserId;
		}
		if (SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
			loginUserId = SecurityContextHolder.getContext().getAuthentication().getName();
			if (loginUserId == null) {
				loginUserId = "";
			}
		}
		return loginUserId;
	}

	public String search;

	public String getSearchQuery() {
		if (search != null && search != "")
			return "%" + search + "%";
		else
			return "";
	}

	public String msg;

	public String limit_page;
	public String limit_pageItemCnt;
	public String limit;

	public String orderby_col;
	public String orderby_dir;

	public void setLimit_page(String limit_page) {
		this.limit_page = limit_page;
		if (limit_page != null && limit_page != "" && limit_pageItemCnt != null && limit_pageItemCnt != "") {
			this.limit = "" + (((Integer.valueOf(limit_page) - 1) * Integer.valueOf(limit_pageItemCnt))) + ", " + limit_pageItemCnt;
		}
	}

	public void setLimit_pageItemCnt(String limit_pageItemCnt) {
		this.limit_pageItemCnt = limit_pageItemCnt;
		if (limit_page != null && limit_page != "" && limit_pageItemCnt != null && limit_pageItemCnt != "") {
			this.limit = "" + (((Integer.valueOf(limit_page) - 1) * Integer.valueOf(limit_pageItemCnt))) + ", " + limit_pageItemCnt;
		}
	}

	public String getLimit() {
		if (limit_page != null && limit_page != "" && limit_pageItemCnt != null && limit_pageItemCnt != "") {
			limit = "" + (((Integer.valueOf(limit_page) - 1) * Integer.valueOf(limit_pageItemCnt))) + ", " + limit_pageItemCnt;
		}
		return limit;
	}

}
