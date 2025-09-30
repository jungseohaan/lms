package com.visang.aidt.lms.api.repository.validator;

import com.visang.aidt.lms.api.repository.entity.CloudLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.rest.core.annotation.*;

//이 클래스는 Spring Data Rest 사용 시, JPA의 Validation 용도로 사용 할 수 있습니다.
//이 클래스를 사용하려면 config 폴더에 핸들러를 사용할 수 있도록 Configuration을 만들어야 합니다.
@Slf4j
@RepositoryEventHandler(CloudLog.class)
public class CloudLogEventHandler {

	@HandleBeforeCreate
	public void handleStdtRegInfoBeforeCreate(CloudLog cloudLog) {
		log.info("Before Create CloudLog");
	}

	@HandleAfterCreate
	public void handleStdtRegInfoAfterCreate(CloudLog cloudLog) {
		log.info("After Create CloudLog");
	}

	@HandleBeforeDelete
	public void handleStdtRegInfoBeforeDelete(CloudLog cloudLog) {
		log.info("Before Delete CloudLog");
	}

	@HandleAfterDelete
	public void handleStdtRegInfoAfterDelete(CloudLog cloudLog) {
		log.info("After Delete CloudLog");
	}

	@HandleBeforeSave
	public void handleStdtRegInfoBeforeSave(CloudLog cloudLog) {
		log.info("Before Save CloudLog");
	}

	@HandleAfterSave
	public void handleStdtRegInfoAfterSave(CloudLog cloudLog) {
		log.info("After Save CloudLog");
	}

}
