package ru.spbau.mit;

/**
 * Created by golovanov on 17.03.16.
 */
public abstract class Predicate<T> extends Function1<T, Boolean> {

    public static final boolean ALWAYS_TRUE = true;
    public static final boolean ALWAYS_FALSE = false;

    public Predicate<T> or(final Predicate<? super T> predic) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T p) {
                return Predicate.this.apply(p) || predic.apply(p);
            }
        };
    }

    public Predicate<T> and(final Predicate<? super T> predic) {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T p) {
                return Predicate.this.apply(p) && predic.apply(p);
            }
        };
    }

    public Predicate<T> not() {
        return new Predicate<T>() {
            @Override
            public Boolean apply(T p) {
                return !Predicate.this.apply(p);
            }
        };
    }
}
