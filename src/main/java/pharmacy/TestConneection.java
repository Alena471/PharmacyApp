package pharmacy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConneection {
    public static void main(String[] args) {
        // Пробуем разные варианты подключения
        String[] urls = {
                "jdbc:sqlserver://localhost:1434;databaseName=master;encrypt=true;trustServerCertificate=true;",
                "jdbc:sqlserver://localhost:1433;databaseName=master;encrypt=true;trustServerCertificate=true;",
                "jdbc:sqlserver://localhost\\SQLEXPRESS01;databaseName=master;encrypt=true;trustServerCertificate=true;",
                "jdbc:sqlserver://DESKTOP-M3PCR8G\\SQLEXPRESS01;databaseName=master;encrypt=true;trustServerCertificate=true;"
        };

        String user = "sa";
        String password = "0000";

        System.out.println("=== ПРОВЕРКА ПОДКЛЮЧЕНИЯ К SQL SERVER ===");
        System.out.println();

        for (String url : urls) {
            System.out.println("Пробуем: " + url);
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                System.out.println("✅ ПОДКЛЮЧЕНО! Версия: " + conn.getMetaData().getDatabaseProductVersion());
                System.out.println("   Используй этот URL: " + url);
                System.out.println("\n✅ УСПЕХ! SQL Server работает!");
                return;
            } catch (SQLException e) {
                System.out.println("❌ Ошибка: " + e.getMessage());
                System.out.println();
            }
        }

        System.out.println("Ни один URL не подошёл.");
        System.out.println("\nВозможные причины:");
        System.out.println("1. SQL Server не запущен");
        System.out.println("2. Пользователь sa отключён или пароль неверный");
        System.out.println("3. Порт 1434 или 1433 занят другим процессом");
        System.out.println("4. Брандмауэр блокирует порт");
    }
}