package com.visang.aidt.lms.api.mq.service;

import com.visang.aidt.lms.api.mq.MessageConstants;
import com.visang.aidt.lms.api.mq.dto.query.QueryAskInfo;
import com.visang.aidt.lms.api.mq.dto.query.QueryAskLog;
import com.visang.aidt.lms.api.mq.dto.query.QueryAskMqDto;
import com.visang.aidt.lms.api.mq.mapper.bulk.QueryAskMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryAskService {

    private static final String temporaryPartnerID = "9d3959d5-5a4c-5311-8ce2-5c8e41ba6604";
    private final QueryAskMapper queryAskMapper;

    public List<QueryAskMqDto> createQueryAskMq(QueryAskInfo queryAskSetting)throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        String currentTime = formatter.format(Instant.now());

        List<QueryAskMqDto> queryAskMqList = new ArrayList<>();
        List<QueryAskInfo> queryAskInfoList = queryAskMapper.findQueryAskLog(queryAskSetting);

        for (QueryAskInfo queryAskInfo : queryAskInfoList) {
            if ("Y".equals(queryAskInfo.getAnswAt())) {
                queryAskInfo.setAnswer(true);
            }else{
                queryAskInfo.setAnswer(false);
            }
            queryAskInfo.setSatisfaction(-1);

            Optional<QueryAskMqDto> existingQueryAskMq = queryAskMqList.stream()
                    .filter(queryAskMq -> queryAskMq.getUserId().equals(queryAskInfo.getUserId()))
                    .findFirst();

            if (existingQueryAskMq.isPresent()) {
                existingQueryAskMq.get().getAskInfoList().add(queryAskInfo);
            } else {
                List<QueryAskInfo> queryAskInfoDto = new ArrayList<>();
                queryAskInfoDto.add(queryAskInfo);

                Map<String, String> ptdInfo = new LinkedHashMap<>();
                if(queryAskInfo.getUserId() != null){
                    ptdInfo = queryAskMapper.getUserInfo(queryAskInfo.getUserId());
                }

                QueryAskMqDto askQstnMqDto = QueryAskMqDto.builder()
                        .partnerId(ptdInfo.get("ptnId"))
                        .userId(queryAskInfo.getUserId())
                        .type(MessageConstants.Type.QUERY)
                        .verb(MessageConstants.Verb.ASKED)
                        .reqTime(currentTime)
                        .askInfoList(queryAskInfoDto)
                        .useTermsAgreeYn(ObjectUtils.defaultIfNull(ptdInfo.get("useTermsAgreeYn"), "N"))
                        .build();

                queryAskMqList.add(askQstnMqDto);
            }
        }

        return queryAskMqList;
    }

    public Object insertQueryAskLog(Map<String, Object> paramData) throws Exception {
        String userId = paramData.get("userId").toString();
        QueryAskLog queryAskLog = new QueryAskLog();
        queryAskLog.setUserId(userId);
        queryAskMapper.insertQueryAskLog(queryAskLog);
        Integer queryAskedId = queryAskLog.getId();

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("queryAskedId", queryAskedId);
        resultMap.put("resultMsg", "저장완료");

        return resultMap;
    }

    public Object modifyQueryAskLog(Map<String, Object> paramData) throws Exception {
        Integer queryAskedId = (Integer) paramData.get("queryAskedId");
        QueryAskInfo queryAskLog = queryAskMapper.findQueryAskLogByQueryAskedId(queryAskedId);
        Timestamp queryRegDt = queryAskLog.getTimestamp();

        // duration 계산
        ZonedDateTime queryRegZonedDateTime = queryRegDt.toInstant().atZone(ZoneId.systemDefault());
        ZonedDateTime nowZonedDateTime = Instant.now().atZone(ZoneId.systemDefault());
        long durationInSeconds = Duration.between(queryRegZonedDateTime, nowZonedDateTime).getSeconds();
        queryAskLog.setDuration((int) durationInSeconds);

        queryAskMapper.modifyQueryAskLog(queryAskLog);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("resultOk", true);
        resultMap.put("resultMsg", "수정완료");

        return resultMap;
    }

    public int modifyQueryAskTrnAt(QueryAskInfo queryAskInfo)throws Exception {
        return queryAskMapper.modifyQueryAskTrnAt(queryAskInfo);
    }



}
