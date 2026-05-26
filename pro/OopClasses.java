////////////////////////////////////////////////////////////////
// OOP — CLASES Y OBJETOS
////////////////////////////////////////////////////////////////

public class OopClasses {

    ////////////////////////////////////////////////////////////////
    // CLASE BÁSICA
    ////////////////////////////////////////////////////////////////

    // Campos (atributos)
    private String name;
    private int age;

    // Constante de clase
    static final String SPECIES = "Human";

    ////////////////////////////////////////////////////////////////
    // CONSTRUCTORES
    ////////////////////////////////////////////////////////////////

    // Constructor por defecto
    public OopClasses() {
        this.name = "Unknown";
        this.age = 0;
    }

    // Constructor con parámetros
    public OopClasses(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Constructor que llama a otro constructor (this)
    public OopClasses(String name) {
        this(name, 0);  // reutiliza el constructor anterior
    }

    ////////////////////////////////////////////////////////////////
    // ENCAPSULACIÓN — getters y setters
    ////////////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;  // this distingue campo de parámetro
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age >= 0) {           // validación en setter
            this.age = age;
        }
    }

    ////////////////////////////////////////////////////////////////
    // MÉTODOS DE INSTANCIA
    ////////////////////////////////////////////////////////////////

    public String greet() {
        return "Hola, soy " + name + " y tengo " + age + " años";
    }

    ////////////////////////////////////////////////////////////////
    // MÉTODOS ESTÁTICOS (pertenecen a la clase, no a la instancia)
    ////////////////////////////////////////////////////////////////

    static int instanceCount = 0;

    public static int getInstanceCount() {
        return instanceCount;
        // No puede acceder a this ni a campos de instancia
    }

    ////////////////////////////////////////////////////////////////
    // toString, equals, hashCode
    ////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "OopClasses{name='" + name + "', age=" + age + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OopClasses other = (OopClasses) obj;
        return age == other.age && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, age);
    }

    ////////////////////////////////////////////////////////////////
    // BLOQUES DE INICIALIZACIÓN
    ////////////////////////////////////////////////////////////////

    // Bloque de instancia — se ejecuta antes del constructor
    {
        instanceCount++;
    }

    // Bloque estático — se ejecuta una vez al cargar la clase
    static {
        System.out.println("Clase OopClasses cargada");
    }

    ////////////////////////////////////////////////////////////////
    // RECORDS (Java 16+) — clases inmutables simplificadas
    ////////////////////////////////////////////////////////////////

    // Un record genera automáticamente: constructor, getters,
    // equals, hashCode y toString
    record Point(int x, int y) {
        // Se pueden añadir métodos extra
        double distanceTo(Point other) {
            return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
        }
    }

    ////////////////////////////////////////////////////////////////
    // ENUMS
    ////////////////////////////////////////////////////////////////

    enum Color {
        RED("#FF0000"),
        GREEN("#00FF00"),
        BLUE("#0000FF");

        private final String hex;

        Color(String hex) {
            this.hex = hex;
        }

        public String getHex() {
            return hex;
        }
    }

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        // Crear objetos
        OopClasses p1 = new OopClasses("Ana", 25);
        OopClasses p2 = new OopClasses("Luis");
        OopClasses p3 = new OopClasses();

        System.out.println(p1.greet());       // "Hola, soy Ana y tengo 25 años"
        System.out.println(p1);               // toString automático
        System.out.println(getInstanceCount()); // 3

        // Record
        Point a = new Point(0, 0);
        Point b = new Point(3, 4);
        System.out.println(a.distanceTo(b));  // 5.0
        System.out.println(b);                // Point[x=3, y=4]

        // Enum
        Color c = Color.RED;
        System.out.println(c.getHex());       // "#FF0000"
        System.out.println(c.name());         // "RED"
        System.out.println(c.ordinal());      // 0

        for (Color color : Color.values()) {
            System.out.println(color + " → " + color.getHex());
        }
    }
}
