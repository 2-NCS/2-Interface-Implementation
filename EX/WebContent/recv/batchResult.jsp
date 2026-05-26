<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
 <meta charset="UTF-8">
 <title>배치 실행 결과</title>
 <style>
 body { font-family: 'Malgun Gothic', sans-serif; max-width: 800px; margin: 40px auto; color:#222; }
 h2 { color: #222; }
 .stat { display: flex; gap: 16px; margin: 16px 0; }
 .stat div { padding: 12px 24px; border: 1px solid #ddd; border-radius: 6px; text-align: center; background:#fff; }
 .stat .num { font-size: 24px; font-weight: bold; color:#222; }
 pre { background: #f5f5f5; padding: 12px; border-radius: 6px; font-size: 12px; max-height: 400px; overflow-y: auto; }
 nav a { font-size: 13px; }
 </style>
</head>
<body>
 <nav><a href="../index.jsp">메인으로</a>| <a href="../monitor/list_">모니터링</a></nav>
 <h2>[2] 계정발급 배치 실행 결과</h2>

 <div class="stat">
 <div><div class="num">${totalRead}</div>읽음</div>
 <div><div class="num">${success}</div>성공</div>
 <div><div class="num">${fail}</div>실패</div>
 <div><div class="num">${skip}</div>멱등 스킵</div>
 </div>

 <h4>처리 로그</h4>
 <pre><c:out value="${log}"/></pre>

 <p>
 <a href="batch">다시 실행</a>|
 <a href="../monitor/list_">모니터링으로</a>
 </p>
</body>
</html>
