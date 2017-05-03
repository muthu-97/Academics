package com.project.muthuraman.academics;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by muthuraman on 24/4/17.
 */

public class SelectCourse extends Fragment {

    Fragment f = this;
    View v;
    AdapterView.OnItemClickListener cli;
    String[] name,id,teach,slot;
    HashMap<String,String> hm = new HashMap<>();
    SharedPreferences sp ;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.select_course,container,false);
        sp=getActivity().getSharedPreferences("MyAcad" , MODE_PRIVATE);
        refresh();
        return v;
    }

    public void refresh(){
        final ListView l = (ListView) v.findViewById(R.id.selectlist);
        cli = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final View v2 = view;
                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                // Setting Dialog Title
                alertDialog.setTitle("Sure");

                // Setting Dialog Message
                alertDialog.setMessage("Please enter class number to confirm.Note: A notification will be sent to faculty");

                final EditText ed = new EditText(getActivity());
                ed.setHint("class number");
                alertDialog.setView(ed);
                // Setting OK Button
                alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,"submit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(ed.getText().toString().equals("")){
                            ed.setError("Please fill this.");
                            return;
                        }
                        TextView t = ((TextView)v2.findViewById(R.id.textView5));
                        hm.put("ccode",t.getText().toString());
                        t = ((TextView)v2.findViewById(R.id.textView6));
                        hm.put("facnam",t.getText().toString());
                        hm.put("room", ed.getText().toString());
                        hm.put("studnum",sp.getString("user",""));
                        new AsyncTask<String, String, String>() {

                            @Override
                            protected String doInBackground(String... params) {
                                try {
                                    String response = NodeRequests.performPostCall("https://academics-server.herokuapp.com/selectcourse", hm);
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
                                    AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                                    // Setting Dialog Title
                                    alertDialog.setTitle("Success");

                                    // Setting Dialog Message
                                    alertDialog.setMessage("Please click ok to continue");

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
                                else {
                                    Toast.makeText(getActivity(),"Wrong class number! Action failed!" , Toast.LENGTH_SHORT);
                                    alertDialog.cancel();

                                }
                            }
                        }.execute();

                    }
                });
                // Showing Alert Message
                alertDialog.setCancelable(true) ;
                alertDialog.show();
            }
        };

        new AsyncTask<String, String, JSONObject>() {

                @Override
                protected JSONObject doInBackground(String... params) {
                    try {
                        String response = NodeRequests.performGetCall("https://academics-server.herokuapp.com/courses/");
                        JSONObject j = new JSONObject(response);
                        Log.e("ResultSelect",response);
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
                JSONArray arr = null;
                try {
                    arr = result.getJSONArray("courses");
                    name = new String[arr.length()];
                    id = new String[arr.length()];
                    teach = new String[arr.length()];
                    slot = new String[arr.length()];
                    for(int i =0;i<arr.length();i++){
                        name[i]=arr.getJSONObject(i).getString("name");
                        id[i]=arr.getJSONObject(i).getString("code");
                        slot[i]=arr.getJSONObject(i).getString("slot");
                        teach[i]=arr.getJSONObject(i).getString("facnam");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                CustomAdapter c = new CustomAdapter(((HomePageActivity)getActivity()),name,id,teach,slot);
                l.setAdapter(c);

                l.setOnItemClickListener(cli);
            }
        }.execute();

    }


    @Override
    public void onDestroy() {
        ((HomePageActivity)getActivity()).detached();
        super.onDestroy();
    }


}
