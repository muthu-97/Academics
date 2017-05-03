package com.project.muthuraman.academics;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences sp;
    SharedPreferences.Editor ed;
    View.OnClickListener cl;
    HashMap<String,String> hm=new HashMap<String, String>();
    String num,pass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp = getSharedPreferences("MyAcad" , MODE_PRIVATE);
        ed=sp.edit();
        final EditText id  = (EditText) findViewById(R.id.mbno);
        final TextView t = (TextView) findViewById(R.id.textView);
        final EditText pw = (EditText) findViewById(R.id.pw);
        final RelativeLayout r = (RelativeLayout) findViewById(R.id.toHide);
        ImageView iv = (ImageView) findViewById(R.id.imageView);

        r.setAlpha(0);
        r.animate().alpha(1).setDuration(1500);

        iv.setAlpha(0.0f);
        iv.animate().alpha(1).setDuration(1500);

        final Button b1 = (Button) findViewById(R.id.login);
        cl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = id.getText().toString();
                pass = pw.getText().toString();
                if(num.equals(""))
                {    id.setError("Please fill this"); return;}
                if(num.length()!=10)
                {    id.setError("Please Enter a valid Mobile Number");return;}
                if(pass.equals(""))
                {   pw.setError("Please fill this");return;}

                b1.setOnClickListener(null);
                final ProgressBar spinner = (ProgressBar)findViewById(R.id.progressBar2);
                spinner.setIndeterminate(true);
                final RelativeLayout rl = (RelativeLayout) findViewById(R.id.toHide);
                rl.setAlpha(0.1f);
                hm.put("id",num);
                hm.put("pass",pass);
                new AsyncTask<String, String, String>() {

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            String response = NodeRequests.performPostCall("https://academics-server.herokuapp.com/login", gethm());
                            JSONObject j = new JSONObject(response);
                            Log.e("Result",response);
                            if(j.getInt("status")==1){ ed.putString("type",j.getString("type"));ed.putString("name",j.getString("name"));}
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
                        spinner.setVisibility(View.GONE);
                        rl.setAlpha(1.0f);
                        String message="";
                        if(result.equals("1")){
                            Intent i = new Intent(getApplicationContext(),HomePageActivity.class);
                            ed.putString("user",num);
                            ed.putBoolean("curr" , true );
                            ed.commit();
                            startActivity(i);

                            finish();
                        }
                        else if(result.equals("0")){
                            message = "Wrong Password\nTry Again!";
                            Snackbar.make( findViewById(R.id.rls), message, Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();
                        }
                        else if(result.equals("-1")){
                            message = "User Not Found!";
                            Snackbar.make( findViewById(R.id.rls), message, Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();
                        }
                        else{ message = "Unable to connect, please try again later.";
                        Snackbar.make( r, message, Snackbar.LENGTH_LONG)
                                .setAction("Action",null).show();}


                        b1.setOnClickListener(cl);

                    }
                }.execute();


            }
        };
        b1.setOnClickListener(cl);

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(i);
            }
        });

    }
    HashMap<String,String> gethm(){return hm;}

    @Override
    protected void onStart() {
        sp = getSharedPreferences("MyAcad" , MODE_PRIVATE);
        if(sp.getBoolean("curr",false))
        {
            Intent i = new Intent(getApplicationContext(),HomePageActivity.class);
            startActivity(i);
        }
        super.onStart();
    }
}
