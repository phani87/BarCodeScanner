package scanner.barcode.android.com.barcodescanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.integration.android.IntentIntegrator;

public class AddProductActivity extends AppCompatActivity {

    private String /*codeFormat*/codeContent;
    private TextView /*formatTxt,*/ contentTxt;

    private String productScanId;
    private String productName;
    private String productManufacturer;
    private String productOwner;
    private String productType;
    private boolean isValid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
       /* formatTxt = (TextView)findViewById(R.id.scan_format);*/
        contentTxt = (TextView)findViewById(R.id.lScanId);
    }

    /**
     * event handler for scan button
     * @param view view of the activity
     */
    public void scanNow(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(this.getString(R.string.scan_bar_code));
       // Wide scanning rectangle, may work better for 1D barcodes
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    /**
     * function handle scan result
     * @param requestCode scanned code
     * @param resultCode  result of scanned code
     * @param intent intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a result
            codeContent = scanningResult.getContents();
            /*codeFormat = scanningResult.getFormatName();*/

            // display it on screen
            /*formatTxt.setText("FORMAT: " + codeFormat);*/
            contentTxt.setText("" + codeContent);

        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void addProduct(View view){

        TextView luggage_color= findViewById(R.id.lColor);
        TextView luggageOwner= findViewById(R.id.lOwner);
        TextView luggage_receiver= findViewById(R.id.lReceiver);
        TextView luggage_carrier= findViewById(R.id.lCarrier);
        TextView luggage_status= findViewById(R.id.lStatus);
        TextView luggage_date = findViewById(R.id.lDate);

        String [] addP = new String[]{contentTxt.getText().toString(),
                luggage_color.getText().toString(),luggageOwner.getText().toString(),
                luggage_receiver.getText().toString(),
                luggage_carrier.getText().toString(),
                luggage_status.getText().toString(),
                luggage_date.getText().toString()};

        //System.out.println("Scan-"+contentTxt.getText().toString()+"-PName-"+pNname.getText().toString());
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//         String server = preferences.getString("server_name", "http://0.0.0.0");
//         String rest_method = preferences.getString("rest_method", "get");
//         Integer port_number = preferences.getInt("server_port", 1000);





        boolean isAddProductSuccess = false;
        //DataMan dataMan = new DataMan();
        new AddProductAsync().execute(addP);
       // isAddProductSuccess = dataMan.addNewProduct(data, server, port_number.toString(), rest_method);
       // validationDialog(isAddProductSuccess);


    }

    private void validationDialog(String isAddProductSuccess){
        System.out.println(isAddProductSuccess);
        AlertDialog.Builder alertValidateBuilder = new AlertDialog.Builder(this);
        alertValidateBuilder.setTitle("Product Add Status");
        if(isAddProductSuccess.equalsIgnoreCase("true")){
            alertValidateBuilder.setMessage("Product Add Success!");
        }else if (isAddProductSuccess.equalsIgnoreCase("duplicate")){
            alertValidateBuilder.setMessage("Product Already Exists!");
        }else if (isAddProductSuccess.equalsIgnoreCase("false")){
            alertValidateBuilder.setMessage("Product Add Failed! Try Again!");
        }

        alertValidateBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog validationAlert = alertValidateBuilder.create();
        validationAlert.show();
    }

    class AddProductAsync extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String isAddProductSuccess = "";
            try{

                Data data = new Data();
                data.setLuggageScanId(params[0].toString());
                data.setLuggageColor(params[1].toString());
                data.setOwner(params[2].toString());
                data.setReciver(params[3].toString());
                data.setCarrier(params[4].toString());
                data.setStatus(params[5].toString());
                data.setDate(params[6].toString());


                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String server = preferences.getString("server_name", "http://0.0.0.0");
                String rest_method = preferences.getString("rest_method", "get");
                Integer port_number = preferences.getInt("server_port", 1000);
                System.out.println("server-"+server+"-rest_method-"+rest_method+"-port_number-"+port_number);
                isAddProductSuccess = new DataMan().addNewProduct(data, server, port_number.toString(), rest_method);;
                Thread.sleep(2000);
            }catch (Exception e){
                e.printStackTrace();
            }

            return isAddProductSuccess;
        }


        @Override
        protected void onPostExecute(String aBoolean) {
            System.out.println(aBoolean);
            validationDialog(aBoolean);
        }
    }

}
