////////////////////////////////////////////////////////////////
// PACKAGES Y MODIFICADORES DE ACCESO
////////////////////////////////////////////////////////////////

// package com.example.myapp;  // declaración de paquete (primera línea del fichero)

// import java.util.List;           // importar clase específica
// import java.util.*;              // importar todas las clases del paquete
// import static java.lang.Math.*;  // importar métodos estáticos

public class PackagesAndAccess {

    ////////////////////////////////////////////////////////////////
    // MODIFICADORES DE ACCESO — 4 niveles
    ////////////////////////////////////////////////////////////////

    // De más restrictivo a menos restrictivo:

    private String privateField = "private";
    // Solo accesible DENTRO de esta clase
    // Ni siquiera las subclases pueden acceder

    String packageField = "package-private";
    // Sin modificador = package-private (default)
    // Accesible por todas las clases del MISMO paquete

    protected String protectedField = "protected";
    // Accesible por el mismo paquete + subclases (incluso en otro paquete)

    public String publicField = "public";
    // Accesible desde CUALQUIER lugar

    ////////////////////////////////////////////////////////////////
    // TABLA RESUMEN
    ////////////////////////////////////////////////////////////////

    //                    | misma clase | mismo paquete | subclase | cualquier lugar
    // -------------------|-------------|---------------|----------|----------------
    // private            |     ✓      |       ✗       |    ✗     |       ✗
    // package-private    |     ✓      |       ✓       |    ✗     |       ✗
    // protected          |     ✓      |       ✓       |    ✓     |       ✗
    // public             |     ✓      |       ✓       |    ✓     |       ✓

    ////////////////////////////////////////////////////////////////
    // MODIFICADORES EN CLASES
    ////////////////////////////////////////////////////////////////

    // public class → accesible desde cualquier paquete
    // (sin modificador) class → solo accesible dentro del mismo paquete
    // No se puede usar private ni protected en clases top-level

    // public class MiClase { }        → cualquier paquete
    // class ClaseInterna { }          → solo este paquete

    ////////////////////////////////////////////////////////////////
    // ENCAPSULACIÓN — patrón getter/setter
    ////////////////////////////////////////////////////////////////

    static class BankAccount {
        // Campos privados — encapsulación
        private String owner;
        private double balance;

        public BankAccount(String owner, double initialBalance) {
            this.owner = owner;
            this.balance = initialBalance;
        }

        // Getter público — lectura controlada
        public String getOwner() {
            return owner;
        }

        public double getBalance() {
            return balance;
        }

        // No hay setBalance público — solo se modifica via deposit/withdraw
        // Esto es encapsulación: controlar CÓMO se accede al estado

        public void deposit(double amount) {
            if (amount <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");
            balance += amount;
        }

        public boolean withdraw(double amount) {
            if (amount <= 0) throw new IllegalArgumentException("Cantidad debe ser positiva");
            if (amount > balance) return false;
            balance -= amount;
            return true;
        }
    }

    ////////////////////////////////////////////////////////////////
    // PAQUETES — estructura de directorios
    ////////////////////////////////////////////////////////////////

    // Convención: dominio invertido
    // com.empresa.proyecto.modulo
    //
    // Estructura de directorios:
    // src/
    // └── com/
    //     └── empresa/
    //         └── proyecto/
    //             ├── model/
    //             │   ├── User.java       → package com.empresa.proyecto.model;
    //             │   └── Product.java
    //             ├── service/
    //             │   ├── UserService.java → package com.empresa.proyecto.service;
    //             │   └── ProductService.java
    //             └── Main.java           → package com.empresa.proyecto;

    ////////////////////////////////////////////////////////////////
    // IMPORTS
    ////////////////////////////////////////////////////////////////

    // Importar clase específica (preferida)
    // import java.util.ArrayList;

    // Importar todo el paquete (menos preciso)
    // import java.util.*;

    // Import estático — importar métodos/constantes estáticos
    // import static java.lang.Math.PI;
    // import static java.lang.Math.sqrt;
    // Permite escribir: sqrt(PI) en vez de Math.sqrt(Math.PI)

    // java.lang.* se importa automáticamente (String, Math, System, etc.)

    ////////////////////////////////////////////////////////////////
    // MODIFICADORES ADICIONALES (no de acceso)
    ////////////////////////////////////////////////////////////////

    // final — no se puede sobrescribir/heredar/reasignar
    static final double PI = 3.14159;         // constante
    // final class → no se puede heredar
    // final method → no se puede sobrescribir

    // static — pertenece a la clase, no a la instancia
    static int instanceCount = 0;

    // abstract — sin implementación, las subclases deben implementar
    // abstract void doSomething();

    // synchronized — solo un thread a la vez
    // volatile — visibilidad entre threads

    // transient — excluido de serialización
    // strictfp — aritmética IEEE 754 estricta (raro)

    ////////////////////////////////////////////////////////////////
    // COMBINACIONES COMUNES
    ////////////////////////////////////////////////////////////////

    // public static final → constante pública
    public static final String APP_NAME = "MyApp";

    // private static final → constante interna
    private static final int MAX_RETRIES = 3;

    // public static → método utilitario (como Math.abs)
    public static int abs(int n) {
        return n < 0 ? -n : n;
    }

    // private → helper interno
    private static boolean isValid(String s) {
        return s != null && !s.isBlank();
    }

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        // Encapsulación en acción
        BankAccount account = new BankAccount("Ana", 1000);
        System.out.println("Titular: " + account.getOwner());
        System.out.println("Saldo: " + account.getBalance());

        account.deposit(500);
        System.out.println("Tras depósito: " + account.getBalance());

        boolean ok = account.withdraw(200);
        System.out.println("Retiro exitoso: " + ok);
        System.out.println("Saldo final: " + account.getBalance());

        // account.balance = 999999; // Error: balance es private

        // Acceso a constantes
        System.out.println("App: " + APP_NAME);
        System.out.println("PI: " + PI);

        // Modificadores en acción
        System.out.println("abs(-42): " + abs(-42));
        System.out.println("isValid('test'): " + isValid("test"));
    }
}
