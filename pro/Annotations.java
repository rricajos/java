////////////////////////////////////////////////////////////////
// ANNOTATIONS — metadatos en Java
////////////////////////////////////////////////////////////////

import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.List;

public class Annotations {

    ////////////////////////////////////////////////////////////////
    // ANNOTATIONS BUILT-IN MÁS COMUNES
    ////////////////////////////////////////////////////////////////

    // @Override — indica que se sobrescribe un método del padre
    // El compilador verifica que realmente existe en la superclase
    static class Animal {
        String sound() { return "..."; }
    }

    static class Dog extends Animal {
        @Override
        String sound() { return "Guau!"; }

        // @Override
        // String soond() { } // Error de compilación: soond no existe en Animal
    }

    // @Deprecated — marca como obsoleto (genera warning al usar)
    @Deprecated(since = "2.0", forRemoval = true)
    static void oldMethod() {
        System.out.println("Método antiguo");
    }

    static void newMethod() {
        System.out.println("Método nuevo");
    }

    // @SuppressWarnings — suprime warnings del compilador
    @SuppressWarnings("unchecked")
    static void suppressExample() {
        List rawList = new java.util.ArrayList();
        rawList.add("test"); // warning suprimido
    }

    // @FunctionalInterface — asegura que la interfaz tiene exactamente 1 método abstracto
    @FunctionalInterface
    interface Transformer<T> {
        T transform(T input);
        // Si añadimos otro método abstracto, error de compilación
    }

    // @SafeVarargs — suprime warnings de heap pollution con varargs genéricos
    @SafeVarargs
    static <T> List<T> asList(T... elements) {
        return List.of(elements);
    }

    ////////////////////////////////////////////////////////////////
    // CREAR ANNOTATIONS PERSONALIZADAS
    ////////////////////////////////////////////////////////////////

    // Annotation simple (marker annotation — sin parámetros)
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface Test {
    }

    // Annotation con valores
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @interface TestCase {
        String description() default "";
        int priority() default 0;
        String[] tags() default {};
    }

    // Annotation para campos
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface NotNull {
        String message() default "El campo no puede ser null";
    }

    // Annotation para clases
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Entity {
        String table();
    }

    ////////////////////////////////////////////////////////////////
    // META-ANNOTATIONS — annotations sobre annotations
    ////////////////////////////////////////////////////////////////

    // @Retention — cuándo está disponible la annotation
    //   RetentionPolicy.SOURCE   → solo en código fuente (descartada al compilar)
    //   RetentionPolicy.CLASS    → en el .class pero no en runtime (default)
    //   RetentionPolicy.RUNTIME  → disponible en runtime via reflection

    // @Target — dónde se puede usar la annotation
    //   ElementType.TYPE         → clases, interfaces, enums
    //   ElementType.FIELD        → campos
    //   ElementType.METHOD       → métodos
    //   ElementType.PARAMETER    → parámetros de método
    //   ElementType.CONSTRUCTOR  → constructores
    //   ElementType.LOCAL_VARIABLE → variables locales
    //   ElementType.ANNOTATION_TYPE → otras annotations
    //   ElementType.PACKAGE      → paquetes
    //   ElementType.TYPE_USE     → cualquier uso de tipo (Java 8+)

    // @Documented — incluir en Javadoc
    // @Inherited — las subclases heredan la annotation
    // @Repeatable — se puede usar múltiples veces en el mismo elemento

    ////////////////////////////////////////////////////////////////
    // USAR ANNOTATIONS PERSONALIZADAS
    ////////////////////////////////////////////////////////////////

    @Entity(table = "users")
    static class User {

        @NotNull(message = "El nombre es obligatorio")
        private String name;

        @NotNull
        private String email;

        private int age; // nullable

        User(String name, String email, int age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }
    }

    static class Calculator {

        @Test
        @TestCase(description = "Suma dos números", priority = 1, tags = {"math", "basic"})
        static void testSum() {
            assert 2 + 3 == 5 : "La suma falló";
            System.out.println("  testSum: OK");
        }

        @Test
        @TestCase(description = "Resta dos números", priority = 2)
        static void testSubtract() {
            assert 5 - 3 == 2 : "La resta falló";
            System.out.println("  testSubtract: OK");
        }

        static void notATest() {
            System.out.println("  Esto no es un test");
        }
    }

    ////////////////////////////////////////////////////////////////
    // LEER ANNOTATIONS EN RUNTIME (reflection)
    ////////////////////////////////////////////////////////////////

    static void runTests(Class<?> clazz) {
        System.out.println("Ejecutando tests de " + clazz.getSimpleName() + ":");

        for (Method method : clazz.getDeclaredMethods()) {
            // Comprobar si tiene @Test
            if (method.isAnnotationPresent(Test.class)) {
                try {
                    method.invoke(null);
                } catch (Exception e) {
                    System.out.println("  " + method.getName() + ": FALLÓ - " + e.getCause());
                }

                // Leer @TestCase si existe
                TestCase tc = method.getAnnotation(TestCase.class);
                if (tc != null) {
                    System.out.println("    Descripción: " + tc.description());
                    System.out.println("    Prioridad: " + tc.priority());
                    System.out.println("    Tags: " + String.join(", ", tc.tags()));
                }
            }
        }
    }

    static void validateNotNull(Object obj) throws Exception {
        for (var field : obj.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(NotNull.class)) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value == null) {
                    NotNull annotation = field.getAnnotation(NotNull.class);
                    throw new IllegalArgumentException(
                        field.getName() + ": " + annotation.message()
                    );
                }
            }
        }
        System.out.println("Validación OK");
    }

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception {

        // @Override en acción
        Dog dog = new Dog();
        System.out.println(dog.sound());  // "Guau!"

        // Ejecutar tests con @Test
        runTests(Calculator.class);

        // Validar @NotNull
        User validUser = new User("Ana", "ana@test.com", 25);
        validateNotNull(validUser);

        try {
            User invalidUser = new User(null, "test@test.com", 30);
            validateNotNull(invalidUser);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Leer @Entity
        Entity entity = User.class.getAnnotation(Entity.class);
        if (entity != null) {
            System.out.println("Tabla: " + entity.table());
        }

        // @FunctionalInterface + lambda
        Transformer<String> upper = String::toUpperCase;
        System.out.println(upper.transform("java"));  // "JAVA"
    }
}
