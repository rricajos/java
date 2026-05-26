////////////////////////////////////////////////////////////////
// OPTIONAL — evitar NullPointerException (Java 8+)
////////////////////////////////////////////////////////////////

import java.util.*;

public class OptionalDemo {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // CREAR OPTIONALS
        ////////////////////////////////////////////////////////////////

        // of — valor que SABES que no es null
        Optional<String> present = Optional.of("Hola");

        // ofNullable — valor que PODRÍA ser null
        String nullValue = null;
        Optional<String> maybe = Optional.ofNullable(nullValue);

        // empty — Optional vacío explícito
        Optional<String> empty = Optional.empty();

        System.out.println(present);  // Optional[Hola]
        System.out.println(maybe);    // Optional.empty
        System.out.println(empty);    // Optional.empty

        ////////////////////////////////////////////////////////////////
        // COMPROBAR Y OBTENER VALOR
        ////////////////////////////////////////////////////////////////

        // isPresent / isEmpty (isEmpty es Java 11+)
        System.out.println(present.isPresent());  // true
        System.out.println(empty.isPresent());    // false
        System.out.println(empty.isEmpty());      // true

        // get — obtener valor (lanza NoSuchElementException si vacío)
        String value = present.get();  // "Hola"
        // empty.get(); // NoSuchElementException — NUNCA usar sin isPresent

        ////////////////////////////////////////////////////////////////
        // VALORES POR DEFECTO
        ////////////////////////////////////////////////////////////////

        // orElse — valor por defecto si vacío
        String result1 = maybe.orElse("valor por defecto");
        System.out.println(result1);  // "valor por defecto"

        // orElseGet — valor por defecto con Supplier (lazy)
        String result2 = maybe.orElseGet(() -> "calculado bajo demanda");
        System.out.println(result2);

        // orElseThrow — lanzar excepción si vacío
        // maybe.orElseThrow(() -> new IllegalStateException("Valor requerido"));

        // orElseThrow sin argumento (Java 10+) — lanza NoSuchElementException
        // maybe.orElseThrow();

        // or — devolver otro Optional si vacío (Java 9+)
        Optional<String> fallback = maybe.or(() -> Optional.of("fallback"));
        System.out.println(fallback);  // Optional[fallback]

        ////////////////////////////////////////////////////////////////
        // TRANSFORMAR CON MAP Y FLATMAP
        ////////////////////////////////////////////////////////////////

        Optional<String> name = Optional.of("Java");

        // map — transformar el valor si presente
        Optional<String> upper = name.map(String::toUpperCase);
        System.out.println(upper);  // Optional[JAVA]

        Optional<Integer> length = name.map(String::length);
        System.out.println(length);  // Optional[4]

        // map sobre vacío devuelve vacío (no ejecuta la función)
        Optional<String> emptyMapped = empty.map(String::toUpperCase);
        System.out.println(emptyMapped);  // Optional.empty

        // flatMap — cuando la función ya devuelve Optional
        Optional<String> flatResult = name.flatMap(n ->
            n.length() > 3 ? Optional.of(n.substring(0, 3)) : Optional.empty()
        );
        System.out.println(flatResult);  // Optional[Jav]

        ////////////////////////////////////////////////////////////////
        // FILTRAR
        ////////////////////////////////////////////////////////////////

        Optional<String> lang = Optional.of("Java");

        // filter — mantiene el valor si cumple el predicado
        Optional<String> filtered = lang.filter(s -> s.startsWith("J"));
        System.out.println(filtered);  // Optional[Java]

        Optional<String> filteredOut = lang.filter(s -> s.startsWith("P"));
        System.out.println(filteredOut);  // Optional.empty

        ////////////////////////////////////////////////////////////////
        // IFPRESENT — ejecutar acción si hay valor
        ////////////////////////////////////////////////////////////////

        // ifPresent — ejecuta Consumer si hay valor
        lang.ifPresent(s -> System.out.println("Lenguaje: " + s));

        // ifPresentOrElse (Java 9+) — acción si presente, alternativa si vacío
        empty.ifPresentOrElse(
            s -> System.out.println("Valor: " + s),
            () -> System.out.println("No hay valor")
        );

        ////////////////////////////////////////////////////////////////
        // OPTIONAL COMO STREAM (Java 9+)
        ////////////////////////////////////////////////////////////////

        // stream() — convierte Optional a Stream de 0 o 1 elemento
        lang.stream().forEach(System.out::println);  // "Java"
        empty.stream().forEach(System.out::println);  // nada

        // Útil en combinación con flatMap de Stream
        List<Optional<String>> optionals = List.of(
            Optional.of("Java"),
            Optional.empty(),
            Optional.of("Python"),
            Optional.empty(),
            Optional.of("Go")
        );

        // Extraer solo los valores presentes
        List<String> values = optionals.stream()
            .flatMap(Optional::stream)
            .toList();
        System.out.println(values);  // [Java, Python, Go]

        ////////////////////////////////////////////////////////////////
        // PATRÓN: OPTIONAL EN MÉTODOS
        ////////////////////////////////////////////////////////////////

        // Devolver Optional en vez de null
        Optional<String> found = findUserById(42);
        String username = found.orElse("anonymous");
        System.out.println("User: " + username);

        // Encadenar operaciones
        String greeting = findUserById(42)
            .map(String::toUpperCase)
            .map(n -> "Hola, " + n + "!")
            .orElse("Hola, desconocido!");
        System.out.println(greeting);

        ////////////////////////////////////////////////////////////////
        // ANTIPATRONES — qué NO hacer
        ////////////////////////////////////////////////////////////////

        // MAL: usar Optional como parámetro de método
        // void process(Optional<String> name) { ... }

        // MAL: usar Optional en campos de clase
        // private Optional<String> name;

        // MAL: usar get() sin comprobar
        // optional.get() → usar orElse o ifPresent

        // MAL: comparar con null
        // if (optional != null) → Optional nunca debería ser null

        // BIEN: Optional solo como tipo de retorno de métodos
        // que pueden no devolver un valor

        ////////////////////////////////////////////////////////////////
        // OPTIONAL CON PRIMITIVOS
        ////////////////////////////////////////////////////////////////

        // OptionalInt, OptionalLong, OptionalDouble — evitan autoboxing
        OptionalInt optInt = OptionalInt.of(42);
        OptionalDouble optDouble = OptionalDouble.empty();

        int intValue = optInt.orElse(0);           // 42
        double dblValue = optDouble.orElse(0.0);   // 0.0

        System.out.println("OptionalInt: " + intValue);
        System.out.println("OptionalDouble: " + dblValue);
    }

    // Ejemplo: método que devuelve Optional
    static Optional<String> findUserById(int id) {
        if (id == 42) {
            return Optional.of("Alice");
        }
        return Optional.empty();
    }
}
