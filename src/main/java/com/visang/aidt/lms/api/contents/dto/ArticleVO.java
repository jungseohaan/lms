package com.visang.aidt.lms.api.contents.dto;

import com.visang.aidt.lms.api.library.dto.LibraryVO;
import com.visang.aidt.lms.api.library.dto.LibtextVO;
import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.system.dto.baseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ArticleVO extends baseVO {

    public String id;
    public Long brand_id;

    private Boolean is_publicOpen;
    private Boolean is_editable;
    private Boolean is_active;

    private String name;
    private String description;
    private String url;
    private String image;
    private String thumbnail;
    private String questionStr;
    private String source;    // 출처
    private Long review;

    private List<String> hashTag;

    public String getHashTags() {
        if (hashTag != null && !hashTag.isEmpty()) {
            return String.join("\t", hashTag);
        } else {
            return "";
        }
    }

    public void setHashTags(String str) {
        //hashTag = (StringUtils.isNotBlank(str)) ? Arrays.asList(str.trim().split("\t")).stream().distinct().collect(Collectors.toList()) : Collections.emptyList();
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


    public Long articleCategory;

    public Long getArticleCategory() {
        if (articleCategory == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "articleCategory".equals(m.getName()))) {
            articleCategory = metaMap.stream().filter(m -> "articleCategory".equals(m.getName())).findAny().orElse(null).id;
        }
        return articleCategory;
    }

    public Long articleType;

    public Long getArticleType() {
        if (articleType == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "articleType".equals(m.getName()))) {
            articleType = metaMap.stream().filter(m -> "articleType".equals(m.getName())).findAny().orElse(null).id;
        }
        return articleType;
    }

    public Long questionType;

    public Long getQestionType() {
        if (questionType == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "questionType".equals(m.getName()))) {
            questionType = metaMap.stream().filter(m -> "questionType".equals(m.getName())).findAny().orElse(null).id;
        }
        return questionType;
    }

    public Long contentArea;

    public Long getContentArea() {
        if (contentArea == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "contentArea".equals(m.getName()))) {
            contentArea = metaMap.stream().filter(m -> "contentArea".equals(m.getName())).findAny().orElse(null).id;
        }
        return contentArea;
    }

    public Long subjectAbility;

    public Long getSubjectAbility() {
        if (subjectAbility == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "subjectAbility".equals(m.getName()))) {
            subjectAbility = metaMap.stream().filter(m -> "subjectAbility".equals(m.getName())).findAny().orElse(null).id;
        }
        return subjectAbility;
    }

    public Long curriYear;

    public Long getCurriYear() {
        if (curriYear == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriYear".equals(m.getName()))) {
            curriYear = metaMap.stream().filter(m -> "curriYear".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriYear;
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

    public Long curriGrade;

    public Long getCurriGrade() {
        if (curriGrade == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriGrade".equals(m.getName()))) {
            curriGrade = metaMap.stream().filter(m -> "curriGrade".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriGrade;
    }

    public Long curriSemester;

    public Long getCurriSemester() {
        if (curriSemester == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriSemester".equals(m.getName()))) {
            curriSemester = metaMap.stream().filter(m -> "curriSemester".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriSemester;
    }

    public Long curriBook;

    public Long getCurriBook() {
        if (curriBook == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriBook".equals(m.getName()))) {
            curriBook = metaMap.stream().filter(m -> "curriBook".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriBook;
    }

    public Long curriUnit1;

    public Long getCurriUnit1() {
        if (curriUnit1 == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriUnit1".equals(m.getName()))) {
            curriUnit1 = metaMap.stream().filter(m -> "curriUnit1".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriUnit1;
    }

    public Long curriUnit2;

    public Long getCurriUnit2() {
        if (curriUnit2 == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriUnit2".equals(m.getName()))) {
            curriUnit2 = metaMap.stream().filter(m -> "curriUnit2".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriUnit2;
    }

    public Long curriUnit3;

    public Long getCurriUnit3() {
        if (curriUnit3 == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriUnit3".equals(m.getName()))) {
            curriUnit3 = metaMap.stream().filter(m -> "curriUnit3".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriUnit3;
    }

    public Long curriUnit4;

    public Long getCurriUnit4() {
        if (curriUnit4 == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriUnit4".equals(m.getName()))) {
            curriUnit4 = metaMap.stream().filter(m -> "curriUnit4".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriUnit4;
    }

    public Long curriUnit5;

    public Long getCurriUnit5() {
        if (curriUnit5 == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "curriUnit5".equals(m.getName()))) {
            curriUnit5 = metaMap.stream().filter(m -> "curriUnit5".equals(m.getName())).findAny().orElse(null).id;
        }
        return curriUnit5;
    }


    public List<MetaVO> metaMap;

    public List<LibraryVO> libraryMap;
    public List<LibtextVO> libtextMap;

    public void clean() {
        version = 1L;
        creator = null;
        creator_id = null;
        creator_name = null;
        regdate = null;

        updater = null;
        updater_id = null;
        updater_name = null;
        updatedate = null;
    }

    // public List<ArticlePartVO> parts; //아티클 하위 정보가 아님

}
