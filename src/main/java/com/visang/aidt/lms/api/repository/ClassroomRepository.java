package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, String> {
    Optional<Classroom> findByClaCd(String claCd) throws Exception;
}
