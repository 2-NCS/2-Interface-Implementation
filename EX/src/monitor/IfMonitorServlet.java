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
	// 함수 호출을 위한 멤버변수 선언
    private OutboxDAO  outboxDAO;
    private InboxDAO   inboxDAO;
    private AccountDAO accountDAO;

    @Override
    public void init() {// init을 통한 오브젝트 생성 후 멤버변수에 저장
        outboxDAO  = new OutboxDAO();
        inboxDAO   = new InboxDAO();
        accountDAO = new AccountDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	// TODO 엔드포인트 /monitor/list_로 get요청시 -> 
    	// GET /monitor/list 로그 생성
        System.out.println("GET /monitor/list");
        // req.setAttribute를 통해 req의 속성의 이름(outboxList, inboxList, accountList)에
    	// 내용물(outboxDAO.listAll(), inboxDAO.listAll(), accountDAO.listAccounts())을 set
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
