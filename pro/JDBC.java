////////////////////////////////////////////////////////////////
// JDBC — Java Database Connectivity
////////////////////////////////////////////////////////////////

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBC {

    ////////////////////////////////////////////////////////////////
    // CONEXIÓN A BASE DE DATOS
    ////////////////////////////////////////////////////////////////

    // URL de conexión según motor:
    // MySQL:      jdbc:mysql://localhost:3306/mydb
    // PostgreSQL: jdbc:postgresql://localhost:5432/mydb
    // SQLite:     jdbc:sqlite:mydb.db
    // H2 (memoria): jdbc:h2:mem:testdb

    static final String URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    static final String USER = "sa";
    static final String PASSWORD = "";

    ////////////////////////////////////////////////////////////////
    // MODELO
    ////////////////////////////////////////////////////////////////

    record User(int id, String name, String email, int age) {}

    ////////////////////////////////////////////////////////////////
    // CREAR CONEXIÓN
    ////////////////////////////////////////////////////////////////

    static Connection getConnection() throws SQLException {
        // DriverManager busca el driver correcto según la URL
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    ////////////////////////////////////////////////////////////////
    // DDL — CREATE TABLE
    ////////////////////////////////////////////////////////////////

    static void createTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS users (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE,
                age INT DEFAULT 0
            )
            """;

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Tabla 'users' creada");
        }
    }

    ////////////////////////////////////////////////////////////////
    // INSERT — PreparedStatement (previene SQL injection)
    ////////////////////////////////////////////////////////////////

    static int insertUser(String name, String email, int age) throws SQLException {
        String sql = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Los ? se reemplazan con valores tipados (1-indexed)
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, age);

            int rows = ps.executeUpdate(); // devuelve filas afectadas
            System.out.println("Insertadas: " + rows + " fila(s)");

            // Obtener ID generado
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    System.out.println("ID generado: " + id);
                    return id;
                }
            }
        }
        return -1;
    }

    ////////////////////////////////////////////////////////////////
    // SELECT — leer datos
    ////////////////////////////////////////////////////////////////

    static List<User> findAllUsers() throws SQLException {
        String sql = "SELECT id, name, email, age FROM users ORDER BY id";
        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // ResultSet es un cursor — next() avanza a la siguiente fila
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getInt("age")
                );
                users.add(user);
            }
        }
        return users;
    }

    static User findUserById(int id) throws SQLException {
        String sql = "SELECT id, name, email, age FROM users WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("age")
                    );
                }
            }
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////
    // UPDATE
    ////////////////////////////////////////////////////////////////

    static int updateUserEmail(int id, String newEmail) throws SQLException {
        String sql = "UPDATE users SET email = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newEmail);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            System.out.println("Actualizadas: " + rows + " fila(s)");
            return rows;
        }
    }

    ////////////////////////////////////////////////////////////////
    // DELETE
    ////////////////////////////////////////////////////////////////

    static int deleteUser(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            System.out.println("Eliminadas: " + rows + " fila(s)");
            return rows;
        }
    }

    ////////////////////////////////////////////////////////////////
    // TRANSACCIONES
    ////////////////////////////////////////////////////////////////

    static void transferExample() throws SQLException {
        Connection conn = getConnection();

        try {
            // Desactivar auto-commit (cada statement ya no es una transacción)
            conn.setAutoCommit(false);

            // Operación 1
            try (PreparedStatement ps1 = conn.prepareStatement(
                    "UPDATE users SET age = age - 1 WHERE id = 1")) {
                ps1.executeUpdate();
            }

            // Operación 2
            try (PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE users SET age = age + 1 WHERE id = 2")) {
                ps2.executeUpdate();
            }

            // Si todo va bien → commit
            conn.commit();
            System.out.println("Transacción completada");

        } catch (SQLException e) {
            // Si algo falla → rollback (deshacer todo)
            conn.rollback();
            System.out.println("Transacción revertida: " + e.getMessage());
        } finally {
            conn.setAutoCommit(true);
            conn.close();
        }
    }

    ////////////////////////////////////////////////////////////////
    // BATCH — ejecutar múltiples operaciones a la vez
    ////////////////////////////////////////////////////////////////

    static void batchInsert(List<User> users) throws SQLException {
        String sql = "INSERT INTO users (name, email, age) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);

            for (User user : users) {
                ps.setString(1, user.name());
                ps.setString(2, user.email());
                ps.setInt(3, user.age());
                ps.addBatch(); // acumula la operación
            }

            int[] results = ps.executeBatch(); // ejecuta todas de golpe
            conn.commit();
            System.out.println("Batch: " + results.length + " operaciones");
        }
    }

    ////////////////////////////////////////////////////////////////
    // STATEMENT vs PREPAREDSTATEMENT
    ////////////////////////////////////////////////////////////////

    // Statement    → SQL como String directo. VULNERABLE a SQL injection.
    // PreparedStatement → SQL con ? placeholders. SEGURO y más eficiente.
    //
    // SIEMPRE usar PreparedStatement con datos de usuario.
    //
    // MAL (SQL injection):
    //   stmt.executeQuery("SELECT * FROM users WHERE name = '" + input + "'");
    //   Si input = "'; DROP TABLE users; --" → desastre
    //
    // BIEN:
    //   ps = conn.prepareStatement("SELECT * FROM users WHERE name = ?");
    //   ps.setString(1, input);

    ////////////////////////////////////////////////////////////////
    // MAIN
    ////////////////////////////////////////////////////////////////

    public static void main(String[] args) throws SQLException {

        // Crear tabla
        createTable();

        // INSERT
        int id1 = insertUser("Ana", "ana@test.com", 25);
        int id2 = insertUser("Luis", "luis@test.com", 30);
        int id3 = insertUser("Eva", "eva@test.com", 28);

        // SELECT ALL
        System.out.println("\nTodos los usuarios:");
        findAllUsers().forEach(u -> System.out.println("  " + u));

        // SELECT BY ID
        User found = findUserById(id1);
        System.out.println("\nPor ID: " + found);

        // UPDATE
        updateUserEmail(id1, "ana.new@test.com");

        // DELETE
        deleteUser(id3);

        // Verificar
        System.out.println("\nTras update y delete:");
        findAllUsers().forEach(u -> System.out.println("  " + u));

        // Transacción
        transferExample();

        // Resultado final
        System.out.println("\nResultado final:");
        findAllUsers().forEach(u -> System.out.println("  " + u));
    }
}
