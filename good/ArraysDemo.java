////////////////////////////////////////////////////////////////
// ARRAYS EN JAVA
////////////////////////////////////////////////////////////////

import java.util.Arrays;

public class ArraysDemo {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // DECLARACIÓN E INICIALIZACIÓN
        ////////////////////////////////////////////////////////////////

        // Declarar con tamaño (valores por defecto: 0, null, false)
        int[] numbers = new int[5];

        // Declarar con valores
        int[] primes = {2, 3, 5, 7, 11};

        // Declarar y luego asignar
        String[] names;
        names = new String[]{"Ana", "Luis", "Eva"};

        // Los arrays tienen tamaño FIJO — no se puede cambiar después

        ////////////////////////////////////////////////////////////////
        // ACCESO Y MODIFICACIÓN
        ////////////////////////////////////////////////////////////////

        System.out.println(primes[0]);     // 2 (primer elemento)
        System.out.println(primes[4]);     // 11 (último elemento)
        System.out.println(primes.length); // 5 (propiedad, no método)

        primes[0] = 1;  // modificar elemento
        // primes[5] = 13; // ArrayIndexOutOfBoundsException

        ////////////////////////////////////////////////////////////////
        // ITERACIÓN
        ////////////////////////////////////////////////////////////////

        // For clásico
        for (int i = 0; i < primes.length; i++) {
            System.out.print(primes[i] + " ");
        }
        System.out.println();

        // For-each (no acceso al índice)
        for (int prime : primes) {
            System.out.print(prime + " ");
        }
        System.out.println();

        ////////////////////////////////////////////////////////////////
        // ARRAYS UTILITY CLASS — java.util.Arrays
        ////////////////////////////////////////////////////////////////

        int[] arr = {5, 2, 8, 1, 9, 3};

        // Ordenar
        Arrays.sort(arr);
        System.out.println(Arrays.toString(arr));  // [1, 2, 3, 5, 8, 9]

        // Buscar (requiere array ordenado)
        int index = Arrays.binarySearch(arr, 5);
        System.out.println("5 está en índice: " + index);  // 3

        // Llenar con un valor
        int[] filled = new int[5];
        Arrays.fill(filled, 42);
        System.out.println(Arrays.toString(filled)); // [42, 42, 42, 42, 42]

        // Copiar
        int[] copy = Arrays.copyOf(arr, arr.length);
        int[] partial = Arrays.copyOfRange(arr, 1, 4); // [2, 3, 5]

        // Comparar
        System.out.println(Arrays.equals(arr, copy));  // true

        ////////////////////////////////////////////////////////////////
        // ARRAYS MULTIDIMENSIONALES
        ////////////////////////////////////////////////////////////////

        // Matriz 3x3
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        System.out.println(matrix[1][2]);  // 6 (fila 1, columna 2)

        // Iterar matriz
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        // Arrays irregulares (jagged arrays)
        int[][] jagged = new int[3][];
        jagged[0] = new int[]{1, 2};
        jagged[1] = new int[]{3, 4, 5};
        jagged[2] = new int[]{6};

        // deepToString para arrays multidimensionales
        System.out.println(Arrays.deepToString(matrix));
        // [[1, 2, 3], [4, 5, 6], [7, 8, 9]]

        ////////////////////////////////////////////////////////////////
        // CONVERSIÓN ARRAY ↔ LISTA
        ////////////////////////////////////////////////////////////////

        // Array → List (vista fija, no modificable en tamaño)
        java.util.List<String> list = Arrays.asList("a", "b", "c");

        // List → Array
        String[] backToArray = list.toArray(new String[0]);

        // Array → List mutable (Java 9+)
        java.util.List<String> mutable = new java.util.ArrayList<>(Arrays.asList("x", "y"));
        mutable.add("z");  // OK

        ////////////////////////////////////////////////////////////////
        // PATRONES ÚTILES
        ////////////////////////////////////////////////////////////////

        // Encontrar el máximo
        int max = arr[0];
        for (int val : arr) {
            if (val > max) max = val;
        }
        System.out.println("Máximo: " + max);

        // Invertir un array
        int[] original = {1, 2, 3, 4, 5};
        int[] reversed = new int[original.length];
        for (int i = 0; i < original.length; i++) {
            reversed[i] = original[original.length - 1 - i];
        }
        System.out.println(Arrays.toString(reversed)); // [5, 4, 3, 2, 1]

        // Eliminar duplicados (con Streams)
        int[] withDups = {1, 2, 2, 3, 3, 3, 4};
        int[] unique = Arrays.stream(withDups).distinct().toArray();
        System.out.println(Arrays.toString(unique));  // [1, 2, 3, 4]
    }
}
