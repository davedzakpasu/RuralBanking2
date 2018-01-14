package com.cergi.ruralbanking.account;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cergi.ruralbanking.CustomArrayAdapter;
import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.entities.INFO_CLIENT;

public class SearchResultsActivity extends AppCompatActivity {
    private TextView txtQuery;
    private ListView lv = null;
    public SearchResultsActivity thisObject = null;

    ProgressDialog pDialog = null;

    String query = null;

    Toolbar toolbar;

    ArrayList<INFO_CLIENT> lc = null;
    INFO_CLIENT ic = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisObject = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        txtQuery = (TextView) findViewById(R.id.txtQuery);
        lv = (ListView) findViewById(R.id.lvResults);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Enabling Back navigation on Action Bar icon

        startActivity(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        startActivity(intent);
    }

    @Override
    public void startActivity(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
//            txtQuery.setText("Search Query: " + query);
            if (!TextUtils.isEmpty(query)) {
                pDialog = ProgressDialog.show(thisObject, "", "Recherche...");
                new Thread(new Runnable() {
                    @SuppressLint("HandlerLeak")
                    private Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            pDialog.dismiss();
                            if (lc.size() == 0) {
                                txtQuery.setText("Recherche de : " + query + "\n Aucun résultat.");
                            } else {
                                txtQuery.setText("Recherche de : " + query + "\n" + lc.size() + " résultat(s)");
                                // Create the adapter to convert the array to views
                                CustomArrayAdapter aa = new CustomArrayAdapter(thisObject, lc);
                                // Attach the adapter to a ListView
                                lv.setAdapter(aa);

                                lv.setOnItemClickListener(new OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Safe.Matricule = lc.get(position).ETCIV_MATRICULE;
                                        thisObject.finish();
                                    }
                                });
                            }
                        }
                    };

                    @Override
                    public void run() {
                        try {
                            lc = WsCommunicator.ListeClient(thisObject, query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        }
    }
}