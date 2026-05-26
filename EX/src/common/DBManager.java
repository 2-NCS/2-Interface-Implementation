package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * [시험 학생용] DBManager — HrmDB + GroupwareDB — ☆ TODO
 *
 * <p>제공되는 properties:</p>
 * <ul>
 *   <li>hrm.url / hrm.user / hrm.password</li>
 *   <li>gw.url / gw.user / gw.password</li>
 *   <li>batch.fetch.size / batch.max.retry</li>
 *   <li>account.init.password.suffix</li>
 * </ul>
 *
 * <p>정답: ../EX_답/src/common/DBManager.java</p>
 */
public class DBManager {
    private DBManager() {}

    /** 송신측 HrmDB Connection */
    public static Connection getHrmConnection() throws SQLException {
        String driver   = "com.mysql.cj.jdbc.Driver";
        String url      = "jdbc:mysql://localhost:3306/HrmDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
        String user     = "ifuserx";
        String password = "ifx1234!";

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("DBManager.getHrmConnection Class.forName..." + e.getCause());
        }
        return DriverManager.getConnection(url, user, password);
    }

    /** 수신측 GroupwareDB Connection */
    public static Connection getGroupwareConnection() throws SQLException {
        String driver   = "com.mysql.cj.jdbc.Driver";
        String url      = "jdbc:mysql://localhost:3306/GroupwareDB?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Seoul&characterEncoding=UTF-8";
        String user     = "ifuserx";
        String password = "ifx1234!";

        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("DBManager.getGroupwareConnection Class.forName..." + e.getCause());
        }
        return DriverManager.getConnection(url, user, password);
    }

    public static int    getFetchSize()           { return 100; }
    public static int    getMaxRetry()            { return 3;   }
    /** 계정 초기 비밀번호 suffix — empId + 이 값을 SHA-256 해시한 게 pwd_hash */
    public static String getInitPasswordSuffix() { return "1234"; }

    /** AutoCloseable 자원 여러 개 안전 close */
    public static void close(AutoCloseable... closeables) {
        for (AutoCloseable c : closeables) {
            if (c != null) {
                try { c.close(); }
                catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
}
