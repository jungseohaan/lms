package com.visang.aidt.lms.api.contents.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.visang.aidt.lms.api.system.dto.MetaVO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SetsSearchRequestVO extends _baseContentsVO_request {

    private Boolean detail;
    private String myuid; //<!-- TODO 테스트 후  저작자 임의 검색 금지   -->

    private Boolean is_publicOpen;
    private Boolean is_editable;

    public Long id;
    public Long brand_id;

    private Long creator_id;
    private String creator;
    private String creator_name;

    private Long updater_id;
    private String updater;
    private String updater_name;

    private String regdate;
    private String updatedate;

    private Boolean is_active;
    public Boolean is_deleted;

    public String reg_sdate;        // 생성시간 검색구간 start
    public String reg_edate;        // 생성시간 검색구간 end


    //add meta_map
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

        if (setCategory != null && setCategory > 0) {
            MetaVO meta = new MetaVO();
            meta.setName("setCategory");
            meta.setId(setCategory);
            _meta.add(meta);
        }
        if (curriBook != null && curriBook > 0) {
            MetaVO meta = new MetaVO();
            meta.setName("curriBook");
            meta.setId(curriBook);
            _meta.add(meta);
        }

        if (curriUnit1 != null && curriUnit1 > 0) {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit1");
            meta.setId(curriUnit1);
            _meta.add(meta);
        }
        if (curriUnit2 != null && curriUnit2 > 0) {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit2");
            meta.setId(curriUnit2);
            _meta.add(meta);
        }
        if (curriUnit3 != null && curriUnit3 > 0) {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit3");
            meta.setId(curriUnit3);
            _meta.add(meta);
        }
        if (curriUnit4 != null && curriUnit4 > 0) {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit4");
            meta.setId(curriUnit4);
            _meta.add(meta);
        }
        if (curriUnit5 != null && curriUnit5 > 0) {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit5");
            meta.setId(curriUnit5);
            _meta.add(meta);
        }
        _meta = _meta.stream().distinct().collect(Collectors.toList());
        return _meta;

    }

    public void setMetaList(List<MetaVO> metaList) {

    }
}
