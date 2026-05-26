# [EX] 사원입사 -> 그룹웨어 계정발급 — 시험용 (Assessment)

> **NCS 능력단위** `2001020212_23v6` 인터페이스 구현
> **분류**: 능력평가 본 시험
>
> 본 프로젝트는 연습용 EX01·EX02·EX03 클리어 후 응시하는 본 시험이다.
> EX02(주문->재고)와 동일한 **Transactional Outbox + Inbox** 패턴을 사용하되, 도메인이 다르다.

---

## 구조

```
EX/
├── docs/{인터페이스설계서.md, 가이드.md}    (정독 필수)
├── db/schema.sql + config/db.properties.sample
├── src/
│   ├── common/  (DBManager TODO, HashUtil TODO, JsonUtil/Filter 제공)
│   ├── send/    (Employee/EmployeeDAO/OutboxDAO/EmpRegistServlet TODO)
│   ├── recv/    (AccountDAO/InboxDAO/AccountBatchServlet TODO)
│   └── monitor/ (IfMonitorServlet/IfRetryServlet TODO)
└── WebContent/  (전체 제공 — 수정 불필요)
```

## 학습 순서

| 순서 | 파일 | 시간 | 능력단위요소 |
|:--:|:---|:--:|:--:|
| 1 | `docs/인터페이스설계서.md` 정독 | 30분 | 1 (24점) |
| 2 | `common/DBManager.java`        | 20분 | 2 |
| 3 | `common/HashUtil.java`         | 15분 | 2 (보안) |
| 4 | `send/Employee.java`           | 10분 | 1 |
| 5 | `send/EmployeeDAO.java`        | 40분 | 2 (트랜잭션) |
| 6 | `send/OutboxDAO.java`          | 30분 | 2,3 |
| 7 | `send/EmpRegistServlet.java`   | 30분 | 2,3 |
| 8 | `recv/AccountDAO.java`         | 30분 | 2,3 (FK + 해시) |
| 9 | `recv/InboxDAO.java`           | 20분 | 2,3 (UNIQUE) |
| 10| `recv/AccountBatchServlet.java`| 60분 | 2,3 핵심 |
| 11| `monitor/IfMonitorServlet.java`| 10분 | 3 |
| 12| `monitor/IfRetryServlet.java`  | 15분 | 3 |

## 시작
1. `mysql -uroot -p < db\schema.sql`
2. `copy config\db.properties.sample config\db.properties`
3. lib jar 배치
4. Eclipse Import -> Run on Server (Tomcat 10.1)
5. <http://localhost:8080/IfApp_EX/>

## 평가
[CHECKLIST.md](CHECKLIST.md) 16항목 / 100점. 합격 60점 이상.

> 막히면 `../EX_답/src/` 참고. (시험 환경에서는 비공개)
