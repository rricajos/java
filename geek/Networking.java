////////////////////////////////////////////////////////////////
// NETWORKING — HTTP Client, Sockets
////////////////////////////////////////////////////////////////

import java.net.*;
import java.net.http.*;
import java.io.*;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class Networking {
    public static void main(String[] args) throws Exception {

        ////////////////////////////////////////////////////////////////
        // HTTP CLIENT (Java 11+) — API moderna
        ////////////////////////////////////////////////////////////////

        // Crear cliente reutilizable
        HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

        // Cliente simple (defaults)
        HttpClient simpleClient = HttpClient.newHttpClient();

        ////////////////////////////////////////////////////////////////
        // GET REQUEST
        ////////////////////////////////////////////////////////////////

        HttpRequest getRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
            .header("Accept", "application/json")
            .GET()  // GET es el default, opcional
            .build();

        // Enviar de forma síncrona
        HttpResponse<String> response = client.send(
            getRequest,
            HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.body().substring(0, 100) + "...");
        System.out.println("Content-Type: " + response.headers().firstValue("content-type").orElse("?"));

        ////////////////////////////////////////////////////////////////
        // POST REQUEST
        ////////////////////////////////////////////////////////////////

        String jsonBody = """
            {
                "title": "Nuevo post",
                "body": "Contenido del post",
                "userId": 1
            }
            """;

        HttpRequest postRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://jsonplaceholder.typicode.com/posts"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        HttpResponse<String> postResponse = client.send(
            postRequest,
            HttpResponse.BodyHandlers.ofString()
        );

        System.out.println("\nPOST Status: " + postResponse.statusCode());
        System.out.println("POST Body: " + postResponse.body());

        ////////////////////////////////////////////////////////////////
        // REQUEST ASÍNCRONO
        ////////////////////////////////////////////////////////////////

        CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(
            getRequest,
            HttpResponse.BodyHandlers.ofString()
        );

        // No bloquea — hacer otras cosas mientras...
        System.out.println("\nEsperando respuesta async...");

        futureResponse
            .thenApply(HttpResponse::body)
            .thenApply(body -> body.substring(0, 50))
            .thenAccept(body -> System.out.println("Async: " + body + "..."))
            .join(); // esperar resultado

        ////////////////////////////////////////////////////////////////
        // PUT, DELETE
        ////////////////////////////////////////////////////////////////

        // PUT
        HttpRequest putRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();

        // DELETE
        HttpRequest deleteRequest = HttpRequest.newBuilder()
            .uri(URI.create("https://jsonplaceholder.typicode.com/posts/1"))
            .DELETE()
            .build();

        HttpResponse<String> deleteResponse = client.send(
            deleteRequest, HttpResponse.BodyHandlers.ofString()
        );
        System.out.println("\nDELETE Status: " + deleteResponse.statusCode());

        ////////////////////////////////////////////////////////////////
        // BODY HANDLERS — distintos tipos de respuesta
        ////////////////////////////////////////////////////////////////

        // String
        // HttpResponse.BodyHandlers.ofString()

        // byte[]
        // HttpResponse.BodyHandlers.ofByteArray()

        // Guardar como archivo
        // HttpResponse.BodyHandlers.ofFile(Path.of("output.json"))

        // Stream de líneas
        // HttpResponse.BodyHandlers.ofLines()

        // Descartar body
        // HttpResponse.BodyHandlers.discarding()

        ////////////////////////////////////////////////////////////////
        // URI — manipular URLs
        ////////////////////////////////////////////////////////////////

        URI uri = URI.create("https://example.com:8080/path?key=value#section");

        System.out.println("\nScheme: " + uri.getScheme());     // https
        System.out.println("Host: " + uri.getHost());           // example.com
        System.out.println("Port: " + uri.getPort());           // 8080
        System.out.println("Path: " + uri.getPath());           // /path
        System.out.println("Query: " + uri.getQuery());         // key=value
        System.out.println("Fragment: " + uri.getFragment());   // section

        // URL encoding
        String encoded = URLEncoder.encode("hola mundo & más", "UTF-8");
        String decoded = URLDecoder.decode(encoded, "UTF-8");
        System.out.println("Encoded: " + encoded);  // hola+mundo+%26+m%C3%A1s
        System.out.println("Decoded: " + decoded);   // hola mundo & más

        ////////////////////////////////////////////////////////////////
        // SOCKETS — TCP básico (nivel bajo)
        ////////////////////////////////////////////////////////////////

        // Servidor (se ejecutaría en otro thread/proceso)
        // try (ServerSocket server = new ServerSocket(8080)) {
        //     System.out.println("Servidor escuchando en 8080...");
        //     Socket clientSocket = server.accept();
        //     BufferedReader in = new BufferedReader(
        //         new InputStreamReader(clientSocket.getInputStream()));
        //     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        //     String line = in.readLine();
        //     out.println("Echo: " + line);
        // }

        // Cliente
        // try (Socket socket = new Socket("localhost", 8080)) {
        //     PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        //     BufferedReader in = new BufferedReader(
        //         new InputStreamReader(socket.getInputStream()));
        //     out.println("Hola servidor");
        //     String response = in.readLine();
        //     System.out.println("Respuesta: " + response);
        // }

        // Para Sockets, usar HttpClient es preferible en la mayoría de casos.
        // Los sockets crudos son útiles para protocolos custom o servidores TCP.

        System.out.println("\nNetworking: ejemplos completados");
    }
}
