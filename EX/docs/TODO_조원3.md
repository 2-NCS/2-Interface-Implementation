# [EX 시험] 조원3 TODO — 모니터링 / 예외처리 / 보안 / 검증 (20점)

**역할 책임**: 운영 도구(모니터링 + 재시도) + 보안 + 실패 시나리오 검증

---

## 작업 목록

### 1) 모니터링 화면 (10점)
- [ ] `src/monitor/IfMonitorServlet.java`
  - [ ] `OutboxDAO.selectAll(HRM)` -> outboxList request 적재
  - [ ] `InboxDAO.selectAll(GW)` -> inboxList request 적재
  - [ ] `AccountDAO.selectAll(GW)` -> accountList request 적재
  - [ ] forward -> `WebContent/monitor/list.jsp`
- [ ] 화면 표시 항목:
  - [ ] outbox: id, if_id, tx_no, status(S/F/N 색상), retry_cnt, 적재일시, 처리일시, err_msg
  - [ ] inbox: id, if_id, tx_no, status, procMs, 수신일시, err_msg
  - [ ] account: account_id, dept_cd, status, 생성일시 (pwd_hash 절대 화면 노출 X)
  - [ ] `if_outbox.status='F'` 행에만 **재시도 버튼**

### 2) 재시도 + 예외처리 (5점)
- [ ] `src/monitor/IfRetryServlet.java`
  - [ ] POST `outboxId` 받음
  - [ ] HRM의 `if_outbox` 해당 행 -> `status='N'`, `retry_cnt += 1`, `err_msg=NULL` 갱신
  - [ ] 모니터링 화면으로 redirect -> 다음 배치 실행 시 재처리
- [ ] 실패 케이스 검증 시나리오:
  - [ ] 존재하지 않는 dept_cd (예: `D999`)로 입사 등록
  - [ ] 배치 실행 -> outbox 'F' + err_msg 기록 확인
  - [ ] 재시도 버튼 -> 'N'으로 복원 확인

### 3) 보안 적용 (5점)
- [ ] **전 SQL PreparedStatement + `?` 바인딩** 확인 (조원1·2 코드 리뷰)
- [ ] **SHA-256 해시** `common/HashUtil.sha256()` 동작 검증 — 평문 비밀번호 DB 미저장 확인
- [ ] **FK 검증** — `account.dept_cd FK->dept` 확인
- [ ] **민감정보 노출 차단** — 모니터링 화면에 pwd_hash, password 절대 표시 안 함

---

## 검증 방법

1. 정상 케이스: 조원1·2 결과 후 모니터링 화면 진입 -> outbox 'S' + inbox 'S' + account 생성 표시
2. 실패 케이스: dept_cd=D999 입력 -> 배치 후 outbox 'F' + 재시도 버튼 표시
3. 재시도 클릭 -> outbox 'N' 복원 -> 부서 추가 후 재배치 -> 'S' 전환
4. 캡처: `after_04_모니터링_정상.png` 재현 + 실패/재시도 별도 캡처

---

## 산출물

- 모니터링 + 재시도 동작 캡처 3컷 (정상/실패/재시도)
- 보안 점검 체크 (PreparedStatement 사용 여부 코드 리뷰 결과)
