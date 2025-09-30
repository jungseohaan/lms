package com.visang.aidt.lms.api.socket.service;

import com.visang.aidt.lms.api.repository.CloudLogRepository;
import com.visang.aidt.lms.api.repository.UserRepository;
import com.visang.aidt.lms.api.repository.entity.CloudLog;
import com.visang.aidt.lms.api.repository.entity.User;
import com.visang.aidt.lms.api.repository.specification.CloudLogSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudLogService {

    private final CloudLogRepository cloudLogRepository;
    private final UserRepository userRepository;

    public void insertWriteLog(CloudLog cloudLog)throws Exception {
        cloudLogRepository.save(cloudLog);
    }

    public void updateWriteLog(CloudLog cloudLog, Integer idx) throws Exception {
        cloudLog.setIdx(idx);
        cloudLogRepository.save(cloudLog);
    }

    @Transactional(readOnly = true)
    public List<CloudLog> getLogList(Map<String, Object> requestMap) throws Exception {

        Specification<CloudLog> spec = (root, query, criteriaBuilder) -> null;

        String user_id = MapUtils.getString(requestMap, "user_id");
        String search_sdate = MapUtils.getString(requestMap, "search_sdate");
        String search_edate = MapUtils.getString(requestMap, "search_edate");
        String search_str = MapUtils.getString(requestMap, "search_str");
        String logDiv = MapUtils.getString(requestMap, "logDiv");
        String level = MapUtils.getString(requestMap, "level");
        String serverName = MapUtils.getString(requestMap, "serverName");

        if (StringUtils.isNotEmpty(user_id)) {
            User user = userRepository.findByUserId(user_id);
            if (user == null) {
                return new ArrayList<>();
            }
            Long userIdx = ObjectUtils.defaultIfNull(user.getId(), 0L);
            if (userIdx == 0) {
                return new ArrayList<>();
            }
            spec = spec.and(CloudLogSpecification.equal("userIdx", userIdx.toString()));
            if (StringUtils.isNotEmpty(search_sdate)) {
                spec = spec.and(CloudLogSpecification.greaterThanOrEqualTo("regDate", search_sdate));
            }
            if (StringUtils.isNotEmpty(search_edate)) {
                spec = spec.and(CloudLogSpecification.greaterThanOrEqualTo("regDate", search_edate));
            }
        } else {
            spec = spec.and(CloudLogSpecification.greaterThanOrEqualTo("regDate", search_sdate))
                    .and(CloudLogSpecification.lessThanOrEqualTo("regDate", search_edate));
        }

        if (StringUtils.isNotEmpty(search_str)) {
            spec = spec.and(CloudLogSpecification.leftRightLike("message", search_str));
        }
        if (StringUtils.isNotEmpty(logDiv)) {
            spec = spec.and(CloudLogSpecification.equal("logDiv", logDiv));
        }
        if (StringUtils.isNotEmpty(level)) {
            spec = spec.and(CloudLogSpecification.equal("logLevel", level));
        }
        if (StringUtils.isNotEmpty(serverName)) {
            spec = spec.and(CloudLogSpecification.leftRightLike("serverName", serverName));
        }

        List<CloudLog> list = cloudLogRepository.findAll(spec);

        return list;
    }
}
