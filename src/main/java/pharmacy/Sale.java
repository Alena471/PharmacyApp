package pharmacy;

import java.time.LocalDateTime;

public class Sale {
    private int saleId;
    private LocalDateTime saleDate;
    private int employeeId;
    private String employeeName;
    private int customerId;
    private String customerName;
    private double totalAmount;

    public Sale(int saleId, LocalDateTime saleDate, int employeeId, String employeeName,
                int customerId, String customerName, double totalAmount) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.customerId = customerId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }

    public int getSaleId() { return saleId; }
    public LocalDateTime getSaleDate() { return saleDate; }
    public int getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public int getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public double getTotalAmount() { return totalAmount; }
}