package org.example.jdbc;

import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class JdbcUtil {

    private final String url;
    private final String user;
    private final String password;

    public JdbcUtil(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

    // (a) Simple execute
    public void execute(String query, Object... args) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            setParams(ps, args);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + query, e);
        }
    }

    // (b) Execute with Consumer
    public void execute(String query, Consumer<PreparedStatement> consumer) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            consumer.accept(ps);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + query, e);
        }
    }

    // Find one
    public <T> Optional<T> findOne(String query, Function<ResultSet, T> mapper, Object... args) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            setParams(ps, args);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return Optional.empty();

            T result = mapper.apply(rs);

            if (rs.next()) {
                throw new RuntimeException("Expected one result but found multiple");
            }

            return Optional.of(result);

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + query, e);
        }
    }

    // Find many
    public <T> List<T> findMany(String query, Function<ResultSet, T> mapper, Object... args) {
        List<T> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            setParams(ps, args);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(mapper.apply(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error executing query: " + query, e);
        }

        return list;
    }

    // Helper
    private void setParams(PreparedStatement ps, Object... args) throws SQLException {
        for (int i = 0; i < args.length; i++) {
            ps.setObject(i + 1, args[i]);
        }
    }
}