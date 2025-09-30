package com.visang.aidt.lms.api.dashboard.service;

import com.visang.aidt.lms.api.dashboard.model.PioPdf;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class DrawPdfService {

    public void addDgnssPage_DGNSS10(PioPdf pioPdf, PDDocument doc, PDPageContentStream cont, int page, Map<String, Object> userInfo, List<Map<String, Object>> dgnssReport3, List<Map<String, Object>> dgnssReport4, List<Map<String, Object>> dgnssReport5, List<Map<String, Object>> dgnssReportStudy) throws IOException
    {
        try {
            // 대분류 점수 배열

            if(  Integer.parseInt(userInfo.get("DGNSS_ORD").toString()) == 1 && (page == 21 || page == 22) )
                pioPdf.drawPageBackground("template02", page, 2);
            else
                pioPdf.drawPageBackground("template02", page, 0);


            if(page > 1 && page != 24) {
                pioPdf.drawHeader(page, userInfo);
                pioPdf.drawFooter();
            }

            // cont.drawImage(pdImage, 0f, 0f, pdImage.getWidth(), pdImage.getHeight());

            if(page == 1) {
                float x = pioPdf.px2mm(298f);
                float width = pioPdf.px2mm(88f);
                float fontSize = 12f;
                float fontHeight = 10f;

                pioPdf.drawTextC((String)userInfo.get("MEM_NM"), x, pioPdf.px2mm(601f - fontHeight, "Y"), width, "Pretendard Medium", fontSize, false, false, Color.BLACK, -0.58f);
                pioPdf.drawTextC((String)userInfo.get("RSPNS_DT_KO"), x, pioPdf.px2mm(633f- fontHeight, "Y"), width, "Pretendard Medium", fontSize, false, false, Color.BLACK, -0.58f);
                pioPdf.drawTextC( (String)userInfo.get("SCH_NM"), x, pioPdf.px2mm(665f- fontHeight, "Y"), width, "Pretendard Medium", fontSize, false, false, Color.BLACK, -0.58f);
                pioPdf.drawTextC((String)userInfo.get("MEM_GRADE_NM") + " " +  (String)userInfo.get("CLASS_NM"), x, pioPdf.px2mm(695f - fontHeight, "Y"), width, "Pretendard Medium", fontSize, false, false, Color.BLACK, -0.58f);
                pioPdf.drawTextC(userInfo.get("CLASS_NO").toString() + "번", x, pioPdf.px2mm(727f  - fontHeight, "Y"), width, "Pretendard Medium", fontSize, false, false, Color.BLACK, -0.58f);

                // pioPdf.drawPicture("./assets/imgs/dgnss/logo/school_snu.png", 5f, 250f, 2*23.0f, 2*17.4f );
            }
            // 해석가이드 > 신뢰도(사회적 바람직성, 반응일관성)
            else if(page == 4) {
                float fontSize = 11f;
                float x = pioPdf.px2mm(182f);

                float y1 = pioPdf.px2mm(226f, "Y");
                float y2 = pioPdf.px2mm(256f, "Y");
                float y3 = pioPdf.px2mm(286f, "Y");

                // 사회적 바람직성
                if(userInfo.get("COCH_DGNSS_QESITM02_MARK").toString().equals("양호"))
                    pioPdf.drawText("양호", x, y1, "Pretendard SemiBold", fontSize, false, false, Color.BLACK, -0.53f);
                else if(userInfo.get("COCH_DGNSS_QESITM02_MARK").toString().equals("주의"))
                    pioPdf.drawText("주의", x, y1, "Pretendard SemiBold", fontSize, false, false, pioPdf.hexa2Color("#FF4800"), -0.53f);

                // 반응 일관성
                if(userInfo.get("COCH_DGNSS_QESITM01_MARK").toString().equals("양호"))
                    pioPdf.drawText("양호", x, y2, "Pretendard SemiBold", fontSize, false, false, Color.BLACK, -0.53f);
                else if(userInfo.get("COCH_DGNSS_QESITM01_MARK").toString().equals("주의"))
                    pioPdf.drawText("주의" , x, y2, "Pretendard SemiBold", fontSize, false, false, pioPdf.hexa2Color("#FF4800"), -0.53f);

                // 연속 동일반응
                if(userInfo.get("REPEATED_RESPONSE_YN").toString().equals("아니오"))
                    pioPdf.drawText("아니오", x-1.5f, y3, "Pretendard SemiBold", fontSize, false, false, Color.BLACK, -0.53f);
                else if(userInfo.get("REPEATED_RESPONSE_YN").toString().equals("예"))
                    pioPdf.drawText("예" , x+1.5f, y3, "Pretendard SemiBold", fontSize, false, false, pioPdf.hexa2Color("#FF4800"), -0.53f);

            }
            // 해석가이드 _ 종합결과(중분류 막대그래프)
            else if(page == 5){
                if (userInfo.get("MEM_NM").toString().length() > 4) {
                    pioPdf.drawTextC(userInfo.get("MEM_NM").toString(), pioPdf.px2mm(108f), pioPdf.px2mm(158f, "Y"), pioPdf.px2mm(30f), "Pretendard SemiBold", 12f, false, false, Color.BLACK, -0.29f);
                } else {
                    pioPdf.drawTextC(userInfo.get("MEM_NM").toString(), pioPdf.px2mm(120f), pioPdf.px2mm(158f, "Y"), pioPdf.px2mm(30f), "Pretendard SemiBold", 12f, false, false, Color.BLACK, -0.29f);
                }

                float[] x = new float[11];
                float y = pioPdf.px2mm(402f, "Y");
                float width = pioPdf.px2mm(16f);
                float height = pioPdf.px2mm(171f);
                Color[] color = new Color[11];

                float[] score = new float[11];

                x[0] = pioPdf.px2mm(96f);
                x[1] = pioPdf.px2mm(129f);
                x[2] = pioPdf.px2mm(181f);
                x[3] = pioPdf.px2mm(214f);
                x[4] = pioPdf.px2mm(247f);
                x[5] = pioPdf.px2mm(297f);
                x[6] = pioPdf.px2mm(330f);
                x[7] = pioPdf.px2mm(381f);
                x[8] = pioPdf.px2mm(414f);
                x[9] = pioPdf.px2mm(445f);
                x[10] = pioPdf.px2mm(497f);

                color[0] = pioPdf.hexa2Color("#00D282");
                color[1] = pioPdf.hexa2Color("#00D282");

                color[2] = pioPdf.hexa2Color("#4BC1FF");
                color[3] = pioPdf.hexa2Color("#4BC1FF");
                color[4] = pioPdf.hexa2Color("#4BC1FF");

                color[5] = pioPdf.hexa2Color("#67A7FF");
                color[6] = pioPdf.hexa2Color("#67A7FF");

                color[7] = pioPdf.hexa2Color("#FF8DA9");
                color[8] = pioPdf.hexa2Color("#FF8DA9");
                color[9] = pioPdf.hexa2Color("#FF8DA9");

                color[10] = pioPdf.hexa2Color("#FF87D4");

                score[0] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "01", "01","0").get("T_SCORE").toString());
                score[1] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "01", "02","0").get("T_SCORE").toString());
                score[2] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "02", "01","0").get("T_SCORE").toString());
                score[3] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "02", "02","0").get("T_SCORE").toString());
                score[4] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "02", "03","0").get("T_SCORE").toString());
                score[5] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "04", "01","0").get("T_SCORE").toString());
                score[6] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "04", "02","0").get("T_SCORE").toString());
                score[7] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "03", "01","0").get("T_SCORE").toString());
                score[8] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "03", "02","0").get("T_SCORE").toString());
                score[9] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "03", "03","0").get("T_SCORE").toString());
                score[10] =Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "05", "01","0").get("T_SCORE").toString());

                for(int i = 0 ; i < x.length ; i++) {
                    if(score[i] > 0)
                        pioPdf.drawBarChart_Vertical(x[i], y, width, (height *  score[i] / 100.0f), color[i], color[i] );
                }
            }
            else if(page == 6) {
                float[] x = new float[6];
                float[] y = new float[7];
                float width  = pioPdf.px2mm(45f);

                x[0] = pioPdf.px2mm(53f);
                x[1] = pioPdf.px2mm(122f);
                x[2] = pioPdf.px2mm(235f);
                x[3] = pioPdf.px2mm(317f);
                x[4] = pioPdf.px2mm(342f);
                x[5] = pioPdf.px2mm(366f);

                y[0] = pioPdf.px2mm(297f - 5.5f, "Y");
                y[1] = pioPdf.px2mm(319f - 5.5f, "Y");
                y[2] = pioPdf.px2mm(415f - 5.5f, "Y");
                y[3] = pioPdf.px2mm(419f - 5.5f, "Y");
                y[4] = pioPdf.px2mm(524f - 5.5f, "Y");
                y[5] = pioPdf.px2mm(529f - 5.5f, "Y");
                y[6] = pioPdf.px2mm(609f - 5.5f, "Y");

                if (userInfo.get("MEM_NM").toString().length() > 4) {
                    pioPdf.drawTextC(userInfo.get("MEM_NM").toString(), pioPdf.px2mm(108f), pioPdf.px2mm(158f, "Y"), pioPdf.px2mm(30f), "Pretendard SemiBold", 12f, false, false, Color.BLACK, -0.29f);
                } else {
                    pioPdf.drawTextC(userInfo.get("MEM_NM").toString(), pioPdf.px2mm(115f), pioPdf.px2mm(158f, "Y"), pioPdf.px2mm(30f), "Pretendard SemiBold", 12f, false, false, Color.BLACK, -0.29f);
                }

                // 학습재설계_메타인지(02-01)
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "02", "01", "0").get("T_RANK").toString(), x[2], y[0], width, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf,  getAnswerReportValue(dgnssReport4, 4, "02", "01", "0").get("T_RANK").toString()), -0.48f);

                // 학습재설계_학습기술(02-02)
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "02", "02", "0").get("T_RANK").toString(), x[2], y[1], width, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf,  getAnswerReportValue(dgnssReport4, 4, "02", "02", "0").get("T_RANK").toString()), -0.48f);

                // 긍정적 자아(01-01)
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "01", "01", "0").get("T_RANK").toString(), x[0], y[2], width, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf,  getAnswerReportValue(dgnssReport4, 4, "01", "01", "0").get("T_RANK").toString()), -0.48f);

                // 지지적 관계(02-03)
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "02", "03", "0").get("T_RANK").toString(), x[1], y[2], width, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf,  getAnswerReportValue(dgnssReport4, 4, "02", "03", "0").get("T_RANK").toString()), -0.48f);

                // 긍정적 공부 마음_학업열의(04-01)
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "04", "01", "0").get("T_RANK").toString(), x[3], y[3], width, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf,  getAnswerReportValue(dgnssReport4, 4, "04", "01", "0").get("T_RANK").toString()), -0.48f);

                // 긍정적 공부 마음_성장력(04-02)
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "04", "02", "0").get("T_RANK").toString(), x[5], y[3], width, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf,  getAnswerReportValue(dgnssReport4, 4, "04", "02", "0").get("T_RANK").toString()), -0.48f);

                // 학업 스트레스(03-01)
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "03", "01", "0").get("T_RANK").toString(), x[0], y[4], width, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf,  getAnswerReportValue(dgnssReport4, 4, "03", "01", "").get("T_RANK").toString(), true), -0.48f);

                // 학업 관계 스트레스(03-03)
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "03", "02", "0").get("T_RANK").toString(), x[1], y[4], width, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf,  getAnswerReportValue(dgnssReport4, 4, "03", "02", "").get("T_RANK").toString(), true), -0.48f);

                // 부정적 공부 마음_학업소진(05-01)
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "05", "01", "0").get("T_RANK").toString(), x[4], y[5], width, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf,  getAnswerReportValue(dgnssReport4, 4, "05", "01", "").get("T_RANK").toString(), true), -0.48f);

                // 학습재설계_자기정서조절(01-02-02)
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, "01", "02", "02").get("T_RANK").toString(), x[2] + pioPdf.px2mm(4f), y[6], width, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf,  getAnswerReportValue(dgnssReport5, 5, "01", "02", "02").get("T_RANK").toString()), -0.48f);

            }
            // 해석 가이드(학습현황 점검)
            else if (page == 7){
                float x[] = new float[5];
                float y1[] = new float[2];

                float x2;
                float y2[] = new float[5];


                String avgStudyTime[]  = new String[5];

                if (userInfo.get("MEM_NM").toString().length() > 4) {
                    pioPdf.drawTextC(userInfo.get("MEM_NM").toString(), pioPdf.px2mm(200f), pioPdf.px2mm(164f, "Y"), pioPdf.px2mm(40f), "Pretendard", 16f, true, false, Color.BLACK);
                } else {
                    pioPdf.drawTextC(userInfo.get("MEM_NM").toString(), pioPdf.px2mm(208f), pioPdf.px2mm(164f, "Y"), pioPdf.px2mm(40f), "Pretendard", 16f, true, false, Color.BLACK);
                }

                x[0] = pioPdf.px2mm(61.5f);
                x[1] = pioPdf.px2mm(115f);
                x[2] = pioPdf.px2mm(169.5f);
                x[3] = pioPdf.px2mm(223.5f);
                x[4] = pioPdf.px2mm(277.5f);

                y1[0] = pioPdf.px2mm(286f, "Y");
                y1[1] = pioPdf.px2mm(385f, "Y");


                x2 = pioPdf.px2mm(291f);

                y2[0] = pioPdf.px2mm(492f, "Y");
                y2[1] = pioPdf.px2mm(514.4f, "Y");
                y2[2] = pioPdf.px2mm(536.8f, "Y");
                y2[3] = pioPdf.px2mm(559.2f, "Y");
                y2[4] = pioPdf.px2mm(581.6f, "Y");

                avgStudyTime[0] = "전혀 안함";
                avgStudyTime[1] = "1시간 미만";
                avgStudyTime[2] = "1시간 이상~2시간 미만";
                avgStudyTime[3] = "2시간 이상~3시간 미만";
                avgStudyTime[4] = "3시간 이상";

                String lsAns05FileName[] = new String[5];

                lsAns05FileName[0] = "./assets/imgs/dgnss/btn/btn_ans05_1.png";
                lsAns05FileName[1] = "./assets/imgs/dgnss/btn/btn_ans05_2.png";
                lsAns05FileName[2] = "./assets/imgs/dgnss/btn/btn_ans05_3.png";
                lsAns05FileName[3] = "./assets/imgs/dgnss/btn/btn_ans05_4.png";
                lsAns05FileName[4] = "./assets/imgs/dgnss/btn/btn_ans05_5.png";


                if(userInfo.get("LS_ANS01") != null && !StringUtils.isEmpty(userInfo.get("LS_ANS01").toString()) ) {
                    pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_check_p4.png", x[Integer.parseInt(userInfo.get("LS_ANS01").toString()) -1], y1[0], pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                }

                if(userInfo.get("LS_ANS02") != null && !StringUtils.isEmpty(userInfo.get("LS_ANS02").toString()) ) {
                    pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_check_p4.png", x[Integer.parseInt(userInfo.get("LS_ANS02").toString())-1], y1[1], pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                }

                if(userInfo.get("LS_ANS03") != null && !StringUtils.isEmpty(userInfo.get("LS_ANS03").toString()) ) {
                    pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_check2.png", x2, y2[Integer.parseInt(userInfo.get("LS_ANS03").toString())-1], pioPdf.px2mm(14f), pioPdf.px2mm(14f));
                }

                if(userInfo.get("LS_ANS04") != null && !StringUtils.isEmpty(userInfo.get("LS_ANS04").toString()) ) {
                    pioPdf.drawTextC(avgStudyTime[Integer.parseInt(userInfo.get("LS_ANS04").toString())-1], pioPdf.px2mm(196f), pioPdf.px2mm(678f, "Y"), pioPdf.px2mm(98f), "Pretendard SemiBold", 11f);
                }

                if(userInfo.get("LS_ANS05") != null && !StringUtils.isEmpty(userInfo.get("LS_ANS05").toString()) ) {
                    pioPdf.drawPicture(lsAns05FileName[Integer.parseInt(userInfo.get("LS_ANS05").toString())-1], pioPdf.px2mm(35f), pioPdf.px2mm(792f, "Y"), pioPdf.px2mm(290f), pioPdf.px2mm(40f));
                }

            }
            // 8 : 자아강점 > 긍정적 자아(3)
            else if((page >= 8 && page <= 14) || (page >= 16 && page <= 19)) {
                String class3 = "";
                String class4 = "";
                int class5Count = 0;
                Color class4Color = Color.BLACK;

                boolean reverse = false;

                // 부정 측정 여부
                if (page >= 16 && page <= 19)
                    reverse = true;

                // 자아 강점_긍정적 자아
                if(page == 8) {
                    class3 = "01";
                    class4 = "01";

                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#00D282");
                }
                //자아 강점_ 대인관계능력
                else if(page == 9) {
                    class3 = "01";
                    class4 = "02";

                    class5Count = 4;
                    class4Color = pioPdf.hexa2Color("#00D282");
                }
                // 학습디딤돌_메타인지
                else if(page == 10) {
                    class3 = "02";
                    class4 = "01";

                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#4BC2FF");
                }
                // 학습디딤돌_학습기술
                else if(page == 11) {
                    class3 = "02";
                    class4 = "02";

                    class5Count = 5;
                    class4Color = pioPdf.hexa2Color("#4BC2FF");

                }
                // 학습디딤돌_지지적 관계
                else if(page == 12) {
                    class3 = "02";
                    class4 = "03";
                    class5Count = 4;
                    class4Color = pioPdf.hexa2Color("#4BC2FF");
                }
                // 긍정적 공부마음_학업열의
                else if(page == 13) {
                    class3 = "04";
                    class4 = "01";
                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#67A7FF");
                }
                // 긍정적 공부마음_성장력
                else if(page == 14) {
                    class3 = "04";
                    class4 = "02";
                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#67A7FF");
                }
                // 학습걸림돌_학업스트레스
                else if(page == 16) {
                    class3 = "03";
                    class4 = "01";
                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#FF8DA9");
                }
                // 학습걸림돌_학업관계스트레스
                else if(page == 17) {
                    class3 = "03";
                    class4 = "02";
                    class5Count = 5;
                    class4Color = pioPdf.hexa2Color("#FF8DA9");
                }
                // 학습걸림돌_학습방해물
                else if(page == 18) {
                    class3 = "03";
                    class4 = "03";
                    class5Count = 2;
                    class4Color = pioPdf.hexa2Color("#FF8DA9");
                }
                // 학습걸림돌_학업관계스트레스
                else if(page == 19) {
                    class3 = "05";
                    class4 = "01";
                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#FF87D4");
                }

                float x1 = pioPdf.px2mm(141);
                float width1 = pioPdf.px2mm(32f);
                float[] y1 = new float[6];

                // 중분류 MARK
                float x2 = pioPdf.px2mm(53f);
                float width2 = pioPdf.px2mm(16f);
                float[] y2 = new float[5];

                float x3 = pioPdf.px2mm(204.5f);
                float barWidth = pioPdf.px2mm(371f);
                float[] y3 = new float[5];

                float y2Height = 2f;
                float y3Height = 0f;

                if(class5Count == 2) {

                    y1[0] = pioPdf.px2mm(243f, "Y");
                    y1[1] = pioPdf.px2mm(263f, "Y");
                    y1[2] = pioPdf.px2mm(283f, "Y");

                    y2[0]= pioPdf.px2mm(352f - y2Height, "Y");
                    y2[1]= pioPdf.px2mm(420f - y2Height ,"Y");

                    y3[0] = pioPdf.px2mm(333.5f - y3Height, "Y");
                    y3[1] = pioPdf.px2mm(402.5f - y3Height, "Y");


                }
                else if(class5Count == 3) {

                    y1[0] = pioPdf.px2mm(243f, "Y");
                    y1[1] = pioPdf.px2mm(263f, "Y");
                    y1[2] = pioPdf.px2mm(283f, "Y");
                    y1[3] = pioPdf.px2mm(303f, "Y");

                    y2[0]= pioPdf.px2mm(372f - y2Height , "Y");
                    y2[1]= pioPdf.px2mm(440f - y2Height,"Y");
                    y2[2]= pioPdf.px2mm(507f - y2Height,"Y");

                    y3[0] = pioPdf.px2mm(353.5f- y3Height, "Y");
                    y3[1] = pioPdf.px2mm(422.5f- y3Height, "Y");
                    y3[2] = pioPdf.px2mm(490.5f- y3Height, "Y");

                }
                else if(class5Count == 4) {
                    y1[0] = pioPdf.px2mm(243f, "Y");
                    y1[1] = pioPdf.px2mm(263f, "Y");
                    y1[2] = pioPdf.px2mm(283f, "Y");
                    y1[3] = pioPdf.px2mm(303f, "Y");
                    y1[4] = pioPdf.px2mm(323f, "Y");

                    y2[0]= pioPdf.px2mm(393f - y2Height, "Y");
                    y2[1]= pioPdf.px2mm(461f - y2Height,"Y");
                    y2[2]= pioPdf.px2mm(529f - y2Height,"Y");
                    y2[3]= pioPdf.px2mm(597f - y2Height,"Y");

                    y3[0] = pioPdf.px2mm(374.5f- y3Height, "Y");
                    y3[1] = pioPdf.px2mm(443.5f- y3Height, "Y");
                    y3[2] = pioPdf.px2mm(511.5f- y3Height, "Y");
                    y3[3] = pioPdf.px2mm(579.5f- y3Height, "Y");

                }

                else if(class5Count == 5) {

                    y1[0] = pioPdf.px2mm(243f, "Y");
                    y1[1] = pioPdf.px2mm(263f, "Y");
                    y1[2] = pioPdf.px2mm(283f, "Y");
                    y1[3] = pioPdf.px2mm(303f, "Y");
                    y1[4] = pioPdf.px2mm(323f, "Y");
                    y1[5] = pioPdf.px2mm(343f, "Y");

                    y2[0]= pioPdf.px2mm(412f - y2Height, "Y");
                    y2[1]= pioPdf.px2mm(480f - y2Height ,"Y");
                    y2[2]= pioPdf.px2mm(548f - y2Height,"Y");
                    y2[3]= pioPdf.px2mm(616f - y2Height,"Y");
                    y2[4]= pioPdf.px2mm(684f - y2Height,"Y");

                    y3[0] = pioPdf.px2mm(394.5f- y3Height, "Y");
                    y3[1] = pioPdf.px2mm(462.5f- y3Height, "Y");
                    y3[2] = pioPdf.px2mm(530.5f- y3Height, "Y");
                    y3[3] = pioPdf.px2mm(598.5f- y3Height, "Y");
                    y3[4] = pioPdf.px2mm(666.5f- y3Height, "Y");
                }

                // pioPdf.setOpacity(0.80f);
                if(Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, class3, class4, "").get("T_SCORE").toString()) >= 0) {
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, class3, class4, "0").get("T_SCORE").toString() + "(" + getAnswerReportValue(dgnssReport4, 4, class3, class4, "0").get("P_RANK").toString() + ")", x1, y1[0], width1, "", 10.5f, true, false, Color.BLACK, -0.5f);
                    pioPdf.drawBarChart_Horizontal(x3, y1[0], (float) barWidth * Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, class3, class4, "").get("T_SCORE").toString()) / 100f, pioPdf.px2mm(10f), Color.WHITE, class4Color);
                } else {
                    pioPdf.drawTextC("?", x1, y1[0], width1, "", 10.5f, true, false, Color.BLACK, -0.5f);
                }

                // pioPdf.setOpacity(1f);

                String tmpClass5 = "";

                for(int i = 0 ; i < class5Count ; i++) {

                    tmpClass5 = StringUtils.leftPad(Integer.toString(i+1), 2, "0");

                    log.debug("tempClass5 : {}, {}, {}", class3, class4,  tmpClass5);

                    // 변인 T점수(백분위)
                    if(Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCORE").toString()) >= 0)
                        pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCORE").toString() + "(" + getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("P_RANK").toString() + ")", x1, y1[i+1], width1, "", 10.5f, false, false, Color.BLACK, -0.5f);
                    else{
                        pioPdf.drawTextC("?", x1, y1[i+1], width1, "", 10.5f, false, false, Color.BLACK, -0.5f);
                    }

                    // 변인 MARK
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_RANK").toString(), x2, y2[i], width2, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf, getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_RANK").toString(), reverse ), -0.48f);

                    // 변인 T점수 가로바 차트

                    if(Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCORE").toString()) > 0) {
                        pioPdf.setOpacity(0.65f);
                        pioPdf.drawBarChart_Horizontal(x3, y1[i + 1], (float) barWidth * Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCORE").toString()) / 100f, pioPdf.px2mm(10f), Color.white, pioPdf.hexa2Color("#9AA0A8"));
                        pioPdf.setOpacity(1f);
                    }
                    log.debug("script 5 :  {}", getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCRIPT").toString());
                    pioPdf.drawTextParagraph(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCRIPT").toString(), pioPdf.px2mm(121f), y3[i], pioPdf.px2mm(438f),  147.4f, "", 9.5f, Color.BLACK, true, pioPdf.px2pt(-0.46f));
                }

                // 중분류 총평
                pioPdf.drawTextParagraph(getAnswerReportValue(dgnssReport4, 4, class3, class4, "0").get("T_SCRIPT").toString(), pioPdf.px2mm(30f), pioPdf.px2mm(753.5f,"Y"), pioPdf.px2mm(528f), 152.4f, "Pretendard Medium", 10.5f, Color.BLACK, true, pioPdf.px2pt(-0.5f));

            }
            else if(page == 15){
                String class3 = "";

                String[] imgFile = new String[25];
                float[] x1 = new float[25];
                float[] y1 = new float[25];

                y1[0] = pioPdf.px2mm(175f, "Y");
                y1[1] = pioPdf.px2mm(200f, "Y");
                y1[2] = pioPdf.px2mm(225f, "Y");
                y1[3] = pioPdf.px2mm(250f, "Y");
                y1[4] = pioPdf.px2mm(275f, "Y");
                y1[5] = pioPdf.px2mm(300f, "Y");
                y1[6] = pioPdf.px2mm(325f, "Y");
                y1[7] = pioPdf.px2mm(360f, "Y");
                y1[8] = pioPdf.px2mm(385f, "Y");
                y1[9] = pioPdf.px2mm(410f, "Y");
                y1[10] = pioPdf.px2mm(435f, "Y");
                y1[11] = pioPdf.px2mm(460f, "Y");
                y1[12] = pioPdf.px2mm(485f, "Y");
                y1[13] = pioPdf.px2mm(510f, "Y");
                y1[14] = pioPdf.px2mm(534f, "Y");
                y1[15] = pioPdf.px2mm(559f, "Y");
                y1[16] = pioPdf.px2mm(585f, "Y");
                y1[17] = pioPdf.px2mm(610f, "Y");
                y1[18] = pioPdf.px2mm(635f, "Y");
                y1[19] = pioPdf.px2mm(684f, "Y");
                y1[20] = pioPdf.px2mm(709f, "Y");
                y1[21] = pioPdf.px2mm(734f, "Y");
                y1[22] = pioPdf.px2mm(759f, "Y");
                y1[23] = pioPdf.px2mm(784f, "Y");
                y1[24] = pioPdf.px2mm(809f, "Y");


                imgFile[0] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "01", "01", "01").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[1] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "01", "01", "02").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[2] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "01", "01", "03").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[3] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "01", "02", "01").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[4] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "01", "02", "02").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[5] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "01", "02", "03").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[6] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "01", "02", "04").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[7] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "01", "01").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[8] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "01", "02").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[9] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "01", "03").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[10] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "02", "01").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[11] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "02", "02").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[12] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "02", "03").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[13] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "02", "04").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[14] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "02", "05").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[15] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "03", "01").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[16] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "03", "02").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[17] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "03", "03").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[18] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "02", "03", "04").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[19] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "04", "01", "01").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[20] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "04", "01", "02").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[21] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "04", "01", "03").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[22] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "04", "02", "01").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[23] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "04", "02", "02").get("T_RANK").toString(), "dgnss10", false,  page - 1);
                imgFile[24] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "04", "02", "03").get("T_RANK").toString(), "dgnss10", false,  page - 1);

                for(int i = 0 ; i < 25 ; i++){
                    if(imgFile[i].contains("ico_mark_high"))
                        x1[i] = pioPdf.px2mm(385f);
                    else if(imgFile[i].contains("ico_mark_mid"))
                        x1[i] = pioPdf.px2mm(321f);
                    else if(imgFile[i].contains("ico_mark_low"))
                        x1[i] = pioPdf.px2mm(257f);
                    else
                        x1[i] = 0;
                }

                for(int i = 0 ; i < 25; i++)
                    pioPdf.drawPicture(imgFile[i], x1[i], y1[i], pioPdf.px2mm(14f), pioPdf.px2mm(14f));

                // 종합등급
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "01", "01", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(199f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK, -0.5f);
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "01", "02", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(287f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK, -0.5f);
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "02", "01", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(384f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK, -0.5f);
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "02", "02", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(484f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK, -0.5f);
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "02", "03", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(593f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK, -0.5f);
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "04", "01", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(708f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK, -0.5f);
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "04", "02", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(783f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK, -0.5f);

                // 종합순위
                if(!getAnswerReportValue(dgnssReport4, 4, "01", "01", "0").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "01", "01", "0").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(199f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport4, 4, "01", "02", "0").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "01", "02", "0").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(287f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport4, 4, "02", "01", "0").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "02", "01", "0").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(384f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport4, 4, "02", "02", "0").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "02", "02", "0").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(484f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport4, 4, "02", "03", "0").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "02", "03", "0").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(593f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport5, 5, "04", "01", "01").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, "04", "01", "01").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(680f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport5, 5, "04", "01", "02").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, "04", "01", "02").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(705f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport5, 5, "04", "01", "03").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, "04", "01", "03").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(730f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport5, 5, "04", "02", "01").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, "04", "02", "01").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(755f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport5, 5, "04", "02", "02").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, "04", "02", "02").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(780f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport5, 5, "04", "02", "03").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, "04", "02", "03").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(805f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

            }
            else if(page == 20){
                String class3 = "";

                String[] imgFile = new String[13];
                float[] x1 = new float[13];
                float[] y1 = new float[13];

                y1[0] = pioPdf.px2mm(175f, "Y");
                y1[1] = pioPdf.px2mm(200f, "Y");
                y1[2] = pioPdf.px2mm(225f, "Y");
                y1[3] = pioPdf.px2mm(250f, "Y");
                y1[4] = pioPdf.px2mm(275f, "Y");
                y1[5] = pioPdf.px2mm(299f, "Y");
                y1[6] = pioPdf.px2mm(325f, "Y");
                y1[7] = pioPdf.px2mm(349f, "Y");
                y1[8] = pioPdf.px2mm(374f, "Y");
                y1[9] = pioPdf.px2mm(400f, "Y");
                y1[10] = pioPdf.px2mm(446f, "Y");
                y1[11] = pioPdf.px2mm(471f, "Y");
                y1[12] = pioPdf.px2mm(496f, "Y");

                imgFile[0] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "03", "01", "01").get("T_RANK").toString(), "dgnss10",  true, page-1 );
                imgFile[1] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "03", "01", "02").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[2] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "03", "01", "03").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[3] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "03", "02", "01").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[4] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "03", "02", "02").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[5] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "03", "02", "03").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[6] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "03", "02", "04").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[7] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "03", "02", "05").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[8] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "03", "03", "01").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[9] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "03", "03", "02").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[10] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "05", "01", "01").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[11] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "05", "01", "02").get("T_RANK").toString(), "dgnss10", true, page-1 );
                imgFile[12] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, "05", "01", "03").get("T_RANK").toString(), "dgnss10", true, page-1 );

                for(int i = 0 ; i < 13 ; i++){
                    if(imgFile[i].contains("ico_mark_high"))
                        x1[i] = pioPdf.px2mm(257f);
                    else if(imgFile[i].contains("ico_mark_mid"))
                        x1[i] = pioPdf.px2mm(321f);
                    else if(imgFile[i].contains("ico_mark_low"))
                        x1[i] = pioPdf.px2mm(385f);
                    else
                        x1[i] = 0;
                }

                for(int i = 0 ; i < 13; i++) {
                    pioPdf.drawPicture(imgFile[i], x1[i], y1[i], pioPdf.px2mm(14f), pioPdf.px2mm(14f));
                }

                // 종합등급
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "03", "01", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(199f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK,-0.5f);
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "03", "02", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(299f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK,-0.5f);
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "03", "03", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(386f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK,-0.5f);
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "05", "01", "0").get("T_RANK").toString(), pioPdf.px2mm(452f), pioPdf.px2mm(470f, "Y"), pioPdf.px2mm(34f), "Pretendard Medium", 10.5f, false, false, Color.BLACK,-0.5f);

                // 종합순위
                if(!getAnswerReportValue(dgnssReport4, 4, "03", "01", "0").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "03", "01", "0").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(199f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport4, 4, "03", "02", "0").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "03", "02", "0").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(299f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport4, 4, "03", "03", "0").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, "03", "03", "0").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(386f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);


                if(!getAnswerReportValue(dgnssReport5, 5, "05", "01", "01").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, "05", "01", "01").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(442f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport5, 5, "05", "01", "02").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, "05", "01", "02").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(467f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

                if(!getAnswerReportValue(dgnssReport5, 5, "05", "01", "03").get("RANK_TOTAL").toString().equals("-1"))
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, "05", "01", "03").get("RANK_TOTAL").toString(), pioPdf.px2mm(536f), pioPdf.px2mm(492f, "Y"), pioPdf.px2mm(6f), "Pretendard Medium", 10.5f, false, false, Color.BLACK);

            }
            else if(page == 21){
                // 종합분석표  첫페이지

                int dataCount = 25;

                float xStart = pioPdf.px2mm(180f);      // 그래프 시작 위치
                float barWidth = pioPdf.px2mm(298f);      // 그래프 바 넓이

                float[] y = new float[dataCount];   // 테이블 각각 높이
                // float height = Double.valueOf(187.0 / 22.0).floatValue();   // 테이블 행 높이
                float height = Double.valueOf(pioPdf.px2mm(601f)  / dataCount).floatValue();   // 테이블 행 높이

                int i = 0;

                y[0] = pioPdf.px2mm(238f, "Y");

                for(i = 1; i < dataCount ; i++)
                    y[i] = y[i-1] - height;

                // 기준선에서 텍스트 높이
                float textHeight = 3f;

                int dgnssOrd = Integer.parseInt(userInfo.get("DGNSS_ORD").toString()) ;

                // 종합해석 1차, 2차 변화 X 좌표
                float x1[] = new float[4];
                x1[0] = pioPdf.px2mm(488f);
                x1[1] = pioPdf.px2mm(520f);
                x1[2] = pioPdf.px2mm(548f);
                x1[3] = pioPdf.px2mm(556f);

                float x1Width = pioPdf.px2mm(14f);
                float x3Width = pioPdf.px2mm(12f);

                if(dgnssOrd == 1){

                    pioPdf.drawText(userInfo.get("DGNSS_ORD").toString() + "차", pioPdf.px2mm(488f), pioPdf.px2mm(204.5f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK);

                    float[] t_score5_1 = new float[7];
                    float[] t_score5_2 = new float[12];
                    float[] t_score5_3 = new float[6];

                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "03").get("T_SCORE").toString());
                    t_score5_1[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "04").get("T_SCORE").toString());

                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "03").get("T_SCORE").toString());
                    t_score5_2[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "01").get("T_SCORE").toString());
                    t_score5_2[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "02").get("T_SCORE").toString());
                    t_score5_2[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "03").get("T_SCORE").toString());
                    t_score5_2[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "04").get("T_SCORE").toString());
                    t_score5_2[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "05").get("T_SCORE").toString());
                    t_score5_2[8] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "01").get("T_SCORE").toString());
                    t_score5_2[9] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "02").get("T_SCORE").toString());
                    t_score5_2[10] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "03").get("T_SCORE").toString());
                    t_score5_2[11] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "04").get("T_SCORE").toString());

                    t_score5_3[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "01").get("T_SCORE").toString());
                    t_score5_3[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "02").get("T_SCORE").toString());
                    t_score5_3[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "03").get("T_SCORE").toString());
                    t_score5_3[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "01").get("T_SCORE").toString());
                    t_score5_3[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "02").get("T_SCORE").toString());
                    t_score5_3[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "03").get("T_SCORE").toString());

                    pioPdf.drawGraph01(t_score5_1,  xStart, y[0] + (height / 2f), barWidth  , height, 1,1, pioPdf.hexa2Color("#00D282"));
                    pioPdf.drawGraph01(t_score5_2,  xStart, y[7] + (height / 2f), barWidth , height,1, 1, pioPdf.hexa2Color("#41BEFF"));
                    pioPdf.drawGraph01(t_score5_3,  xStart, y[19] + (height / 2f), barWidth , height, 1, 1, pioPdf.hexa2Color("#4F96FE"));



                    for(i = 0 ; i < 7 ; i++){
                        if(t_score5_1[i] > 0)
                            pioPdf.drawTextC( Integer.toString((int)(t_score5_1[i])) , x1[0], y[i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);
                    }

                    for(i = 0 ; i < 12 ; i++){
                        if(t_score5_2[i] > 0)
                            pioPdf.drawTextC( Integer.toString((int)(t_score5_2[i])), x1[0], y[7+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);
                    }

                    for(i = 0 ; i < 6 ; i++){
                        if(t_score5_3[i] > 0)
                            pioPdf.drawTextC( Integer.toString((int)(t_score5_3[i])), x1[0], y[19+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);
                    }

                }
                else{
                    pioPdf.drawText(userInfo.get("DGNSS_ORD_FIRST").toString() + "차", pioPdf.px2mm(488f), pioPdf.px2mm(204.5f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK);
                    pioPdf.drawText(userInfo.get("DGNSS_ORD").toString() + "차", pioPdf.px2mm(519f), pioPdf.px2mm(204.5f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK);


                    float[] t_score5_1_first = new float[7];
                    float[] t_score5_2_first = new float[12];
                    float[] t_score5_3_first = new float[6];

                    float[] t_score5_1 = new float[7];
                    float[] t_score5_2 = new float[12];
                    float[] t_score5_3 = new float[6];

                    float[] t_score5_1_gap = new float[7];
                    float[] t_score5_2_gap = new float[12];
                    float[] t_score5_3_gap = new float[6];



                    t_score5_1_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "03").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "04").get("T_SCORE_FIRST").toString());

                    t_score5_2_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "03").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "04").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "05").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[8] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[9] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[10] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "03").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[11] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "04").get("T_SCORE_FIRST").toString());

                    t_score5_3_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "03").get("T_SCORE_FIRST").toString());


                    t_score5_1_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "01").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "02").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "03").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "01").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "02").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "03").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "04").get("T_SCORE_GAP").toString());

                    t_score5_2_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "01").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "02").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "03").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "01").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "02").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "03").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "04").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "05").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[8] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "01").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[9] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "02").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[10] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "03").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[11] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "04").get("T_SCORE_GAP").toString());

                    t_score5_3_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "01").get("T_SCORE_GAP").toString());
                    t_score5_3_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "02").get("T_SCORE_GAP").toString());
                    t_score5_3_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "03").get("T_SCORE_GAP").toString());
                    t_score5_3_gap[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "01").get("T_SCORE_GAP").toString());
                    t_score5_3_gap[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "02").get("T_SCORE_GAP").toString());
                    t_score5_3_gap[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "03").get("T_SCORE_GAP").toString());


                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "03").get("T_SCORE").toString());
                    t_score5_1[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "04").get("T_SCORE").toString());

                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "03").get("T_SCORE").toString());
                    t_score5_2[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "01").get("T_SCORE").toString());
                    t_score5_2[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "02").get("T_SCORE").toString());
                    t_score5_2[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "03").get("T_SCORE").toString());
                    t_score5_2[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "04").get("T_SCORE").toString());
                    t_score5_2[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "05").get("T_SCORE").toString());
                    t_score5_2[8] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "01").get("T_SCORE").toString());
                    t_score5_2[9] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "02").get("T_SCORE").toString());
                    t_score5_2[10] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "03").get("T_SCORE").toString());
                    t_score5_2[11] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "03", "04").get("T_SCORE").toString());

                    t_score5_3[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "01").get("T_SCORE").toString());
                    t_score5_3[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "02").get("T_SCORE").toString());
                    t_score5_3[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "01", "03").get("T_SCORE").toString());
                    t_score5_3[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "01").get("T_SCORE").toString());
                    t_score5_3[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "02").get("T_SCORE").toString());
                    t_score5_3[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "04", "02", "03").get("T_SCORE").toString());

                    pioPdf.drawGraph01(t_score5_1_first,  xStart, y[0] + (height / 2f), barWidth  , height, 1,1, pioPdf.hexa2Color("#A9ADB2"));
                    pioPdf.drawGraph01(t_score5_2_first,  xStart, y[7] + (height / 2f), barWidth , height,1, 1, pioPdf.hexa2Color("#A9ADB2"));
                    pioPdf.drawGraph01(t_score5_3_first,  xStart, y[19] + (height / 2f), barWidth , height, 1, 1, pioPdf.hexa2Color("#A9ADB2"));

                    pioPdf.drawGraph01(t_score5_1,  xStart, y[0] + (height / 2f), barWidth  , height, 1,1, pioPdf.hexa2Color("#00D282"));
                    pioPdf.drawGraph01(t_score5_2,  xStart, y[7] + (height / 2f), barWidth , height,1, 1, pioPdf.hexa2Color("#41BEFF"));
                    pioPdf.drawGraph01(t_score5_3,  xStart, y[19] + (height / 2f), barWidth , height, 1, 1, pioPdf.hexa2Color("#4F96FE"));

                    for(i = 0 ; i < 7 ; i++){

                        if(t_score5_1_first[i] > 0)
                            pioPdf.drawTextC(Integer.toString((int)(t_score5_1_first[i])) , x1[0], y[i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);

                        if(t_score5_1[i] > 0)
                            pioPdf.drawTextC(Integer.toString((int)(t_score5_1[i])) , x1[1], y[i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);


                        if(t_score5_1_first[i] > 0 && t_score5_1[i] > 0){
                            if (t_score5_1_gap[i] < 0f) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x1[2], y[i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(Integer.toString((int) (t_score5_1_gap[i])), x1[3], y[i] + textHeight, x3Width, "", 9.5f, false, false, pioPdf.hexa2Color("#FF4800"));
                            } else if (t_score5_1_gap[i] == 0f) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[i] + (height / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                            } else {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x1[2], y[i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(Integer.toString((int) (t_score5_1_gap[i])), x1[3], y[i] + textHeight, x3Width, "", 9.5f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                            }
                        }

                    }

                    for(i = 0 ; i < 12 ; i++){
                        if(t_score5_2_first[i] > 0)
                            pioPdf.drawTextC( Integer.toString((int)(t_score5_2_first[i])) , x1[0], y[7+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);

                        if(t_score5_2[i] > 0)
                            pioPdf.drawTextC(Integer.toString((int)(t_score5_2[i])), x1[1], y[7+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);


                        if(t_score5_2_first[i] > 0 && t_score5_2[i] > 0){
                            if (t_score5_2_gap[i] < 0f) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x1[2], y[7 + i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(Integer.toString((int) (t_score5_2_gap[i])), x1[3], y[7 + i] + textHeight, x3Width, "", 9.5f, false, false, pioPdf.hexa2Color("#FF4800"));
                            } else if (t_score5_2_gap[i] == 0f) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[7 + i] + (height / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                            } else {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x1[2], y[7 + i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(Integer.toString((int) (t_score5_2_gap[i])), x1[3], y[7 + i] + textHeight, x3Width, "", 9.5f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                            }
                        }
                    }

                    for(i = 0 ; i < 6 ; i++){
                        if(t_score5_3_first[i] > 0)
                            pioPdf.drawTextC( Integer.toString((int)(t_score5_3_first[i])), x1[0], y[19+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);

                        if(t_score5_3[i] > 0)
                            pioPdf.drawTextC( Integer.toString((int)(t_score5_3[i])), x1[1], y[19+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);

                        if(t_score5_3_first[i] > 0 && t_score5_3[i] > 0){
                            if (t_score5_3_gap[i] < 0) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x1[2], y[19 + i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(Integer.toString((int) (t_score5_3_gap[i])), x1[3], y[19 + i] + textHeight, x3Width, "", 9.5f, false, false, pioPdf.hexa2Color("#FF4800"));
                            } else if (t_score5_3_gap[i] == 0) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[19 + i] + (height / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                            } else {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x1[2], y[19 + i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(Integer.toString((int) (t_score5_3_gap[i])), x1[3], y[19 + i] + textHeight, x3Width, "", 9.5f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                            }
                        }
                    }
                }
            }
            // 종합분석표  두번째페이지 (학습걸림돌, 부정적 공부마음)
            else if(page == 22){

                int dataCount = 13;

                float xStart = pioPdf.px2mm(180f);      // 그래프 시작 위치
                float barWidth = pioPdf.px2mm(298f);      // 그래프 바 넓이

                float[] y = new float[dataCount];   // 테이블 각각 높이
                // float height = Double.valueOf(187.0 / 22.0).floatValue();   // 테이블 행 높이
                float height = Double.valueOf(pioPdf.px2mm(313f)  / dataCount).floatValue();   // 테이블 행 높이

                int i = 0;

                y[0] = pioPdf.px2mm(238f, "Y");

                for(i = 1; i < dataCount ; i++)
                    y[i] = y[i-1] - height;

                // 기준선에서 텍스트 높이
                float textHeight = 3f;

                int dgnssOrd = Integer.parseInt(userInfo.get("DGNSS_ORD").toString()) ;

                // 종합해석 1차, 2차 변화 X 좌표
                float x1[] = new float[4];
                x1[0] = pioPdf.px2mm(488f);
                x1[1] = pioPdf.px2mm(520f);
                x1[2] = pioPdf.px2mm(548f);
                x1[3] = pioPdf.px2mm(556f);
//                 {164.5f, 176.7f, 187.75f, 190.73f};
                float x1Width = pioPdf.px2mm(12f);
                float x3Width = pioPdf.px2mm(12f);

                if(dgnssOrd == 1){

                    pioPdf.drawPicture("./assets/imgs/dgnss/btn/btn_total_anal_10_1st.png", pioPdf.px2mm(185f), pioPdf.px2mm(815f, "Y"), pioPdf.px2mm(220f), pioPdf.px2mm(33f));

                    pioPdf.drawText(userInfo.get("DGNSS_ORD").toString() + "차", pioPdf.px2mm(488f), pioPdf.px2mm(204.5f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK);

                    float[] t_score5_1 = new float[10];
                    float[] t_score5_2 = new float[3];

                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "03").get("T_SCORE").toString());
                    t_score5_1[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "04").get("T_SCORE").toString());
                    t_score5_1[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "05").get("T_SCORE").toString());
                    t_score5_1[8] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "03", "01").get("T_SCORE").toString());
                    t_score5_1[9] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "03", "02").get("T_SCORE").toString());

                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "03").get("T_SCORE").toString());

                    pioPdf.drawGraph01(t_score5_1,  xStart, y[0] + (height / 2f), barWidth  , height, 1,1, pioPdf.hexa2Color("#FF849F"));
                    pioPdf.drawGraph01(t_score5_2,  xStart, y[10] + (height / 2f), barWidth , height,1, 1, pioPdf.hexa2Color("#FF87D4"));

                    for(i = 0 ; i < 10 ; i++){
                        if(t_score5_1[i] > 0)
                            pioPdf.drawTextC( Integer.toString((int)(t_score5_1[i])) , x1[0], y[i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);
                    }

                    for(i = 0 ; i < 3 ; i++){
                        if(t_score5_2[i] > 0)
                            pioPdf.drawTextC( Integer.toString((int)(t_score5_2[i])), x1[0], y[10+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);
                    }

                    pioPdf.drawTextC(userInfo.get("DGNSS_ORD").toString() + "차 : " + userInfo.get("RSPNS_DT"), pioPdf.px2mm(211f), pioPdf.px2mm(803f, "Y"), pioPdf.px2mm(68f), "Pretendard Medium", 10f, false, false, Color.BLACK, -0.48f);
                }
                else{

                    pioPdf.drawPicture("./assets/imgs/dgnss/btn/btn_total_anal_10_nst.png", pioPdf.px2mm(185f), pioPdf.px2mm(815f, "Y"), pioPdf.px2mm(220f), pioPdf.px2mm(33f));

                    pioPdf.drawText(userInfo.get("DGNSS_ORD_FIRST").toString() + "차", pioPdf.px2mm(488f), pioPdf.px2mm(204.5f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK);
                    pioPdf.drawText(userInfo.get("DGNSS_ORD").toString() + "차", pioPdf.px2mm(519f), pioPdf.px2mm(204.5f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK);


                    float[] t_score5_1_first = new float[10];
                    float[] t_score5_2_first = new float[3];

                    float[] t_score5_1_gap = new float[10];
                    float[] t_score5_2_gap = new float[3];

                    float[] t_score5_1 = new float[10];
                    float[] t_score5_2 = new float[3];

                    t_score5_1_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "03").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "04").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "05").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[8] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "03", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[9] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "03", "02").get("T_SCORE_FIRST").toString());

                    t_score5_2_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "03").get("T_SCORE_FIRST").toString());

                    t_score5_1_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "01").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "02").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "03").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "01").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "02").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "03").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "04").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "05").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[8] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "03", "01").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[9] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "03", "02").get("T_SCORE_GAP").toString());

                    t_score5_2_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "01").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "02").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "03").get("T_SCORE_GAP").toString());


                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "03").get("T_SCORE").toString());
                    t_score5_1[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "04").get("T_SCORE").toString());
                    t_score5_1[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "05").get("T_SCORE").toString());
                    t_score5_1[8] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "03", "01").get("T_SCORE").toString());
                    t_score5_1[9] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "03", "02").get("T_SCORE").toString());

                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "05", "01", "03").get("T_SCORE").toString());


                    pioPdf.drawGraph01(t_score5_1_first,  xStart, y[0] + (height / 2f), barWidth  , height, 1,1, pioPdf.hexa2Color("#A9ADB2"));
                    pioPdf.drawGraph01(t_score5_2_first,  xStart, y[10] + (height / 2f), barWidth , height,1, 1, pioPdf.hexa2Color("#A9ADB2"));

                    pioPdf.drawGraph01(t_score5_1,  xStart, y[0] + (height / 2f), barWidth  , height, 1,1, pioPdf.hexa2Color("#FF849F"));
                    pioPdf.drawGraph01(t_score5_2,  xStart, y[10] + (height / 2f), barWidth , height,1, 1, pioPdf.hexa2Color("#FF87D4"));

                    for(i = 0 ; i < 10 ; i++){
                        if(t_score5_1_first[i] > 0)
                            pioPdf.drawTextC( Integer.toString((int)(t_score5_1_first[i])) , x1[0], y[i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);

                        if(t_score5_1[i] > 0)
                            pioPdf.drawTextC( Integer.toString((int)(t_score5_1[i])) , x1[1], y[i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);

                        if(t_score5_1_first[i] > 0 && t_score5_1[i] > 0){
                            if (t_score5_1_gap[i] < 0f) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_blue.png", x1[2], y[i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(Integer.toString((int) (t_score5_1_gap[i])), x1[3], y[i] + textHeight, x3Width, "", 9.5f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                            } else if (t_score5_1_gap[i] == 0f) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[i] + (height / 2), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                            } else {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_red.png", x1[2], y[i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(Integer.toString((int) (t_score5_1_gap[i])), x1[3], y[i] + textHeight, x3Width, "", 9.5f, false, false, pioPdf.hexa2Color("#FF4800"));
                            }
                        }

                    }

                    for(i = 0 ; i < 3 ; i++){
                        if(t_score5_2_first[i] > 0)
                            pioPdf.drawTextC(Integer.toString((int)(t_score5_2_first[i])), x1[0], y[10+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);
                        else
                            pioPdf.drawTextC("?", x1[0], y[10+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);

                        if(t_score5_2[i] > 0)
                            pioPdf.drawTextC(Integer.toString((int)(t_score5_2[i])), x1[1], y[10+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);
                        else
                            pioPdf.drawTextC("?", x1[1], y[10+i] + textHeight, x1Width, "", 9.5f, false, false, Color.BLACK);

                        if(t_score5_2_first[i] > 0 && t_score5_2[i] > 0){
                            if (t_score5_2_gap[i] < 0f) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_blue.png", x1[2], y[10 + i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(Integer.toString((int) (t_score5_2_gap[i])), x1[3], y[10 + i] + textHeight, x3Width, "", 9.5f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                            } else if (t_score5_2_gap[i] == 0f) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[10 + i] + (height / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                            } else {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_red.png", x1[2], y[10 + i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(Integer.toString((int) (t_score5_2_gap[i])), x1[3], y[10 + i] + textHeight, x3Width, "", 9.5f, false, false, pioPdf.hexa2Color("#FF4800"));
                            }
                        }
                    }

                    pioPdf.drawText(userInfo.get("DGNSS_ORD_FIRST").toString() + "차 : " + userInfo.get("RSPNS_DT_FIRST"), pioPdf.px2mm(211f), pioPdf.px2mm(803f,"Y"), "", 10f, false, false, pioPdf.hexa2Color("#A9ADB2"), -0.48f);
                    pioPdf.drawText(userInfo.get("DGNSS_ORD").toString() + "차 : " + userInfo.get("RSPNS_DT"), pioPdf.px2mm(310f), pioPdf.px2mm(803f,"Y"),"Pretendard Medium", 10f, false, false, Color.BLACK, -0.48f);

                }
            }
            else if(page == 23){

                float[] x = new float[12];
                float[] x1 = new float[12];        // 낮음, 보통, 높음
                float[] y = new float[12];
                float width = pioPdf.px2mm(86f);

                x[0] = pioPdf.px2mm(123f);
                x[1] = pioPdf.px2mm(123f);
                x[2] = pioPdf.px2mm(123f);
                x[3] = pioPdf.px2mm(369f);
                x[4] = pioPdf.px2mm(369f);
                x[5] = pioPdf.px2mm(369f);
                x[6] = pioPdf.px2mm(123f);
                x[7] = pioPdf.px2mm(123f);
                x[8] = pioPdf.px2mm(123f);
                x[9] = pioPdf.px2mm(369f);
                x[10] = pioPdf.px2mm(369f);
                x[11] = pioPdf.px2mm(369f);

                y[0] = pioPdf.px2mm(285f, "Y");
                y[1] = pioPdf.px2mm(319f, "Y");
                y[2] = pioPdf.px2mm(352f, "Y");
                y[3] = pioPdf.px2mm(285f, "Y");
                y[4] = pioPdf.px2mm(319f, "Y");
                y[5] = pioPdf.px2mm(352f, "Y");

                y[6] = pioPdf.px2mm(387f, "Y");
                y[7] = pioPdf.px2mm(421f, "Y");
                y[8] = pioPdf.px2mm(454f, "Y");
                y[9] = pioPdf.px2mm(387f, "Y");
                y[10] = pioPdf.px2mm(421f, "Y");
                y[11] = pioPdf.px2mm(454f, "Y");

                for(int i = 0 ; i < 12 ; i++) {
                    pioPdf.drawTextC(dgnssReportStudy.get(i).get("SECTION_NM").toString(), x[i], y[i] + pioPdf.px2mm(4f), width, "Pretendard Medium", 10f, false, false, Color.BLACK);
                    // pioPdf.drawTextC(dgnssReportStudy.get(0).get("SECTION_NM").toString(), x[0], y[0], width, "", 10f, false, false, Color.BLACK);
                }

                String[] imgFile = new String[12];

                for (int i = 0 ; i < 12; i++){
                    if ((i >= 0 && i <= 2  ) || (i>= 6 && i<= 8)) {
                        imgFile[i] = getMarkLevel3ImageFileName(dgnssReportStudy.get(i).get("T_RANK").toString(), "dgnss10", false, page - 1);

                        if (imgFile[i].contains("ico_mark_high"))
                            x1[i] = pioPdf.px2mm(303f);
                        else if (imgFile[i].contains("ico_mark_mid"))
                            x1[i] = pioPdf.px2mm(266f);
                        else if (imgFile[i].contains("ico_mark_low"))
                            x1[i] = pioPdf.px2mm(229f);
                        else
                            x1[i] = 0;
                    }
                    else{
                        imgFile[i] = getMarkLevel3ImageFileName(dgnssReportStudy.get(i).get("T_RANK").toString(), "dgnss10",  true, page -1);

                        if (imgFile[i].contains("ico_mark_high"))
                            x1[i] = pioPdf.px2mm(475f);
                        else if (imgFile[i].contains("ico_mark_mid"))
                            x1[i] = pioPdf.px2mm(512f);
                        else if (imgFile[i].contains("ico_mark_low"))
                            x1[i] = pioPdf.px2mm(549f);
                        else
                            x1[i] = 0;
                    }
                }

                for(int i = 0 ; i < 12; i++)
                    pioPdf.drawPicture(imgFile[i], x1[i], y[i], pioPdf.px2mm(14f), pioPdf.px2mm(14f));

                // 공부마음 종합 분석

                float[] x2 = new float[3];
                float[] y2 = new float[3];
                String[] imgFile2 =  new String[3];

                y2[0] = pioPdf.px2mm(736f, "Y");
                y2[1] = pioPdf.px2mm(770f, "Y");
                y2[2] = pioPdf.px2mm(804f,"Y");

                // 1. 학업열의 2. 성장력  3. 학업소진
                imgFile2[0] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport4, 4, "04", "01", "0").get("T_RANK").toString(), "dgnss10", false, page -1);
                imgFile2[1] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport4, 4, "04", "02", "0").get("T_RANK").toString(), "dgnss10", false, page - 1);
                imgFile2[2] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport4, 4, "05", "01", "0").get("T_RANK").toString(), "dgnss10",true, page - 1);

                for(int i = 0; i < 3 ; i++) {
                    if(i <  2) {
                        if (imgFile2[i].contains("ico_mark_high"))
                            x2[i] = pioPdf.px2mm(497f);
                        else if (imgFile2[i].contains("ico_mark_mid"))
                            x2[i] = pioPdf.px2mm(357f);
                        else if (imgFile2[i].contains("ico_mark_low"))
                            x2[i] = pioPdf.px2mm(217f);
                        else
                            x2[i] = 0;
                    }
                    else {
                        if (imgFile2[i].contains("ico_mark_high"))
                            x2[i] = pioPdf.px2mm(217f);
                        else if (imgFile2[i].contains("ico_mark_mid"))
                            x2[i] = pioPdf.px2mm(357f);
                        else if (imgFile2[i].contains("ico_mark_low"))
                            x2[i] = pioPdf.px2mm(497f);
                        else
                            x2[i] = 0;
                    }
                }

                for(int i = 0 ; i < 3; i++)
                    pioPdf.drawPicture(imgFile2[i], x2[i], y2[i], pioPdf.px2mm(14f), pioPdf.px2mm(14f));


            }
        }catch(NullPointerException e){
            log.error("DGNSS10 페이지 생성 실패 - 데이터 누락: {}", e.getMessage());
        }catch(IOException e){
            log.error("DGNSS10 페이지 생성 실패 - I/O 오류: {}", e.getMessage());
        }catch(Exception e){
            log.error("DGNSS10 페이지 생성 실패 - 예상치 못한 오류: {}", e.getMessage());
        }
    }

    public void addDgnssPage_DGNSS20(PioPdf pioPdf, PDDocument doc, PDPageContentStream cont, int page, Map<String, Object> userInfo, List<Map<String, Object>> dgnssReport3, List<Map<String, Object>> dgnssReport4, List<Map<String, Object>> dgnssReport5) throws IOException
    {
        try {
            // 대분류 점수 배열

            if(  Integer.parseInt(userInfo.get("DGNSS_ORD").toString()) == 1 && (page == 16 || page == 17) )
                pioPdf.drawPageBackground("template02", page, 2);
            else
                pioPdf.drawPageBackground("template02", page, 0);


            if(page > 1 && page != 18) {
                pioPdf.drawHeader(page, userInfo);
                pioPdf.drawFooter();
            }

            // cont.drawImage(pdImage, 0f, 0f, pdImage.getWidth(), pdImage.getHeight());

            if(page == 1) {
//                float x = 87.02f;
//                float width = 54.18f;
//                float fontSize = 11.52f;
//
//                pioPdf.drawTextC((String)userInfo.get("MEM_NM"), x, 80f, width, "", fontSize, false, false, Color.BLACK);
//                pioPdf.drawTextC((String)userInfo.get("RSPNS_DT_KO"), x, 70f, width, "", fontSize, false, false, Color.BLACK);
//                pioPdf.drawTextC( (String)userInfo.get("SCH_NM"), x, 60f, width, "", fontSize, false, false, Color.BLACK);
//                pioPdf.drawTextC((String)userInfo.get("MEM_GRADE_NM") + " " +  (String)userInfo.get("CLASS_NM") + " " + (String)userInfo.get("CLASS_NO"), x, 50f, width, "", fontSize, false, false, Color.BLACK);
//                pioPdf.drawTextC("", x, 40f, width, "", fontSize, false, false, Color.BLACK);

                // pioPdf.drawPicture("./assets/imgs/dgnss/logo/school_snu.png", 5f, 250f, 2*23.0f, 2*17.4f );


                float x = pioPdf.px2mm(298f);
                float width = pioPdf.px2mm(88f);
                float fontSize = 12f;
                float fontHeight = 10f;

                // 이름
                pioPdf.drawTextC((String)userInfo.get("MEM_NM"), x, pioPdf.px2mm(600f - fontHeight, "Y"), width, "Pretendard Medium", fontSize, false, false, Color.BLACK, -0.58f);
                // 검사일
                pioPdf.drawTextC((String)userInfo.get("RSPNS_DT_KO"), x, pioPdf.px2mm(632f- fontHeight, "Y"), width, "Pretendard Medium", fontSize, false, false, Color.BLACK, -0.58f);
                // 학교
                pioPdf.drawTextC( (String)userInfo.get("SCH_NM"), x, pioPdf.px2mm(663f- fontHeight, "Y"), width, "Pretendard Medium", fontSize, false, false, Color.BLACK, -0.58f);
                // 학급
                pioPdf.drawTextC((String)userInfo.get("MEM_GRADE_NM") + " " +  (String)userInfo.get("CLASS_NM"), x, pioPdf.px2mm(695f - fontHeight, "Y"), width, "Pretendard Medium", fontSize, false, false, Color.BLACK, -0.58f);
                // 번호(현재는 해석전문가)
                pioPdf.drawTextC(userInfo.get("CLASS_NO").toString() + "번", x, pioPdf.px2mm(727f  - fontHeight, "Y"), width, "Pretendard Medium", fontSize, false, false, Color.BLACK, -0.58f);


            }
            else if(page == 4) {
                float fontSize = 11f;
                float x = pioPdf.px2mm(177f);

                float y1 = pioPdf.px2mm(225f, "Y");
                float y2 = pioPdf.px2mm(255f, "Y");
                float y3 = pioPdf.px2mm(285f, "Y");

                // 사회적 바람직성
                if(userInfo.get("COCH_DGNSS_QESITM02_MARK").toString().equals("양호"))
                    pioPdf.drawText("양호", x, y1, "Pretendard SemiBold", fontSize, false, false, Color.BLACK, -0.53f);
                else if(userInfo.get("COCH_DGNSS_QESITM02_MARK").toString().equals("주의"))
                    pioPdf.drawText("주의", x, y1, "Pretendard SemiBold", fontSize, false, false, pioPdf.hexa2Color("#FF4800"), -0.53f);

                // 반응 일관성
                if(userInfo.get("COCH_DGNSS_QESITM01_MARK").toString().equals("양호"))
                    pioPdf.drawText("양호", x, y2, "Pretendard SemiBold", fontSize, false, false, Color.BLACK, -0.53f);
                else if(userInfo.get("COCH_DGNSS_QESITM01_MARK").toString().equals("주의"))
                    pioPdf.drawText("주의" , x, y2, "Pretendard SemiBold", fontSize, false, false, pioPdf.hexa2Color("#FF4800"), -0.53f);

                // 연속 동일반응
                if(userInfo.get("REPEATED_RESPONSE_YN").toString().equals("아니오"))
                    pioPdf.drawText("아니오", x-1.5f, y3, "Pretendard SemiBold", fontSize, false, false, Color.BLACK, -0.53f);
                else if(userInfo.get("REPEATED_RESPONSE_YN").toString().equals("예"))
                    pioPdf.drawText("예" , x+1.5f, y3, "Pretendard SemiBold", fontSize, false, false, pioPdf.hexa2Color("#FF4800"), -0.53f);


            }
            // 해석가이드
            else if(page == 5){
                float x[] = new float[5];
                float y1[] = new float[2];

                float x2;
                float y2[] = new float[5];


                String avgStudyTime[]  = new String[5];

                if (userInfo.get("MEM_NM").toString().length() > 4) {
                    pioPdf.drawTextC(userInfo.get("MEM_NM").toString(), pioPdf.px2mm(195f), pioPdf.px2mm(164f, "Y"), pioPdf.px2mm(40f), "Pretendard", 16f, true, false, Color.BLACK);
                } else {
                    pioPdf.drawTextC(userInfo.get("MEM_NM").toString(), pioPdf.px2mm(207f), pioPdf.px2mm(164f, "Y"), pioPdf.px2mm(40f), "Pretendard", 16f, true, false, Color.BLACK);
                }

                x[0] = pioPdf.px2mm(61.5f);
                x[1] = pioPdf.px2mm(115f);
                x[2] = pioPdf.px2mm(169.5f);
                x[3] = pioPdf.px2mm(223.5f);
                x[4] = pioPdf.px2mm(277.5f);

                y1[0] = pioPdf.px2mm(286f, "Y");
                y1[1] = pioPdf.px2mm(385f, "Y");


                x2 = pioPdf.px2mm(291f);

                y2[0] = pioPdf.px2mm(492f, "Y");
                y2[1] = pioPdf.px2mm(514.4f, "Y");
                y2[2] = pioPdf.px2mm(536.8f, "Y");
                y2[3] = pioPdf.px2mm(559.2f, "Y");
                y2[4] = pioPdf.px2mm(581.6f, "Y");

                avgStudyTime[0] = "전혀 안함";
                avgStudyTime[1] = "1시간 미만";
                avgStudyTime[2] = "1시간 이상~2시간 미만";
                avgStudyTime[3] = "2시간 이상~3시간 미만";
                avgStudyTime[4] = "3시간 이상";

                String lsAns05FileName[] = new String[5];

                lsAns05FileName[0] = "./assets/imgs/dgnss/btn/btn_ans05_1.png";
                lsAns05FileName[1] = "./assets/imgs/dgnss/btn/btn_ans05_2.png";
                lsAns05FileName[2] = "./assets/imgs/dgnss/btn/btn_ans05_3.png";
                lsAns05FileName[3] = "./assets/imgs/dgnss/btn/btn_ans05_4.png";
                lsAns05FileName[4] = "./assets/imgs/dgnss/btn/btn_ans05_5.png";


                if(userInfo.get("LS_ANS01") != null && !StringUtils.isEmpty(userInfo.get("LS_ANS01").toString()) ) {
                    pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_check_p4.png", x[Integer.parseInt(userInfo.get("LS_ANS01").toString()) -1], y1[0], pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                }

                if(userInfo.get("LS_ANS02") != null && !StringUtils.isEmpty(userInfo.get("LS_ANS02").toString()) ) {
                    pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_check_p4.png", x[Integer.parseInt(userInfo.get("LS_ANS02").toString())-1], y1[1], pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                }

                if(userInfo.get("LS_ANS03") != null && !StringUtils.isEmpty(userInfo.get("LS_ANS03").toString()) ) {
                    pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_check2.png", x2, y2[Integer.parseInt(userInfo.get("LS_ANS03").toString())-1], pioPdf.px2mm(14f), pioPdf.px2mm(14f));
                }

                if(userInfo.get("LS_ANS04") != null && !StringUtils.isEmpty(userInfo.get("LS_ANS04").toString()) ) {
                    pioPdf.drawTextC(avgStudyTime[Integer.parseInt(userInfo.get("LS_ANS04").toString())-1], pioPdf.px2mm(196f), pioPdf.px2mm(678f, "Y"), pioPdf.px2mm(98f), "Pretendard SemiBold", 11f);
                }

                if(userInfo.get("LS_ANS05") != null && !StringUtils.isEmpty(userInfo.get("LS_ANS05").toString()) ) {
                    pioPdf.drawPicture(lsAns05FileName[Integer.parseInt(userInfo.get("LS_ANS05").toString())-1], pioPdf.px2mm(35f), pioPdf.px2mm(792f, "Y"), pioPdf.px2mm(290f), pioPdf.px2mm(40f));
                }
            }
            // 해석가이드 종합결과
            else if(page == 6) {
                pioPdf.drawTextC(userInfo.get("MEM_NM").toString(),  pioPdf.px2mm(117f), pioPdf.px2mm(158f, "Y"), pioPdf.px2mm(30f), "Pretendard Semibold", 12f, false, false, Color.BLACK);

                float[] t_score4 = new float[6];

                t_score4[0] = Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "01", "01", "0").get("T_SCORE").toString());
                t_score4[1] = Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "01", "02", "0").get("T_SCORE").toString());
                t_score4[2] = Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "02", "01", "0").get("T_SCORE").toString());
                t_score4[3] = Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "02", "02", "0").get("T_SCORE").toString());
                t_score4[4] = Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "03", "01", "0").get("T_SCORE").toString());
                t_score4[5] = Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, "03", "02", "0").get("T_SCORE").toString());

                pioPdf.drawRadarChart(t_score4, pioPdf.px2mm(295f), pioPdf.px2mm(370f, "Y"), pioPdf.px2mm(118f), pioPdf.hexa2Color("#FF9D00"), pioPdf.hexa2Color("#FFFCC9"), 0.4f);

                pioPdf.setOpacity(1f);

                // 대분류 등급
                pioPdf.drawPicture( getMarkImageFileName(getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("T_RANK").toString()), pioPdf.px2mm(70f), pioPdf.px2mm(680f, "Y"), pioPdf.px2mm(70f), pioPdf.px2mm(70f));
                pioPdf.drawPicture( getMarkImageFileName(getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("T_RANK").toString()), pioPdf.px2mm(260f), pioPdf.px2mm(680f, "Y"), pioPdf.px2mm(70f), pioPdf.px2mm(70f));
                pioPdf.drawPicture( getMarkImageFileName(getAnswerReportValue(dgnssReport3, 3, "03", "0", "0").get("T_RANK").toString()), pioPdf.px2mm(450f), pioPdf.px2mm(680f, "Y"), pioPdf.px2mm(70f), pioPdf.px2mm(70f));

                pioPdf.drawTextC(getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("T_SCORE").toString() + "(" +getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("P_RANK").toString() + ")", pioPdf.px2mm(70f), pioPdf.px2mm(660f,"Y"), pioPdf.px2mm(70f));
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("T_SCORE").toString() + "(" +getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("P_RANK").toString() + ")", pioPdf.px2mm(260f), pioPdf.px2mm(660f,"Y"), pioPdf.px2mm(70f));
                pioPdf.drawTextC(getAnswerReportValue(dgnssReport3, 3, "03", "0", "0").get("T_SCORE").toString() + "(" +getAnswerReportValue(dgnssReport3, 3, "03", "0", "0").get("P_RANK").toString() + ")", pioPdf.px2mm(450f), pioPdf.px2mm(660f,"Y"), pioPdf.px2mm(70f));

                String[] t_script3 = new String[3];

                t_script3[0] = getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("T_SCRIPT").toString();
                t_script3[1] = getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("T_SCRIPT").toString();
                t_script3[2] = getAnswerReportValue(dgnssReport3, 3, "03", "0", "0").get("T_SCRIPT").toString();

                pioPdf.drawTextParagraph(t_script3[0], pioPdf.px2mm(25f), pioPdf.px2mm(740f, "Y"), pioPdf.px2mm(157f), 150,  "", 9f, Color.BLACK, true, pioPdf.px2pt(-0.43f));
                pioPdf.drawTextParagraph(t_script3[1], pioPdf.px2mm(214.8f), pioPdf.px2mm(740f, "Y"), pioPdf.px2mm(157f), 150,  "", 9f, Color.BLACK, true, pioPdf.px2pt(-0.43f));
                pioPdf.drawTextParagraph(t_script3[2], pioPdf.px2mm(405f), pioPdf.px2mm(740f, "Y"), pioPdf.px2mm(157f), 150,  "", 9f, Color.BLACK, true, pioPdf.px2pt(-0.43f));

            }
            // 7 : 동기전략 > 학습원동력 , 8 : 동기전략 > 정서조절 10 인지전략 > 메타인지  11 인지전략 > 인지적 학습기술 13 행동전략 > 행동적 학스비술
            else if(page == 7 || page == 8 || page == 10 || page == 11 || page == 13 || page == 14 ) {
                String class3 = "";
                String class4 = "";

                int class5Count = 0;
                Color class4Color = Color.BLACK;


                if(page == 7) {
                    class3 = "01";
                    class4 = "01";

                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#B2A7F9");
                }
                else if(page == 8) {
                    class3 = "01";
                    class4 = "02";

                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#B2A7F9");
                }
                else if(page == 10) {
                    class3 = "02";
                    class4 = "01";

                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#10DAFF");

                }
                else if(page == 11) {
                    class3 = "02";
                    class4 = "02";

                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#10DAFF");
                }
                else if(page == 13) {
                    class3 = "03";
                    class4 = "01";

                    class5Count = 3;
                    class4Color = pioPdf.hexa2Color("#FF8A94");
                }
                else if(page == 14) {
                    class3 = "03";
                    class4 = "02";

                    class5Count = 5;
                    class4Color = pioPdf.hexa2Color("#FF8A94");
                }

                float x1 = pioPdf.px2mm(142f);
                float width1 = pioPdf.px2mm(30f);
                float[] y1 = new float[6];

                // 중분류 MARK
                float x2 = pioPdf.px2mm(53f);
                float width2 = pioPdf.px2mm(16f);
                float[] y2 = new float[5];

                float x3 = pioPdf.px2mm(204.5f);
                float barWidth = pioPdf.px2mm(371f);
                float[] y3 = new float[5];

                float y3Height = 0f;     // 4f -> 0f

                if(class5Count == 3) {

                    y1[0] = pioPdf.px2mm(243f, "Y");
                    y1[1] = pioPdf.px2mm(263f, "Y");
                    y1[2] = pioPdf.px2mm(283f, "Y");
                    y1[3] = pioPdf.px2mm(303f, "Y");

                    y2[0]= pioPdf.px2mm(370f, "Y");
                    y2[1]= pioPdf.px2mm(438f ,"Y");
                    y2[2]= pioPdf.px2mm(506f,"Y");

                    y3[0] = pioPdf.px2mm(354f- y3Height, "Y");
                    y3[1] = pioPdf.px2mm(423f- y3Height, "Y");
                    y3[2] = pioPdf.px2mm(491f- y3Height, "Y");

                }else if(class5Count == 5) {

                    y1[0] = pioPdf.px2mm(243f, "Y");
                    y1[1] = pioPdf.px2mm(263f, "Y");
                    y1[2] = pioPdf.px2mm(283f, "Y");
                    y1[3] = pioPdf.px2mm(303f, "Y");
                    y1[4] = pioPdf.px2mm(323f, "Y");
                    y1[5] = pioPdf.px2mm(343f, "Y");

                    y2[0]= pioPdf.px2mm(410f, "Y");
                    y2[1]= pioPdf.px2mm(478f ,"Y");
                    y2[2]= pioPdf.px2mm(546f,"Y");
                    y2[3]= pioPdf.px2mm(614f,"Y");
                    y2[4]= pioPdf.px2mm(682f,"Y");

                    y3[0] = pioPdf.px2mm(395f- y3Height, "Y");
                    y3[1] = pioPdf.px2mm(463f- y3Height, "Y");
                    y3[2] = pioPdf.px2mm(531f- y3Height, "Y");
                    y3[3] = pioPdf.px2mm(599f- y3Height, "Y");
                    y3[4] = pioPdf.px2mm(667f- y3Height, "Y");
                }

                // 대분류 등급()
//                 pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, class3, class4, "0").get("T_SCORE").toString() + "(" + getAnswerReportValue(dgnssReport4, 4, class3, class4, "0").get("P_RANK").toString() + ")", 38f, y1[0], 26f);

                if(Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, class3, class4, "0").get("T_SCORE").toString()) >= 0) {
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport4, 4, class3, class4, "0").get("T_SCORE").toString() + "(" + getAnswerReportValue(dgnssReport4, 4, class3, class4, "0").get("P_RANK").toString() + ")", x1, y1[0], width1, "", 10.5f, true, false, Color.BLACK, -0.5f);
                    pioPdf.drawBarChart_Horizontal(x3, y1[0], (float) barWidth * Float.parseFloat(getAnswerReportValue(dgnssReport4, 4, class3, class4, "").get("T_SCORE").toString()) / 100f, pioPdf.px2mm(10f), Color.WHITE, class4Color);
                } else {
                    pioPdf.drawTextC("?", x1, y1[0], width1, "", 10.5f, true, false, Color.BLACK, -0.5f);
                }

                String tmpClass5 = "";

                for(int i = 0 ; i < class5Count ; i++) {

                    tmpClass5 = StringUtils.leftPad(Integer.toString(i+1), 2, "0");

                    // 변인 T점수(백분위)
                    if(Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCORE").toString()) >= 0)
                        pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCORE").toString() + "(" + getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("P_RANK").toString() + ")", x1, y1[i+1], width1, "", 10.5f, false, false, Color.BLACK, -0.5f);
                    else{
                        pioPdf.drawTextC("?", x1, y1[i+1], width1, "", 10.5f, false, false, Color.BLACK, -0.5f);
                    }

                    // 변인 MARK
                    pioPdf.drawTextC(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_RANK").toString(), x2, y2[i], width2, "Pretendard Bold", 10f, true, false, getColorByTRank(pioPdf, getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_RANK").toString()), -0.48f);

                    // 변인 T점수 가로바 차트
                    if(Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCORE").toString()) > 0) {
                        pioPdf.setOpacity(0.65f);
                        pioPdf.drawBarChart_Horizontal(x3, y1[i + 1], (float) barWidth * Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCORE").toString()) / 100f, pioPdf.px2mm(10f), Color.white, pioPdf.hexa2Color("#9AA0A8"));
                        pioPdf.setOpacity(1f);
                    }

                    pioPdf.drawTextParagraph(getAnswerReportValue(dgnssReport5, 5, class3, class4, tmpClass5).get("T_SCRIPT").toString(), pioPdf.px2mm(121f), y3[i], pioPdf.px2mm(438f),  147.4f, "", 9.5f, Color.BLACK, true, pioPdf.px2pt(-0.46f));
                }

                // 중분류 총평
                pioPdf.drawTextParagraph(getAnswerReportValue(dgnssReport4, 4, class3, class4, "0").get("T_SCRIPT").toString(), pioPdf.px2mm(30f), pioPdf.px2mm(753.5f,"Y"), pioPdf.px2mm(528f), 152.4f, "Pretendard Medium", 10.5f, Color.BLACK, true, pioPdf.px2pt(-0.5f));

            }
            //  행동전략 강점 및 보완점
            else if(page == 9 || page == 12){

                String class3 = "";

                if(page == 9) {
                    class3 = "01";
                }
                else if(page == 12) {
                    class3 = "02";
                }

                String[] imgFile = new String[6];
                float[] x = new float[6];

                imgFile[0] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "01", "01").get("T_RANK").toString(), "dgnss20",  false, page-1);
                imgFile[1] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "01", "02").get("T_RANK").toString(), "dgnss20", false, page-1);
                imgFile[2] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "01", "03").get("T_RANK").toString(), "dgnss20", false, page-1);
                imgFile[3] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "02", "01").get("T_RANK").toString(), "dgnss20", false, page-1);
                imgFile[4] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "02", "02").get("T_RANK").toString(), "dgnss20", false, page-1);
                imgFile[5] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "02", "03").get("T_RANK").toString(), "dgnss20", false, page-1);

                for(int i = 0 ; i < 6 ; i++){
                    if(imgFile[i].contains("ico_mark_high"))
                        x[i] = pioPdf.px2mm(278f);
                    else if(imgFile[i].contains("ico_mark_mid"))
                        x[i] = pioPdf.px2mm(383f);
                    else if(imgFile[i].contains("ico_mark_low"))
                        x[i] = pioPdf.px2mm(488f);
                    else
                        x[i] = 0;
                }

                // 강점 성장기대, 보완점을 마크한다.
                if(page == 9) {
                    pioPdf.drawPicture(imgFile[0], x[0], pioPdf.px2mm(275f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                    pioPdf.drawPicture(imgFile[1], x[1], pioPdf.px2mm(309f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                    pioPdf.drawPicture(imgFile[2], x[2], pioPdf.px2mm(343f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                    pioPdf.drawPicture(imgFile[3], x[3], pioPdf.px2mm(377f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                    pioPdf.drawPicture(imgFile[4], x[4], pioPdf.px2mm(412f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                    pioPdf.drawPicture(imgFile[5], x[5], pioPdf.px2mm(446f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                }
                else if(page == 12) {
                    pioPdf.drawPicture(imgFile[0], x[0], pioPdf.px2mm(264f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                    pioPdf.drawPicture(imgFile[1], x[1], pioPdf.px2mm(298f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                    pioPdf.drawPicture(imgFile[2], x[2], pioPdf.px2mm(332f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                    pioPdf.drawPicture(imgFile[3], x[3], pioPdf.px2mm(367f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                    pioPdf.drawPicture(imgFile[4], x[4], pioPdf.px2mm(401f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                    pioPdf.drawPicture(imgFile[5], x[5], pioPdf.px2mm(435f, "Y"), pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                }

                // 학습습관
                pioPdf.drawPicture(getHabitImageFileName(class3, "01", "01", getAnswerReportValue(dgnssReport5, 5, class3, "01", "01").get("T_RANK").toString()), pioPdf.px2mm(81f),pioPdf.px2mm(690f, "Y"), pioPdf.px2mm(135f), pioPdf.px2mm(95f));
                pioPdf.drawPicture(getHabitImageFileName(class3, "01", "02", getAnswerReportValue(dgnssReport5, 5, class3, "01", "02").get("T_RANK").toString()), pioPdf.px2mm(228f),pioPdf.px2mm(690f, "Y"), pioPdf.px2mm(135f), pioPdf.px2mm(95f));
                pioPdf.drawPicture(getHabitImageFileName(class3, "01", "03", getAnswerReportValue(dgnssReport5, 5, class3, "01", "03").get("T_RANK").toString()), pioPdf.px2mm(375f),pioPdf.px2mm(690f, "Y"), pioPdf.px2mm(135f), pioPdf.px2mm(95f));

                pioPdf.drawPicture(getHabitImageFileName(class3, "02", "01", getAnswerReportValue(dgnssReport5, 5, class3, "02", "01").get("T_RANK").toString()), pioPdf.px2mm(81f),pioPdf.px2mm(795f, "Y"), pioPdf.px2mm(135f), pioPdf.px2mm(95f));
                pioPdf.drawPicture(getHabitImageFileName(class3, "02", "02", getAnswerReportValue(dgnssReport5, 5, class3, "02", "02").get("T_RANK").toString()), pioPdf.px2mm(228f),pioPdf.px2mm(795f, "Y"), pioPdf.px2mm(135f), pioPdf.px2mm(95f));
                pioPdf.drawPicture(getHabitImageFileName(class3, "02", "03", getAnswerReportValue(dgnssReport5, 5, class3, "02", "03").get("T_RANK").toString()), pioPdf.px2mm(375f),pioPdf.px2mm(795f, "Y"), pioPdf.px2mm(135f), pioPdf.px2mm(95f));


            }
            // 행동전략 강점 및 보완점
            else if(page == 15){

                String class3 = "03";

                String[] imgFile = new String[8];
                float[] x = new float[8];

                imgFile[0] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "01", "01").get("T_RANK").toString(), "dgnss20", false, page - 1);
                imgFile[1] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "01", "02").get("T_RANK").toString(), "dgnss20", false, page - 1);
                imgFile[2] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "01", "03").get("T_RANK").toString(), "dgnss20", false, page - 1);
                imgFile[3] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "02", "01").get("T_RANK").toString(), "dgnss20", false, page - 1);
                imgFile[4] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "02", "02").get("T_RANK").toString(), "dgnss20", false, page - 1);
                imgFile[5] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "02", "03").get("T_RANK").toString(), "dgnss20", false, page - 1);
                imgFile[6] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "02", "04").get("T_RANK").toString(), "dgnss20", false, page - 1);
                imgFile[7] = getMarkLevel3ImageFileName(getAnswerReportValue(dgnssReport5, 5, class3, "02", "05").get("T_RANK").toString(), "dgnss20", false, page - 1);

                float[] y2 = new float[8];

                y2[0] = pioPdf.px2mm(243f, "Y");
                y2[1] = pioPdf.px2mm(273f, "Y");
                y2[2] = pioPdf.px2mm(304f, "Y");
                y2[3] = pioPdf.px2mm(335f, "Y");
                y2[4] = pioPdf.px2mm(365f, "Y");
                y2[5] = pioPdf.px2mm(393f, "Y");
                y2[6] = pioPdf.px2mm(425f, "Y");
                y2[7] = pioPdf.px2mm(455f, "Y");

                for(int i = 0 ; i < 8 ; i++){

                    if(imgFile[i].contains("ico_mark_high"))
                        x[i] = pioPdf.px2mm(278f);
                    else if(imgFile[i].contains("ico_mark_mid"))
                        x[i] = pioPdf.px2mm(383f);
                    else if(imgFile[i].contains("ico_mark_low"))
                        x[i] = pioPdf.px2mm(488f);
                    else
                        x[i] = 0;
                }

                // 강점 성장기대, 보완점을 마크한다.
                for(int i = 0 ; i < 8 ; i++)
                    pioPdf.drawPicture(imgFile[i], x[i], y2[i],  pioPdf.px2mm(20f), pioPdf.px2mm(20f));


                // 학습습관
                pioPdf.drawPicture(getHabitImageFileName(class3, "01", "01", getAnswerReportValue(dgnssReport5, 5, class3, "01", "01").get("T_RANK").toString()),pioPdf.px2mm(41f),pioPdf.px2mm(690f, "Y"), pioPdf.px2mm(120f), pioPdf.px2mm(95f));
                pioPdf.drawPicture(getHabitImageFileName(class3, "01", "02", getAnswerReportValue(dgnssReport5, 5, class3, "01", "02").get("T_RANK").toString()),pioPdf.px2mm(171f),pioPdf.px2mm(690f, "Y"), pioPdf.px2mm(120f), pioPdf.px2mm(95f));
                pioPdf.drawPicture(getHabitImageFileName(class3, "01", "03", getAnswerReportValue(dgnssReport5, 5, class3, "01", "03").get("T_RANK").toString()),pioPdf.px2mm(301f),pioPdf.px2mm(690f, "Y"), pioPdf.px2mm(120f), pioPdf.px2mm(95f));

                pioPdf.drawPicture(getHabitImageFileName(class3, "02", "01", getAnswerReportValue(dgnssReport5, 5, class3, "02", "01").get("T_RANK").toString()),pioPdf.px2mm(431f),pioPdf.px2mm(690f, "Y"), pioPdf.px2mm(120f), pioPdf.px2mm(95f));
                pioPdf.drawPicture(getHabitImageFileName(class3, "02", "02", getAnswerReportValue(dgnssReport5, 5, class3, "02", "02").get("T_RANK").toString()),pioPdf.px2mm(41f),pioPdf.px2mm(795f, "Y"), pioPdf.px2mm(120f), pioPdf.px2mm(95f));
                pioPdf.drawPicture(getHabitImageFileName(class3, "02", "03", getAnswerReportValue(dgnssReport5, 5, class3, "02", "03").get("T_RANK").toString()),pioPdf.px2mm(171f),pioPdf.px2mm(795f, "Y"), pioPdf.px2mm(120f), pioPdf.px2mm(95f));
                pioPdf.drawPicture(getHabitImageFileName(class3, "02", "04", getAnswerReportValue(dgnssReport5, 5, class3, "02", "04").get("T_RANK").toString()),pioPdf.px2mm(301f),pioPdf.px2mm(795f, "Y"), pioPdf.px2mm(120f), pioPdf.px2mm(95f));
                pioPdf.drawPicture(getHabitImageFileName(class3, "02", "05", getAnswerReportValue(dgnssReport5, 5, class3, "02", "05").get("T_RANK").toString()),pioPdf.px2mm(431f),pioPdf.px2mm(795f, "Y"), pioPdf.px2mm(120f), pioPdf.px2mm(95f));
            }
            // 종합 해석
            else if(page == 16){

                // 종합분석표(대분류 T점수

                float xStart = pioPdf.px2mm(180.5f);        // 그래프 시작 위치
                float barWidth = pioPdf.px2mm(298f);      // 그래프 바 넓이

                float[] y = new float[23];   // 테이블 각각 높이
                float height =  pioPdf.px2mm(Double.valueOf(578f / 24.0).floatValue());   // 테이블 행 높이


                int i = 0;

                y[0] =  pioPdf.px2mm(238f, "Y");
                for(i = 1; i < 23 ; i++)
                    y[i] = y[i-1] - height;

                // 기준선에서 텍스트 높이
                float textHeight = 3f;

                int dgnssOrd = Integer.parseInt(userInfo.get("DGNSS_ORD").toString()) ;

                pioPdf.drawBarChart_Horizontal (xStart, y[0] + pioPdf.px2mm(7f), barWidth * (float) Float.parseFloat(getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("T_SCORE").toString()) / 100.0f, pioPdf.px2mm(10f), Color.white,  pioPdf.hexa2Color("#B2A7F9"));
                pioPdf.drawBarChart_Horizontal( xStart, y[7] + pioPdf.px2mm(7f), barWidth * (float) Float.parseFloat(getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("T_SCORE").toString()) / 100.0f, pioPdf.px2mm(10f), Color.white,  pioPdf.hexa2Color("#10DAFF"));
                pioPdf.drawBarChart_Horizontal( xStart, y[14] + pioPdf.px2mm(7f), barWidth * (float) Float.parseFloat(getAnswerReportValue(dgnssReport3, 3, "03","0", "0").get("T_SCORE").toString()) / 100.0f, pioPdf.px2mm(10f),  Color.white,  pioPdf.hexa2Color("#FF8A94"));

                // 종합해석 1차, 2차 변화 X 좌표
                float x1[] = new float[4];

                x1[0] = pioPdf.px2mm(488f);
                x1[1] = pioPdf.px2mm(520f);
                x1[2] = pioPdf.px2mm(548f);
                x1[3] = pioPdf.px2mm(556f);

                float x3Width = 4.74f;

                if(dgnssOrd == 1){

                    pioPdf.drawPicture("./assets/imgs/dgnss/btn/btn_total_anal_20_1st.png", pioPdf.px2mm(185f), pioPdf.px2mm(815f, "Y"), pioPdf.px2mm(220f), pioPdf.px2mm(33f));
                    pioPdf.drawText(userInfo.get("DGNSS_ORD").toString() + "차",  pioPdf.px2mm(488f), pioPdf.px2mm(206f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);

                    float[] t_score5_1 = new float[6];
                    float[] t_score5_2 = new float[6];
                    float[] t_score5_3 = new float[8];

                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "03").get("T_SCORE").toString());

                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "03").get("T_SCORE").toString());
                    t_score5_2[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "01").get("T_SCORE").toString());
                    t_score5_2[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "02").get("T_SCORE").toString());
                    t_score5_2[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "03").get("T_SCORE").toString());

                    t_score5_3[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "01").get("T_SCORE").toString());
                    t_score5_3[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "02").get("T_SCORE").toString());
                    t_score5_3[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "03").get("T_SCORE").toString());
                    t_score5_3[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "01").get("T_SCORE").toString());
                    t_score5_3[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "02").get("T_SCORE").toString());
                    t_score5_3[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "03").get("T_SCORE").toString());
                    t_score5_3[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "04").get("T_SCORE").toString());
                    t_score5_3[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "05").get("T_SCORE").toString());

                    pioPdf.drawGraph01(t_score5_1,  xStart, y[1] + (height / 2f), barWidth  , height, 1,1, pioPdf.hexa2Color("#9F91F8"));
                    pioPdf.drawGraph01(t_score5_2,  xStart, y[8] + (height / 2f), barWidth , height,1, 1, pioPdf.hexa2Color("#00CEF4"));
                    pioPdf.drawGraph01(t_score5_3,  xStart, y[15] + (height / 2f), barWidth , height, 1, 1, pioPdf.hexa2Color("#FF8A94"));

                    // 대분류 점수
                    if(!getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("T_SCORE").toString().equals("-1"))
                        pioPdf.drawText(getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("T_SCORE").toString(), x1[0], y[0] + textHeight, "", 9f, true, false);
                    else
                        pioPdf.drawText("?", x1[0], y[0] + textHeight, "", 9f, true, false);

                    if(!getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("T_SCORE").toString().equals("-1"))
                        pioPdf.drawText(getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("T_SCORE").toString(), x1[0], y[7] + textHeight, "", 9f, true, false);
                    else
                        pioPdf.drawText("?", x1[0], y[7] + textHeight, "", 9f, true, false);

                    if(!getAnswerReportValue(dgnssReport3, 3, "03", "0", "0").get("T_SCORE").toString().equals("-1"))
                        pioPdf.drawText(getAnswerReportValue(dgnssReport3, 3, "03", "0", "0").get("T_SCORE").toString(), x1[0], y[14] + textHeight, "", 9f, true, false);
                    else
                        pioPdf.drawText("?", x1[0], y[14] + textHeight, "", 9f, true, false);

                    Map<String, Object> dgnssReport;
                    int j = 1 ;
                    for(i = 0 ; i < dgnssReport5.size() ; i++){
                        dgnssReport = (Map<String, Object>)dgnssReport5.get(i);

                        if(j == 7 || j == 14 )
                            j = j + 1;

                        if(!dgnssReport.get("T_SCORE").toString().equals("-1"))
                            pioPdf.drawTextC(dgnssReport.get("T_SCORE").toString(), x1[0], y[j] + textHeight, pioPdf.px2mm(12f));
                        else
                            pioPdf.drawTextC("?", x1[0], y[j] + textHeight, pioPdf.px2mm(12f));

                        j = j + 1;
                    }

                    pioPdf.drawTextC(userInfo.get("DGNSS_ORD").toString() + "차 : " + userInfo.get("RSPNS_DT"), pioPdf.px2mm(211f), pioPdf.px2mm(802f, "Y"), pioPdf.px2mm(68f), "Pretendard", 10f, false, false, Color.black, -0.48f);

                }
                else{

                    pioPdf.drawPicture("./assets/imgs/dgnss/btn/btn_total_anal_20_1st.png", pioPdf.px2mm(185f), pioPdf.px2mm(815f, "Y"), pioPdf.px2mm(220f), pioPdf.px2mm(33f));

                    pioPdf.drawText(userInfo.get("DGNSS_ORD_FIRST").toString() + "차",  pioPdf.px2mm(488f), pioPdf.px2mm(206f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);
                    pioPdf.drawText(userInfo.get("DGNSS_ORD").toString() + "차",  pioPdf.px2mm(519f), pioPdf.px2mm(206f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);


                    float[] t_score5_1_first = new float[6];
                    float[] t_score5_2_first = new float[6];
                    float[] t_score5_3_first = new float[8];

                    float[] t_score5_1 = new float[6];
                    float[] t_score5_2 = new float[6];
                    float[] t_score5_3 = new float[8];


                    t_score5_1_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "03").get("T_SCORE_FIRST").toString());

                    t_score5_2_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "03").get("T_SCORE_FIRST").toString());

                    t_score5_3_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "03").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "04").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "05").get("T_SCORE_FIRST").toString());


                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "01", "02", "03").get("T_SCORE").toString());

                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "01", "03").get("T_SCORE").toString());
                    t_score5_2[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "01").get("T_SCORE").toString());
                    t_score5_2[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "02").get("T_SCORE").toString());
                    t_score5_2[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "02", "02", "03").get("T_SCORE").toString());

                    t_score5_3[0] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "01").get("T_SCORE").toString());
                    t_score5_3[1] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "02").get("T_SCORE").toString());
                    t_score5_3[2] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "01", "03").get("T_SCORE").toString());
                    t_score5_3[3] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "01").get("T_SCORE").toString());
                    t_score5_3[4] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "02").get("T_SCORE").toString());
                    t_score5_3[5] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "03").get("T_SCORE").toString());
                    t_score5_3[6] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "04").get("T_SCORE").toString());
                    t_score5_3[7] = Float.parseFloat(getAnswerReportValue(dgnssReport5, 5, "03", "02", "05").get("T_SCORE").toString());

                    pioPdf.drawGraph01(t_score5_1_first,  xStart, y[1] + (height / 2f), barWidth , height, 1, 1, pioPdf.hexa2Color("#A9ADB2" ));
                    pioPdf.drawGraph01(t_score5_2_first,  xStart, y[8] + (height / 2f), barWidth , height, 1, 1,  pioPdf.hexa2Color("#A9ADB2" ));
                    pioPdf.drawGraph01(t_score5_3_first,  xStart, y[15] + (height / 2f), barWidth , height, 1, 1,  pioPdf.hexa2Color("#A9ADB2" ) );

                    pioPdf.drawGraph01(t_score5_1,  xStart, y[1] + (height / 2f), barWidth , height, 1,1, pioPdf.hexa2Color("#9F91F8"));
                    pioPdf.drawGraph01(t_score5_2,  xStart, y[8] + (height / 2f), barWidth , height,1, 1, pioPdf.hexa2Color("#00CEF4"));
                    pioPdf.drawGraph01(t_score5_3,  xStart, y[15] + (height / 2f), barWidth , height, 1, 1, pioPdf.hexa2Color("#FF8A94"));

                    // 대분류 점수
                    if(!getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("T_SCORE_FIRST").toString().equals("-1"))
                        pioPdf.drawText(getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("T_SCORE_FIRST").toString(), x1[0], y[0] + textHeight, "Pretendard", 9.5f, true, false);
                    else
                        pioPdf.drawText("?", x1[0], y[0] + textHeight, "Pretendard", 9.5f, true, false);

                    if(!getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("T_SCORE_FIRST").toString().equals("-1"))
                        pioPdf.drawText(getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("T_SCORE_FIRST").toString(), x1[0], y[7] + textHeight, "Pretendard", 9.5f, true, false);
                    else
                        pioPdf.drawText("?", x1[0], y[7] + textHeight, "Pretendard", 9.5f, true, false);

                    if(!getAnswerReportValue(dgnssReport3, 3, "03", "0", "0").get("T_SCORE_FIRST").toString().equals("-1"))
                        pioPdf.drawText(getAnswerReportValue(dgnssReport3, 3, "03", "0", "0").get("T_SCORE_FIRST").toString(), x1[0], y[14] + textHeight, "Pretendard", 9.5f, true, false);
                    else
                        pioPdf.drawText("?", x1[0], y[14] + textHeight, "Pretendard", 9.5f, true, false);

                    if(!getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("T_SCORE").toString().equals("-1"))
                        pioPdf.drawText(getAnswerReportValue(dgnssReport3, 3, "01", "0", "0").get("T_SCORE").toString(), x1[1], y[0] + textHeight, "Pretendard", 9.5f, true, false);
                    else
                        pioPdf.drawText("?", x1[1], y[0] + textHeight, "Pretendard", 9.5f, true, false);

                    if(!getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("T_SCORE").toString().equals("-1"))
                        pioPdf.drawText(getAnswerReportValue(dgnssReport3, 3, "02", "0", "0").get("T_SCORE").toString(), x1[1], y[7] + textHeight, "Pretendard", 9.5f, true, false);
                    else
                        pioPdf.drawText("?", x1[1], y[7] + textHeight, "Pretendard", 9.5f, true, false);

                    if(!getAnswerReportValue(dgnssReport3, 3, "03", "0", "0").get("T_SCORE").toString().equals("-1"))
                        pioPdf.drawText(getAnswerReportValue(dgnssReport3, 3, "03", "0", "0").get("T_SCORE").toString(), x1[1], y[14] + textHeight, "Pretendard", 9.5f, true, false);
                    else
                        pioPdf.drawText("?", x1[1], y[14] + textHeight, "Pretendard", 9.5f, true, false);

                    //   0 , 7 , 17
                    for(i = 0 ; i < 3 ; i++) {
                        if(!getAnswerReportValue(dgnssReport3,  3,  StringUtils.leftPad(Integer.toString(i+1), 2, "0"), "0", "0").get("T_SCORE").toString().equals("-1") && !getAnswerReportValue(dgnssReport3,  3,  StringUtils.leftPad(Integer.toString(i+1), 2, "0"), "0", "0").get("T_SCORE_FIRST").toString().equals("-1")){
                            if (getAnswerReportValue(dgnssReport3, 3, StringUtils.leftPad(Integer.toString(i + 1), 2, "0"), "0", "0").get("T_SCORE_GAP").toString().contains("-")) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x1[2], y[7 * i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(getAnswerReportValue(dgnssReport3, 3, StringUtils.leftPad(Integer.toString(i + 1), 2, "0"), "0", "0").get("T_SCORE_GAP").toString(), x1[3], y[7 * i] + textHeight, x3Width, "", 9.5f, true, false, pioPdf.hexa2Color("#FF4800"));
                            } else if (getAnswerReportValue(dgnssReport3, 3, StringUtils.leftPad(Integer.toString(i + 1), 2, "0"), "0", "0").get("T_SCORE_GAP").toString().equals("0")) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[7 * i] + (height / 2f), pioPdf.px2mm(7f), pioPdf.px2mm(1f));
                            } else {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x1[2], y[7 * i] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(getAnswerReportValue(dgnssReport3, 3, StringUtils.leftPad(Integer.toString(i + 1), 2, "0"), "0", "0").get("T_SCORE_GAP").toString(), x1[3], y[7 * i] + textHeight, x3Width, "", 9.5f, true, false, pioPdf.hexa2Color("#0B9DFF"));
                            }
                        }
                    }

                    Map<String, Object> dgnssReport;
                    int j = 1 ;
                    for(i = 0 ; i < dgnssReport5.size() ; i++){
                        dgnssReport = (Map<String, Object>)dgnssReport5.get(i);

                        if(j == 7 || j == 14 )
                            j = j + 1;

                        if(!dgnssReport.get("T_SCORE_FIRST").toString().equals("-1"))
                            pioPdf.drawText(dgnssReport.get("T_SCORE_FIRST").toString(), x1[0], y[j] + textHeight);
                        else
                            pioPdf.drawText("?", x1[0], y[j] + textHeight);

                        if(!dgnssReport.get("T_SCORE").toString().equals("-1"))
                            pioPdf.drawText(dgnssReport.get("T_SCORE").toString(), x1[1], y[j] + textHeight);
                        else
                            pioPdf.drawText("?", x1[1], y[j] + textHeight);

                        if(!dgnssReport.get("T_SCORE_FIRST").toString().equals("-1") && !dgnssReport.get("T_SCORE").toString().equals("-1")) {
                            if (dgnssReport.get("T_SCORE_GAP").toString().contains("-")) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x1[2], y[j] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(dgnssReport.get("T_SCORE_GAP").toString(), x1[3], y[j] + textHeight, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#FF4800"));
                            } else if (dgnssReport.get("T_SCORE_GAP").toString().equals("0")) {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[j] + (height / 2f), pioPdf.px2mm(7f), pioPdf.px2mm(1f));
                            } else {
                                pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x1[2], y[j] + textHeight, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                                pioPdf.drawTextC(dgnssReport.get("T_SCORE_GAP").toString(), x1[3], y[j] + textHeight, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                            }
                        }

                        j = j + 1;
                    }


                    pioPdf.drawTextC(userInfo.get("DGNSS_ORD_FIRST").toString() + "차 : " + userInfo.get("RSPNS_DT_FIRST"), pioPdf.px2mm(211f), pioPdf.px2mm(802f, "Y"), pioPdf.px2mm(68f), "Pretendard Medium", 10f, false, false, pioPdf.hexa2Color("#A9ADB2"), -0.48f);
                    pioPdf.drawTextC(userInfo.get("DGNSS_ORD").toString() + "차 : " + userInfo.get("RSPNS_DT"), pioPdf.px2mm(310f), pioPdf.px2mm(802f, "Y"), pioPdf.px2mm(72f), "Pretendard", 10f, false, false, Color.black, -0.48f);


                }
            }
            // 종합 해석 02. 척도조합 해석
            else if(page == 17) {
                int dgnssOrd = Integer.parseInt(userInfo.get("DGNSS_ORD").toString());

                // 보통 #00D282 높음, 매우 높음 #0B9DFF   낮음, 매우 낮음 #FF4800
                String[] score4_first = new String[6];
                String[] score4 = new String[6];
                int[] tscore_gap = new int[6];


                float[] x1 = new float[3];

                x1[0] = pioPdf.px2mm(102f);
                x1[1] = pioPdf.px2mm(380f);
                x1[2] = pioPdf.px2mm(428f);


                float[] x = new float[4];

                // 2차시 변구
                x[0] = pioPdf.px2mm(63f);
                x[1] = pioPdf.px2mm(142f);
                x[2] = pioPdf.px2mm(378f);
                x[3] = pioPdf.px2mm(485f);

                float[] y = new float[6];

                y[0] = pioPdf.px2mm(492f, "Y");
                y[1] = pioPdf.px2mm(293f, "Y");
                y[2] = pioPdf.px2mm(342f, "Y");
                y[3] = pioPdf.px2mm(391f, "Y");
                y[4] = pioPdf.px2mm(440f, "Y");
                y[5] = pioPdf.px2mm(489f, "Y");

                float alignWidth = pioPdf.px2mm(74f);

                Map<String, Object> dgnssReport;

                for(int i = 0 ; i < dgnssReport4.size() ; i++) {
                    dgnssReport = (Map<String, Object>) dgnssReport4.get(i);

                    if(dgnssReport.get("T_RANK").toString().equals("?"))
                        score4[i] = dgnssReport.get("T_RANK").toString();
                    else
                        score4[i] = dgnssReport.get("T_RANK").toString() + " " + dgnssReport.get("T_SCORE").toString() + "(" + dgnssReport.get("P_RANK").toString() + ")";

                    if(dgnssOrd > 1) {
                        if(dgnssReport.get("T_RANK_FIRST").toString().equals("?"))
                            score4_first[i] = dgnssReport.get("T_RANK_FIRST").toString();
                        else
                            score4_first[i] = dgnssReport.get("T_RANK_FIRST").toString() + " " + dgnssReport.get("T_SCORE_FIRST").toString() + "(" + dgnssReport.get("P_RANK_FIRST").toString() + ")";

                        tscore_gap[i] = Integer.parseInt(dgnssReport.get("T_SCORE_GAP").toString());

                    }
                }

                if(dgnssOrd == 1){
                    for(int i = 0 ; i < 6; i++){
                        if(i == 0) {
                            pioPdf.drawTextC(userInfo.get("DGNSS_ORD").toString() + "차", x1[0], y[i] + 7f, alignWidth, "", 11f, false, false, Color.BLACK, pioPdf.px2pt(-0.53f));
                            pioPdf.drawTextC(score4[i], x1[0], y[i], alignWidth, "Pretendard SemiBold", 10f, false, false, getColorByTRank(pioPdf, score4[i]));
                        }
                        else {
                            pioPdf.drawTextC(userInfo.get("DGNSS_ORD").toString() + "차", x1[1], y[i] + 2.2f, alignWidth, "", 9.5f, false, false, Color.BLACK);
                            pioPdf.drawTextC(score4[i], x1[2], y[i] + 2.2f, alignWidth, "Pretendard SemiBold", 9.5f, false, false, getColorByTRank(pioPdf, score4[i]));
                        }
                    }

                }
                else{
                    for(int i = 0 ; i < 6; i++){
                        if(i == 0) {
                            pioPdf.drawTextC(userInfo.get("DGNSS_ORD_FIRST").toString() + "차", x[0], y[i] + 7f, alignWidth, "", 11f, false, false, Color.BLACK, -0.46f);
                            pioPdf.drawTextC(score4_first[i], x[0], y[i], alignWidth, "Pretendard Semibold", 10f, false, false, getColorByTRank(pioPdf, score4_first[i]));
                        }
                        else {
                            pioPdf.drawTextC(userInfo.get("DGNSS_ORD_FIRST").toString() + "차", x[2], y[i] + 7.5f, alignWidth, "", 9.5f, false, false, Color.BLACK, -0.46f);
                            pioPdf.drawTextC(score4_first[i], x[2], y[i]+2.5f, alignWidth, "Pretendard SemiBold", 9.5f, false, false, getColorByTRank(pioPdf, score4_first[i]));
                        }

                        if(i == 0) {
                            pioPdf.drawTextC(userInfo.get("DGNSS_ORD").toString() + "차", x[1], y[i] + 7f, alignWidth, "", 11f, false, false, Color.BLACK);
                            pioPdf.drawTextC(score4[i], x[1], y[i], alignWidth, "Pretendard SemiBold", 10f, false, false, getColorByTRank(pioPdf, score4[i]));
                        }
                        else {
                            pioPdf.drawTextC(userInfo.get("DGNSS_ORD").toString() + "차", x[3], y[i] + 7.5f, alignWidth, "", 9.5f, false, false, Color.BLACK);
                            pioPdf.drawTextC(score4[i], x[3], y[i]+2.5f, alignWidth, "Pretendard SemiBold", 9.5f, true, false, getColorByTRank(pioPdf, score4[i]));

                            if(!score4[i].equals("?") && !score4_first[i].equals("?")) {
                                if (tscore_gap[i] < 0)
                                    pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_more.png", pioPdf.px2mm(459f), y[i], pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                                else if (tscore_gap[i] == 0)
                                    pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_equal.png", pioPdf.px2mm(459f), y[i], pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                                else
                                    pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_less.png", pioPdf.px2mm(459f), y[i], pioPdf.px2mm(20f), pioPdf.px2mm(20f));
                            }

                        }

                    }
                }
            }

        } catch (NullPointerException e) {
            log.error("DGNSS20 페이지 생성 실패 - 데이터 누락: {}", e.getMessage());
        } catch (IOException e) {
            log.error("DGNSS20 페이지 생성 실패 - I/O 오류: {}", e.getMessage());
        } catch (Exception e) {
            log.error("DGNSS20 페이지 생성 실패 - 예상치 못한 오류: {}", e.getMessage());
        }

    }

    public void addDgnssPage_DGNSS20_COCH(PioPdf pioPdf, PDDocument doc, PDPageContentStream cont, int page, Map<String, Object> testInfo, List<Map<String, Object>> dgnssReportLS, List<Map<String, Object>> dgnssReportSection, List<Map<String, Object>> dgnssReportValidity, List<Map<String, Object>> dgnssReportMem, List<Map<String, Object>> dgnssReportStat3, List<Map<String, Object>> dgnssReportStat5) throws IOException
    {
        float x[] = new float[15];
        float y[] = new float[31];

        float height = pioPdf.px2mm(20f);
        float textHeight = pioPdf.px2mm(6f);
        float fontSize = 9.5f;
        y[0] = pioPdf.px2mm(234f, "Y");

        float bgPadding = pioPdf.px2mm(0.5f);
        Color redBg = pioPdf.hexa2Color("#FFC7B2");
        Color yellowBg = pioPdf.hexa2Color("#FFEE99");
        Color redText= pioPdf.hexa2Color("#FF4800");
        boolean isVivaClassNickName = StringUtils.isNotEmpty(MapUtils.getString(testInfo, "clsType", "")) && StringUtils.equals("5", MapUtils.getString(testInfo, "clsType", ""));
        for(int i = 1 ; i < 31 ; i++)
            y[i] = y[i-1] - height;

        try {

            if(page > 1 && page != 13) {
                pioPdf.drawHeaderCoch(page, testInfo, isVivaClassNickName);
                pioPdf.drawFooter();
            }


            if(page == 1){
                float x1 = pioPdf.px2mm(296f);
                float width1 = pioPdf.px2mm(88f);
                float fontSize1 = 12f;

                // 검사일
                pioPdf.drawTextC((String)testInfo.get("TEST_DT_KO"), x1, pioPdf.px2mm(651f, "Y"), width1, "Pretendard Medium", fontSize1, false, false, Color.BLACK, -0.58f);
                // 학교
                pioPdf.drawTextC((String)testInfo.get("SCH_NM"), x1, pioPdf.px2mm(682f, "Y"), width1, "Pretendard Medium", fontSize1, false, false, Color.BLACK, -0.58f);
                // 학급
                if (isVivaClassNickName) {
                    pioPdf.drawTextC((String)testInfo.get("nickNameClass"), x1, pioPdf.px2mm(714f, "Y"), width1, "Pretendard Medium", fontSize1, false, false, Color.BLACK, -0.58f);
                } else {
                    pioPdf.drawTextC((String)testInfo.get("MEM_GRADE_NM") + " " +  (String)testInfo.get("CLASS_NM"), x1, pioPdf.px2mm(714f, "Y"), width1, "Pretendard Medium", fontSize1, false, false, Color.BLACK, -0.58f);
                }
            }
            // 검사결과(신뢰도 지표, 학습현황)
            else if(page == 5){

                x[0] = pioPdf.px2mm(15f);
                x[1] = pioPdf.px2mm(44f);
                x[2] = pioPdf.px2mm(106f);
                x[3] = pioPdf.px2mm(134f);
                x[4] = pioPdf.px2mm(172f);
                x[5] = pioPdf.px2mm(202f);
                x[6] = pioPdf.px2mm(254f);
                x[7] = pioPdf.px2mm(314f);
                x[8] = pioPdf.px2mm(374f);
                x[9] = pioPdf.px2mm(445f);
                x[10] = pioPdf.px2mm(510f);
                x[11] = pioPdf.px2mm(575f);

                for(int i= 0 ; i < dgnssReportLS.size(); i++) {

                    if(dgnssReportLS.get(i).get("CLASS_NO") == null)
                        pioPdf.drawTextC( "?", x[0] , y[i] + textHeight, x[1] - x[0], "", fontSize);
                    else
                        pioPdf.drawTextC( dgnssReportLS.get(i).get("CLASS_NO").toString(), x[0] , y[i] + textHeight, x[1] - x[0], "", fontSize);

                    if(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM01_MARK").toString().equals("주의") || dgnssReportLS.get(i).get("COCH_DGNSS_QESITM02_MARK").toString().equals("주의") || StringUtils.equals("예", dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString()) ) {
                        pioPdf.drawRectangle(x[1], y[i], x[2] - x[1], height, Color.BLACK, redBg, 1f);
                    }

                    pioPdf.drawTextC(dgnssReportLS.get(i).get("MEM_NM").toString(), x[1] , y[i] + textHeight, x[2] - x[1], "", fontSize);

                    pioPdf.drawTextC(dgnssReportLS.get(i).get("MEM_GENDER_NM").toString(), x[2] , y[i] + textHeight, x[3] - x[2], "", fontSize);

                    if(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM02_MARK").toString().equals("주의")) {
                        pioPdf.drawRectangle(x[3], y[i], x[4] - x[3], height, Color.BLACK, redBg);
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM02_MARK").toString(), x[3], y[i] + textHeight, x[4] - x[3], "", fontSize, false, false,  redText);
                    }else{
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM02_MARK").toString(), x[3], y[i] + textHeight, x[4] - x[3], "", fontSize);
                    }

                    if(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM01_MARK").toString().equals("주의")) {
                        pioPdf.drawRectangle(x[4], y[i], x[5] - x[4], height, Color.BLACK, redBg);
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM01_MARK").toString(), x[4], y[i] + textHeight, x[5] - x[4], "", fontSize, false, false, redText);
                    }else{
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM01_MARK").toString(), x[4], y[i] + textHeight, x[5] - x[4], "", fontSize);
                    }

                    // 무응답수(10건 이상이면 red)
                    // AIDT에서는 무응답이 없기때문에 연속동일반응으로 대체
//                    if(Integer.parseInt(dgnssReportLS.get(i).get("NO_ANS_CNT").toString()) >= 10) {
//                        pioPdf.drawRectangle(x[5], y[i], x[6] - x[5], height, Color.BLACK, redBg);
//                        pioPdf.drawTextC(dgnssReportLS.get(i).get("NO_ANS_CNT").toString(), x[5], y[i] + textHeight, x[6] - x[5], "", fontSize, false, false, redText);
//                    }else{
//                        pioPdf.drawTextC(dgnssReportLS.get(i).get("NO_ANS_CNT").toString(), x[5], y[i] + textHeight, x[6] - x[5], "", fontSize);
//                    }

                   // 연속동일반응
                    if (StringUtils.equals("예", dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString())) {
                        pioPdf.drawRectangle(x[5], y[i], x[6] - x[5], height, Color.BLACK, redBg);
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString(), x[5], y[i] + textHeight, x[6] - x[5], "", fontSize, false, false, redText);
                    } else {
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString(), x[5], y[i] + textHeight, x[6] - x[5], "", fontSize);
                    }

                   // 학업성취도
                    if(dgnssReportLS.get(i).get("LS_ANS01").toString().equals("1")){
                        pioPdf.drawRectangle(x[6], y[i], x[7] - x[6], height, Color.BLACK, redBg);
                        pioPdf.drawTextC("매우 낮음", x[6], y[i] + textHeight, x[7] - x[6], "", fontSize, false, false, redText);
                    }else if(dgnssReportLS.get(i).get("LS_ANS01").toString().equals("2")){
                        pioPdf.drawRectangle(x[6], y[i], x[7] - x[6], height, Color.BLACK, yellowBg);
                        pioPdf.drawTextC("낮음", x[6], y[i] + textHeight, x[7] - x[6], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS01").toString().equals("3")) {
                        pioPdf.drawTextC("보통", x[6], y[i] + textHeight, x[7] - x[6], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS01").toString().equals("4")) {
                        pioPdf.drawTextC("높음", x[6], y[i] + textHeight, x[7] - x[6], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS01").toString().equals("5")) {
                        pioPdf.drawTextC("매우 높음", x[6], y[i] + textHeight, x[7] - x[6], "", fontSize);
                    }

                    // 성적만족도
                    if(dgnssReportLS.get(i).get("LS_ANS02").toString().equals("1")){
                        pioPdf.drawRectangle(x[7], y[i], x[8] - x[7], height, Color.BLACK, redBg);
                        pioPdf.drawTextC("매우 낮음", x[7], y[i] + textHeight, x[8] - x[7], "", fontSize, false, false, redText);
                    }else if(dgnssReportLS.get(i).get("LS_ANS02").toString().equals("2")){
                        pioPdf.drawRectangle(x[7], y[i], x[8] - x[7], height, Color.BLACK, yellowBg);
                        pioPdf.drawTextC("낮음", x[7], y[i] + textHeight, x[8] - x[7], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS02").toString().equals("3")) {
                        pioPdf.drawTextC("보통", x[7], y[i] + textHeight, x[8] - x[7], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS02").toString().equals("4")) {
                        pioPdf.drawTextC("높음", x[7], y[i] + textHeight, x[8] - x[7], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS02").toString().equals("5")) {
                        pioPdf.drawTextC("매우 높음", x[7], y[i] + textHeight, x[8] - x[7], "", fontSize);
                    }

                    // 공부이유
                    if(dgnssReportLS.get(i).get("LS_ANS03").toString().equals("1")){
                        pioPdf.drawTextC("흥미를 느껴서", x[8], y[i] + textHeight, x[9] - x[8], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS03").toString().equals("2")){
                        pioPdf.drawTextC("미래를 위해서", x[8], y[i] + textHeight, x[9] - x[8], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS03").toString().equals("3")) {
                        pioPdf.drawTextC("대학 진학", x[8], y[i] + textHeight, x[9] - x[8], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS03").toString().equals("4")) {
                        pioPdf.drawTextC("주변 기대 때문에", x[8], y[i] + textHeight, x[9] - x[8], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS03").toString().equals("5")) {
                        pioPdf.drawTextC("모르겠음", x[8], y[i] + textHeight, x[9] - x[8], "", fontSize);
                    }

                    // 1일 혼공 시간
                    if(dgnssReportLS.get(i).get("LS_ANS04").toString().equals("1")){
                        pioPdf.drawTextC("전혀 안함", x[9], y[i] + textHeight, x[10] - x[9], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS04").toString().equals("2")){
                        pioPdf.drawTextC("1시간 미만", x[9], y[i] + textHeight, x[10] - x[9], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS04").toString().equals("3")) {
                        pioPdf.drawTextC("1~2시간", x[9], y[i] + textHeight, x[10] - x[9], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS04").toString().equals("4")) {
                        pioPdf.drawTextC("2~3시간", x[9], y[i] + textHeight, x[10] - x[9], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS04").toString().equals("5")) {
                        pioPdf.drawTextC("3시간 이상", x[9], y[i] + textHeight, x[10] - x[9], "", fontSize);
                    }

                    // 학습고민 상담
                    if(dgnssReportLS.get(i).get("LS_ANS05").toString().equals("1")){
                        pioPdf.drawTextC("친구", x[10], y[i] + textHeight, x[11] - x[10], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS05").toString().equals("2")){
                        pioPdf.drawTextC("선생님", x[10], y[i] + textHeight, x[11] - x[10], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS05").toString().equals("3")) {
                        pioPdf.drawTextC("가족", x[10], y[i] + textHeight, x[11] - x[10], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS05").toString().equals("4")) {
                        pioPdf.drawTextC("상담 전문가", x[10], y[i] + textHeight, x[11] - x[10], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS05").toString().equals("5")) {
                        pioPdf.drawTextC("기타", x[10], y[i] + textHeight, x[11] - x[10], "", fontSize);
                    }
                }

            }
            // 검사 결과 : 동기전략(학습원동력, 정서조절)
            else if (page == 6 || page == 7 || page == 8 || page == 9) {
                int tScore[] = new int[10];
                int tScoreCnt = 0;

                if(page == 6 || page == 7) {

                    tScoreCnt = 8;

                    x[0] = pioPdf.px2mm(15f);
                    x[1] = pioPdf.px2mm(44f);
                    x[2] = pioPdf.px2mm(106f);
                    x[3] = pioPdf.px2mm(134f);
                    x[4] = pioPdf.px2mm(174f);
                    x[5] = pioPdf.px2mm(234f);
                    x[6] = pioPdf.px2mm(294f);
                    x[7] = pioPdf.px2mm(354f);
                    x[8] = pioPdf.px2mm(394f);
                    x[9] = pioPdf.px2mm(454f);
                    x[10] = pioPdf.px2mm(514f);
                    x[11] = pioPdf.px2mm(575f);
                }else if(page == 8){
                    tScoreCnt = 10;

                    x[0] = pioPdf.px2mm(15f);
                    x[1] = pioPdf.px2mm(44f);
                    x[2] = pioPdf.px2mm(106f);
                    x[3] = pioPdf.px2mm(134f);
                    x[4] = pioPdf.px2mm(166f);
                    x[5] = pioPdf.px2mm(213f);
                    x[6] = pioPdf.px2mm(260f);
                    x[7] = pioPdf.px2mm(307f);
                    x[8] = pioPdf.px2mm(339f);
                    x[9] = pioPdf.px2mm(386f);
                    x[10] = pioPdf.px2mm(433f);
                    x[11] = pioPdf.px2mm(480f);
                    x[12] = pioPdf.px2mm(527f);
                    x[13] = pioPdf.px2mm(575f);
                }else if(page == 9){
                    tScoreCnt = 9;

                    x[0] = pioPdf.px2mm(15f);
                    x[1] = pioPdf.px2mm(44f);
                    x[2] = pioPdf.px2mm(108f);
                    x[3] = pioPdf.px2mm(136f);
                    x[4] = pioPdf.px2mm(166f);
                    x[5] = pioPdf.px2mm(224f);
                    x[6] = pioPdf.px2mm(282f);
                    x[7] = pioPdf.px2mm(312f);
                    x[8] = pioPdf.px2mm(370f);
                    x[9] = pioPdf.px2mm(428f);
                    x[10] = pioPdf.px2mm(458f);
                    x[11] = pioPdf.px2mm(516f);
                    x[12] = pioPdf.px2mm(575f);
                }

                for(int i= 0 ; i < dgnssReportLS.size();i++) {
                    if(page == 6) {
                        tScore[0] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_01").toString());
                        tScore[1] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_01_01").toString());
                        tScore[2] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_01_02").toString());
                        tScore[3] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_01_03").toString());

                        tScore[4] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_02").toString());
                        tScore[5] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_02_01").toString());
                        tScore[6] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_02_02").toString());
                        tScore[7] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_02_03").toString());
                    }else if(page == 7){
                        tScore[0] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_01").toString());
                        tScore[1] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_01_01").toString());
                        tScore[2] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_01_02").toString());
                        tScore[3] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_01_03").toString());

                        tScore[4] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02").toString());
                        tScore[5] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02_01").toString());
                        tScore[6] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02_02").toString());
                        tScore[7] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02_03").toString());
                    }else if(page == 8){
                        tScore[0] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_01").toString());
                        tScore[1] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_01_01").toString());
                        tScore[2] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_01_02").toString());
                        tScore[3] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_01_03").toString());

                        tScore[4] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02").toString());
                        tScore[5] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02_01").toString());
                        tScore[6] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02_02").toString());
                        tScore[7] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02_03").toString());
                        tScore[8] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02_04").toString());
                        tScore[9] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02_05").toString());
                    }
                    else if(page == 9){
                        tScore[0] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01").toString());
                        tScore[1] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_01").toString());
                        tScore[2] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_02").toString());
                        tScore[3] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02").toString());
                        tScore[4] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_01").toString());
                        tScore[5] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02").toString());
                        tScore[6] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03").toString());
                        tScore[7] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_01").toString());
                        tScore[8] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02").toString());
                    }

                    if(dgnssReportLS.get(i).get("CLASS_NO") == null)
                        pioPdf.drawTextC("?", x[0] , y[i] + textHeight, x[1] - x[0], "", fontSize);
                    else
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("CLASS_NO").toString(), x[0] , y[i] + textHeight, x[1] - x[0], "", fontSize);

                    if(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM01_MARK").toString().equals("주의") || dgnssReportLS.get(i).get("COCH_DGNSS_QESITM02_MARK").toString().equals("주의") || StringUtils.equals("예", dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString()) )
                        pioPdf.drawRectangle(x[1], y[i], x[2] - x[1], height, Color.BLACK, redBg);

                    pioPdf.drawTextC(dgnssReportLS.get(i).get("MEM_NM").toString(), x[1] , y[i] + textHeight, x[2] - x[1], "", fontSize);

                    pioPdf.drawTextC(dgnssReportLS.get(i).get("MEM_GENDER_NM").toString(), x[2] , y[i] + textHeight, x[3] - x[2], "", fontSize);


                    for(int j = 0 ; j < tScoreCnt ; j++){
                        if(tScore[j] < 0){
                            pioPdf.drawTextC("?", x[j + 3], y[i] + textHeight, x[j + 4] - x[j + 3], "", fontSize);
                        }else{
                            if (tScore[j] < 30) {
                                pioPdf.drawRectangle(x[j + 3], y[i], x[j + 4] - x[j + 3], height, Color.BLACK, redBg);
                            } else if (tScore[j] >= 30 && tScore[j] < 40) {
                                pioPdf.drawRectangle(x[j + 3], y[i], x[j + 4] - x[j + 3], height, Color.BLACK, yellowBg);
                            }
                            pioPdf.drawTextC(String.valueOf(tScore[j]), x[j + 3], y[i] + textHeight, x[j + 4] - x[j + 3], "", fontSize);
                        }
                    }

                }

            }
            // 상담 지도가 필요한 학생
            else if(page == 10){

                float x10 = pioPdf.px2mm(156f);
                float x10_width = pioPdf.px2mm(410f);
                float y10[] = new float[9];

                String memList[] = new String[9];


                memList[0] = dgnssReportMem.get(0).get("QESITM02_MEM").toString();
                memList[1] = dgnssReportMem.get(0).get("QESITM01_MEM").toString();
                memList[2] = dgnssReportMem.get(0).get("REPEATED_RESPONSE_YN").toString();

                memList[3] = dgnssReportMem.get(0).get("SECTION_MEM_01_01").toString();
                memList[4] = dgnssReportMem.get(0).get("SECTION_MEM_01_02").toString();
                memList[5] = dgnssReportMem.get(0).get("SECTION_MEM_02_01").toString();
                memList[6] = dgnssReportMem.get(0).get("SECTION_MEM_02_02").toString();
                memList[7] = dgnssReportMem.get(0).get("SECTION_MEM_03_01").toString();
                memList[8] = dgnssReportMem.get(0).get("SECTION_MEM_03_02").toString();

                float y10textHeight = 17f;

                y10[0] = pioPdf.px2mm(221f + y10textHeight, "Y");
                y10[1] = pioPdf.px2mm(278f + y10textHeight, "Y");
                y10[2] = pioPdf.px2mm(335f + y10textHeight, "Y");
                y10[3] = pioPdf.px2mm(421f + y10textHeight, "Y");
                y10[4] = pioPdf.px2mm(476f + y10textHeight, "Y");
                y10[5] = pioPdf.px2mm(561f + y10textHeight, "Y");
                y10[6] = pioPdf.px2mm(617f + y10textHeight, "Y");
                y10[7] = pioPdf.px2mm(702f + y10textHeight, "Y");
                y10[8] = pioPdf.px2mm(758f + y10textHeight, "Y");

                for(int i = 0; i < 9; i++) {
                    pioPdf.drawTextParagraph(memList[i], x10, y10[i], x10_width, 150, "", 10.3f, Color.BLACK, true, -0.49f);
                }

            }
            // 종합해석(반평균 분석)
            else if(page == 11) {
                // 종합분석표(대분류 T점수)

                float xStart = pioPdf.px2mm(180.5f);      // 그래프 시작 위치
                float barWidth = pioPdf.px2mm(298f);      // 그래프 바 넓이

                float[] y11 = new float[23];   // 테이블 각각 높이
                float height11 =  pioPdf.px2mm(24f);   // 테이블 행 높이

                int i = 0;

                y11[0] =  pioPdf.px2mm(238f, "Y");
                for(i = 1; i < 23 ; i++)
                    y11[i] = y11[i-1] - height11;

                // 기준선에서 텍스트 높이
                float textHeight11 = 3f;

                int testOrd = Integer.parseInt(testInfo.get("TEST_ORD").toString()) ;

                pioPdf.drawBarChart_Horizontal(xStart, y11[0] + pioPdf.px2mm(7f), barWidth * (float) Float.parseFloat(getAnswerReportValue(dgnssReportStat3, 3, "01", "0", "0").get("T_SCORE").toString()) / 100.0f, pioPdf.px2mm(10f), Color.white,  pioPdf.hexa2Color("#B2A7F9"));
                pioPdf.drawBarChart_Horizontal(xStart, y11[7] + pioPdf.px2mm(7f), barWidth * (float) Float.parseFloat(getAnswerReportValue(dgnssReportStat3, 3, "02", "0", "0").get("T_SCORE").toString()) / 100.0f, pioPdf.px2mm(10f), Color.white,  pioPdf.hexa2Color("#10DAFF"));
                pioPdf.drawBarChart_Horizontal(xStart, y11[14] + pioPdf.px2mm(7f), barWidth * (float) Float.parseFloat(getAnswerReportValue(dgnssReportStat3, 3, "03","0", "0").get("T_SCORE").toString()) / 100.0f, pioPdf.px2mm(10f),  Color.white,  pioPdf.hexa2Color("#FF8A94"));

                // 종합해석 1차, 2차 변화 X 좌표
                float x1[] = new float[4];

                x1[0] = pioPdf.px2mm(488f);
                x1[1] = pioPdf.px2mm(520f);
                x1[2] = pioPdf.px2mm(548f);
                x1[3] = pioPdf.px2mm(556f);

                float x3Width = 4.74f;

                if(testOrd == 1){

                    pioPdf.drawPicture("./assets/imgs/dgnss/btn/btn_total_anal_20_1st.png", pioPdf.px2mm(185f), pioPdf.px2mm(815f, "Y"), pioPdf.px2mm(220f), pioPdf.px2mm(33f));

                    pioPdf.drawText(testInfo.get("TEST_ORD").toString() + "차",  pioPdf.px2mm(488f), pioPdf.px2mm(206f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);

                    float[] t_score5_1 = new float[6];
                    float[] t_score5_2 = new float[6];
                    float[] t_score5_3 = new float[8];

                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "03").get("T_SCORE").toString());

                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "03").get("T_SCORE").toString());
                    t_score5_2[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "01").get("T_SCORE").toString());
                    t_score5_2[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "02").get("T_SCORE").toString());
                    t_score5_2[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "03").get("T_SCORE").toString());

                    t_score5_3[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "01").get("T_SCORE").toString());
                    t_score5_3[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "02").get("T_SCORE").toString());
                    t_score5_3[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "03").get("T_SCORE").toString());
                    t_score5_3[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "01").get("T_SCORE").toString());
                    t_score5_3[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "02").get("T_SCORE").toString());
                    t_score5_3[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "03").get("T_SCORE").toString());
                    t_score5_3[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "04").get("T_SCORE").toString());
                    t_score5_3[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "05").get("T_SCORE").toString());

                    pioPdf.drawGraph01(t_score5_1,  xStart, y11[1] + (height11 / 2f), barWidth  , height11, 1,1, pioPdf.hexa2Color("#6B30F8"));
                    pioPdf.drawGraph01(t_score5_2,  xStart, y11[8] + (height11 / 2f), barWidth , height11,1, 1, pioPdf.hexa2Color("#41BEFF"));
                    pioPdf.drawGraph01(t_score5_3,  xStart, y11[15] + (height11 / 2f), barWidth , height11, 1, 1, pioPdf.hexa2Color("#FF8A94"));

                    // 대분류 점수
                    pioPdf.drawText(getAnswerReportValue(dgnssReportStat3, 3, "01", "0", "0").get("T_SCORE").toString(), x1[0], y11[0] + textHeight11, "", 9f, true, false);
                    pioPdf.drawText(getAnswerReportValue(dgnssReportStat3, 3, "02", "0", "0").get("T_SCORE").toString(), x1[0], y11[7] + textHeight11, "", 9f, true, false);
                    pioPdf.drawText(getAnswerReportValue(dgnssReportStat3, 3, "03", "0", "0").get("T_SCORE").toString(), x1[0], y11[14] + textHeight11, "", 9f, true, false);

                    Map<String, Object> dgnssReport;
                    int j = 1 ;
                    for(i = 0 ; i < dgnssReportStat5.size() ; i++){
                        dgnssReport = (Map<String, Object>)dgnssReportStat5.get(i);

                        if(j == 7 || j == 14 )
                            j = j + 1;

                        pioPdf.drawTextC(dgnssReport.get("T_SCORE").toString(), x1[0], y11[j] + textHeight11, pioPdf.px2mm(12f));

                        j = j + 1;
                    }

                    pioPdf.drawTextC(testInfo.get("TEST_ORD").toString() + "차 : " + testInfo.get("TEST_DT"), pioPdf.px2mm(211f), pioPdf.px2mm(802f, "Y"), pioPdf.px2mm(68f), "Pretendard", 10f, false, false, Color.black, -0.48f);

                }
                else{
                    pioPdf.drawPicture("./assets/imgs/dgnss/btn/btn_total_anal_20_nst.png", pioPdf.px2mm(185f), pioPdf.px2mm(815f, "Y"), pioPdf.px2mm(220f), pioPdf.px2mm(33f));

                    pioPdf.drawText(testInfo.get("TEST_ORD_FIRST").toString() + "차",  pioPdf.px2mm(488f), pioPdf.px2mm(206f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);
                    pioPdf.drawText(testInfo.get("TEST_ORD").toString() + "차",  pioPdf.px2mm(519f), pioPdf.px2mm(206f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);


                    float[] t_score5_1_first = new float[6];
                    float[] t_score5_2_first = new float[6];
                    float[] t_score5_3_first = new float[8];

                    float[] t_score5_1 = new float[6];
                    float[] t_score5_2 = new float[6];
                    float[] t_score5_3 = new float[8];


                    t_score5_1_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "03").get("T_SCORE_FIRST").toString());

                    t_score5_2_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "03").get("T_SCORE_FIRST").toString());

                    t_score5_3_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "03").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "04").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "05").get("T_SCORE_FIRST").toString());


                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "03").get("T_SCORE").toString());

                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "03").get("T_SCORE").toString());
                    t_score5_2[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "01").get("T_SCORE").toString());
                    t_score5_2[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "02").get("T_SCORE").toString());
                    t_score5_2[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "03").get("T_SCORE").toString());

                    t_score5_3[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "01").get("T_SCORE").toString());
                    t_score5_3[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "02").get("T_SCORE").toString());
                    t_score5_3[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "03").get("T_SCORE").toString());
                    t_score5_3[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "01").get("T_SCORE").toString());
                    t_score5_3[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "02").get("T_SCORE").toString());
                    t_score5_3[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "03").get("T_SCORE").toString());
                    t_score5_3[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "04").get("T_SCORE").toString());
                    t_score5_3[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "05").get("T_SCORE").toString());

                    pioPdf.drawGraph01(t_score5_1_first,  xStart, y11[1] + (height11 / 2f), barWidth , height11, 1, 1, pioPdf.hexa2Color("#A9ADB2" ));
                    pioPdf.drawGraph01(t_score5_2_first,  xStart, y11[8] + (height11 / 2f), barWidth , height11, 1, 1,  pioPdf.hexa2Color("#A9ADB2" ));
                    pioPdf.drawGraph01(t_score5_3_first,  xStart, y11[15] + (height11 / 2f), barWidth , height11, 1, 1,  pioPdf.hexa2Color("#A9ADB2" ) );

                    pioPdf.drawGraph01(t_score5_1,  xStart, y11[1] + (height11 / 2f), barWidth , height11, 1,1, pioPdf.hexa2Color("#6B30F8"));
                    pioPdf.drawGraph01(t_score5_2,  xStart, y11[8] + (height11 / 2f), barWidth , height11,1, 1, pioPdf.hexa2Color("#41BEFF"));
                    pioPdf.drawGraph01(t_score5_3,  xStart, y11[15] + (height11 / 2f), barWidth , height11, 1, 1, pioPdf.hexa2Color("#FF8A94"));

                    // 대분류 점수
                    pioPdf.drawText(getAnswerReportValue(dgnssReportStat3, 3, "01", "0", "0").get("T_SCORE_FIRST").toString(), x1[0], y11[0] + textHeight11, "Pretendard", 9.5f, true, false);
                    pioPdf.drawText(getAnswerReportValue(dgnssReportStat3, 3, "02", "0", "0").get("T_SCORE_FIRST").toString(), x1[0], y11[7] + textHeight11, "Pretendard", 9.5f, true, false);
                    pioPdf.drawText(getAnswerReportValue(dgnssReportStat3, 3, "03", "0", "0").get("T_SCORE_FIRST").toString(), x1[0], y11[14] + textHeight11, "Pretendard", 9.5f, true, false);

                    pioPdf.drawText(getAnswerReportValue(dgnssReportStat3, 3, "01", "0", "0").get("T_SCORE").toString(), x1[1], y11[0] + textHeight11, "Pretendard", 9.5f, true, false);
                    pioPdf.drawText(getAnswerReportValue(dgnssReportStat3, 3, "02", "0", "0").get("T_SCORE").toString(), x1[1], y11[7] + textHeight11, "Pretendard", 9.5f, true, false);
                    pioPdf.drawText(getAnswerReportValue(dgnssReportStat3, 3, "03", "0", "0").get("T_SCORE").toString(), x1[1], y11[14] + textHeight11, "Pretendard", 9.5f, true, false);


                    //   0 , 7 , 17
                    for(i = 0 ; i < 3 ; i++) {
                        if (getAnswerReportValue(dgnssReportStat3,  3,  StringUtils.leftPad(Integer.toString(i+1), 2, "0"), "0", "0").get("T_SCORE_GAP").toString().contains("-")) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x1[2], y11[7*i] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(getAnswerReportValue(dgnssReportStat3, 3, StringUtils.leftPad(Integer.toString(i+1), 2, "0"), "0", "0").get("T_SCORE_GAP").toString(), x1[3], y11[7*i] + textHeight11, x3Width, "", 9.5f, true, false, pioPdf.hexa2Color("#FF4800"));
                        } else if (getAnswerReportValue(dgnssReportStat3, 3,  StringUtils.leftPad(Integer.toString(i+1), 2, "0"), "0", "0").get("T_SCORE_GAP").toString().equals("0")) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y11[7*i] +(height11 / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                        } else {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x1[2], y11[7*i] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(getAnswerReportValue(dgnssReportStat3, 3,  StringUtils.leftPad(Integer.toString(i+1), 2, "0"), "0", "0").get("T_SCORE_GAP").toString(), x1[3], y11[7*i] + textHeight11, x3Width, "", 9.5f, true, false, pioPdf.hexa2Color("#0B9DFF"));
                        }
                    }

                    Map<String, Object> dgnssReport;
                    int j = 1 ;
                    for(i = 0 ; i < dgnssReportStat5.size() ; i++){
                        dgnssReport = (Map<String, Object>)dgnssReportStat5.get(i);

                        if(j == 7 || j == 14 )
                            j = j + 1;

                        pioPdf.drawText(dgnssReport.get("T_SCORE_FIRST").toString(), x1[0], y11[j] + textHeight11);
                        pioPdf.drawText(dgnssReport.get("T_SCORE").toString(), x1[1], y11[j] + textHeight11);

                        if(dgnssReport.get("T_SCORE_GAP").toString().contains("-")) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x1[2], y11[j] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(dgnssReport.get("T_SCORE_GAP").toString(), x1[3], y11[j] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#FF4800"));
                        }
                        else if (dgnssReport.get("T_SCORE_GAP").toString().equals("0")) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png",  pioPdf.px2mm(555f), y11[j] + (height11 / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                        }
                        else {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x1[2], y11[j] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(dgnssReport.get("T_SCORE_GAP").toString(), x1[3], y11[j] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                        }

                        j = j + 1;
                    }


                    pioPdf.drawTextC(testInfo.get("TEST_ORD_FIRST").toString() + "차 : " + testInfo.get("TEST_DT_FIRST"), pioPdf.px2mm(211f), pioPdf.px2mm(802f, "Y"), pioPdf.px2mm(68f), "Pretendard Medium", 10f, false, false, pioPdf.hexa2Color("#A9ADB2"), -0.48f);
                    pioPdf.drawTextC(testInfo.get("TEST_ORD").toString() + "차 : " + testInfo.get("TEST_DT"), pioPdf.px2mm(309f), pioPdf.px2mm(802f, "Y"), pioPdf.px2mm(72f), "Pretendard", 10f, false, false, Color.black, -0.48f);


                }



            }

        } catch (NullPointerException e) {
            log.error("DGNSS20 COCH 페이지 생성 실패 - 데이터 누락: {}", e.getMessage());
        } catch (IOException e) {
            log.error("DGNSS20 COCH 페이지 생성 실패 - I/O 오류: {}", e.getMessage());
        } catch (Exception e) {
            log.error("DGNSS20 COCH 페이지 생성 실패 - 예상치 못한 오류: {}", e.getMessage());
        }

    }


    public void addDgnssPage_DGNSS10_COCH(PioPdf pioPdf, PDDocument doc, PDPageContentStream cont, int page, Map<String, Object> testInfo, List<Map<String, Object>> dgnssReportLS, List<Map<String, Object>> dgnssReportSection, List<Map<String, Object>> dgnssReportValidity, List<Map<String, Object>> dgnssReportMem, List<Map<String, Object>> dgnssReportStat3, List<Map<String, Object>> dgnssReportStat5) throws IOException
    {
        float x[] = new float[17];
        float y[] = new float[30];

        float height = pioPdf.px2mm(20f);
        float textHeight = pioPdf.px2mm(6f);
        float fontSize = 9.5f;
        y[0] = pioPdf.px2mm(234f, "Y");

        float bgPadding = pioPdf.px2mm(0.5f);
        Color redBg = pioPdf.hexa2Color("#FFC7B2");
        Color yellowBg = pioPdf.hexa2Color("#FFEE99");
        Color redText= pioPdf.hexa2Color("#FF4800");
        boolean isVivaClassNickName = StringUtils.isNotEmpty(MapUtils.getString(testInfo, "clsType", "")) && StringUtils.equals("5", MapUtils.getString(testInfo, "clsType", ""));
        for(int i = 1 ; i < 30 ; i++)
            y[i] = y[i-1] - height;

        try {

            if(page > 1 && page != 19) {
                pioPdf.drawHeaderCoch(page, testInfo, isVivaClassNickName);
                pioPdf.drawFooter();
            }

            if(page == 1){
                float x1 = pioPdf.px2mm(296f);
                float width1 = pioPdf.px2mm(88f);
                float fontSize1 = 12f;

                pioPdf.drawTextC((String)testInfo.get("TEST_DT_KO"), x1, pioPdf.px2mm(651f, "Y"), width1, "Pretendard Medium", fontSize1, false, false, Color.BLACK, -0.58f);
                pioPdf.drawTextC((String)testInfo.get("SCH_NM"), x1, pioPdf.px2mm(682f, "Y"), width1, "Pretendard Medium", fontSize1, false, false, Color.BLACK, -0.58f);
                if (isVivaClassNickName) {
                    pioPdf.drawTextC((String)testInfo.get("nickNameClass"), x1, pioPdf.px2mm(714f, "Y"), width1, "Pretendard Medium", fontSize1, false, false, Color.BLACK, -0.58f);
                } else {
                    pioPdf.drawTextC((String)testInfo.get("MEM_GRADE_NM") + " " +  (String)testInfo.get("CLASS_NM"), x1, pioPdf.px2mm(714f, "Y"), width1, "Pretendard Medium", fontSize1, false, false, Color.BLACK, -0.58f);
                }
            }
            // 검사결과(신뢰도 지표, 학습현황)
            else if(page == 6){

                x[0] = pioPdf.px2mm(15f);
                x[1] = pioPdf.px2mm(44f);
                x[2] = pioPdf.px2mm(106f);
                x[3] = pioPdf.px2mm(134f);
                x[4] = pioPdf.px2mm(172f);
                x[5] = pioPdf.px2mm(202f);
                x[6] = pioPdf.px2mm(254f);
                x[7] = pioPdf.px2mm(314f);
                x[8] = pioPdf.px2mm(374f);
                x[9] = pioPdf.px2mm(445f);
                x[10] = pioPdf.px2mm(510f);
                x[11] = pioPdf.px2mm(575f);

                for(int i= 0 ; i < dgnssReportLS.size();i++) {

                    if(dgnssReportLS.get(i).get("CLASS_NO") == null)
                        pioPdf.drawTextC( "?", x[0] , y[i] + textHeight, x[1] - x[0], "", fontSize);
                    else
                        pioPdf.drawTextC( dgnssReportLS.get(i).get("CLASS_NO").toString(), x[0] , y[i] + textHeight, x[1] - x[0], "", fontSize);

                    if(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM01_MARK").toString().equals("주의") || dgnssReportLS.get(i).get("COCH_DGNSS_QESITM02_MARK").toString().equals("주의") || StringUtils.equals("예", dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString()) ) {
                        pioPdf.drawRectangle(x[1], y[i], x[2] - x[1], height, Color.BLACK, redBg, 1f);
                    }

                    pioPdf.drawTextC(dgnssReportLS.get(i).get("MEM_NM").toString(), x[1] , y[i] + textHeight, x[2] - x[1], "", fontSize);

                    pioPdf.drawTextC(dgnssReportLS.get(i).get("MEM_GENDER_NM").toString(), x[2] , y[i] + textHeight, x[3] - x[2], "", fontSize);

                    if(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM02_MARK").toString().equals("주의")) {
                        pioPdf.drawRectangle(x[3], y[i], x[4] - x[3], height, Color.BLACK, redBg);
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM02_MARK").toString(), x[3], y[i] + textHeight, x[4] - x[3], "", fontSize, false, false,  redText);
                    }else{
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM02_MARK").toString(), x[3], y[i] + textHeight, x[4] - x[3], "", fontSize);
                    }

                    if(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM01_MARK").toString().equals("주의")) {
                        pioPdf.drawRectangle(x[4], y[i], x[5] - x[4], height, Color.BLACK, redBg);
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM01_MARK").toString(), x[4], y[i] + textHeight, x[5] - x[4], "", fontSize, false, false, redText);
                    }else{
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM01_MARK").toString(), x[4], y[i] + textHeight, x[5] - x[4], "", fontSize);
                    }

                    // 무응답수(10건 이상이면 red)
                    // AIDT에는 무응답이 없어서 삭제 및 연속동일반응으로 대체
//                    if(Integer.parseInt(dgnssReportLS.get(i).get("NO_ANS_CNT").toString()) >= 10) {
//                        pioPdf.drawRectangle(x[5], y[i], x[6] - x[5], height, Color.BLACK, redBg);
//                        pioPdf.drawTextC(dgnssReportLS.get(i).get("NO_ANS_CNT").toString(), x[5], y[i] + textHeight, x[6] - x[5], "", fontSize, false, false, redText);
//                    }else{
//                        pioPdf.drawTextC(dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString(), x[5], y[i] + textHeight, x[6] - x[5], "", fontSize);
//                    }

                    // 연속동일반응
                    if (StringUtils.equals("예", dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString())) {
                        pioPdf.drawRectangle(x[5], y[i], x[6] - x[5], height, Color.BLACK, redBg);
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString(), x[5], y[i] + textHeight, x[6] - x[5], "", fontSize, false, false, redText);
                    } else {
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString(), x[5], y[i] + textHeight, x[6] - x[5], "", fontSize);
                    }

                    // 학업성취도
                    if(dgnssReportLS.get(i).get("LS_ANS01").toString().equals("1")){
                        pioPdf.drawRectangle(x[6], y[i], x[7] - x[6], height, Color.BLACK, redBg);
                        pioPdf.drawTextC("매우 낮음", x[6], y[i] + textHeight, x[7] - x[6], "", fontSize, false, false, redText);
                    }else if(dgnssReportLS.get(i).get("LS_ANS01").toString().equals("2")){
                        pioPdf.drawRectangle(x[6], y[i], x[7] - x[6], height, Color.BLACK, yellowBg);
                        pioPdf.drawTextC("낮음", x[6], y[i] + textHeight, x[7] - x[6], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS01").toString().equals("3")) {
                        pioPdf.drawTextC("보통", x[6], y[i] + textHeight, x[7] - x[6], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS01").toString().equals("4")) {
                        pioPdf.drawTextC("높음", x[6], y[i] + textHeight, x[7] - x[6], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS01").toString().equals("5")) {
                        pioPdf.drawTextC("매우 높음", x[6], y[i] + textHeight, x[7] - x[6], "", fontSize);
                    }

                    // 성적만족도
                    if(dgnssReportLS.get(i).get("LS_ANS02").toString().equals("1")){
                        pioPdf.drawRectangle(x[7], y[i], x[8] - x[7], height, Color.BLACK, redBg);
                        pioPdf.drawTextC("매우 낮음", x[7], y[i] + textHeight, x[8] - x[7], "", fontSize, false, false, redText);
                    }else if(dgnssReportLS.get(i).get("LS_ANS02").toString().equals("2")){
                        pioPdf.drawRectangle(x[7], y[i], x[8] - x[7], height, Color.BLACK, yellowBg);
                        pioPdf.drawTextC("낮음", x[7], y[i] + textHeight, x[8] - x[7], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS02").toString().equals("3")) {
                        pioPdf.drawTextC("보통", x[7], y[i] + textHeight, x[8] - x[7], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS02").toString().equals("4")) {
                        pioPdf.drawTextC("높음", x[7], y[i] + textHeight, x[8] - x[7], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS02").toString().equals("5")) {
                        pioPdf.drawTextC("매우 높음", x[7], y[i] + textHeight, x[8] - x[7], "", fontSize);
                    }

                    // 공부이유
                    if(dgnssReportLS.get(i).get("LS_ANS03").toString().equals("1")){
                        pioPdf.drawTextC("흥미를 느껴서", x[8], y[i] + textHeight, x[9] - x[8], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS03").toString().equals("2")){
                        pioPdf.drawTextC("미래를 위해서", x[8], y[i] + textHeight, x[9] - x[8], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS03").toString().equals("3")) {
                        pioPdf.drawTextC("대학 진학", x[8], y[i] + textHeight, x[9] - x[8], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS03").toString().equals("4")) {
                        pioPdf.drawTextC("주변 기대 때문에", x[8], y[i] + textHeight, x[9] - x[8], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS03").toString().equals("5")) {
                        pioPdf.drawTextC("모르겠음", x[8], y[i] + textHeight, x[9] - x[8], "", fontSize);
                    }

                    // 1일 혼공 시간
                    if(dgnssReportLS.get(i).get("LS_ANS04").toString().equals("1")){
                        pioPdf.drawTextC("전혀 안함", x[9], y[i] + textHeight, x[10] - x[9], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS04").toString().equals("2")){
                        pioPdf.drawTextC("1시간 미만", x[9], y[i] + textHeight, x[10] - x[9], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS04").toString().equals("3")) {
                        pioPdf.drawTextC("1~2시간", x[9], y[i] + textHeight, x[10] - x[9], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS04").toString().equals("4")) {
                        pioPdf.drawTextC("2~3시간", x[9], y[i] + textHeight, x[10] - x[9], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS04").toString().equals("5")) {
                        pioPdf.drawTextC("3시간 이상", x[9], y[i] + textHeight, x[10] - x[9], "", fontSize);
                    }

                    // 학습고민 상담
                    if(dgnssReportLS.get(i).get("LS_ANS05").toString().equals("1")){
                        pioPdf.drawTextC("친구", x[10], y[i] + textHeight, x[11] - x[10], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS05").toString().equals("2")){
                        pioPdf.drawTextC("선생님", x[10], y[i] + textHeight, x[11] - x[10], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS05").toString().equals("3")) {
                        pioPdf.drawTextC("가족", x[10], y[i] + textHeight, x[11] - x[10], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS05").toString().equals("4")) {
                        pioPdf.drawTextC("상담 전문가", x[10], y[i] + textHeight, x[11] - x[10], "", fontSize);
                    }else if(dgnssReportLS.get(i).get("LS_ANS05").toString().equals("5")) {
                        pioPdf.drawTextC("기타", x[10], y[i] + textHeight, x[11] - x[10], "", fontSize);
                    }
                }

            }
            // 검사 결과 : 동기전략(학습원동력, 정서조절)
            else if (page >= 7 && page <= 11) {
                int tScore[] = new int[13];
                int tScoreCnt = 0;

                if(page ==  7) {

                    tScoreCnt = 9;

                    x[0] = pioPdf.px2mm(15f);
                    x[1] = pioPdf.px2mm(44f);
                    x[2] = pioPdf.px2mm(101f);
                    x[3] = pioPdf.px2mm(129f);
                    x[4] = pioPdf.px2mm(159f);
                    x[5] = pioPdf.px2mm(214f);
                    x[6] = pioPdf.px2mm(269f);
                    x[7] = pioPdf.px2mm(324f);
                    x[8] = pioPdf.px2mm(354f);
                    x[9] = pioPdf.px2mm(409f);
                    x[10] = pioPdf.px2mm(464f);
                    x[11] = pioPdf.px2mm(519f);
                    x[12] = pioPdf.px2mm(575f);
                }else if(page == 8){
                    tScoreCnt = 10;

                    x[0] = pioPdf.px2mm(15f);
                    x[1] = pioPdf.px2mm(44f);
                    x[2] = pioPdf.px2mm(101f);
                    x[3] = pioPdf.px2mm(129f);
                    x[4] = pioPdf.px2mm(159f);
                    x[5] = pioPdf.px2mm(207f);
                    x[6] = pioPdf.px2mm(255f);
                    x[7] = pioPdf.px2mm(303f);
                    x[8] = pioPdf.px2mm(333f);
                    x[9] = pioPdf.px2mm(381f);
                    x[10] = pioPdf.px2mm(429f);
                    x[11] = pioPdf.px2mm(477f);
                    x[12] = pioPdf.px2mm(525f);
                    x[13] = pioPdf.px2mm(575f);
                }else if(page == 9){
                    tScoreCnt = 13;

                    x[0] = pioPdf.px2mm(15f);
                    x[1] = pioPdf.px2mm(44f);
                    x[2] = pioPdf.px2mm(106f);
                    x[3] = pioPdf.px2mm(134f);
                    x[4] = pioPdf.px2mm(161f);
                    x[5] = pioPdf.px2mm(197f);
                    x[6] = pioPdf.px2mm(233f);
                    x[7] = pioPdf.px2mm(269f);
                    x[8] = pioPdf.px2mm(305f);
                    x[9] = pioPdf.px2mm(332f);
                    x[10] = pioPdf.px2mm(368f);
                    x[11] = pioPdf.px2mm(404f);
                    x[12] = pioPdf.px2mm(440f);
                    x[13] = pioPdf.px2mm(467f);
                    x[14] = pioPdf.px2mm(503f);
                    x[15] = pioPdf.px2mm(539f);
                    x[16] = pioPdf.px2mm(575f);
                }
                else if(page == 10){
                    tScoreCnt = 7;

                    x[0] = pioPdf.px2mm(15f);
                    x[1] = pioPdf.px2mm(44f);
                    x[2] = pioPdf.px2mm(106f);
                    x[3] = pioPdf.px2mm(134f);
                    x[4] = pioPdf.px2mm(174f);
                    x[5] = pioPdf.px2mm(244f);
                    x[6] = pioPdf.px2mm(314f);
                    x[7] = pioPdf.px2mm(384f);
                    x[8] = pioPdf.px2mm(424f);
                    x[9] = pioPdf.px2mm(499f);
                    x[10] = pioPdf.px2mm(575f);
                }
                else if(page == 11){
                    tScoreCnt = 10;

                    x[0] = pioPdf.px2mm(15f);
                    x[1] = pioPdf.px2mm(44f);
                    x[2] = pioPdf.px2mm(101f);
                    x[3] = pioPdf.px2mm(129f);
                    x[4] = pioPdf.px2mm(159f);
                    x[5] = pioPdf.px2mm(211f);
                    x[6] = pioPdf.px2mm(263f);
                    x[7] = pioPdf.px2mm(315f);
                    x[8] = pioPdf.px2mm(367f);
                    x[9] = pioPdf.px2mm(419f);
                    x[10] = pioPdf.px2mm(449f);
                    x[11] = pioPdf.px2mm(491f);
                    x[12] = pioPdf.px2mm(532f);
                    x[13] = pioPdf.px2mm(575f);
                }



                for(int i= 0 ; i < dgnssReportLS.size();i++) {
                    // 긍정적 자아(01-01 3개), 대인관계능력(01-02 : 4개)
                    if(page == 7) {
                        tScore[0] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_01").toString());
                        tScore[1] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_01_01").toString());
                        tScore[2] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_01_02").toString());
                        tScore[3] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_01_03").toString());

                        tScore[4] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_02").toString());
                        tScore[5] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_02_01").toString());
                        tScore[6] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_02_02").toString());
                        tScore[7] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_02_03").toString());
                        tScore[8] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_01_02_04").toString());
                    }
                    // 메타인지(02-01 :  3개),  학습기술(02-02 : 5개)
                    else if(page == 8){
                        tScore[0] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_01").toString());
                        tScore[1] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_01_01").toString());
                        tScore[2] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_01_02").toString());
                        tScore[3] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_01_03").toString());

                        tScore[4] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02").toString());
                        tScore[5] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02_01").toString());
                        tScore[6] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02_02").toString());
                        tScore[7] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02_03").toString());
                        tScore[8] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02_04").toString());
                        tScore[9] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_02_05").toString());

                    }
                    // 지지적 관계(02-03 : 4개) 학업열의(04-01 : 3개) 성장력(04-02 : 3개)
                    else if(page == 9){
                        tScore[0] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_03").toString());
                        tScore[1] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_03_01").toString());
                        tScore[2] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_03_02").toString());
                        tScore[3] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_03_03").toString());
                        tScore[4] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_02_03_04").toString());

                        tScore[5] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_04_01").toString());
                        tScore[6] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_04_01_01").toString());
                        tScore[7] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_04_01_02").toString());
                        tScore[8] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_04_01_03").toString());

                        tScore[9] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_04_02").toString());
                        tScore[10] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_04_02_01").toString());
                        tScore[11] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_04_02_02").toString());
                        tScore[12] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_04_02_03").toString());

                    }
                    // 학업스트레스(03-01 : 3개), 학습방해물(03-03 : 2개)
                    else if(page == 10){
                        tScore[0] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_01").toString());
                        tScore[1] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_01_01").toString());
                        tScore[2] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_01_02").toString());
                        tScore[3] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_01_03").toString());

                        tScore[4] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_03").toString());
                        tScore[5] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_03_01").toString());
                        tScore[6] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_03_02").toString());


                    }
                    // 학업관계스트레스(03-02 : 5개), 학습방해물(05-01 : 3개)
                    else if(page == 11){
                        tScore[0] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02").toString());
                        tScore[1] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02_01").toString());
                        tScore[2] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02_02").toString());
                        tScore[3] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02_03").toString());
                        tScore[4] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02_04").toString());
                        tScore[5] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_03_02_05").toString());

                        tScore[6] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_05_01").toString());
                        tScore[7] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_05_01_01").toString());
                        tScore[8] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_05_01_02").toString());
                        tScore[9] = Integer.parseInt(dgnssReportSection.get(i).get("T_SCORE_05_01_03").toString());


                    }

                    if(dgnssReportLS.get(i).get("CLASS_NO") == null)
                        pioPdf.drawTextC("?", x[0] , y[i] + textHeight, x[1] - x[0], "", fontSize);
                    else
                        pioPdf.drawTextC(dgnssReportLS.get(i).get("CLASS_NO").toString(), x[0] , y[i] + textHeight, x[1] - x[0], "", fontSize);

                    if(dgnssReportLS.get(i).get("COCH_DGNSS_QESITM01_MARK").toString().equals("주의") || dgnssReportLS.get(i).get("COCH_DGNSS_QESITM02_MARK").toString().equals("주의") || StringUtils.equals("예", dgnssReportLS.get(i).get("REPEATED_RESPONSE_YN").toString()) )
                        pioPdf.drawRectangle(x[1], y[i], x[2] - x[1], height, Color.BLACK, redBg);

                    pioPdf.drawTextC(dgnssReportLS.get(i).get("MEM_NM").toString(), x[1] , y[i] + textHeight, x[2] - x[1], "", fontSize);

                    pioPdf.drawTextC(dgnssReportLS.get(i).get("MEM_GENDER_NM").toString(), x[2] , y[i] + textHeight, x[3] - x[2], "", fontSize);


                    for(int j = 0 ; j < tScoreCnt ; j++){
                        if(tScore[j] < 0){
                            pioPdf.drawTextC("?", x[j + 3], y[i] + textHeight, x[j + 4] - x[j + 3], "", fontSize);
                        }else{
                            if(page == 10 || page == 11) {
                                if (tScore[j] >= 70) {
                                    pioPdf.drawRectangle(x[j + 3], y[i], x[j + 4] - x[j + 3], height, Color.BLACK, redBg);
                                } else if (tScore[j] >= 60 && tScore[j] < 70) {
                                    pioPdf.drawRectangle(x[j + 3], y[i], x[j + 4] - x[j + 3], height, Color.BLACK, yellowBg);
                                }
                                pioPdf.drawTextC(String.valueOf(tScore[j]), x[j + 3], y[i] + textHeight, x[j + 4] - x[j + 3], "", fontSize);
                            }else {

                                if (tScore[j] <  30) {
                                    pioPdf.drawRectangle(x[j + 3], y[i], x[j + 4] - x[j + 3], height, Color.BLACK, redBg);
                                } else if (tScore[j] >= 30 && tScore[j] < 40) {
                                    pioPdf.drawRectangle(x[j + 3], y[i], x[j + 4] - x[j + 3], height, Color.BLACK, yellowBg);
                                }
                                pioPdf.drawTextC(String.valueOf(tScore[j]), x[j + 3], y[i] + textHeight, x[j + 4] - x[j + 3], "", fontSize);
                            }
                        }
                    }

                }

            }
            // 상담 지도가 필요한 학생
            else if(page >= 12 && page <= 16){

                x[0] = pioPdf.px2mm(154f);
                float x_width = pioPdf.px2mm(413f);

                String memList[] = new String[10];
                float y_textHeight = 11f;

                int memCount = 0;

                if(page == 12){
                    memCount = 10;

                    y[0] = pioPdf.px2mm(221f + y_textHeight, "Y");
                    y[1] = pioPdf.px2mm(277f + y_textHeight, "Y");
                    y[2] = pioPdf.px2mm(333f + y_textHeight, "Y");
                    y[3] = pioPdf.px2mm(428f + y_textHeight, "Y");
                    y[4] = pioPdf.px2mm(484f + y_textHeight, "Y");
                    y[5] = pioPdf.px2mm(540f + y_textHeight, "Y");
                    y[6] = pioPdf.px2mm(596f + y_textHeight, "Y");
                    y[7] = pioPdf.px2mm(652f + y_textHeight, "Y");
                    y[8] = pioPdf.px2mm(708f + y_textHeight, "Y");
                    y[9] = pioPdf.px2mm(764f + y_textHeight, "Y");

                    memList[0] = dgnssReportMem.get(0).get("QESITM02_MEM").toString();
                    memList[1] = dgnssReportMem.get(0).get("QESITM01_MEM").toString();
                    memList[2] = dgnssReportMem.get(0).get("REPEATED_RESPONSE_YN").toString();
                    memList[3] = dgnssReportMem.get(0).get("SECTION_MEM_01_01_01").toString();
                    memList[4] = dgnssReportMem.get(0).get("SECTION_MEM_01_01_02").toString();
                    memList[5] = dgnssReportMem.get(0).get("SECTION_MEM_01_01_03").toString();
                    memList[6] = dgnssReportMem.get(0).get("SECTION_MEM_01_02_01").toString();
                    memList[7] = dgnssReportMem.get(0).get("SECTION_MEM_01_02_02").toString();
                    memList[8] = dgnssReportMem.get(0).get("SECTION_MEM_01_02_03").toString();
                    memList[9] = dgnssReportMem.get(0).get("SECTION_MEM_01_02_04").toString();
                }else if (page == 13){
                    memCount = 10;

                    y[0] = pioPdf.px2mm(221f + y_textHeight, "Y");
                    y[1] = pioPdf.px2mm(277f + y_textHeight, "Y");
                    y[2] = pioPdf.px2mm(333f + y_textHeight, "Y");
                    y[3] = pioPdf.px2mm(389f + y_textHeight, "Y");
                    y[4] = pioPdf.px2mm(445f + y_textHeight, "Y");
                    y[5] = pioPdf.px2mm(501f + y_textHeight, "Y");
                    y[6] = pioPdf.px2mm(557f + y_textHeight, "Y");
                    y[7] = pioPdf.px2mm(613f + y_textHeight, "Y");
                    y[8] = pioPdf.px2mm(698f + y_textHeight, "Y");
                    y[9] = pioPdf.px2mm(755f + y_textHeight, "Y");

                    memList[0] = dgnssReportMem.get(0).get("SECTION_MEM_02_01_01").toString();
                    memList[1] = dgnssReportMem.get(0).get("SECTION_MEM_02_01_02").toString();
                    memList[2] = dgnssReportMem.get(0).get("SECTION_MEM_02_01_03").toString();
                    memList[3] = dgnssReportMem.get(0).get("SECTION_MEM_02_02_01").toString();
                    memList[4] = dgnssReportMem.get(0).get("SECTION_MEM_02_02_02").toString();
                    memList[5] = dgnssReportMem.get(0).get("SECTION_MEM_02_02_03").toString();
                    memList[6] = dgnssReportMem.get(0).get("SECTION_MEM_02_02_04").toString();
                    memList[7] = dgnssReportMem.get(0).get("SECTION_MEM_02_02_05").toString();
                    memList[8] = dgnssReportMem.get(0).get("SECTION_MEM_02_03_01").toString();
                    memList[9] = dgnssReportMem.get(0).get("SECTION_MEM_02_03_02").toString();

                }else if (page == 14){
                    memCount = 8;

                    y[0] = pioPdf.px2mm(221f + y_textHeight, "Y");
                    y[1] = pioPdf.px2mm(277f + y_textHeight, "Y");
                    y[2] = pioPdf.px2mm(372f + y_textHeight, "Y");
                    y[3] = pioPdf.px2mm(428f + y_textHeight, "Y");
                    y[4] = pioPdf.px2mm(484f + y_textHeight, "Y");
                    y[5] = pioPdf.px2mm(540f + y_textHeight, "Y");
                    y[6] = pioPdf.px2mm(596f + y_textHeight, "Y");
                    y[7] = pioPdf.px2mm(652f + y_textHeight, "Y");

                    memList[0] = dgnssReportMem.get(0).get("SECTION_MEM_02_03_03").toString();
                    memList[1] = dgnssReportMem.get(0).get("SECTION_MEM_02_03_04").toString();
                    memList[2] = dgnssReportMem.get(0).get("SECTION_MEM_04_01_01").toString();
                    memList[3] = dgnssReportMem.get(0).get("SECTION_MEM_04_01_02").toString();
                    memList[4] = dgnssReportMem.get(0).get("SECTION_MEM_04_01_03").toString();
                    memList[5] = dgnssReportMem.get(0).get("SECTION_MEM_04_02_01").toString();
                    memList[6] = dgnssReportMem.get(0).get("SECTION_MEM_04_02_02").toString();
                    memList[7] = dgnssReportMem.get(0).get("SECTION_MEM_04_02_03").toString();
                }else if (page == 15){
                    memCount = 10;

                    y[0] = pioPdf.px2mm(221f + y_textHeight, "Y");
                    y[1] = pioPdf.px2mm(277f + y_textHeight, "Y");
                    y[2] = pioPdf.px2mm(333f + y_textHeight, "Y");
                    y[3] = pioPdf.px2mm(389f + y_textHeight, "Y");
                    y[4] = pioPdf.px2mm(445f + y_textHeight, "Y");
                    y[5] = pioPdf.px2mm(501f + y_textHeight, "Y");
                    y[6] = pioPdf.px2mm(557f + y_textHeight, "Y");
                    y[7] = pioPdf.px2mm(613f + y_textHeight, "Y");
                    y[8] = pioPdf.px2mm(669f + y_textHeight, "Y");
                    y[9] = pioPdf.px2mm(725f + y_textHeight, "Y");

                    memList[0] = dgnssReportMem.get(0).get("SECTION_MEM_03_01_01").toString();
                    memList[1] = dgnssReportMem.get(0).get("SECTION_MEM_03_01_02").toString();
                    memList[2] = dgnssReportMem.get(0).get("SECTION_MEM_03_01_03").toString();
                    memList[3] = dgnssReportMem.get(0).get("SECTION_MEM_03_03_01").toString();
                    memList[4] = dgnssReportMem.get(0).get("SECTION_MEM_03_03_02").toString();
                    memList[5] = dgnssReportMem.get(0).get("SECTION_MEM_03_02_01").toString();
                    memList[6] = dgnssReportMem.get(0).get("SECTION_MEM_03_02_02").toString();
                    memList[7] = dgnssReportMem.get(0).get("SECTION_MEM_03_02_03").toString();
                    memList[8] = dgnssReportMem.get(0).get("SECTION_MEM_03_02_04").toString();
                    memList[9] = dgnssReportMem.get(0).get("SECTION_MEM_03_02_05").toString();

                }else if(page == 16){
                    memCount = 3;

                    y[0] = pioPdf.px2mm(221f + y_textHeight, "Y");
                    y[1] = pioPdf.px2mm(277f + y_textHeight, "Y");
                    y[2] = pioPdf.px2mm(333f + y_textHeight, "Y");

                    memList[0] = dgnssReportMem.get(0).get("SECTION_MEM_05_01_01").toString();
                    memList[1] = dgnssReportMem.get(0).get("SECTION_MEM_05_01_02").toString();
                    memList[2] = dgnssReportMem.get(0).get("SECTION_MEM_05_01_03").toString();


                }

                for(int i = 0; i < memCount; i++) {
                    pioPdf.drawTextParagraph(memList[i], x[0], y[i], x_width, 150, "", 10.3f, Color.BLACK, true, -0.49f);
                }

            }
            // 종합해석 1(자아강점, 학습디딤돌, 긍정적 공부마음 반평균 분석)
            else if(page == 17) {
                // 종합분석표(대분류 T점수)

                float xStart = pioPdf.px2mm(180.5f);      // 그래프 시작 위치
                float barWidth = pioPdf.px2mm(298f);      // 그래프 바 넓이

                float height11 = pioPdf.px2mm(24f);   // 테이블 행 높이

                int i = 0;

                y[0] = pioPdf.px2mm(238f, "Y");
                for (i = 1; i < 26; i++)
                    y[i] = y[i - 1] - height11;

                // 기준선에서 텍스트 높이
                float textHeight11 = 3f;

                int testOrd = Integer.parseInt(testInfo.get("TEST_ORD").toString());

                // 종합해석 1차, 2차 변화 X 좌표
                x[0] = pioPdf.px2mm(488f);
                x[1] = pioPdf.px2mm(520f);
                x[2] = pioPdf.px2mm(548f);
                x[3] = pioPdf.px2mm(556f);

                float x3Width = 4.74f;

                if (testOrd == 1) {

                    float[] t_score5_1 = new float[7];
                    float[] t_score5_2 = new float[8];
                    float[] t_score5_3 = new float[4];
                    float[] t_score5_4 = new float[6];

                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "03").get("T_SCORE").toString());
                    t_score5_1[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "04").get("T_SCORE").toString());

                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "03").get("T_SCORE").toString());
                    t_score5_2[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "01").get("T_SCORE").toString());
                    t_score5_2[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "02").get("T_SCORE").toString());
                    t_score5_2[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "03").get("T_SCORE").toString());
                    t_score5_2[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "04").get("T_SCORE").toString());
                    t_score5_2[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "05").get("T_SCORE").toString());

                    t_score5_3[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "01").get("T_SCORE").toString());
                    t_score5_3[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "02").get("T_SCORE").toString());
                    t_score5_3[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "03").get("T_SCORE").toString());
                    t_score5_3[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "04").get("T_SCORE").toString());

                    t_score5_4[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "01").get("T_SCORE").toString());
                    t_score5_4[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "02").get("T_SCORE").toString());
                    t_score5_4[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "03").get("T_SCORE").toString());
                    t_score5_4[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "01").get("T_SCORE").toString());
                    t_score5_4[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "02").get("T_SCORE").toString());
                    t_score5_4[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "03").get("T_SCORE").toString());

                    pioPdf.drawGraph01(t_score5_1, xStart, y[0] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#00D282"));
                    pioPdf.drawGraph01(t_score5_2, xStart, y[7] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#41BEFF"));
                    pioPdf.drawGraph01(t_score5_3, xStart, y[15] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#41BEFF"));
                    pioPdf.drawGraph01(t_score5_4, xStart, y[19] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#41BEFF"));

                    pioPdf.drawText(testInfo.get("TEST_ORD").toString() + "차",  pioPdf.px2mm(488f), pioPdf.px2mm(204f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);

                    for(i = 0 ; i < 7; i++)
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_1[i])), x[0], y[i] + textHeight11, pioPdf.px2mm(12f));
                    for(i = 0 ; i < 8; i++)
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_2[i])), x[0], y[i+7] + textHeight11, pioPdf.px2mm(12f));
                    for(i = 0 ; i < 4 ; i++)
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_3[i])), x[0], y[i+15] + textHeight11, pioPdf.px2mm(12f));
                    for(i = 0 ; i < 6 ; i++)
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_4[i])), x[0], y[i+19] + textHeight11, pioPdf.px2mm(12f));
                } else {
                    float[] t_score5_1_first = new float[7];
                    float[] t_score5_2_first = new float[8];
                    float[] t_score5_3_first = new float[4];
                    float[] t_score5_4_first = new float[6];

                    float[] t_score5_1 = new float[7];
                    float[] t_score5_2 = new float[8];
                    float[] t_score5_3 = new float[4];
                    float[] t_score5_4 = new float[6];

                    float[] t_score5_1_gap = new float[7];
                    float[] t_score5_2_gap = new float[8];
                    float[] t_score5_3_gap = new float[4];
                    float[] t_score5_4_gap = new float[6];



                    t_score5_1_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "03").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "04").get("T_SCORE_FIRST").toString());

                    t_score5_2_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "03").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "04").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "05").get("T_SCORE_FIRST").toString());

                    t_score5_3_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "01").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "02").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "03").get("T_SCORE_FIRST").toString());
                    t_score5_3_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "04").get("T_SCORE_FIRST").toString());

                    t_score5_4_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_4_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_4_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_4_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_4_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_4_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "03").get("T_SCORE_FIRST").toString());

                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "03").get("T_SCORE").toString());
                    t_score5_1[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "04").get("T_SCORE").toString());

                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "03").get("T_SCORE").toString());
                    t_score5_2[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "01").get("T_SCORE").toString());
                    t_score5_2[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "02").get("T_SCORE").toString());
                    t_score5_2[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "03").get("T_SCORE").toString());
                    t_score5_2[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "04").get("T_SCORE").toString());
                    t_score5_2[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "05").get("T_SCORE").toString());

                    t_score5_3[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "01").get("T_SCORE").toString());
                    t_score5_3[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "02").get("T_SCORE").toString());
                    t_score5_3[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "03").get("T_SCORE").toString());
                    t_score5_3[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "04").get("T_SCORE").toString());

                    t_score5_4[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "01").get("T_SCORE").toString());
                    t_score5_4[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "02").get("T_SCORE").toString());
                    t_score5_4[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "03").get("T_SCORE").toString());
                    t_score5_4[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "01").get("T_SCORE").toString());
                    t_score5_4[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "02").get("T_SCORE").toString());
                    t_score5_4[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "03").get("T_SCORE").toString());

                    t_score5_1_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "01").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "02").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "01", "03").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "01").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "02").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "03").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "01", "02", "04").get("T_SCORE_GAP").toString());

                    t_score5_2_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "01").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "02").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "01", "03").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "01").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "02").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "03").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "04").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "02", "05").get("T_SCORE_GAP").toString());

                    t_score5_3_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "01").get("T_SCORE_GAP").toString());
                    t_score5_3_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "02").get("T_SCORE_GAP").toString());
                    t_score5_3_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "03").get("T_SCORE_GAP").toString());
                    t_score5_3_gap[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "02", "03", "04").get("T_SCORE_GAP").toString());

                    t_score5_4_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "01").get("T_SCORE_GAP").toString());
                    t_score5_4_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "02").get("T_SCORE_GAP").toString());
                    t_score5_4_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "01", "03").get("T_SCORE_GAP").toString());
                    t_score5_4_gap[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "01").get("T_SCORE_GAP").toString());
                    t_score5_4_gap[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "02").get("T_SCORE_GAP").toString());
                    t_score5_4_gap[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "04", "02", "03").get("T_SCORE_GAP").toString());

                    pioPdf.drawGraph01(t_score5_1_first, xStart, y[0] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#A9ADB2"));
                    pioPdf.drawGraph01(t_score5_2_first, xStart, y[7] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#A9ADB2"));
                    pioPdf.drawGraph01(t_score5_3_first, xStart, y[15] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#A9ADB2"));
                    pioPdf.drawGraph01(t_score5_4_first, xStart, y[19] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#A9ADB2"));

                    pioPdf.drawGraph01(t_score5_1, xStart, y[0] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#00D282"));
                    pioPdf.drawGraph01(t_score5_2, xStart, y[7] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#41BEFF"));
                    pioPdf.drawGraph01(t_score5_3, xStart, y[15] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#41BEFF"));
                    pioPdf.drawGraph01(t_score5_4, xStart, y[19] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#41BEFF"));

                    pioPdf.drawText(testInfo.get("TEST_ORD_FIRST").toString() + "차",  pioPdf.px2mm(488f), pioPdf.px2mm(204f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);
                    pioPdf.drawText(testInfo.get("TEST_ORD").toString() + "차",  pioPdf.px2mm(519f), pioPdf.px2mm(204f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);


                    for(i = 0 ; i < 7; i++) {
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_1_first[i])), x[0], y[i] + textHeight11, pioPdf.px2mm(12f));
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_1[i])), x[1], y[i] + textHeight11, pioPdf.px2mm(12f));

                        if (t_score5_1_gap[i] < 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x[2], y[i] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_1_gap[i])), x[3], y[i] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#FF4800"));
                        } else if (t_score5_1_gap[i] == 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[i] + (height11 / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                        } else {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x[2], y[i] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_1_gap[i])), x[3], y[i] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                        }
                    }
                    for(i = 0 ; i < 8; i++) {
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_2_first[i])), x[0], y[i + 7] + textHeight11, pioPdf.px2mm(12f));
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_2[i])), x[1], y[i + 7] + textHeight11, pioPdf.px2mm(12f));

                        if (t_score5_2_gap[i] < 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x[2], y[i+7] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_2_gap[i])), x[3], y[i+7] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#FF4800"));
                        } else if (t_score5_2_gap[i] == 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[i+7] + (height11 / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                        } else {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x[2], y[i+7] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_2_gap[i])), x[3], y[i+7] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                        }
                    }
                    for(i = 0 ; i < 4 ; i++) {
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_3_first[i])), x[0], y[i + 15] + textHeight11, pioPdf.px2mm(12f));
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_3[i])), x[1], y[i + 15] + textHeight11, pioPdf.px2mm(12f));

                        if (t_score5_3_gap[i] < 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x[2], y[i+15] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_3_gap[i])), x[3], y[i+15] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#FF4800"));
                        } else if (t_score5_3_gap[i] == 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[i+15] + (height11 / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                        } else {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x[2], y[i+15] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_3_gap[i])), x[3], y[i+15] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                        }
                    }
                    for(i = 0 ; i < 6 ; i++) {
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_4_first[i])), x[0], y[i + 19] + textHeight11, pioPdf.px2mm(12f));
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_4[i])), x[1], y[i + 19] + textHeight11, pioPdf.px2mm(12f));

                        if (t_score5_4_gap[i] < 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_red.png", x[2], y[i+19] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_4_gap[i])), x[3], y[i+19] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#FF4800"));
                        } else if (t_score5_4_gap[i] == 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[i+19] + (height11 / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                        } else {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_blue.png", x[2], y[i+19] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_4_gap[i])), x[3], y[i+19] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                        }
                    }
                }

            }
            // 종합해석 2(학습 걸림돌, 부정적 공부마음 반평균 분석)
            else if(page == 18) {
                // 종합분석표(대분류 T점수)

                float xStart = pioPdf.px2mm(180.5f);      // 그래프 시작 위치
                float barWidth = pioPdf.px2mm(298f);      // 그래프 바 넓이

                float height11 = pioPdf.px2mm(24f);   // 테이블 행 높이

                int i = 0;

                y[0] = pioPdf.px2mm(238f, "Y");
                for (i = 1; i < 26; i++)
                    y[i] = y[i - 1] - height11;

                // 기준선에서 텍스트 높이
                float textHeight11 = 3f;

                int testOrd = Integer.parseInt(testInfo.get("TEST_ORD").toString());

                // 종합해석 1차, 2차 변화 X 좌표
                x[0] = pioPdf.px2mm(488f);
                x[1] = pioPdf.px2mm(520f);
                x[2] = pioPdf.px2mm(548f);
                x[3] = pioPdf.px2mm(556f);

                float x3Width = 4.74f;

                if (testOrd == 1) {

                    float[] t_score5_1 = new float[10];
                    float[] t_score5_2 = new float[3];


                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "03").get("T_SCORE").toString());
                    t_score5_1[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "04").get("T_SCORE").toString());
                    t_score5_1[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "05").get("T_SCORE").toString());
                    t_score5_1[8] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "03", "01").get("T_SCORE").toString());
                    t_score5_1[9] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "03", "02").get("T_SCORE").toString());


                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "03").get("T_SCORE").toString());


                    pioPdf.drawText(testInfo.get("TEST_ORD").toString() + "차",  pioPdf.px2mm(488f), pioPdf.px2mm(204f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);
                    pioPdf.drawPicture("./assets/imgs/dgnss/btn/btn_total_anal_10_1st.png", pioPdf.px2mm(185f), pioPdf.px2mm(815f, "Y"), pioPdf.px2mm(220f), pioPdf.px2mm(33f));

                    pioPdf.drawGraph01(t_score5_1, xStart, y[0] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#FF849F"));
                    pioPdf.drawGraph01(t_score5_2, xStart, y[10] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#FF87D4"));

                    for(i = 0 ; i < 10; i++)
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_1[i])), x[0], y[i] + textHeight11, pioPdf.px2mm(12f));
                    for(i = 0 ; i < 3; i++)
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_2[i])), x[0], y[i+10] + textHeight11, pioPdf.px2mm(12f));

                    pioPdf.drawTextC(testInfo.get("TEST_ORD").toString() + "차 : " + testInfo.get("TEST_DT"), pioPdf.px2mm(211f), pioPdf.px2mm(802f, "Y"), pioPdf.px2mm(68f), "Pretendard", 10f, false, false, Color.black, -0.48f);
                } else {
                    float[] t_score5_1_first = new float[10];
                    float[] t_score5_2_first = new float[3];

                    float[] t_score5_1 = new float[10];
                    float[] t_score5_2 = new float[3];

                    float[] t_score5_1_gap = new float[10];
                    float[] t_score5_2_gap = new float[3];

                    t_score5_1_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "03").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "02").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "03").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "04").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "05").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[8] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "03", "01").get("T_SCORE_FIRST").toString());
                    t_score5_1_first[9] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "03", "02").get("T_SCORE_FIRST").toString());


                    t_score5_2_first[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "01").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "02").get("T_SCORE_FIRST").toString());
                    t_score5_2_first[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "03").get("T_SCORE_FIRST").toString());


                    t_score5_1[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "01").get("T_SCORE").toString());
                    t_score5_1[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "02").get("T_SCORE").toString());
                    t_score5_1[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "03").get("T_SCORE").toString());
                    t_score5_1[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "01").get("T_SCORE").toString());
                    t_score5_1[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "02").get("T_SCORE").toString());
                    t_score5_1[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "03").get("T_SCORE").toString());
                    t_score5_1[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "04").get("T_SCORE").toString());
                    t_score5_1[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "05").get("T_SCORE").toString());
                    t_score5_1[8] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "03", "01").get("T_SCORE").toString());
                    t_score5_1[9] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "03", "02").get("T_SCORE").toString());


                    t_score5_2[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "01").get("T_SCORE").toString());
                    t_score5_2[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "02").get("T_SCORE").toString());
                    t_score5_2[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "03").get("T_SCORE").toString());

                    t_score5_1_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "01").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "02").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "01", "03").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[3] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "01").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[4] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "02").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[5] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "03").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[6] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "04").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[7] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "02", "05").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[8] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "03", "01").get("T_SCORE_GAP").toString());
                    t_score5_1_gap[9] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "03", "03", "02").get("T_SCORE_GAP").toString());


                    t_score5_2_gap[0] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "01").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[1] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "02").get("T_SCORE_GAP").toString());
                    t_score5_2_gap[2] = Float.parseFloat(getAnswerReportValue(dgnssReportStat5, 5, "05", "01", "03").get("T_SCORE_GAP").toString());


                    pioPdf.drawGraph01(t_score5_1_first, xStart, y[0] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#A9ADB2"));
                    pioPdf.drawGraph01(t_score5_2_first, xStart, y[10] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#A9ADB2"));


                    pioPdf.drawGraph01(t_score5_1, xStart, y[0] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#FF849F"));
                    pioPdf.drawGraph01(t_score5_2, xStart, y[10] + (height11 / 2f), barWidth, height11, 1, 1, pioPdf.hexa2Color("#FF87D4"));


                    pioPdf.drawText(testInfo.get("TEST_ORD_FIRST").toString() + "차",  pioPdf.px2mm(488f), pioPdf.px2mm(204f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);
                    pioPdf.drawText(testInfo.get("TEST_ORD").toString() + "차",  pioPdf.px2mm(519f), pioPdf.px2mm(204f, "Y"), "Pretendard Medium", 9.5f, false, false, Color.BLACK, -0.46f);

                    pioPdf.drawPicture("./assets/imgs/dgnss/btn/btn_total_anal_10_nst.png", pioPdf.px2mm(185f), pioPdf.px2mm(815f, "Y"), pioPdf.px2mm(220f), pioPdf.px2mm(33f));


                    for(i = 0 ; i < 10; i++) {
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_1_first[i])), x[0], y[i] + textHeight11, pioPdf.px2mm(12f));
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_1[i])), x[1], y[i] + textHeight11, pioPdf.px2mm(12f));

                        if (t_score5_1_gap[i] < 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_blue.png", x[2], y[i] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_1_gap[i])), x[3], y[i] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                        } else if (t_score5_1_gap[i] == 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[i] + (height11 / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                        } else {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_red.png", x[2], y[i] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_1_gap[i])), x[3], y[i] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#FF4800"));
                        }
                    }
                    for(i = 0 ; i < 3; i++) {
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_2_first[i])), x[0], y[i + 10] + textHeight11, pioPdf.px2mm(12f));
                        pioPdf.drawTextC(String.valueOf(Math.round(t_score5_2[i])), x[1], y[i + 10] + textHeight11, pioPdf.px2mm(12f));

                        if (t_score5_2_gap[i] < 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_down_blue.png", x[2], y[i+10] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_2_gap[i])), x[3], y[i+10] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#0B9DFF"));
                        } else if (t_score5_2_gap[i] == 0) {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_maintain.png", pioPdf.px2mm(555f), y[i+10] + (height11 / 2f), pioPdf.px2mm(8f), pioPdf.px2mm(1f));
                        } else {
                            pioPdf.drawPicture("./assets/imgs/dgnss/ico/ico_up_red.png", x[2], y[i+10] + textHeight11, pioPdf.px2mm(8f), pioPdf.px2mm(7f));
                            pioPdf.drawTextC(String.valueOf(Math.round(t_score5_2_gap[i])), x[3], y[i+10] + textHeight11, x3Width, "", 9.12f, false, false, pioPdf.hexa2Color("#FF4800"));
                        }
                    }

                    pioPdf.drawTextC(testInfo.get("TEST_ORD_FIRST").toString() + "차 : " + testInfo.get("TEST_DT_FIRST"), pioPdf.px2mm(211f), pioPdf.px2mm(802f, "Y"), pioPdf.px2mm(68f), "Pretendard Medium", 10f, false, false, pioPdf.hexa2Color("#A9ADB2"), -0.48f);
                    pioPdf.drawTextC(testInfo.get("TEST_ORD").toString() + "차 : " + testInfo.get("TEST_DT"), pioPdf.px2mm(309f), pioPdf.px2mm(802f, "Y"), pioPdf.px2mm(72f), "Pretendard", 10f, false, false, Color.black, -0.48f);

                }

            }

        } catch(NumberFormatException e) {
            log.error("error : {}", e.getMessage());
        } catch(IOException e) {
            log.error("error : {}", e.getMessage());
        } catch(Exception e){
            log.error("error : {}", e.getMessage());
        }

    }


    private Map<String, Object> getAnswerReportValue(List<Map<String, Object>> dgnssReports, int depth, String class3, String class4, String class5){

        Map<String, Object> retValue = new HashMap<String, Object>();

        for(Map<String, Object> dgnssReport : dgnssReports){
            if(depth == 3) {
                if (dgnssReport.get("CLASS3").toString().equals(class3)) {
                    retValue = dgnssReport;
                }
            }
            else if(depth == 4)
            {
                if (dgnssReport.get("CLASS3").toString().equals(class3) && dgnssReport.get("CLASS4").toString().equals(class4)) {
                    retValue = dgnssReport;
                }
            }
            else if(depth == 5)
            {
                if (dgnssReport.get("CLASS3").toString().equals(class3) && dgnssReport.get("CLASS4").toString().equals(class4) && dgnssReport.get("CLASS5").toString().equals(class5)) {
                    retValue = dgnssReport;
                }
            }
        }

        return retValue;

    }

    private String getHabitImageFileName(String class3, String class4, String class5, String tRank){
        String retValue = "";

        if(tRank.replaceAll(" ", "").equals("매우낮음") || tRank.replaceAll(" ", "").equals("낮음"))
            retValue = "./assets/imgs/dgnss/template02/btn/habit_" + class3 + "_" + class4 + "_" + class5 + "_on.png";
        else
            retValue = "./assets/imgs/dgnss/template02/btn/habit_" + class3 + "_" + class4 + "_" + class5 + "_off.png";

        return retValue;

    }

    private Color getColorByTRank(PioPdf pioPdf, String tRank, boolean reverse){
        Color retValue = Color.BLACK;


        if(reverse) {
            if (tRank.replaceAll(" ", "").contains("매우낮음") || tRank.contains("낮음"))
                retValue = pioPdf.hexa2Color("#0B9DFF");
            else if (tRank.contains("보통"))
                retValue = pioPdf.hexa2Color("#00D282");
            else if (tRank.replaceAll(" ", "").contains("매우높음") || tRank.contains("높음"))
                retValue = pioPdf.hexa2Color("#FF4800");
            else
                retValue = Color.BLACK;

            return retValue;
        }
        else {
            if (tRank.replaceAll(" ", "").contains("매우낮음") || tRank.contains("낮음"))
                retValue = pioPdf.hexa2Color("#FF4800");
            else if (tRank.contains("보통"))
                retValue = pioPdf.hexa2Color("#00D282");
            else if (tRank.replaceAll(" ", "").contains("매우높음") || tRank.contains("높음"))
                retValue = pioPdf.hexa2Color("#0B9DFF");
            else
                retValue = Color.BLACK;

            return retValue;
        }
    }

    private Color getColorByTRank(PioPdf pioPdf, String tRank) {
        return getColorByTRank(pioPdf, tRank, false);
    }

    private String getMarkLevel3ImageFileName(String tRank, String dgnss, boolean reverse, int page){
        String retValue = "";
        String dgnssTemplate = "";

        if(dgnss.toLowerCase().equals("dgnss10")){
            dgnssTemplate = "template01";
        }else{
            dgnssTemplate = "template02";
        }

        if(reverse) {
            if (tRank.replaceAll(" ", "").equals("매우낮음") || tRank.replaceAll(" ", "").equals("낮음"))
                retValue = "./assets/imgs/dgnss/" + dgnssTemplate + "/ico/ico_mark_high_p" + page + ".png";
            else if (tRank.replaceAll(" ", "").equals("보통"))
                retValue = "./assets/imgs/dgnss/" + dgnssTemplate + "/ico/ico_mark_mid_p" + page + ".png";
            else if (tRank.replaceAll(" ", "").equals("높음") || tRank.replaceAll(" ", "").equals("매우높음"))
                retValue = "./assets/imgs/dgnss/" + dgnssTemplate + "/ico/ico_mark_low_p" + page + ".png";
            else {
                //log.debug("tRank : {}", tRank);
                retValue = "";
            }
        }
        else{
            if (tRank.replaceAll(" ", "").equals("매우낮음") || tRank.replaceAll(" ", "").equals("낮음"))
                retValue = "./assets/imgs/dgnss/" + dgnssTemplate + "/ico/ico_mark_low_p" + page + ".png";
            else if (tRank.replaceAll(" ", "").equals("보통"))
                retValue = "./assets/imgs/dgnss/" + dgnssTemplate + "/ico/ico_mark_mid_p" + page + ".png";
            else if (tRank.replaceAll(" ", "").equals("높음") || tRank.replaceAll(" ", "").equals("매우높음"))
                retValue = "./assets/imgs/dgnss/" + dgnssTemplate + "/ico/ico_mark_high_p" + page + ".png";
            else {
                //log.debug("tRank : {}", tRank);
                retValue = "";
            }
        }

        return retValue;
    }

    private String getMarkImageFileName(String tRank){
        String retValue = "";

        if(tRank.replaceAll(" ", "").equals("매우낮음"))
            retValue = "./assets/imgs/dgnss/template02/ico/ico_mark_1.png";
        else if(tRank.replaceAll(" ", "").equals("낮음"))
            retValue = "./assets/imgs/dgnss/template02/ico/ico_mark_2.png";
        else if(tRank.replaceAll(" ", "").equals("보통"))
            retValue = "./assets/imgs/dgnss/template02/ico/ico_mark_3.png";
        else if(tRank.replaceAll(" ", "").equals("높음"))
            retValue = "./assets/imgs/dgnss/template02/ico/ico_mark_4.png";
        else if(tRank.replaceAll(" ", "").equals("매우높음"))
            retValue = "./assets/imgs/dgnss/template02/ico/ico_mark_5.png";

        return retValue;
    }
}
