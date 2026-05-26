////////////////////////////////////////////////////////////////
// FILE I/O — ENTRADA/SALIDA DE ARCHIVOS
////////////////////////////////////////////////////////////////

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class FileIO {
    public static void main(String[] args) throws IOException {

        ////////////////////////////////////////////////////////////////
        // PATH — representar y manipular rutas
        ////////////////////////////////////////////////////////////////

        Path path = Path.of("demo.txt");
        Path absolute = path.toAbsolutePath();
        System.out.println("Absoluto: " + absolute);
        System.out.println("Nombre: " + path.getFileName());
        System.out.println("Padre: " + absolute.getParent());

        // Combinar rutas
        Path dir = Path.of("src", "main", "java");
        System.out.println("Combinado: " + dir); // src/main/java

        // Resolver rutas
        Path base = Path.of("/home/user");
        Path resolved = base.resolve("docs/file.txt");
        System.out.println("Resuelto: " + resolved); // /home/user/docs/file.txt

        // Relativizar
        Path p1 = Path.of("/home/user/docs");
        Path p2 = Path.of("/home/user/images");
        System.out.println("Relativo: " + p1.relativize(p2)); // ../images

        ////////////////////////////////////////////////////////////////
        // ESCRIBIR ARCHIVOS
        ////////////////////////////////////////////////////////////////

        // writeString — escribir un String directamente (Java 11+)
        Files.writeString(Path.of("output.txt"), "Hola Mundo\nSegunda línea");
        System.out.println("Archivo escrito: output.txt");

        // write — escribir lista de líneas
        Files.write(
            Path.of("lines.txt"),
            List.of("línea 1", "línea 2", "línea 3")
        );

        // Append — añadir al final sin sobrescribir
        Files.writeString(
            Path.of("output.txt"),
            "\nTercera línea (append)",
            StandardOpenOption.APPEND
        );

        ////////////////////////////////////////////////////////////////
        // LEER ARCHIVOS
        ////////////////////////////////////////////////////////////////

        // readString — leer todo como String (archivos pequeños, Java 11+)
        String content = Files.readString(Path.of("output.txt"));
        System.out.println("Contenido:\n" + content);

        // readAllLines — leer como List<String>
        List<String> lines = Files.readAllLines(Path.of("lines.txt"));
        System.out.println("Líneas: " + lines);

        // lines() — leer como Stream (archivos grandes, lazy, eficiente)
        System.out.println("Líneas no vacías:");
        try (var stream = Files.lines(Path.of("output.txt"))) {
            stream.filter(line -> !line.isBlank())
                  .map(line -> "  > " + line)
                  .forEach(System.out::println);
        }

        // readAllBytes — leer bytes crudos (imágenes, binarios)
        byte[] bytes = Files.readAllBytes(Path.of("output.txt"));
        System.out.println("Bytes leídos: " + bytes.length);

        ////////////////////////////////////////////////////////////////
        // OPERACIONES CON ARCHIVOS Y DIRECTORIOS
        ////////////////////////////////////////////////////////////////

        // Comprobar existencia y tipo
        System.out.println("Existe: " + Files.exists(Path.of("output.txt")));
        System.out.println("Es archivo: " + Files.isRegularFile(Path.of("output.txt")));
        System.out.println("Es directorio: " + Files.isDirectory(Path.of("output.txt")));

        // Tamaño
        long size = Files.size(Path.of("output.txt"));
        System.out.println("Tamaño: " + size + " bytes");

        // Crear directorios (createDirectories crea padres si no existen)
        Path tempDir = Path.of("demo_dir", "sub1", "sub2");
        Files.createDirectories(tempDir);
        System.out.println("Directorio creado: " + tempDir);

        // Copiar archivo
        Files.copy(
            Path.of("output.txt"),
            Path.of("output_copy.txt"),
            StandardCopyOption.REPLACE_EXISTING
        );

        // Mover / renombrar
        Files.move(
            Path.of("output_copy.txt"),
            Path.of("output_renamed.txt"),
            StandardCopyOption.REPLACE_EXISTING
        );

        // Atributos del archivo
        BasicFileAttributes attrs = Files.readAttributes(
            Path.of("output.txt"), BasicFileAttributes.class
        );
        System.out.println("Creado: " + attrs.creationTime());
        System.out.println("Modificado: " + attrs.lastModifiedTime());

        ////////////////////////////////////////////////////////////////
        // LISTAR Y BUSCAR ARCHIVOS
        ////////////////////////////////////////////////////////////////

        // list — listar directorio (1 nivel)
        System.out.println("\nArchivos en directorio actual:");
        try (var listing = Files.list(Path.of("."))) {
            listing.filter(Files::isRegularFile)
                   .forEach(p -> System.out.println("  " + p.getFileName()));
        }

        // walk — recorrer recursivamente
        System.out.println("\nRecorrido recursivo de demo_dir:");
        try (var walk = Files.walk(Path.of("demo_dir"))) {
            walk.forEach(p -> System.out.println("  " + p));
        }

        // find — buscar con criterio
        System.out.println("\nArchivos .txt encontrados:");
        try (var found = Files.find(Path.of("."), 3,
                (p, a) -> a.isRegularFile() && p.toString().endsWith(".txt"))) {
            found.forEach(p -> System.out.println("  " + p));
        }

        ////////////////////////////////////////////////////////////////
        // BUFFERED READER/WRITER — para archivos grandes
        ////////////////////////////////////////////////////////////////

        // BufferedWriter — escritura eficiente con buffer
        try (BufferedWriter writer = Files.newBufferedWriter(Path.of("buffered.txt"))) {
            writer.write("Primera línea");
            writer.newLine();
            writer.write("Segunda línea");
            writer.newLine();
            writer.write("Tercera línea");
        }

        // BufferedReader — lectura eficiente línea a línea
        try (BufferedReader reader = Files.newBufferedReader(Path.of("buffered.txt"))) {
            String line;
            int lineNum = 1;
            while ((line = reader.readLine()) != null) {
                System.out.println(lineNum++ + ": " + line);
            }
        }

        ////////////////////////////////////////////////////////////////
        // TRY-WITH-RESOURCES — cierre automático
        ////////////////////////////////////////////////////////////////

        // Cualquier clase que implemente AutoCloseable se cierra
        // automáticamente al salir del bloque try.
        // No necesita finally para cerrar el recurso.

        // Múltiples recursos en un solo try
        try (BufferedReader in = Files.newBufferedReader(Path.of("buffered.txt"));
             BufferedWriter out = Files.newBufferedWriter(Path.of("copy.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                out.write(line.toUpperCase());
                out.newLine();
            }
        }

        // Verificar la copia
        String copyContent = Files.readString(Path.of("copy.txt"));
        System.out.println("Copia en mayúsculas:\n" + copyContent);

        ////////////////////////////////////////////////////////////////
        // PROPIEDADES (.properties)
        ////////////////////////////////////////////////////////////////

        // Escribir un archivo de propiedades de ejemplo
        Files.writeString(Path.of("config.properties"),
            "db.host=localhost\ndb.port=5432\ndb.name=myapp\n");

        // Leer propiedades
        var props = new java.util.Properties();
        try (var in = Files.newInputStream(Path.of("config.properties"))) {
            props.load(in);
        }

        System.out.println("db.host = " + props.getProperty("db.host"));
        System.out.println("db.port = " + props.getProperty("db.port"));
        System.out.println("db.timeout = " + props.getProperty("db.timeout", "30")); // default

        ////////////////////////////////////////////////////////////////
        // SCANNER — leer input del usuario
        ////////////////////////////////////////////////////////////////

        // Scanner lee desde System.in u otras fuentes
        // Aquí leemos desde un String para que sea ejecutable
        String input = "Ana\n25\n";
        try (var scanner = new java.util.Scanner(input)) {
            String name = scanner.nextLine();
            int age = scanner.nextInt();
            System.out.println("Hola " + name + ", tienes " + age + " años");
        }

        // Desde System.in sería:
        // try (var scanner = new java.util.Scanner(System.in)) {
        //     System.out.print("Tu nombre: ");
        //     String name = scanner.nextLine();
        // }

        ////////////////////////////////////////////////////////////////
        // LIMPIEZA — eliminar archivos y directorios de prueba
        ////////////////////////////////////////////////////////////////

        Files.deleteIfExists(Path.of("output.txt"));
        Files.deleteIfExists(Path.of("output_renamed.txt"));
        Files.deleteIfExists(Path.of("lines.txt"));
        Files.deleteIfExists(Path.of("buffered.txt"));
        Files.deleteIfExists(Path.of("copy.txt"));
        Files.deleteIfExists(Path.of("config.properties"));

        // Eliminar directorio recursivamente
        if (Files.exists(Path.of("demo_dir"))) {
            try (var walk = Files.walk(Path.of("demo_dir"))) {
                walk.sorted(java.util.Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.delete(p); }
                        catch (IOException e) { /* ignorar */ }
                    });
            }
        }

        System.out.println("\nFileIO: todos los ejemplos completados");
    }
}
