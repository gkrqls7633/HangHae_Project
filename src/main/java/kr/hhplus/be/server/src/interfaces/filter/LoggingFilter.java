package kr.hhplus.be.server.src.interfaces.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class LoggingFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper((HttpServletResponse) response);

        long startTime = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("traceId", traceId);

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);

        } catch (Exception e) {
            logger.error("[{}] Exception during filter chain: {}", traceId, e.getMessage(), e);
            throw e;
        } finally {
            logRequestResponse(wrappedRequest, wrappedResponse, startTime, traceId);
            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }

    private void logRequestResponse(ContentCachingRequestWrapper request,
                                    ContentCachingResponseWrapper response,
                                    long startTime, String traceId) {

        boolean isLoggableBody = false;

        long duration = System.currentTimeMillis() - startTime;

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        String userAgent = request.getHeader("User-Agent");
        String auth = request.getHeader("Authorization");
        String contentType = request.getContentType();

        if (!HttpMethod.GET.name().equalsIgnoreCase(method) && contentType != null) {
            if (contentType.contains("application/json") ||
                    contentType.contains("application/x-www-form-urlencoded")) {
                isLoggableBody = true;
            }
        }

        String requestBody = "";
        if (isLoggableBody) {
            requestBody = new String(request.getContentAsByteArray(), StandardCharsets.UTF_8);
            // 민감 정보 마스킹
            requestBody.replaceAll("\"password\"\\s*:\\s*\".*?\"", "\"password\":\"***\"");
        }

        String responseBody = new String(response.getContentAsByteArray(), StandardCharsets.UTF_8);

        logger.info("""
                    [TRACE: {}]
                    - Request: {} {}{}
                    - Headers: User-Agent={}
                    - Authorization={}
                    {}
                    - Response: Status={}, Duration={}ms
                    - Response Body: {}
                    """,
                traceId,
                method, uri, query,
                userAgent, auth,
                isLoggableBody ? "- Body: " + requestBody : "- Body: (skipped - GET or non-JSON/Form)",
                response.getStatus(), duration,
                responseBody
        );

    }
}
