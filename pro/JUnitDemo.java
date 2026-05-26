////////////////////////////////////////////////////////////////
// JUNIT 5 — Testing en Java
////////////////////////////////////////////////////////////////

// JUnit 5 requiere las dependencias en el classpath:
//   org.junit.jupiter:junit-jupiter-api
//   org.junit.jupiter:junit-jupiter-engine
//
// Con Maven:
//   <dependency>
//     <groupId>org.junit.jupiter</groupId>
//     <artifactId>junit-jupiter</artifactId>
//     <version>5.10.0</version>
//     <scope>test</scope>
//   </dependency>
//
// Con Gradle:
//   testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
//
// Este fichero es una GUÍA de referencia. Para ejecutar los tests
// se necesita un runner de JUnit (Maven/Gradle/IDE).

// import org.junit.jupiter.api.*;
// import static org.junit.jupiter.api.Assertions.*;

public class JUnitDemo {

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
    // ESTRUCTURA DE UN TEST
    ////////////////////////////////////////////////////////////////

    // @Test                    → marca un método como test
    // @DisplayName("...")      → nombre descriptivo
    // @Disabled("razón")       → desactivar test temporalmente
    //
    // @BeforeEach              → se ejecuta ANTES de cada test
    // @AfterEach               → se ejecuta DESPUÉS de cada test
    // @BeforeAll               → se ejecuta UNA vez antes de todos (static)
    // @AfterAll                → se ejecuta UNA vez después de todos (static)

    // Ejemplo de test class:
    //
    // class CalculatorTest {
    //     Calculator calc;
    //
    //     @BeforeEach
    //     void setUp() {
    //         calc = new Calculator();
    //     }
    //
    //     @Test
    //     @DisplayName("Suma de dos números positivos")
    //     void testAdd() {
    //         assertEquals(5, calc.add(2, 3));
    //     }
    //
    //     @Test
    //     void testSubtract() {
    //         assertEquals(1, calc.subtract(3, 2));
    //     }
    //
    //     @AfterEach
    //     void tearDown() {
    //         calc = null;
    //     }
    // }

    ////////////////////////////////////////////////////////////////
    // ASSERTIONS — verificar resultados
    ////////////////////////////////////////////////////////////////

    // assertEquals(expected, actual)         → igualdad
    // assertEquals(expected, actual, delta)  → doubles con tolerancia
    // assertNotEquals(unexpected, actual)    → desigualdad
    // assertTrue(condition)                  → condición true
    // assertFalse(condition)                 → condición false
    // assertNull(obj)                        → es null
    // assertNotNull(obj)                     → no es null
    // assertSame(expected, actual)           → misma referencia
    // assertArrayEquals(expected, actual)    → arrays iguales

    // Ejemplo:
    // @Test
    // void testAssertions() {
    //     Calculator calc = new Calculator();
    //
    //     assertEquals(4, calc.add(2, 2), "2 + 2 debería ser 4");
    //     assertNotEquals(5, calc.add(2, 2));
    //     assertTrue(calc.isEven(4));
    //     assertFalse(calc.isEven(3));
    //
    //     double result = calc.divide(10, 3);
    //     assertEquals(3.33, result, 0.01); // delta de tolerancia
    // }

    ////////////////////////////////////////////////////////////////
    // ASSERTTHROWS — verificar excepciones
    ////////////////////////////////////////////////////////////////

    // @Test
    // void testDivideByZero() {
    //     Calculator calc = new Calculator();
    //
    //     ArithmeticException ex = assertThrows(
    //         ArithmeticException.class,
    //         () -> calc.divide(10, 0)
    //     );
    //     assertEquals("División por cero", ex.getMessage());
    // }
    //
    // // Verificar que NO lanza excepción
    // @Test
    // void testDivideNormal() {
    //     assertDoesNotThrow(() -> new Calculator().divide(10, 2));
    // }

    ////////////////////////////////////////////////////////////////
    // ASSERTALL — múltiples assertions agrupadas
    ////////////////////////////////////////////////////////////////

    // @Test
    // void testMultipleAssertions() {
    //     Calculator calc = new Calculator();
    //
    //     // Ejecuta TODAS las assertions (no se detiene en la primera falla)
    //     assertAll("Operaciones básicas",
    //         () -> assertEquals(5, calc.add(2, 3)),
    //         () -> assertEquals(1, calc.subtract(3, 2)),
    //         () -> assertEquals(6, calc.multiply(2, 3)),
    //         () -> assertEquals(2.5, calc.divide(5, 2))
    //     );
    // }

    ////////////////////////////////////////////////////////////////
    // PARAMETERIZED TESTS — mismo test con distintos datos
    ////////////////////////////////////////////////////////////////

    // @ParameterizedTest
    // @ValueSource(ints = {2, 4, 6, 8, 100})
    // void testIsEven(int number) {
    //     assertTrue(new Calculator().isEven(number));
    // }
    //
    // @ParameterizedTest
    // @CsvSource({
    //     "1, 1, 2",
    //     "2, 3, 5",
    //     "10, 20, 30",
    //     "-1, 1, 0"
    // })
    // void testAddParameterized(int a, int b, int expected) {
    //     assertEquals(expected, new Calculator().add(a, b));
    // }
    //
    // @ParameterizedTest
    // @MethodSource("addProvider")
    // void testAddWithMethodSource(int a, int b, int expected) {
    //     assertEquals(expected, new Calculator().add(a, b));
    // }
    //
    // static Stream<Arguments> addProvider() {
    //     return Stream.of(
    //         Arguments.of(1, 1, 2),
    //         Arguments.of(0, 0, 0),
    //         Arguments.of(-1, -1, -2)
    //     );
    // }

    ////////////////////////////////////////////////////////////////
    // NESTED TESTS — organizar tests en grupos
    ////////////////////////////////////////////////////////////////

    // @Nested
    // @DisplayName("Tests de suma")
    // class AddTests {
    //     @Test
    //     void positiveNumbers() {
    //         assertEquals(5, new Calculator().add(2, 3));
    //     }
    //
    //     @Test
    //     void negativeNumbers() {
    //         assertEquals(-5, new Calculator().add(-2, -3));
    //     }
    //
    //     @Test
    //     void zero() {
    //         assertEquals(0, new Calculator().add(0, 0));
    //     }
    // }

    ////////////////////////////////////////////////////////////////
    // TIMEOUT — limitar tiempo de ejecución
    ////////////////////////////////////////////////////////////////

    // @Test
    // @Timeout(value = 500, unit = TimeUnit.MILLISECONDS)
    // void testPerformance() {
    //     // Si tarda más de 500ms, falla
    //     new Calculator().add(1, 1);
    // }
    //
    // @Test
    // void testWithAssertTimeout() {
    //     assertTimeout(Duration.ofMillis(100), () -> {
    //         new Calculator().add(1, 1);
    //     });
    // }

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

    // @Test
    // void testAdd_positiveNumbers_returnsSum() {
    //     // Arrange
    //     Calculator calc = new Calculator();
    //     int a = 2, b = 3;
    //
    //     // Act
    //     int result = calc.add(a, b);
    //
    //     // Assert
    //     assertEquals(5, result);
    // }

    ////////////////////////////////////////////////////////////////
    // EJECUTAR TESTS
    ////////////////////////////////////////////////////////////////

    // Maven:  mvn test
    // Gradle: gradle test
    // IDE:    Click derecho → Run Tests

    ////////////////////////////////////////////////////////////////
    // MAIN (demostración sin JUnit)
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {
        Calculator calc = new Calculator();

        // Simulación manual de tests
        System.out.println("=== Tests manuales (sin JUnit) ===\n");

        assert calc.add(2, 3) == 5 : "add falló";
        System.out.println("OK: add(2, 3) = " + calc.add(2, 3));

        assert calc.subtract(5, 3) == 2 : "subtract falló";
        System.out.println("OK: subtract(5, 3) = " + calc.subtract(5, 3));

        assert calc.multiply(3, 4) == 12 : "multiply falló";
        System.out.println("OK: multiply(3, 4) = " + calc.multiply(3, 4));

        assert calc.divide(10, 3) > 3.33 : "divide falló";
        System.out.println("OK: divide(10, 3) = " + calc.divide(10, 3));

        assert calc.isEven(4) : "isEven falló";
        System.out.println("OK: isEven(4) = true");

        try {
            calc.divide(1, 0);
            System.out.println("FAIL: debería haber lanzado excepción");
        } catch (ArithmeticException e) {
            System.out.println("OK: divide(1, 0) lanza ArithmeticException");
        }

        System.out.println("\nTodos los tests pasaron");
        System.out.println("\nPara tests reales usar JUnit 5 con Maven/Gradle");
    }
}
