package com.visang.aidt.lms.api.dashboard.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visang.aidt.lms.api.dashboard.mapper.EtcMapper;
import com.visang.aidt.lms.api.dashboard.model.VivaClassApiDto;
import com.visang.aidt.lms.api.dashboard.model.VivaClassWithdrawDto;
import com.visang.aidt.lms.api.dashboard.service.EtcService;
import com.visang.aidt.lms.api.integration.mapper.IntegPublishMapper;
import com.visang.aidt.lms.api.log.service.BtchExcnLogService;
import com.visang.aidt.lms.api.integration.vo.WithdrawUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@Component
@Profile({"vs-math-develop-job", "vs-engl-develop-job", "vs-math-prod-job", "vs-engl-prod-job", "local"})
public class EtcVivaClassUserWithdrawBatchJob {

    private final IntegPublishMapper integPublishMapper;
    private final BtchExcnLogService btchExcnLogService;
    private final EtcService etcService;
    private final EtcMapper etcMapper;

    @Value("${cloud.aws.nas.path}")
    private String nasPath;

    @Value("${spring.profiles.active}")
    private String serverEnv;

    @Scheduled(cron = "${batch-job.schedule.EtcBatchJob.executeVivaClassDgnssUserWithdraw}")
    public void executeVivaClassUserWithdraw() throws Exception {
        String btchNm = "EtcBatchJob.executeVivaClassDgnssUserWithdraw";
        Map<String, Object> resultOfBatchInfoExist = btchExcnLogService.checkBatchInfoExist(btchNm);

        Boolean isBatchInfoExist = (Boolean) resultOfBatchInfoExist.get("resultOk");
        if (isBatchInfoExist) {
            // 배치 실행 로그 기록
            Map<String, Object> resultOfCreateBtchExcnLog = btchExcnLogService.createBtchExcnLog(resultOfBatchInfoExist.get("btchId").toString());

            // 배치 실행 로그 생성 성공 여부 확인
            Boolean isCreateBtchExcnLog = (Boolean) resultOfCreateBtchExcnLog.get("resultOk");
            if (isCreateBtchExcnLog) {
                Map<String, Object> batchparamData = new HashMap<>();
                batchparamData.put("btchDetId", resultOfBatchInfoExist.get("btchDetId"));

                String vivaClassUrl = "";
                String token = "";
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                if (StringUtils.equals(serverEnv, "vs-engl-develop-job") || StringUtils.equals(serverEnv, "vs-math-develop-job") || StringUtils.equals(serverEnv, "local")) {
                    vivaClassUrl = "https://dev-vivaclassapi.vivasam.com";
                } else if (StringUtils.equals(serverEnv, "vs-engl-prod-job") || StringUtils.equals(serverEnv, "vs-math-prod-job")) {
                    vivaClassUrl = "https://vivaclassapi.vivasam.com";
                }
                if (StringUtils.isNotEmpty(vivaClassUrl)) {
                    Map<String, Object> param = new HashMap<>();
                    String makeJwtTokenUrl = "/api/auth/login";
                    param.put("id", "metapsycho");
                    param.put("accessKey", "MDTMPmBACTNcYGsx+Pfk1lPDlBrCveACPzwtz1cPHBmv6KKhAZg+ikyD4/A/TCnKvFeD6GeLS5889Ic7HjwYv4TlpmpgNLUclPSoqkxe0ac=");

                    VivaClassApiDto vivaApiResponse = etcService.vivaClassApiCall(vivaClassUrl + makeJwtTokenUrl, param, headers);
                    if (StringUtils.equals(vivaApiResponse.getCode(), "-1")) {
                        batchparamData.put("failDc", "vivaclass token api fail : " + param.toString()); //실패사유
                        batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                        return;
                    }
                    token = "Bearer " + vivaApiResponse.getResponse();
                    headers.set("Authorization", token);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
                    String startDt = LocalDate.now().minusDays(1).format(formatter);

                    Map<String, Object> withDrawParam = new HashMap<>();
                    withDrawParam.put("startDt", startDt);
                    withDrawParam.put("endDt", startDt);

                    // 교사 학급 정보 호출
                    VivaClassApiDto vivaClassApiResponse = etcService.vivaClassApiCall(vivaClassUrl + "/api/metapsycho/member/withdraw/list", withDrawParam, headers);
                    if (StringUtils.equals(vivaClassApiResponse.getCode(), "-1")) {
                        batchparamData.put("failDc", "vivaclass teacher token api fail : " + withDrawParam.toString()); //실패사유
                        batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                        return;
                    }
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<VivaClassWithdrawDto> vivaClassWithdrawDtoList = new ArrayList<>();
                    if (vivaClassApiResponse.getResponse() instanceof List<?>) {
                        vivaClassWithdrawDtoList = ((List<?>) vivaClassApiResponse.getResponse()).stream()
                                .map(item -> objectMapper.convertValue(item, VivaClassWithdrawDto.class))
                                .collect(Collectors.toList());
                    } else {
                        batchparamData.put("failDc", "vivaclass student token api fail : no students"); //실패사유
                        batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
                        return;
                    }

                    int result = 0;

                    if (CollectionUtils.isNotEmpty(vivaClassWithdrawDtoList)) {
                        List<WithdrawUserDto> withdrawUserDtoList = new ArrayList<>();
                        for (VivaClassWithdrawDto vivaClassWithdrawDto : vivaClassWithdrawDtoList) {
                            WithdrawUserDto withdrawUserDto = new WithdrawUserDto();

                            withdrawUserDto.setUserId("vivaclass-" + vivaClassWithdrawDto.getUserType() + "-" + vivaClassWithdrawDto.getMemberId());
                            withdrawUserDto.setUserSeCd(vivaClassWithdrawDto.getUserType());
                            withdrawUserDto.setPlatform("vivaclass");
                            withdrawUserDto.setIntegUserId(vivaClassWithdrawDto.getMemberId());
                            withdrawUserDto.setWithdrawDt(vivaClassWithdrawDto.getTimestamp());
                            withdrawUserDto.setSyncAt("N");
                            withdrawUserDto.setRgtr("batchSystem");

                            withdrawUserDtoList.add(withdrawUserDto);
                        }

                        result = integPublishMapper.insertVivaClassWithDrawUser(withdrawUserDtoList);
                    }

                    batchparamData.put("btchExcnRsltCnt", result);
                    batchparamData.put("btchRsltAt", "Y");
                    btchExcnLogService.modifyBtchExcnLog(batchparamData);
                }
            }
        }
    }

    @Scheduled(cron = "${batch-job.schedule.EtcBatchJob.executeVivaClassUserWithdrawSync}")
    public void executeVivaClassUserWithdrawSync() throws Exception {
        String btchNm = "EtcBatchJob.executeVivaClassDgnssUserWithdrawSync";
        Map<String, Object> resultOfBatchInfoExist = btchExcnLogService.checkBatchInfoExist(btchNm);

        Boolean isBatchInfoExist = (Boolean) resultOfBatchInfoExist.get("resultOk");
        if (isBatchInfoExist) {
            int result = 0;
            Map<String, Object> batchparamData = new HashMap<>();
            batchparamData.put("btchDetId", resultOfBatchInfoExist.get("btchDetId"));

            // 교사, 학생의 삭제 조건이 다름
            List<Map<String, Object>> targetWithDrawTcList = integPublishMapper.selectWithDrawTcList();
            List<Map<String, Object>> targetWithDrawStList = integPublishMapper.selectWithDrawStList();

            List<Integer> dgnssList = new ArrayList<>();
            List<String> tcUserList = new ArrayList<>();
            List<String> stUserList = new ArrayList<>();
            List<String> deleteTargetStdtList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(targetWithDrawTcList)) {
                deleteTargetStdtList = integPublishMapper.selectStdtListWithTcList(tcUserList);
                for (Map<String, Object> tcMap : targetWithDrawTcList) {
                    tcUserList.add(MapUtils.getString(tcMap, "userId", ""));
                }
            }

            if (CollectionUtils.isNotEmpty(targetWithDrawStList)) {
                for (Map<String, Object> stMap : targetWithDrawStList) {
                    stUserList.add(MapUtils.getString(stMap, "userId", ""));
                }
            }

            List<String> mergeUserId = Stream.of(tcUserList, deleteTargetStdtList, stUserList)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());

            List<String> mergeStdtId = Stream.of(deleteTargetStdtList, stUserList)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
            try {
                this.deleteProc(dgnssList, tcUserList, mergeUserId, mergeStdtId);
            } catch (Exception e) {
                batchparamData.put("failDc", "query fail"); //실패사유
                batchparamData.put("btchRsltAt", "N"); //배치결과여부(실패)
            } finally {
                batchparamData.put("failDc", "execute user delete : " + mergeUserId.size() + " user");
                batchparamData.put("btchRsltAt", "Y"); //배치결과여부(실패)
                // 배치 결과 업데이트
                btchExcnLogService.modifyBtchExcnLog(batchparamData);
            }
        }
    }

    public void deleteProc(List<Integer> dgnssList,
                           List<String> tcUserList,
                           List<String> mergeUserId,
                           List<String> mergeStdtId) {

        if (CollectionUtils.isNotEmpty(tcUserList)) {
            // 교사가 속해있는 모든 학급의 심리검사 정보 삭제
            if (CollectionUtils.isNotEmpty(dgnssList)) {
                etcMapper.deleteTcDgnssAnswerReport(dgnssList);
                etcMapper.deleteTcDgnssAnswerWithDgnssIdList(dgnssList);
                etcMapper.deleteTcDgnssOmrWithDgnssIfList(dgnssList);
                etcMapper.deleteTcDgnssResultInfoWithDgnssIdList(dgnssList);
                etcMapper.deleteTcDgnssInfoWithDgnssIdList(dgnssList);
            }

            // 교사 개인정보 및 학급에 속한 학생 정보 삭제
            if (CollectionUtils.isNotEmpty(tcUserList)) {
                integPublishMapper.deleteTcClaMbInfoWithTcIdList(tcUserList);
                integPublishMapper.deleteTcClaInfoWithTcIdList(tcUserList);
                integPublishMapper.deleteTcRegInfoWithTcIdList(tcUserList);
            }

            if (CollectionUtils.isNotEmpty(mergeStdtId)) {
                integPublishMapper.deleteStdtRegInfoWithTcIdList(mergeStdtId);
            }

            if (CollectionUtils.isNotEmpty(mergeUserId)) {
                integPublishMapper.deleteSpPrchsHist(mergeUserId);
                integPublishMapper.deleteSpPrchsInfo(mergeUserId);
                integPublishMapper.deleteUserWithUserId(mergeUserId);
            }
        }
    }
}
