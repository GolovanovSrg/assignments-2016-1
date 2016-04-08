package ru.spbau.mit;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import static org.junit.Assert.*;
import static ru.spbau.mit.SecondPartTasks.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SecondPartTasksTest {

    @Test
    public void testFindQuotes() {
        assertEquals(findQuotes(FILES_PATHS, "распределен"),
                Arrays.asList("распределение для описания", "распределения благосостояния,",
                        "а также распределения дохода", "непрерывных распределений,"));
    }

    @Test
    public void testPiDividedBy4() {
        assertTrue(Math.abs(piDividedBy4() - Math.PI / 4) < 10e-3);
    }

    @Test
    public void testFindPrinter() {

        Map<String, List<String>> autorsMap = ImmutableMap.of(AUTOR_1, WORKS_1, AUTOR_2, WORKS_2);
        assertEquals(findPrinter(autorsMap), AUTOR_2);
    }

    @Test
    public void testCalculateGlobalOrder() {
        assertEquals(calculateGlobalOrder(Arrays.asList(PROD_1, PROD_2, PROD_3)), PROD_ALL);
    }

    private static final List<String> FILES_PATHS = Arrays.asList("src/test/java/ru/spbau/mit/testFindQuotes1.txt",
                                                                  "src/test/java/ru/spbau/mit/testFindQuotes2.txt");

    private static final String AUTOR_1 = "Autor_1";
    private static final String AUTOR_2 = "Autor_2";
    private static final List<String> WORKS_1 = Arrays.asList("text", "text text text", "text, text");
    private static final List<String> WORKS_2 = Arrays.asList("text2", "text2 text2 text2", "text2, text2", "text2, text2, text2");
    private static final Map<String, Integer> PROD_1 = ImmutableMap.of("Boolka", 100);
    private static final Map<String, Integer> PROD_2 = ImmutableMap.of("Boolka", 20);
    private static final Map<String, Integer> PROD_3 = ImmutableMap.of("Cheboorek", 300);
    private static final Map<String, Integer> PROD_ALL = ImmutableMap.of("Boolka", 120, "Cheboorek", 300);

}
