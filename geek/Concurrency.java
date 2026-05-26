////////////////////////////////////////////////////////////////
// CONCURRENCIA Y MULTITHREADING
////////////////////////////////////////////////////////////////

import java.util.concurrent.*;
import java.util.List;

public class Concurrency {

    ////////////////////////////////////////////////////////////////
    // CREAR THREADS
    ////////////////////////////////////////////////////////////////

    // Opción 1: Extender Thread
    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Thread: " + Thread.currentThread().getName());
        }
    }

    // Opción 2: Implementar Runnable (preferida)
    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            System.out.println("Runnable: " + Thread.currentThread().getName());
        }
    }

    ////////////////////////////////////////////////////////////////
    // SYNCHRONIZED — evitar race conditions
    ////////////////////////////////////////////////////////////////

    static class Counter {
        private int count = 0;

        // Método sincronizado — solo un thread a la vez
        public synchronized void increment() {
            count++;
        }

        // Bloque sincronizado — más granular
        public void incrementBlock() {
            synchronized (this) {
                count++;
            }
        }

        public int getCount() {
            return count;
        }
    }

    ////////////////////////////////////////////////////////////////
    // VOLATILE — visibilidad entre threads
    ////////////////////////////////////////////////////////////////

    // volatile garantiza que todos los threads ven el último valor
    static volatile boolean running = true;

    static class Worker implements Runnable {
        @Override
        public void run() {
            while (running) {
                // trabajar...
            }
            System.out.println("Worker detenido");
        }
    }

    ////////////////////////////////////////////////////////////////
    // CALLABLE — thread que devuelve resultado
    ////////////////////////////////////////////////////////////////

    static class FactorialTask implements Callable<Long> {
        private final int number;

        public FactorialTask(int number) {
            this.number = number;
        }

        @Override
        public Long call() {
            long result = 1;
            for (int i = 2; i <= number; i++) {
                result *= i;
            }
            return result;
        }
    }

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws Exception {

        ////////////////////////////////////////////////////////////////
        // THREADS BÁSICOS
        ////////////////////////////////////////////////////////////////

        // Extender Thread
        MyThread t1 = new MyThread();
        t1.start();  // start() crea un nuevo thread, run() no

        // Implementar Runnable
        Thread t2 = new Thread(new MyRunnable());
        t2.start();

        // Lambda (lo más conciso)
        Thread t3 = new Thread(() -> {
            System.out.println("Lambda thread: " + Thread.currentThread().getName());
        });
        t3.start();

        // Esperar a que terminen
        t1.join();
        t2.join();
        t3.join();

        ////////////////////////////////////////////////////////////////
        // THREAD.SLEEP Y ESTADOS
        ////////////////////////////////////////////////////////////////

        Thread sleepy = new Thread(() -> {
            try {
                System.out.println("Durmiendo...");
                Thread.sleep(1000);  // pausa 1 segundo
                System.out.println("Despierto!");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        sleepy.start();
        sleepy.join();

        ////////////////////////////////////////////////////////////////
        // SYNCHRONIZED — ejemplo práctico
        ////////////////////////////////////////////////////////////////

        Counter counter = new Counter();
        List<Thread> threads = new java.util.ArrayList<>();

        // 100 threads incrementan el contador 1000 veces cada uno
        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    counter.increment();
                }
            });
            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Counter (sync): " + counter.getCount()); // 100000

        ////////////////////////////////////////////////////////////////
        // EXECUTOR SERVICE — pool de threads
        ////////////////////////////////////////////////////////////////

        // Fixed thread pool — número fijo de threads
        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                System.out.println("Tarea " + taskId + " en " + Thread.currentThread().getName());
            });
        }

        executor.shutdown();  // no acepta más tareas
        executor.awaitTermination(5, TimeUnit.SECONDS); // espera a que terminen

        // Otros tipos de pools:
        // Executors.newSingleThreadExecutor()   — 1 thread
        // Executors.newCachedThreadPool()        — threads bajo demanda
        // Executors.newScheduledThreadPool(n)    — tareas programadas

        ////////////////////////////////////////////////////////////////
        // FUTURE — resultado asíncrono
        ////////////////////////////////////////////////////////////////

        ExecutorService pool = Executors.newFixedThreadPool(2);

        Future<Long> future = pool.submit(new FactorialTask(10));

        // Hacer otras cosas mientras se calcula...
        System.out.println("Calculando factorial...");

        Long result = future.get();  // bloquea hasta obtener resultado
        System.out.println("10! = " + result);  // 3628800

        pool.shutdown();

        ////////////////////////////////////////////////////////////////
        // COMPLETABLE FUTURE (Java 8+) — async/await de Java
        ////////////////////////////////////////////////////////////////

        // Ejecutar async
        CompletableFuture<String> cf = CompletableFuture.supplyAsync(() -> {
            // Simular trabajo pesado
            return "Resultado";
        });

        // Encadenar transformaciones (como Promises)
        CompletableFuture<String> chain = cf
            .thenApply(s -> s.toUpperCase())        // transformar
            .thenApply(s -> s + "!!!");              // otra transformación

        System.out.println(chain.get());  // "RESULTADO!!!"

        // thenAccept — consumir sin devolver
        CompletableFuture.supplyAsync(() -> "Hola")
            .thenAccept(s -> System.out.println("Recibido: " + s));

        // thenCompose — encadenar otro CompletableFuture (flatMap)
        CompletableFuture<String> composed = CompletableFuture
            .supplyAsync(() -> "hello")
            .thenCompose(s -> CompletableFuture.supplyAsync(() -> s + " world"));

        // allOf — esperar a todos
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "A");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "B");
        CompletableFuture<Void> all = CompletableFuture.allOf(f1, f2);
        all.get();

        // anyOf — el primero que termine
        CompletableFuture<Object> any = CompletableFuture.anyOf(f1, f2);
        System.out.println("Primero: " + any.get());

        // Manejo de errores
        CompletableFuture<String> withError = CompletableFuture
            .supplyAsync(() -> {
                if (true) throw new RuntimeException("Error!");
                return "OK";
            })
            .exceptionally(ex -> "Fallback: " + ex.getMessage());
        System.out.println(withError.get());  // "Fallback: ..."

        ////////////////////////////////////////////////////////////////
        // CONCURRENT COLLECTIONS
        ////////////////////////////////////////////////////////////////

        // Thread-safe sin synchronized manual
        ConcurrentHashMap<String, Integer> concMap = new ConcurrentHashMap<>();
        concMap.put("a", 1);
        concMap.putIfAbsent("b", 2);

        CopyOnWriteArrayList<String> concList = new CopyOnWriteArrayList<>();
        concList.add("safe");

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        queue.put("item");           // bloquea si está llena
        String item = queue.take();  // bloquea si está vacía
    }
}
