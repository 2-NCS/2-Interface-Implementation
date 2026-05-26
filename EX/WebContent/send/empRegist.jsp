<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
 <meta charset="UTF-8">
 <title>사원 입사등록</title>
 <style>
 body { font-family: 'Malgun Gothic', sans-serif; max-width: 560px; margin: 40px auto; color:#222; }
 h2 { color: #222; }
 label { display: block; margin: 8px 0 4px; font-weight: bold; }
 input, select { width: 100%; padding: 8px; box-sizing: border-box; font-size: 14px; }
 button { margin-top: 20px; padding: 10px 30px; background:#333; color:#fff; border:0; font-size:14px; cursor:pointer; border-radius:3px; }
 small { color: #888; }
 nav a { font-size: 13px; }
 </style>
</head>
<body>
 <nav><a href="../index.jsp">메인으로</a>| <a href="../monitor/list_">모니터링</a></nav>
 <h2>[1] 사원 입사등록 (송신측 HRM)</h2>
 <p>POST <code>/send/emp</code> -> employee + if_outbox 트랜잭션 INSERT (empId 자동 발급)</p>

 <form action="../send/emp_" method="post">
 <label>사원명 (empName) <small>* VARCHAR(50)</small></label>
 <input type="text" name="empName" maxlength="50" value="김신입" required>

 <label>부서 (deptCd)</label>
 <select name="deptCd">
 <option value="D001">D001 — 경영지원본부</option>
 <option value="D002">D002 — 개발본부</option>
 <option value="D003">D003 — 영업본부</option>
 <option value="D004">D004 — 마케팅본부</option>
 <option value="D005">D005 — 기획실</option>
 <option value="D999">D999 — (TC03용 미등록 부서)</option>
 </select>

 <label>직급 (position)</label>
 <select name="position">
 <option value="사원">사원</option>
 <option value="대리">대리</option>
 <option value="과장">과장</option>
 <option value="차장">차장</option>
 <option value="부장">부장</option>
 <option value="인턴">인턴 (TC04용 — 미지원)</option>
 </select>

 <label>이메일 (email)</label>
 <input type="email" name="email" placeholder="kim@company.com">

 <button type="submit">입사 등록</button>
 </form>

 <hr>
 <small>입사 등록 후 계정 발급은 <strong>수신측 배치 실행</strong>후 반영됩니다.<br>
 메인 -> [2] 배치 실행 -> [3] 모니터링 순서로 검증.</small>
</body>
</html>
