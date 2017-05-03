package com.project.muthuraman.academics;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class SignupActivity extends AppCompatActivity {
    Boolean isStudent=false;
    HashMap<String,String> hm=new HashMap<String, String>();
    View.OnClickListener cli;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        c= this;
        final EditText sid  = (EditText) findViewById(R.id.smbno);
        final EditText spw = (EditText) findViewById(R.id.spw);
        final EditText srpw  = (EditText) findViewById(R.id.srpw);
        final EditText sname = (EditText) findViewById(R.id.sname);
        final EditText sregno  = (EditText) findViewById(R.id.sregno);
        final TextView intro1  = (TextView) findViewById(R.id.textView2);
        final TextView intro2  = (TextView) findViewById(R.id.textView3);
        final ImageView img1  = (ImageView) findViewById(R.id.imageView2);
        final ImageView img2  = (ImageView) findViewById(R.id.imageView3);
        final Button stud = (Button)  findViewById(R.id.stud);
        final Button teacher = (Button)  findViewById(R.id.teacher);
        final Button signup = (Button)  findViewById(R.id.signup);
        View.OnClickListener cl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stud.setVisibility(View.GONE);
                teacher.setVisibility(View.GONE);
                img1.setVisibility(View.GONE);
                img2.setVisibility(View.GONE);

                sid.setVisibility(View.VISIBLE);sid.setAlpha(0);
                sid.animate().alpha(1).setDuration(1500);

                spw.setVisibility(View.VISIBLE);spw.setAlpha(0);
                spw.animate().alpha(1).setDuration(1500);

                srpw.setVisibility(View.VISIBLE);srpw.setAlpha(0);
                srpw.animate().alpha(1).setDuration(1500);

                sname.setVisibility(View.VISIBLE);sname.setAlpha(0);
                sname.animate().alpha(1).setDuration(1500);

                signup.setVisibility(View.VISIBLE);signup.setAlpha(0);
                signup.animate().alpha(1).setDuration(1500);

                if(v==stud){
                    isStud();
                sregno.setVisibility(View.VISIBLE);sregno.setAlpha(0);
                sregno.animate().alpha(1).setDuration(1500);}
            }
        };

        intro1.setVisibility(View.VISIBLE);intro1.setAlpha(0);
        intro1.animate().alpha(1).setDuration(1000);

        intro2.setVisibility(View.VISIBLE);intro2.setAlpha(0);
        intro2.animate().alpha(1).setDuration(2000).setStartDelay(1200);


        stud.setVisibility(View.VISIBLE);stud.setAlpha(0);
        stud.animate().alpha(1).setDuration(1500).setStartDelay(3400);

        teacher.setVisibility(View.VISIBLE);teacher.setAlpha(0);
        teacher.animate().alpha(1).setDuration(1500).setStartDelay(3400);

        img1.setVisibility(View.VISIBLE);img1.setAlpha(0.0f);
        img1.animate().alpha(1).setDuration(1500).setStartDelay(3400);

        img2.setVisibility(View.VISIBLE);img2.setAlpha(0.0f);
        img2.animate().alpha(1).setDuration(1500).setStartDelay(3400);



        teacher.setOnClickListener(cl);
        stud.setOnClickListener(cl);

        cli = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sn,sp,srp,smno,sr;
                sn = sname.getText().toString();
                sp = spw.getText().toString();
                srp = srpw.getText().toString();
                smno = sid.getText().toString();
                sr = sregno.getText().toString();

                if(sn.equals("")) {
                    sname.setError("Please fill this");
                    return;
                }
                if(smno.length()!=10) {
                    sid.setError("Please Enter a valid Mobile Number");
                    return;
                }
                if(smno.equals("")) {
                    sid.setError("Please fill this");
                    return;
                }
                if(sp.equals("")) {
                    spw.setError("Please fill this");
                    return;
                }
                if(srp.equals("")) {
                    srpw.setError("Please fill this");
                    return;
                }
                if(!srp.equals(sp)){
                    srpw.setError("Must match with passWord ");return;
                }
                String type="F";
                if(isStudent&&sr.equals("")) {
                    sregno.setError("Must be filled");
                    return;
                }
                if(isStudent)
                    type="S";

                hm.put("id",smno);
                hm.put("pass",sp);
                hm.put("type", type);
                hm.put("name", sn);
                hm.put("regno" , sr);
                signup.setOnClickListener(null);

                new AsyncTask<String, String, String>() {

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            String response = NodeRequests.performPostCall("https://academics-server.herokuapp.com/signup", gethm());
                            JSONObject j = new JSONObject(response);
                            Log.e("Result",response);
                            return String.valueOf(j.getInt("status"));
                        } catch (IOException ex) {
                            ex.printStackTrace();
                            return "-2";
                        } catch (JSONException e) {
                            e.printStackTrace();
                            return "-2";
                        }
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        String message="";
                        if(result.equals("1")){
                            final Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                            AlertDialog alertDialog = new AlertDialog.Builder(c).create();

                            // Setting Dialog Title
                            alertDialog.setTitle("Success");

                            // Setting Dialog Message
                            alertDialog.setMessage("Please login to continue");

                            // Setting OK Button
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(i);
                                    finish();
                                }
                            });
                            // Showing Alert Message
                            alertDialog.setCancelable(false) ;// cannot dismiss without Ok button
                            alertDialog.show();

                        }
                        else if(result.equals("0")){
                            Snackbar.make( findViewById(R.id.rls), message, Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();
                            message = "User ALready exists!";
                        }
                        else {message = "Unable to connect, please try again later.";
                            Snackbar.make( findViewById(R.id.rls), message, Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();}
                        signup.setOnClickListener(cli);
                    }
                }.execute();



            }
        };
        signup.setOnClickListener(cli);

    }
    public void isStud(){
        isStudent=true;
    }
    HashMap<String,String> gethm(){return hm;}
}
