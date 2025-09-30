package com.visang.aidt.lms.api.utility.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.UncategorizedSQLException;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 초경량 예외 요약 유틸 (Streams/regex/crypto 해시 미사용)
 * - 메시지/스택 프레임 길이 제한
 * - 앱 패키지 첫 프레임 탐색
 * - 라인번호 제외한 롤링 해시 기반 fingerprint
 * - SQL 예외 발생 시 실행 SQL 추출 (Spring/JDBC 예외 대응)
 *
 * 사용:
 *   String brief = ExcBriefLiteUtil.brief(e, "com.visang");
 *   log.error("EXC {}", brief);
 */
@Slf4j
public final class ExcBriefLiteUtil {
    private ExcBriefLiteUtil() {}

    // 튜닝 포인트
    private static final int MAX_MSG_LEN      = 160;
    private static final int MAX_FRAMES_TOTAL = 8;   // 전체 원인 체인에서 합산
    private static final int MAX_FP_FRAMES    = 8;   // fingerprint에 사용할 총 프레임 수
    private static final int MAX_SQL_LEN      = 200;

    // SQL 패턴 (SELECT / UPDATE / INSERT / DELETE / MERGE / WITH)
    private static final Pattern SQL_PATTERN = Pattern.compile(
        "(?is)\\b(select|update|insert|delete|merge|with)\\b.+?(?=;|$)"
    );

    /** 예외 한 줄 요약 생성 */
    public static String brief(Throwable t, String appPkgPrefix) {
        if (t == null) return "NoException";

        StringBuilder sb = new StringBuilder(256);
        sb.append("msg='").append(trunc(firstLine(t.getMessage()), MAX_MSG_LEN)).append('\'');

        // at: 우리 패키지 첫 프레임(없으면 0번째)
        StackTraceElement frame = firstAppFrame(t, appPkgPrefix);
        if (frame == null) {
            StackTraceElement[] st0 = t.getStackTrace();
            if (st0 != null && st0.length > 0) frame = st0[0];
        }
        if (frame != null) {
            sb.append(" | at ")
              .append(frame.getClassName()).append('#').append(frame.getMethodName())
              .append('(').append(frame.getFileName()).append(':').append(frame.getLineNumber()).append(')');
        } else {
            sb.append(" | at n/a");
        }

        // 간단한 원인 체인 타입 요약(최대 3개)
        sb.append(" | chain=").append(chainTypes(t, 3));

        // SQL 문 추출
        String sql = extractSql(t);
        if (sql != null) {
            sb.append(" | sql='").append(trunc(firstLine(sql), MAX_SQL_LEN)).append("'");
        }

        // fingerprint
        sb.append(" | fp=").append(fingerprintHex(t));

        return sb.toString();
    }

    /** fingerprint만 필요할 때 */
    public static String fingerprintHex(Throwable t) {
        long h = 1125899906842597L; // FNV-ish seed
        int used = 0;

        for (Throwable cur = t; cur != null && used < MAX_FP_FRAMES; cur = nextCause(cur)) {
            StackTraceElement[] st = cur.getStackTrace();
            if (st == null) continue;
            int lim = Math.min(st.length, MAX_FP_FRAMES - used);
            for (int i = 0; i < lim; i++) {
                StackTraceElement e = st[i];
                h = 31 * h + strHash(e.getClassName());
                h = 31 * h + strHash(e.getMethodName());
            }
            used += lim;
        }
        return toHex(h);
    }

    // ===== 내부 헬퍼들 =====

    private static String simpleName(Throwable t) {
        String n = (t == null) ? null : t.getClass().getSimpleName();
        if (n == null || n.isEmpty()) {
            String fqcn = (t == null) ? null : t.getClass().getName();
            if (fqcn == null) return "Throwable";
            int idx = fqcn.lastIndexOf('.');
            return (idx >= 0 && idx + 1 < fqcn.length()) ? fqcn.substring(idx + 1) : fqcn;
        }
        return n;
    }

    private static Throwable nextCause(Throwable t) {
        Throwable c = (t == null) ? null : t.getCause();
        return (c == t) ? null : c;
    }

    private static String chainTypes(Throwable t, int max) {
        if (t == null || max <= 0) return "-";
        StringBuilder sb = new StringBuilder(64);
        int n = 0;
        for (Throwable cur = t; cur != null && n < max; cur = nextCause(cur), n++) {
            if (n > 0) sb.append(" -> ");
            sb.append(simpleName(cur));
        }
        return (sb.length() == 0) ? "-" : sb.toString();
    }

    private static StackTraceElement firstAppFrame(Throwable t, String prefix) {
        if (t == null || prefix == null) return null;
        int taken = 0;
        for (Throwable cur = t; cur != null && taken < MAX_FRAMES_TOTAL; cur = nextCause(cur)) {
            StackTraceElement[] st = cur.getStackTrace();
            if (st == null) continue;
            int lim = Math.min(st.length, MAX_FRAMES_TOTAL - taken);
            for (int i = 0; i < lim; i++) {
                StackTraceElement e = st[i];
                if (e.getClassName() != null && e.getClassName().startsWith(prefix)) {
                    return e;
                }
            }
            taken += lim;
        }
        return null;
    }

    private static String firstLine(String s) {
        if (s == null) return null;
        int i = 0, n = s.length();
        while (i < n) {
            char c = s.charAt(i);
            if (c == '\n' || c == '\r') break;
            i++;
        }
        return (i == n) ? s : s.substring(0, i);
    }

    private static String trunc(String s, int max) {
        if (s == null) return null;
        if (s.length() <= max) return s;
        return s.substring(0, max) + "…";
    }

    private static int strHash(String s) {
        if (s == null) return 0;
        int h = 0;
        for (int i = 0, n = s.length(); i < n; i++) {
            h = 31 * h + s.charAt(i);
        }
        return h;
    }

    private static String toHex(long v) {
        String hex = Long.toHexString(v);
        if (hex.length() > 10) return hex.substring(hex.length() - 10);
        if (hex.length() < 10) {
            StringBuilder sb = new StringBuilder(10);
            for (int i = hex.length(); i < 10; i++) sb.append('0');
            sb.append(hex);
            return sb.toString();
        }
        return hex;
    }

    // ===== SQL 관련 =====

    private static String extractSql(Throwable t) {
        // 1) Spring JDBC 예외 우선
        for (Throwable cur = t; cur != null; cur = nextCause(cur)) {
            String springSql = getSpringSqlIfPresent(cur);
            if (springSql != null && !springSql.isEmpty()) {
                return trunc(sanitizeSql(firstLine(springSql)), MAX_SQL_LEN);
            }
        }
        // 2) SQLException 계열 메시지/nextException에서 패턴 추출
        for (Throwable cur = t; cur != null; cur = nextCause(cur)) {
            if (cur instanceof SQLException) {
                String fromMsg = findSqlInMessage(cur.getMessage());
                if (fromMsg != null) return trunc(sanitizeSql(firstLine(fromMsg)), MAX_SQL_LEN);

                SQLException se = (SQLException) cur;
                for (SQLException nx = se.getNextException(); nx != null; nx = nx.getNextException()) {
                    String fromNext = findSqlInMessage(nx.getMessage());
                    if (fromNext != null) return trunc(sanitizeSql(firstLine(fromNext)), MAX_SQL_LEN);
                }
            }
        }
        return null;
    }

    private static String getSpringSqlIfPresent(Throwable cur) {
        if (cur instanceof BadSqlGrammarException) {
            return ((BadSqlGrammarException) cur).getSql();
        }
        if (cur instanceof UncategorizedSQLException) {
            return ((UncategorizedSQLException) cur).getSql();
        }
        // 리플렉션으로 getSql() 메서드 있는지 탐색
        try {
            Method m = cur.getClass().getMethod("getSql");
            if (m.getReturnType() == String.class) {
                Object v = m.invoke(cur);
                return (v instanceof String) ? (String) v : null;
            }
        } catch (NoSuchMethodException e) {
            log.debug("getSql() NoSuchMethodException failed on {}: {}", cur.getClass().getName(), e.getMessage());
        } catch (ReflectiveOperationException e) {
            // 리플렉션 실패: 요약 항목만 생략하고 계속 진행
            log.debug("getSql() reflection failed on {}: {}", cur.getClass().getName(), e.getMessage());
        }
        return null;
    }

    private static String findSqlInMessage(String msg) {
        if (msg == null || msg.isEmpty()) return null;
        Matcher m = SQL_PATTERN.matcher(msg);
        if (m.find()) {
            return m.group().trim();
        }
        return null;
    }

    /** 간단한 SQL 민감값 마스킹 */
    private static String sanitizeSql(String sql) {
        if (sql == null) return null;
        sql = sql.replaceAll("(?s)'(?:''|[^'])*'", "?");   // '...' → ?
        sql = sql.replaceAll("(?s)\"(?:\"\"|[^\"])*\"", "?"); // "..." → ?
        sql = sql.replaceAll("\\b\\d{3,}\\b", "?");        // 긴 숫자 → ?
        return sql;
    }
}
