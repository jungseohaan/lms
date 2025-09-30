package com.visang.aidt.lms.api.mq;

public class MessageConstants {
    public static class Type {
        // real
        public static final String INITIALIZED = "initialized";
        public static final String TERMINATED = "terminated";
        public static final String CURRICULUM_PROGRESSED = "curriculum_progressed";
        public static final String CURRICULUM_COMPLETED = "curriculum_completed";
        public static final String CURRICULUM_SCORE = "curriculum_score";

        // bulk
        public static final String ASSESSMENT = "assessment";
        public static final String ASSIGNMENT = "assignment";
        public static final String MEDIA = "media";
        public static final String NAVIGATION = "navigation";
        public static final String QUERY = "query";
        public static final String TEACHING = "teaching";
    }

    public static class Verb {
        public static final String SUBMITTED = "submitted";
        public static final String PLAYED = "played";
        public static final String GAVE = "gave";
        public static final String FINISHED = "finished";
        public static final String VIEWED = "viewed";
        public static final String READ = "read";
        public static final String DID = "did";
        public static final String LEARNED = "learned";
        public static final String ASKED = "asked";
        public static final String REORGANIZED = "reorganized";
    }

}
