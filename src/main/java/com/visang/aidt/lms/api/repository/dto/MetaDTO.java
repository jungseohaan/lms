package com.visang.aidt.lms.api.repository.dto;

import com.visang.aidt.lms.api.repository.entity.MetaEntity;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetaDTO {
    /** ID */

    private Long id;
    /** 코드(KEY). 계층관계인경우 '상위코드-하위코드'로 표시 */

    private String code;
    /** 이름 */

    private String name;
    /** 설명 */

    private String description;
    /** 값 */

    private String val;
    /** 계층구조의 계층레벨 */

    private Integer depth;
    /** 단원정보의 차시레벨 */

    private Integer maxDepth;

    /** 사용여부 */
    private Boolean isActive;

    /** 상위번호(계층) */
    private Long parentId;

    public static MetaDTO toDTO(MetaEntity entity) {
        return MetaDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .val(entity.getVal())
                .isActive(entity.getIsActive())
                .build();
    }
}
