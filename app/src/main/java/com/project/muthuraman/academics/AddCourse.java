package com.project.muthuraman.academics;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by muthuraman on 24/4/17.
 */

public class AddCourse extends Fragment {
    HashMap<String,String> hm = new HashMap<>();
    View v;
    View.OnClickListener cl;
    final Fragment f =this;
    SharedPreferences sp;
    Context context;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v =  inflater.inflate(R.layout.add_course,container,false);
        Log.e("addc","v created");
        sp = context.getSharedPreferences("MyAcad" , MODE_PRIVATE);

        final EditText cname  = (EditText) v.findViewById(R.id.cname);
        final EditText croom = (EditText) v.findViewById(R.id.croom);
        final EditText ccode  = (EditText) v.findViewById(R.id.ccode);
        final EditText cslot = (EditText) v.findViewById(R.id.cslot);

        final TextView intro1  = (TextView) v.findViewById(R.id.textView8);
        final TextView intro2  = (TextView) v.findViewById(R.id.textView9);

        final Button signup = (Button)  v.findViewById(R.id.ccreate);

        intro1.setVisibility(View.VISIBLE);intro1.setAlpha(0);
        intro1.animate().alpha(1).setDuration(800);
        intro2.setVisibility(View.VISIBLE);intro2.setAlpha(0);
        intro2.animate().alpha(1).setDuration(800);

        cname.setVisibility(View.VISIBLE);cname.setAlpha(0);
        cname.animate().alpha(1).setDuration(1500);
        croom.setVisibility(View.VISIBLE);croom.setAlpha(0);
        croom.animate().alpha(1).setDuration(1500);
        ccode.setVisibility(View.VISIBLE);ccode.setAlpha(0);
        ccode.animate().alpha(1).setDuration(1500);
        cslot.setVisibility(View.VISIBLE);cslot.setAlpha(0);
        cslot.animate().alpha(1).setDuration(1500);

        signup.setVisibility(View.VISIBLE);signup.setAlpha(0);
        signup.animate().alpha(1).setDuration(2000);

        cl = new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
                String cn,cs,cr,cc,cf;
                cn = cname.getText().toString();
                cs = cslot.getText().toString();
                cr = croom.getText().toString();
                cc = ccode.getText().toString();
                cf = sp.getString("user", " ");

                if(cn.equals("")) {
                    cname.setError("Please fill this");
                    return;
                }
                if(cs.equals("")) {
                    cslot.setError("Please fill this");
                    return;
                }
                if(cr.equals("")) {
                    croom.setError("Please fill this");
                    return;
                }
                if(cc.equals("")) {
                    ccode.setError("Please fill this");
                    return;
                }

                signup.setOnClickListener(null);
                hm.put("code",cc);
                hm.put("name",cn);
                hm.put("slot", cs);
                hm.put("room", cr);
                hm.put("fac" , cf);
                new AsyncTask<String, String, String>() {

                    @Override
                    protected String doInBackground(String... params) {
                        try {
                            String response = NodeRequests.performPostCall("https://academics-server.herokuapp.com/addcourse", gethm());
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
                            AlertDialog alertDialog = new AlertDialog.Builder(context).create();

                            // Setting Dialog Title
                            alertDialog.setTitle("Success");

                            // Setting Dialog Message
                            alertDialog.setMessage("The course was added succesfully");

                            // Setting OK Button
                            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,"OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    getFragmentManager().beginTransaction().remove(f).commitAllowingStateLoss();
                                }
                            });
                            // Showing Alert Message
                            alertDialog.setCancelable(false) ;// cannot dismiss without Ok button

                            alertDialog.show();

                        }
                        else{ message = "Unable to connect, please try again later.";
                            Snackbar.make( v, message, Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();
                        signup.setOnClickListener(cl);}
                    }
                }.execute();

            }
        };
        signup.setOnClickListener(cl);
        
        return v;
        
    }

    @Override
    public void onAttach(Context context) {
        Log.e("addc","attached");
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroy() {
        ((HomePageActivity)getActivity()).detached();
        super.onDestroy();
    }

    HashMap<String,String> gethm(){return hm;}


}
