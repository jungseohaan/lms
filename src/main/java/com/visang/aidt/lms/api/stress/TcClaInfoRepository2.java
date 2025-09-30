package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.repository.entity.TcClaInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TcClaInfoRepository2 extends JpaRepository<TcClaInfoEntity2, Long> {
    TcClaInfoEntity2 findByClaId(String claId) throws Exception;
    TcClaInfoEntity2 findTop1ByUserId(String userId) throws Exception;

    TcClaInfoEntity2 findByClaIdAndUserId(String claId, String userId) throws Exception;
}
