package recv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import common.DBManager;

/**
 * if_inbox DAO — tx_no UNIQUE 멱등 보장
 */
public class InboxDAO {

    private static final String SQL_INSERT =
            "INSERT INTO if_inbox (if_id, tx_no, payload, status, proc_ms, err_msg) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_LIST =
            "SELECT inbox_id, if_id, tx_no, status, proc_ms, recv_dt, err_msg " +
            "FROM if_inbox ORDER BY inbox_id DESC LIMIT 100";

    public void insertInTx(Connection conn, String ifId, String txNo, String payload,
                           String status, int procMs, String errMsg) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(SQL_INSERT)) {
            pstmt.setString(1, ifId);
            pstmt.setString(2, txNo);
            pstmt.setString(3, payload);
            pstmt.setString(4, status);
            pstmt.setInt   (5, procMs);
            pstmt.setString(6, errMsg);
            pstmt.executeUpdate();
        }
    }

    public List<Inbox> listAll() throws SQLException {
        List<Inbox> list = new ArrayList<>();
        try (Connection conn = DBManager.getGroupwareConnection();
             PreparedStatement pstmt = conn.prepareStatement(SQL_LIST);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                Inbox in = new Inbox();
                in.inboxId = rs.getLong("inbox_id");
                in.ifId    = rs.getString("if_id");
                in.txNo    = rs.getString("tx_no");
                in.status  = rs.getString("status");
                in.procMs  = rs.getInt("proc_ms");
                in.recvDt  = rs.getTimestamp("recv_dt");
                in.errMsg  = rs.getString("err_msg");
                list.add(in);
            }
        }
        return list;
    }

    public static class Inbox {
        public long      inboxId;
        public String    ifId;
        public String    txNo;
        public String    status;
        public int       procMs;
        public Timestamp recvDt;
        public String    errMsg;

        public long      getInboxId() { return inboxId; }
        public String    getIfId()    { return ifId; }
        public String    getTxNo()    { return txNo; }
        public String    getStatus()  { return status; }
        public int       getProcMs()  { return procMs; }
        public Timestamp getRecvDt()  { return recvDt; }
        public String    getErrMsg()  { return errMsg; }
    }
}
