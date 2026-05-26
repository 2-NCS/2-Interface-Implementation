# lib/ 폴더 — 외부 jar 안내

본 폴더에 아래 **두 가지 jar** 를 직접 받아 넣는다.
WebContent/WEB-INF/lib/ 로 복사도 함께 (Tomcat 배포용).

## 1. MySQL Connector/J (필수)
- 다운로드: https://dev.mysql.com/downloads/connector/j/
- 파일명 예: `mysql-connector-j-9.1.0.jar`
- 두 곳에 모두 복사:
  - `lib/` (Eclipse 빌드패스용 — `.classpath` 가 이 경로를 참조함)
  - `WebContent/WEB-INF/lib/` (Tomcat 런타임용)

## 2. JSTL (JSP 태그 라이브러리)
모니터링 화면의 `<c:forEach>` `<fmt:formatDate>` 사용에 필요.

Tomcat 10+ (Jakarta EE 9+) 기준 두 개 jar 가 필요하다:
- 다운로드: https://jakarta.ee/specifications/tags/3.0/
- 파일명 예:
  - `jakarta.servlet.jsp.jstl-3.0.x.jar`
  - `jakarta.servlet.jsp.jstl-api-3.0.x.jar`

> 참고: Tomcat 9 (Java EE 8) 을 쓰는 경우 `javax.servlet.jsp.jstl-1.2.x.jar` + 패키지 이름
>    `jakarta.tags.core` 를 `http://java.sun.com/jsp/jstl/core` 로 바꿔야 한다.
>    본 프로젝트는 **Tomcat 10.1 기준**으로 작성되었다.

## .gitignore 처리
`*.jar` 는 `.gitignore` 로 커밋 제외 — 학생이 각자 받아서 배치 (저작권 + 용량 회피)
