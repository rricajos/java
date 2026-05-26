////////////////////////////////////////////////////////////////
// OOP — HERENCIA Y POLIMORFISMO
////////////////////////////////////////////////////////////////

public class OopInheritance {

    ////////////////////////////////////////////////////////////////
    // CLASE BASE (superclase)
    ////////////////////////////////////////////////////////////////

    static class Animal {
        protected String name;
        protected int age;

        public Animal(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String speak() {
            return name + " hace un sonido";
        }

        @Override
        public String toString() {
            return name + " (" + age + " años)";
        }
    }

    ////////////////////////////////////////////////////////////////
    // HERENCIA — extends
    ////////////////////////////////////////////////////////////////

    static class Dog extends Animal {

        private String breed;

        public Dog(String name, int age, String breed) {
            super(name, age);  // llama al constructor de Animal
            this.breed = breed;
        }

        // Sobrescritura de método (override)
        @Override
        public String speak() {
            return name + " dice: ¡Guau!";
        }

        // Método propio de Dog
        public String fetch() {
            return name + " trae la pelota";
        }
    }

    static class Cat extends Animal {

        public Cat(String name, int age) {
            super(name, age);
        }

        @Override
        public String speak() {
            return name + " dice: ¡Miau!";
        }
    }

    ////////////////////////////////////////////////////////////////
    // POLIMORFISMO
    ////////////////////////////////////////////////////////////////

    // El mismo tipo de referencia puede apuntar a distintos objetos
    static void makeSpeak(Animal animal) {
        System.out.println(animal.speak());
        // Llama al speak() correcto según el tipo real del objeto
    }

    ////////////////////////////////////////////////////////////////
    // CLASE ABSTRACTA
    ////////////////////////////////////////////////////////////////

    static abstract class Shape {
        protected String color;

        public Shape(String color) {
            this.color = color;
        }

        // Método abstracto — las subclases DEBEN implementarlo
        abstract double area();

        // Método concreto — las subclases lo heredan
        public String getColor() {
            return color;
        }
    }

    static class Circle extends Shape {
        private double radius;

        public Circle(String color, double radius) {
            super(color);
            this.radius = radius;
        }

        @Override
        double area() {
            return Math.PI * radius * radius;
        }
    }

    static class Rectangle extends Shape {
        private double width, height;

        public Rectangle(String color, double width, double height) {
            super(color);
            this.width = width;
            this.height = height;
        }

        @Override
        double area() {
            return width * height;
        }
    }

    ////////////////////////////////////////////////////////////////
    // INTERFACES
    ////////////////////////////////////////////////////////////////

    interface Drawable {
        void draw();  // método abstracto (implícito)

        // Método con implementación por defecto (Java 8+)
        default void drawWithBorder() {
            System.out.println("--- borde ---");
            draw();
            System.out.println("--- borde ---");
        }

        // Método estático en interfaz
        static String getVersion() {
            return "1.0";
        }
    }

    interface Resizable {
        void resize(double factor);
    }

    // Una clase puede implementar MÚLTIPLES interfaces
    static class DrawableCircle extends Circle implements Drawable, Resizable {
        private double radius;

        public DrawableCircle(String color, double radius) {
            super(color, radius);
            this.radius = radius;
        }

        @Override
        public void draw() {
            System.out.println("Dibujando círculo " + color + " (r=" + radius + ")");
        }

        @Override
        public void resize(double factor) {
            this.radius *= factor;
        }
    }

    ////////////////////////////////////////////////////////////////
    // SEALED CLASSES (Java 17+)
    ////////////////////////////////////////////////////////////////

    // Restringen qué clases pueden heredar
    sealed interface Payment permits CreditCard, Cash, Transfer {
        double amount();
    }

    record CreditCard(double amount, String cardNumber) implements Payment {}
    record Cash(double amount) implements Payment {}
    record Transfer(double amount, String iban) implements Payment {}

    ////////////////////////////////////////////////////////////////
    // CASTING DE OBJETOS
    ////////////////////////////////////////////////////////////////

    static void castingExample() {
        Animal animal = new Dog("Rex", 5, "Pastor Alemán"); // upcasting (implícito)

        // Downcasting (explícito) — necesita verificar tipo
        if (animal instanceof Dog dog) {  // pattern matching (Java 16+)
            System.out.println(dog.fetch());
        }

        // Sin pattern matching:
        // if (animal instanceof Dog) {
        //     Dog d = (Dog) animal;
        //     System.out.println(d.fetch());
        // }
    }

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        // Herencia básica
        Dog dog = new Dog("Rex", 5, "Labrador");
        Cat cat = new Cat("Luna", 3);

        System.out.println(dog.speak());  // "Rex dice: ¡Guau!"
        System.out.println(cat.speak());  // "Luna dice: ¡Miau!"

        // Polimorfismo
        Animal[] animals = {dog, cat, new Dog("Max", 2, "Bulldog")};
        for (Animal a : animals) {
            makeSpeak(a);  // cada uno ejecuta su propia versión
        }

        // Clases abstractas
        Shape circle = new Circle("rojo", 5);
        Shape rect = new Rectangle("azul", 4, 6);
        System.out.printf("Círculo: %.2f%n", circle.area());  // 78.54
        System.out.printf("Rectángulo: %.2f%n", rect.area()); // 24.00

        // Interfaces
        DrawableCircle dc = new DrawableCircle("verde", 3);
        dc.draw();
        dc.drawWithBorder();
        dc.resize(2);

        // Casting
        castingExample();

        // Sealed classes
        Payment payment = new CreditCard(49.99, "1234-5678");
        String desc = switch (payment) {
            case CreditCard cc -> "Tarjeta: " + cc.cardNumber();
            case Cash c        -> "Efectivo";
            case Transfer t    -> "Transferencia a " + t.iban();
        };
        System.out.println(desc);
    }
}
