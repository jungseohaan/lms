package com.visang.aidt.lms.api.mathexplore.batch;

import com.visang.aidt.lms.api.mathexplore.mapper.MathExploreMapper;
import com.visang.aidt.lms.api.user.service.StntRewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Profile({"engl-stg","math-stg","math-beta2-job","engl-beta2-job","math-release-job","engl-release-job","engl-prod-job", "math-prod-job"})
@Component
public class MathExploreBatchJob {
    private MathExploreMapper mathExploreMapper;

    private StntRewardService stntRewardService;

    final Integer[] RWD_AMT_LIST = {1500, 1200, 1000, 500, 400, 300, 200, 150, 100, 50};

    @Scheduled(cron = "0 0 0 L * *") // 매월 마지막 날 자정
    public void executeRewardsProvided() throws Exception {
        log.info("MathExploreBathJob > updateResult()");

        this.saveReward();
    }

    private void cleanupTable() throws Exception {
        this.mathExploreMapper.deleteAll();
    }

    private void saveReward() throws Exception {
        List<Map> classList = this.mathExploreMapper.findClaList();
        Map<String, Object> rwdMap = new HashMap<>();

        for(Map<String, Object> classItem : classList){
            classItem.put("cnt", 10);
            List<Map<String, Object>> rankList = this.mathExploreMapper.selectClaRankList(classItem);
            for (int i = 0 ; i < rankList.size() ; i++){
                Map<String, Object> stdtItem = rankList.get(i);

                rwdMap.put("userId", stdtItem.get("stdtId"));	// 예) vstea1
                rwdMap.put("claId", stdtItem.get("claId"));	// 예) 1dfd618eb8fb11ee88c00242ac110002
                rwdMap.put("seCd", "1");		// 구분		1:획득, 2:사용
                rwdMap.put("menuSeCd", "1");	// 메뉴구분	1:교과서, 2:과제, 3:평가, 4:셀프러닝
                rwdMap.put("sveSeCd", "2");		// 서비스구분	1:문제, 2:활동, 3:과제, 4:평가, 5:자동학습, 6:수동학습, 7:오답노트
                rwdMap.put("trgtId", 0);		// 대상ID
                rwdMap.put("rwdSeCd", "1");		//리워드구분	1:하트, 2:스타
                rwdMap.put("rwdAmt", RWD_AMT_LIST[i]);		//지급할 리워드 금액
                rwdMap.put("rwdUseAmt", 0);		//지급일때는 0
            }
        }

        Map<String, Object> rewardResult = stntRewardService.createReward(rwdMap);

        this.cleanupTable();
    }
}
