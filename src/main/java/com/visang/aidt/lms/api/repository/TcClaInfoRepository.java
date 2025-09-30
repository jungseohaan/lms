package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.TcClaInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TcClaInfoRepository extends JpaRepository<TcClaInfoEntity, Long> {
    TcClaInfoEntity findByClaId(String claId) throws Exception;
    TcClaInfoEntity findTop1ByUserId(String userId) throws Exception;

    TcClaInfoEntity findByClaIdAndUserId(String claId, String userId) throws Exception;

    // 여러 건 조회를 위한 메소드 추가
    List<TcClaInfoEntity> findByUserId(String userId) throws Exception;
}
