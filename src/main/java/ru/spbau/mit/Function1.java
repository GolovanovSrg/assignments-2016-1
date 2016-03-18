package ru.spbau.mit;

/**
 * Created by golovanov on 17.03.16.
 */
public abstract class Function1<T, R> {

    public abstract R apply(T x);

    public <Q> Function1<T, Q> compose(final Function1<? super R, Q> func) {
        return new Function1<T, Q>() {
            @Override
            public Q apply(T x) {
                return func.apply(Function1.this.apply(x));
            }
        };
    }
}
