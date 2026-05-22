package pharmacy;

public class SaleItem {
    private int saleItemId;
    private int saleId;
    private int batchId;
    private String medicineName;
    private int quantity;
    private double price;
    private double subtotal;

    public SaleItem(int saleItemId, int saleId, int batchId, String medicineName,
                    int quantity, double price, double subtotal) {
        this.saleItemId = saleItemId;
        this.saleId = saleId;
        this.batchId = batchId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    public SaleItem(int batchId, String medicineName, int quantity, double price) {
        this.batchId = batchId;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.price = price;
    }

    public int getBatchId() { return batchId; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public double getTotal() { return quantity * price; }
}