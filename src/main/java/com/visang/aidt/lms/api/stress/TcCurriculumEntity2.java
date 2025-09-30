package com.visang.aidt.lms.api.stress;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.visang.aidt.lms.api.repository.entity.TabInfoEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "stress_tc_curriculum")
public class TcCurriculumEntity2 {
    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 교사 ID
    @Column(name = "wrter_id")
    private String wrterId;

    // 학급 ID
    @Column(name = "cla_id")
    private String claId;

    // 교과서 ID
    @Column(name = "textbk_id")
    private Long textbkId;

    // 목차 ID
    @Column(name = "textbk_idx_id")
    private Long textbkIdxId;

    // 순서
    @Column(name = "order")
    private Integer order;

    // 커리큘럼 key
    @Column(name = "key")
    private Long key;

    // 커리큘럼 parent key
    @Column(name = "parent")
    private Long parent;

    @Column(name = "depth")
    private Integer depth;

    // PDF 페이지 번호
    @Column(name = "startPage")
    private Integer startPage;

    // PDF 페이지 번호
    @Column(name = "endPage")
    private Integer endPage;

    // 내용
    @Column(name = "text")
    private String text;

    // 사용여부
    @Column(name = "use_at")
    private String useAt;

    // 삭제여부
    @Column(name = "del_at")
    private String delAt;

    // 컨텐츠 등록 가능 여부
    // - 하위에 tab 존재하는 경우
    @Column(name = "addcon_at")
    private String addconAt;

    @Column(name = "curriUnit1")
    private Long curriUnit1;

    @Column(name = "curriUnit2")
    private Long curriUnit2;

    @Column(name = "curriUnit3")
    private Long curriUnit3;

    @Column(name = "curriUnit4")
    private Long curriUnit4;

    @Column(name = "curriUnit5")
    private Long curriUnit5;

    @Column(name = "rgtr")
    private String rgtr;

    @Column(name = "reg_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date regDt;

    @Column(name = "mdfr")
    private String mdfr;

    @Column(name = "mdfy_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Date mdfyDt;

    @Transient
    private List<TabInfoEntity> textbookTabList = List.of();

    @Transient
    private String userId;

    @Transient
    private Long crculId;
}
