////////////////////////////////////////////////////////////////
// OPERADORES DE ASIGNACIÓN
////////////////////////////////////////////////////////////////

public class OperatorsAssignment {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // ASIGNACIÓN SIMPLE
        ////////////////////////////////////////////////////////////////

        int x = 10;  // asigna 10 a x

        ////////////////////////////////////////////////////////////////
        // ASIGNACIÓN COMPUESTA
        ////////////////////////////////////////////////////////////////

        x += 5;   // x = x + 5   → 15
        System.out.println("x += 5  → " + x);

        x -= 3;   // x = x - 3   → 12
        System.out.println("x -= 3  → " + x);

        x *= 2;   // x = x * 2   → 24
        System.out.println("x *= 2  → " + x);

        x /= 4;   // x = x / 4   → 6
        System.out.println("x /= 4  → " + x);

        x %= 4;   // x = x % 4   → 2
        System.out.println("x %= 4  → " + x);

        ////////////////////////////////////////////////////////////////
        // ASIGNACIÓN CON OPERADORES BIT A BIT
        ////////////////////////////////////////////////////////////////

        int bits = 0b1010;  // 10 en binario

        bits &= 0b1100;    // AND → 0b1000 (8)
        System.out.println("&= → " + bits);

        bits |= 0b0011;    // OR  → 0b1011 (11)
        System.out.println("|= → " + bits);

        bits ^= 0b1111;    // XOR → 0b0100 (4)
        System.out.println("^= → " + bits);

        bits <<= 2;        // shift left  → 0b10000 (16)
        System.out.println("<<= → " + bits);

        bits >>= 1;        // shift right → 0b1000 (8)
        System.out.println(">>= → " + bits);

        ////////////////////////////////////////////////////////////////
        // DIFERENCIA CLAVE CON ASIGNACIÓN COMPUESTA
        ////////////////////////////////////////////////////////////////

        // La asignación compuesta incluye un cast implícito
        byte b = 10;
        // b = b + 5;    // Error: int no cabe en byte sin cast
        b += 5;          // OK: equivale a b = (byte)(b + 5)
        System.out.println("byte b += 5 → " + b);
    }
}
