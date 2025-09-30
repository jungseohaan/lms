package com.visang.aidt.lms.api.mq.mapper.bulk;

import com.visang.aidt.lms.api.mq.dto.query.QueryAskInfo;
import com.visang.aidt.lms.api.mq.dto.query.QueryAskLog;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface QueryAskMapper {
    List<QueryAskInfo>findQueryAskLog(QueryAskInfo queryAskInfo);
    QueryAskInfo findQueryAskLogByQueryAskedId(Integer queryAskedId);
    void insertQueryAskLog(QueryAskLog queryAskLog);
    void modifyQueryAskLog(QueryAskInfo queryAskInfo);
    int modifyQueryAskTrnAt(QueryAskInfo queryAskInfo);
    Map<String, String> getUserInfo(String userId);
}
