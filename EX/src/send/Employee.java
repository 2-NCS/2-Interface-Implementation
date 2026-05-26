package send;

/**
 * [시험 학생용] Employee VO — ☆ TODO
 *
 * <p>필드 6개 + 기본/전체 생성자 + getter/setter 12개</p>
 * <ul>
 *   <li>empId  : String (VARCHAR(12) E + YYYYMMDD + 3자리)</li>
 *   <li>empName: String (VARCHAR(50))</li>
 *   <li>deptCd : String (CHAR(4))</li>
 *   <li>position: String (VARCHAR(20))</li>
 *   <li>hireDt : String (CHAR(8) YYYYMMDD)</li>
 *   <li>email  : String</li>
 * </ul>
 *
 * <p>정답: ../EX_답/src/send/Employee.java</p>
 */
public class Employee {
	// private 멤버 변수 필드 6개 선언
    private String empId;        // VARCHAR(12) E + YYYYMMDD + 3자리
    private String empName;      // VARCHAR(50) UTF-8
    private String deptCd;       // CHAR(4)
    private String position;     // VARCHAR(20)
    private String hireDt;       // CHAR(8) YYYYMMDD
    private String email;        // VARCHAR(100)

    public Employee() {}
    // 멤버 필드 6개 선언 및 전체 생성자 초기화
    public Employee(String empId, String empName, String deptCd,
                    String position, String hireDt, String email) {
        this.empId    = empId;
        this.empName  = empName;
        this.deptCd   = deptCd;
        this.position = position;
        this.hireDt   = hireDt;
        this.email    = email;
    }
    // Getter / Setter 메소드 내부 리턴 및 대입
    public String getEmpId()             { return empId; }
    public void   setEmpId(String v)     { this.empId = v; }
    public String getEmpName()           { return empName; }
    public void   setEmpName(String v)   { this.empName = v; }
    public String getDeptCd()            { return deptCd; }
    public void   setDeptCd(String v)    { this.deptCd = v; }
    public String getPosition()          { return position; }
    public void   setPosition(String v)  { this.position = v; }
    public String getHireDt()            { return hireDt; }
    public void   setHireDt(String v)    { this.hireDt = v; }
    public String getEmail()             { return email; }
    public void   setEmail(String v)     { this.email = v; }
    
}
