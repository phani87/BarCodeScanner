package scanner.barcode.android.com.barcodescanner;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Calendar;

public class AddProductActivity extends AppCompatActivity {

    private String /*codeFormat*/codeContent;
    private TextView /*formatTxt,*/ contentTxt;
    private TextView dateView;
    private  Spinner spinner;

    private String productScanId;
    private String productName;
    private String productManufacturer;
    private String productOwner;
    private String productType;
    private boolean isValid;

    private Calendar calendar;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
       /* formatTxt = (TextView)findViewById(R.id.scan_format);*/
        contentTxt = (TextView)findViewById(R.id.lScanId);
        dateView = (TextView) findViewById(R.id.lDate);

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        showDate(year, month+1, day);


        spinner = (Spinner) findViewById(R.id.lStatus);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.transit_status, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

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
     * event handler for datepicker button
     * @param view view of the activity
     */
    public void datepicker(View view){
        showDialog(999);
//        AppCompatDialogFragment newFragment = new DatePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "datePicker");


    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
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
        String luggage_status=  spinner.getSelectedItem().toString();
        TextView luggage_date = findViewById(R.id.lDate);

        String [] addP = new String[]{contentTxt.getText().toString(),
                luggage_color.getText().toString(),luggageOwner.getText().toString(),
                luggage_receiver.getText().toString(),
                luggage_carrier.getText().toString(),
                luggage_status.toString(),
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

    /**
     * Adding datepicker activity listener
     */

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    showDate(i, i1+1, i2);
                }


            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(year).append(".")
                .append(month).append(".").append(day));
    }
}
