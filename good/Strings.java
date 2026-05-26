////////////////////////////////////////////////////////////////
// STRINGS EN JAVA
////////////////////////////////////////////////////////////////

public class Strings {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // CREACIÓN DE STRINGS
        ////////////////////////////////////////////////////////////////

        String s1 = "Hola";                    // literal (String Pool)
        String s2 = new String("Hola");        // nuevo objeto en heap
        String s3 = String.valueOf(42);        // "42" desde otro tipo

        // Los Strings en Java son INMUTABLES
        // Cada operación devuelve un nuevo String

        ////////////////////////////////////////////////////////////////
        // MÉTODOS DE CONSULTA
        ////////////////////////////////////////////////////////////////

        String text = "Hola Mundo Java";

        System.out.println(text.length());          // 15
        System.out.println(text.isEmpty());          // false
        System.out.println(text.isBlank());          // false (Java 11+)
        System.out.println("  ".isBlank());          // true
        System.out.println(text.charAt(0));          // 'H'
        System.out.println(text.indexOf("Mundo"));   // 5
        System.out.println(text.lastIndexOf('a'));    // 14
        System.out.println(text.contains("Java"));   // true
        System.out.println(text.startsWith("Hola")); // true
        System.out.println(text.endsWith("Java"));   // true

        ////////////////////////////////////////////////////////////////
        // TRANSFORMACIÓN
        ////////////////////////////////////////////////////////////////

        System.out.println(text.toUpperCase());              // "HOLA MUNDO JAVA"
        System.out.println(text.toLowerCase());              // "hola mundo java"
        System.out.println("  hola  ".trim());               // "hola" (quita espacios)
        System.out.println("  hola  ".strip());              // "hola" (Java 11+, Unicode-aware)
        System.out.println(text.replace("Mundo", "World"));  // "Hola World Java"
        System.out.println(text.substring(5));               // "Mundo Java"
        System.out.println(text.substring(5, 10));           // "Mundo"

        ////////////////////////////////////////////////////////////////
        // SPLIT Y JOIN
        ////////////////////////////////////////////////////////////////

        String csv = "uno,dos,tres,cuatro";

        // Split — divide en array
        String[] parts = csv.split(",");
        for (String part : parts) {
            System.out.println(part);  // uno, dos, tres, cuatro
        }

        // Join — une con separador
        String joined = String.join(" - ", parts);
        System.out.println(joined);  // "uno - dos - tres - cuatro"

        // Split con límite
        String[] limited = csv.split(",", 2);
        // ["uno", "dos,tres,cuatro"]

        ////////////////////////////////////////////////////////////////
        // COMPARACIÓN
        ////////////////////////////////////////////////////////////////

        String a = "hello";
        String b = "HELLO";

        System.out.println(a.equals(b));             // false
        System.out.println(a.equalsIgnoreCase(b));    // true
        System.out.println(a.compareTo("hello"));     // 0 (iguales)
        System.out.println(a.compareTo("world"));     // negativo (a < w)

        // NUNCA usar == para comparar contenido de Strings

        ////////////////////////////////////////////////////////////////
        // FORMATO DE STRINGS
        ////////////////////////////////////////////////////////////////

        String name = "Java";
        int version = 21;

        // String.format (estilo printf)
        String formatted = String.format("Lenguaje: %s, Versión: %d", name, version);
        System.out.println(formatted);

        // Printf directo
        System.out.printf("%.2f%n", 3.14159);  // "3.14"

        // Formatted (Java 15+)
        String msg = "Versión %d de %s".formatted(version, name);
        System.out.println(msg);

        ////////////////////////////////////////////////////////////////
        // STRINGBUILDER (mutable, eficiente para concatenaciones)
        ////////////////////////////////////////////////////////////////

        StringBuilder sb = new StringBuilder();
        sb.append("Hola");
        sb.append(" ");
        sb.append("Mundo");
        sb.insert(5, ",");       // "Hola, Mundo"
        sb.delete(4, 5);         // "Hola Mundo"
        sb.reverse();            // "odnuM aloH"
        String result = sb.toString();

        // Usar StringBuilder cuando se concatena en bucles
        // String += en bucle crea muchos objetos intermedios

        ////////////////////////////////////////////////////////////////
        // REGEX
        ////////////////////////////////////////////////////////////////

        String email = "user@example.com";

        // matches — comprueba si TODA la cadena encaja
        boolean valid = email.matches("[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}");
        System.out.println("Email válido: " + valid);  // true

        // replaceAll con regex
        String cleaned = "abc 123 def 456".replaceAll("\\d+", "#");
        System.out.println(cleaned);  // "abc # def #"

        // split con regex
        String[] words = "uno   dos    tres".split("\\s+");
        // ["uno", "dos", "tres"]

        ////////////////////////////////////////////////////////////////
        // CHARS Y CODE POINTS
        ////////////////////////////////////////////////////////////////

        String emoji = "Hola 🌍";

        // charAt solo da 16 bits, no maneja bien emojis
        System.out.println(emoji.length());        // 7 (emoji cuenta como 2 chars)
        System.out.println(emoji.codePointCount(0, emoji.length())); // 6 code points

        // Iterar por caracteres
        for (char ch : text.toCharArray()) {
            System.out.print(ch + " ");
        }
        System.out.println();
    }
}
