package pharmacy;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static String currentDbType;
    private static String postgresqlUrl;
    private static String postgresqlUser;
    private static String postgresqlPassword;
    private static String sqlserverUrl;
    private static String sqlserverUser;
    private static String sqlserverPassword;

    static {
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            Properties props = new Properties();
            props.load(input);

            postgresqlUrl = props.getProperty("postgresql.url");
            postgresqlUser = props.getProperty("postgresql.user");
            postgresqlPassword = props.getProperty("postgresql.password");

            sqlserverUrl = props.getProperty("sqlserver.url");
            sqlserverUser = props.getProperty("sqlserver.user");
            sqlserverPassword = props.getProperty("sqlserver.password");

            currentDbType = props.getProperty("db.type", "postgresql");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось загрузить конфигурацию БД");
        }
    }

    public static void setDatabaseType(String dbType) {
        currentDbType = dbType;
    }

    public static String getDatabaseType() {
        return currentDbType;
    }

    public static Connection getConnection() throws SQLException {
        if (currentDbType.equals("postgresql")) {
            try {
                Class.forName("org.postgresql.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL driver not found", e);
            }
            return DriverManager.getConnection(postgresqlUrl, postgresqlUser, postgresqlPassword);

        } else if (currentDbType.equals("sqlserver")) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQL Server driver not found", e);
            }
            return DriverManager.getConnection(sqlserverUrl, sqlserverUser, sqlserverPassword);

        } else {
            throw new SQLException("Unknown database type: " + currentDbType);
        }
    }
}