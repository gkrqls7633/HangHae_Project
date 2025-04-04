package kr.hhplus.be.server.application.function;

import java.util.Objects;

@FunctionalInterface
public interface QuadConsumer<T, U, V, R> {

    void accept(T t, U u, V v, R r);

    default QuadConsumer<T, U, V, R> andThen(QuadConsumer<? super T, ? super U, ? super V, ? super R> after) {
        Objects.requireNonNull(after);

        return (t, u, v, r) -> {
            accept(t, u, v, r);
            after.accept(t, u, v, r);
        };
    }

}