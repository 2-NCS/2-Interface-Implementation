# [EX 시험] 조원2 TODO — 수신측 배치 구현 (20점)

**역할 책임**: Groupware(그룹웨어) 수신측 — `if_outbox` polling -> `account` 생성 + `if_inbox` 기록

---

## 작업 목록

### 1) Account DAO + Inbox DAO (5점)
- [ ] `src/recv/AccountDAO.java`
  - [ ] `insert(Connection, accountId, deptCd, pwdHash)` — pwd_hash 컬럼 (평문 절대 금지)
  - [ ] `existsDept(Connection, deptCd)` — FK 검증용
- [ ] `src/recv/InboxDAO.java`
  - [ ] `insert(Connection, ifId, txNo, status, procMs, errMsg)`
  - [ ] `existsByTxNo(Connection, txNo)` — **멱등성 체크** (이미 처리된 tx_no면 SKIP)

### 2) 배치 처리 Service (10점)
- [ ] `src/recv/AccountBatchServlet.java`
  - [ ] HRM의 `if_outbox WHERE status='N'` 전체 SELECT
  - [ ] 각 outbox 행에 대해:
    - [ ] **트랜잭션 시작**
    - [ ] inbox 멱등 체크 — 이미 있으면 outbox만 'S' 갱신 후 skip
    - [ ] payload JSON 파싱
    - [ ] **dept_cd FK 검증** -> 부서 미존재 시 'F' (조원3 예외 처리와 연결)
    - [ ] 비밀번호 **SHA-256 해시** (`HashUtil.sha256()`)
    - [ ] `account` INSERT + `account_history` INSERT
    - [ ] `if_inbox` INSERT (status='S', procMs 측정)
    - [ ] HRM의 `if_outbox.status` -> 'S' UPDATE
    - [ ] commit
- [ ] 통계 카운터: 읽음 / 성공 / 실패 / 멱등스킵
- [ ] 결과 화면(`/recv/batchResult.jsp`)에 통계 + 처리 로그 표시

### 3) 멱등성 보장 (5점)
- [ ] 동일 tx_no 재실행 시 **계정 중복 생성 안 됨**
- [ ] 멱등 스킵 시 통계 `skip++`
- [ ] account.account_id = tx_no = emp_id 로 통일

---

## 검증 방법

1. 조원1 입사 등록 완료 후 `/recv/batch` 실행
2. 통계 화면: `읽음 N / 성공 N / 실패 0 / 스킵 0`
3. 같은 batch 다시 실행 -> `스킵 N` 으로 증가
4. MySQL `GroupwareDB`: `SELECT * FROM account WHERE pwd_hash IS NOT NULL`
5. 캡처: `after_03_배치실행_OK.png` 재현

---

## 주의

- **반드시 PreparedStatement** — SQL Injection 방어
- 비밀번호 평문 저장 절대 금지 — SHA-256 해시 후 저장
- 부서 미존재 시 `RuntimeException("dept_cd not found: " + deptCd)` 발생 -> rollback -> outbox 'F'
- 한 outbox 처리 실패 시 다음 outbox 는 계속 처리 (전체 중단 X)
