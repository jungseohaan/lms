package com.visang.aidt.lms.api.contents.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.visang.aidt.lms.api.system.dto.MetaVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TemplateSearchRequestVO extends _baseContentsVO_request {

    private String myuid; //<!--  TODO 테스트 후  저작자 임의 검색 금지   -->

    public Long id;
    public Long brand_id;

    private Boolean is_publicOpen;
    private Boolean is_editable;
    private Boolean is_active;
    private Boolean is_signs;

    private Long creator_id;
    private String creator;
    private String creator_name;

    private Long updater_id;
    private String updater;
    private String updater_name;


    private String regdate;
    private String updatedate;


    public String reg_sdate;        // 생성시간 검색구간 start
    public String reg_edate;        // 생성시간 검색구간 end


    //디폴트값인것은 쿼리결과에 포함시킴.
    public Boolean brand_id_default;
    public Boolean articleType_default;


    //add meta_map
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


    private List<MetaVO> metaMap;
    private List<MetaVO> _meta;

    public List<MetaVO> getMetaList() {
        if (_meta != null && _meta.size() > 0) return _meta;
        if (_meta == null) _meta = new ArrayList<MetaVO>();
        if (this.metaMap != null) {
            _meta.addAll(metaMap.stream().distinct().collect(Collectors.toList()));
            //_meta.addAll(metaMap);
            //_meta.addAll(meta.stream().map(k->k.getId()).collect(Collectors.toList()));
        }

        if (articleType != null && articleType > 0) {
            MetaVO meta = new MetaVO();
            meta.setName("articleType");
            meta.setId(articleType);
            _meta.add(meta);
        }
        if (curriSchool != null && curriSchool > 0) {
            MetaVO meta = new MetaVO();
            meta.setName("curriSchool");
            meta.setId(curriSchool);
            _meta.add(meta);
        }
        if (curriSubject != null && curriSubject > 0) {
            MetaVO meta = new MetaVO();
            meta.setName("curriSubject");
            meta.setId(curriSubject);
            _meta.add(meta);
        }

        _meta = _meta.stream().distinct().collect(Collectors.toList());
        return _meta;

    }

    public void setMetaList(List<MetaVO> metaList) {

    }
}
