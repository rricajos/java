////////////////////////////////////////////////////////////////
// VARIABLES Y TIPOS DE DATOS EN JAVA
////////////////////////////////////////////////////////////////

// Java es un lenguaje fuertemente tipado: cada variable
// debe tener un tipo declarado en tiempo de compilación.

public class VariablesAndTypes {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // TIPOS PRIMITIVOS (8 tipos)
        ////////////////////////////////////////////////////////////////

        // Enteros
        byte b = 127;                  // 8 bits  → -128 a 127
        short s = 32767;               // 16 bits → -32768 a 32767
        int i = 2_147_483_647;         // 32 bits (el más usado)
        long l = 9_223_372_036_854L;   // 64 bits (sufijo L)

        // Decimales
        float f = 3.14f;              // 32 bits (sufijo f obligatorio)
        double d = 3.141592653589;    // 64 bits (por defecto)

        // Carácter y booleano
        char c = 'A';                 // 16 bits Unicode
        boolean flag = true;          // true o false

        ////////////////////////////////////////////////////////////////
        // TIPO STRING (no es primitivo, es un objeto)
        ////////////////////////////////////////////////////////////////

        String name = "Java";
        String greeting = "Hola " + name;     // concatenación
        String multi = """
                Texto multilínea
                disponible desde Java 13
                (text blocks)
                """;

        // Strings son inmutables
        String a = "hello";
        String aUpper = a.toUpperCase(); // "HELLO" — a sigue siendo "hello"

        ////////////////////////////////////////////////////////////////
        // VAR (inferencia de tipos - Java 10+)
        ////////////////////////////////////////////////////////////////

        // El compilador infiere el tipo a partir del valor asignado
        var number = 42;               // int
        var text = "inferido";         // String
        var list = new java.util.ArrayList<String>(); // ArrayList<String>

        // var solo se puede usar en variables locales, no en campos de clase

        ////////////////////////////////////////////////////////////////
        // CASTING (conversión de tipos)
        ////////////////////////////////////////////////////////////////

        // Widening (implícito) — de menor a mayor, sin pérdida
        int myInt = 100;
        double myDouble = myInt;       // 100.0 automático

        // Narrowing (explícito) — de mayor a menor, posible pérdida
        double pi = 3.14159;
        int truncated = (int) pi;      // 3 — se pierde la parte decimal

        // Parsing desde String
        int parsed = Integer.parseInt("42");
        double parsedD = Double.parseDouble("3.14");
        String back = String.valueOf(42);  // "42"

        ////////////////////////////////////////////////////////////////
        // WRAPPER CLASSES (tipos primitivos como objetos)
        ////////////////////////////////////////////////////////////////

        // Cada primitivo tiene su clase envolvente
        Integer objInt = 42;           // autoboxing: int → Integer
        int primInt = objInt;          // unboxing:   Integer → int

        // Byte, Short, Integer, Long, Float, Double, Character, Boolean
        // Útiles para Collections (que no aceptan primitivos)

        ////////////////////////////////////////////////////////////////
        // CONSTANTES
        ////////////////////////////////////////////////////////////////

        // Se usa final para declarar constantes (no se pueden reasignar)
        final int MAX_SIZE = 100;
        // MAX_SIZE = 200; // Error: cannot assign a value to final variable

        // Convención: UPPER_SNAKE_CASE para constantes

        ////////////////////////////////////////////////////////////////
        // VALORES POR DEFECTO (solo en campos de clase, no en locales)
        ////////////////////////////////////////////////////////////////

        // int     → 0
        // double  → 0.0
        // boolean → false
        // char    → '\u0000'
        // String  → null (cualquier objeto)

        ////////////////////////////////////////////////////////////////
        // TYPEOF EQUIVALENTE — instanceof y getClass()
        ////////////////////////////////////////////////////////////////

        String str = "test";
        System.out.println(str instanceof String);   // true
        System.out.println(str.getClass().getName()); // java.lang.String

        // Para primitivos no existe instanceof, pero se puede usar
        // los wrappers y reflexión si es necesario

        ////////////////////////////////////////////////////////////////
        // COMPARACIÓN DE STRINGS
        ////////////////////////////////////////////////////////////////

        String s1 = "hello";
        String s2 = "hello";
        String s3 = new String("hello");

        System.out.println(s1 == s2);         // true  (mismo literal en el pool)
        System.out.println(s1 == s3);         // false (distinta referencia)
        System.out.println(s1.equals(s3));    // true  (mismo contenido)

        // SIEMPRE usar .equals() para comparar Strings, nunca ==
    }
}
