////////////////////////////////////////////////////////////////
// CONTROL DE FLUJO
////////////////////////////////////////////////////////////////

public class ControlFlow {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // IF / ELSE IF / ELSE
        ////////////////////////////////////////////////////////////////

        int score = 85;

        if (score >= 90) {
            System.out.println("Excelente");
        } else if (score >= 70) {
            System.out.println("Notable");
        } else if (score >= 50) {
            System.out.println("Aprobado");
        } else {
            System.out.println("Suspendido");
        }
        // → "Notable"

        ////////////////////////////////////////////////////////////////
        // FOR CLÁSICO
        ////////////////////////////////////////////////////////////////

        // for (inicialización; condición; actualización)
        for (int i = 0; i < 5; i++) {
            System.out.print(i + " ");  // 0 1 2 3 4
        }
        System.out.println();

        // Bucle descendente
        for (int i = 10; i > 0; i -= 2) {
            System.out.print(i + " ");  // 10 8 6 4 2
        }
        System.out.println();

        ////////////////////////////////////////////////////////////////
        // FOR-EACH (enhanced for)
        ////////////////////////////////////////////////////////////////

        String[] fruits = {"manzana", "banana", "naranja"};

        for (String fruit : fruits) {
            System.out.println(fruit);
        }
        // No da acceso al índice directamente

        ////////////////////////////////////////////////////////////////
        // WHILE
        ////////////////////////////////////////////////////////////////

        int count = 0;

        while (count < 3) {
            System.out.println("count = " + count);
            count++;
        }
        // Se ejecuta 0, 1, 2 veces y sale cuando count == 3

        ////////////////////////////////////////////////////////////////
        // DO-WHILE
        ////////////////////////////////////////////////////////////////

        // Se ejecuta AL MENOS una vez (comprueba la condición al final)
        int num = 10;

        do {
            System.out.println("num = " + num);
            num++;
        } while (num < 3);
        // Imprime "num = 10" una vez, aunque 10 < 3 es false

        ////////////////////////////////////////////////////////////////
        // BREAK — salir del bucle
        ////////////////////////////////////////////////////////////////

        for (int i = 0; i < 100; i++) {
            if (i == 5) {
                break;  // sale del bucle
            }
            System.out.print(i + " ");  // 0 1 2 3 4
        }
        System.out.println();

        ////////////////////////////////////////////////////////////////
        // CONTINUE — saltar a la siguiente iteración
        ////////////////////////////////////////////////////////////////

        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                continue;  // salta los pares
            }
            System.out.print(i + " ");  // 1 3 5 7 9
        }
        System.out.println();

        ////////////////////////////////////////////////////////////////
        // LABELED BREAK / CONTINUE (bucles anidados)
        ////////////////////////////////////////////////////////////////

        // Label permite salir de un bucle externo desde uno interno
        outer:
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (i == 1 && j == 1) {
                    break outer;  // sale de AMBOS bucles
                }
                System.out.println("i=" + i + " j=" + j);
            }
        }
        // i=0 j=0, i=0 j=1, i=0 j=2, i=1 j=0 → se detiene

        ////////////////////////////////////////////////////////////////
        // FOR CON MÚLTIPLES VARIABLES
        ////////////////////////////////////////////////////////////////

        for (int i = 0, j = 10; i < j; i++, j--) {
            System.out.println("i=" + i + " j=" + j);
        }
        // i=0 j=10, i=1 j=9, ... i=4 j=6

        ////////////////////////////////////////////////////////////////
        // BUCLE INFINITO (útil con break condicional)
        ////////////////////////////////////////////////////////////////

        // while (true) {
        //     // leer input...
        //     if (condicionDeSalida) break;
        // }

        // for (;;) {
        //     // equivalente a while(true)
        // }
    }
}
