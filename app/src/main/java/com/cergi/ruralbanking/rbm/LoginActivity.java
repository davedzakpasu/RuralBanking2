package com.cergi.ruralbanking.rbm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cergi.ruralbanking.Alerts;
import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.entities.INFO_CAISSE;
import com.cergi.ruralbanking.entities.UTILISATEUR;

/**
 * Created by Thierry on 03/02/14.
 */
@SuppressLint("HandlerLeak")
public class LoginActivity extends AppCompatActivity implements Runnable {

    Button btnCancel = null;
    Button btnLogin = null;
    ImageButton btnConfig = null;
    UTILISATEUR acc = null;
    INFO_CAISSE info_cais = null;
    EditText txtUsername = null;

    EditText txtPassword = null;

    LoginActivity thisObject = null;
    ProgressDialog pDialog = null;
    int OperationType = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisObject = this;
        Safe.LoginScreenActivity = this;
        Safe.MasterObject = this;
        Safe.LoadConfig();
        super.onCreate(savedInstanceState);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.login);

        btnLogin = (Button)findViewById(R.id.btnLogin);
//        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnConfig = (ImageButton)findViewById(R.id.btnConfig);
        txtUsername = (EditText) findViewById(R.id.txtUsername);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialog = ProgressDialog.show(thisObject, "", "Connexion en cours...");
                Thread t = new Thread(thisObject);
                OperationType = 1;
                t.start();
            }
        });

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(thisObject);

                alert.setTitle("Paramètres de Connexion");
                alert.setMessage("Adresse IP du Serveur");

                // Set an EditText view to get user input
                final EditText input = new EditText(thisObject);
                alert.setView(input);

                InputFilter[] filters = new InputFilter[1];
                filters[0] = new InputFilter() {
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        if (end > start) {
                            String destTxt = dest.toString();
                            String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                            if (!resultingTxt.matches("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) {
                                return "";
                            } else {
                                String[] splits = resultingTxt.split("\\.");
                                for (int i = 0; i < splits.length; i++) {
                                    if (Integer.valueOf(splits[i]) > 255) {
                                        return "";
                                    }
                                }
                            }
                        }
                        return null;
                    }
                };

                input.setFilters(filters);

                //load data
                SharedPreferences sharedPref = thisObject.getPreferences(Context.MODE_PRIVATE);
                String server_ip = sharedPref.getString("server_ip", Safe.ServerIP);
                input.setText(server_ip);

                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        // Do something with value!
                        SharedPreferences sharedPref = thisObject.getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("server_ip", value);
                        editor.commit();

                        Safe.ServerIP = value;
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                    }
                });

                alert.show();
            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pDialog.dismiss();
            switch (OperationType)
            {
            case 1:
            	if(acc != null)
            	{
                    pDialog = ProgressDialog.show(thisObject, "", "Connexion réussie. Bienvenue "+acc.USER_NOM+"..");
                    Thread t = new Thread(thisObject);
                    OperationType = 2;
                    t.start();
            	}
            	else
            	{
            		if(!Safe.Error)
            			Alerts.showAlert("ACCES REFUSE", "Nom d'Utilisateur/Mot de passe Incorrect.", thisObject);
            		else
            		{
            			Toast.makeText(thisObject,
            					"Impossible de se connecter au Serveur. \n " +
            					"Veuillez re-essayer plus tard ou vérifier les paramètres de connexion.",
            					Toast.LENGTH_LONG).show();
            		}
            	}

            	break;
            case 2:
                /*if(info_cais != null)
                {*/
                    Intent intent = new Intent(thisObject, Main.class);
                    startActivity(intent);
                    Safe.CurrentUser = acc;

                    thisObject.finish();
                /*}
                else {
                    if(!Safe.Error)
                        Alerts.showAlert("ERREUR", "Vous n'êtes pas autorisé à utiliser l'application", thisObject);
                    else
                    {
                        Toast.makeText(thisObject,
                                "Erreur interne. \n " +
                                        "Veuillez re-essayer plus tard",
                                Toast.LENGTH_LONG).show();
                    }
                }*/

                break;
            }
        }
    };

    public void run()
    {
        switch (OperationType)
        {
            case 1:
                try {
                    acc = WsCommunicator.Login(this, txtUsername.getText().toString(), txtPassword.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 2:
                try {
                    info_cais = WsCommunicator.InfoCaisse(acc.USER_CODE, acc.SERVAG_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        handler.sendEmptyMessage(0);
    }

}
