<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
 <meta charset="UTF-8">
 <title>IfApp_EX [시험] — 사원입사 그룹웨어 계정발급</title>
 <style>
 body { font-family: 'Malgun Gothic', sans-serif; max-width: 760px; margin: 40px auto; color:#222; }
 h1 { color: #222; border-bottom: 1px solid #ccc; padding-bottom: 8px; }
 .badge { background:#666; color:#fff; padding:2px 8px; border-radius:4px; font-size:12px; }
 .card{ border: 1px solid #ddd; border-radius: 6px; padding: 16px; margin: 12px 0; }
 .card a { font-size: 18px; font-weight: bold; }
 small { color: #777; }
 </style>
</head>
<body>
 <h1>EX <span class="badge">시험</span>— 사원입사 그룹웨어 계정발급</h1>
 <p>NCS 2001020212_23v6 인터페이스 구현 — <strong>능력평가 본 시험 (정답본)</strong></p>

 <div class="card">
 <a href="send/empRegist.jsp">[1] 사원 입사등록 (송신측)</a><br>
 <small>입사등록 -> employee + if_outbox 트랜잭션 INSERT</small>
 </div>

 <div class="card">
 <a href="recv/batch_">[2] 배치 실행 (수신측)</a><br>
 <small>if_outbox polling -> 계정 생성(SHA-256 해시 비밀번호) -> inbox 적재</small>
 </div>

 <div class="card">
 <a href="monitor/list_">[3] 모니터링</a><br>
 <small>Outbox / Inbox / 계정 현황 + 실패건 재시도</small>
 </div>

 <hr>
 <p><strong>인터페이스 ID</strong>: IF_HR_001</p>
 <p><strong>설계서</strong>: <code>docs/인터페이스설계서.md</code></p>
 <p><strong>특징</strong>: Transactional Outbox + Inbox UNIQUE 멱등 + SHA-256 비번 해시</p>
</body>
</html>
