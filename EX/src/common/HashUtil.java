package common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * [시험 학생용] SHA-256 해시 — ☆ TODO
 *
 * <pre>
 * 능력단위요소 2 / 수행준거 2.4 (보안)
 * 학습모듈 2-3 §2 표 2-14 SHA-256
 * </pre>
 *
 * <p><b>sha256(plain)</b>:</p>
 * <ol>
 *   <li>MessageDigest.getInstance("SHA-256")</li>
 *   <li>md.digest(plain.getBytes(StandardCharsets.UTF_8))</li>
 *   <li>byte[] → hex String (StringBuilder + String.format("%02x", b))</li>
 *   <li>리턴 길이는 64자</li>
 * </ol>
 *
 * <p>정답: ../EX_답/src/common/HashUtil.java</p>
 */
public class HashUtil {

    /** SHA-256 hex string */
    public static String sha256(String plain) {
        if (plain == null) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(64);
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
