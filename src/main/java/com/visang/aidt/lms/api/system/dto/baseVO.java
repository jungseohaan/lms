
package com.visang.aidt.lms.api.system.dto;

import lombok.EqualsAndHashCode;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.Data;

@Data
@EqualsAndHashCode(callSuper = false)
public class baseVO {


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

}
