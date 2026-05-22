package pharmacy;

public class CartItem {
    private int batchId;
    private String medicineName;
    private int quantity;
    private double price;

    public CartItem(int batchId, String medicineName, int quantity, double price) {
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