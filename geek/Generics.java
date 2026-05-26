////////////////////////////////////////////////////////////////
// GENERICS
////////////////////////////////////////////////////////////////

import java.util.*;

public class Generics {

    ////////////////////////////////////////////////////////////////
    // CLASE GENÉRICA
    ////////////////////////////////////////////////////////////////

    // T es un parámetro de tipo — se reemplaza al instanciar
    static class Box<T> {
        private T content;

        public Box(T content) {
            this.content = content;
        }

        public T getContent() {
            return content;
        }

        public void setContent(T content) {
            this.content = content;
        }
    }

    // Múltiples parámetros de tipo
    static class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }
        public V getValue() { return value; }

        @Override
        public String toString() {
            return key + " = " + value;
        }
    }

    ////////////////////////////////////////////////////////////////
    // MÉTODOS GENÉRICOS
    ////////////////////////////////////////////////////////////////

    // El tipo <T> se declara antes del tipo de retorno
    static <T> void printArray(T[] array) {
        for (T element : array) {
            System.out.print(element + " ");
        }
        System.out.println();
    }

    // Método genérico que devuelve un tipo genérico
    static <T> List<T> arrayToList(T[] array) {
        List<T> list = new ArrayList<>();
        for (T element : array) {
            list.add(element);
        }
        return list;
    }

    ////////////////////////////////////////////////////////////////
    // BOUNDED TYPES (tipos acotados)
    ////////////////////////////////////////////////////////////////

    // Upper bound — T debe ser Number o subclase de Number
    static <T extends Number> double sum(List<T> numbers) {
        double total = 0;
        for (T num : numbers) {
            total += num.doubleValue();
        }
        return total;
    }

    // Multiple bounds — T debe implementar varias interfaces
    static <T extends Comparable<T> & java.io.Serializable> T findMax(List<T> list) {
        T max = list.get(0);
        for (T item : list) {
            if (item.compareTo(max) > 0) {
                max = item;
            }
        }
        return max;
    }

    ////////////////////////////////////////////////////////////////
    // WILDCARDS (comodines)
    ////////////////////////////////////////////////////////////////

    // ? — wildcard sin acotar (cualquier tipo)
    static void printList(List<?> list) {
        for (Object item : list) {
            System.out.print(item + " ");
        }
        System.out.println();
    }

    // ? extends T — upper bounded wildcard (lectura)
    // Acepta Number y cualquier subclase (Integer, Double...)
    static double sumWildcard(List<? extends Number> numbers) {
        double total = 0;
        for (Number num : numbers) {
            total += num.doubleValue();
        }
        return total;
    }

    // ? super T — lower bounded wildcard (escritura)
    // Acepta Integer y cualquier superclase (Number, Object)
    static void addIntegers(List<? super Integer> list) {
        list.add(1);
        list.add(2);
        list.add(3);
    }

    // PECS: Producer Extends, Consumer Super
    // Si LEES de la lista     → extends (producer)
    // Si ESCRIBES en la lista → super   (consumer)

    ////////////////////////////////////////////////////////////////
    // INTERFAZ GENÉRICA
    ////////////////////////////////////////////////////////////////

    interface Repository<T> {
        void save(T entity);
        T findById(int id);
        List<T> findAll();
    }

    // Implementar con tipo concreto
    static class UserRepository implements Repository<String> {
        private List<String> users = new ArrayList<>();

        @Override
        public void save(String user) { users.add(user); }

        @Override
        public String findById(int id) { return users.get(id); }

        @Override
        public List<String> findAll() { return users; }
    }

    ////////////////////////////////////////////////////////////////
    // TYPE ERASURE — cómo funcionan los generics internamente
    ////////////////////////////////////////////////////////////////

    // Los generics solo existen en tiempo de compilación
    // En runtime, List<String> y List<Integer> son ambos List
    // Por eso no se puede hacer:
    //   new T()          — no se conoce T en runtime
    //   instanceof T     — T no existe en runtime
    //   new T[10]        — no se puede crear array de tipo genérico

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        // Clase genérica
        Box<String> stringBox = new Box<>("Hola");
        Box<Integer> intBox = new Box<>(42);
        System.out.println(stringBox.getContent()); // "Hola"
        System.out.println(intBox.getContent());     // 42

        // Diamond operator (<>) — infiere el tipo
        Box<Double> doubleBox = new Box<>(3.14);

        // Pair
        Pair<String, Integer> pair = new Pair<>("Java", 1995);
        System.out.println(pair); // "Java = 1995"

        // Métodos genéricos
        String[] names = {"Ana", "Luis", "Eva"};
        Integer[] nums = {1, 2, 3};
        printArray(names);  // Ana Luis Eva
        printArray(nums);   // 1 2 3

        // Bounded types
        List<Integer> ints = List.of(10, 20, 30);
        List<Double> doubles = List.of(1.5, 2.5, 3.5);
        System.out.println(sum(ints));     // 60.0
        System.out.println(sum(doubles));  // 7.5

        // Wildcards
        printList(ints);     // funciona con List<Integer>
        printList(doubles);  // funciona con List<Double>

        System.out.println(sumWildcard(ints));     // 60.0
        System.out.println(sumWildcard(doubles));  // 7.5

        List<Number> numberList = new ArrayList<>();
        addIntegers(numberList);  // ? super Integer acepta List<Number>
        System.out.println(numberList); // [1, 2, 3]
    }
}
