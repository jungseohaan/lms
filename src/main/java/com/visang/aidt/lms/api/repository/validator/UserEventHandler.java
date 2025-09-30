package com.visang.aidt.lms.api.repository.validator;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.*;

import com.visang.aidt.lms.api.repository.entity.User;

//이 클래스는 Spring Data Rest 사용 시, JPA의 Validation 용도로 사용 할 수 있습니다.
//이 클래스를 사용하려면 config 폴더에 핸들러를 사용할 수 있도록 Configuration을 만들어야 합니다.
@Slf4j
@RepositoryEventHandler(User.class)
public class UserEventHandler {

	@HandleBeforeCreate
	public void handleUserBeforeCreate(User user) {
		log.info("Before Create User");
	}

	@HandleAfterCreate
	public void handleUserAfterCreate(User user) {
		log.info("After Create User");
	}

	@HandleBeforeDelete
	public void handleUserBeforeDelete(User user) {
		log.info("Before Delete User");
	}

	@HandleAfterDelete
	public void handleUserAfterDelete(User user) {
		log.info("After Delete User");
	}

	@HandleBeforeSave
	public void handleUserBeforeSave(User user) {
		log.info("Before Save User");
	}

	@HandleAfterSave
	public void handleUserAfterSave(User user) {
		log.info("After Save User");
	}

}
