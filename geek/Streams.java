////////////////////////////////////////////////////////////////
// STREAMS API
////////////////////////////////////////////////////////////////

import java.util.*;
import java.util.stream.*;

public class Streams {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // CREAR STREAMS
        ////////////////////////////////////////////////////////////////

        // Desde colección
        List<String> names = List.of("Ana", "Luis", "Eva", "Carlos", "Bob");
        Stream<String> stream1 = names.stream();

        // Desde valores
        Stream<String> stream2 = Stream.of("a", "b", "c");

        // Desde array
        int[] arr = {1, 2, 3, 4, 5};
        IntStream stream3 = Arrays.stream(arr);

        // Rango de números
        IntStream range = IntStream.range(1, 10);      // 1..9
        IntStream rangeClosed = IntStream.rangeClosed(1, 10); // 1..10

        // Stream infinito
        Stream<Double> randoms = Stream.generate(Math::random).limit(5);
        Stream<Integer> counting = Stream.iterate(0, n -> n + 2).limit(5); // 0, 2, 4, 6, 8

        ////////////////////////////////////////////////////////////////
        // OPERACIONES INTERMEDIAS (lazy, devuelven Stream)
        ////////////////////////////////////////////////////////////////

        List<String> langs = List.of("Java", "Python", "Go", "JavaScript", "C++", "Java");

        // filter — filtrar
        List<String> longNames = langs.stream()
            .filter(s -> s.length() > 3)
            .collect(Collectors.toList());
        System.out.println(longNames);  // [Java, Python, JavaScript, Java]

        // map — transformar
        List<String> upper = langs.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        System.out.println(upper);  // [JAVA, PYTHON, GO, JAVASCRIPT, C++, JAVA]

        // flatMap — aplanar streams anidados
        List<List<Integer>> nested = List.of(
            List.of(1, 2, 3),
            List.of(4, 5),
            List.of(6)
        );
        List<Integer> flat = nested.stream()
            .flatMap(java.util.Collection::stream)
            .collect(Collectors.toList());
        System.out.println(flat);  // [1, 2, 3, 4, 5, 6]

        // distinct — eliminar duplicados
        List<String> unique = langs.stream()
            .distinct()
            .collect(Collectors.toList());
        System.out.println(unique);  // [Java, Python, Go, JavaScript, C++]

        // sorted — ordenar
        List<String> sorted = langs.stream()
            .sorted()
            .collect(Collectors.toList());
        System.out.println(sorted);  // [C++, Go, Java, Java, JavaScript, Python]

        // sorted con Comparator
        List<String> byLength = langs.stream()
            .sorted(Comparator.comparingInt(String::length))
            .collect(Collectors.toList());
        System.out.println(byLength);

        // peek — inspeccionar sin modificar (útil para debug)
        langs.stream()
            .peek(s -> System.out.println("Procesando: " + s))
            .filter(s -> s.length() > 3)
            .count();

        // limit y skip
        List<String> first3 = langs.stream().limit(3).collect(Collectors.toList());
        List<String> skip2 = langs.stream().skip(2).collect(Collectors.toList());

        ////////////////////////////////////////////////////////////////
        // OPERACIONES TERMINALES (ejecutan el pipeline)
        ////////////////////////////////////////////////////////////////

        // forEach
        langs.stream().forEach(System.out::println);

        // collect — recoger en colección
        Set<String> set = langs.stream().collect(Collectors.toSet());
        String joined = langs.stream().collect(Collectors.joining(", "));
        System.out.println(joined);  // "Java, Python, Go, JavaScript, C++, Java"

        // count
        long count = langs.stream().filter(s -> s.startsWith("J")).count();
        System.out.println("Empiezan con J: " + count);  // 3

        // findFirst / findAny
        Optional<String> first = langs.stream()
            .filter(s -> s.startsWith("P"))
            .findFirst();
        System.out.println(first.orElse("No encontrado"));  // "Python"

        // anyMatch / allMatch / noneMatch
        boolean anyLong = langs.stream().anyMatch(s -> s.length() > 8);
        boolean allShort = langs.stream().allMatch(s -> s.length() < 20);
        boolean noneEmpty = langs.stream().noneMatch(String::isEmpty);

        // min / max
        Optional<String> shortest = langs.stream()
            .min(Comparator.comparingInt(String::length));
        System.out.println("Más corto: " + shortest.orElse("")); // "Go"

        ////////////////////////////////////////////////////////////////
        // REDUCE — acumular valores
        ////////////////////////////////////////////////////////////////

        List<Integer> nums = List.of(1, 2, 3, 4, 5);

        // Suma
        int sum = nums.stream().reduce(0, Integer::sum);
        System.out.println("Suma: " + sum);  // 15

        // Producto
        int product = nums.stream().reduce(1, (a, b) -> a * b);
        System.out.println("Producto: " + product);  // 120

        // Sin valor inicial → devuelve Optional
        Optional<Integer> max = nums.stream().reduce(Integer::max);
        System.out.println("Max: " + max.orElse(0));  // 5

        ////////////////////////////////////////////////////////////////
        // COLLECTORS AVANZADOS
        ////////////////////////////////////////////////////////////////

        // groupingBy — agrupar
        Map<Integer, List<String>> byLengthMap = langs.stream()
            .collect(Collectors.groupingBy(String::length));
        System.out.println(byLengthMap);
        // {2=[Go], 3=[C++], 4=[Java, Java], 6=[Python], 10=[JavaScript]}

        // groupingBy con counting
        Map<Integer, Long> countByLength = langs.stream()
            .collect(Collectors.groupingBy(String::length, Collectors.counting()));

        // partitioningBy — dividir en dos grupos (true/false)
        Map<Boolean, List<String>> partitioned = langs.stream()
            .collect(Collectors.partitioningBy(s -> s.length() > 3));
        System.out.println("Largos: " + partitioned.get(true));
        System.out.println("Cortos: " + partitioned.get(false));

        // toMap
        Map<String, Integer> nameLengths = langs.stream()
            .distinct()
            .collect(Collectors.toMap(s -> s, String::length));

        // summarizingInt — estadísticas
        IntSummaryStatistics stats = langs.stream()
            .collect(Collectors.summarizingInt(String::length));
        System.out.println("Media longitud: " + stats.getAverage());
        System.out.println("Max longitud: " + stats.getMax());

        ////////////////////////////////////////////////////////////////
        // STREAMS NUMÉRICOS
        ////////////////////////////////////////////////////////////////

        // mapToInt / mapToDouble / mapToLong
        int totalLength = langs.stream()
            .mapToInt(String::length)
            .sum();

        OptionalDouble average = langs.stream()
            .mapToInt(String::length)
            .average();
        System.out.println("Media: " + average.orElse(0));

        ////////////////////////////////////////////////////////////////
        // PARALLEL STREAMS
        ////////////////////////////////////////////////////////////////

        // Ejecuta operaciones en paralelo usando ForkJoinPool
        long parallelSum = IntStream.rangeClosed(1, 1_000_000)
            .parallel()
            .sum();
        System.out.println("Suma paralela: " + parallelSum);

        // Cuidado: solo usar parallel si:
        // 1. El dataset es grande
        // 2. Las operaciones son costosas
        // 3. No hay efectos secundarios (no shared mutable state)
    }
}
