package recv;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import common.DBManager;
import common.HashUtil;

/**
 * AccountDAO — ★ 정답
 *
 * <pre>
 * 능력단위요소 2 / 수행준거 2.2, 2.4 (보안 — 비밀번호 SHA-256 해시)
 * </pre>
 */
public class AccountDAO {

    private static final String SQL_CHECK_DEPT =
            "SELECT 1 FROM dept WHERE dept_cd = ?";

    private static final String SQL_INSERT_ACCOUNT =
            "INSERT INTO account (account_id, pwd_hash, dept_cd, status) VALUES (?, ?, ?, 'A')";

    private static final String SQL_INSERT_HIST =
            "INSERT INTO account_history (account_id, action_cd, tx_no) VALUES (?, '01', ?)";

    private static final String SQL_LIST =
            "SELECT account_id, dept_cd, status, create_dt FROM account ORDER BY account_id DESC LIMIT 100";

    /**
     * 계정 생성 (외부 트랜잭션 안에서 호출).
     * @throws SQLException 부서코드 없음 → "DEPT_NOT_FOUND:" + deptCd
     */
    public void createAccountInTx(Connection conn, String empId, String deptCd, String txNo)
            throws SQLException {

        // 부서 존재 확인
    	// dept 테이블에서 deptCd 확인, 없으면 DEPT_NOT_FOUND 에러
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_CHECK_DEPT)) {
            pstmt.setString(1, deptCd);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) throw new SQLException("DEPT_NOT_FOUND:" + deptCd);
            }
        }
        conn.setAutoCommit(false);
        // empId + "1234" 로 초기 비밀번호 생성 후 SHA-256 암호화
        // 비밀번호 해시 = SHA-256(empId + 초기비번 suffix)
        String pwdPlain = empId + DBManager.getInitPasswordSuffix();
        String pwdHash  = HashUtil.sha256(pwdPlain);
        
        // account 테이블에 empId, pwdHash, deptCd INSERT
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_ACCOUNT)) {
            pstmt.setString(1, empId);
            pstmt.setString(2, pwdHash);
            pstmt.setString(3, deptCd); 
            pstmt.executeUpdate();
        }
        // account_history 테이블에 empId, txNo INSERT
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_HIST)) {
            pstmt.setString(1, empId);
            pstmt.setString(2, txNo);
            pstmt.executeUpdate();
        }
        // conn.commit() 으로 저장
        conn.commit();
    }

    /** 모니터링 — 계정 목록 (pwd_hash 노출 X) */
    public List<Map<String, Object>> listAccounts() throws SQLException {
        List<Map<String, Object>> out = new ArrayList<>();
        // DBManager로 DB 연결 후 SQL_LIST 실행
        try (Connection conn = DBManager.getGroupwareConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_LIST);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("accountId", rs.getString("account_id"));
                row.put("deptCd",    rs.getString("dept_cd"));
                row.put("status",    rs.getString("status"));
                row.put("createDt",  rs.getTimestamp("create_dt"));
                out.add(row);
            }
        }
        return out;
    }
}
