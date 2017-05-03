package com.project.muthuraman.academics;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by muthuraman on 22/4/17.
 */

public class NodeRequests {

    public static String performPostCall(String requestURL,
                                         HashMap<String, String> postDataParams) throws IOException {

        String s = getPostDataString(postDataParams);
        Log.e("er", s);
        URL url = new URL(requestURL + '?' + s);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        String line;
        StringBuffer jsonString = new StringBuffer();

        uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        uc.setRequestMethod("POST");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        uc.disconnect();
        return jsonString.toString();
    }

    private static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }


    public static String performGetCall(String requestURL) throws IOException {

        URL url = new URL(requestURL);
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        String line;
        StringBuffer jsonString = new StringBuffer();

        uc.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        uc.setRequestMethod("GET");
        uc.setDoInput(true);
        uc.setInstanceFollowRedirects(false);
        uc.connect();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            while ((line = br.readLine()) != null) {
                jsonString.append(line);
            }
            br.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        uc.disconnect();
        return jsonString.toString();
    }

    public static Bitmap performGetImageCall(String requestURL) {
        try {
            Bitmap result;
            URL url = new URL(requestURL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream in = urlConnection.getInputStream();
            result = BitmapFactory.decodeStream(in);
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("err" ,"mal");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("err" ,"mal2");

        }
        return null;

    }
}

