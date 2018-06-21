package scanner.barcode.android.com.barcodescanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HandshakeActivity extends AppCompatActivity {

    private TextView scanId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handshake);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        scanId = findViewById(R.id.scanId);
    }

    public void scanLuggageCode(View view){
        IntentIntegrator integrator = new IntentIntegrator(this);
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(this.getString(R.string.scan_bar_code));
        // Wide scanning rectangle, may work better for 1D barcodes
        integrator.setOrientationLocked(false);
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.initiateScan();
    }

    public void transferLuggageActivity(Data validTransactionDetails){
        if(validTransactionDetails.isValid()){
            Intent i = new Intent(this, TransferValidLuggage.class);
            i.putExtra("transaction_id", validTransactionDetails.getTransaction_id());
            i.putExtra("carrier", validTransactionDetails.getCarrier());
            i.putExtra("owner", validTransactionDetails.getOwner());
            i.putExtra("receiver", validTransactionDetails.getReciver());
            i.putExtra("status", validTransactionDetails.getStatus());
            startActivity(i);
        }else{
            Toast.makeText(getApplicationContext(), "Not Valid Code",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void forward(View view){

        System.out.println("Scanning >>>>" +scanId.getText().toString());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String server = preferences.getString("server_name", "http://0.0.0.0");
        String rest_method = preferences.getString("rest_method", "get");
        Integer port_number = preferences.getInt("server_port", 1000);

        String [] result_array = new String[]{scanId.getText().toString(), server, port_number.toString(), rest_method};
        new ValidateBarcodeScan().execute(result_array);
       // dm.getTransactions();

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
            //scanId = scanningResult.getContents();
            /*codeFormat = scanningResult.getFormatName();*/

            // display it on screen
            /*formatTxt.setText("FORMAT: " + codeFormat);*/
            scanId.setText("" + scanningResult.getContents().toString());

        }else{
            Toast toast = Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    class ValidateBarcodeScan extends AsyncTask<String, String, Data> {
        @Override
        protected Data doInBackground(String... params) {
            Data isValid = null ;
            try {
                System.out.println("Background Params: "+params[0].toString());
                isValid = new DataMan().getTransactions(params[0].toString(), params[1].toString(), params[2].toString(), params[3].toString());
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return isValid;
        }

        @Override
        protected void onPostExecute(Data result) {
            System.out.println(result.isValid());
            transferLuggageActivity(result);


        }
    }

}
