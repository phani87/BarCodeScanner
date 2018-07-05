package scanner.barcode.android.com.barcodescanner;

import android.app.DatePickerDialog;
import android.app.Dialog;


import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

/**
 * Datepicker class that renders datepicker
 */
public class DatePickerFragment extends AppCompatDialogFragment implements DatePickerDialog.OnDateSetListener {


    public String date_picked;



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);


        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);

    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
       Integer year = i;
       Integer month = i1;
       Integer day = i2;

       date_picked = (Integer.toString(year)+Integer.toString(month+1)+Integer.toString(day));

       System.out.println(date_picked);
    }

}
