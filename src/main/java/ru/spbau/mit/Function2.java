package ru.spbau.mit;

/**
 * Created by golovanov on 17.03.16.
 */
public abstract class Function2<T1, T2, R> {

    public abstract R apply(T1 x, T2 y);

    public <Q> Function2<T1, T2, Q> compose(final Function1<? super R, Q> func) {
        return new Function2<T1, T2, Q>() {
            @Override
            public Q apply(T1 x, T2 y) {
                return func.apply(Function2.this.apply(x, y));
            }
        };
    }

    public Function1<T2, R> bind1(final T1 x) {
        return new Function1<T2, R>() {
            @Override
            public R apply(T2 y) {
                return Function2.this.apply(x, y);
            }
        };
    }

    public Function1<T1, R> bind2(final T2 y) {
        return new Function1<T1, R>() {
            @Override
            public R apply(T1 x) {
                return Function2.this.apply(x, y);
            }
        };
    }

    public Function1<T1, Function1<T2, R>> curry() {
        return new Function1<T1, Function1<T2, R>>() {
            @Override
            public Function1<T2, R> apply(T1 x) {
                return Function2.this.bind1(x);
            }
        };
    }

}
