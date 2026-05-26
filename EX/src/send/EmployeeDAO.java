package send;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.Map;

import common.DBManager;
import common.JsonUtil;

/**
 * EmployeeDAO — employee + if_outbox 트랜잭션 통합 — ★ 정답
 *
 * <pre>
 * 능력단위요소 2 / 수행준거 2.2
 * 학습모듈 2-1 §2 (테이블 인터페이스), Transactional Outbox 패턴
 * </pre>
 */
public class EmployeeDAO {

    private static final String SQL_INSERT_EMP =
            "INSERT INTO employee (emp_id, emp_name, dept_cd, position, hire_dt, email) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_OUTBOX =
            "INSERT INTO if_outbox (if_id, tx_no, payload, status) VALUES (?, ?, ?, 'N')";

    private static final String IF_ID = "IF_HR_001";

    /**
     * 사원 등록 + outbox 한 트랜잭션.
     * @return 생성된 outbox_id
     */
    public long createEmployeeWithOutbox(Employee emp) throws SQLException {
        Connection conn = null;
        try {
            conn = DBManager.getHrmConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT_EMP)) {
                pstmt.setString(1, emp.getEmpId());
                pstmt.setString(2, emp.getEmpName());
                pstmt.setString(3, emp.getDeptCd());
                pstmt.setString(4, emp.getPosition());
                pstmt.setString(5, emp.getHireDt());
                pstmt.setString(6, emp.getEmail());
                pstmt.executeUpdate();
            }

            String payload = toPayloadJson(emp);
            long outboxId;
            try (PreparedStatement pstmt = conn.prepareStatement(
                    SQL_INSERT_OUTBOX, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, IF_ID);
                pstmt.setString(2, emp.getEmpId());   // 멱등키
                pstmt.setString(3, payload);
                pstmt.executeUpdate();
                try (ResultSet keys = pstmt.getGeneratedKeys()) {
                    outboxId = keys.next() ? keys.getLong(1) : -1L;
                }
            }

            conn.commit();
            return outboxId;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ignored) {}
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                DBManager.close(conn);
            }
        }
    }

    /** payload JSON (인터페이스설계서 §3.3 형식) */
    private String toPayloadJson(Employee e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("empId",    e.getEmpId());
        m.put("empName",  e.getEmpName());
        m.put("deptCd",   e.getDeptCd());
        m.put("position", e.getPosition());
        m.put("hireDt",   e.getHireDt());
        m.put("email",    e.getEmail());
        return JsonUtil.toJson(m);
    }
}
