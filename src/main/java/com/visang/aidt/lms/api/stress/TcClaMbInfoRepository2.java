package com.visang.aidt.lms.api.stress;

import com.visang.aidt.lms.api.repository.entity.TcClaMbInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TcClaMbInfoRepository2 extends JpaRepository<TcClaMbInfoEntity2, Long> {

    List<TcClaMbInfoEntity2> findByClaId(String claId) throws Exception;
    TcClaMbInfoEntity2 findByUserIdAndStdtId(String userId, String stdtId) throws Exception;
    List<TcClaMbInfoEntity2> findByStdtId(String stdtId) throws Exception;
}
