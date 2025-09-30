package com.visang.aidt.lms.api.user.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class RewardConstants {

    @Getter
    @RequiredArgsConstructor
    public enum RewardAdjustType {
        EARN(1, "획득"),
        DEDUCT(2, "차감");

        private final int code;
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum RewardSeCode {
        EARN("1", "획득"),
        DEDUCT("2", "차감"),
        HEART("1", "하트"),
        STAR("2", "스타");

        private final String code;
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum MenuSeCode {
        TEACHER("7", "교사");

        private final String code;
        private final String description;
    }

    @Getter
    @RequiredArgsConstructor
    public enum SveSeCode {
        REWARD_ADJUST("11", "리워드 조정");

        private final String code;
        private final String description;
    }
}