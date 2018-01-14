package com.cergi.ruralbanking.rbm;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.cergi.ruralbanking.DemandeCreditActivity;
import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.RetraitActivity;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.VersementActivity;
import com.cergi.ruralbanking.account.AccountActivity;
import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.fingerprints.MyApplication;
import com.cergi.ruralbanking.transfer.MoneyTransferActivity;

public class MainActivity extends AppCompatActivity implements Runnable {

    MainActivity thisObject;

    Button btnNewAccount, btnVersement, btnRetrait, btnMoneyTransfer, btnDemandeCredit, btnNewMain;

    private Toolbar toolbar;

    ProgressDialog pDialog;
    int OperationType;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Safe.CurrentUser.USER_ABREGE);
        menu.add(R.string.logout);
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(thisObject, LoginActivity.class);
                startActivity(intent);
                thisObject.finish();
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Safe.MasterObject = this;
        thisObject = this;
        Safe.application = (MyApplication) thisObject.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btnNewAccount = (Button) findViewById(R.id.btnNewAccount);
        btnVersement = (Button) findViewById(R.id.btnVersement);
        btnRetrait = (Button) findViewById(R.id.btnRetrait);
        btnMoneyTransfer = (Button) findViewById(R.id.btnMoneyTransfer);
        btnDemandeCredit = (Button) findViewById(R.id.btnDemandeDeCredit);
        btnNewMain = (Button) findViewById(R.id.btnNew);

        btnNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Safe.Matricule = null;
                Intent intent = new Intent(thisObject, AccountActivity.class);
                startActivity(intent);
            }
        });
        btnVersement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisObject, VersementActivity.class);
                startActivity(intent);
            }
        });
        btnRetrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisObject, RetraitActivity.class);
                startActivity(intent);
            }
        });
        btnMoneyTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisObject, MoneyTransferActivity.class);
                startActivity(intent);
            }
        });
        btnDemandeCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisObject, DemandeCreditActivity.class);
                startActivity(intent);
            }
        });

        btnNewMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(thisObject, Main.class);
                startActivity(intent);
            }
        });
        if(savedInstanceState == null) {
            pDialog = ProgressDialog.show(thisObject, "", "Initialisation en cours... Veuillez patienter.");
            Thread t = new Thread(thisObject);
            OperationType = 1;
            t.start();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putString("mTitle", mTitle.toString());
//        setTitle(mTitle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        mTitle = savedInstanceState.getString("mTitle");
//        setTitle(mTitle);
        super.onRestoreInstanceState(savedInstanceState);
    }

    /*@Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }*/

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pDialog.dismiss();
            switch (OperationType) {
                case 1:

                    break;
            }
        }
    };

    public void run() {
        switch (OperationType) {
            case 1:
                try {
                    Safe.ListPays = WsCommunicator.ListPays(thisObject);
                    Safe.ListNaturePieceId = WsCommunicator.ListNaturePieceId(thisObject);
                    Safe.ListSitMatrim = WsCommunicator.ListSitMatrim(thisObject);
                    Safe.ListStatut = WsCommunicator.ListStatut(thisObject);
                    Safe.ListActivite = WsCommunicator.ListActivite(thisObject);
                    //Safe.ListTypeIntervenant = WsCommunicator.ListTypeIntervenant(thisObject);
                    Safe.ListProfession = WsCommunicator.ListProfession(thisObject);
                    Safe.ListDevise = WsCommunicator.ListDevise(thisObject);
                    Safe.ListPeriodicite = WsCommunicator.ListPeriodicite(thisObject);
                    Safe.ListSecteurActiv = WsCommunicator.ListSecteurActiv(thisObject);
                    Safe.ListLocal = WsCommunicator.ListLocalite(thisObject);
                    Safe.ListObjets = WsCommunicator.ListObjet(thisObject);
                    Safe.ListTypeEngagement = WsCommunicator.ListTypeEngagement(thisObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        handler.sendEmptyMessage(0);
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
