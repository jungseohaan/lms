package com.visang.aidt.lms.api.contents.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.visang.aidt.lms.api.system.dto.MetaVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SetsSaveVO extends SetsExVO {

    private List<MetaVO> _meta;
    public List<MetaVO> getMetaList()
    {
        if(_meta != null && _meta.size() > 0) return _meta;
        if(_meta == null) _meta = new ArrayList<MetaVO>();
        if(this.metaMap != null)
        {
            metaMap.removeIf(m -> m == null);
            _meta.addAll(metaMap.stream().distinct().collect(Collectors.toList()));
            //_meta.addAll(metaMap);
            //_meta.addAll(meta.stream().map(k->k.getId()).collect(Collectors.toList()));
        }
        if(difficulty != null && difficulty > 0)
        {
            MetaVO meta = new MetaVO();
            meta.setName("difficulty");
            meta.setId(difficulty);
            _meta.add(meta);
        }

        if(setCategory != null && setCategory > 0)
        {
            MetaVO meta = new MetaVO();
            meta.setName("setCategory");
            meta.setId(setCategory);
            _meta.add(meta);
        }
        if(curriBook != null && curriBook > 0)
        {
            MetaVO meta = new MetaVO();
            meta.setName("curriBook");
            meta.setId(curriBook);
            _meta.add(meta);
        }

        if(curriUnit1 != null &&curriUnit1 > 0)
        {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit1");
            meta.setId(curriUnit1);
            _meta.add(meta);
        }
        if(curriUnit2 != null &&curriUnit2 > 0)
        {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit2");
            meta.setId(curriUnit2);
            _meta.add(meta);
        }
        if(curriUnit3 != null &&curriUnit3 > 0)
        {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit3");
            meta.setId(curriUnit3);
            _meta.add(meta);
        }
        if(curriUnit4 != null &&curriUnit4 > 0)
        {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit4");
            meta.setId(curriUnit4);
            _meta.add(meta);
        }
        if(curriUnit5 != null &&curriUnit5 > 0)
        {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit5");
            meta.setId(curriUnit5);
            _meta.add(meta);
        }


        if(curriUnit6  != null &&curriUnit6 > 0)
        {
            MetaVO meta = new MetaVO();
            meta.setName("curriUnit6");
            meta.setId(curriUnit6);
            _meta.add(meta);
        }
        _meta = _meta.stream().distinct().collect(Collectors.toList());
        return _meta;

    }
    public Long curriUnit6;	 //컬럼에 6 없음. 입력이 있다면 meta에 저장만...
    public void setMetaList(List<MetaVO> metaList)
    {

    }
}
