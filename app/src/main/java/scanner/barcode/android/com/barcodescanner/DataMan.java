package scanner.barcode.android.com.barcodescanner;


import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class DataMan  {
    Data item_data = null;
   /* public Data[] getInvoices() {*//*

        Data[] data = new Data[20];



        for(int i = 0; i < 20; i ++) {
            Data row = new Data();
            row.id = (i+1);
            row.invoiceNumber = row.id;
            row.amountDue = BigDecimal.valueOf(20.00 * i);
            row.invoiceAmount = BigDecimal.valueOf(120.00 * (i+1));
            row.invoiceDate = new Date();
            row.customerName =  "Thomas John Beckett";
            row.customerAddress = "1112, Hash Avenue, NYC";
            data[i] = row;
        }
        return data;*//*
    }*/


    public Data getTransactions(String barScan, String server, String rest_method){

        Data validTransactionDetails = null;

        System.out.println("This is the server ::: "+server);
        System.out.println("This is the rest ::: "+rest_method);
        try{
            System.out.println("Barcode Scan: "+barScan);
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, "{\"channel\":\"judechannel\",\"chaincode\":\"carTrace\",\"method\":\"getHistoryForRecord\",\"args\":[\""+barScan+"\"],\"chaincodeVer\":\"v1\"}");
            Request request = new Request.Builder()
                    .url("http://129.146.152.37:3100/bcsgw/rest/v1/transaction/query")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cache-Control", "no-cache")
                    .build();

            Response response = client.newCall(request).execute();

            //System.out.println(response.body().string());
            String response_string = response.body().string();

            validTransactionDetails = isTrasactionValid(response_string);



        }catch (Exception e){
            e.printStackTrace();
        }

        return validTransactionDetails;
    }


    private Data isTrasactionValid(String response_string){
        boolean isValid = false;
        try {
            item_data = new Data();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response_string);
            JSONObject jo = (JSONObject) obj;
            String resultSet =  jo.get("result").toString();
            Object obj1 = parser.parse(resultSet);
            JSONArray transactionResult = (JSONArray) obj1;
            //JSONArray tranactionValue = (JSONArray) ((JSONObject) obj1).get("Value");

            Iterator transactionIterator = transactionResult.iterator();

            if(transactionResult.size()>0){
                while(transactionIterator.hasNext()){
                    JSONObject transactionDetails = (JSONObject)transactionIterator.next();
                    item_data.setTransaction_id(transactionDetails.get("TxId").toString());
                    JSONObject transactionDetails1 = (JSONObject) transactionDetails.get("Value");
                    item_data.setChassisNumber(transactionDetails1.get("chassisNumber").toString());
                    item_data.setManufacturer(transactionDetails1.get("manufacturer").toString());
                    item_data.setModel(transactionDetails1.get("model").toString());
                    item_data.setAssemblyDate(transactionDetails1.get("assemblyDate").toString());
                    item_data.setOwner(transactionDetails1.get("owner").toString());
                    item_data.setValid(true);
                }
            }


            //item_data.setTransaction_id(transactionResult.get("TxId").toString());
           /* item_data.setDocType();
            item_data.setChassisNumber();
            item_data.setManufacturer();
            item_data.setModel();
            item_data.setAssemblyDate();
            item_data.setOwner();*/
           /* if(transactionResult.size()>0){
                System.out.print("Im here");
                isValid = true;
            }*/
        }catch(Exception e){
            e.printStackTrace();
        }
        return item_data;
    }

    public static void main(String [] args){
        DataMan dm = new DataMan();
        //dm.getTransactions("1231231231");
        dm.isTrasactionValid("{\n" +
                "\t\"returnCode\": \"Success\",\n" +
                "\t\"result\": \"[{\\\"TxId\\\":\\\"b4d1d1bdf790da7f2352c1c8c30af72eafc9aa04f559eb9b85c53db652ccbcac\\\", \\\"Value\\\":{\\\"docType\\\":\\\"vehicle\\\",\\\"chassisNumber\\\":\\\"876543\\\",\\\"manufacturer\\\":\\\"audi\\\",\\\"model\\\":\\\"3\\\",\\\"assemblyDate\\\":22022018,\\\"airbagSerialNumber\\\":\\\"airbag3457\\\",\\\"owner\\\":\\\"shikhar\\\",\\\"recall\\\":true,\\\"recallDate\\\":22022018}, \\\"Timestamp\\\":\\\"2018-05-17 19:08:36.01 +0000 UTC\\\", \\\"IsDelete\\\":\\\"false\\\"}]\",\n" +
                "\t\"info\": null\n" +
                "}");
        //876543
    }


}
