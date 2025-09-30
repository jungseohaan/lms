package com.visang.aidt.lms.api.library.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.system.dto.baseVO_request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LibrarySearchRequestVO extends baseVO_request {

	private Boolean detail;
	private String myuid; //<!-- TODO 테스트 후  저작자 임의 검색 금지   -->


	public Long id;




    public String type_1;
    public String type_2;

    public String description;
    public Long 	brand_id;


    public String name;
    public String url;
    public String thumbnail;


    public String used_in;
    public Long version;
    public Boolean is_deleted;
    private Boolean is_active;
    private Boolean is_publicOpen;


    private Long creator_id;
    private String creator;
    private String creator_name;
    private String regdate;

    public Long updater_id;
    public String updater;
    public String updater_name;
    private String updatedate;


	public String reg_sdate;		// 라이브러리 생성시간 검색구간 start
	public String reg_edate;		// 라이브러리 생성시간 검색구간 end


    public String search;
    public String getSearch()
	{
    	if(search != null && search != "")
			return "%"+search+"%";
		else
			return "";
	}


    //add meta_map
    public Long curriYear;
    public Long getCurriYear()
    {
    	if(curriYear == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriYear".equals(m.getName())))
    	{
    		curriYear = metaMap.stream().filter(m -> "curriYear".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriYear;
    }
    public Long curriSchool;
    public Long getCurriSchool()
    {
    	if(curriSchool == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriSchool".equals(m.getName())))
    	{
    		curriSchool = metaMap.stream().filter(m -> "curriSchool".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriSchool;
    }
    public Long curriSubject;
    public Long getCurriSubject()
    {
    	if(curriSubject == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriSubject".equals(m.getName())))
    	{
    		curriSubject = metaMap.stream().filter(m -> "curriSubject".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriSubject;
    }
    public Long curriGrade;
    public Long getCurriGrade()
    {
    	if(curriGrade == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriGrade".equals(m.getName())))
    	{
    		curriGrade = metaMap.stream().filter(m -> "curriGrade".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriGrade;
    }
    public Long curriSemester;
    public Long getCurriSemester()
    {
    	if(curriSemester == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriSemester".equals(m.getName())))
    	{
    		curriSemester = metaMap.stream().filter(m -> "curriSemester".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriSemester;
    }
    public Long curriBook;
    public Long getCurriBook()
    {
    	if(curriBook == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriBook".equals(m.getName())))
    	{
    		curriBook = metaMap.stream().filter(m -> "curriBook".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriBook;
    }

    public Long curriUnit1;
    public Long getCurriUnit1()
    {
    	if(curriUnit1 == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriUnit1".equals(m.getName())))
    	{
    		curriUnit1 = metaMap.stream().filter(m -> "curriUnit1".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriUnit1;
    }
    public Long curriUnit2;
    public Long getCurriUnit2()
    {
    	if(curriUnit2 == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriUnit2".equals(m.getName())))
    	{
    		curriUnit2 = metaMap.stream().filter(m -> "curriUnit2".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriUnit2;
    }
    public Long curriUnit3;
    public Long getCurriUnit3()
    {
    	if(curriUnit3 == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriUnit3".equals(m.getName())))
    	{
    		curriUnit3 = metaMap.stream().filter(m -> "curriUnit3".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriUnit3;
    }
    public Long curriUnit4;
    public Long getCurriUnit4()
    {
    	if(curriUnit4 == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriUnit4".equals(m.getName())))
    	{
    		curriUnit4 = metaMap.stream().filter(m -> "curriUnit4".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriUnit4;
    }
    public Long curriUnit5;
    public Long getCurriUnit5()
    {
    	if(curriUnit5 == null && metaMap != null && metaMap.size() > 0
    			&& metaMap.stream().anyMatch(m-> "curriUnit5".equals(m.getName())))
    	{
    		curriUnit5 = metaMap.stream().filter(m -> "curriUnit5".equals(m.getName())).findAny().orElse(null).id;
    	}
    	return curriUnit5;
    }

    public List<FileVO> files;
    public List<MetaVO> metaMap;
    public List<MetaVO> _meta;
    public List<MetaVO> getMetaList()
	{
		 if(_meta != null) return _meta;
		 if(_meta == null) _meta = new ArrayList<MetaVO>();
		 if(this.metaMap != null)
		 {
			 _meta.addAll(metaMap.stream().distinct().collect(Collectors.toList()));
			 //_meta.addAll(metaMap);
			 //_meta.addAll(meta.stream().map(k->k.getId()).collect(Collectors.toList()));
		 }

		 if(curriYear != null && curriYear > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriYear");
			 meta.setId(curriYear);
			 _meta.add(meta);
		 }
		 if(curriSchool != null && curriSchool > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriSchool");
			 meta.setId(curriSchool);
			 _meta.add(meta);
		 }
		 if(curriSubject != null && curriSubject > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriSubject");
			 meta.setId(curriSubject);
			 _meta.add(meta);
		 }
		 if(curriGrade != null && curriGrade > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriGrade");
			 meta.setId(curriGrade);
			 _meta.add(meta);
		 }
		 if(curriSemester != null && curriSemester > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriSemester");
			 meta.setId(curriSemester);
			 _meta.add(meta);
		 }

		 if(curriBook != null && curriBook > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriBook");
			 meta.setId(curriBook);
			 _meta.add(meta);
		 }
		 if(curriUnit1 != null && curriUnit1 > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriUnit1");
			 meta.setId(curriUnit1);
			 _meta.add(meta);
		 }
		 if(curriUnit2 != null && curriUnit2 > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriUnit2");
			 meta.setId(curriUnit2);
			 _meta.add(meta);
		 }
		 if(curriUnit3 != null && curriUnit3 > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriUnit3");
			 meta.setId(curriUnit3);
			 _meta.add(meta);
		 }
		 if(curriUnit4 != null && curriUnit4 > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriUnit4");
			 meta.setId(curriUnit4);
			 _meta.add(meta);
		 }
		 if(curriUnit5 != null && curriUnit5 > 0)
		 {
			 MetaVO meta = new MetaVO();
			 meta.setName("curriUnit5");
			 meta.setId(curriUnit5);
			 _meta.add(meta);
		 }
		 _meta = _meta.stream().distinct().collect(Collectors.toList());
		 return _meta;
	}
    public void setMetaList(List<MetaVO> metaList)
	 {

	 }


}

