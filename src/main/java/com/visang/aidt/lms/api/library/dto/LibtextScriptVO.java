package com.visang.aidt.lms.api.library.dto;

import com.visang.aidt.lms.api.contents.dto.ArticleVO;
import com.visang.aidt.lms.api.system.dto.baseVO;
import com.visang.aidt.lms.api.utility.utils.CustomLokiLog;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class LibtextScriptVO extends baseVO {

    Long 	id;
    Long 	libtext_id;
    Long 	pkey;
    Long 	skey;



    String 	speaker;
    String 	entry;
    String 	multiLang;

    //String 	subtitles;

    String 	startTime;
    String 	endTime;
    String 	summary;
    String 	akey; //article "1,2,3"

    Long 	start;
    Long 	end;
    Boolean  no_line_break;

    List<ArticleVO> articles;

    public void setAkey(String s)
    {
    	if(s == null) return;
    	akey = s.replaceAll("[^0-9,]", "");;
    	List<String> articleIds = Arrays.asList(akey.split(",")).stream().distinct().collect(Collectors.toList());
		if(articleIds != null && articleIds.size() > 0)
		{
			articles = new ArrayList<ArticleVO>();
			articleIds.forEach(k->{
				try
				{
					ArticleVO article = new ArticleVO();
					article.setId(k);
					articles.add(article);
				}catch(Exception ee) {
                    log.error(CustomLokiLog.errorLog(ee));
                }
			});
		}
    }


    //private List<LibtextSubtitlesVO> subtitleList;


//    aKey: "924"
//	endTime: "11"
//	entry: "ENTRY"
//	id: -1
//	multiLang: "엔트리"
//	pKey: "1"  >> script_pk
//	sKey: "" >> script_sk
//	speaker: "SPEAKER 1"
//	startTime: "1"
//	summary: "Summary"
//	type: ""
}
