package com.project.muthuraman.academics;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CoursePageActivity extends AppCompatActivity {

    HashMap<String,Integer> r;
    File file;
    String s;
    Context cont;
    int size =0,present=0,late=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_page);
        final Intent x=getIntent();
        setTitle(x.getStringExtra("name"));
        final String id = getIntent().getStringExtra("id") + "attendance";

        LinearLayout ll1 = (LinearLayout) findViewById(R.id.grpcht);
        LinearLayout ll2 = (LinearLayout) findViewById(R.id.ll2);
        LinearLayout ll3 = (LinearLayout) findViewById(R.id.ll3);

        TextView tv1 = (TextView) findViewById(R.id.textView11);
        TextView intv1 = (TextView) findViewById(R.id.inftxt123);

        ll1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),ChatPark.class);
                i.putExtra("name", x.getStringExtra("id"));
                startActivity(i);
            }
        });

        final SharedPreferences sp = getSharedPreferences("MyAcad" , MODE_PRIVATE);
        if(sp.getString("type" , "F").equals("F")){
            tv1.setText("Take attendance");
            intv1.setText("Create qr code and let students scan it");

        }
        else{
            tv1.setText("Give attendance");
            intv1.setText("Scan qr code and give attendance");
        }

        ll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sp.getString("type" , "F").equals("F")){
                    Intent i = new Intent(getApplicationContext(),TakeAttenadnceActivity.class);
                    i.putExtra("name", x.getStringExtra("id"));
                    startActivity(i);

                }
                else{
                    Intent i = new Intent(getApplicationContext(),GiveAttendanceActivity.class);
                    i.putExtra("name", x.getStringExtra("id"));
                    startActivity(i);
                }
            }
        });

        cont = this;
        ll3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar2);
                spinner.setIndeterminate(true);
                final RelativeLayout rl = (RelativeLayout) findViewById(R.id.toHide);
                rl.setAlpha(0.1f);
                if(sp.getString("type" , "F").equals("F")){
                    s ="";

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(id);
                    final List<String[]> ls = new ArrayList<>();
                    r = new HashMap<>();

                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterable<DataSnapshot> d = dataSnapshot.getChildren();

                            for(DataSnapshot i:d){
                                Log.e("att" , i.toString());
                                String[] row = new String[60];
                                for(int k=0;k<row.length;k++)row[k]="";
                                row[0]=i.getKey();

                                List<Student> st = new ArrayList<Student>();
                                for(DataSnapshot j : i.child("students").getChildren()){
                                    st.add(j.getValue(Student.class));
                                }
                                for(int j =0;j<st.size();j++){
                                    Student s = st.get(j);
                                    if(!r.containsKey(s.id)) {size++;r.put(s.id,size);}
                                    row[r.get(s.id)]="P";
                                    Log.e("att",r.toString());
                                }
                                ls.add(row);
                            }
                            String[] row = new String[60];
                            row[0]="Date";
                            for(String k:r.keySet()){
                                row[r.get(k)] = k;
                            }
                            ls.add(0,row);
                            for(int k = 0 ; k < ls.size();k++){
                                row = ls.get(k);

                                for (int l=0;l<=r.size();l++){
                                    if(k!=0&&l!=0 && !row[l].equals("P")) row[l]="A";
                                    s+=(l!=0)?(","+row[l]):(row[l]);
                                }s+='\n';
                            }

                            String filename = "attendance.csv";
                            FileOutputStream outputStream;

                            Log.e("att" , s);
                            file = new File(getExternalFilesDir(null), filename);

                            try {
                                outputStream = new FileOutputStream(file);
                                outputStream.write(s.getBytes());
                                outputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            spinner.setVisibility(View.GONE);
                            rl.setAlpha(1.0f);
                            sendMail();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
                else{
                    DatabaseReference db = FirebaseDatabase.getInstance().getReference().child(id);
                    Log.e("ID" , id);
                    db.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            size =(int) dataSnapshot.getChildrenCount();
                            Iterable<DataSnapshot> d = dataSnapshot.getChildren();

                            for(DataSnapshot i:d){
                                List<Student> st = new ArrayList<Student>();
                                for(DataSnapshot j : i.child("students").getChildren()){
                                    st.add(j.getValue(Student.class));
                                }
                                for(int j =0;j<st.size();j++){
                                    Student s = st.get(j);
                                    if(s.id.equals(getSharedPreferences("MyAcad" , MODE_PRIVATE).getString("user","")))
                                    {present++;late+=s.late;}
                                }

                            }
                            final AlertDialog alertDialog = new AlertDialog.Builder(cont).create();

                            // Setting Dialog Title
                            alertDialog.setTitle("Attendance");

                            // Setting Dialog Message
                            alertDialog.setMessage("Total classes :" + size +"\nAttended classes :" + present +"\nLate Index :" + late );

                            // Setting OK Button
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.hide();
                                }
                            });
                            // Showing Alert Message
                            alertDialog.setCancelable(false) ;// cannot dismiss without Ok button
                            alertDialog.show();
                            Log.e("Att", "" + size + " " + present + " " + late);
                            spinner.setVisibility(View.GONE);
                            rl.setAlpha(1.0f);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }
        });
    }


    public void sendMail(){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent .setType("*/*");
        emailIntent .putExtra(Intent.EXTRA_EMAIL, "muthuraman1997@gmail.com");
        emailIntent .putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        emailIntent .putExtra(Intent.EXTRA_SUBJECT, "Attendance");
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
