package monitor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import send.OutboxDAO;

@WebServlet("/monitor/retry_")
public class IfRetryServlet extends HttpServlet {

    private OutboxDAO outboxDAO;

    @Override
    public void init() {
        outboxDAO = new OutboxDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("POST /monitor/retry");

        String idStr = req.getParameter("outboxId");
        if (idStr == null) { resp.sendError(400, "outboxId 필요"); return; }

        try {
            outboxDAO.reopen(Long.parseLong(idStr));
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(500, "재시도 오류: " + e.getMessage());
            return;
        }
        resp.sendRedirect(req.getContextPath() + "/monitor/list");
    }
}
