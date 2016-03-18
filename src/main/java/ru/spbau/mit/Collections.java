package ru.spbau.mit;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by golovanov on 18.03.16.
 */
public final class Collections {

    private Collections() {}

    public static <T, R> ArrayList<R> map(Function1<? super T, R> func, Iterable<T> col) {
        ArrayList<R> newCol = new ArrayList<>();
        for (T colItem : col) {
            R newColItem = func.apply(colItem);
            newCol.add(newColItem);
        }
        return newCol;
    }

    public static <T> ArrayList<T> filter(Predicate<T> predic, Iterable<T> col) {
        ArrayList<T> newCol = new ArrayList<>();
        for (T colItem : col) {
            if (predic.apply(colItem)) {
                newCol.add(colItem);
            }
        }
        return newCol;
    }

    public static <T> ArrayList<T> takeWhile(Predicate<T> predic, Iterable<T> col) {
        ArrayList<T> newCol = new ArrayList<>();
        for (T colItem : col) {
            if (!predic.apply(colItem)) {
                break;
            }
            newCol.add(colItem);
        }
        return newCol;
    }

    public static <T> ArrayList<T> takeUnless(Predicate<T> predic, Iterable<T> col) {
        return takeWhile(predic.not(), col);
    }

    public static <T, R> R foldl(Function2<R, T, R> func, R init, Iterable<T> col) {
        R res = init;
        for (T colItem : col) {
            res = func.apply(res, colItem);
        }
        return res;
    }

    public static <T, R> R foldr(Function2<T, R, R> func, R init, Iterable<T> col) {
        LinkedList<T> revCol = new LinkedList<>();
        for (T colItem : col) {
            revCol.addFirst(colItem);
        }
        R res = init;
        for (T colItem : col) {
            res = func.apply(colItem, res);
        }
        return res;
    }

}
