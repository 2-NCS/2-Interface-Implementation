package send;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/send/emp_")
public class EmpRegistServlet extends HttpServlet {

    private static final Set<String> ALLOWED_POSITIONS =
            Set.of("사원", "대리", "과장", "차장", "부장");

    private EmployeeDAO empDAO;

    @Override
    public void init() {
        empDAO = new EmployeeDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        System.out.println("POST /send/emp");

        String empName  = trim(req.getParameter("empName"));
        String deptCd   = trim(req.getParameter("deptCd"));
        String position = trim(req.getParameter("position"));
        String email    = trim(req.getParameter("email"));

        if (empName.isEmpty() || deptCd.isEmpty() || position.isEmpty()) {
            forward(req, resp, "FAIL", "E10: 필수값(empName/deptCd/position) 누락", null);
            return;
        }
        if (empName.length() > 50) {
            forward(req, resp, "FAIL", "E10: empName 길이 초과 (50)", null);
            return;
        }
        if (!deptCd.matches("D\\d{3}")) {
            forward(req, resp, "FAIL", "E10: deptCd 형식 오류 (Dnnn)", null);
            return;
        }
        if (!ALLOWED_POSITIONS.contains(position)) {
            forward(req, resp, "FAIL", "E31: 미지원 직급 (" + position + ")", null);
            return;
        }

        String hireDt = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String empId  = "E" + hireDt + String.format("%03d", new Random().nextInt(1000));

        Employee emp = new Employee(empId, empName, deptCd, position, hireDt, email);

        try {
            long outboxId = empDAO.createEmployeeWithOutbox(emp);
            String msg = String.format(
                "입사 등록 완료. empId=%s, outboxId=%d (계정은 배치 처리 후 발급됩니다)",
                empId, outboxId);
            forward(req, resp, "OK", msg, empId);
        } catch (SQLException e) {
            e.printStackTrace();
            forward(req, resp, "FAIL", "E90 DB 오류: " + e.getMessage(), null);
        }
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private void forward(HttpServletRequest req, HttpServletResponse resp,
                         String status, String message, String empId)
            throws ServletException, IOException {
        req.setAttribute("status",  status);
        req.setAttribute("message", message);
        req.setAttribute("empId",   empId);
        req.getRequestDispatcher("/send/result.jsp").forward(req, resp);
    }
}
