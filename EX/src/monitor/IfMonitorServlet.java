package monitor;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import recv.AccountDAO;
import recv.InboxDAO;
import send.OutboxDAO;

@WebServlet("/monitor/list_")
public class IfMonitorServlet extends HttpServlet {

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
            throws ServletException, IOException {
        System.out.println("GET /monitor/list");

        try {
            req.setAttribute("outboxList",  outboxDAO.listAll());
            req.setAttribute("inboxList",   inboxDAO.listAll());
            req.setAttribute("accountList", accountDAO.listAccounts());
        } catch (SQLException e) {
            e.printStackTrace();
            req.setAttribute("error", "조회 오류: " + e.getMessage());
        }
        req.getRequestDispatcher("/monitor/list.jsp").forward(req, resp);
    }
}
