package com.visang.aidt.lms.api.dashboard.vo;

import com.fasterxml.jackson.annotation.JsonValue;
import java.security.SecureRandom;

public enum QuadrantAlertEnum {

    // 제1사분면 알림 메시지
    FIRST_QUADRANT_ALERT_1("수업에 열심히 참여하여 좋은 성과를 내고 있네요. 정말 대단해요!"),
    FIRST_QUADRANT_ALERT_2("멋진 성과를 내고 있어요! 앞으로도 성실하게 파이팅!"),
    FIRST_QUADRANT_ALERT_3("더 큰 목표를 설정하고 도전해 볼 준비가 되었나요? 선생님은 늘 응원합니다!"),

    // 제2사분면 알림 메시지
    SECOND_QUADRANT_ALERT_1("굉장한 실력을 가지고 있네요! 조금 더 즐거운 학습을 위해 관심있는 주제를 선생님께 알려 주세요!"),
    SECOND_QUADRANT_ALERT_2("좋은 실력을 가지고 있어요! 하지만 학습에 흥미를 느끼고 열심히 참여하면 좋을 것 같아요!"),
    SECOND_QUADRANT_ALERT_3("이미 좋은 성과를 내고 있지만, 더 성실하고 꾸준한 참여가 필요해요!"),
    SECOND_QUADRANT_ALERT_4("좋은 실력을 유지하기 위해 과제도 열심히 해 보는 건 어때요? 성실함을 더해 보아요!"),
    SECOND_QUADRANT_ALERT_5("이미 좋은 성과를 내고 있지만, 더 흥미로운 학습을 위해 재미있는 프로젝트로 공부해 보는 건 어때요?"),
    SECOND_QUADRANT_ALERT_6("충분히 잘하고 있어요! 이번엔 조금 더 흥미로운 학습으로 실생활과 관련된 문제를 풀어 보아요!"),

    // 제3사분면 알림 메시지
    THIRD_QUADRANT_ALERT_1("지금까지 잘하고 있어요! 학습을 시작하는 게 어려울 수 있지만, 작은 목표부터 하나씩 해 보는 것도 좋을 것 같아요!"),
    THIRD_QUADRANT_ALERT_2("작은 목표를 설정하여 하나씩 이루어 봐요! 자신감과 성취감이 쌓일 거예요!"),
    THIRD_QUADRANT_ALERT_3("조금씩 꾸준히 하다 보면 수업이 재미있을 거예요! 선생님은 늘 응원합니다!"),
    THIRD_QUADRANT_ALERT_4("간단한 문제를 해결하며 실력을 쌓아 보아요! 차근차근 하다 보면 어느새 실력이 늘 거예요!"),
    THIRD_QUADRANT_ALERT_5("처음에는 어렵더라도 조금씩 꾸준히 하다 보면 점점 더 쉬워질 거예요!"),

    // 제4사분면 알림 메시지
    FOURTH_QUADRANT_ALERT_1("성실함이 스스로를 더 성장하게 만들어 줄 거예요. 선생님과 함께 설정을 올릴 방법을 찾아봐요!"),
    FOURTH_QUADRANT_ALERT_2("꾸준한 노력이 언젠가는 성장이라는 보상으로 돌아올 거예요. 선생님과 더 효과적인 방법을 찾아봐요!"),
    FOURTH_QUADRANT_ALERT_3("꾸준한 노력은 반드시 결실을 맺게 될 거예요. 스스로를 믿어 봐요!"),
    FOURTH_QUADRANT_ALERT_4("학습 태도가 정말 좋아요! 좋은 성적도 멋진 태도를 따라 올 거예요!"),
    FOURTH_QUADRANT_ALERT_5("열심히 잘 하고 있네요! 선생님과 함께 새로운 공부 방법을 찾아 보아요!");

    private final String message;

    QuadrantAlertEnum(String message) {
        this.message = message;
    }

    @JsonValue
    public String getMessage() {
        return message;
    }

    /**
     * 주어진 사분면에 해당하는 알림 메시지 중 랜덤으로 하나를 반환합니다.
     * @param quadrant 사분면 번호 (1, 2, 3, 4)
     * @return 랜덤하게 선택된 알림 메시지
     */
    public static String getRandomAlertForQuadrant(int quadrant) {
        QuadrantAlertEnum[] allAlerts = values();
        String prefix = "";

        switch (quadrant) {
            case 1:
                prefix = "FIRST_QUADRANT_ALERT_";
                break;
            case 2:
                prefix = "SECOND_QUADRANT_ALERT_";
                break;
            case 3:
                prefix = "THIRD_QUADRANT_ALERT_";
                break;
            case 4:
                prefix = "FOURTH_QUADRANT_ALERT_";
                break;
            default:
                return "유효하지 않은 사분면입니다.";
        }

        // 해당 사분면의 알림 메시지만 필터링
        java.util.List<QuadrantAlertEnum> quadrantAlerts = new java.util.ArrayList<>();
        for (QuadrantAlertEnum alert : allAlerts) {
            if (alert.name().startsWith(prefix)) {
                quadrantAlerts.add(alert);
            }
        }

        // 랜덤 선택
        if (quadrantAlerts.isEmpty()) {
            return "해당 사분면의 알림 메시지가 없습니다.";
        }

        // CSAP 수정사항 보안상 안전한 SecureRandom 사용
        SecureRandom secureRandom = new SecureRandom();
        int randomIndex = secureRandom.nextInt(quadrantAlerts.size());
        return quadrantAlerts.get(randomIndex).getMessage();
    }
}