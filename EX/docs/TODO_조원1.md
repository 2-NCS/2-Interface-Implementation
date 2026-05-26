# [EX 시험] 조원1 TODO — 송신측 구현 (20점)

**역할 책임**: HRM(인사관리) 송신측 — 사원 입사등록 -> `employee` + `if_outbox` 트랜잭션 INSERT

---

## 작업 목록

### 1) VO + DAO 구현 (5점)
- [ ] `src/send/Employee.java` — VO 필드 채우기 (emp_id, name, dept_cd, position, hire_dt, email)
- [ ] `src/send/EmployeeDAO.java`
  - [ ] `insert(Connection, Employee)` — PreparedStatement 사용
  - [ ] `nextEmpId()` — `E + yyyyMMdd + seq` 형식 자동 생성

### 2) Outbox 적재 DAO (5점)
- [ ] `src/send/OutboxDAO.java`
  - [ ] `insert(Connection, ifId, txNo, payload)` — status='N' 으로 적재
  - [ ] payload는 JSON 직렬화 (`common/JsonUtil` 활용)

### 3) Servlet — 트랜잭션 처리 (10점)
- [ ] `src/send/EmpRegistServlet.java`
  - [ ] POST 파라미터 수신: name, dept_cd, position, hire_dt, email, password
  - [ ] **트랜잭션 시작**: `conn.setAutoCommit(false)`
  - [ ] `employee` INSERT -> `if_outbox` INSERT (tx_no=emp_id)
  - [ ] 성공 시 `commit()`, 실패 시 `rollback()` + finally `close()`
  - [ ] 결과 화면(`/send/result.jsp`) 으로 forward — empId, outboxId 전달

---

## 검증 방법

1. `/send/empRegist.jsp` 폼에서 입사 등록 제출
2. 결과 화면에 사원번호 `E20260518xxx` 표시 확인
3. MySQL `HrmDB`: `SELECT * FROM employee` / `SELECT * FROM if_outbox WHERE status='N'`
4. 캡처: `after_02_입사등록_OK.png` 와 동일한 화면 재현

---

## 주의

- 모든 SQL **반드시 PreparedStatement + ?** (조원3 보안 항목과 연결)
- 비밀번호는 평문으로 받되 — **employee 테이블에는 평문 저장 안 함** (조원2 배치에서 해시 후 account에 저장)
- 트랜잭션 누락 시 outbox 만 적재되거나 employee 만 적재되는 정합성 깨짐
