package com.visang.aidt.lms.api.repository.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * packageName : com.visang.aidt.lms.api.repository.entity
 * fileName : TabInfoEntity
 * USER : kil80
 * date : 2024-01-13
 * ===============================================
 * DATE            USER               NOTE
 * -----------------------------------------------
 * 2024-01-13         kil80          최초 생성
 */
@Getter
@Setter
@Entity
@Table(name = "tab_info")
public class TabInfoEntity {
    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 작성자 ID
    @Column(name = "wrter_id")
    private String wrterId;

    // 학급 ID
    @Column(name = "cla_id")
    private String claId;

    // 교과서 ID
    @Column(name = "textbk_id")
    private Long textbkId;

    // 커리큘럼 key
    @Column(name = "crcul_id")
    private Long crculId;

    // 탭명
    @Column(name = "tab_nm")
    private String tabNm;

    // 탭순서
    @Column(name = "tab_seq")
    private int tabSeq;

    // 셋트지 정보
    @OneToOne
    @JoinColumn(name = "sets_id", referencedColumnName = "id" )
    private SetsEntity set;

    // 사용여부(Y/N)
    @Column(name = "use_at")
    private String useAt;

    // 노출여부(Y/N)
    @Column(name = "expos_at")
    private String exposAt;

    // 탭추가여부  Y: 추가, N:기본
    @Column(name = "tab_add_at")
    private String tabAddAt;

    // 등록자
    @Column(name = "rgtr")
    private String rgtr;

    // 등록일자
    @Column(name = "reg_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date regDt;

    // 수정자
    @Column(name = "mdfr")
    private String mdfr;

    // 수정일시
    @Column(name = "mdfy_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date mdfyDt;

    @Transient
    private Integer userId;

    @Transient
    private Long tabId;

    // 커리큘럼
    @Transient
    private TcCurriculumEntity tcCurriculum;
}
