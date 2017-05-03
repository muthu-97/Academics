package com.project.muthuraman.academics;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TakeAttenadnceActivity extends AppCompatActivity {

    boolean hasDate;
    String date;
    Map<String,AttendanceDate> ds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attenadnce);
        date = (new SimpleDateFormat("dd-M-yy")).format(new Date());
        final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        final String id = getIntent().getStringExtra("name") + "attendance";
        final ImageView qr = (ImageView) findViewById(R.id.QRCode);
        List<String> time = new ArrayList<>() ;
        int h = new Date().getHours();
        Log.e("err",Calendar.MINUTE+"");
        time.add(h + ":50");
        time.add((h+1)+":40");
        final Spinner s = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, time);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(dataAdapter);
        s.setSelection(0);
        final int x1 = (int) (Math.random()*1000);


        db.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(date)) {
                    hasDate = true;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final Button b = (Button) findViewById(R.id.tkat);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String x2 = (String) s.getSelectedItem();
                b.setOnClickListener(null);
            try{
                new AsyncTask<String, String, Bitmap>() {

                    @Override
                    protected Bitmap doInBackground(String... params) {
                        try {
                            String requesturl = "https://api.qrserver.com/v1/create-qr-code/?size=750x750&data={\"otp\":"+x1+",\"timeout\":\""+x2+"\"}";

                            Log.e("url",requesturl);
                            Bitmap response = NodeRequests.performGetImageCall(requesturl);
                            return response;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return  null;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bitmap) {
                        qr.setImageBitmap(bitmap);
                        if(hasDate) {
                            Map<String, Object> dateUpdate = new HashMap();
                            dateUpdate.put("otp" ,x1);
                            dateUpdate.put("exptime",x2);
                            db.child(id).child(date).updateChildren(dateUpdate);

                        }
                        else{
                            db.child(id).child(date).setValue(new AttendanceDate(x1,x2,new ArrayList<Student>()));
                        }
                    }

                }.execute();


            }catch(Exception e){e.printStackTrace();}





            }
        });

        //get permission
        //disable timeout


    }




}
