////////////////////////////////////////////////////////////////
// MÉTODOS EN JAVA
////////////////////////////////////////////////////////////////

public class Methods {

    ////////////////////////////////////////////////////////////////
    // DECLARACIÓN BÁSICA
    ////////////////////////////////////////////////////////////////

    // modificador tipoRetorno nombre(parámetros) { cuerpo }
    static int sum(int a, int b) {
        return a + b;
    }

    // void — no devuelve nada
    static void greet(String name) {
        System.out.println("Hola, " + name);
    }

    ////////////////////////////////////////////////////////////////
    // SOBRECARGA DE MÉTODOS (overloading)
    ////////////////////////////////////////////////////////////////

    // Mismo nombre, distintos parámetros (tipo o cantidad)
    static int add(int a, int b) {
        return a + b;
    }

    static double add(double a, double b) {
        return a + b;
    }

    static int add(int a, int b, int c) {
        return a + b + c;
    }

    // Java elige el método correcto según los argumentos proporcionados

    ////////////////////////////////////////////////////////////////
    // VARARGS (argumentos variables)
    ////////////////////////////////////////////////////////////////

    // Los ... permiten pasar 0 o más argumentos del mismo tipo
    static int sumAll(int... numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }
    // sumAll(1, 2, 3)    → 6
    // sumAll(1, 2, 3, 4) → 10
    // sumAll()            → 0

    // Varargs debe ser el ÚLTIMO parámetro
    static void log(String prefix, String... messages) {
        for (String msg : messages) {
            System.out.println(prefix + ": " + msg);
        }
    }

    ////////////////////////////////////////////////////////////////
    // PASO POR VALOR
    ////////////////////////////////////////////////////////////////

    // Java SIEMPRE pasa por valor
    // Primitivos: se copia el valor
    static void changeValue(int x) {
        x = 100;  // solo modifica la copia local
    }

    // Objetos: se copia la REFERENCIA (no el objeto)
    static void changeArray(int[] arr) {
        arr[0] = 999;  // modifica el objeto original (misma referencia)
    }

    static void reassignArray(int[] arr) {
        arr = new int[]{1, 2, 3};  // solo reasigna la copia local
    }

    ////////////////////////////////////////////////////////////////
    // RECURSIÓN
    ////////////////////////////////////////////////////////////////

    static int factorial(int n) {
        if (n <= 1) return 1;         // caso base
        return n * factorial(n - 1);  // caso recursivo
    }
    // factorial(5) → 5 * 4 * 3 * 2 * 1 = 120

    static int fibonacci(int n) {
        if (n <= 0) return 0;
        if (n == 1) return 1;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    ////////////////////////////////////////////////////////////////
    // MAIN — punto de entrada
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        // Llamadas a métodos
        System.out.println(sum(3, 5));        // 8
        greet("Java");                         // "Hola, Java"

        // Sobrecarga
        System.out.println(add(2, 3));         // 5 (int)
        System.out.println(add(2.5, 3.5));     // 6.0 (double)
        System.out.println(add(1, 2, 3));      // 6 (tres ints)

        // Varargs
        System.out.println(sumAll(1, 2, 3, 4, 5)); // 15

        // Paso por valor — primitivos
        int val = 42;
        changeValue(val);
        System.out.println(val);  // 42 (no cambia)

        // Paso por valor — objetos
        int[] arr = {10, 20, 30};
        changeArray(arr);
        System.out.println(arr[0]);  // 999 (sí cambia el contenido)

        reassignArray(arr);
        System.out.println(arr[0]);  // 999 (la reasignación no afecta)

        // Recursión
        System.out.println(factorial(5));     // 120
        System.out.println(fibonacci(10));    // 55
    }
}
