////////////////////////////////////////////////////////////////
// COLLECTIONS FRAMEWORK
////////////////////////////////////////////////////////////////

import java.util.*;

public class Collections {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // LIST — ordenada, permite duplicados
        ////////////////////////////////////////////////////////////////

        // ArrayList — acceso rápido por índice, inserción lenta en medio
        List<String> arrayList = new ArrayList<>();
        arrayList.add("Java");
        arrayList.add("Python");
        arrayList.add("Java");  // permite duplicados
        arrayList.add(1, "C++"); // insertar en posición

        System.out.println(arrayList);           // [Java, C++, Python, Java]
        System.out.println(arrayList.get(0));     // "Java"
        System.out.println(arrayList.size());     // 4
        System.out.println(arrayList.contains("Python")); // true
        System.out.println(arrayList.indexOf("Java"));    // 0

        arrayList.set(0, "JavaScript");  // reemplazar
        arrayList.remove("Java");        // elimina la primera ocurrencia
        arrayList.remove(0);             // elimina por índice

        // LinkedList — inserción rápida, acceso por índice lento
        List<String> linkedList = new LinkedList<>();
        linkedList.add("uno");
        linkedList.add("dos");

        // List inmutable (Java 9+)
        List<String> immutable = List.of("a", "b", "c");
        // immutable.add("d"); // UnsupportedOperationException

        ////////////////////////////////////////////////////////////////
        // SET — sin duplicados, sin orden garantizado
        ////////////////////////////////////////////////////////////////

        // HashSet — sin orden, O(1) para add/remove/contains
        Set<String> hashSet = new HashSet<>();
        hashSet.add("Java");
        hashSet.add("Python");
        hashSet.add("Java");  // ignorado, ya existe
        System.out.println(hashSet);         // [Java, Python] (orden no garantizado)
        System.out.println(hashSet.size());  // 2

        // LinkedHashSet — mantiene orden de inserción
        Set<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("C");
        linkedHashSet.add("A");
        linkedHashSet.add("B");
        System.out.println(linkedHashSet);  // [C, A, B] (orden de inserción)

        // TreeSet — ordenado naturalmente (o con Comparator)
        Set<String> treeSet = new TreeSet<>();
        treeSet.add("C");
        treeSet.add("A");
        treeSet.add("B");
        System.out.println(treeSet);  // [A, B, C] (orden alfabético)

        // Set inmutable (Java 9+)
        Set<Integer> immutableSet = Set.of(1, 2, 3);

        ////////////////////////////////////////////////////////////////
        // MAP — pares clave-valor
        ////////////////////////////////////////////////////////////////

        // HashMap — sin orden, O(1) para get/put
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put("Java", 1995);
        hashMap.put("Python", 1991);
        hashMap.put("JavaScript", 1995);

        System.out.println(hashMap.get("Java"));          // 1995
        System.out.println(hashMap.containsKey("Python")); // true
        System.out.println(hashMap.containsValue(1991));   // true
        System.out.println(hashMap.size());                // 3

        // getOrDefault — valor por defecto si no existe la clave
        System.out.println(hashMap.getOrDefault("Go", 0)); // 0

        // putIfAbsent — solo pone si la clave no existe
        hashMap.putIfAbsent("Java", 2000);  // no cambia, Java ya existe

        // Iterar un Map
        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }

        // Solo claves o solo valores
        System.out.println(hashMap.keySet());   // [Java, Python, JavaScript]
        System.out.println(hashMap.values());   // [1995, 1991, 1995]

        // LinkedHashMap — mantiene orden de inserción
        // TreeMap — ordenado por clave

        // Map inmutable (Java 9+)
        Map<String, Integer> immutableMap = Map.of("a", 1, "b", 2, "c", 3);

        ////////////////////////////////////////////////////////////////
        // QUEUE — cola FIFO
        ////////////////////////////////////////////////////////////////

        Queue<String> queue = new LinkedList<>();
        queue.offer("primero");   // añadir al final
        queue.offer("segundo");
        queue.offer("tercero");

        System.out.println(queue.peek());   // "primero" (ver sin quitar)
        System.out.println(queue.poll());   // "primero" (quitar y devolver)
        System.out.println(queue.poll());   // "segundo"

        // PriorityQueue — ordena por prioridad
        Queue<Integer> pq = new PriorityQueue<>();
        pq.offer(30);
        pq.offer(10);
        pq.offer(20);
        System.out.println(pq.poll());  // 10 (el menor primero)

        ////////////////////////////////////////////////////////////////
        // DEQUE — cola doble (puede ser stack o queue)
        ////////////////////////////////////////////////////////////////

        Deque<String> deque = new ArrayDeque<>();

        // Como stack (LIFO)
        deque.push("a");
        deque.push("b");
        deque.push("c");
        System.out.println(deque.pop());   // "c"
        System.out.println(deque.pop());   // "b"

        ////////////////////////////////////////////////////////////////
        // ITERACIÓN
        ////////////////////////////////////////////////////////////////

        List<String> langs = List.of("Java", "Python", "Go");

        // For-each
        for (String lang : langs) {
            System.out.println(lang);
        }

        // forEach con lambda
        langs.forEach(lang -> System.out.println(lang));

        // Iterator (permite eliminar durante iteración)
        List<String> mutable = new ArrayList<>(langs);
        Iterator<String> it = mutable.iterator();
        while (it.hasNext()) {
            String lang = it.next();
            if (lang.equals("Go")) {
                it.remove();  // seguro durante iteración
            }
        }

        ////////////////////////////////////////////////////////////////
        // COLLECTIONS UTILITY CLASS
        ////////////////////////////////////////////////////////////////

        List<Integer> nums = new ArrayList<>(List.of(5, 2, 8, 1, 9));

        java.util.Collections.sort(nums);               // [1, 2, 5, 8, 9]
        java.util.Collections.reverse(nums);             // [9, 8, 5, 2, 1]
        java.util.Collections.shuffle(nums);             // orden aleatorio
        System.out.println(java.util.Collections.max(nums));  // 9
        System.out.println(java.util.Collections.min(nums));  // 1
        System.out.println(java.util.Collections.frequency(nums, 5)); // 1

        // Lista inmutable desde una existente
        List<Integer> unmodifiable = java.util.Collections.unmodifiableList(nums);
    }
}
