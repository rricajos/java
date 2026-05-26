////////////////////////////////////////////////////////////////
// LAMBDAS E INTERFACES FUNCIONALES
////////////////////////////////////////////////////////////////

import java.util.*;
import java.util.function.*;

public class LambdasAndFunctional {

    ////////////////////////////////////////////////////////////////
    // LAMBDA EXPRESSIONS
    ////////////////////////////////////////////////////////////////

    // Una lambda es una función anónima que implementa
    // una interfaz funcional (interfaz con un solo método abstracto)

    // Sintaxis: (parámetros) -> expresión
    //           (parámetros) -> { bloque de código }

    ////////////////////////////////////////////////////////////////
    // INTERFACES FUNCIONALES PRINCIPALES (java.util.function)
    ////////////////////////////////////////////////////////////////

    // Predicate<T>    — T → boolean   (filtrar)
    // Function<T, R>  — T → R         (transformar)
    // Consumer<T>     — T → void      (consumir)
    // Supplier<T>     — () → T        (producir)
    // UnaryOperator<T> — T → T        (transformar mismo tipo)
    // BinaryOperator<T> — (T, T) → T  (combinar)
    // BiFunction<T, U, R> — (T, U) → R

    ////////////////////////////////////////////////////////////////
    // INTERFAZ FUNCIONAL PERSONALIZADA
    ////////////////////////////////////////////////////////////////

    @FunctionalInterface
    interface MathOperation {
        double operate(double a, double b);

        // Puede tener métodos default y static
        default MathOperation andThen(UnaryOperator<Double> after) {
            return (a, b) -> after.apply(this.operate(a, b));
        }
    }

    ////////////////////////////////////////////////////////////////
    // METHOD REFERENCES (referencias a métodos)
    ////////////////////////////////////////////////////////////////

    // Cuatro tipos:
    // 1. Referencia a método estático      → Clase::metodoEstatico
    // 2. Referencia a método de instancia  → objeto::metodo
    // 3. Referencia a método de tipo       → Clase::metodo
    // 4. Referencia a constructor          → Clase::new

    static int compareByLength(String a, String b) {
        return Integer.compare(a.length(), b.length());
    }

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // LAMBDAS BÁSICAS
        ////////////////////////////////////////////////////////////////

        // Sin parámetros
        Runnable hello = () -> System.out.println("Hola");
        hello.run();

        // Un parámetro (paréntesis opcionales)
        Consumer<String> print = s -> System.out.println(s);
        print.accept("Lambda!");

        // Dos parámetros
        MathOperation sum = (a, b) -> a + b;
        MathOperation multiply = (a, b) -> a * b;
        System.out.println(sum.operate(3, 4));       // 7.0
        System.out.println(multiply.operate(3, 4));   // 12.0

        // Con bloque de código
        MathOperation power = (a, b) -> {
            double result = Math.pow(a, b);
            return result;
        };
        System.out.println(power.operate(2, 10));  // 1024.0

        ////////////////////////////////////////////////////////////////
        // PREDICATE — test (filtrar)
        ////////////////////////////////////////////////////////////////

        Predicate<String> isLong = s -> s.length() > 5;
        Predicate<String> startsWithJ = s -> s.startsWith("J");

        System.out.println(isLong.test("Java"));        // false
        System.out.println(isLong.test("JavaScript"));   // true

        // Composición de predicados
        Predicate<String> isLongAndJ = isLong.and(startsWithJ);
        Predicate<String> isLongOrJ = isLong.or(startsWithJ);
        Predicate<String> isNotLong = isLong.negate();

        System.out.println(isLongAndJ.test("JavaScript")); // true
        System.out.println(isLongAndJ.test("Java"));        // false

        ////////////////////////////////////////////////////////////////
        // FUNCTION — apply (transformar)
        ////////////////////////////////////////////////////////////////

        Function<String, Integer> toLength = String::length;
        Function<Integer, String> toStars = n -> "*".repeat(n);

        System.out.println(toLength.apply("Hola"));  // 4

        // Composición de funciones
        Function<String, String> toStarBar = toLength.andThen(toStars);
        System.out.println(toStarBar.apply("Hola"));  // "****"

        ////////////////////////////////////////////////////////////////
        // CONSUMER — accept (consumir sin devolver)
        ////////////////////////////////////////////////////////////////

        Consumer<String> shout = s -> System.out.println(s.toUpperCase());
        Consumer<String> whisper = s -> System.out.println(s.toLowerCase());

        // Encadenar consumers
        Consumer<String> both = shout.andThen(whisper);
        both.accept("Hola");
        // "HOLA"
        // "hola"

        ////////////////////////////////////////////////////////////////
        // SUPPLIER — get (producir sin recibir)
        ////////////////////////////////////////////////////////////////

        Supplier<Double> random = Math::random;
        Supplier<List<String>> emptyList = ArrayList::new;

        System.out.println(random.get());     // 0.xxxx
        List<String> list = emptyList.get();   // nueva lista vacía

        ////////////////////////////////////////////////////////////////
        // BIFUNCTION Y BINARYOPERATOR
        ////////////////////////////////////////////////////////////////

        BiFunction<String, String, String> concat = (a, b) -> a + " " + b;
        System.out.println(concat.apply("Hola", "Mundo")); // "Hola Mundo"

        BinaryOperator<Integer> max = Integer::max;
        System.out.println(max.apply(10, 20));  // 20

        ////////////////////////////////////////////////////////////////
        // METHOD REFERENCES
        ////////////////////////////////////////////////////////////////

        List<String> names = Arrays.asList("Carlos", "Ana", "Eva", "Bob");

        // 1. Método estático
        names.sort(LambdasAndFunctional::compareByLength);

        // 2. Método de instancia de un objeto
        names.forEach(System.out::println);

        // 3. Método de instancia del tipo
        names.sort(String::compareToIgnoreCase);

        // 4. Constructor
        Function<String, StringBuilder> toSB = StringBuilder::new;
        StringBuilder sb = toSB.apply("Hola");

        ////////////////////////////////////////////////////////////////
        // LAMBDAS CON COLLECTIONS
        ////////////////////////////////////////////////////////////////

        List<String> langs = Arrays.asList("Java", "Python", "Go", "JavaScript", "C++");

        // Filtrar con removeIf
        List<String> mutable = new ArrayList<>(langs);
        mutable.removeIf(s -> s.length() <= 2);
        System.out.println(mutable);  // [Java, Python, JavaScript]

        // Ordenar con Comparator
        langs.sort(Comparator.comparingInt(String::length));
        System.out.println(langs);  // [Go, C++, Java, Python, JavaScript]

        // replaceAll
        List<String> upper = new ArrayList<>(langs);
        upper.replaceAll(String::toUpperCase);
        System.out.println(upper);

        // Map forEach
        Map<String, Integer> scores = Map.of("Ana", 90, "Luis", 85);
        scores.forEach((name, score) ->
            System.out.println(name + ": " + score)
        );
    }
}
