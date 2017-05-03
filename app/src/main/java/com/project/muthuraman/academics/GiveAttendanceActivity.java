package com.project.muthuraman.academics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.AutoText;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class GiveAttendanceActivity extends AppCompatActivity {

    AttendanceDate a;
    SharedPreferences sp;
    String date;
    private IntentIntegrator qrScan;
    TextView textViewData;
    DatabaseReference db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_attendance);

        sp = getSharedPreferences("MyAcad" , MODE_PRIVATE);
        date = (new SimpleDateFormat("dd-M-yy")).format(new Date());
        a = new AttendanceDate();
        String id = getIntent().getStringExtra("name") + "attendance";
        db = FirebaseDatabase.getInstance().getReference().child(id);
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(date)) {
                    a.otp = Integer.parseInt(dataSnapshot.child(date).child("otp").getValue().toString());
                    a.exptime = dataSnapshot.child(date).child("exptime").getValue().toString();
                    a.currTime = Long.parseLong(dataSnapshot.child(date).child("currTime").getValue().toString());
                    //a.students =  dataSnapshot.child(date).child("students").getValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        textViewData = (TextView) findViewById(R.id.testing);
        qrScan = new IntentIntegrator(this);
        qrScan.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    if(obj.getInt("otp") == a.otp) {
                        int late = (int) (new Date().getTime() - a.currTime) / 60000;
                        Student st = new Student(sp.getString("name", ""), sp.getString("user", ""), late);
                        db.child(date).child("students").push().setValue(st);
                        textViewData.setText("Attendence given, " + late + " minutes late to class");
                    }
                    } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {

    }
}
