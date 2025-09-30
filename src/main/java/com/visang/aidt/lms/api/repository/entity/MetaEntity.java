package com.visang.aidt.lms.api.repository.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "aidt_lcms.meta")
public class MetaEntity {
    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /** 코드(KEY). 계층관계인경우 '상위코드-하위코드'로 표시 */
    @Column(name = "code")
    private String code;
    /** 이름 */
    @Column(name = "name")
    private String name;
    /** 설명 */
    @Column(name = "description")
    private String description;
    /** 값 */
    @Column(name = "val")
    private String val;
    /** 계층구조의 계층레벨 */
    @Column(name = "depth")
    private Integer depth;
    /** 단원정보의 차시레벨 */
    @Column(name = "max_depth")
    private Integer maxDepth;
    @Column(name = "is_active")
    /** 사용여부 */
    private Boolean isActive;
    @Column(name = "parent_id")
    /** 상위번호(계층) */
    private Long parentId;

    @Transient
    private String setsId;

    @Transient
    private String articleId;
}
