package recv;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.DBManager;
import common.JsonUtil;
import send.OutboxDAO;
import send.OutboxDAO.Outbox;

@WebServlet("/recv/batch_")
public class AccountBatchServlet extends HttpServlet {

    private OutboxDAO  outboxDAO;
    private InboxDAO   inboxDAO;
    private AccountDAO accountDAO;

    @Override
    public void init() {
        outboxDAO  = new OutboxDAO();
        inboxDAO   = new InboxDAO();
        accountDAO = new AccountDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { process(req, resp); }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException { process(req, resp); }

    private void process(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("/recv/batch 배치 시작");

        int totalRead = 0, success = 0, fail = 0, skip = 0;
        StringBuilder log = new StringBuilder();

        try {
            List<Outbox> pendings = outboxDAO.fetchPending(DBManager.getMaxRetry(), DBManager.getFetchSize());
            totalRead = pendings.size();

            for (Outbox o : pendings) {
                String result = processOne(o);
                log.append(String.format("[outboxId=%d, tx=%s] %s%n", o.outboxId, o.txNo, result));
                if      (result.startsWith("OK"))   success++;
                else if (result.startsWith("SKIP")) skip++;
                else                                fail++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.append("FATAL: ").append(e.getMessage()).append('\n');
        }

        req.setAttribute("totalRead", totalRead);
        req.setAttribute("success",   success);
        req.setAttribute("fail",      fail);
        req.setAttribute("skip",      skip);
        req.setAttribute("log",       log.toString());
        req.getRequestDispatcher("/recv/batchResult.jsp").forward(req, resp);
    }

    private String processOne(Outbox o) {
        long startMs = System.currentTimeMillis();
        Connection conn = null;
        try {
            Map<String, Object> body = JsonUtil.parse(o.payload);
            String empId  = JsonUtil.getString(body, "empId",  "");
            String deptCd = JsonUtil.getString(body, "deptCd", "");

            if (empId.isEmpty() || deptCd.isEmpty()) throw new SQLException("REQUIRED_MISSING");
            if (!empId.matches("E\\d{8}\\d{3}"))     throw new SQLException("EMP_ID_FORMAT");

            conn = DBManager.getGroupwareConnection();
            conn.setAutoCommit(false);

            try {
                inboxDAO.insertInTx(conn, o.ifId, o.txNo, o.payload, "S", 0, null);
            } catch (SQLException ex) {
                if ("23000".equals(ex.getSQLState())) {
                    conn.rollback();
                    outboxDAO.markSuccess(o.outboxId);
                    return "SKIP: 멱등 중복 (이미 발급된 계정)";
                }
                throw ex;
            }

            accountDAO.createAccountInTx(conn, empId, deptCd, o.txNo);

            conn.commit();
            outboxDAO.markSuccess(o.outboxId);
            return String.format("OK (account=%s, %dms)", empId, System.currentTimeMillis() - startMs);

        } catch (SQLException e) {
            String msg = e.getMessage();
            String errCode;
            if      (msg != null && msg.startsWith("DEPT_NOT_FOUND")) errCode = "E30";
            else if ("REQUIRED_MISSING".equals(msg))                  errCode = "E10";
            else if ("EMP_ID_FORMAT".equals(msg))                     errCode = "E32";
            else                                                      errCode = "E90";

            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}

            try (Connection c2 = DBManager.getGroupwareConnection()) {
                c2.setAutoCommit(true);
                try {
                    inboxDAO.insertInTx(c2, o.ifId, o.txNo, o.payload, "F",
                            (int)(System.currentTimeMillis() - startMs),
                            errCode + ": " + msg);
                } catch (SQLException ignored) { }
            } catch (SQLException ignored) {}

            try { outboxDAO.markFail(o.outboxId, errCode + ": " + msg); }
            catch (SQLException ignored) {}
            return "FAIL " + errCode + ": " + msg;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            try { outboxDAO.markFail(o.outboxId, "E99: " + e.getMessage()); }
            catch (SQLException ignored) {}
            return "FAIL E99: " + e.getMessage();

        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException ignored) {}
                DBManager.close(conn);
            }
        }
    }
}
