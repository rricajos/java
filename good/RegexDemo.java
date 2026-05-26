////////////////////////////////////////////////////////////////
// REGEX — expresiones regulares (java.util.regex)
////////////////////////////////////////////////////////////////

import java.util.regex.*;
import java.util.List;
import java.util.ArrayList;

public class RegexDemo {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // PATTERN Y MATCHER — las clases principales
        ////////////////////////////////////////////////////////////////

        // Compilar el patrón (reutilizable, thread-safe)
        Pattern pattern = Pattern.compile("\\d+");

        // Crear matcher para una cadena específica
        Matcher matcher = pattern.matcher("Tengo 3 gatos y 5 perros");

        // find() — busca la siguiente coincidencia
        while (matcher.find()) {
            System.out.println("Encontrado: '" + matcher.group()
                + "' en posición " + matcher.start() + "-" + matcher.end());
        }
        // Encontrado: '3' en posición 6-7
        // Encontrado: '5' en posición 16-17

        ////////////////////////////////////////////////////////////////
        // MATCHES — comprobar si TODA la cadena coincide
        ////////////////////////////////////////////////////////////////

        // String.matches() — shorthand (compila pattern cada vez)
        System.out.println("123".matches("\\d+"));       // true
        System.out.println("abc123".matches("\\d+"));     // false (no toda la cadena)

        // Pattern.matches() — equivalente estático
        System.out.println(Pattern.matches("\\d+", "456")); // true

        ////////////////////////////////////////////////////////////////
        // SINTAXIS DE REGEX
        ////////////////////////////////////////////////////////////////

        // CARACTERES
        // .       → cualquier carácter (excepto newline)
        // \\d     → dígito [0-9]
        // \\D     → NO dígito [^0-9]
        // \\w     → word char [a-zA-Z0-9_]
        // \\W     → NO word char
        // \\s     → whitespace [ \t\n\r\f]
        // \\S     → NO whitespace

        // CUANTIFICADORES
        // *       → 0 o más
        // +       → 1 o más
        // ?       → 0 o 1
        // {n}     → exactamente n
        // {n,}    → n o más
        // {n,m}   → entre n y m

        // ANCLAJES
        // ^       → inicio de línea
        // $       → fin de línea
        // \\b     → word boundary

        // GRUPOS Y CLASES
        // [abc]   → a, b o c
        // [^abc]  → NO a, b ni c
        // [a-z]   → rango a-z
        // (abc)   → grupo de captura
        // (?:abc) → grupo sin captura
        // a|b     → a o b

        ////////////////////////////////////////////////////////////////
        // EJEMPLOS PRÁCTICOS DE VALIDACIÓN
        ////////////////////////////////////////////////////////////////

        // Email (simplificado)
        String emailPattern = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$";
        System.out.println("user@test.com: " + "user@test.com".matches(emailPattern));   // true
        System.out.println("invalid@: " + "invalid@".matches(emailPattern));              // false

        // Teléfono español (+34 seguido de 9 dígitos)
        String phonePattern = "^\\+34\\d{9}$";
        System.out.println("+34612345678: " + "+34612345678".matches(phonePattern));  // true

        // Contraseña fuerte (mín 8 chars, mayúscula, minúscula, dígito)
        String password = "MyPass123";
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasLength = password.length() >= 8;
        System.out.println("Contraseña fuerte: " + (hasUpper && hasLower && hasDigit && hasLength));

        // IP address
        String ipPattern = "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$";
        System.out.println("192.168.1.1: " + "192.168.1.1".matches(ipPattern));   // true
        System.out.println("999.0.0.1: " + "999.0.0.1".matches(ipPattern));       // false

        ////////////////////////////////////////////////////////////////
        // GRUPOS DE CAPTURA
        ////////////////////////////////////////////////////////////////

        // Extraer partes de un patrón con (paréntesis)
        Pattern datePattern = Pattern.compile("(\\d{2})/(\\d{2})/(\\d{4})");
        Matcher dateMatcher = datePattern.matcher("Fecha: 25/12/2025");

        if (dateMatcher.find()) {
            System.out.println("Día: " + dateMatcher.group(1));    // "25"
            System.out.println("Mes: " + dateMatcher.group(2));    // "12"
            System.out.println("Año: " + dateMatcher.group(3));    // "2025"
            System.out.println("Completo: " + dateMatcher.group(0)); // "25/12/2025"
        }

        // Grupos con nombre (?<nombre>patrón)
        Pattern namedPattern = Pattern.compile(
            "(?<name>[\\w]+)\\s*=\\s*(?<value>[\\w]+)"
        );
        Matcher namedMatcher = namedPattern.matcher("host = localhost");

        if (namedMatcher.find()) {
            System.out.println("Clave: " + namedMatcher.group("name"));   // "host"
            System.out.println("Valor: " + namedMatcher.group("value"));  // "localhost"
        }

        ////////////////////////////////////////////////////////////////
        // BUSCAR TODAS LAS COINCIDENCIAS
        ////////////////////////////////////////////////////////////////

        // Con find() en bucle
        Pattern wordPat = Pattern.compile("[A-Z][a-z]+");
        Matcher wordMat = wordPat.matcher("Hola Mundo Java Es Genial");

        List<String> words = new ArrayList<>();
        while (wordMat.find()) {
            words.add(wordMat.group());
        }
        System.out.println("Palabras capitalizadas: " + words);
        // [Hola, Mundo, Java, Es, Genial]

        // Con results() como Stream (Java 9+)
        List<String> matches = wordPat.matcher("Hola Mundo Java")
            .results()
            .map(MatchResult::group)
            .toList();
        System.out.println("Stream results: " + matches);

        ////////////////////////////////////////////////////////////////
        // REEMPLAZAR
        ////////////////////////////////////////////////////////////////

        // replaceAll — reemplazar todas las coincidencias
        String censored = "Mi teléfono es 612345678 y otro 698765432"
            .replaceAll("\\d{9}", "***");
        System.out.println(censored);
        // "Mi teléfono es *** y otro ***"

        // replaceFirst — solo la primera
        String first = "aaa bbb ccc".replaceFirst("\\w+", "XXX");
        System.out.println(first);  // "XXX bbb ccc"

        // Reemplazo con backreference ($1, $2...)
        String swapped = "apellido, nombre".replaceAll(
            "(\\w+),\\s*(\\w+)", "$2 $1"
        );
        System.out.println(swapped);  // "nombre apellido"

        // Reemplazo con función (Java 9+ Matcher.replaceAll con lambda)
        String shouted = Pattern.compile("\\b\\w+\\b")
            .matcher("hola mundo java")
            .replaceAll(mr -> mr.group().toUpperCase());
        System.out.println(shouted);  // "HOLA MUNDO JAVA"

        ////////////////////////////////////////////////////////////////
        // SPLIT CON REGEX
        ////////////////////////////////////////////////////////////////

        // Dividir por uno o más espacios
        String[] parts = "uno   dos    tres".split("\\s+");
        System.out.println(java.util.Arrays.toString(parts)); // [uno, dos, tres]

        // Dividir por delimitadores múltiples
        String[] tokens = "a,b;c:d".split("[,;:]");
        System.out.println(java.util.Arrays.toString(tokens)); // [a, b, c, d]

        // Split con Pattern (precompilado, más eficiente en bucles)
        Pattern csv = Pattern.compile(",\\s*");
        String[] fields = csv.split("nombre, edad, email");
        System.out.println(java.util.Arrays.toString(fields)); // [nombre, edad, email]

        ////////////////////////////////////////////////////////////////
        // FLAGS DE COMPILACIÓN
        ////////////////////////////////////////////////////////////////

        // CASE_INSENSITIVE
        Pattern ci = Pattern.compile("java", Pattern.CASE_INSENSITIVE);
        System.out.println(ci.matcher("JAVA").matches());   // true

        // MULTILINE — ^ y $ coinciden con inicio/fin de CADA línea
        Pattern ml = Pattern.compile("^\\w+", Pattern.MULTILINE);
        Matcher mlm = ml.matcher("primera\nsegunda\ntercera");
        while (mlm.find()) {
            System.out.println("Línea: " + mlm.group());
        }

        // DOTALL — el . también coincide con newline
        // Pattern.DOTALL

        // Combinar flags con |
        // Pattern.compile("pattern", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);

        // Flags inline: (?i) case insensitive, (?m) multiline, (?s) dotall
        System.out.println("JAVA".matches("(?i)java"));  // true

        ////////////////////////////////////////////////////////////////
        // ESCAPAR CARACTERES ESPECIALES
        ////////////////////////////////////////////////////////////////

        // Pattern.quote() — escapa toda la cadena
        String literal = Pattern.quote("precio: $10.00");
        System.out.println("precio: $10.00".matches(literal));  // true

        // En Java los backslash se duplican: \d en regex = \\d en String
        // \\ en regex = \\\\ en String (escapar el backslash)
    }
}
