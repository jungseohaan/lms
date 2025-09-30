package com.visang.aidt.lms.api.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
public class ReadableRequestWrapperFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        // Do nothing
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            ReadableRequestWrapper wrapper = new ReadableRequestWrapper((HttpServletRequest) request);
            chain.doFilter(wrapper, response);
        } catch (IOException e) {
            if (isConnectionReset(e)) {
                log.warn(e.getMessage());
            } else {
                log.error(e.getMessage());
            }
        } catch (ClassCastException e) {
            log.error("ReadableRequestWrapperFilter - ServletRequest 캐스팅 오류: {}", e.getMessage());
        } catch (OutOfMemoryError e) {
            log.error("ReadableRequestWrapperFilter - 메모리 부족으로 인한 래퍼 생성 실패: {}", e.getMessage());
        } catch (Exception e) {
            log.error("ReadableRequestWrapperFilter - 예상치 못한 필터 오류: {}", e.getMessage(), e);
        }
    }

    private boolean isConnectionReset(Throwable t) {
        // 일반 메시지 패턴
        String msg = t.getMessage();
        return msg != null &&
                (msg.contains("Connection reset by peer") ||
                        msg.contains("Broken pipe") ||
                        msg.contains("An existing connection was forcibly closed"));
    }

    @Override
    public void destroy() {
        // Do nothing
    }

}
