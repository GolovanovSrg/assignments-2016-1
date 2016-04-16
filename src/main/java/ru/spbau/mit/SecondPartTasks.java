package ru.spbau.mit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SecondPartTasks {

    private SecondPartTasks() {}

    // Найти строки из переданных файлов, в которых встречается указанная подстрока.
    public static List<String> findQuotes(List<String> paths, CharSequence sequence) {
        Stream<String> streamLinesFromFiles = paths.stream()
                                                    .flatMap(p -> {
                                                        try {
                                                            return Files.lines(Paths.get(p));
                                                        } catch (IOException e) {
                                                            return null;
                                                        }
                                                    });

        return streamLinesFromFiles
                    .filter(l -> l.contains(sequence))
                    .collect(Collectors.toList());
    }

    // В квадрат с длиной стороны 1 вписана мишень.
    // Стрелок атакует мишень и каждый раз попадает в произвольную точку квадрата.
    // Надо промоделировать этот процесс с помощью класса java.util.Random и посчитать,
    // какова вероятность попасть в мишень.
    public static double piDividedBy4() {
        final int nIteration = 100000;
        final Random rnd = new Random();
        final double centerX = 0.5;
        final double centerY = 0.5;
        final double radius = 0.5;

        return (double) Stream.generate(() -> Math.sqrt(Math.pow(rnd.nextDouble() - centerX, 2)
                                                + Math.pow(rnd.nextDouble() - centerY, 2)))
                .limit(nIteration)
                .filter(d -> d <= radius)
                .count() / nIteration;
    }

    // Дано отображение из имени автора в список с содержанием его произведений.
    // Надо вычислить, чья общая длина произведений наибольшая.
    public static String findPrinter(Map<String, List<String>> compositions) {
        return compositions.entrySet().stream()
                .max(Comparator.comparing(es -> es.getValue().stream()
                                                    .mapToInt(String::length)
                                                    .sum()))
                .get()
                .getKey();
    }

    // Вы крупный поставщик продуктов. Каждая торговая сеть делает вам заказ в виде Map<Товар, Количество>.
    // Необходимо вычислить, какой товар и в каком количестве надо поставить.
    public static Map<String, Integer> calculateGlobalOrder(List<Map<String, Integer>> orders) {
        return orders.stream()
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.groupingBy(Map.Entry::getKey,
                         Collectors.summingInt(Map.Entry::getValue)));
    }
}
