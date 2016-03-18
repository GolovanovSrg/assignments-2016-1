package ru.spbau.mit;

import static org.junit.Assert.*;

import jdk.nashorn.internal.objects.annotations.Function;
import org.junit.Test;
import static java.util.Arrays.asList;

import java.util.ArrayList;

/**
 * Created by golovanov on 18.03.16.
 */
public class FunctionalJavaTest {

    @Test
    public void testFunction1() {
        Function1<Integer, Double> func1 = new Function1<Integer, Double>() {
            @Override
            public Double apply(Integer x) {
                return new Double(x) / 2;
            }
        };

        Function1<Double, Double> func2 = new Function1<Double, Double>() {
            @Override
            public Double apply(Double x) {
                return new Double(2 * x);
            }
        };

        assertEquals(new Double(1.5), func1.apply(3));
        assertEquals(new Double(3), func1.compose(func2).apply(3));
    }

    @Test
    public void testFunction2() {
        Function1<String, String> func1 = new Function1<String, String>() {
            @Override
            public String apply(String x) {
                return x + "-func1";
            }
        };

        Function2<String, String, String> func2 = new Function2<String, String, String>() {
            @Override
            public String apply(String x, String y) {
                return x + y;
            }
        };

        assertEquals("12-func1", func2.compose(func1).apply("1", "2"));
        assertEquals("12", func2.bind1("1").apply("2"));
        assertEquals("21", func2.bind2("1").apply("2"));

        Function1<String, Function1<String, String>> curry = func2.curry();
        assertEquals("12", curry.apply("1").apply("2"));
    }

    @Test
    public void testPredicate() {
        Predicate<Integer> predic1 = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer t) {
                return t >= 0;
            }
        };

        Predicate<Integer> predic2 = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer t) {
                return t <= 10;
            }
        };

        Predicate<Boolean> predic3 = new Predicate<Boolean>() {
            @Override
            public Boolean apply(Boolean t) {
                return !t;
            }
        };


        assertTrue(predic1.compose(predic3).apply(-1));
        assertTrue(predic1.or(predic2).apply(11));
        assertTrue(predic1.and(predic2).apply(5));
        assertFalse(predic1.and(predic2).apply(11));
        assertTrue(predic1.not().apply(-1));
        assertFalse(Predicate.ALWAYS_FALSE);
        assertTrue(Predicate.ALWAYS_TRUE);

    }

    @Test
    public void testCollections() {
        ArrayList<Integer> col = new ArrayList<>(asList(1, 1, 2, 1));

        Function1<Integer, Integer> func1 = new Function1<Integer, Integer>() {
            @Override
            public Integer apply(Integer x) {
                return x + 1;
            }
        };

        Function2<Integer, Integer, Integer> func2 = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer x, Integer y) {
                return x + y;
            }
        };

        Predicate<Integer> predic = new Predicate<Integer>() {
            @Override
            public Boolean apply(Integer t) {
                return t.equals(1);
            }
        };


        assertEquals(asList(2, 2, 3, 2), Collections.map(func1, col));
        assertEquals(asList(1, 1, 1), Collections.filter(predic, col));
        assertEquals(asList(1, 1), Collections.takeWhile(predic, col));
        assertEquals(asList(), Collections.takeUnless(predic, col));
        assertEquals(new Integer(6), Collections.foldl(func2, 1, col));
        assertEquals(new Integer(6), Collections.foldr(func2, 1, col));
    }
}
