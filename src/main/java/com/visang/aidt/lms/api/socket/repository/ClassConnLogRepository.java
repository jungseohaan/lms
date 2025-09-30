package com.visang.aidt.lms.api.socket.repository;

import com.visang.aidt.lms.api.socket.entity.ClassConnLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClassConnLogRepository extends JpaRepository<ClassConnLogEntity, Long> {
    Optional<ClassConnLogEntity> findByClassConnLogIdxAndClassIdxAndUserIdx(long classConnLogIdx, int classIdx, int UserIdx) throws Exception;
}
