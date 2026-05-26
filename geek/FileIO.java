////////////////////////////////////////////////////////////////
// FILE I/O — ENTRADA/SALIDA DE ARCHIVOS
////////////////////////////////////////////////////////////////

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class FileIO {
    public static void main(String[] args) throws IOException {

        ////////////////////////////////////////////////////////////////
        // NIO.2 — API MODERNA (java.nio.file) — PREFERIDA
        ////////////////////////////////////////////////////////////////

        // Path — representar rutas
        Path path = Path.of("test.txt");
        Path absolute = path.toAbsolutePath();
        Path parent = path.getParent();
        String fileName = path.getFileName().toString();

        // Combinar paths
        Path dir = Path.of("src", "main", "java");
        // → src/main/java

        ////////////////////////////////////////////////////////////////
        // LEER ARCHIVOS (NIO)
        ////////////////////////////////////////////////////////////////

        // Leer todo como String (archivos pequeños)
        // String content = Files.readString(Path.of("file.txt"));

        // Leer todas las líneas como List
        // List<String> lines = Files.readAllLines(Path.of("file.txt"));

        // Leer como Stream (archivos grandes, lazy)
        // try (var stream = Files.lines(Path.of("file.txt"))) {
        //     stream.filter(line -> !line.isBlank())
        //           .forEach(System.out::println);
        // }

        // Leer bytes
        // byte[] bytes = Files.readAllBytes(Path.of("image.png"));

        ////////////////////////////////////////////////////////////////
        // ESCRIBIR ARCHIVOS (NIO)
        ////////////////////////////////////////////////////////////////

        // Escribir String
        Files.writeString(
            Path.of("output.txt"),
            "Hola Mundo\nSegunda línea"
        );

        // Escribir líneas
        Files.write(
            Path.of("lines.txt"),
            List.of("línea 1", "línea 2", "línea 3")
        );

        // Append (añadir al final)
        Files.writeString(
            Path.of("output.txt"),
            "\nNueva línea",
            StandardOpenOption.APPEND
        );

        ////////////////////////////////////////////////////////////////
        // OPERACIONES CON ARCHIVOS Y DIRECTORIOS
        ////////////////////////////////////////////////////////////////

        // Comprobar existencia
        boolean exists = Files.exists(Path.of("output.txt"));

        // Crear directorios
        // Files.createDirectory(Path.of("nuevo_dir"));
        // Files.createDirectories(Path.of("a/b/c")); // crea padres

        // Copiar
        // Files.copy(Path.of("a.txt"), Path.of("b.txt"));
        // Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);

        // Mover / Renombrar
        // Files.move(Path.of("old.txt"), Path.of("new.txt"));

        // Eliminar
        // Files.delete(Path.of("file.txt"));          // lanza si no existe
        // Files.deleteIfExists(Path.of("file.txt"));  // no lanza

        // Tamaño
        // long size = Files.size(Path.of("file.txt"));

        // Listar directorio
        // try (var stream = Files.list(Path.of("."))) {
        //     stream.forEach(System.out::println);
        // }

        // Buscar archivos recursivamente
        // try (var stream = Files.walk(Path.of("src"))) {
        //     stream.filter(p -> p.toString().endsWith(".java"))
        //           .forEach(System.out::println);
        // }

        // Find con criterio
        // try (var stream = Files.find(Path.of("."), 10,
        //         (p, attrs) -> attrs.isRegularFile() && p.toString().endsWith(".txt"))) {
        //     stream.forEach(System.out::println);
        // }

        ////////////////////////////////////////////////////////////////
        // TRY-WITH-RESOURCES — cierre automático
        ////////////////////////////////////////////////////////////////

        // Cualquier clase que implemente AutoCloseable se cierra automáticamente
        // al salir del bloque try

        // BufferedReader (para archivos grandes, línea a línea)
        // try (BufferedReader reader = Files.newBufferedReader(Path.of("file.txt"))) {
        //     String line;
        //     while ((line = reader.readLine()) != null) {
        //         System.out.println(line);
        //     }
        // }

        // BufferedWriter
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of("buffered.txt"))) {
            writer.write("Primera línea");
            writer.newLine();
            writer.write("Segunda línea");
        }

        ////////////////////////////////////////////////////////////////
        // SERIALIZACIÓN (objetos → bytes → archivo)
        ////////////////////////////////////////////////////////////////

        // La clase debe implementar Serializable
        // record User(String name, int age) implements Serializable {}

        // Escribir objeto
        // try (var oos = new ObjectOutputStream(new FileOutputStream("user.dat"))) {
        //     oos.writeObject(new User("Ana", 25));
        // }

        // Leer objeto
        // try (var ois = new ObjectInputStream(new FileInputStream("user.dat"))) {
        //     User user = (User) ois.readObject();
        // }

        ////////////////////////////////////////////////////////////////
        // SCANNER — leer input del usuario
        ////////////////////////////////////////////////////////////////

        // try (var scanner = new java.util.Scanner(System.in)) {
        //     System.out.print("Tu nombre: ");
        //     String name = scanner.nextLine();
        //
        //     System.out.print("Tu edad: ");
        //     int age = scanner.nextInt();
        //
        //     System.out.println("Hola " + name + ", tienes " + age + " años");
        // }

        ////////////////////////////////////////////////////////////////
        // PROPIEDADES (archivos .properties)
        ////////////////////////////////////////////////////////////////

        // var props = new java.util.Properties();
        // try (var in = new FileInputStream("config.properties")) {
        //     props.load(in);
        // }
        // String dbUrl = props.getProperty("db.url", "localhost");

        ////////////////////////////////////////////////////////////////
        // LIMPIEZA
        ////////////////////////////////////////////////////////////////

        // Eliminar archivos de prueba
        Files.deleteIfExists(Path.of("output.txt"));
        Files.deleteIfExists(Path.of("lines.txt"));
        Files.deleteIfExists(Path.of("buffered.txt"));

        System.out.println("FileIO ejemplos completados");
    }
}
