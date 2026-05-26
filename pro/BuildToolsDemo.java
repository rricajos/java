////////////////////////////////////////////////////////////////
// BUILD TOOLS — Maven y Gradle
////////////////////////////////////////////////////////////////

// Las herramientas de build automatizan compilación, testing,
// empaquetado y gestión de dependencias en proyectos Java.
// Este archivo es una GUÍA ejecutable con ejemplos de referencia.
// Compilar: javac BuildToolsDemo.java && java BuildToolsDemo

public class BuildToolsDemo {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // ¿QUÉ ES UNA HERRAMIENTA DE BUILD?
        ////////////////////////////////////////////////////////////////

        System.out.println("=== ¿Qué es una herramienta de build? ===\n");
        System.out.println("Automatiza tareas repetitivas del desarrollo:");
        System.out.println("  1. Compilar código fuente (.java → .class)");
        System.out.println("  2. Gestionar dependencias (librerías externas)");
        System.out.println("  3. Ejecutar tests automáticamente");
        System.out.println("  4. Empaquetar (JAR, WAR, fat-jar)");
        System.out.println("  5. Desplegar a repositorios o servidores");

        ////////////////////////////////////////////////////////////////
        // ESTRUCTURA DE PROYECTO MAVEN
        ////////////////////////////////////////////////////////////////

        // Maven usa convención sobre configuración:
        //
        // mi-proyecto/
        // ├── pom.xml                          ← configuración central
        // ├── src/
        // │   ├── main/
        // │   │   ├── java/                    ← código fuente
        // │   │   │   └── com/miapp/App.java
        // │   │   └── resources/               ← archivos de config
        // │   │       └── application.properties
        // │   └── test/
        // │       ├── java/                    ← tests
        // │       │   └── com/miapp/AppTest.java
        // │       └── resources/
        // └── target/                          ← output (generado)
        //     ├── classes/
        //     └── mi-proyecto-1.0.jar

        ////////////////////////////////////////////////////////////////
        // POM.XML — Project Object Model
        ////////////////////////////////////////////////////////////////

        // <?xml version="1.0" encoding="UTF-8"?>
        // <project xmlns="http://maven.apache.org/POM/4.0.0">
        //     <modelVersion>4.0.0</modelVersion>
        //
        //     <!-- Coordenadas GAV (Group, Artifact, Version) -->
        //     <groupId>com.miapp</groupId>
        //     <artifactId>mi-proyecto</artifactId>
        //     <version>1.0.0</version>
        //     <packaging>jar</packaging>
        //
        //     <!-- Propiedades globales -->
        //     <properties>
        //         <maven.compiler.source>21</maven.compiler.source>
        //         <maven.compiler.target>21</maven.compiler.target>
        //         <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        //     </properties>
        //
        //     <!-- Dependencias -->
        //     <dependencies>
        //         <dependency>
        //             <groupId>org.junit.jupiter</groupId>
        //             <artifactId>junit-jupiter</artifactId>
        //             <version>5.10.0</version>
        //             <scope>test</scope>
        //         </dependency>
        //     </dependencies>
        //
        //     <!-- Plugins -->
        //     <build>
        //         <plugins>
        //             <plugin>
        //                 <groupId>org.apache.maven.plugins</groupId>
        //                 <artifactId>maven-compiler-plugin</artifactId>
        //                 <version>3.11.0</version>
        //             </plugin>
        //         </plugins>
        //     </build>
        // </project>

        ////////////////////////////////////////////////////////////////
        // MAVEN: CICLO DE VIDA
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== Maven: Ciclo de vida ===\n");

        String[][] lifecycle = {
            {"validate",  "Verifica que el proyecto es correcto"},
            {"compile",   "Compila el código fuente → target/classes/"},
            {"test",      "Ejecuta los tests unitarios"},
            {"package",   "Empaqueta en JAR/WAR → target/*.jar"},
            {"verify",    "Ejecuta tests de integración"},
            {"install",   "Copia el JAR al repositorio local (~/.m2)"},
            {"deploy",    "Sube el JAR a un repositorio remoto"},
        };

        for (int i = 0; i < lifecycle.length; i++) {
            System.out.printf("  %d. %-10s → %s%n", i + 1, lifecycle[i][0], lifecycle[i][1]);
        }
        System.out.println("\n  Cada fase ejecuta todas las anteriores:");
        System.out.println("  'mvn package' → validate + compile + test + package");

        ////////////////////////////////////////////////////////////////
        // MAVEN: COMANDOS COMUNES
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== Maven: Comandos ===\n");

        String[][] mvnCommands = {
            {"mvn clean",              "Borra target/ (empezar limpio)"},
            {"mvn compile",            "Compila el código"},
            {"mvn test",               "Ejecuta tests"},
            {"mvn package",            "Genera el JAR/WAR"},
            {"mvn install",            "Instala en repo local"},
            {"mvn clean package",      "Limpia + empaqueta (más común)"},
            {"mvn dependency:tree",    "Muestra árbol de dependencias"},
            {"mvn versions:display-dependency-updates", "Busca actualizaciones"},
        };

        for (String[] cmd : mvnCommands) {
            System.out.printf("  %-45s %s%n", cmd[0], cmd[1]);
        }

        ////////////////////////////////////////////////////////////////
        // MAVEN: SCOPES DE DEPENDENCIAS
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== Maven: Scopes ===\n");

        String[][] scopes = {
            {"compile",  "Disponible en compilación + runtime + tests (por defecto)"},
            {"provided", "Solo compilación (el servidor lo provee, ej: Servlet API)"},
            {"runtime",  "Solo runtime (no en compilación, ej: driver JDBC)"},
            {"test",     "Solo tests (ej: JUnit, Mockito)"},
            {"system",   "Como provided pero con path local (evitar)"},
        };

        for (String[] scope : scopes) {
            System.out.printf("  %-10s → %s%n", scope[0], scope[1]);
        }

        ////////////////////////////////////////////////////////////////
        // MAVEN: PLUGINS COMUNES
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== Maven: Plugins comunes ===\n");

        String[][] plugins = {
            {"compiler-plugin",     "Configura versión Java para compilación"},
            {"surefire-plugin",     "Ejecuta tests unitarios (JUnit, TestNG)"},
            {"failsafe-plugin",    "Ejecuta tests de integración"},
            {"shade-plugin",       "Crea fat-jar (JAR con todas las dependencias)"},
            {"spring-boot-plugin", "Empaqueta y ejecuta aplicaciones Spring Boot"},
            {"javadoc-plugin",     "Genera documentación HTML desde Javadoc"},
        };

        for (String[] p : plugins) {
            System.out.printf("  %-22s → %s%n", p[0], p[1]);
        }

        ////////////////////////////////////////////////////////////////
        // ESTRUCTURA DE PROYECTO GRADLE
        ////////////////////////////////////////////////////////////////

        // Gradle usa la misma estructura de carpetas que Maven:
        //
        // mi-proyecto/
        // ├── build.gradle          ← o build.gradle.kts (Kotlin DSL)
        // ├── settings.gradle
        // ├── gradle/
        // │   └── wrapper/
        // │       ├── gradle-wrapper.jar
        // │       └── gradle-wrapper.properties
        // ├── gradlew               ← wrapper Linux/Mac
        // ├── gradlew.bat           ← wrapper Windows
        // ├── src/
        // │   ├── main/java/...
        // │   └── test/java/...
        // └── build/                ← output (generado)

        ////////////////////////////////////////////////////////////////
        // BUILD.GRADLE — configuración con Groovy DSL
        ////////////////////////////////////////////////////////////////

        // plugins {
        //     id 'java'
        //     id 'application'                // para crear ejecutables
        //     id 'org.springframework.boot' version '3.2.0' // Spring Boot
        // }
        //
        // group = 'com.miapp'
        // version = '1.0.0'
        //
        // java {
        //     sourceCompatibility = JavaVersion.VERSION_21
        //     targetCompatibility = JavaVersion.VERSION_21
        // }
        //
        // repositories {
        //     mavenCentral()                  // repositorio principal
        //     mavenLocal()                    // ~/.m2/repository
        //     maven { url 'https://repo.empresa.com/maven' } // privado
        // }
        //
        // dependencies {
        //     implementation 'com.google.guava:guava:32.1.3-jre'
        //     compileOnly   'org.projectlombok:lombok:1.18.30'
        //     runtimeOnly   'mysql:mysql-connector-j:8.2.0'
        //     testImplementation 'org.junit.jupiter:junit-jupiter:5.10.0'
        // }
        //
        // application {
        //     mainClass = 'com.miapp.App'
        // }

        ////////////////////////////////////////////////////////////////
        // GRADLE: COMANDOS COMUNES
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== Gradle: Comandos ===\n");

        String[][] gradleCommands = {
            {"gradle build",          "Compila + tests + empaqueta"},
            {"gradle test",           "Ejecuta tests"},
            {"gradle clean",          "Borra build/"},
            {"gradle run",            "Ejecuta la app (plugin application)"},
            {"gradle dependencies",   "Muestra árbol de dependencias"},
            {"gradle tasks",          "Lista todas las tareas disponibles"},
            {"./gradlew build",       "Usa el wrapper (recomendado)"},
        };

        for (String[] cmd : gradleCommands) {
            System.out.printf("  %-30s %s%n", cmd[0], cmd[1]);
        }

        ////////////////////////////////////////////////////////////////
        // GRADLE: CONFIGURACIONES DE DEPENDENCIAS
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== Gradle: Configuraciones ===\n");

        String[][] gradleConfs = {
            {"implementation",     "Dependencia de compilación y runtime"},
            {"api",                "Como implementation pero se expone transitivamente"},
            {"compileOnly",        "Solo compilación (como Maven provided)"},
            {"runtimeOnly",        "Solo runtime (ej: driver JDBC)"},
            {"testImplementation", "Solo para tests"},
            {"annotationProcessor","Procesadores de anotaciones (Lombok, MapStruct)"},
        };

        for (String[] conf : gradleConfs) {
            System.out.printf("  %-22s → %s%n", conf[0], conf[1]);
        }

        ////////////////////////////////////////////////////////////////
        // MAVEN vs GRADLE — comparación
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== Maven vs Gradle ===\n");

        System.out.printf("  %-20s %-22s %-22s%n", "Aspecto", "Maven", "Gradle");
        System.out.println("  " + "─".repeat(64));

        String[][] comparison = {
            {"Config",         "XML (pom.xml)",       "Groovy/Kotlin DSL"},
            {"Velocidad",      "Más lento",           "Más rápido (caché)"},
            {"Flexibilidad",   "Convención estricta", "Muy flexible"},
            {"Curva aprend.",  "Más fácil empezar",   "Más potente"},
            {"IDE soporte",    "Excelente",           "Excelente"},
            {"Multi-módulo",   "Bueno",               "Mejor"},
            {"Popularidad",    "Más usado (legacy)",  "Creciendo rápido"},
            {"Android",        "No recomendado",      "Estándar oficial"},
        };

        for (String[] row : comparison) {
            System.out.printf("  %-20s %-22s %-22s%n", row[0], row[1], row[2]);
        }

        ////////////////////////////////////////////////////////////////
        // WRAPPERS — gradlew / mvnw
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== Wrappers ===\n");
        System.out.println("Los wrappers incluyen la herramienta de build en el proyecto.");
        System.out.println("Ventaja: no hace falta instalar Maven/Gradle globalmente.\n");
        System.out.println("  Maven Wrapper:");
        System.out.println("    mvn wrapper:wrapper                → genera mvnw");
        System.out.println("    ./mvnw clean package               → usa el wrapper\n");
        System.out.println("  Gradle Wrapper:");
        System.out.println("    gradle wrapper                     → genera gradlew");
        System.out.println("    ./gradlew build                    → usa el wrapper");
        System.out.println("\n  Siempre subir mvnw/gradlew al repositorio git.");
        System.out.println("  Así todos en el equipo usan la misma versión.");

        ////////////////////////////////////////////////////////////////
        // CUÁNDO USAR CADA UNO
        ////////////////////////////////////////////////////////////////

        System.out.println("\n=== ¿Cuándo usar cada uno? ===\n");
        System.out.println("Maven:");
        System.out.println("  ✓ Proyectos empresariales tradicionales");
        System.out.println("  ✓ Equipos que prefieren convención sobre configuración");
        System.out.println("  ✓ Si el equipo ya conoce Maven");
        System.out.println("  ✓ Proyectos que requieren reproducibilidad estricta\n");
        System.out.println("Gradle:");
        System.out.println("  ✓ Proyectos Android (obligatorio)");
        System.out.println("  ✓ Proyectos con builds complejos o personalizados");
        System.out.println("  ✓ Multi-módulo grandes (mejor rendimiento)");
        System.out.println("  ✓ Kotlin (DSL nativo con build.gradle.kts)");

        System.out.println("\nBuildToolsDemo: ejemplos completados");
    }
}
