package com.assignment3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.JsonWriter;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private final List<Stock> stockList = new ArrayList<>();

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swiper;


    String title, text, ch;
    int pos;
    Stock ed, ko,lo;
    symbolloader obj;
    double p=0.0,pc=0.0,cp=0.0;
    private stockAdapter nAdap;

   List<String> temp= new ArrayList<>();
   List<String> tempList= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recycle);
        nAdap = new stockAdapter(stockList, this);
        recyclerView.setAdapter(nAdap);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        swiper = findViewById(R.id.swiper);



        temp=loadFiles();

        if (!checkNetworkConnection()) {

            loadFile();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Cannot Start Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();

            swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    doRefreshs();
                }
            });
            return;

        }


        else{

            for (int i = 0; i < temp.size(); i++) {

                stockdatadownload stockdatadownloadRunnable = new stockdatadownload(this,temp.get(i));
                new Thread(stockdatadownloadRunnable).start();
            }



        }
        symbolloader symbolloaderRunnable = new symbolloader();
        new Thread(symbolloaderRunnable).start();





        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                doRefresh();
            }
        });






    }



    private void doRefresh() {

        if (!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            swiper.setRefreshing(false);
            return;
        }

            List<Stock> tempList = new ArrayList<>(stockList);
            stockList.clear();
            for (int i = 0; i < tempList.size(); i++) {

                ko = tempList.get(i);

                stockdatadownload stockdatadownloadRunnable = new stockdatadownload(this, ko.getStock_Symbol());
                new Thread(stockdatadownloadRunnable).start();


            }
            Toast.makeText(this, "List Refreshed", Toast.LENGTH_SHORT).show();
            swiper.setRefreshing(false);


    }



    private void doRefreshs() {

        if (!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Stocks Cannot Be Updated Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            swiper.setRefreshing(false);
            return;
        }


            symbolloader symbolloaderRunnable = new symbolloader();
            new Thread(symbolloaderRunnable).start();

            List<Stock> tempList = new ArrayList<>(stockList);
            stockList.clear();
            for (int i = 0; i < tempList.size(); i++) {

                ko = tempList.get(i);

                stockdatadownload stockdatadownloadRunnable = new stockdatadownload(this, ko.getStock_Symbol());
                new Thread(stockdatadownloadRunnable).start();


            }
            Toast.makeText(this, "List Refreshed", Toast.LENGTH_SHORT).show();
            swiper.setRefreshing(false);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stock_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.adstock) {
            stockDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void stockDialog() {

        if (!checkNetworkConnection()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No Network Connection");
            builder.setMessage("Content Cannot Be Added Without A Network Connection");
            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);


        final EditText et = new EditText(this);
        et.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);/*professor this changes the keyboard to all CAPS there by allowing
                                                                                             only CAPS letter but user still can enter small letters by changing
                                                                                             keyboard from the screen*/

        et.setFilters(new InputFilter[] {new InputFilter.AllCaps()});/*this ensures that the entry to the field is CAPS only even after the user changes the keyboard from screen*/
        et.setGravity(Gravity.CENTER_HORIZONTAL);
        builder.setView(et);


        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                ch = et.getText().toString();

                final ArrayList<String> results = symbolloader.findMatches(ch);


                if (results.size() == 0) {
                    doNoAnswer(ch);
                } else if (results.size() == 1) {
                    doSelection(results.get(0));
                } else {
                    String[] array = results.toArray(new String[0]);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Make a selection");
                    builder.setItems(array, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String symbol = results.get(which);
                            doSelection(symbol);
                        }
                    });
                    builder.setNegativeButton("Nevermind", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    AlertDialog dialog2 = builder.create();
                    dialog2.show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });

        builder.setMessage("Please enter a Symbol");
        builder.setTitle("Stock Selection");

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    private void doNoAnswer(String symbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Data for stock symbol");
        builder.setTitle("Symbol Not Found: " + symbol);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doSelection(String sym) {
        String[] data = sym.split("-");
        stockdatadownload stockdatadownloadRunnable = new stockdatadownload(this, data[0].trim());
        new Thread(stockdatadownloadRunnable).start();
    }

    public void addStock(Stock stock) {
        if (stock == null) {
            wrongStockAlert(ch);
            return;
        }


        if (stockList.contains(stock)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage("Stock Symbol "+ stock.getStock_Symbol() + " is already displayed");
            builder.setTitle("Duplicate Stock");
            builder.setIcon(R.drawable.baseline_warning_black_36);

            AlertDialog dialog = builder.create();
            dialog.show();
            return;
        }

        stockList.add(stock);
        Collections.sort(stockList);
        nAdap.notifyDataSetChanged();

    }



    private void wrongStockAlert(String sym) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("No data for selection");
        builder.setTitle("Symbol Not Found: " + sym);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private boolean checkNetworkConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    @Override
    public void onClick(View v) {

        int poss = recyclerView.getChildLayoutPosition(v);
        ed = stockList.get(poss);

        //Toast.makeText(this,"Hi",Toast.LENGTH_LONG).show();


    }



    @Override
    public boolean onLongClick(View view) {
        pos = recyclerView.getChildLayoutPosition(view);
        ed = stockList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Stock");
        builder.setMessage("Delete Stock Symbol"+" "+ "'"+ed.getStock_Symbol()+"'"+"?");
        builder.setIcon(R.drawable.baseline_delete_outline_black_36);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                remove(null);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                new MainActivity();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
        return false;
    }

    public void remove(View v) {
        if (!stockList.isEmpty()) {
            stockList.remove(pos);
            nAdap.notifyDataSetChanged();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        saveNote();



    }


    private void saveNote() {
        try {
            FileOutputStream fos = getApplicationContext().
                    openFileOutput(getString(R.string.file_name),Context.MODE_PRIVATE);

            JsonWriter writer = new JsonWriter(new OutputStreamWriter(fos, getString(R.string.encoding)));
            writer.setIndent("  ");
            writer.beginArray();
            for (Stock n :stockList) {
                writer.beginObject();
                writer.name("Symbol").value(n.getStock_Symbol());
                writer.name("CompanyName").value(n.getCompany_name());
                writer.endObject();
            }
            writer.endArray();
            writer.close();



        } catch (Exception e) {
            e.getStackTrace();
        }

    }




    private List<Stock> loadFile() {



        try {

            InputStream fis = getApplicationContext().
                    openFileInput(getString(R.string.file_name));

            byte[] data = new byte[(int) fis.available()];
            int loaded = fis.read(data);

            fis.close();
            String json = new String(data);
            JSONArray noteArr = new JSONArray(json);
            for (int i = 0; i < noteArr.length(); i++) {
                JSONObject nObj = noteArr.getJSONObject(i);


                title = nObj.getString("Symbol");
                text = nObj.getString("CompanyName");



                Stock n = new Stock(title, text,p,pc,cp);
                stockList.add(n);


            }
            nAdap.notifyDataSetChanged();


        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return stockList;


    }

    private List<String> loadFiles() {



        try {

            InputStream fis = getApplicationContext().
                    openFileInput(getString(R.string.file_name));

            byte[] data = new byte[(int) fis.available()];
            int loaded = fis.read(data);
            // Log.d(TAG, "readJSONData: Loaded " + loaded + " bytes");
            fis.close();
            String json = new String(data);
            JSONArray noteArr = new JSONArray(json);
            for (int i = 0; i < noteArr.length(); i++) {
                JSONObject nObj = noteArr.getJSONObject(i);


                title = nObj.getString("Symbol");
                text = nObj.getString("CompanyName");



                tempList.add(title);


            }



        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.no_file), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return tempList;


    }

}