package com.visang.aidt.lms.api.repository;

import com.visang.aidt.lms.api.repository.entity.TcClaMbInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TcClaMbInfoRepository extends JpaRepository<TcClaMbInfoEntity, Long> {

    List<TcClaMbInfoEntity> findByClaId(String claId) throws Exception;
    List<TcClaMbInfoEntity> findByClaIdAndActvtnAt(String claId, String actvtnAt) throws Exception;
    TcClaMbInfoEntity findByUserIdAndStdtId(String userId, String stdtId) throws Exception;
    List<TcClaMbInfoEntity> findByStdtId(String stdtId) throws Exception;
    List<TcClaMbInfoEntity> findByStdtIdAndActvtnAt(String stdtId, String actvtnAt);

}
