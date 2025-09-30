package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

// If you do not want to expose this repository as REST, remove comment below.
//@RepositoryRestResource(exported = false)
@RepositoryRestResource(path = "users")
public interface UserRepository extends JpaRepository<User, Long> {

	// userId로 조회
    User findByUserId(String userId) throws Exception;
    // userId로 userSeCd 값을 조회
    User findUserSeCdByUserId(String userId) throws Exception;
}
