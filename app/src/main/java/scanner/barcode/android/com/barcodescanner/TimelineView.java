package scanner.barcode.android.com.barcodescanner;


import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.qap.ctimelineview.TimelineRow;
import org.qap.ctimelineview.TimelineViewAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class TimelineView extends AppCompatActivity {

    //Create Timeline Rows List
    private ArrayList<TimelineRow> timelineRowsList = new ArrayList<>();
    ArrayAdapter<TimelineRow> myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_view);

        //get the bundle
        Bundle b = getIntent().getExtras();
        //getting the arraylist from the key
        ArrayList<Data> transactions = (ArrayList<Data>) b.getSerializable("transactions");



        for(int i = 0 ; i< transactions.size(); i++){

            timelineRowsList.add(createRandomTimelineRow(transactions.get(i), i));
        }
        //        for (int i = 0; i < 15; i++) {
        //            //add the new row to the list
        //            timelineRowsList.add(createRandomTimelineRow(i));
        //        }


        //Create the Timeline Adapter
        myAdapter = new TimelineViewAdapter(this, 0, timelineRowsList,
                //if true, list will be sorted by date
                true);


        //Get the ListView and Bind it with the Timeline Adapter
        ListView myListView = (ListView) findViewById(R.id.timeline_listView);
        myListView.setAdapter(myAdapter);



        //if you wish to handle list scrolling
        myListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
            private LinearLayout lBelow;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;


            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && this.currentScrollState == SCROLL_STATE_IDLE) {

//                    for(int i = 0 ; i< transactions.size(); i++){
//                        timelineRowsList.add(createRandomTimelineRow(transactions.get(i), i));
//                    }
                    ////on scrolling to end of the list, add new rows
//                    for (int i = 0; i < 15; i++) {
//                      //  myAdapter.add(createRandomTimelineRow(i));
//                    }

                }
            }


        });

        //if you wish to handle the clicks on the rows
        AdapterView.OnItemClickListener adapterListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TimelineRow row = timelineRowsList.get(position);
                Toast.makeText(TimelineView.this, row.getTitle(), Toast.LENGTH_SHORT).show();
            }
        };
        myListView.setOnItemClickListener(adapterListener);


    }

    //Method to create new Timeline Row
    private TimelineRow createRandomTimelineRow(Data d, int i) {
        TimelineRow myRow = new TimelineRow(i);

       if(d.getStatus().equalsIgnoreCase("start")){

           // Create new timeline row (pass your Id)

           //to set the row Date (optional)
           //myRow.setDate(getDate(d.getDate().toString()));
           //to set the row Title (optional)
           myRow.setTitle(d.getStatus());
           //to set the row Description (optional)
           myRow.setDescription(" Carrier: " + d.getCarrier() +" \n Origin: " +d.getOwner()+" \n Receiver:  " +d.getReciver());
//        //to set the row bitmap image (optional)
            myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.start ));
//        //to set row Below Line Color (optional)
           myRow.setBellowLineColor(Color.argb(245, 0, 0, 0));
//        //to set row Below Line Size in dp (optional)
           myRow.setBellowLineSize(3);
//        //to set row Image Size in dp (optional)
           myRow.setImageSize(18);
//        //to set background color of the row image (optional)
           myRow.setBackgroundColor(Color.argb(245, 2, 135, 2));
//        //to set the Background Size of the row image in dp (optional)
           myRow.setBackgroundSize(30);
//        //to set row Date text color (optional)
//        myRow.setDateColor(getRandomColor());
//        //to set row Title text color (optional)
            myRow.setTitleColor(Color.argb(245, 245, 245, 245));
//        //to set row Description text color (optional)
           myRow.setDescriptionColor(Color.argb(245, 245, 245, 245));
       }else if(d.getStatus().equalsIgnoreCase("end")){
           // Create new timeline row (pass your Id)

           //to set the row Date (optional)
           //myRow.setDate(getDate(d.getDate().toString()));
           //to set the row Title (optional)
           myRow.setTitle(d.getStatus());
           //to set the row Description (optional)
           myRow.setDescription(" Carrier: " + d.getCarrier() +" \n Origin: " +d.getOwner()+" \n Receiver: " +d.getReciver());
//        //to set the row bitmap image (optional)
        myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.end ));
//        //to set row Below Line Color (optional)
           myRow.setBellowLineColor(Color.argb(245, 0, 0, 0));
//        //to set row Below Line Size in dp (optional)
           myRow.setBellowLineSize(3);
//        //to set row Image Size in dp (optional)
            myRow.setImageSize(18);
//        //to set background color of the row image (optional)
           myRow.setBackgroundColor(Color.argb(245, 245, 2, 2));
//        //to set the Background Size of the row image in dp (optional)
           myRow.setBackgroundSize(30);
//        //to set row Date text color (optional)
//        myRow.setDateColor(getRandomColor());
//        //to set row Title text color (optional)
            myRow.setTitleColor(Color.argb(245, 245, 245, 245));
//        //to set row Description text color (optional)
          myRow.setDescriptionColor(Color.argb(245, 245, 245, 245));
       }else{
           // Create new timeline row (pass your Id)

           //to set the row Date (optional)
           //myRow.setDate(getDate(d.getDate().toString()));
           //to set the row Title (optional)
           myRow.setTitle(d.getStatus());
           //to set the row Description (optional)
           myRow.setDescription(" Carrier: " + d.getCarrier() +" \n Origin: " +d.getOwner()+" \n Receiver: " +d.getReciver());
//        //to set the row bitmap image (optional)
        myRow.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.intransit ));
//        //to set row Below Line Color (optional)
           myRow.setBellowLineColor(Color.argb(245, 0, 0, 0));
//        //to set row Below Line Size in dp (optional)
           myRow.setBellowLineSize(3);
//        //to set row Image Size in dp (optional)
           myRow.setImageSize(18);
//        //to set background color of the row image (optional)
           myRow.setBackgroundColor(Color.argb(245, 2, 2, 180));
//        //to set the Background Size of the row image in dp (optional)
           myRow.setBackgroundSize(30);
//        //to set row Date text color (optional)
//        myRow.setDateColor(getRandomColor());
//        //to set row Title text color (optional)
        myRow.setTitleColor(Color.argb(245, 245, 245, 245));
//        //to set row Description text color (optional)
           myRow.setDescriptionColor(Color.argb(245, 245, 245, 245));
       }


        return myRow;
    }


    //Random Methods
    public int getRandomColor() {
        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        ;
        return color;
    }

    public int getRandomNumber(int min, int max) {
        return min + (int) (Math.random() * max);
    }


    public Date getDate(String date){
        System.out.println("Date Print>>>>>>>>>>>>>>"+date.toString());
        String fullDate = "";
        Date date_ = null;
        try {
            if(date.length()==7){
                String year = date.substring(0,4);
                String month = date.substring(4,5);
                String day = date.substring(5, date.length());
                fullDate = year+"-0"+month+"-"+day;
                System.out.println(fullDate);
            }else if(date.length()==8){
                String year = date.substring(0,4);
                String month = date.substring(4,6);
                String day = date.substring(6, date.length());
                fullDate = year+"-"+month+"-"+day;
                System.out.println(fullDate);
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

            date_ = new Date();

            date_ = df.parse(fullDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date_;
    }


}
