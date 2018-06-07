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


    public Data getTransactions(String barScan, String server, String port, String rest_method){

        Data validTransactionDetails = null;

        System.out.println("This is the server ::: "+server);
        System.out.println("This is the rest method ::: "+rest_method);
        System.out.println("This is the port ::: " + port);
        try{
            System.out.println("Barcode Scan: "+barScan);
            System.out.println("REST API ::: "+server+":"+port+"/bcsgw/rest/v1/transaction/query");
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            //RequestBody body = RequestBody.create(mediaType, "{\"channel\":\"judechannel\",\"chaincode\":\"carTrace\",\"method\":\"getHistoryForRecord\",\"args\":[\""+barScan+"\"],\"chaincodeVer\":\"v1\"}");
            RequestBody body = RequestBody.create(mediaType, "{\"channel\":\"ownerchannel\",\"chaincode\":\"postal1\",\"method\":\"getHistoryForRecord\",\"args\":[\""+barScan+"\"],\"chaincodeVer\":\"v2\"}");


            Request request = new Request.Builder()
                    .url(server+":"+port+"/bcsgw/rest/v1/transaction/query")
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

    public String addNewProduct(Data data, String server, String port, String rest_method){

        String isAddProductSuccess = "";


        try{
            System.out.println("Barcode Scan: "+data.getLuggageScanId());
            System.out.println("REST API ::: "+server+":"+port+"/bcsgw/rest/v1/transaction/invocation");
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            //RequestBody body = RequestBody.create(mediaType, "{\"channel\":\"judechannel\",\"chaincode\":\"carTrace\",\"method\":\"getHistoryForRecord\",\"args\":[\""+barScan+"\"],\"chaincodeVer\":\"v1\"}");
            RequestBody body = RequestBody.create(mediaType, "{\"channel\":\"ownerchannel\",\"chaincode\":\"postal1\",\"method\":\"initLuggage\"," +
                    "\"args\":[\""+data.getLuggageScanId()+"\"," +
                    "\""+data.getLuggageColor()+"\","+
                    "\""+data.getOwner()+"\","+
                    "\""+data.getReciver()+"\","+
                    "\""+data.getCarrier()+"\","+
                    "\""+data.getStatus()+"\","+
                    "\""+data.getDate()+"\""+
                    "],\"chaincodeVer\":\"v2\"}");

            System.out.println("{\"channel\":\"scmchannel\",\"chaincode\":\"postal1\",\"method\":\"initProduct\"," +
                    "\"args\":[\""+data.getLuggageScanId()+"\"," +
                    "\""+data.getLuggageColor()+"\","+
                    "\""+data.getOwner()+"\","+
                    "\""+data.getReciver()+"\","+
                    "\""+data.getCarrier()+"\","+
                    "\""+data.getStatus()+"\""+
                    "\""+data.getDate()+"\""+
                    "],\"chaincodeVer\":\"v2\"}");


            Request request = new Request.Builder()
                    .url(server+":"+port+"/bcsgw/rest/v1/transaction/invocation")
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Cache-Control", "no-cache")
                    .build();

            Response response = client.newCall(request).execute();


            String response_string = response.body().string();
            System.out.println(response_string);
            isAddProductSuccess = isAddProduct(response_string);

        }catch (Exception e){
            e.printStackTrace();
        }

        return isAddProductSuccess;
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
                    item_data.setOwner(transactionDetails1.get("owner").toString());
                    item_data.setReciver(transactionDetails1.get("receiver").toString());
                    item_data.setStatus(transactionDetails1.get("status").toString());
                    item_data.setCarrier(transactionDetails1.get("carrier").toString());
                    item_data.setValid(true);
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return item_data;
    }


    private String isAddProduct(String response_string){
        String isSuccess = "";
        try {
            item_data = new Data();
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(response_string);
            JSONObject jo = (JSONObject) obj;
            String returnCode =  jo.get("returnCode").toString();
            if(returnCode.equalsIgnoreCase("Success")){
                isSuccess = "true";
            }else if(returnCode.equalsIgnoreCase("Failure")){
                String info =  jo.get("info").toString();
                if(info.contains("This product already exists")){
                    isSuccess = "duplicate";
                }
            }else{
                isSuccess = "false";
            }

        }catch(Exception e){
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static void main(String [] args){
        DataMan dm = new DataMan();
        //dm.getTransactions("1231231231");
        System.out.print(dm.isAddProduct("{\n" +
                "\t\"returnCode\": \"Failure\",\n" +
                "\t\"info\": \"Proposal not pass. ErrorMsg: Peer peer0.manufacturer.com, status: \\\"FAILURE\\\", Messages: \\\"Sending proposal to peer0.manufacturer.com failed because of: gRPC failure=Status{code=UNKNOWN, description=chaincode error (status: 500, message: This product already exists: K17-00101770), cause=null}\\\", Was verified: \\\"false\\\". .\",\n" +
                "\t\"transactionID\": \"6b341f59e7190bfc9d66732c080f497319a8129fbce97a9140f1fdfa26b69888\"\n" +
                "}"));
        //876543
    }


}
