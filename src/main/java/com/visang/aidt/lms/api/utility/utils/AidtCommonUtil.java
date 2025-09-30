package com.visang.aidt.lms.api.utility.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.visang.aidt.lms.api.utility.exception.AidtException;
import com.visang.aidt.lms.global.ResponseDTO;
import com.visang.aidt.lms.global.vo.CustomBody;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Mockup 작성용 테스트 Util
 * - (추후 삭제 예정)
 */
@Slf4j
public class AidtCommonUtil {

    /**
     * 입력된 String 값을 각 Object type 에 맞게 return
     *
     * @param string 문자열
     * @return Object
     */
    public static Object stringToValue(String string) {
        if (string.isEmpty()) {
            return string;
        }
        if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("null")) {
            return "";
        }

        char initial = string.charAt(0);
        if ((initial >= '0' && initial <= '9') || initial == '-') {
            try {
                if (string.indexOf('.') > -1 || string.indexOf('e') > -1
                        || string.indexOf('E') > -1
                        || "-0".equals(string)) {
                    Double d = Double.valueOf(string);
                    if (!d.isInfinite() && !d.isNaN()) {
                        return d;
                    }
                } else {
                    Long myLong = Long.valueOf(string);
                    if (string.equals(myLong.toString())) {
                        if (myLong.longValue() == myLong.intValue()) {
                            return Integer.valueOf(myLong.intValue());
                        }
                        return myLong;
                    }
                }
            } catch (Exception ignore) {
                log.error(CustomLokiLog.errorLog(ignore));
            }
        }
        return string;
    }

    /**
     * 엑셀에서 만든 테이블 그대로 copy + paste 한 String 을 Map 으로 변환
     *
     * @param str 문자열
     * @return rootMap
     */
    public static List<Map<String, Object>> convertStrToMap(String str) {
        // 응답 데이터
        List<Map<String, Object>> resultList = new ArrayList<>();

        Map<String, Object> resultMap;

        String[] lineArr = str.split("__"); // 구분자 $$로 object 나누기

        for (String lineStr : lineArr) {
            resultMap = new HashMap<>();
            String repStr = lineStr.replaceAll(",", "");
            repStr = lineStr.replaceAll("\r\n|\r|\n\r|\n", ",");

            String[] arrStr = repStr.split(",");

            for (String tmp : arrStr) {
                String[] arrTmp = tmp.split("\t");
                if (arrTmp.length == 2) {
                    resultMap.put(String.valueOf(arrTmp[0]), stringToValue(arrTmp[1]));
                }
            }
            resultList.add(resultMap);
        }
        return resultList;
    }

    /**
     * 셀에서 만든 테이블 그대로 copy + paste 한 String 을 Map 으로 변환 2
     *
     * @param data 문자열
     * @return List<Map < String, Object>>
     */
    private static List<Map<String, Object>> convertStrToMap3(String data) {
        List<Map<String, Object>> dataList = new ArrayList<>();

        String[] lines = data.split("\n");
        String[] headers = lines[0].split("\t");

        for (int i = 1; i < lines.length; i++) {
            String[] values = lines[i].split("\t");

            Map<String, Object> map = new HashMap<>();
            for (int j = 0; j < headers.length; j++) {
                map.put(headers[j], values[j]);
            }

            dataList.add(map);
        }

        return dataList;
    }


    /**
     * map 에 필수요소 추가하여 반환
     *
     * @param str 문자열
     * @return Map<String, Object>
     */
    public static List<Map<String, Object>> makeJsonFormatSample(String str) {
        return convertStrToMap(str);
    }

    public static Map<String, Object> makeJsonFormatSample2(JSONObject json) {
        return json.toMap();
    }

    /**
     * map 에 필수요소 추가하여 반환 type2
     *
     * @param str 문자열
     * @return 반환
     */
    public static List<Map<String, Object>> makeJsonFormatSample3(String str) {
        return convertStrToMap3(str);
    }

    /**
     * HttpServletRequest 에서 ParamMap 을 뽑아서 반환한다.
     *
     * @param request 요청객체
     * @return 맵객체
     */
    public static Map<String, Object> converHttpRequestToParamMap(HttpServletRequest request) {

        Map<String, Object> param = new HashMap<>();

        Enumeration<String> parameterNames = request.getParameterNames();
        log.debug("request START ==================================");
        log.debug("Method: {}", request.getMethod());
        log.debug("URI: {}", request.getRequestURI());

        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            log.debug("{}: {}", paramName, paramValue);

            param.put(paramName, paramValue);
        }
        log.debug("request END ==================================");

        return param;
    }

    public static ResponseDTO<CustomBody> makeResultSuccess(Map<String, Object> paramData, Object resultData, String resultMessage) {
        return ResponseDTO.of()
                .header(null) // optional 헤더 정의가 필요할때
                .success()
                .resultCode(HttpStatus.OK)
                .paramData(paramData)
                .resultData(resultData)
                .resultMessage(resultMessage)
                .build();
    }

    public static <T> ResponseDTO<CustomBody> makeResultSuccess(T paramData, Object resultData, String resultMessage) {
        return ResponseDTO.of()
                .header(null) // optional 헤더 정의가 필요할때
                .success()
                .resultCode(HttpStatus.OK)
                .paramData(paramData)
                .resultData(resultData)
                .resultMessage(resultMessage)
                .build();
    }

    public static ResponseDTO<CustomBody> makeResultFail(Map<String, Object> paramData, Map<String, Object> resultData, String resultMessage) {
        return ResponseDTO.of()
                .header(null) // optional 헤더 정의가 필요할때
                .fail()
                .resultCode(HttpStatus.OK)
                .paramData(paramData)
                .resultData(resultData)
                .resultMessage(resultMessage)
                .build();
    }

    public static <T> ResponseDTO<CustomBody> makeResultFail(T paramData, Map<String, Object> resultData, String resultMessage) {
        return ResponseDTO.of()
                .header(null) // optional 헤더 정의가 필요할때
                .fail()
                .resultCode(HttpStatus.OK)
                .paramData(paramData)
                .resultData(resultData)
                .resultMessage(resultMessage)
                .build();
    }

    public static List<Long> strToLongList(String str) {
        StringUtils.replace(str, " ", "");
        String[] split = StringUtils.split(str.replaceAll("\\s+", ""), ",");
        return Arrays.stream(split).map(Long::valueOf).collect(Collectors.toList());
    }

    public static List<String> strToStringList(String str) {
        if (StringUtils.isBlank(str)) return new ArrayList<>();

        String[] split = StringUtils.split(str.replaceAll("\\s+", ""), ","); // 공백제거 후 split
        return Arrays.stream(split).map(String::valueOf).collect(Collectors.toList());
    }

    public static PagingInfo ofPageInfo(List<Map> mapList, Pageable pageable, long total) {
        var pageInfo = new PageImpl<>(mapList, pageable, total);

        return PagingInfo.builder()
                .size(pageInfo.getNumberOfElements())
                .totalElements(total)
                .totalPages(pageable.getPageSize() == 0 ? 1 : (int) Math.ceil((double) total / pageable.getPageSize()))
                .number(pageInfo.getNumber())
                .build();

        /*
        return PagingInfo.builder()
            .size(pageInfo.getNumberOfElements())
            .totalElements(pageInfo.getTotalElements())
            .totalPages(pageInfo.getTotalPages())
            .number(pageInfo.getNumber())
            .build();
         */
    }

    /**
     * Date type 을 yy.mm.dd 형식으로 반환
     *
     * @param dt
     * @return
     */
    public static String formatDtYYMMDDComma(Date dt) {
        return formatDtToString(dt, "yyyy-MM-dd");
    }


    public static String formatDtToString(Date dt, String format) {
        if (dt == null || format == null) {
            return "";
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(dt);

        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            return "";
        }

    }

    public static String formatCurrentDtToString(String format) {
        if (format == null) {
            return "";
        }

        Date dt = new Date();
        return formatDtToString(dt, format);

    }

    /**
     * "12:12" 형식의 str을 입력받아, 초단위 int 로 반환
     *
     * @param timeString 12:12 형식
     * @return int
     */
    public static int convertToSeconds(String timeString) {
        if (StringUtils.isBlank(timeString)) {
            log.warn("No timeString");
            return 0;
        }

        String[] timeParts = timeString.split(":");

        if (timeParts.length != 2) {
            log.warn("Invalid timeString");
            return 0;
        }

        // 분과 초를 추출하여 정수로 변환
        int minutes = Integer.parseInt(timeParts[0]);
        int seconds = Integer.parseInt(timeParts[1]);

        // 전체 시간을 초로 변환
        return minutes * 60 + seconds;
    }

    /**
     * 초단위 값을 입력받아 12:12 형식으로 반환
     *
     * @param totalSeconds 초단위
     * @return String 12:12
     */
    public static String convertToTimeString(int totalSeconds) {
        // 초를 분과 초로 분리
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        // 분:초 형식의 문자열 생성
        String timeString = String.format("%d:%02d", minutes, seconds);

        return timeString;
    }


    public static LinkedHashMap<Object, Object> filterToMap(List<String> itemList, Object obj) {
        var tgtMap = new LinkedHashMap<>();
        if (Objects.isNull(obj)) return tgtMap;

        var srcMap = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .convertValue(obj, Map.class);

//        var srcMap = new ObjectMapper().convertValue(obj, Map.class);
        itemList.forEach(s -> {
            tgtMap.put(s, srcMap.get(s));
        });
        return tgtMap;
    }

    public static List<LinkedHashMap<Object, Object>> filterToList(List<String> itemList, List<Map> objList) {
        return CollectionUtils.emptyIfNull(objList).stream().map(s -> {
            return AidtCommonUtil.filterToMap(itemList, s);
        }).toList();
    }

    public static String epochTimeToFormatString(Long epoch, String format) {
        if (epoch == null) return null;
        String formatted = Instant.ofEpochMilli(epoch)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()
                .format(DateTimeFormatter.ofPattern(format));
        return formatted;
    }

    public static String stringToDateFormat(String str, String format) {
        if (str == null) return null;
        LocalDateTime dt = LocalDateTime.parse(str);
        return dt.format(DateTimeFormatter.ofPattern(format));
    }


    /**
     * 평가기준 결과명 구하기
     *
     * @param evlStdrSetAt      평가기준 설정여부
     * @param evlStdrSet        평가기준 (1: 상/중/하, 2: 통과/실패, 3: 점수)
     * @param evlResultScrTotal 학생의 총점
     * @param evlGdStdrScr      상 기준점수
     * @param evlAvStdrScr      중 기준점수
     * @param evlPsStdrScr      통과 기준점수
     * @param submAt 제출 여부
     * @return 평가기준 결과명
     */
//    public static String getEvlResultGradeNm(String evlStdrSetAt, Integer evlStdrSet, Double evlResultScrTotal, Integer evlGdStdrScr, Integer evlAvStdrScr, Integer evlPsStdrScr, String submAt) {
//        log.debug("getEvlResultGradeNm------------------------- START");
//        log.debug("evlStdrSetAt:{}", evlStdrSetAt);
//        log.debug("evlStdrSet:{}", evlStdrSet);
//        log.debug("evlResultScrTotal:{}", evlResultScrTotal);
//        log.debug("evlGdStdrScr:{}", evlGdStdrScr);
//        log.debug("evlAvStdrScr:{}", evlAvStdrScr);
//        log.debug("evlPsStdrScr:{}", evlPsStdrScr);
//        log.debug("submAt:{}", submAt);
//
//        if(evlResultScrTotal == null) return null;
//
//        try {
//            if (StringUtils.equals(evlStdrSetAt, "Y") ) {
//                switch (evlStdrSet) {
//                    case 1:
//                        if (evlResultScrTotal >= evlGdStdrScr) { return "상"; }
//                        else if(evlResultScrTotal >= evlAvStdrScr) { return "중"; }
//                        else { return "하"; }
//                    case 2: return (evlResultScrTotal >= evlPsStdrScr) ? "통과" : "실패";
//                    case 3: return String.valueOf(evlResultScrTotal); // 점수일때는 그냥 점수 반환- "" 반환으로 바뀌면 수정할것
//                    default: return "";
//                }
//            } else {
//                return StringUtils.equals("Y", submAt) ? "완료" : "미완료"; // 세팅 안됐을 때 제출여부에 따라 완료/미완료
//            }
//        } catch (Exception e) {
//            log.error("getEvlResultGradeNm err: ", e);
//            return "";
//        }
//    }

    /**
     * @param evlStdrSetAt      평가기준 설정여부
     * @param evlStdrSet        평가기준 (1: 상/중/하, 2: 통과/실패, 3: 점수)
     * @param evlIemScrTotal    만점배점
     * @param evlResultScrTotal 학생의 총점
     * @param evlGdStdrScr      상 기준점수
     * @param evlAvStdrScr      중 기준점수
     * @param evlPsStdrScr      통과 기준점수
     * @param submAt            제출 여부
     * @return 평가기준 결과명
     */
    public static String getEvlResultGradeNmNew(String evlStdrSetAt, Integer evlStdrSet, Double evlIemScrTotal, Double evlResultScrTotal, Integer evlGdStdrScr, Integer evlAvStdrScr, Integer evlPsStdrScr, String submAt) {
        log.debug("getEvlResultGradeNm------------------------- START");
        log.debug("evlStdrSetAt:{}", evlStdrSetAt);
        log.debug("evlStdrSet:{}", evlStdrSet);
        log.debug("evlIemScrTotal:{}", evlIemScrTotal);
        log.debug("evlResultScrTotal:{}", evlResultScrTotal);
        log.debug("evlGdStdrScr:{}", evlGdStdrScr);
        log.debug("evlAvStdrScr:{}", evlAvStdrScr);
        log.debug("evlPsStdrScr:{}", evlPsStdrScr);
        log.debug("submAt:{}", submAt);

        if (evlResultScrTotal == null || evlIemScrTotal == null) return null;

        double totalScoreRate = Math.round(((evlResultScrTotal / evlIemScrTotal) * 100) * 100.0) / 100.0; // 백분율 점수(소수점 2자리까지)

        try {
            if (StringUtils.equals(evlStdrSetAt, "Y")) {
                switch (evlStdrSet) {
                    case 1:
                        if (totalScoreRate >= evlGdStdrScr) {
                            return "상";
                        } else if (totalScoreRate >= evlAvStdrScr) {
                            return "중";
                        } else {
                            return "하";
                        }
                    case 2:
                        return (totalScoreRate >= evlPsStdrScr) ? "통과" : "실패";
                    case 3:
                        return String.valueOf(totalScoreRate); // 점수일때는 그냥 점수 반환- "" 반환으로 바뀌면 수정할것
                    default:
                        return "";
                }
            } else {
                return StringUtils.equals("Y", submAt) ? "완료" : "미완료"; // 세팅 안됐을 때 제출여부에 따라 완료/미완료
            }
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            log.error("getEvlResultGradeNm err: ", e);
            return "";
        }
    }

    public static Double floatToDouble(Float num) {
        if (num == null) return 0D;
        return num.doubleValue();
    }

    public static Long intToLong(Object obj) {
        if (obj == null) return null;
        if (obj instanceof Integer num) {
            return Long.valueOf(num.toString());
        }
        return null;
    }

    public static void checkRequiredParameter(Map<String, Object> paramData, List<String> requiredParams) throws Exception {
        List<String> requireList = new ArrayList<>();
        requiredParams.forEach(s -> {
            if (Objects.isNull(paramData.get(s))) {
                requireList.add(s);
            }
        });
        if (!requireList.isEmpty()) {
            throw new AidtException("required parameter is empty: " + String.join(",", requireList));
        }
    }

    /**
     * [{a=1, b=1}, {a=1, b=1}] 형태 String 데이터를 리스트맵에 저장하기
     *
     * @param paramData
     * @return List<Map < String, Object>>
     * @throws Exception
     */
    public static List<Map<String, Object>> objectStringToListMap(String paramData) {
        String[] mapStrings = paramData.split("[\\{\\}]");

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (String mapString : mapStrings) {
            Map<String, Object> map = new HashMap<>();
            String[] keyValuePairs = mapString.split(", ");
            for (String pair : keyValuePairs) {
                String[] entry = pair.split("=");
                if (entry.length == 2) {
                    map.put(entry[0], entry[1]);
                }
            }
            if (!map.isEmpty()) {
                dataList.add(map);
            }
        }

        return dataList;
    }

    /**
     * Map 의 1개 key-value 를 이용해서 VO 필드명이 같으면 set 한다. 없으면 false 반환
     *
     * @param targetObject
     * @param key
     * @param value
     * @return
     * @throws IllegalAccessException
     */
    public static boolean mapToVoByKeyValue(Object targetObject, String key, Object value) {
        try {
            Field field = targetObject.getClass().getDeclaredField(key);
            field.setAccessible(true);
            field.set(targetObject, value);
            return true;
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            // 해당 키값과 일치하는 변수가 없는 경우, false
            return false;
        }
    }

    /**
     * Map의 모든 key, value 값을 출력한다. log 레벨은  debug.
     *
     * @param map
     */
    public static void printMapKeyValue(Map map) {
        try {
            AtomicInteger count = new AtomicInteger(1);
            map.forEach((key, value) -> log.debug("{}. {}:{}", count.getAndIncrement(), key, value));
        } catch (Exception e) {
            log.error(CustomLokiLog.errorLog(e));
            // no action
        }
    }

    /**
     * Object 를 Long 형태로 반환
     *
     * @param val
     * @return Long
     */
    public static Long getLongValueFromObject(Object val) {
        if (val == null) return null;

        if (val instanceof Number) {
            return Long.valueOf(String.valueOf(val));
        }

        return null;

    }

    /**
     * 지문별 응답율 String 만들기
     *
     * @param answer 응답String List
     * @return 지문별응답율
     */
    public static String getAnswerCountString(List<String> answer) {

        Map<String, Integer> countMap = new TreeMap<>();

        // 제거할 문자
        String[] removeStr = {"\""};

        // answer 배열을 순회하며 번호별 개수를 세기
        for (String str : answer) {

            for (String s : removeStr) {
                str = str.replace(s, "");
            }

            if (str.matches("\\[[\\d,]+\\]")) { // 숫자가 포함된 경우
                // 대괄호 제거 및 쉼표를 기준으로 숫자 분리
                String[] numbers = str.replaceAll("\\[|\\]", "").split(",");
                for (String numStr : numbers) {
                    // 빈 문자열이 아닌 경우에만 숫자로 변환하여 개수를 증가
                    if (!numStr.isEmpty()) {
                        countMap.put(numStr, countMap.getOrDefault(numStr, 0) + 1);
                    }
                }
            } else { // 숫자가 아닌 경우
                if (!str.matches("\\[\\s*\\]")) { // 기입한 답이 있는 경우만.
                    countMap.put(str, countMap.getOrDefault(str, 0) + 1);
                }
            }
        }

        // 결과 문자열 생성
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, Integer> entry : countMap.entrySet()) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(entry.getKey()).append("(").append(entry.getValue()).append("명)");
        }

        return result.toString();
    }

    public static void checkSubIdParameter(Map<String, Object> paramData) {
        if (ObjectUtils.isEmpty(paramData.get("subId"))) {
            paramData.put("subId", 0);
        }
    }

    public static void checkDefaultParameter(Map<String, Object> paramData, String param, Object val) {
        if (ObjectUtils.isEmpty(paramData.get(param))) {
            paramData.put(param, val);
        }
    }

    public static String checkCreatorVisangYn(Object creatorIdList) {
        if (creatorIdList == null) return "Y"; // 출처 선택이 없는 경우에도 비상 셋트지를 목록을 조회한다.
        if (creatorIdList instanceof List creatorIds) {
            if (creatorIds.isEmpty()) return "Y";
            return creatorIds.contains("visang") ? "Y" : "N";
        }
        return "N";
    }

    /**
     * [영어] - 발음평가형 (아티클의 questionType - ptqz)의 발성문장에 대한 발성평가 점수 정보(proficiencyScore) 조회
     * - (ID, EN_HOLISTIC, EN_INTONATION, EN_SEGMENT, EN_RATE, EN_PITCH)
     *
     * @param jsonValue
     * @return
     */
    public static List<Map<String, Object>> parseProficiencyScore(String jsonValue) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            // ID 처리용
            AtomicInteger libTextId = new AtomicInteger(0);

            // Json 문자열 -> Map
            Gson gson = new Gson();
            List<Map<String, Object>> list = gson.fromJson(jsonValue, ArrayList.class);

            CollectionUtils.emptyIfNull(list)
                    .stream()
                    .filter(m -> MapUtils.isNotEmpty(m)) // null은 제외
                    .peek(m -> {
                        libTextId.set(MapUtils.getInteger(m, "id", 0));
                    })
                    .map(m -> m.entrySet())
                    .forEach(m -> {
                        m.stream()
                                .filter(s -> s.getKey().equals("sentenceLevel"))
                                .forEach(s -> {
                                    Map<String, Object> resultMap = new HashMap<>();
                                    resultMap.put("libTextId", libTextId.get());

                                    Map<String, Object> x = (Map<String, Object>) s.getValue();
                                    // Map 출력
                                    for (Map.Entry<String, Object> entry : x.entrySet()) {
                                        if (entry.getKey().equals("proficiencyScore")) {
                                            ArrayList<Map<String, Object>> valueList = (ArrayList<Map<String, Object>>) entry.getValue();
                                            for (Map<String, Object> valueMap : valueList) {
                                        /*if("acoustic".equals(valueMap.get("name"))) {
                                            continue;
                                        }*/
                                                // EN_HOLISTIC, EN_INTONATION, EN_SEGMENT, EN_RATE, EN_PITCH 값 저장
                                                resultMap.put(MapUtils.getString(valueMap, "name"), MapUtils.getFloat(valueMap, "score"));
                                            }

                                            resultList.add(resultMap);
                                        }
                                    }
                                });
                    });
        } catch (Exception e) {
//            e.printStackTrace();
            CustomLokiLog.errorLog(e);
        }

        return resultList;
    }

    /**
     * 발성 평가 데이터를 파싱하여 vocal_evl_info_detail 테이블용 데이터를 생성
     * - 두 가지 입력 형식 지원:
     * 1. [null, {...}] 형태의 배열 데이터
     * 2. {"resultDetailId": "...", "subMitAnw": "[null, {...}]", ...} 형태의 객체 데이터
     *
     * @param jsonString JSON 문자열
     * @return 테이블 데이터 리스트
     */
    public static List<Map<String, Object>> parseVocalEvaluationDetail(String jsonString, String vocalEvlId) {
        try {
            // subMitAnw 필드가 있는 객체인지 확인
            if (jsonString.contains("\"subMitAnw\":")) {
                Gson gson = new Gson();
                Map<String, Object> rootObject = gson.fromJson(jsonString, Map.class);

                // subMitAnw 필드 값을 추출
                if (rootObject.containsKey("subMitAnw")) {
                    String subMitAnw = (String) rootObject.get("subMitAnw");
                    String resultDetailId = rootObject.containsKey("resultDetailId") ?
                            String.valueOf(rootObject.get("resultDetailId")) : null;

                    return parseArrayData(subMitAnw, resultDetailId, vocalEvlId);
                }
            }

            // 직접 배열 형태의 데이터인 경우
            return parseArrayData(jsonString, null, vocalEvlId);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            // CustomLokiLog.errorLog(e);
            return new ArrayList<>();
        }
    }

    /**
     * [null, {...}] 형태의 배열 데이터 파싱
     *
     * @param jsonString     배열 형태의 JSON 문자열
     * @param resultDetailId 외부에서 전달받은 resultDetailId (없으면 null)
     * @return 테이블 데이터 리스트
     */
    private static List<Map<String, Object>> parseArrayData(String jsonString, String resultDetailId, String vocalEvlId) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            // JSON 파싱 - 배열 구조
            Gson gson = new Gson();
            List<Object> jsonArray = gson.fromJson(jsonString, ArrayList.class);

            // 유효한 데이터 찾기 (보통 인덱스 1에 위치)
            Map<String, Object> recordData = null;
            Integer recordId = null;

            for (Object item : jsonArray) {
                if (item != null && item instanceof Map) {
                    Map<String, Object> mapItem = (Map<String, Object>) item;
                    if (mapItem.containsKey("sentenceLevel") && mapItem.containsKey("wordLevel")) {
                        recordData = mapItem;
                        if (mapItem.containsKey("id")) {
                            recordId = ((Number) mapItem.get("id")).intValue();
                        }
                        break;
                    }
                }
            }

            if (recordData == null) {
                throw new RuntimeException("유효한 발성 평가 데이터를 찾을 수 없습니다.");
            }

            // wordLevel 데이터 가져오기
            List<Map<String, Object>> wordLevelList = (List<Map<String, Object>>) recordData.get("wordLevel");

            // nativeCompare 데이터 가져오기
            Map<String, Object> sentenceLevel = (Map<String, Object>) recordData.get("sentenceLevel");
            Map<String, Object> nativeCompare = (Map<String, Object>) sentenceLevel.get("nativeCompare");

            // 억양 정보 가져오기
            List<Map<String, Object>> intonationList = (List<Map<String, Object>>) nativeCompare.get("intonation");

            // 강세 정보 가져오기
            List<Map<String, Object>> stressList = (List<Map<String, Object>>) nativeCompare.get("stress");

            // vocal_evl_scr_id 설정 우선순위:
            // 1. 외부에서 전달받은 resultDetailId
            // 2. 데이터 내부의 id
            String vocalEvlScrId = resultDetailId != null ? resultDetailId :
                    (recordId != null ? String.valueOf(recordId) : null);

            // 단어별 데이터 생성
            for (int i = 0; i < wordLevelList.size(); i++) {
                Map<String, Object> wordData = wordLevelList.get(i);
                Map<String, Object> intonationData = intonationList.get(i);
                Map<String, Object> stressData = stressList.get(i);

                Map<String, Object> resultMap = new HashMap<>();

                // vocal_evl_scr_id 설정
                if (vocalEvlScrId != null) {
                    resultMap.put("vocal_evl_scr_id", vocalEvlScrId);
                }

                // 단어 정보
                resultMap.put("word", wordData.get("text"));
                resultMap.put("word_index", wordData.get("index"));
                resultMap.put("start_time_sec", wordData.get("startTimeInSec"));
                resultMap.put("end_time_sec", wordData.get("endTimeInSec"));
                resultMap.put("word_stress", wordData.get("stress"));

                // acoustic_score 가져오기
                List<Map<String, Object>> proficiencyScoreList = (List<Map<String, Object>>) wordData.get("proficiencyScore");
                for (Map<String, Object> scoreData : proficiencyScoreList) {
                    if ("acoustic".equals(scoreData.get("name"))) {
                        resultMap.put("acoustic_score", scoreData.get("score"));
                        break;
                    }
                }

                // 억양 정보
                resultMap.put("intonation_difference", intonationData.get("difference"));

                Map<String, Object> xPosition = (Map<String, Object>) intonationData.get("xPosition");
                resultMap.put("xPosition_reference", xPosition.get("reference"));
                resultMap.put("xPosition_user", xPosition.get("user"));

                Map<String, Object> yPosition = (Map<String, Object>) intonationData.get("yPosition");
                resultMap.put("yPosition_reference", yPosition.get("reference"));
                resultMap.put("yPosition_user", yPosition.get("user"));

                // 강세 정보
                resultMap.put("stress_difference", stressData.get("difference"));
                resultMap.put("stress_reference", stressData.get("reference"));
                resultMap.put("stress_user", stressData.get("user"));
                resultMap.put("vocal_evl_scr_id", vocalEvlId);
                resultMap.put("id", null);
                resultList.add(resultMap);
            }

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            // CustomLokiLog.errorLog(e);
        }

        return resultList;
    }

    /**
     * 글자별 색상 정보 파싱 (ColorOfLetter)
     *
     * @param jsonString JSON 문자열
     * @return 글자별 색상 정보 리스트
     */
    public static List<Map<String, Object>> parseColorOfLetter(String jsonString, String vocalEvlDetailId) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            // JSON 파싱 - 배열 구조
            Gson gson = new Gson();
            List<Object> jsonArray = null;

            // subMitAnw 필드가 있는 객체인지 확인
            if (jsonString.contains("\"subMitAnw\":")) {
                Map<String, Object> rootObject = gson.fromJson(jsonString, Map.class);
                if (rootObject.containsKey("subMitAnw")) {
                    jsonArray = gson.fromJson((String) rootObject.get("subMitAnw"), ArrayList.class);
                }
            } else {
                jsonArray = gson.fromJson(jsonString, ArrayList.class);
            }

            if (jsonArray == null) {
                return resultList;
            }

            // 유효한 데이터 찾기
            Map<String, Object> recordData = null;
            for (Object item : jsonArray) {
                if (item != null && item instanceof Map) {
                    Map<String, Object> mapItem = (Map<String, Object>) item;
                    if (mapItem.containsKey("wordLevel")) {
                        recordData = mapItem;
                        break;
                    }
                }
            }

            if (recordData == null) {
                return resultList;
            }

            // wordLevel 데이터 가져오기
            List<Map<String, Object>> wordLevelList = (List<Map<String, Object>>) recordData.get("wordLevel");

            // 각 단어별로 ColorOfLetter 정보 추출
            for (int i = 0; i < wordLevelList.size(); i++) {
                Map<String, Object> wordData = wordLevelList.get(i);

                // 단어 텍스트 가져오기
                String wordText = (String) wordData.get("text");
                if (wordText == null) {
                    continue; // 텍스트가 없으면 건너뜀
                }

                // ColorOfLetter 추출
                List<Map<String, Object>> colorOfLetterList = (List<Map<String, Object>>) wordData.get("ColorOfLetter");
                if (colorOfLetterList == null) {
                    continue;
                }

                for (int j = 0; j < colorOfLetterList.size(); j++) {
                    Map<String, Object> colorData = colorOfLetterList.get(j);

                    // 각 항목에는 하나의 키-값 쌍만 있음 (예: {"m":"red"})
                    for (Map.Entry<String, Object> entry : colorData.entrySet()) {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("word_text", wordText);        // vocal_detail_id 대신 단어 텍스트 사용
                        resultMap.put("letter", entry.getKey());
                        resultMap.put("position", j);
                        resultMap.put("color", entry.getValue());
                        resultMap.put("vocal_detail_id", vocalEvlDetailId);
                        resultList.add(resultMap);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            // CustomLokiLog.errorLog(e);
        }

        return resultList;
    }

    /**
     * 음소 수준 정보 파싱 (phoneLevel)
     *
     * @param jsonString JSON 문자열
     * @return 음소 수준 정보 리스트
     */
    public static List<Map<String, Object>> parsePhoneLevel(String jsonString, String vocalEvlId) {
        List<Map<String, Object>> resultList = new ArrayList<>();

        try {
            // JSON 파싱 - 배열 구조
            Gson gson = new Gson();
            List<Object> jsonArray = null;
            String vocalEvlScrId = null;

            // subMitAnw 필드가 있는 객체인지 확인
            if (jsonString.contains("\"subMitAnw\":")) {
                Map<String, Object> rootObject = gson.fromJson(jsonString, Map.class);
                if (rootObject.containsKey("subMitAnw")) {
                    jsonArray = gson.fromJson((String) rootObject.get("subMitAnw"), ArrayList.class);

                    if (rootObject.containsKey("resultDetailId")) {
                        vocalEvlScrId = String.valueOf(rootObject.get("resultDetailId"));
                    }
                }
            } else {
                jsonArray = gson.fromJson(jsonString, ArrayList.class);
            }

            if (jsonArray == null) {
                return resultList;
            }

            // 유효한 데이터 찾기
            Map<String, Object> recordData = null;
            for (Object item : jsonArray) {
                if (item != null && item instanceof Map) {
                    Map<String, Object> mapItem = (Map<String, Object>) item;
                    if (mapItem.containsKey("phoneLevel")) {
                        recordData = mapItem;
                        if (vocalEvlScrId == null && mapItem.containsKey("id")) {
                            vocalEvlScrId = String.valueOf(mapItem.get("id"));
                        }
                        break;
                    }
                }
            }

            if ((recordData == null || recordData.size() == 0) && vocalEvlScrId == null) {
                return resultList;
            }

            // phoneLevel 데이터 가져오기
            List<List<Map<String, Object>>> phoneLevelList = (List<List<Map<String, Object>>>) recordData.get("phoneLevel");

            int index = 0;
            // 각 음소 정보 추출
            for (List<Map<String, Object>> wordPhones : phoneLevelList) {
                for (Map<String, Object> phoneData : wordPhones) {
                    Map<String, Object> resultMap = new HashMap<>();
                    resultMap.put("vocal_evl_scr_id", vocalEvlId);
                    resultMap.put("vocal_group_id", index);
                    resultMap.put("word_index", phoneData.get("windex"));
                    resultMap.put("phone_index", phoneData.get("pindex"));
                    resultMap.put("text", phoneData.get("text"));
                    resultMap.put("ipa", phoneData.get("ipa"));

                    // score가 문자열로 되어있으므로 변환
                    String scoreStr = (String) phoneData.get("score");
                    double score = 0;
                    try {
                        score = Double.parseDouble(scoreStr);
                    } catch (Exception e) {
                        // 파싱 실패 시 기본값 0
                        log.error("Unexpected error: {}", e.getMessage(), e);
                    }
                    resultMap.put("scr", score);

                    resultMap.put("start_time_sec", phoneData.get("startTimeInSec"));
                    resultMap.put("end_time_sec", phoneData.get("endTimeInSec"));

                    resultList.add(resultMap);

                }
                index++;
            }

        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
            // CustomLokiLog.errorLog(e);
        }

        return resultList;
    }


    /**
     * 평가/과제 시작시 상태 및 시작/종료 일시를 체크한다.
     *
     * @param sttsCd   상태
     * @param objStDt  시작일시
     * @param objEndDt 종료일시
     * @return errMsg(empty 일때 성공. 메세지 값이 있을때 실패)
     */
    public static String validateStart(int sttsCd, Object objStDt, Object objEndDt) {

        // validation
        Map<String, String> errMap = new HashMap<>();
        // 상태 체크
        if (sttsCd != 2) {
            return "현재 상태가 진행 중이 아닙니다.";
        }
        // 현재 시간
        LocalDateTime now = LocalDateTime.now();
        // 3. 시작시간 체크
        if (objStDt != null) {
            if (objStDt instanceof LocalDateTime) {
                LocalDateTime stDt = (LocalDateTime) objStDt;
                log.info("stDt:{}", stDt);
                if (now.isBefore(stDt)) {
                    log.error("start Time error");
                    return "응시 기간이 아닙니다.";
                }
                log.info("start Time passed");
            } else {
                log.info("startDt is not LocalDateTime");
            }
        } else {
            log.info("startDt is null. pass");
        }
        // 4. 종료시간 체크
        if (objEndDt != null) {
            if (objEndDt instanceof LocalDateTime) {
                LocalDateTime endDt = (LocalDateTime) objEndDt;
                log.info("endDt:{}", endDt);
                if (now.isAfter(endDt)) {
                    log.error("end Time error");
                    return "응시 기간이 아닙니다.";
                }
                log.info("end Time passed");
            } else {
                log.info("endDt is not LocalDateTime");
            }
        } else {
            log.info("endDt is null. pass");
        }

        log.info("all pass!");
        return "";
    }

    /**
     * json string -> map 변환
     *
     * @param json
     * @return
     */
    public static Map<String, Object> jsonToMap(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String, Object>>() {
            };

            return objectMapper.readValue(json, typeReference);
        } catch (Exception e) {
            CustomLokiLog.errorLog(e);
        }
        return null;
    }

    /**
     * 컨트롤러의 응답 메세지를 초기화 합니다.
     *
     * @param resultData     서비스 메소드의 최종 응답데이터
     * @param defaultMessage 기본 응답 메세지
     * @return
     */
    public static String getReturnMessage(Object resultData, String defaultMessage) {
        if (resultData instanceof Map) {
            return ((Map<String, Object>) resultData).getOrDefault("message", defaultMessage).toString();
        }
        return defaultMessage;
    }

    public static Map<String, Object> getUserConnectionInfo(HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");
        String ip = getClientIp(request);
        if (StringUtils.isEmpty(ip)) {
            ip = "0.0.0.0";
        }
        String os = detectOS(userAgent);
        if (StringUtils.isEmpty(os)) {
            os = "Empty";
        }
        String browser = detectBrowser(userAgent);
        if (StringUtils.isEmpty(browser)) {
            browser = "Empty";
        }
        String device = detectDevice(userAgent);
        if (StringUtils.isEmpty(device)) {
            device = "Empty";
        }

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("ip", ip);
        resultMap.put("os", os);
        resultMap.put("browser", browser);
        resultMap.put("device", device);

        return resultMap;
    }

    public static String getClientIp(HttpServletRequest request) {

        if (request == null) {
            return "Unknown";
        }

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 여러 개 찍힐 수 있으니 첫 번째 값만 사용
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }

    public static String detectOS(String userAgent) {
        String result = "Unknown";

        if (StringUtils.isEmpty(userAgent)) {
            return result;
        }

        try {
            // Windows
            if (userAgent.contains("Windows NT 10.0")) result = "Windows 10";
            else if (userAgent.contains("Windows NT 6.3")) result = "Windows 8.1";
            else if (userAgent.contains("Windows NT 6.2")) result = "Windows 8";
            else if (userAgent.contains("Windows NT 6.1")) result = "Windows 7";
            else if (userAgent.contains("Windows NT 6.0")) result = "Windows Vista";
            else if (userAgent.contains("Windows NT 5.1") || userAgent.contains("Windows XP")) result = "Windows XP";

            // Mac
            else if (userAgent.contains("Mac OS X")) {
                int start = userAgent.indexOf("Mac OS X");
                int end = userAgent.indexOf(")", start);
                if (start != -1 && (end == -1 || end <= start)) end = userAgent.length();
                if (start != -1) {
                    String version = userAgent.substring(start, end).replace("_", ".");
                    version = version.replace("Mac OS X ", "").trim();
                    if (!version.isEmpty()) result = "Mac OS " + version;
                    else result = "Mac OS (Unknown version)";
                }
            }

            // Android
            else if (userAgent.contains("Android")) {
                int start = userAgent.indexOf("Android");
                int end = userAgent.indexOf(";", start);
                if (start != -1 && (end == -1 || end <= start)) end = userAgent.length();
                if (start != -1) {
                    String version = userAgent.substring(start, end).trim();
                    if (!version.isEmpty()) result = version;
                    else result = "Android (Unknown version)";
                }
            }

            // iOS (iPhone / iPad)
            else if (userAgent.contains("iPhone") || userAgent.contains("iPad")) {
                String device = userAgent.contains("iPhone") ? "iPhone" : "iPad";
                int start = userAgent.indexOf("OS ");
                if (start != -1) {
                    int end = userAgent.indexOf(" ", start + 3);
                    if (end == -1) end = userAgent.indexOf(" like Mac OS X", start);
                    if (end == -1 || end <= start + 3) end = userAgent.length();
                    String version = userAgent.substring(start + 3, end).replace("_", ".");
                    if (!version.isEmpty()) result = "iOS " + version + " (" + device + ")";
                    else result = "iOS (Unknown version) (" + device + ")";
                } else {
                    result = "iOS (Unknown version) (" + device + ")";
                }
            }

            // Unix
            else if (userAgent.contains("X11")) result = "Unix";

        } catch (StringIndexOutOfBoundsException e) {
            log.error("detectOS substring index error!");
            // 안전하게 예외 처리
            result = "Unknown";
        } catch (NullPointerException e) {
            log.error("detectOS null error!");
            // 안전하게 예외 처리
            result = "Unknown";
        }

        return result;
    }

    public static String detectBrowser(String userAgent) {

        if (StringUtils.isEmpty(userAgent)) {
            return "Unknown";
        }

        if (userAgent.contains("Chrome") && !userAgent.contains("Edg")) {
            return extractVersion(userAgent, "Chrome/([\\d\\.]+)", "Chrome");
        } else if (userAgent.contains("Edg")) {
            return extractVersion(userAgent, "Edg/([\\d\\.]+)", "Edge");
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            return extractVersion(userAgent, "Version/([\\d\\.]+)", "Safari");
        } else if (userAgent.contains("Firefox")) {
            return extractVersion(userAgent, "Firefox/([\\d\\.]+)", "Firefox");
        } else if (userAgent.contains("MSIE")) {
            return extractVersion(userAgent, "MSIE ([\\d\\.]+)", "Internet Explorer");
        } else if (userAgent.contains("Trident")) { // IE 11 이상
            return extractVersion(userAgent, "rv:([\\d\\.]+)", "Internet Explorer");
        }

        return "Unknown";
    }

    private static String extractVersion(String userAgent, String regex, String browserName) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(userAgent);
        if (matcher.find()) {
            return browserName + " " + matcher.group(1); // 예: Chrome 139.0.0.0
        }
        return browserName;
    }

    public static String detectDevice(String userAgent) {

        if (StringUtils.isEmpty(userAgent)) {
            return "Unknown";
        }

        if (userAgent.contains("Mobile")) return "Mobile";
        else if (userAgent.contains("Tablet") || userAgent.contains("iPad")) return "Tablet";
        return "PC";
    }
}



