package com.visang.aidt.lms.api.contents.dto;

import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.system.dto.baseVO;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)

public class TemplateVO extends baseVO {

    public Long id;
    public Long brand_id;

    private Boolean is_publicOpen;
    private Boolean is_editable;
    private Boolean is_active;
    private Boolean is_signs;

    private String name;
    private String description;
    private String url;
    private String image;
    private String thumbnail;
    public List<String> hashTag;

    public String getHashTags() {
        if (hashTag != null && !hashTag.isEmpty()) {
            return String.join("\t", hashTag);
        } else {
            return "";
        }
    }

    public void setHashTags(String str) {
        hashTag = (StringUtils.isNotBlank(str)) ? Arrays.asList(str.trim().split("\t")).stream().distinct().collect(Collectors.toList()) : Collections.emptyList();
    }

    private Long version;

    private Long creator_id;
    private String creator;
    private String creator_name;
    private String regdate;
    private Long updater_id;
    private String updater;
    private String updater_name;
    private String updatedate;


    private Long full_count;
    private Long open_count;

    public Long articleType;

    public Long getArticleType() {
        if (articleType == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "articleType".equals(m.getName()))) {
            articleType = metaMap.stream().filter(m -> "articleType".equals(m.getName())).findAny().orElse(null).id;
        }
        return articleType;
    }

    public Long curriSchool;

    public Long getCurriSchool() {
        if (curriSchool == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriSchool".equals(m.getName()))) {
            curriSchool = metaMap.stream().filter(m -> "curriSchool".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriSchool;
    }

    public Long curriSubject;

    public Long getCurriSubject() {
        if (curriSubject == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriSubject".equals(m.getName()))) {
            curriSubject = metaMap.stream().filter(m -> "curriSubject".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriSubject;
    }

    public List<MetaVO> metaMap;


}
