<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
 <meta charset="UTF-8">
 <title>인터페이스 모니터링 (EX 시험)</title>
 <style>
 body { font-family: 'Malgun Gothic', sans-serif; max-width: 1300px; margin: 24px auto; color:#222; }
 h2,h3 { color: #222; }
 h3 { margin-top: 32px; }
 table { width: 100%; border-collapse: collapse; font-size: 13px; }
 th,td { padding: 6px 8px; border: 1px solid #ddd; text-align: left; }
 th { background: #f5f5f5; }
 .ok { color: #222; font-weight: bold; }
 .fail { color: #222; font-weight: bold; }
 .ing { color: #222; font-weight: bold; }
 button{ padding: 4px 10px; background:#333; color:#fff; border:0; cursor:pointer; border-radius: 3px; }
 .btn-batch { background: #333; padding: 8px 20px; text-decoration: none; color: #fff; border-radius: 4px; }
 nav a { font-size: 13px; }
 </style>
</head>
<body>
 <nav><a href="../index.jsp">메인으로</a></nav>
 <h2>인터페이스 + 계정 모니터링</h2>
 <p><a class="btn-batch" href="../recv/batch_">배치 실행</a></p>

 <c:if test="${not empty error}"><p class="fail">${error}</p></c:if>

 <!-- 계정 현황 -->
 <h3>발급된 계정 (account) — pwd_hash 는 보안상 노출 X</h3>
 <table>
 <thead><tr><th>accountId</th><th>deptCd</th><th>status</th><th>생성일시</th></tr></thead>
 <tbody>
 <c:forEach items="${accountList}" var="r">
 <tr>
 <td>${r.accountId}</td><td>${r.deptCd}</td><td>${r.status}</td>
 <td><fmt:formatDate value="${r.createDt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
 </tr>
 </c:forEach>
 </tbody>
 </table>

 <!-- Outbox -->
 <h3>송신 Outbox (if_outbox)</h3>
 <table>
 <thead>
 <tr><th>id</th><th>if_id</th><th>txNo(empId)</th><th>status</th><th>retry</th><th>적재</th><th>처리</th><th>오류</th><th>재시도</th></tr>
 </thead>
 <tbody>
 <c:forEach items="${outboxList}" var="r">
 <tr>
 <td>${r.outboxId}</td><td>${r.ifId}</td><td>${r.txNo}</td>
 <td>
 <c:choose>
 <c:when test="${r.status eq 'S'}"><span class="ok">S</span></c:when>
 <c:when test="${r.status eq 'F'}"><span class="fail">F</span></c:when>
 <c:otherwise><span class="ing">N</span></c:otherwise>
 </c:choose>
 </td>
 <td>${r.retryCnt}</td>
 <td><fmt:formatDate value="${r.regDt}" pattern="HH:mm:ss"/></td>
 <td><fmt:formatDate value="${r.procDt}" pattern="HH:mm:ss"/></td>
 <td><c:out value="${r.errMsg}"/></td>
 <td>
 <c:if test="${r.status eq 'F'}">
 <form action="retry" method="post" style="margin:0">
 <input type="hidden" name="outboxId" value="${r.outboxId}">
 <button type="submit">재시도</button>
 </form>
 </c:if>
 </td>
 </tr>
 </c:forEach>
 </tbody>
 </table>

 <!-- Inbox -->
 <h3>수신 Inbox (if_inbox)</h3>
 <table>
 <thead>
 <tr><th>id</th><th>if_id</th><th>txNo</th><th>status</th><th>처리(ms)</th><th>수신</th><th>오류</th></tr>
 </thead>
 <tbody>
 <c:forEach items="${inboxList}" var="r">
 <tr>
 <td>${r.inboxId}</td><td>${r.ifId}</td><td>${r.txNo}</td>
 <td>
 <c:choose>
 <c:when test="${r.status eq 'S'}"><span class="ok">S</span></c:when>
 <c:otherwise><span class="fail">F</span></c:otherwise>
 </c:choose>
 </td>
 <td>${r.procMs}</td>
 <td><fmt:formatDate value="${r.recvDt}" pattern="HH:mm:ss"/></td>
 <td><c:out value="${r.errMsg}"/></td>
 </tr>
 </c:forEach>
 </tbody>
 </table>
</body>
</html>
