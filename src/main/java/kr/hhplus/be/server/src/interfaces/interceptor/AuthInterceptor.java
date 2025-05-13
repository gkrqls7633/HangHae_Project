package kr.hhplus.be.server.src.interfaces.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.hhplus.be.server.src.domain.enums.TokenStatus;
import kr.hhplus.be.server.src.domain.queue.Queue;
import kr.hhplus.be.server.src.domain.queue.QueueRepository;
import kr.hhplus.be.server.src.domain.queue.RedisQueueRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final QueueRepository queueRepository;

    private final RedisQueueRepository redisQueueRepository;

    private static final String BEARER_PREFIX = "Bearer ";

    public AuthInterceptor(QueueRepository queueRepository, RedisQueueRepository redisQueueRepository) {
        this.queueRepository = queueRepository;
        this.redisQueueRepository = redisQueueRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith(BEARER_PREFIX)) {
            token = token.substring(BEARER_PREFIX.length());
        }

        if (token == null || token.isBlank()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authorization header is missing\"}");
            return false;
        }

        // 토큰 유효성 체크
        Optional<Queue> activeToken = Optional.ofNullable(redisQueueRepository.findByTokenStatus(TokenStatus.ACTIVE));

        if (activeToken.isEmpty() || activeToken.get().getExpiredAt().isBefore(LocalDateTime.now())) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
            return false;
        }

        return true;
    }

}