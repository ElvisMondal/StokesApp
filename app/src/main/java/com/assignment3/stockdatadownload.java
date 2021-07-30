package com.assignment3;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static android.provider.Settings.System.getString;
import static androidx.constraintlayout.motion.utils.Oscillator.TAG;

public class stockdatadownload implements Runnable {


    private static final String DATA_URL = " https://cloud.iexapis.com/stable/stock/";
    private MainActivity mainActivity;
    private String symbol;
    private static final String UR="/quote?token=pk_a9fdb523c1034f3b801465c23f8e1dd9 ";

    public stockdatadownload(MainActivity mainActivity,String symbol) {
        this.mainActivity = mainActivity;
        this.symbol=symbol;
    }

    @Override
    public void run() {

        Uri dataUri = Uri.parse(DATA_URL +symbol +UR);
        String urlToUse = dataUri.toString();

        Log.d(TAG, "run: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "run: HTTP ResponseCode NOT OK: " + conn.getResponseCode());
                return;
            }

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

            Log.d(TAG, "run: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "run: ", e);
            return;
        }

        parserJSON(sb.toString());
        Log.d(TAG, "run: ");


    }


    private void parserJSON(String s) {


        try {

            JSONObject jsonObject = new JSONObject(s);
            String symbolname = jsonObject.getString("symbol");
            String companyname = jsonObject.getString("companyName");
            String latestprice = jsonObject.getString("latestPrice");
            String change = jsonObject.getString("change");
            String changepercentage = jsonObject.getString("changePercent");


            final Stock stock= new Stock(symbolname, companyname, Double.parseDouble(latestprice), Double.parseDouble(change), Double.parseDouble(changepercentage));

            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.addStock(stock);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}



