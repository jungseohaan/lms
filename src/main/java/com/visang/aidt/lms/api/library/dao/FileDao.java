package com.visang.aidt.lms.api.library.dao;


import com.visang.aidt.lms.api.library.dto.FileDto;
import com.visang.aidt.lms.api.library.dto.FileLogDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FileDao {

	void insertUploadFile(FileDto fileDto);
	String selectExistsFileUuid(FileDto fileDto);
	FileDto selectFileInfo(FileDto fileDto);
	FileDto selectFileInfoWithPionada(FileDto fileDto);
	List<Map<String, Object>> selectFileDgnssFileList(Map<String, Object> param);
	FileDto selectFileInfoWithPartnerActivity(FileDto fileDto);
    void insertDownloadLog(FileLogDto fileLogDto);

	List<FileDto> selectFileInfoList();
	void updateFileInfoList(List<FileDto> fileDtoList);

	List<String> selectTcListFromCreator(Map<String, Object> map);
	String selectFileAuthStudent(Map<String, Object> map);

	Map<String, Object> selectTcDgnssInfoWithId(Map<String, Object> param);
}
