////////////////////////////////////////////////////////////////
// INNER CLASSES — clases anidadas en Java
////////////////////////////////////////////////////////////////

import java.util.*;

public class InnerClasses {

    // Campo de la clase externa
    private String outerField = "campo externo";
    private static String staticField = "campo estático";

    ////////////////////////////////////////////////////////////////
    // 1. STATIC NESTED CLASS — no necesita instancia de la externa
    ////////////////////////////////////////////////////////////////

    static class StaticNested {
        void show() {
            // Puede acceder a miembros ESTÁTICOS de la externa
            System.out.println("Static nested: " + staticField);
            // System.out.println(outerField); // Error: no puede acceder a instancia
        }
    }

    ////////////////////////////////////////////////////////////////
    // 2. INNER CLASS (no estática) — necesita instancia de la externa
    ////////////////////////////////////////////////////////////////

    class Inner {
        void show() {
            // Puede acceder a TODOS los miembros de la externa (incluso private)
            System.out.println("Inner: " + outerField);
            System.out.println("Inner: " + staticField);
        }

        // Referencia explícita a la instancia externa
        void showExplicit() {
            System.out.println("Outer this: " + InnerClasses.this.outerField);
        }
    }

    ////////////////////////////////////////////////////////////////
    // 3. LOCAL CLASS — definida dentro de un método
    ////////////////////////////////////////////////////////////////

    void methodWithLocalClass() {
        String localVar = "variable local";  // efectivamente final

        class Local {
            void show() {
                // Accede a miembros de la externa Y variables locales (final/effectively final)
                System.out.println("Local: " + outerField);
                System.out.println("Local: " + localVar);
                // localVar = "otro"; // Error: debe ser effectively final
            }
        }

        Local local = new Local();
        local.show();
    }

    ////////////////////////////////////////////////////////////////
    // 4. ANONYMOUS CLASS — sin nombre, se define inline
    ////////////////////////////////////////////////////////////////

    interface Greeting {
        void greet(String name);
    }

    abstract static class Animal {
        abstract String sound();
    }

    void anonymousClassExamples() {

        // Implementar interfaz anónimamente
        Greeting hello = new Greeting() {
            @Override
            public void greet(String name) {
                System.out.println("Hola, " + name + "!");
            }
        };
        hello.greet("Java");

        // Extender clase abstracta anónimamente
        Animal cat = new Animal() {
            @Override
            String sound() {
                return "Miau";
            }
        };
        System.out.println("Gato dice: " + cat.sound());

        // Con lambda es más conciso (si es interfaz funcional)
        Greeting helloLambda = name -> System.out.println("Hola, " + name + "!");
        helloLambda.greet("Lambda");
    }

    ////////////////////////////////////////////////////////////////
    // CASO PRÁCTICO: Iterator con inner class
    ////////////////////////////////////////////////////////////////

    static class NumberRange implements Iterable<Integer> {
        private final int start;
        private final int end;

        NumberRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public Iterator<Integer> iterator() {
            // Inner class que accede a los campos de NumberRange
            return new Iterator<Integer>() {
                private int current = start;

                @Override
                public boolean hasNext() {
                    return current <= end;
                }

                @Override
                public Integer next() {
                    return current++;
                }
            };
        }
    }

    ////////////////////////////////////////////////////////////////
    // CASO PRÁCTICO: Builder pattern con static nested class
    ////////////////////////////////////////////////////////////////

    static class User {
        private final String name;
        private final String email;
        private final int age;

        private User(Builder builder) {
            this.name = builder.name;
            this.email = builder.email;
            this.age = builder.age;
        }

        @Override
        public String toString() {
            return "User{name='" + name + "', email='" + email + "', age=" + age + "}";
        }

        // Static nested class como Builder
        static class Builder {
            private String name;
            private String email;
            private int age;

            Builder name(String name) { this.name = name; return this; }
            Builder email(String email) { this.email = email; return this; }
            Builder age(int age) { this.age = age; return this; }

            User build() {
                return new User(this);
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // CUÁNDO USAR CADA TIPO
    ////////////////////////////////////////////////////////////////

    // Static nested → utilidad relacionada, no necesita instancia externa
    //                  (Ejemplo: Builder, Entry en Map, Node en LinkedList)
    //
    // Inner class   → necesita acceso a campos de instancia de la externa
    //                  (Ejemplo: Iterator que accede al estado de la colección)
    //
    // Local class   → lógica encapsulada dentro de un método específico
    //                  (Raro, normalmente se usa lambda o anonymous)
    //
    // Anonymous      → implementación rápida de interfaz/clase abstracta
    //                  (Reemplazada por lambdas en interfaces funcionales)

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        // Static nested — se crea sin instancia de la externa
        StaticNested sn = new StaticNested();
        sn.show();

        // Inner class — necesita instancia de la externa
        InnerClasses outer = new InnerClasses();
        Inner inner = outer.new Inner();
        inner.show();
        inner.showExplicit();

        // Local class
        outer.methodWithLocalClass();

        // Anonymous class
        outer.anonymousClassExamples();

        // Iterator con inner class
        NumberRange range = new NumberRange(1, 5);
        for (int n : range) {
            System.out.print(n + " ");  // 1 2 3 4 5
        }
        System.out.println();

        // Builder pattern
        User user = new User.Builder()
            .name("Ana")
            .email("ana@example.com")
            .age(25)
            .build();
        System.out.println(user);

        // Comparator como anonymous class vs lambda
        List<String> names = new ArrayList<>(List.of("Carlos", "Ana", "Eva"));

        // Anonymous class
        names.sort(new Comparator<String>() {
            @Override
            public int compare(String a, String b) {
                return a.length() - b.length();
            }
        });

        // Lambda equivalente (mucho más conciso)
        names.sort((a, b) -> a.length() - b.length());

        // Method reference equivalente
        names.sort(Comparator.comparingInt(String::length));

        System.out.println("Ordenado: " + names);
    }
}
