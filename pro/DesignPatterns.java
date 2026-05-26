////////////////////////////////////////////////////////////////
// DESIGN PATTERNS — patrones de diseño más usados en Java
////////////////////////////////////////////////////////////////

import java.util.*;

public class DesignPatterns {

    ////////////////////////////////////////////////////////////////
    // 1. SINGLETON — una sola instancia en toda la aplicación
    ////////////////////////////////////////////////////////////////

    static class DatabaseConnection {
        private static volatile DatabaseConnection instance;
        private final String url;

        private DatabaseConnection(String url) {
            this.url = url;
            System.out.println("  Conexión creada: " + url);
        }

        // Double-checked locking (thread-safe)
        public static DatabaseConnection getInstance() {
            if (instance == null) {
                synchronized (DatabaseConnection.class) {
                    if (instance == null) {
                        instance = new DatabaseConnection("jdbc:mysql://localhost/db");
                    }
                }
            }
            return instance;
        }

        public String getUrl() { return url; }
    }

    // Alternativa moderna: enum Singleton (más simple, thread-safe)
    enum AppConfig {
        INSTANCE;

        private String appName = "MyApp";
        public String getAppName() { return appName; }
        public void setAppName(String name) { this.appName = name; }
    }

    ////////////////////////////////////////////////////////////////
    // 2. FACTORY METHOD — crear objetos sin especificar clase exacta
    ////////////////////////////////////////////////////////////////

    interface Notification {
        void send(String message);
    }

    static class EmailNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("  Email: " + message);
        }
    }

    static class SMSNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("  SMS: " + message);
        }
    }

    static class PushNotification implements Notification {
        @Override
        public void send(String message) {
            System.out.println("  Push: " + message);
        }
    }

    // Factory
    static class NotificationFactory {
        static Notification create(String type) {
            return switch (type.toLowerCase()) {
                case "email" -> new EmailNotification();
                case "sms"   -> new SMSNotification();
                case "push"  -> new PushNotification();
                default -> throw new IllegalArgumentException("Tipo desconocido: " + type);
            };
        }
    }

    ////////////////////////////////////////////////////////////////
    // 3. BUILDER — construir objetos complejos paso a paso
    ////////////////////////////////////////////////////////////////

    static class HttpRequest {
        private final String method;
        private final String url;
        private final Map<String, String> headers;
        private final String body;

        private HttpRequest(Builder builder) {
            this.method = builder.method;
            this.url = builder.url;
            this.headers = builder.headers;
            this.body = builder.body;
        }

        @Override
        public String toString() {
            return method + " " + url + " headers=" + headers
                + (body != null ? " body=" + body : "");
        }

        static class Builder {
            private String method = "GET";
            private String url;
            private Map<String, String> headers = new HashMap<>();
            private String body;

            Builder(String url) { this.url = url; }

            Builder method(String method) { this.method = method; return this; }
            Builder header(String key, String value) { headers.put(key, value); return this; }
            Builder body(String body) { this.body = body; return this; }

            HttpRequest build() {
                if (url == null) throw new IllegalStateException("URL requerida");
                return new HttpRequest(this);
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // 4. OBSERVER — notificar cambios a múltiples objetos
    ////////////////////////////////////////////////////////////////

    interface EventListener {
        void onEvent(String event, String data);
    }

    static class EventManager {
        private final Map<String, List<EventListener>> listeners = new HashMap<>();

        void subscribe(String event, EventListener listener) {
            listeners.computeIfAbsent(event, k -> new ArrayList<>()).add(listener);
        }

        void unsubscribe(String event, EventListener listener) {
            List<EventListener> list = listeners.get(event);
            if (list != null) list.remove(listener);
        }

        void notify(String event, String data) {
            List<EventListener> list = listeners.get(event);
            if (list != null) {
                list.forEach(l -> l.onEvent(event, data));
            }
        }
    }

    static class UserService {
        final EventManager events = new EventManager();

        void createUser(String name) {
            System.out.println("  Usuario creado: " + name);
            events.notify("user.created", name);
        }
    }

    ////////////////////////////////////////////////////////////////
    // 5. STRATEGY — intercambiar algoritmos en runtime
    ////////////////////////////////////////////////////////////////

    interface SortStrategy {
        void sort(int[] array);
        String name();
    }

    static class BubbleSort implements SortStrategy {
        @Override
        public void sort(int[] array) {
            for (int i = 0; i < array.length - 1; i++)
                for (int j = 0; j < array.length - i - 1; j++)
                    if (array[j] > array[j + 1]) {
                        int temp = array[j];
                        array[j] = array[j + 1];
                        array[j + 1] = temp;
                    }
        }
        @Override
        public String name() { return "BubbleSort"; }
    }

    static class QuickSort implements SortStrategy {
        @Override
        public void sort(int[] array) {
            Arrays.sort(array); // simplificado
        }
        @Override
        public String name() { return "QuickSort"; }
    }

    static class Sorter {
        private SortStrategy strategy;

        Sorter(SortStrategy strategy) { this.strategy = strategy; }

        void setStrategy(SortStrategy strategy) { this.strategy = strategy; }

        void sort(int[] array) {
            System.out.println("  Usando: " + strategy.name());
            strategy.sort(array);
        }
    }

    ////////////////////////////////////////////////////////////////
    // 6. DECORATOR — añadir funcionalidad sin modificar la clase
    ////////////////////////////////////////////////////////////////

    interface DataSource {
        void writeData(String data);
        String readData();
    }

    static class FileDataSource implements DataSource {
        private String data = "";

        @Override
        public void writeData(String data) { this.data = data; }
        @Override
        public String readData() { return data; }
    }

    // Decorator base
    static abstract class DataSourceDecorator implements DataSource {
        protected DataSource wrapped;
        DataSourceDecorator(DataSource source) { this.wrapped = source; }
    }

    // Decorator concreto: encriptar
    static class EncryptionDecorator extends DataSourceDecorator {
        EncryptionDecorator(DataSource source) { super(source); }

        @Override
        public void writeData(String data) {
            // "Encriptar" (simplificado: invertir string)
            wrapped.writeData(new StringBuilder(data).reverse().toString());
        }

        @Override
        public String readData() {
            return new StringBuilder(wrapped.readData()).reverse().toString();
        }
    }

    // Decorator concreto: comprimir
    static class CompressionDecorator extends DataSourceDecorator {
        CompressionDecorator(DataSource source) { super(source); }

        @Override
        public void writeData(String data) {
            wrapped.writeData("[compressed:" + data.length() + "]" + data);
        }

        @Override
        public String readData() {
            String raw = wrapped.readData();
            int idx = raw.indexOf("]");
            return idx >= 0 ? raw.substring(idx + 1) : raw;
        }
    }

    ////////////////////////////////////////////////////////////////
    // 7. ADAPTER — compatibilizar interfaces incompatibles
    ////////////////////////////////////////////////////////////////

    // Interfaz que espera nuestro código
    interface JsonParser {
        String toJson(Object obj);
    }

    // Librería externa con interfaz diferente
    static class XmlConverter {
        String convertToXml(Object obj) {
            return "<object>" + obj.toString() + "</object>";
        }
    }

    // Adapter: hace que XmlConverter funcione como JsonParser
    static class XmlToJsonAdapter implements JsonParser {
        private final XmlConverter converter;

        XmlToJsonAdapter(XmlConverter converter) {
            this.converter = converter;
        }

        @Override
        public String toJson(Object obj) {
            String xml = converter.convertToXml(obj);
            // Conversión simplificada
            return "{\"data\": \"" + xml + "\"}";
        }
    }

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) {

        // 1. Singleton
        System.out.println("=== SINGLETON ===");
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        System.out.println("  Misma instancia: " + (db1 == db2)); // true

        AppConfig.INSTANCE.setAppName("SuperApp");
        System.out.println("  Config: " + AppConfig.INSTANCE.getAppName());

        // 2. Factory
        System.out.println("\n=== FACTORY ===");
        Notification email = NotificationFactory.create("email");
        Notification sms = NotificationFactory.create("sms");
        email.send("Bienvenido!");
        sms.send("Código: 1234");

        // 3. Builder
        System.out.println("\n=== BUILDER ===");
        HttpRequest request = new HttpRequest.Builder("https://api.example.com/users")
            .method("POST")
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer token123")
            .body("{\"name\": \"Ana\"}")
            .build();
        System.out.println("  " + request);

        // 4. Observer
        System.out.println("\n=== OBSERVER ===");
        UserService userService = new UserService();
        userService.events.subscribe("user.created",
            (event, data) -> System.out.println("  Log: " + event + " → " + data));
        userService.events.subscribe("user.created",
            (event, data) -> System.out.println("  Email de bienvenida a: " + data));
        userService.createUser("Ana");

        // 5. Strategy
        System.out.println("\n=== STRATEGY ===");
        int[] arr = {5, 2, 8, 1, 9};
        Sorter sorter = new Sorter(new BubbleSort());
        sorter.sort(arr);
        System.out.println("  Resultado: " + Arrays.toString(arr));

        arr = new int[]{5, 2, 8, 1, 9};
        sorter.setStrategy(new QuickSort());
        sorter.sort(arr);
        System.out.println("  Resultado: " + Arrays.toString(arr));

        // 6. Decorator
        System.out.println("\n=== DECORATOR ===");
        DataSource source = new FileDataSource();
        DataSource encrypted = new EncryptionDecorator(source);
        DataSource compressed = new CompressionDecorator(encrypted);

        compressed.writeData("Hola Mundo Secreto");
        System.out.println("  Escrito (raw): " + source.readData());
        System.out.println("  Leído (decoded): " + compressed.readData());

        // 7. Adapter
        System.out.println("\n=== ADAPTER ===");
        JsonParser parser = new XmlToJsonAdapter(new XmlConverter());
        System.out.println("  " + parser.toJson("datos importantes"));
    }
}
