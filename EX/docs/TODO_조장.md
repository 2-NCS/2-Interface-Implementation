# [EX 시험] 조장 TODO — 설계 / 공통 / 통합 (20점)

**시나리오**: 사원입사(HRM) -> 그룹웨어 계정발급(Groupware) — `IF_HR_001`
**역할 책임**: 인터페이스 설계서 해석, DB 스키마, 공통 유틸, 팀 통합 + 제출

---

## 작업 목록

### 1) 인터페이스 설계서 정독 + 매핑표 작성 (5점)
- [ ] `docs/인터페이스설계서.md` 정독
- [ ] 송신(HRM)/수신(Groupware) **공통 데이터 항목** 6개 식별
  -> `emp_id, name, dept_cd, position, hire_dt, email`
- [ ] **데이터 표준** 정의: `tx_no=emp_id (UNIQUE)`, `status N/S/F`, payload JSON
- [ ] 조원에게 매핑표 + IF_ID 공유

### 2) DB 스키마 작성 + 적용 (5점)
- [ ] `db/schema.sql` 검토 — HrmDB / GroupwareDB 2개 분리
- [ ] `if_outbox.tx_no UNIQUE` 제약 확인
- [ ] `account.dept_cd FK -> dept(dept_cd)` 확인
- [ ] MySQL에 두 DB 생성 + 부서 마스터(dept) 시드 데이터 입력

### 3) 공통 모듈 구현 (5점)
- [ ] `src/common/DBManager.java` — HRM/GW 각각 Connection 획득
- [ ] `src/common/HashUtil.java` — SHA-256 해시 유틸 (조원3와 공유)
- [ ] `config/db.properties` 생성 (sample 복사 후 비밀번호 채움)

### 4) 통합 + 검증 보고서 (5점)
- [ ] 조원1~3 작업물 통합 + Tomcat 배포
- [ ] 전체 동작 점검 (입사등록 -> 배치 -> 모니터링 정상)
- [ ] **검증 보고서** 작성: outbox/inbox 통계 + 실패 재시도 결과 첨부
- [ ] before/after 캡처 8컷 수집 후 제출 zip 패키징

---

## 산출물

- `docs/매핑표.md` (송수신 컬럼 매핑)
- `config/db.properties` (실제 환경값)
- `submit/검증보고서.md` + `submit/캡처/`
- 최종 제출 zip
