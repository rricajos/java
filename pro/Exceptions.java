////////////////////////////////////////////////////////////////
// MANEJO DE EXCEPCIONES
////////////////////////////////////////////////////////////////

public class Exceptions {

    ////////////////////////////////////////////////////////////////
    // TRY / CATCH / FINALLY
    ////////////////////////////////////////////////////////////////

    static void basicTryCatch() {
        try {
            int result = 10 / 0;  // ArithmeticException
            System.out.println(result);
        } catch (ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
            // "Error: / by zero"
        } finally {
            // SIEMPRE se ejecuta, haya o no excepción
            System.out.println("Bloque finally ejecutado");
        }
    }

    ////////////////////////////////////////////////////////////////
    // MÚLTIPLES CATCH
    ////////////////////////////////////////////////////////////////

    static void multipleCatch(String input) {
        try {
            int number = Integer.parseInt(input);
            int[] arr = new int[2];
            arr[number] = 42;
        } catch (NumberFormatException e) {
            System.out.println("No es un número válido: " + input);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Índice fuera de rango: " + e.getMessage());
        } catch (Exception e) {
            // Catch genérico — SIEMPRE al final
            System.out.println("Error inesperado: " + e.getMessage());
        }
    }

    // Multi-catch en un solo bloque (Java 7+)
    static void multiCatch(String input) {
        try {
            int num = Integer.parseInt(input);
            int result = 100 / num;
        } catch (NumberFormatException | ArithmeticException e) {
            System.out.println("Error de formato o aritmético: " + e.getMessage());
        }
    }

    ////////////////////////////////////////////////////////////////
    // CHECKED vs UNCHECKED EXCEPTIONS
    ////////////////////////////////////////////////////////////////

    // CHECKED — el compilador obliga a manejarlas (try/catch o throws)
    // → IOException, SQLException, FileNotFoundException
    // → Heredan de Exception (pero no de RuntimeException)

    // UNCHECKED — no obliga el compilador (errores de programación)
    // → NullPointerException, ArrayIndexOutOfBoundsException
    // → Heredan de RuntimeException

    // Ejemplo de checked exception
    static String readFile(String path) throws java.io.IOException {
        // throws declara que este método puede lanzar IOException
        return java.nio.file.Files.readString(java.nio.file.Path.of(path));
    }

    ////////////////////////////////////////////////////////////////
    // THROW — lanzar excepciones manualmente
    ////////////////////////////////////////////////////////////////

    static void validateAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa: " + age);
        }
        if (age > 150) {
            throw new IllegalArgumentException("Edad no realista: " + age);
        }
        System.out.println("Edad válida: " + age);
    }

    ////////////////////////////////////////////////////////////////
    // EXCEPCIONES PERSONALIZADAS
    ////////////////////////////////////////////////////////////////

    // Checked exception personalizada
    static class InsufficientFundsException extends Exception {
        private final double amount;

        public InsufficientFundsException(double amount) {
            super("Fondos insuficientes. Faltan: " + amount);
            this.amount = amount;
        }

        public double getAmount() {
            return amount;
        }
    }

    // Unchecked exception personalizada
    static class InvalidUserException extends RuntimeException {
        public InvalidUserException(String username) {
            super("Usuario inválido: " + username);
        }
    }

    // Uso de excepciones personalizadas
    static void withdraw(double balance, double amount) throws InsufficientFundsException {
        if (amount > balance) {
            throw new InsufficientFundsException(amount - balance);
        }
        System.out.println("Retiro exitoso. Saldo: " + (balance - amount));
    }

    ////////////////////////////////////////////////////////////////
    // TRY-WITH-RESOURCES (Java 7+)
    ////////////////////////////////////////////////////////////////

    // Cierra automáticamente recursos que implementan AutoCloseable
    static void tryWithResources() {
        // El recurso se cierra automáticamente al salir del try
        try (var scanner = new java.util.Scanner(System.in)) {
            // usar scanner...
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        // No necesita finally para cerrar el recurso

        // Múltiples recursos
        // try (var in = new FileInputStream("a.txt");
        //      var out = new FileOutputStream("b.txt")) {
        //     // ...
        // }
    }

    ////////////////////////////////////////////////////////////////
    // JERARQUÍA DE EXCEPCIONES
    ////////////////////////////////////////////////////////////////

    // Throwable
    // ├── Error (no se deben capturar)
    // │   ├── OutOfMemoryError
    // │   ├── StackOverflowError
    // │   └── ...
    // └── Exception
    //     ├── RuntimeException (unchecked)
    //     │   ├── NullPointerException
    //     │   ├── IllegalArgumentException
    //     │   ├── IndexOutOfBoundsException
    //     │   └── ...
    //     ├── IOException (checked)
    //     ├── SQLException (checked)
    //     └── ...

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        // Try/catch básico
        basicTryCatch();

        // Múltiples catch
        multipleCatch("abc");  // NumberFormatException
        multipleCatch("5");    // ArrayIndexOutOfBoundsException

        // Throw y validación
        validateAge(25);       // OK
        // validateAge(-5);    // IllegalArgumentException

        // Excepción personalizada
        try {
            withdraw(100, 150);
        } catch (InsufficientFundsException e) {
            System.out.println(e.getMessage());    // "Fondos insuficientes. Faltan: 50.0"
            System.out.println("Faltan: " + e.getAmount());
        }

        // Checked exception — obligatorio manejar
        try {
            String content = readFile("noexiste.txt");
        } catch (java.io.IOException e) {
            System.out.println("Archivo no encontrado: " + e.getMessage());
        }
    }
}
