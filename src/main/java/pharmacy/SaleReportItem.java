package pharmacy;

import java.time.LocalDate;

public class SaleReportItem {
    private int saleId;
    private LocalDate saleDate;
    private String medicineName;
    private int quantity;
    private double price;
    private double total;

    public SaleReportItem(int saleId, LocalDate saleDate, String medicineName,
                          int quantity, double price, double total) {
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public int getSaleId() { return saleId; }
    public LocalDate getSaleDate() { return saleDate; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return total; }
}