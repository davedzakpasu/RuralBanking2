package com.cergi.ruralbanking.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cergi.ruralbanking.Alerts;
import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.entities.CATEGORIE_CPTE;
import com.cergi.ruralbanking.entities.DEVISE;
import com.cergi.ruralbanking.entities.GENRE_CPTE;
import com.cergi.ruralbanking.rbm.LoginActivity;

/**
 * Created by Thierry on 13/02/14.
 */
@SuppressLint({ "DefaultLocale", "HandlerLeak" })
public class NewAccountActivity extends AppCompatActivity implements Runnable {

    NewAccountActivity thisObject = null;

    ProgressDialog pDialog = null;
    int OperationType = 0;

    Button btnCreateAccount = null;
    Button btnCancel = null;
    Spinner spinCategorie = null;
    EditText etDev = null;
    Spinner spinGenre = null;
    Spinner spinDevise = null;
    EditText txtMatricule = null;
    EditText txtNomReduit = null;
    EditText txtNumCpte = null;
    EditText txtCleRib = null;
    EditText txtlibSpecifiq = null;
    EditText txtModeArrete = null;


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {menu.add(Safe.CurrentUser.USER_ABREGE);
        menu.add("Déconnexion");
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
        thisObject = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_account);

        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCreateAccount = (Button)findViewById(R.id.btnCreateAccount);

        spinCategorie = (Spinner)findViewById(R.id.spinnerCategorieCpte);
//        etDev = (EditText)findViewById(R.id.etDevise);
        spinGenre = (Spinner)findViewById(R.id.spinnerGenre);
        spinDevise = (Spinner)findViewById(R.id.spinnerDevise);

        txtMatricule = (EditText)findViewById(R.id.txtMatricule);
        txtNomReduit = (EditText)findViewById(R.id.txtNomReduit);
        txtNumCpte = (EditText)findViewById(R.id.txtNumCpte);
        txtCleRib = (EditText)findViewById(R.id.txtCleRib);
        txtlibSpecifiq = (EditText)findViewById(R.id.txtLibSpecifiq);
        txtModeArrete = (EditText)findViewById(R.id.txtModeArrete);


        txtMatricule.setText(Safe.Matricule);
        txtNomReduit.setText(Safe.NomReduit.toUpperCase());
        txtlibSpecifiq.setText(Safe.NomReduit.toUpperCase());

        ArrayAdapter<DEVISE> daDevise = new ArrayAdapter<DEVISE>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListDevise);
        daDevise.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDevise.setAdapter(daDevise);

//        etDev.setText(Safe.ListDevise.CODE+"-"+Safe.ListDevise.LIBELLE);

        spinCategorie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                pDialog = ProgressDialog.show(thisObject, "", "Veuillez patienter...");
                Thread t = new Thread(thisObject);
                OperationType = 2;
                t.start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thisObject.finish();
            }
        });

        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: create account
                pDialog = ProgressDialog.show(thisObject, "", "Création du compte en cours ...");
                Thread t = new Thread(thisObject);
                OperationType = 3;
                t.start();
            }
        });

        pDialog = ProgressDialog.show(thisObject, "", "Veuillez patienter...");
        Thread t = new Thread(thisObject);
        OperationType = 1;
        t.start();
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg)  {
        pDialog.dismiss();
        switch(OperationType)
        {
            case 1:
                ArrayAdapter<CATEGORIE_CPTE> daCateg = new ArrayAdapter<CATEGORIE_CPTE>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListCategorieCpte);
                daCateg.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinCategorie.setAdapter(daCateg);

                break;
            case 2:
                ArrayAdapter<GENRE_CPTE> daGenre = new ArrayAdapter<GENRE_CPTE>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListGenre);
                daGenre.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinGenre.setAdapter(daGenre);

                txtNumCpte.setText(Safe.DetailsCpte.NUM_CPTE);
                txtCleRib.setText(Safe.DetailsCpte.CLE_RIB);
                txtModeArrete.setText(Safe.DetailsCpte.MODARR_LIBELLE);
                break;
            case 3:
                if(Safe.AccountOpenResult.Success)
                {
                    Toast.makeText(Safe.MasterObject, "Compte ["+Safe.DetailsCpte.NUM_CPTE+"] a été créé.",
                            Toast.LENGTH_LONG).show();

                    thisObject.finish();
                }
                else
                    Alerts.showAlert("Erreur", Safe.AccountOpenResult.ErrorMessage, thisObject);
                break;
        }
        }
    };

    public void run()
    {
        switch(OperationType)
        {
            case 1:
                Safe.ListCategorieCpte = WsCommunicator.ListCategorieCpte(thisObject, Safe.Matricule, Safe.CurrentUser.USER_CODE);
                break;
            case 2:
                String codeCateg = ((CATEGORIE_CPTE)spinCategorie.getSelectedItem()).CODE;
                Safe.ListGenre = WsCommunicator.ListGenreCpte(thisObject, codeCateg);

                Safe.DetailsCpte = WsCommunicator.GetDetailsCompte(thisObject, Safe.Matricule, codeCateg, Safe.CurrentUser.AGENCE_CODE);
                break;
            case 3:
                String categ = ((CATEGORIE_CPTE)spinCategorie.getSelectedItem()).CODE;
                String cptAssoc = ((GENRE_CPTE)spinGenre.getSelectedItem()).PLANCPTA_NUMCPTE;
                String genre = ((GENRE_CPTE)spinGenre.getSelectedItem()).CODE;
                String devise = ((DEVISE)spinDevise.getSelectedItem()).CODE;
                Safe.AccountOpenResult = WsCommunicator.CreerCompte(thisObject,Safe.DetailsCpte.MODARR_CODE,categ, Safe.CurrentUser.AGENCE_CODE,
                        Safe.DetailsCpte.NUM_CPTE, genre, devise, cptAssoc, txtlibSpecifiq.getText().toString(), Safe.DetailsCpte.CLE_RIB, Safe.CurrentUser.USER_CODE);
                break;
            default:
                break;
        }
        handler.sendEmptyMessage(0);
    }
}
