package com.assignment3;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class symbolloader implements Runnable {

    private static final String DATA_URL = " https://api.iextrading.com/1.0/ref-data/symbols";
    private static final String TAG = "SymbolLoader";
    private MainActivity mainActivity;
    double p = 0.0, pc = 0.0, cp = 0.0;
    String symbol, companyName,vl,lp;
    int k = 0;

    public static HashMap<String, String> symbolNameLoad= new HashMap<>();



    @Override
    public void run() {

        Uri dataUri = Uri.parse(DATA_URL);
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

        process(sb.toString());
        Log.d(TAG, "run: ");


    }

    private void process(String s) {
        try {
            JSONArray jObjMain = new JSONArray(s);

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jCountry = (JSONObject) jObjMain.get(i);

                String smbl = jCountry.getString("symbol");
                String name = jCountry.getString("name");

                symbolNameLoad.put(smbl, name);
            }
            Log.d(TAG, "process: ");
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static ArrayList<String> findMatches(String str) {
        String strToMatch = str.toLowerCase().trim();
        HashSet<String> matchSet = new HashSet<>();

        for (String sym : symbolNameLoad.keySet()) {
            if (sym.toLowerCase().trim().contains(strToMatch)) {
                matchSet.add(sym + " - " + symbolNameLoad.get(sym));
            }
            String name = symbolNameLoad.get(sym);
            if (name != null &&
                    name.toLowerCase().trim().contains(strToMatch)) {
                matchSet.add(sym + " - " + name);
            }
        }


        ArrayList<String> results = new ArrayList<>(matchSet);
        Collections.sort(results);

        return results;
    }



    }



