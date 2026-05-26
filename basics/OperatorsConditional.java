////////////////////////////////////////////////////////////////
// OPERADORES CONDICIONALES
////////////////////////////////////////////////////////////////

public class OperatorsConditional {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // OPERADOR TERNARIO (? :)
        ////////////////////////////////////////////////////////////////

        int age = 20;

        // condición ? valorSiTrue : valorSiFalse
        String status = (age >= 18) ? "adulto" : "menor";
        System.out.println(status);  // "adulto"

        // Equivalente a:
        // if (age >= 18) { status = "adulto"; } else { status = "menor"; }

        // Ternarios anidados (evitar si son complejos)
        String category = (age < 13) ? "niño"
                         : (age < 18) ? "adolescente"
                         : "adulto";
        System.out.println(category);  // "adulto"

        ////////////////////////////////////////////////////////////////
        // SWITCH CLÁSICO (statement)
        ////////////////////////////////////////////////////////////////

        int day = 3;

        switch (day) {
            case 1:
                System.out.println("Lunes");
                break;
            case 2:
                System.out.println("Martes");
                break;
            case 3:
                System.out.println("Miércoles");
                break;
            case 4:
                System.out.println("Jueves");
                break;
            case 5:
                System.out.println("Viernes");
                break;
            default:
                System.out.println("Fin de semana");
                break;
        }

        // Sin break se produce "fall-through": ejecuta los cases siguientes
        // Esto es un error frecuente en Java

        ////////////////////////////////////////////////////////////////
        // SWITCH EXPRESSION (Java 14+)
        ////////////////////////////////////////////////////////////////

        // Forma moderna con arrow (->), sin necesidad de break
        String dayName = switch (day) {
            case 1 -> "Lunes";
            case 2 -> "Martes";
            case 3 -> "Miércoles";
            case 4 -> "Jueves";
            case 5 -> "Viernes";
            case 6, 7 -> "Fin de semana";  // múltiples valores
            default -> "Día inválido";
        };
        System.out.println(dayName);  // "Miércoles"

        // Switch expression con bloques
        String description = switch (day) {
            case 1, 2, 3, 4, 5 -> {
                String d = dayName + " es laborable";
                yield d;  // yield devuelve valor desde un bloque
            }
            default -> "Descanso";
        };
        System.out.println(description);

        ////////////////////////////////////////////////////////////////
        // SWITCH CON STRINGS
        ////////////////////////////////////////////////////////////////

        String command = "start";

        switch (command) {
            case "start"   -> System.out.println("Iniciando...");
            case "stop"    -> System.out.println("Deteniendo...");
            case "restart" -> System.out.println("Reiniciando...");
            default        -> System.out.println("Comando desconocido");
        }

        ////////////////////////////////////////////////////////////////
        // SWITCH CON PATTERN MATCHING (Java 21+)
        ////////////////////////////////////////////////////////////////

        Object obj = "Hello";

        String result = switch (obj) {
            case Integer i  -> "Entero: " + i;
            case String s   -> "String: " + s;
            case null       -> "Es null";
            default         -> "Otro tipo";
        };
        System.out.println(result);  // "String: Hello"
    }
}
