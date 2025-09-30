package com.visang.aidt.lms.global;

public class AidtConst {

    public static final String FORMAT_STRING_YMDHM = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_STRING_YMDHMS = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_STRING_YMD = "yyyy-MM-dd";
    public static final String STNT_LEVEL_HIGH = "gd";
    public static final String STNT_LEVEL_MID = "av";
    public static final String STNT_LEVEL_LOW = "lw";

    /**
     * MD01:난이도5(상) ~ MD05:난이도1(하)
     */
    public enum DIFFICULT_CODE {
        /***
         * 난이도5(상)
         */
        MD01,
        /**
         * 난이도4(중상)
         */
        MD02,
        /**
         * 난이도3(중)
         */
        MD03,
        /**
         * 난이도2(중하)
         */
        MD04,
        /**
         * 난이도1(하)
         */
        MD05
    }
}
