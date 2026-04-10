package org.example.jdbc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class JdbcUtilTest {

    private JdbcUtil jdbc;

    @BeforeEach
    void setup() {
        jdbc = new JdbcUtil(
                "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
                "sa",
                ""
        );

        jdbc.execute("CREATE TABLE IF NOT EXISTS users (id INT, name VARCHAR(50))");

        jdbc.execute("DELETE FROM users");
    }

    // -----------------------------------------
    // NORMAL CASES
    // -----------------------------------------

    @Test
    void shouldInsertData_usingExecute() {
        jdbc.execute("INSERT INTO users VALUES (?, ?)", 1, "Satish");

        Optional<String> result = jdbc.findOne(
                "SELECT name FROM users WHERE id = ?",
                rs -> {
                    try { return rs.getString("name"); }
                    catch (Exception e) { throw new RuntimeException(e); }
                },
                1
        );

        assertTrue(result.isPresent());
        assertEquals("Satish", result.get());
    }

    @Test
    void shouldInsertData_usingConsumer() {

        jdbc.execute("INSERT INTO users VALUES (?, ?)", ps -> {
            try {
                ps.setInt(1, 2);
                ps.setString(2, "Kumar");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        List<String> result = jdbc.findMany(
                "SELECT name FROM users",
                rs -> {
                    try { return rs.getString("name"); }
                    catch (Exception e) { throw new RuntimeException(e); }
                }
        );

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmpty_whenNoResult() {

        Optional<String> result = jdbc.findOne(
                "SELECT name FROM users WHERE id = ?",
                rs -> {
                    try { return rs.getString("name"); }
                    catch (Exception e) { throw new RuntimeException(e); }
                },
                999
        );

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReturnMultipleResults() {

        jdbc.execute("INSERT INTO users VALUES (?, ?)", 1, "A");
        jdbc.execute("INSERT INTO users VALUES (?, ?)", 2, "B");

        List<String> result = jdbc.findMany(
                "SELECT name FROM users ORDER BY id",
                rs -> {
                    try { return rs.getString("name"); }
                    catch (Exception e) { throw new RuntimeException(e); }
                }
        );

        assertEquals(2, result.size());
    }

    // -----------------------------------------
    // 🔥 MISSING BRANCHES (IMPORTANT)
    // -----------------------------------------

    @Test
    void shouldThrowException_whenExecuteFails() {

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                jdbc.execute("INVALID SQL HERE")
        );

        assertTrue(ex.getMessage().contains("Error executing query"));
    }

    @Test
    void shouldThrowException_whenExecuteConsumerFails() {

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                jdbc.execute("INSERT INTO users VALUES (?, ?)", ps -> {
                    throw new RuntimeException("Consumer failure");
                })
        );

        assertEquals("Consumer failure", ex.getMessage());
    }

    @Test
    void shouldThrowException_whenFindOneFails() {

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                jdbc.findOne("INVALID SQL", rs -> "x")
        );

        assertTrue(ex.getMessage().contains("Error executing query"));
    }

    @Test
    void shouldThrowException_whenFindManyFails() {

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                jdbc.findMany("INVALID SQL", rs -> "x")
        );

        assertTrue(ex.getMessage().contains("Error executing query"));
    }

    @Test
    void shouldThrowException_whenFindOneReturnsMultipleRows() {

        jdbc.execute("INSERT INTO users VALUES (?, ?)", 1, "A");
        jdbc.execute("INSERT INTO users VALUES (?, ?)", 1, "B"); // duplicate

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                jdbc.findOne(
                        "SELECT name FROM users WHERE id = ?",
                        rs -> {
                            try { return rs.getString("name"); }
                            catch (Exception e) { throw new RuntimeException(e); }
                        },
                        1
                )
        );

        assertTrue(ex.getMessage().contains("Expected one result"));
    }
}