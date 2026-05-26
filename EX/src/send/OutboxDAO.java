package send;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import common.DBManager;

/**
 * if_outbox DAO — ★ 정답
 *
 * <pre>
 * 능력단위요소 3 / 수행준거 3.1 (검증도구)
 * </pre>
 */
public class OutboxDAO {

    private static final String SQL_FETCH_PENDING =
            "SELECT outbox_id, if_id, tx_no, payload, retry_cnt " +
            "FROM if_outbox WHERE status='N' AND retry_cnt < ? ORDER BY outbox_id ASC LIMIT ?";

    private static final String SQL_MARK_SUCCESS =
            "UPDATE if_outbox SET status='S', proc_dt=CURRENT_TIMESTAMP, err_msg=NULL WHERE outbox_id=?";

    private static final String SQL_MARK_FAIL =
            "UPDATE if_outbox SET status='F', retry_cnt=retry_cnt+1, proc_dt=CURRENT_TIMESTAMP, err_msg=? WHERE outbox_id=?";

    private static final String SQL_REOPEN =
            "UPDATE if_outbox SET status='N', err_msg=NULL WHERE outbox_id=? AND status='F'";

    private static final String SQL_LIST_ALL =
            "SELECT outbox_id, if_id, tx_no, status, retry_cnt, reg_dt, proc_dt, err_msg " +
            "FROM if_outbox ORDER BY outbox_id DESC LIMIT 100";

    public List<Outbox> fetchPending(int maxRetry, int limit) throws SQLException {
        List<Outbox> out = new ArrayList<>();
        try (Connection conn = DBManager.getHrmConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_FETCH_PENDING)) {
            pstmt.setInt(1, maxRetry);
            pstmt.setInt(2, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Outbox o = new Outbox();
                    o.outboxId = rs.getLong("outbox_id");
                    o.ifId     = rs.getString("if_id");
                    o.txNo     = rs.getString("tx_no");
                    o.payload  = rs.getString("payload");
                    o.retryCnt = rs.getInt("retry_cnt");
                    out.add(o);
                }
            }
        }
        return out;
    }

    public int markSuccess(long outboxId) throws SQLException {
        try (Connection conn = DBManager.getHrmConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_MARK_SUCCESS)) {
            pstmt.setLong(1, outboxId);
            return pstmt.executeUpdate();
        }
    }

    public int markFail(long outboxId, String errMsg) throws SQLException {
        try (Connection conn = DBManager.getHrmConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_MARK_FAIL)) {
            pstmt.setString(1, errMsg);
            pstmt.setLong  (2, outboxId);
            return pstmt.executeUpdate();
        }
    }

    public int reopen(long outboxId) throws SQLException {
        try (Connection conn = DBManager.getHrmConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_REOPEN)) {
            pstmt.setLong(1, outboxId);
            return pstmt.executeUpdate();
        }
    }

    public List<Outbox> listAll() throws SQLException {
        List<Outbox> out = new ArrayList<>();
        try (Connection conn = DBManager.getHrmConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_LIST_ALL);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Outbox o = new Outbox();
                o.outboxId = rs.getLong("outbox_id");
                o.ifId     = rs.getString("if_id");
                o.txNo     = rs.getString("tx_no");
                o.status   = rs.getString("status");
                o.retryCnt = rs.getInt("retry_cnt");
                o.regDt    = rs.getTimestamp("reg_dt");
                o.procDt   = rs.getTimestamp("proc_dt");
                o.errMsg   = rs.getString("err_msg");
                out.add(o);
            }
        }
        return out;
    }

    /** VO — JSTL EL 호환 */
    public static class Outbox {
        public long      outboxId;
        public String    ifId;
        public String    txNo;
        public String    payload;
        public String    status;
        public int       retryCnt;
        public Timestamp regDt;
        public Timestamp procDt;
        public String    errMsg;

        public long      getOutboxId() { return outboxId; }
        public String    getIfId()     { return ifId; }
        public String    getTxNo()     { return txNo; }
        public String    getPayload()  { return payload; }
        public String    getStatus()   { return status; }
        public int       getRetryCnt() { return retryCnt; }
        public Timestamp getRegDt()    { return regDt; }
        public Timestamp getProcDt()   { return procDt; }
        public String    getErrMsg()   { return errMsg; }
    }
}
