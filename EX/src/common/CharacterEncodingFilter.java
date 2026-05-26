package common;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 모든 요청·응답을 UTF-8 로 강제 — ★ 정답
 *
 * <p>한글 회원명(memName)이 깨지지 않게 하기 위함. web.xml 의 filter-mapping &quot;/*&quot; 로 적용.</p>
 *
 * <p>학습모듈 1-2 §1 (인터페이스 데이터 구조 표준 — UTF-8)</p>
 */
public class CharacterEncodingFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig cfg) {
        String enc = cfg.getInitParameter("encoding");
        if (enc != null && !enc.isEmpty()) this.encoding = enc;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        req.setCharacterEncoding(encoding);
        res.setCharacterEncoding(encoding);
        chain.doFilter(req, res);
    }

    @Override public void destroy() { /* no-op */ }
}
