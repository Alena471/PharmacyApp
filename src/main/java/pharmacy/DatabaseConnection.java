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
            System.out.println("DB TYPE: " + currentDbType);
            System.out.println("POSTGRES URL: " + postgresqlUrl);
            System.out.println("POSTGRES USER: " + postgresqlUser);
            System.out.println("POSTGRES PASSWORD: " + postgresqlPassword);
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
            System.out.println("TRYING POSTGRES CONNECTION");
            System.out.println(postgresqlUrl);
            System.out.println(postgresqlUser);
            System.out.println(postgresqlPassword);
            try {
                Connection conn = DriverManager.getConnection(
                        postgresqlUrl,
                        postgresqlUser,
                        postgresqlPassword
                );

                System.out.println("POSTGRES CONNECTED SUCCESSFULLY");

                return conn;

            } catch (SQLException e) {
                System.out.println("POSTGRES CONNECTION ERROR:");
                e.printStackTrace();

                throw e;
            }

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