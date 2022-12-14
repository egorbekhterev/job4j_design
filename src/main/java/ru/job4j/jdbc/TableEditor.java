package ru.job4j.jdbc;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.StringJoiner;

public class TableEditor implements AutoCloseable {

    private Connection connection;

    private final Properties properties;

    public TableEditor(Properties properties) throws SQLException, ClassNotFoundException {
        this.properties = properties;
        initConnection();
    }

    private void initConnection() throws ClassNotFoundException, SQLException {
        Class.forName(properties.getProperty("driver_class"));
        String url = properties.getProperty("url");
        String login = properties.getProperty("username");
        String password = properties.getProperty("password");
        connection = DriverManager.getConnection(url, login, password);
    }

    public void statement(String s) {
        try (Statement statement = connection.createStatement()) {
            statement.execute(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable(String tableName) {
        String sql = String.format(
                "create table if not exists %s(%s);",
                tableName,
                "id serial primary key"
        );
        statement(sql);
    }

    public void dropTable(String tableName) {
        String sql = String.format(
                "drop table %s;", tableName);
        statement(sql);
    }

    public void addColumn(String tableName, String columnName, String type) {
        String sql = String.format(
                "alter table %s add column %s %s;",
                tableName, columnName, type
        );
        statement(sql);
    }

    public void dropColumn(String tableName, String columnName) {
        String sql = String.format(
                "alter table %s drop column %s;",
                tableName, columnName
        );
        statement(sql);
    }

    public void renameColumn(String tableName, String columnName, String newColumnName) {
        String sql = String.format(
                "alter table %s rename column %s to %s;",
                tableName, columnName, newColumnName
        );
        statement(sql);
    }

    public String getTableScheme(String tableName) throws Exception {
        var rowSeparator = "-".repeat(30).concat(System.lineSeparator());
        var header = String.format("%-15s|%-15s%n", "NAME", "TYPE");
        var buffer = new StringJoiner(rowSeparator, rowSeparator, rowSeparator);
        buffer.add(header);
        try (var statement = connection.createStatement()) {
            var selection = statement.executeQuery(String.format(
                    "select * from %s limit 1", tableName
            ));
            var metaData = selection.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                buffer.add(String.format("%-15s|%-15s%n",
                        metaData.getColumnName(i), metaData.getColumnTypeName(i))
                );
            }
        }
        return buffer.toString();
    }

    @Override
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        ClassLoader loader = TableEditor.class.getClassLoader();
        try (InputStream io = loader.getResourceAsStream("table_editor.properties")) {
            properties.load(io);
            try (TableEditor tableEditor = new TableEditor(properties)) {
                String tn = "demo_table";
                tableEditor.createTable(tn);
                System.out.println(tableEditor.getTableScheme(tn));
                tableEditor.addColumn(tn, "smth", "varchar(50)");
                System.out.println(tableEditor.getTableScheme(tn));
                tableEditor.renameColumn(tn, "smth", "description");
                System.out.println(tableEditor.getTableScheme(tn));
                tableEditor.dropColumn(tn, "description");
                System.out.println(tableEditor.getTableScheme(tn));
                tableEditor.dropTable(tn);
            }
        }
    }
}
