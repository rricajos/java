////////////////////////////////////////////////////////////////
// OPERADORES LÓGICOS Y DE COMPARACIÓN
////////////////////////////////////////////////////////////////

public class OperatorsLogical {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // OPERADORES DE COMPARACIÓN
        ////////////////////////////////////////////////////////////////

        int a = 10, b = 20;

        System.out.println(a == b);    // false — igual a
        System.out.println(a != b);    // true  — distinto de
        System.out.println(a > b);     // false — mayor que
        System.out.println(a < b);     // true  — menor que
        System.out.println(a >= 10);   // true  — mayor o igual
        System.out.println(a <= 10);   // true  — menor o igual

        ////////////////////////////////////////////////////////////////
        // OPERADORES LÓGICOS
        ////////////////////////////////////////////////////////////////

        boolean x = true, y = false;

        // AND — ambos deben ser true
        System.out.println(x && y);    // false

        // OR — al menos uno debe ser true
        System.out.println(x || y);    // true

        // NOT — invierte el valor
        System.out.println(!x);        // false
        System.out.println(!y);        // true

        ////////////////////////////////////////////////////////////////
        // SHORT-CIRCUIT EVALUATION (evaluación en corto circuito)
        ////////////////////////////////////////////////////////////////

        // && deja de evaluar si el primero es false
        // || deja de evaluar si el primero es true

        String str = null;

        // Sin short-circuit daría NullPointerException
        if (str != null && str.length() > 0) {
            System.out.println("String no vacío");
        }
        // str.length() NUNCA se ejecuta porque str != null es false

        // Esto es un patrón muy común para null-checks en Java

        ////////////////////////////////////////////////////////////////
        // & y | (sin short-circuit) — evalúan AMBOS lados siempre
        ////////////////////////////////////////////////////////////////

        int count = 0;

        // Con &&: si false, no evalúa el segundo
        boolean result1 = (false && (++count > 0));
        System.out.println("count tras &&: " + count);  // 0

        // Con &: evalúa ambos siempre
        boolean result2 = (false & (++count > 0));
        System.out.println("count tras &:  " + count);  // 1

        ////////////////////////////////////////////////////////////////
        // OPERADORES BIT A BIT (bitwise)
        ////////////////////////////////////////////////////////////////

        int m = 0b1010;  // 10
        int n = 0b1100;  // 12

        System.out.println(m & n);   // 0b1000 = 8   (AND)
        System.out.println(m | n);   // 0b1110 = 14  (OR)
        System.out.println(m ^ n);   // 0b0110 = 6   (XOR)
        System.out.println(~m);      // invierte todos los bits (NOT)

        // Shift
        System.out.println(m << 1);  // 0b10100 = 20 (shift left)
        System.out.println(m >> 1);  // 0b0101  = 5  (shift right)
        System.out.println(m >>> 1); // unsigned shift right

        ////////////////////////////////////////////////////////////////
        // INSTANCEOF — comprobar tipo de objeto
        ////////////////////////////////////////////////////////////////

        Object obj = "Hola";

        System.out.println(obj instanceof String);  // true
        System.out.println(obj instanceof Integer); // false

        // Pattern matching instanceof (Java 16+)
        if (obj instanceof String text) {
            System.out.println(text.toUpperCase()); // "HOLA"
            // 'text' ya es de tipo String, sin necesidad de cast
        }
    }
}
