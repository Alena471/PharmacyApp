package pharmacy;

import java.time.LocalDate;

public class Batch {
    private int batchId;
    private String batchNumber;
    private int medicineId;
    private String medicineName;
    private int supplierId;
    private String supplierName;
    private LocalDate deliveryDate;
    private LocalDate expirationDate;
    private int quantity;
    private double purchasePrice;
    private double salePrice;

    public Batch(int batchId, String batchNumber, String medicineName,
                 LocalDate expirationDate, int quantity, double salePrice) {
        this.batchId = batchId;
        this.batchNumber = batchNumber;
        this.medicineName = medicineName;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
        this.salePrice = salePrice;
    }

    public Batch(int batchId, String batchNumber, int medicineId, String medicineName,
                 int supplierId, String supplierName, LocalDate deliveryDate,
                 LocalDate expirationDate, int quantity, double purchasePrice, double salePrice) {
        this.batchId = batchId;
        this.batchNumber = batchNumber;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.deliveryDate = deliveryDate;
        this.expirationDate = expirationDate;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
    }

    public Batch(int batchId, String batchNumber, int quantity, double salePrice) {
        this.batchId = batchId;
        this.batchNumber = batchNumber;
        this.quantity = quantity;
        this.salePrice = salePrice;
        this.medicineName = "";
    }

    // Getters and Setters
    public int getBatchId() { return batchId; }
    public String getBatchNumber() { return batchNumber; }
    public int getMedicineId() { return medicineId; }
    public String getMedicineName() { return medicineName; }
    public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
    public int getSupplierId() { return supplierId; }
    public String getSupplierName() { return supplierName; }
    public LocalDate getDeliveryDate() { return deliveryDate; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public int getQuantity() { return quantity; }
    public double getPurchasePrice() { return purchasePrice; }
    public double getSalePrice() { return salePrice; }
}