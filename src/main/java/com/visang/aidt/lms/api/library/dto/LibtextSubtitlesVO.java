package com.visang.aidt.lms.api.library.dto;

import com.visang.aidt.lms.api.system.dto.baseVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LibtextSubtitlesVO extends baseVO {

    public Long 	id;
//    public Long 	libtext_id;
//    public Long 	libtextScript_id;

    private String 	entry;
    private String 	entryKey;

    private String 	eng;	//영어
    private String 	kor; 	//한국
    private String 	jpn; 	//일본
    private String 	ch1;	//중국어 간체
    private String 	ch2;	//중국어 번체
    private String 	vtn;	//베트남어
    private String 	spa;	//스페인어
    private String 	por;	//포르투갈어
    private String 	ara;	//아랍어


}
