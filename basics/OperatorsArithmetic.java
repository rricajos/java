////////////////////////////////////////////////////////////////
// OPERADORES ARITMÉTICOS
////////////////////////////////////////////////////////////////

public class OperatorsArithmetic {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // OPERADORES BÁSICOS
        ////////////////////////////////////////////////////////////////

        int a = 10, b = 3;

        System.out.println(a + b);   // 13  — suma
        System.out.println(a - b);   // 7   — resta
        System.out.println(a * b);   // 30  — multiplicación
        System.out.println(a / b);   // 3   — división entera (ambos int)
        System.out.println(a % b);   // 1   — módulo (resto)

        ////////////////////////////////////////////////////////////////
        // DIVISIÓN ENTERA vs DECIMAL
        ////////////////////////////////////////////////////////////////

        // Si ambos operandos son int, el resultado es int (truncado)
        System.out.println(10 / 3);       // 3

        // Si al menos uno es double, el resultado es double
        System.out.println(10.0 / 3);     // 3.3333...
        System.out.println(10 / 3.0);     // 3.3333...
        System.out.println((double) 10 / 3); // 3.3333... (cast explícito)

        ////////////////////////////////////////////////////////////////
        // INCREMENTO Y DECREMENTO
        ////////////////////////////////////////////////////////////////

        int x = 5;

        // Postfijo: usa el valor actual y DESPUÉS incrementa/decrementa
        System.out.println(x++);  // 5 (imprime 5, luego x = 6)
        System.out.println(x);    // 6

        // Prefijo: incrementa/decrementa ANTES de usar el valor
        System.out.println(++x);  // 7 (incrementa primero, luego imprime 7)
        System.out.println(x);    // 7

        // Lo mismo aplica para --
        System.out.println(x--);  // 7 (imprime 7, luego x = 6)
        System.out.println(--x);  // 5 (decrementa primero, luego imprime 5)

        ////////////////////////////////////////////////////////////////
        // OVERFLOW Y UNDERFLOW
        ////////////////////////////////////////////////////////////////

        // Java NO lanza error por overflow, simplemente da la vuelta
        int maxInt = Integer.MAX_VALUE;     // 2147483647
        System.out.println(maxInt + 1);     // -2147483648 (overflow!)

        int minInt = Integer.MIN_VALUE;     // -2147483648
        System.out.println(minInt - 1);     // 2147483647 (underflow!)

        // Para detectar overflow usar Math.addExact()
        // Math.addExact(maxInt, 1); // ArithmeticException

        ////////////////////////////////////////////////////////////////
        // MATH CLASS — operaciones comunes
        ////////////////////////////////////////////////////////////////

        System.out.println(Math.abs(-42));       // 42
        System.out.println(Math.max(10, 20));    // 20
        System.out.println(Math.min(10, 20));    // 10
        System.out.println(Math.pow(2, 10));     // 1024.0
        System.out.println(Math.sqrt(144));      // 12.0
        System.out.println(Math.round(3.7));     // 4
        System.out.println(Math.ceil(3.1));      // 4.0
        System.out.println(Math.floor(3.9));     // 3.0
        System.out.println(Math.random());       // 0.0 a 0.999...
    }
}
