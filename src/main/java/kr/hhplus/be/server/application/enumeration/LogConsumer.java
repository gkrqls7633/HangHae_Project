package kr.hhplus.be.server.application.enumeration;

import kr.hhplus.be.server.application.function.QuadConsumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;

@AllArgsConstructor
public enum LogConsumer implements CommonEnumInterface {
    TRACE("TRACE", (log, format, message, e) -> log.trace(format, message, e)),
    DEBUG("DEBUG", (log, format, message, e) -> log.debug(format, message, e)),
    INFO("INFO", (log, format, message, e) -> log.info(format, message, e)),
    WARN("WARN", (log, format, message, e) -> log.warn(format, message, e)),
    ERROR("ERROR", (log, format, message, e) -> log.error(format, message, e));

    @Getter
    String code;
    QuadConsumer<Logger, String, String, Throwable> consumer;

    public void logging(Logger log, String format, String message, Throwable e) {
        this.consumer.accept(log, format, message, e);
    }
}
