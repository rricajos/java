////////////////////////////////////////////////////////////////
// REFLECTION — inspección y manipulación en runtime
////////////////////////////////////////////////////////////////

import java.lang.reflect.*;
import java.util.Arrays;

public class Reflection {

    ////////////////////////////////////////////////////////////////
    // CLASE DE EJEMPLO
    ////////////////////////////////////////////////////////////////

    static class Person {
        private String name;
        private int age;
        public String email;

        public Person() {
            this("Unknown", 0, "none");
        }

        public Person(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }

        private String getSecret() {
            return "secreto de " + name;
        }

        public String greet() {
            return "Hola, soy " + name;
        }

        public String greet(String greeting) {
            return greeting + ", soy " + name;
        }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age + ", email='" + email + "'}";
        }
    }

    public static void main(String[] args) throws Exception {

        ////////////////////////////////////////////////////////////////
        // OBTENER OBJETO CLASS
        ////////////////////////////////////////////////////////////////

        // 3 formas de obtener la clase
        Class<?> clazz1 = Person.class;                    // desde el tipo
        Class<?> clazz2 = new Person().getClass();          // desde instancia
        Class<?> clazz3 = Class.forName(
            Reflection.class.getName() + "$Person");        // desde nombre completo

        System.out.println("Clase: " + clazz1.getSimpleName());  // "Person"
        System.out.println("Nombre completo: " + clazz1.getName());
        System.out.println("Superclase: " + clazz1.getSuperclass().getSimpleName());

        ////////////////////////////////////////////////////////////////
        // INSPECCIONAR CAMPOS
        ////////////////////////////////////////////////////////////////

        System.out.println("\n--- CAMPOS ---");

        // getFields() — solo campos PUBLIC (incluye heredados)
        Field[] publicFields = clazz1.getFields();
        System.out.println("Campos públicos: " + Arrays.toString(publicFields));

        // getDeclaredFields() — TODOS los campos (solo de esta clase)
        Field[] allFields = clazz1.getDeclaredFields();
        for (Field field : allFields) {
            System.out.printf("  %s %s %s%n",
                Modifier.toString(field.getModifiers()),
                field.getType().getSimpleName(),
                field.getName()
            );
        }
        // private String name
        // private int age
        // public String email

        ////////////////////////////////////////////////////////////////
        // INSPECCIONAR MÉTODOS
        ////////////////////////////////////////////////////////////////

        System.out.println("\n--- MÉTODOS ---");

        // getDeclaredMethods() — todos los métodos de esta clase
        Method[] methods = clazz1.getDeclaredMethods();
        for (Method method : methods) {
            System.out.printf("  %s %s(%s)%n",
                method.getReturnType().getSimpleName(),
                method.getName(),
                Arrays.toString(method.getParameterTypes())
            );
        }

        ////////////////////////////////////////////////////////////////
        // INSPECCIONAR CONSTRUCTORES
        ////////////////////////////////////////////////////////////////

        System.out.println("\n--- CONSTRUCTORES ---");

        Constructor<?>[] constructors = clazz1.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.printf("  %s(%s)%n",
                constructor.getDeclaringClass().getSimpleName(),
                Arrays.toString(constructor.getParameterTypes())
            );
        }

        ////////////////////////////////////////////////////////////////
        // CREAR INSTANCIAS DINÁMICAMENTE
        ////////////////////////////////////////////////////////////////

        System.out.println("\n--- CREAR INSTANCIAS ---");

        // Con constructor sin argumentos
        Constructor<?> noArgConstructor = clazz1.getDeclaredConstructor();
        Object person1 = noArgConstructor.newInstance();
        System.out.println("Sin args: " + person1);

        // Con constructor con argumentos
        Constructor<?> fullConstructor = clazz1.getDeclaredConstructor(
            String.class, int.class, String.class
        );
        Object person2 = fullConstructor.newInstance("Ana", 25, "ana@test.com");
        System.out.println("Con args: " + person2);

        ////////////////////////////////////////////////////////////////
        // LEER Y MODIFICAR CAMPOS (incluso private)
        ////////////////////////////////////////////////////////////////

        System.out.println("\n--- ACCESO A CAMPOS ---");

        Person person = new Person("Luis", 30, "luis@test.com");

        // Campo público — acceso directo
        Field emailField = clazz1.getField("email");
        System.out.println("Email: " + emailField.get(person));

        // Campo privado — necesita setAccessible(true)
        Field nameField = clazz1.getDeclaredField("name");
        nameField.setAccessible(true);  // rompe encapsulación
        System.out.println("Name (private): " + nameField.get(person));

        // Modificar campo privado
        nameField.set(person, "Carlos");
        System.out.println("Modificado: " + person);

        Field ageField = clazz1.getDeclaredField("age");
        ageField.setAccessible(true);
        ageField.setInt(person, 99);
        System.out.println("Edad modificada: " + person);

        ////////////////////////////////////////////////////////////////
        // INVOCAR MÉTODOS (incluso private)
        ////////////////////////////////////////////////////////////////

        System.out.println("\n--- INVOCAR MÉTODOS ---");

        // Método público sin argumentos
        Method greetMethod = clazz1.getDeclaredMethod("greet");
        String result = (String) greetMethod.invoke(person);
        System.out.println(result);  // "Hola, soy Carlos"

        // Método público con argumentos
        Method greetWithArg = clazz1.getDeclaredMethod("greet", String.class);
        String result2 = (String) greetWithArg.invoke(person, "Buenos días");
        System.out.println(result2);  // "Buenos días, soy Carlos"

        // Método privado
        Method secretMethod = clazz1.getDeclaredMethod("getSecret");
        secretMethod.setAccessible(true);
        String secret = (String) secretMethod.invoke(person);
        System.out.println("Secreto: " + secret);

        ////////////////////////////////////////////////////////////////
        // COMPROBAR MODIFICADORES Y TIPOS
        ////////////////////////////////////////////////////////////////

        System.out.println("\n--- MODIFICADORES ---");

        int modifiers = nameField.getModifiers();
        System.out.println("name es private: " + Modifier.isPrivate(modifiers));
        System.out.println("name es static: " + Modifier.isStatic(modifiers));
        System.out.println("name es final: " + Modifier.isFinal(modifiers));

        // Comprobar si implementa interfaz
        System.out.println("Person implements Serializable: "
            + java.io.Serializable.class.isAssignableFrom(Person.class));

        // Comprobar si es instancia
        System.out.println("person es Person: " + clazz1.isInstance(person));

        ////////////////////////////////////////////////////////////////
        // CUÁNDO USAR REFLECTION
        ////////////////////////////////////////////////////////////////

        // ✓ Frameworks (Spring, Hibernate, JUnit)
        // ✓ Serialización/deserialización (JSON, XML)
        // ✓ Inyección de dependencias
        // ✓ Herramientas de testing y mocking
        // ✓ Plugins y sistemas extensibles

        // ✗ NO usar en código normal de aplicación
        // ✗ Rompe encapsulación y type safety
        // ✗ Es más lento que el acceso directo
        // ✗ No detecta errores en compilación (solo en runtime)

        System.out.println("\nReflection: ejemplos completados");
    }
}
