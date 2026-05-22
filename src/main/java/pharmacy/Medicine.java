package pharmacy;

public class Medicine {
    private int medicineId;
    private String name;
    private String dosage;
    private String releaseForm;
    private int manufacturerId;
    private String manufacturerName;
    private boolean prescriptionRequired;

    public Medicine(int medicineId, String name, String dosage, String releaseForm,
                    int manufacturerId, String manufacturerName, boolean prescriptionRequired) {
        this.medicineId = medicineId;
        this.name = name;
        this.dosage = dosage;
        this.releaseForm = releaseForm;
        this.manufacturerId = manufacturerId;
        this.manufacturerName = manufacturerName;
        this.prescriptionRequired = prescriptionRequired;
    }

    public int getMedicineId() { return medicineId; }
    public String getName() { return name; }
    public String getDosage() { return dosage; }
    public String getReleaseForm() { return releaseForm; }
    public int getManufacturerId() { return manufacturerId; }
    public String getManufacturerName() { return manufacturerName; }
    public boolean isPrescriptionRequired() { return prescriptionRequired; }
}