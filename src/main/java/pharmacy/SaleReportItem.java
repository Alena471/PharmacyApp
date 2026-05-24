package pharmacy;

import java.time.LocalDate;

public class SaleReportItem {
    private int saleId;
    private LocalDate saleDate;
    private String employeeName;      // кто продал
    private String customerName;      // кому продали
    private String medicineName;
    private int quantity;
    private double price;
    private double total;

    // Конструктор с сотрудником и клиентом
    public SaleReportItem(int saleId, LocalDate saleDate,
                          String employeeName, String customerName,
                          String medicineName, int quantity,
                          double price, double total) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.employeeName = employeeName;
        this.customerName = customerName;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    // Геттеры
    public int getSaleId() { return saleId; }
    public LocalDate getSaleDate() { return saleDate; }
    public String getEmployeeName() { return employeeName; }
    public String getCustomerName() { return customerName; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return total; }
}