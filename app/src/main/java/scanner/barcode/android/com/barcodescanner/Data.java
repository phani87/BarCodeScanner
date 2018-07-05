package scanner.barcode.android.com.barcodescanner;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Data is a POJO class that allows defines the object to be put into BCS
 *
 * You can redefine based on your requirements
 */

public class Data implements java.io.Serializable{

    private String transaction_id;
    private String docType;
    private String luggageScanId;
    private String luggageColor;
    private String owner;
    private String reciver;
    private String carrier;
    private String status;
    private String date;

    //getters and setters

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

    public String getLuggageScanId() {
        return luggageScanId;
    }

    public void setLuggageScanId(String luggageScanId) {
        this.luggageScanId = luggageScanId;
    }

    public String getLuggageColor() {
        return luggageColor;
    }

    public void setLuggageColor(String luggageColor) {
        this.luggageColor = luggageColor;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getReciver() {
        return reciver;
    }

    public void setReciver(String reciver) {
        this.reciver = reciver;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    private boolean isValid;




}
