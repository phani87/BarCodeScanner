package scanner.barcode.android.com.barcodescanner;

import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);

            System.out.println(item.getItemId());


        return super.onOptionsItemSelected(item);
    }

    public void scan(View view){
        zXingScannerView  = new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    public void getTable(View view){
        Intent intent = new Intent(MainActivity.this, GetTableActivity.class );
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (zXingScannerView != null) {
            zXingScannerView.stopCamera();
        }
    }

    @Override
    public void handleResult(final Result result)  {
       // Toast.makeText(getApplicationContext(),result.getText(),Toast.LENGTH_SHORT).show();


        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Scan Result");
        alertBuilder.setMessage(result.getText());
        alertBuilder.setPositiveButton("Validate", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String server = preferences.getString("server_name", "http://0.0.0.0");
                String rest_method = preferences.getString("rest_method", "get");
                System.out.println(server);
                String [] result_array = new String[]{server, rest_method, result.toString()};
                new ValidateBarcodeScan().execute(result_array);
            }
        });
        AlertDialog alert = alertBuilder.create();
        alert.show();

        zXingScannerView.resumeCameraPreview(this);
    }

    private void validationDialog(Data validTransactionDetails){


        AlertDialog.Builder alertValidateBuilder = new AlertDialog.Builder(this);
        alertValidateBuilder.setTitle("Validation Result");
        if(validTransactionDetails.isValid()){
            alertValidateBuilder.setMessage("Transaction Validity : "+validTransactionDetails.isValid()
                    +"\nTransaction ID : "+validTransactionDetails.getTransaction_id()
                    +"\nProduct Owner : "+validTransactionDetails.getOwner()
                    +"\nProduct Model : "+validTransactionDetails.getModel()
                    +"\nProduct Assembly: "+validTransactionDetails.getAssemblyDate());
        }else{
            alertValidateBuilder.setMessage("Transaction Validity : "+validTransactionDetails.isValid()
                );

        }

        alertValidateBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog validationAlert = alertValidateBuilder.create();
        validationAlert.show();
    }

  /*  @Override
    public void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(getApplicationContext());
        zXingScannerView.startCamera();
    }*/

    class ValidateBarcodeScan extends AsyncTask<String, String, Data> {
        @Override
        protected Data doInBackground(String... params) {
            Data isValid = null ;
            try {
                System.out.println("Background Params: "+params.toString());
                isValid = new DataMan().getTransactions(params[2].toString(), params[1].toString(), params[0].toString());
                Thread.sleep(2000);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return isValid;
        }

        @Override
        protected void onPostExecute(Data result) {
            System.out.println(result);

                validationDialog(result);


        }
    }

}
