////////////////////////////////////////////////////////////////
// MÓDULOS EN JAVA (Java Platform Module System — Java 9+)
////////////////////////////////////////////////////////////////

// Los módulos son un sistema de organización SUPERIOR a los paquetes.
// Controlan qué paquetes son accesibles desde fuera del módulo.
// Este archivo es una GUÍA ya que los módulos requieren estructura
// de proyecto completa para funcionar.

public class ModulesDemo {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // ¿QUÉ ES UN MÓDULO?
        ////////////////////////////////////////////////////////////////

        // Un módulo es un grupo de paquetes con un descriptor (module-info.java)
        // que define:
        //   - Qué paquetes EXPORTA (accesibles desde fuera)
        //   - Qué módulos REQUIERE (dependencias)
        //
        // Sin módulos: cualquier clase public es accesible desde cualquier lugar.
        // Con módulos: solo las clases en paquetes exportados son accesibles.

        ////////////////////////////////////////////////////////////////
        // ESTRUCTURA DE PROYECTO CON MÓDULOS
        ////////////////////////////////////////////////////////////////

        // proyecto/
        // ├── com.myapp.core/                     ← módulo 1
        // │   ├── module-info.java
        // │   └── com/myapp/core/
        // │       ├── User.java
        // │       └── internal/
        // │           └── UserValidator.java       ← paquete interno, NO exportado
        // │
        // ├── com.myapp.web/                       ← módulo 2
        // │   ├── module-info.java
        // │   └── com/myapp/web/
        // │       └── WebApp.java
        // │
        // └── com.myapp.api/                       ← módulo 3
        //     ├── module-info.java
        //     └── com/myapp/api/
        //         └── ApiController.java

        ////////////////////////////////////////////////////////////////
        // MODULE-INFO.JAVA — descriptor del módulo
        ////////////////////////////////////////////////////////////////

        // Ejemplo: module-info.java del módulo core
        //
        // module com.myapp.core {
        //     // Exportar paquetes públicos
        //     exports com.myapp.core;
        //
        //     // NO exportamos internal → nadie fuera puede acceder
        //     // exports com.myapp.core.internal;  ← omitido intencionalmente
        //
        //     // Dependencias que necesitamos
        //     requires java.base;       // implícito, siempre disponible
        //     requires java.sql;        // para JDBC
        //     requires java.logging;    // para logging
        // }

        // Ejemplo: module-info.java del módulo web
        //
        // module com.myapp.web {
        //     requires com.myapp.core;   // depende del módulo core
        //     requires com.myapp.api;
        //
        //     exports com.myapp.web;
        // }

        ////////////////////////////////////////////////////////////////
        // KEYWORDS DE MODULE-INFO
        ////////////////////////////////////////////////////////////////

        System.out.println("=== Keywords de module-info.java ===\n");

        // requires — declarar dependencia
        System.out.println("requires moduleName;");
        System.out.println("  → Este módulo depende de moduleName\n");

        // requires transitive — dependencia transitiva
        System.out.println("requires transitive moduleName;");
        System.out.println("  → Quien dependa de nosotros, también obtiene moduleName\n");

        // exports — hacer visible un paquete
        System.out.println("exports com.myapp.core;");
        System.out.println("  → El paquete es accesible desde otros módulos\n");

        // exports ... to — exportar solo a módulos específicos
        System.out.println("exports com.myapp.core.internal to com.myapp.web;");
        System.out.println("  → Solo com.myapp.web puede acceder al paquete interno\n");

        // opens — permitir acceso por reflection (para frameworks)
        System.out.println("opens com.myapp.core to spring.core;");
        System.out.println("  → Spring puede usar reflection sobre este paquete\n");

        // provides ... with — declarar implementación de servicio
        System.out.println("provides com.myapp.api.Service with com.myapp.core.ServiceImpl;");
        System.out.println("  → ServiceImpl es la implementación de Service (SPI)\n");

        // uses — declarar que se consume un servicio
        System.out.println("uses com.myapp.api.Service;");
        System.out.println("  → Este módulo busca implementaciones de Service\n");

        ////////////////////////////////////////////////////////////////
        // MÓDULOS DE LA JDK
        ////////////////////////////////////////////////////////////////

        System.out.println("=== Módulos principales de la JDK ===\n");

        String[][] jdkModules = {
            {"java.base",    "Siempre disponible: String, List, Map, Math, System..."},
            {"java.sql",     "JDBC: Connection, Statement, ResultSet"},
            {"java.net.http","HttpClient (Java 11+)"},
            {"java.logging", "java.util.logging"},
            {"java.desktop", "AWT, Swing"},
            {"java.xml",     "DOM, SAX, StAX parsers"},
        };

        for (String[] mod : jdkModules) {
            System.out.printf("  %-17s → %s%n", mod[0], mod[1]);
        }

        ////////////////////////////////////////////////////////////////
        // COMPILAR Y EJECUTAR CON MÓDULOS
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== Compilar y ejecutar ===\n");

        System.out.println("# Compilar módulo");
        System.out.println("javac -d out --module-source-path src $(find src -name '*.java')\n");

        System.out.println("# Ejecutar módulo");
        System.out.println("java --module-path out -m com.myapp.core/com.myapp.core.Main\n");

        System.out.println("# Crear JAR modular");
        System.out.println("jar --create --file=myapp.jar --module-version=1.0 -C out/com.myapp.core .\n");

        System.out.println("# Ver info del módulo");
        System.out.println("jar --describe-module --file=myapp.jar\n");

        ////////////////////////////////////////////////////////////////
        // EJEMPLO COMPLETO DE SERVICE PROVIDER INTERFACE (SPI)
        ////////////////////////////////////////////////////////////////

        System.out.println("=== Service Provider Interface (SPI) ===\n");

        // 1. Módulo API define la interfaz:
        //    module com.myapp.api {
        //        exports com.myapp.api;
        //    }
        //    public interface Logger { void log(String msg); }

        // 2. Módulo IMPL proporciona implementación:
        //    module com.myapp.logging {
        //        requires com.myapp.api;
        //        provides com.myapp.api.Logger with com.myapp.logging.ConsoleLogger;
        //    }

        // 3. Módulo CONSUMIDOR usa el servicio:
        //    module com.myapp.core {
        //        requires com.myapp.api;
        //        uses com.myapp.api.Logger;
        //    }
        //    ServiceLoader<Logger> loggers = ServiceLoader.load(Logger.class);
        //    loggers.findFirst().ifPresent(l -> l.log("Hola"));

        // Demostrar ServiceLoader con un servicio del JDK
        System.out.println("Charsets disponibles via SPI:");
        java.nio.charset.Charset.availableCharsets()
            .entrySet().stream()
            .limit(5)
            .forEach(e -> System.out.println("  " + e.getKey()));

        ////////////////////////////////////////////////////////////////
        // CUÁNDO USAR MÓDULOS
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== ¿Cuándo usar módulos? ===\n");
        System.out.println("✓ Proyectos grandes con múltiples equipos");
        System.out.println("✓ Librerías públicas (para encapsular internals)");
        System.out.println("✓ Cuando necesitas control fino de dependencias");
        System.out.println("✓ Para crear JRE custom con jlink\n");
        System.out.println("✗ Proyectos pequeños/medianos (innecesario)");
        System.out.println("✗ Si usas Spring Boot (gestiona dependencias por ti)");
        System.out.println("✗ Si no necesitas encapsular paquetes internos");

        System.out.println("\nModulesDemo: ejemplos completados");
    }
}
