package common;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 외부 라이브러리(Jackson, Gson) 없이 사용할 수 있는 미니멀 JSON 유틸 — ★ 정답
 *
 * <pre>
 * 학습모듈 2-1 §2 (JSON 데이터 포맷 — 표 2-2, 표 2-3)
 * 능력단위요소 2 / 수행준거 2.1 (정형화된 인터페이스 구현)
 * </pre>
 *
 * <p>※ 학습 목적상 외부 라이브러리 없이 동작하도록 구현됨.
 *    운영에서는 Jackson/Gson 등 검증된 라이브러리 사용 권장.</p>
 *
 * <h3>지원 기능</h3>
 * <ul>
 *   <li>{@link #toJson(Map)} — Map 을 JSON 문자열로 직렬화</li>
 *   <li>{@link #parse(String)} — JSON 문자열을 Map 으로 역직렬화</li>
 * </ul>
 *
 * <p>지원하는 자료형: String, Integer/Long, Boolean, null<br>
 *    배열·중첩객체는 본 학습 모듈에서 다루지 않는다.</p>
 */
public class JsonUtil {

    /** Map → JSON 문자열 */
    public static String toJson(Map<String, Object> map) {
        if (map == null) return "null";
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> e : map.entrySet()) {
            if (!first) sb.append(",");
            sb.append('"').append(escape(e.getKey())).append("\":");
            Object v = e.getValue();
            if (v == null)                     sb.append("null");
            else if (v instanceof Number)      sb.append(v);
            else if (v instanceof Boolean)     sb.append(v);
            else                               sb.append('"').append(escape(v.toString())).append('"');
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /** JSON 문자열 → Map (단순 한 단계 객체만 지원) */
    public static Map<String, Object> parse(String json) {
        Map<String, Object> map = new LinkedHashMap<>();
        if (json == null) return map;
        String s = json.trim();
        if (!s.startsWith("{") || !s.endsWith("}")) {
            throw new IllegalArgumentException("JSON 형식 아님: " + s);
        }
        s = s.substring(1, s.length() - 1).trim();
        if (s.isEmpty()) return map;

        int i = 0, n = s.length();
        while (i < n) {
            // key
            i = skipWs(s, i);
            if (s.charAt(i) != '"') throw new IllegalArgumentException("키는 따옴표로 시작: pos=" + i);
            int keyEnd = s.indexOf('"', i + 1);
            String key = unescape(s.substring(i + 1, keyEnd));
            i = keyEnd + 1;
            i = skipWs(s, i);
            if (s.charAt(i) != ':') throw new IllegalArgumentException("':' 필요: pos=" + i);
            i++;
            i = skipWs(s, i);

            // value
            Object value;
            char c = s.charAt(i);
            if (c == '"') {
                int end = i + 1;
                while (end < n && s.charAt(end) != '"') {
                    if (s.charAt(end) == '\\') end++;     // escape skip
                    end++;
                }
                value = unescape(s.substring(i + 1, end));
                i = end + 1;
            } else if (c == 't' || c == 'f') {
                boolean tv = s.startsWith("true", i);
                value = tv;
                i += tv ? 4 : 5;
            } else if (c == 'n') {
                value = null;
                i += 4;
            } else {                                       // number
                int end = i;
                while (end < n && "0123456789.-+eE".indexOf(s.charAt(end)) >= 0) end++;
                String num = s.substring(i, end);
                value = num.contains(".") ? (Object) Double.parseDouble(num)
                                          : (Object) Long.parseLong(num);
                i = end;
            }
            map.put(key, value);
            i = skipWs(s, i);
            if (i < n && s.charAt(i) == ',') i++;
        }
        return map;
    }

    /* helpers */
    private static int skipWs(String s, int i) {
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;
        return i;
    }
    private static String escape(String v) {
        return v.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
    private static String unescape(String v) {
        return v.replace("\\\"", "\"").replace("\\n", "\n").replace("\\r", "\r")
                .replace("\\t", "\t").replace("\\\\", "\\");
    }

    /** 응답 Map 의 키를 안전하게 String 으로 꺼내기 */
    public static String getString(Map<String, Object> m, String key, String def) {
        Object o = m.get(key);
        return o == null ? def : o.toString();
    }

    public static int getInt(Map<String, Object> m, String key, int def) {
        Object o = m.get(key);
        if (o == null) return def;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(o.toString()); }
        catch (NumberFormatException e) { return def; }
    }
}
