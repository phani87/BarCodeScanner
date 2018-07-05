package scanner.barcode.android.com.barcodescanner;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;


import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView zXingScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setIcon(R.drawable.oracle);
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


        return super.onOptionsItemSelected(item);
    }


    /**
     * Method to scan an Image
     * Deprecated
     * @param view
     */
    public void scan(View view){
        zXingScannerView  = new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    /**
     * Method to add new product
     * Calls another activity AddProductActivity.class
     * @param view
     */
    public void add(View view){
        Intent i = new Intent(this, AddProductActivity.class);
        startActivity(i);
    }

    /**
     * Method to transfer product
     * Calls another activity HandshakeActivity.class
     * @param view
     */
    public void handshake(View view){
        Intent i = new Intent(this, HandshakeActivity.class);
        startActivity(i);
    }

    /**
     *  Method to show timeline product
     * Calls another activity timeline_activity.class
     * @param view
     */
    public void timeline(View view){
        Intent i = new Intent(this, timeline_activity.class);
        startActivity(i);
    }

    public void timelineScan(View view){
        Intent i = new Intent(this, TimelineScan.class);
        startActivity(i);
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
                Integer port_number = preferences.getInt("server_port", 1000);

                String [] result_array = new String[]{result.toString(), server, port_number.toString(), rest_method};
                //new ValidateBarcodeScan().execute(result_array);
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
                    +"\nOwner : "+validTransactionDetails.getOwner()
                    +"\nReceiver : "+validTransactionDetails.getReciver()
                    +"\nCarrier: "+validTransactionDetails.getCarrier()
                    +"\nStatus: "+validTransactionDetails.getStatus());
            alertValidateBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertValidateBuilder.setPositiveButton("Timeline", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(((Dialog)dialog).getContext(), timeline_activity.class));
                }
            });

        }else{
            alertValidateBuilder.setMessage("Transaction Validity : "+validTransactionDetails.isValid());
            alertValidateBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }


        AlertDialog validationAlert = alertValidateBuilder.create();
        validationAlert.show();
    }

  /*  @Override
    public void onResume() {
        super.onResume();
        zXingScannerView.setResultHandler(getApplicationContext());
        zXingScannerView.startCamera();
    }*/

//    class ValidateBarcodeScan extends AsyncTask<String, String, Data> {
//        @Override
//        protected Data doInBackground(String... params) {
//            Data isValid = null ;
//            try {
//                System.out.println("Background Params: "+params.toString());
//                isValid = new DataMan().getTransactions(params[0].toString(), params[1].toString(), params[2].toString(), params[3].toString());
//                Thread.sleep(2000);
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            return isValid;
//        }
//
//        @Override
//        protected void onPostExecute(Data result) {
//            System.out.println(result);
//                validationDialog(result);
//
//
//        }
//    }

}
