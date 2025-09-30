package com.visang.aidt.lms.api.contents.dto;

import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.system.dto.baseVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
@EqualsAndHashCode(callSuper = false)
public class SetsVO extends baseVO {

    public Long gen_id;
    public String id;
    public Long brand_id;


    private Boolean is_publicOpen;
    private Boolean is_editable;
    private Boolean is_active;
    public Boolean is_deleted;


    private String name;
    private String description;
    //private String url;
    private String thumbnail;

    private List<String> hashTag;

    public String getHashTags() {
        if (hashTag != null) {
            return String.join("\t", hashTag);
        } else {
            return "";
        }
    }

    public void setHashTags(String str) {
        if (str != null) {
            hashTag = Arrays.asList(str.trim().split("\t")).stream().distinct().collect(Collectors.toList());
        }
    }

    String points_type;    // 배점 방식 'auto'|'manual';
    Long points;            //총 배점
    String limit_time_type;    // 제한시간 방식 'total'|'individual';
    Long limit_time;        //총 제한 시간


    public Long version;


    public Long creator_id;
    public String creator;
    public String creator_name;
    public String regdate;

    public Long updater_id;
    public String updater;
    public String updater_name;
    public String updatedate;

    public int creator_ty;


    private Long full_count;
    private Long open_count;


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


    public Long difficulty;

    public Long getDifficulty() {
        if (difficulty == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "difficulty".equals(m.getName()))) {
            difficulty = metaMap.stream().filter(m -> "difficulty".equals(m.getName())).findAny().orElse(null).id;
        }
        return difficulty;
    }


    public Long setCategory;

    public Long getSetCategory() {
        if (setCategory == null && metaMap != null && metaMap.size() > 0
                && metaMap.stream().anyMatch(m -> "setCategory".equals(m.getName()))) {
            setCategory = metaMap.stream().filter(m -> "setCategory".equals(m.getName())).findAny().orElse(null).id;
        }
        return setCategory;
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
//      public Long curriUnit6;
//      public Long getCurriUnit6()
//      {
//      	if(curriUnit6 == null && metaMap != null && metaMap.size() > 0
//      			&& metaMap.stream().anyMatch(m-> "curriUnit6".equals(m.getName())))
//      	{
//      		curriUnit6 = metaMap.stream().filter(m -> "curriUnit6".equals(m.getName())).findAny().orElse(null).id;
//      	}
//      	return curriUnit6;
//      }

    public List<MetaVO> metaMap;
    public List<SetsArticleInfoVO> articles;

}
