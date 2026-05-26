////////////////////////////////////////////////////////////////
// JUNIT 5 — Testing en Java (mini-framework ejecutable)
////////////////////////////////////////////////////////////////

// En un proyecto real, JUnit 5 se añade como dependencia:
//   Maven:  junit-jupiter:5.10+
//   Gradle: testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
//
// Este fichero SIMULA JUnit 5 con un mini-framework propio
// para que puedas ejecutarlo directamente: javac JUnitDemo.java && java JUnitDemo

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class JUnitDemo {

    ////////////////////////////////////////////////////////////////
    // 1. ANNOTATIONS — simulan las de JUnit 5
    ////////////////////////////////////////////////////////////////

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Test {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface DisplayName { String value(); }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface BeforeEach {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface AfterEach {}

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Disabled { String value() default ""; }

    ////////////////////////////////////////////////////////////////
    // 2. ASSERTIONS — simulan org.junit.jupiter.api.Assertions
    ////////////////////////////////////////////////////////////////

    static int assertionCount = 0;

    static void assertEquals(Object expected, Object actual) {
        assertionCount++;
        if (!Objects.equals(expected, actual))
            throw new AssertionError("assertEquals falló: esperado <" + expected + "> pero fue <" + actual + ">");
    }

    static void assertEquals(Object expected, Object actual, String msg) {
        assertionCount++;
        if (!Objects.equals(expected, actual))
            throw new AssertionError(msg + " — esperado <" + expected + "> pero fue <" + actual + ">");
    }

    static void assertEquals(double expected, double actual, double delta) {
        assertionCount++;
        if (Math.abs(expected - actual) > delta)
            throw new AssertionError("assertEquals falló: esperado <" + expected + " ±" + delta + "> pero fue <" + actual + ">");
    }

    static void assertNotEquals(Object unexpected, Object actual) {
        assertionCount++;
        if (Objects.equals(unexpected, actual))
            throw new AssertionError("assertNotEquals falló: no esperaba <" + unexpected + ">");
    }

    static void assertTrue(boolean condition) {
        assertionCount++;
        if (!condition) throw new AssertionError("assertTrue falló: condición fue false");
    }

    static void assertTrue(boolean condition, String msg) {
        assertionCount++;
        if (!condition) throw new AssertionError("assertTrue falló: " + msg);
    }

    static void assertFalse(boolean condition) {
        assertionCount++;
        if (condition) throw new AssertionError("assertFalse falló: condición fue true");
    }

    static void assertNull(Object obj) {
        assertionCount++;
        if (obj != null) throw new AssertionError("assertNull falló: objeto no es null");
    }

    static void assertNotNull(Object obj) {
        assertionCount++;
        if (obj == null) throw new AssertionError("assertNotNull falló: objeto es null");
    }

    @SuppressWarnings("unchecked")
    static <T extends Throwable> T assertThrows(Class<T> expected, Runnable code) {
        assertionCount++;
        try {
            code.run();
            throw new AssertionError("assertThrows falló: se esperaba " + expected.getSimpleName() + " pero no se lanzó nada");
        } catch (Throwable t) {
            if (expected.isInstance(t)) return (T) t;
            throw new AssertionError("assertThrows falló: se esperaba " + expected.getSimpleName() + " pero se lanzó " + t.getClass().getSimpleName());
        }
    }

    // assertAll ejecuta TODAS las assertions y reporta todas las que fallan
    static void assertAll(String heading, Runnable... assertions) {
        List<String> failures = new ArrayList<>();
        for (Runnable r : assertions) {
            try { r.run(); }
            catch (AssertionError e) { failures.add(e.getMessage()); }
        }
        if (!failures.isEmpty()) {
            throw new AssertionError("assertAll '" + heading + "' — " + failures.size()
                + " fallo(s):\n    " + String.join("\n    ", failures));
        }
    }

    // assertTimeout verifica que el código termina dentro del tiempo límite
    static void assertTimeout(long millis, Runnable code) {
        assertionCount++;
        long start = System.currentTimeMillis();
        code.run();
        long elapsed = System.currentTimeMillis() - start;
        if (elapsed > millis)
            throw new AssertionError("assertTimeout falló: límite " + millis + "ms pero tardó " + elapsed + "ms");
    }

    // Error personalizado para assertions (no usar java.lang.AssertionError)
    static class AssertionError extends RuntimeException {
        AssertionError(String msg) { super(msg); }
    }

    ////////////////////////////////////////////////////////////////
    // 3. TEST RUNNER — simula el runner de JUnit 5 con reflection
    ////////////////////////////////////////////////////////////////

    static int totalTests = 0, passed = 0, failed = 0, disabled = 0;

    static void runTests(Class<?>... testClasses) {
        System.out.println("=== Mini JUnit Runner ===\n");

        for (Class<?> clazz : testClasses) {
            System.out.println("▶ " + clazz.getSimpleName());

            // Buscar métodos @BeforeEach y @AfterEach
            Method beforeEach = null, afterEach = null;
            for (Method m : clazz.getDeclaredMethods()) {
                if (m.isAnnotationPresent(BeforeEach.class)) beforeEach = m;
                if (m.isAnnotationPresent(AfterEach.class)) afterEach = m;
            }

            // Ejecutar cada método @Test
            for (Method m : clazz.getDeclaredMethods()) {
                if (!m.isAnnotationPresent(Test.class)) continue;
                totalTests++;

                // Nombre del test
                String name = m.isAnnotationPresent(DisplayName.class)
                    ? m.getAnnotation(DisplayName.class).value()
                    : m.getName();

                // ¿Está desactivado?
                if (m.isAnnotationPresent(Disabled.class)) {
                    String reason = m.getAnnotation(Disabled.class).value();
                    System.out.println("  ○ SKIP: " + name + (reason.isEmpty() ? "" : " (" + reason + ")"));
                    disabled++;
                    continue;
                }

                try {
                    Object instance = clazz.getDeclaredConstructor().newInstance();
                    m.setAccessible(true);
                    if (beforeEach != null) { beforeEach.setAccessible(true); beforeEach.invoke(instance); }
                    m.invoke(instance);
                    if (afterEach != null) { afterEach.setAccessible(true); afterEach.invoke(instance); }
                    System.out.println("  ✓ PASS: " + name);
                    passed++;
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    System.out.println("  ✗ FAIL: " + name + " → " + cause.getMessage());
                    failed++;
                } catch (Exception e) {
                    System.out.println("  ✗ ERROR: " + name + " → " + e.getMessage());
                    failed++;
                }
            }
            System.out.println();
        }

        // Resumen
        System.out.println("════════════════════════════════════");
        System.out.printf("Tests: %d | Pasados: %d | Fallidos: %d | Desactivados: %d%n",
            totalTests, passed, failed, disabled);
        System.out.println("Assertions ejecutadas: " + assertionCount);
        System.out.println("════════════════════════════════════");
        System.out.println(failed == 0 ? "RESULTADO: TODO OK" : "RESULTADO: HAY FALLOS");
    }

    ////////////////////////////////////////////////////////////////
    // CLASE A TESTEAR
    ////////////////////////////////////////////////////////////////

    static class Calculator {
        int add(int a, int b) { return a + b; }
        int subtract(int a, int b) { return a - b; }
        int multiply(int a, int b) { return a * b; }
        double divide(int a, int b) {
            if (b == 0) throw new ArithmeticException("División por cero");
            return (double) a / b;
        }
        boolean isEven(int n) { return n % 2 == 0; }
    }

    ////////////////////////////////////////////////////////////////
    // 4. TESTS: ASSERTIONS BÁSICAS
    ////////////////////////////////////////////////////////////////

    // Simula: @BeforeEach, @AfterEach, @Test, @DisplayName
    static class CalculatorBasicTest {
        Calculator calc;

        @BeforeEach
        void setUp() { calc = new Calculator(); }

        @AfterEach
        void tearDown() { calc = null; }

        @Test
        @DisplayName("Suma de dos positivos")
        void testAdd() {
            assertEquals(5, calc.add(2, 3));
            assertEquals(0, calc.add(-1, 1));
        }

        @Test
        @DisplayName("Resta básica")
        void testSubtract() {
            assertEquals(1, calc.subtract(3, 2));
        }

        @Test
        @DisplayName("Multiplicación")
        void testMultiply() {
            assertEquals(12, calc.multiply(3, 4));
            assertEquals(0, calc.multiply(5, 0));
        }

        @Test
        @DisplayName("Verificar par/impar")
        void testIsEven() {
            assertTrue(calc.isEven(4));
            assertFalse(calc.isEven(3));
            assertTrue(calc.isEven(0), "Cero es par");
        }

        @Disabled("Ejemplo de test desactivado")
        @Test
        void testSkipped() {
            // Este test no se ejecuta
            assertEquals(1, 2);
        }
    }

    ////////////////////////////////////////////////////////////////
    // 5. TESTS: ASSERTTHROWS — verificar excepciones
    ////////////////////////////////////////////////////////////////

    static class CalculatorExceptionTest {

        @Test
        @DisplayName("División por cero lanza ArithmeticException")
        void testDivideByZero() {
            Calculator calc = new Calculator();
            ArithmeticException ex = assertThrows(
                ArithmeticException.class,
                () -> calc.divide(10, 0)
            );
            assertEquals("División por cero", ex.getMessage());
        }

        @Test
        @DisplayName("División normal no lanza excepción")
        void testDivideNormal() {
            // assertDoesNotThrow simulado: si no lanza, pasa
            new Calculator().divide(10, 2);
            assertTrue(true); // llegamos aquí = no hubo excepción
        }
    }

    ////////////////////////////////////////////////////////////////
    // 6. TESTS: ASSERTALL — múltiples assertions agrupadas
    ////////////////////////////////////////////////////////////////

    static class CalculatorGroupedTest {

        @Test
        @DisplayName("Todas las operaciones en un solo assertAll")
        void testAllOperations() {
            Calculator calc = new Calculator();
            // assertAll ejecuta TODAS y reporta todas las que fallan
            assertAll("Operaciones básicas",
                () -> assertEquals(5, calc.add(2, 3)),
                () -> assertEquals(1, calc.subtract(3, 2)),
                () -> assertEquals(6, calc.multiply(2, 3)),
                () -> assertEquals(2.5, calc.divide(5, 2))
            );
        }
    }

    ////////////////////////////////////////////////////////////////
    // 7. TESTS: PARAMETERIZED — mismo test con distintos datos
    ////////////////////////////////////////////////////////////////

    // En JUnit 5 real se usa @ParameterizedTest + @ValueSource/@CsvSource.
    // Aquí lo simulamos con bucles (misma idea).

    static class CalculatorParameterizedTest {

        @Test
        @DisplayName("isEven con múltiples valores (simula @ValueSource)")
        void testIsEvenParameterized() {
            int[] evenNumbers = {2, 4, 6, 8, 100, 0, -2};
            Calculator calc = new Calculator();
            for (int n : evenNumbers) {
                assertTrue(calc.isEven(n), n + " debería ser par");
            }
        }

        @Test
        @DisplayName("Suma parametrizada (simula @CsvSource)")
        void testAddParameterized() {
            // Cada fila: {a, b, resultado_esperado}
            int[][] data = { {1,1,2}, {2,3,5}, {10,20,30}, {-1,1,0}, {0,0,0} };
            Calculator calc = new Calculator();
            for (int[] row : data) {
                assertEquals(row[2], calc.add(row[0], row[1]),
                    row[0] + " + " + row[1] + " debería ser " + row[2]);
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // 8. TESTS: TIMEOUT — limitar tiempo de ejecución
    ////////////////////////////////////////////////////////////////

    static class CalculatorTimeoutTest {

        @Test
        @DisplayName("La suma no tarda más de 100ms")
        void testPerformance() {
            assertTimeout(100, () -> {
                Calculator calc = new Calculator();
                for (int i = 0; i < 1_000_000; i++) calc.add(i, i);
            });
        }
    }

    ////////////////////////////////////////////////////////////////
    // CONVENCIONES Y BUENAS PRÁCTICAS
    ////////////////////////////////////////////////////////////////

    // Estructura de proyecto:
    //   src/main/java/com/app/Calculator.java    → código
    //   src/test/java/com/app/CalculatorTest.java → tests

    // Naming: testMetodo_escenario_resultadoEsperado
    //   testAdd_positiveNumbers_returnsSum
    //   testDivide_byZero_throwsArithmeticException

    // AAA Pattern:
    //   Arrange → preparar datos
    //   Act     → ejecutar operación
    //   Assert  → verificar resultado

    // Ejecución:
    //   Maven:  mvn test
    //   Gradle: gradle test
    //   IDE:    Click derecho → Run Tests

    ////////////////////////////////////////////////////////////////
    // MAIN — ejecutar todos los tests con el mini-runner
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        runTests(
            CalculatorBasicTest.class,
            CalculatorExceptionTest.class,
            CalculatorGroupedTest.class,
            CalculatorParameterizedTest.class,
            CalculatorTimeoutTest.class
        );
    }
}
