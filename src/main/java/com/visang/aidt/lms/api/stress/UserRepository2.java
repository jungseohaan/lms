package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "users2")
public interface UserRepository2 extends JpaRepository<User2, Long> {

	// userId로 조회
    User2 findByUserId(String userId) throws Exception;
}
