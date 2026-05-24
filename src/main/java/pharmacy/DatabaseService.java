package pharmacy;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    // ==================== ЛЕКАРСТВА ====================

    public static List<Medicine> getAllMedicines() throws SQLException {
        List<Medicine> list = new ArrayList<>();
        String sql = "SELECT m.medicine_id, m.name, m.dosage, m.release_form, " +
                "m.manufacturer_id, m.prescription_required, man.name as manufacturer_name " +
                "FROM medicines m JOIN manufacturers man ON m.manufacturer_id = man.manufacturer_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Medicine(
                        rs.getInt("medicine_id"),
                        rs.getString("name"),
                        rs.getString("dosage"),
                        rs.getString("release_form"),
                        rs.getInt("manufacturer_id"),
                        rs.getString("manufacturer_name"),
                        rs.getBoolean("prescription_required")
                ));
            }
        }
        return list;
    }

    public static void addMedicine(Medicine medicine) throws SQLException {
        String sql = "INSERT INTO medicines (name, dosage, release_form, manufacturer_id, prescription_required) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setString(2, medicine.getDosage());
            pstmt.setString(3, medicine.getReleaseForm());
            pstmt.setInt(4, medicine.getManufacturerId());
            pstmt.setBoolean(5, medicine.isPrescriptionRequired());
            pstmt.executeUpdate();
        }
    }

    public static void updateMedicine(Medicine medicine) throws SQLException {
        String sql = "UPDATE medicines SET name=?, dosage=?, release_form=?, manufacturer_id=?, prescription_required=? WHERE medicine_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, medicine.getName());
            pstmt.setString(2, medicine.getDosage());
            pstmt.setString(3, medicine.getReleaseForm());
            pstmt.setInt(4, medicine.getManufacturerId());
            pstmt.setBoolean(5, medicine.isPrescriptionRequired());
            pstmt.setInt(6, medicine.getMedicineId());
            pstmt.executeUpdate();
        }
    }

    public static boolean canDeleteMedicine(int medicineId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM batches WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, medicineId);
            ResultSet rs = pstmt.executeQuery();
            rs.next();
            return rs.getInt(1) == 0;
        }
    }

    public static void deleteMedicine(int medicineId) throws SQLException {
        String sql = "DELETE FROM medicines WHERE medicine_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, medicineId);
            pstmt.executeUpdate();
        }
    }

    public static int getMedicineId(String name) throws SQLException {
        String sql = "SELECT medicine_id FROM medicines WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("medicine_id");
            return -1;
        }
    }

    // ==================== ПРОИЗВОДИТЕЛИ ====================

    public static List<String> getAllManufacturers() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM manufacturers ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("name"));
        }
        return list;
    }

    public static int getManufacturerId(String name) throws SQLException {
        String sql = "SELECT manufacturer_id FROM manufacturers WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("manufacturer_id");
            return -1;
        }
    }

    // ==================== ПАРТИИ ====================

    public static List<Batch> getAllBatches() throws SQLException {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT b.batch_id, b.batch_number, m.name as medicine_name, " +
                "b.expiration_date, b.quantity, b.sale_price " +
                "FROM batches b JOIN medicines m ON b.medicine_id = m.medicine_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Batch(
                        rs.getInt("batch_id"),
                        rs.getString("batch_number"),
                        rs.getString("medicine_name"),
                        rs.getDate("expiration_date").toLocalDate(),
                        rs.getInt("quantity"),
                        rs.getDouble("sale_price")
                ));
            }
        }
        return list;
    }

    public static List<Batch> getAvailableBatches() throws SQLException {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT b.batch_id, b.batch_number, m.name as medicine_name, " +
                "b.quantity, b.sale_price FROM batches b " +
                "JOIN medicines m ON b.medicine_id = m.medicine_id " +
                "WHERE b.quantity > 0 AND b.expiration_date > CURRENT_DATE";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Batch batch = new Batch(
                        rs.getInt("batch_id"),
                        rs.getString("batch_number"),
                        rs.getInt("quantity"),
                        rs.getDouble("sale_price")
                );
                batch.setMedicineName(rs.getString("medicine_name"));
                list.add(batch);
            }
        }
        return list;
    }

    public static void addBatch(int medicineId, int supplierId, String batchNumber, LocalDate deliveryDate,
                                LocalDate expirationDate, int quantity, double purchasePrice, double salePrice) throws SQLException {
        String sql = "INSERT INTO batches (batch_number, medicine_id, supplier_id, delivery_date, expiration_date, quantity, purchase_price, sale_price) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, batchNumber);
            pstmt.setInt(2, medicineId);
            pstmt.setInt(3, supplierId);
            pstmt.setDate(4, Date.valueOf(deliveryDate));
            pstmt.setDate(5, Date.valueOf(expirationDate));
            pstmt.setInt(6, quantity);
            pstmt.setDouble(7, purchasePrice);
            pstmt.setDouble(8, salePrice);
            pstmt.executeUpdate();
        }
    }

    public static List<String> getAllSuppliers() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT name FROM suppliers ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("name"));
        }
        return list;
    }

    public static int getSupplierId(String name) throws SQLException {
        String sql = "SELECT supplier_id FROM suppliers WHERE name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("supplier_id");
            return -1;
        }
    }

    // ==================== ОСТАТКИ ====================

    public static List<Batch> getStock() throws SQLException {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT b.batch_id, m.name as medicine_name, b.quantity, b.sale_price, b.expiration_date " +
                "FROM batches b JOIN medicines m ON b.medicine_id = m.medicine_id " +
                "WHERE b.quantity > 0 ORDER BY m.name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Batch(
                        rs.getInt("batch_id"),
                        null,
                        rs.getString("medicine_name"),
                        rs.getDate("expiration_date").toLocalDate(),
                        rs.getInt("quantity"),
                        rs.getDouble("sale_price")
                ));
            }
        }
        return list;
    }

    // ==================== ПРОДАЖИ ====================

    public static int createSale(int employeeId, int customerId) throws SQLException {
        String sql = "INSERT INTO sales (sale_date, employee_id, customer_id) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, employeeId);
            pstmt.setInt(3, customerId);
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            return -1;
        }
    }

    public static void addSaleItem(int saleId, int batchId, int quantity, double price) throws SQLException {
        String sql = "INSERT INTO sale_items (sale_id, batch_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, saleId);
            pstmt.setInt(2, batchId);
            pstmt.setInt(3, quantity);
            pstmt.setDouble(4, price);
            pstmt.executeUpdate();
        }
    }

    public static void updateBatchQuantity(int batchId, int newQuantity) throws SQLException {
        String sql = "UPDATE batches SET quantity = ? WHERE batch_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, batchId);
            pstmt.executeUpdate();
        }
    }

    public static List<String> getAllEmployees() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT first_name || ' ' || last_name as full_name FROM employees ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("full_name"));
        }
        return list;
    }

    public static int getEmployeeId(String fullName) throws SQLException {
        String[] names = fullName.split(" ");
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";
        String sql = "SELECT employee_id FROM employees WHERE first_name = ? AND last_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("employee_id");
            return -1;
        }
    }

    public static List<String> getAllCustomers() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT first_name || ' ' || last_name as full_name FROM customers ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(rs.getString("full_name"));
        }
        return list;
    }

    public static int getCustomerId(String fullName) throws SQLException {
        String[] names = fullName.split(" ");
        String firstName = names[0];
        String lastName = names.length > 1 ? names[1] : "";
        String sql = "SELECT customer_id FROM customers WHERE first_name = ? AND last_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("customer_id");
            return -1;
        }
    }

    // ==================== ОТЧЁТЫ ====================

    public static List<SaleReportItem> getSalesReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        List<SaleReportItem> list = new ArrayList<>();
        String sql = "SELECT s.sale_id, s.sale_date::date, " +
                "e.first_name || ' ' || e.last_name as employee_name, " +
                "COALESCE(c.first_name || ' ' || c.last_name, 'Аноним') as customer_name, " +
                "m.name as medicine_name, " +
                "si.quantity, si.price, (si.quantity * si.price) as total " +
                "FROM sales s " +
                "JOIN employees e ON s.employee_id = e.employee_id " +
                "LEFT JOIN customers c ON s.customer_id = c.customer_id " +
                "JOIN sale_items si ON s.sale_id = si.sale_id " +
                "JOIN batches b ON si.batch_id = b.batch_id " +
                "JOIN medicines m ON b.medicine_id = m.medicine_id " +
                "WHERE s.sale_date::date BETWEEN ? AND ? " +
                "ORDER BY s.sale_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(startDate));
            pstmt.setDate(2, Date.valueOf(endDate));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new SaleReportItem(
                        rs.getInt("sale_id"),
                        rs.getDate("sale_date").toLocalDate(),
                        rs.getString("employee_name"),
                        rs.getString("customer_name"),
                        rs.getString("medicine_name"),
                        rs.getInt("quantity"),
                        rs.getDouble("price"),
                        rs.getDouble("total")
                ));
            }
        }
        return list;
    }
}