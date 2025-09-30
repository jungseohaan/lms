package com.visang.aidt.lms.api.library.dto;

import java.util.List;

import com.visang.aidt.lms.api.contents.dto.ArticleVO;
import com.visang.aidt.lms.api.system.dto.MetaVO;
import com.visang.aidt.lms.api.system.dto.baseVO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class LibtextVO extends baseVO {

	Long id;

    String type_1;
    String type_2;


    Long 	brand_id;
    Long 	version;

    Boolean is_active;
    Boolean is_deleted;
    Boolean is_publicOpen;

    String name;
    String parts;
    String description;

    String contents_entry;
    String contents_multiLang;
    String contents_def;
    String contents_pron;
    String contents_image;
    String contents_audio;
    String contents_video;


    //문장인 경우
    String chunk;
    String info1; // "구문분석 정보"
    String info2; // "분절 정보"
    String key_desc; // "Key Grammar desc"
    String key_info; // ""
    String key_title; //"Key Grammar title"
    String key_type; //"Grammar"
    String speaker; //""


    Long 	creator_id;
    String 	creator;
    String 	creator_name;
    String 	regdate;

    Long 	updater_id;
    String 	updater;
    String 	updater_name;
    String 	updatedate;

    Long 	curriBook;
    Long 	curriUnit1;
    Long 	curriUnit2;
    Long 	curriUnit3;
    Long 	curriUnit4;
    Long 	curriUnit5;

 	List<ArticleVO> articles;
   	List<LibtextVO> exam_sentences;
   	List<MetaVO> studyMap;

    List<MetaVO> metaMap;
    List<LibtextScriptVO> scripts;
    //List<LibtextSubtitlesVO> subtitleList;
    List<LibtextVO> medias;

    Long full_count;
    Long open_count;



}
