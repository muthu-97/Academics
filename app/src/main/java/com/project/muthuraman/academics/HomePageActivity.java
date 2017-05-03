package com.project.muthuraman.academics;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class HomePageActivity extends AppCompatActivity {
    HomePageActivity h;
    SharedPreferences sp;
    ProgressBar spinner;
    RelativeLayout rl;
    String[] name,id,teach,slot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        h=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.e("err", new Date().getHours()+"" );
        spinner = (ProgressBar)findViewById(R.id.progressBar1);
        spinner.setIndeterminate(true);
        rl = (RelativeLayout) findViewById(R.id.hide);
        rl.setAlpha(0.1f);

        final android.app.FragmentManager fm = getFragmentManager();
        refresh();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sp.getString("type","").equals("S")){
                    fm.beginTransaction().add(R.id.frag_hold,new SelectCourse(),"selectcourse").commit();
                }
                else
                {
                    fm.beginTransaction().add(R.id.frag_hold,new AddCourse(),"addcourse").commit();
                }
            }
        });

    }


    public void detached(){
        refresh();
    }


    public void refresh(){
        final ListView l = (ListView) findViewById(R.id.ListView);
        sp = getSharedPreferences("MyAcad" , MODE_PRIVATE);
        JSONObject result = new JSONObject();

         new AsyncTask<String, String, JSONObject>() {

                @Override
                protected JSONObject doInBackground(String... params) {
                    try {
                        String response = NodeRequests.performGetCall("https://academics-server.herokuapp.com/getcourses/"+sp.getString("user",""));
                        JSONObject j = new JSONObject(response);
                        Log.e("Result",response);
                        return j;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return  null;
                }

             @Override
             protected void onPostExecute(JSONObject result) {
                 try {
                     if(result.getInt("status")==1) {
                         JSONArray arr = result.getJSONArray("courses");
                         name = new String[arr.length()];
                         id = new String[arr.length()];
                         teach = new String[arr.length()];
                         slot = new String[arr.length()];

                         for (int i = 0; i < arr.length(); i++) {
                             name[i] = arr.getJSONObject(i).getString("name");
                             id[i] = arr.getJSONObject(i).getString("code");
                             slot[i] = arr.getJSONObject(i).getString("slot");
                             teach[i] = arr.getJSONObject(i).getString("facnam");
                         }
                         Log.e("er", String.valueOf(arr.length()));
                         CustomAdapter c = new CustomAdapter(h, name, id, teach, slot);
                         l.setAdapter(c);
                         spinner.setVisibility(View.GONE);
                         rl.setAlpha(1.0f);
                         rl.setBackgroundColor(Color.WHITE);
                         l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                             @Override
                             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                 TextView na = (TextView)view.findViewById(R.id.textView4);
                                 Intent i = new Intent(getApplicationContext(),CoursePageActivity.class);
                                 i.putExtra("name",na.getText().toString());
                                 TextView ids = (TextView)view.findViewById(R.id.textView6);
                                 na = (TextView)view.findViewById(R.id.textView7);
                                 String s = ids.getText().toString()+na.getText().toString(),s1="";
                                 for(int j =0;j<s.length();j++){
                                     if(s.charAt(j)!=' '&&s.charAt(j)!='.')
                                         s1+=s.charAt(j);
                                 }
                                 i.putExtra("id",s1);

                                 startActivity(i);
                             }
                         });
                     }
                     else{
                         spinner.setVisibility(View.GONE);
                         rl.setAlpha(1.0f);
                         rl.setBackgroundColor(Color.WHITE);
                         String message = "No courses found.Add a course to continue!";
                         Snackbar.make( l, message, Snackbar.LENGTH_LONG)
                                 .setAction("Action",null).show();
                     }
                 } catch (Exception e) {
                     e.printStackTrace();
                 }
             }
         }.execute();


    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.logout:
                SharedPreferences.Editor ed = sp.edit();
                ed.putBoolean("curr",false);
                ed.commit();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}






