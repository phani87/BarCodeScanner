package scanner.barcode.android.com.barcodescanner;

import java.math.BigDecimal;
import java.util.Date;

public class Data {

    private String transaction_id;
    private String docType;
    private String chassisNumber;
    private String manufacturer;
    private String model;
    private String assemblyDate;
    private String owner;
    private boolean isValid;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getAssemblyDate() {
        return assemblyDate;
    }

    public void setAssemblyDate(String assemblyDate) {
        this.assemblyDate = assemblyDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
