package scanner.barcode.android.com.barcodescanner;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fr.ganfra.materialspinner.MaterialSpinner;

public class TransferValidLuggage extends AppCompatActivity {

    private TextView setTransctionId, setCarrier, setOwner, setReceiver, setStatus, setScanId;
    private Spinner updateStatus;
    private String transId, scanId, carrier, owner, receiver, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_valid_luggage);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        transId = getIntent().getStringExtra("transaction_id");
        scanId = getIntent().getStringExtra("scan_id");
        carrier = getIntent().getStringExtra("carrier");
        owner = getIntent().getStringExtra("owner");
        receiver = getIntent().getStringExtra("receiver");
        status = getIntent().getStringExtra("status");

        System.out.println("Scaneeedddd ??? " +scanId);

        setScanId = findViewById(R.id.setScanId);
        setScanId.setText(scanId);

        setCarrier = findViewById(R.id.carrier);
        setCarrier.setText(carrier);

        setOwner = findViewById(R.id.owner);
        setOwner.setText(owner);

        setReceiver = findViewById(R.id.receiver);
        setReceiver.setText(receiver);

        setStatus = findViewById(R.id.current_status);
        setStatus.setText(status);


        updateStatus = (MaterialSpinner) findViewById(R.id.update);
        // Create an ArrayAdapter using the string array and a default spinner layout
        String [] transit_status = {"Start","In Transit","End"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, transit_status);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        updateStatus.setAdapter(adapter);

    }

    public void update(View view){

        System.out.println("Scanning >>>>" +scanId.toString());
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String server = preferences.getString("server_name", "http://0.0.0.0");
        String rest_method = preferences.getString("rest_method", "get");
        Integer port_number = preferences.getInt("server_port", 1000);
        String update_status = updateStatus.getSelectedItem().toString();
        String carrier = setCarrier.getText().toString();

        System.out.println("UPDATEEEEE STATUSTTTT >>>>>>"+update_status.toString());

        String [] result_array = new String[]{scanId.toString(), update_status, carrier, "2018627",server, port_number.toString(), rest_method};
        new UpdateBCS().execute(result_array);
        // dm.getTransactions();

    }


    class UpdateBCS extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String isValid = null ;
            try {
                System.out.println("Background Params: "+params[0].toString());
                isValid = new DataMan().updateProductStatus(params[0].toString(), params[1].toString(), params[2].toString(),params[3].toString(), params[4].toString(), params[5].toString(), params[6].toString());
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return isValid;
        }


        @Override
        protected void onPostExecute(String result) {
            if(result.equalsIgnoreCase("true")){
                Toast.makeText(getApplicationContext(), "Handshake Success",
                        Toast.LENGTH_SHORT)
                        .show();
            }else{
                Toast.makeText(getApplicationContext(), "Handshake Failed",
                        Toast.LENGTH_SHORT)
                        .show();
            }

            //transferLuggageActivity(result);


        }
    }

}
