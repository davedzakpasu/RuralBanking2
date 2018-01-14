/**
 *
 */
package com.cergi.ruralbanking;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.entities.DEVISE;
import com.cergi.ruralbanking.entities.INFO_COMPTE;
import com.cergi.ruralbanking.entities.LOCALITE;
import com.cergi.ruralbanking.entities.OBJET;
import com.cergi.ruralbanking.entities.PERIODICITE;
import com.cergi.ruralbanking.entities.SECTEUR_ACTIV;
import com.cergi.ruralbanking.entities.TYPE_ENGAGEMENT;
import com.cergi.ruralbanking.fingerprints.ConnectActivity;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnGenCharListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnGetImageListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnRegModelListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnUpCharListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnUpImageListener;
import com.cergi.ruralbanking.fingerprints.utils.ToastUtil;
import com.cergi.ruralbanking.rbm.LoginActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by CERGI on  22 janv. 2015
 */
public class DemandeCreditActivity extends AppCompatActivity implements Runnable, OnClickListener {

    DemandeCreditActivity thisObject;

    private Toolbar toolbar;

    TextView txtNumDepot;
    String num_depot = "";

    EditText txtNumCpte, txtNomClt, txtMontant, txtDuree, txtDateVisite, txtComments;

    Spinner spinTypeCredit, spinPeriodic, spinDev, spinSectAct, spinLocalite, spinObjet;

    Button btnValidateDemande, btnCancelDemande;

    ImageButton btnFinger;

    ImageView imgClient;

    ProgressDialog pDialog;
    int OperationType, Choice;
    byte[] emp;

    AlertDialog.Builder ab = null;

    INFO_COMPTE infoCompte;

    private AsyncFingerprint getFingerprint;

    private ProgressDialog progressDialog;

    @SuppressWarnings("unused")
    private byte[] model;

    private int countFinger;

    private Bitmap bitmapFinger;

    // android built in classes for bluetooth operations
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    public static final int SCANNER_ACTIVITY = 1;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;

    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;
    @SuppressWarnings("unused")
    private static final int REQUEST_ENABLE_BT = 0;

    GregorianCalendar now = new GregorianCalendar();
    Calendar cal = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;

    int year = now.get(Calendar.YEAR), month = now.get(Calendar.MONTH), day = now.get(Calendar.DAY_OF_MONTH);
    int y, m, d;
    Date startDate = new GregorianCalendar(year, month, day).getTime();
    Date endDate = new GregorianCalendar(year, 11, 31).getTime();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add(Safe.CurrentUser.USER_ABREGE);
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
        setContentView(R.layout.demande_credit);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Home Button Enabled
        //getSupportActionBar().setHomeButtonEnabled(true);

        // Add subtitle
        getSupportActionBar().setSubtitle(R.string.demande_credit);

        txtNumDepot = (TextView) findViewById(R.id.txtNumDepot);
        txtNumCpte = (EditText) findViewById(R.id.txtNumCpteClient);
        txtNomClt = (EditText) findViewById(R.id.txtNomClient);
        txtMontant = (EditText) findViewById(R.id.txtMttDemande);
        txtDuree = (EditText) findViewById(R.id.txtDuree);
        txtDateVisite = (EditText) findViewById(R.id.txtDateVisite);
        txtComments = (EditText) findViewById(R.id.txtComments);

        imgClient = (ImageView) findViewById(R.id.imgClient);

        spinTypeCredit = (Spinner) findViewById(R.id.spinnerTypeCredit);
        spinPeriodic = (Spinner) findViewById(R.id.spinnerPeriode);
        spinDev = (Spinner) findViewById(R.id.spinnerDeviseMtD);
        spinSectAct = (Spinner) findViewById(R.id.spinnerSectAct);
        spinLocalite = (Spinner) findViewById(R.id.spinnerLocal);
        spinObjet = (Spinner) findViewById(R.id.spinnerObjet);

        btnValidateDemande = (Button) findViewById(R.id.btnValidateDemande);
        btnCancelDemande = (Button) findViewById(R.id.btnCancelDemande);

        //liste des devises
        ArrayAdapter<DEVISE> daDev = new ArrayAdapter<DEVISE>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListDevise);
        daDev.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDev.setAdapter(daDev);
        spinDev.setSelection(16);

        // liste des types d'engagement
        ArrayAdapter<TYPE_ENGAGEMENT> adapter = new ArrayAdapter<TYPE_ENGAGEMENT>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListTypeEngagement);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTypeCredit.setAdapter(adapter);

        //liste des p�riode
        ArrayAdapter<PERIODICITE> adPeriod = new ArrayAdapter<PERIODICITE>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListPeriodicite);
        adPeriod.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPeriodic.setAdapter(adPeriod);
        spinPeriodic.setSelection(3);

        //liste des secteurs d'activit�
        ArrayAdapter<SECTEUR_ACTIV> adSectAct = new ArrayAdapter<SECTEUR_ACTIV>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListSecteurActiv);
        adSectAct.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSectAct.setAdapter(adSectAct);
        spinSectAct.setSelection(0);

        //liste des localit�s
        ArrayAdapter<LOCALITE> adLocal = new ArrayAdapter<LOCALITE>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListLocal);
        adLocal.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinLocalite.setAdapter(adLocal);
        spinLocalite.setSelection(0);

        //liste des objets_demande
        ArrayAdapter<OBJET> adObjet = new ArrayAdapter<OBJET>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListObjets);
        adObjet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinObjet.setAdapter(adObjet);
        spinObjet.setSelection(0);

        txtDateVisite.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        y = year;
                        m = monthOfYear;
                        d = dayOfMonth;

                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, monthOfYear);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        updateLabel(txtDateVisite);
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(thisObject, pDateSetListener, y, m, d);
                dialog.getDatePicker().setMinDate(startDate.getTime());
                dialog.getDatePicker().setMaxDate(endDate.getTime());
                dialog.getDatePicker().init(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), null);
                dialog.show();
            }
        });

        txtNumCpte.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (txtNumCpte.getText().toString().trim().equals(""))
                    btnFinger.setClickable(true);
                else if (txtNumCpte.getText().length() == 12) {
                    btnFinger.setClickable(false);
                    pDialog = ProgressDialog.show(thisObject, "", "Recherche du compte en cours ...");
                    Thread t = new Thread(thisObject);
                    OperationType = 1;
                    t.start();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
            }

            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });

        btnValidateDemande.setOnClickListener(thisObject);

        btnCancelDemande.setOnClickListener(thisObject);

        initView();
        initViewListener();
        initData();
    }

    private void initView() {
        btnFinger = (ImageButton) findViewById(R.id.fingerClient);
    }

    private void initData() {
        try {
            getFingerprint = new AsyncFingerprint(Safe.application.getHandlerThread().getLooper(),
                    Safe.application.getChatService());

            getFingerprint.setOnGetImageListener(new OnGetImageListener() {
                @Override
                public void onGetImageSuccess() {
                    cancelProgressDialog();
                    getFingerprint.PS_UpImage();
                    showProgressDialog(R.string.processing);
                }

                @Override
                public void onGetImageFail() {
                    getFingerprint.PS_GetImage();
                }
            });

            getFingerprint.setOnUpImageListener(new OnUpImageListener() {
                @Override
                public void onUpImageSuccess(byte[] data) {
                    Bitmap image = BitmapFactory.decodeByteArray(data, 0, data.length);
                    if (bitmapFinger != null && !bitmapFinger.isRecycled()) {
                        bitmapFinger.recycle();
                    }
                    bitmapFinger = image;
//					srcFP.setBackgroundDrawable(new BitmapDrawable(image));
                    getFingerprint.PS_GenChar(countFinger);
                }

                @Override
                public void onUpImageFail() {
                    Log.i("whw", "up image fail");
                    cancelProgressDialog();
                }
            });

            getFingerprint.setOnGenCharListener(new OnGenCharListener() {
                @Override
                public void onGenCharSuccess(int bufferId) {
                    if (bufferId == 1) {
                        cancelProgressDialog();
                        showProgressDialog(R.string.print_finger_again);
                        getFingerprint.PS_GetImage();
                        countFinger++;
                    } else if (bufferId == 2) {
                        getFingerprint.PS_RegModel();
                    }
                }

                @Override
                public void onGenCharFail() {
                    cancelProgressDialog();
                }
            });

            getFingerprint.setOnRegModelListener(new OnRegModelListener() {

                @Override
                public void onRegModelSuccess() {
                    cancelProgressDialog();
                    getFingerprint.PS_UpChar();
                    getMatch();
                }

                @Override
                public void onRegModelFail() {
                    cancelProgressDialog();
                }
            });

            getFingerprint.setOnUpCharListener(new OnUpCharListener() {
                @Override
                public void onUpCharSuccess(byte[] model) {
                    cancelProgressDialog();
                    Log.i("whw", "#################model.length=" + model.length);
                    thisObject.model = model;
                }

                @Override
                public void onUpCharFail() {
                    cancelProgressDialog();
                }
            });
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    private void initViewListener() {
        btnFinger.setOnClickListener(thisObject);
    }

    private void showProgressDialog(int resId) {
        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getResources().getString(resId));
            progressDialog.show();
        } catch (Exception ex) {
            ex.getMessage();
            ex.printStackTrace();
        }
    }

    private void cancelProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (OperationType) {
                case 1:
                    pDialog.dismiss(); //ferme la boite de progression
                    if (infoCompte != null) {
                        if (TextUtils.isEmpty(infoCompte.MessageErreur)) {
                            Alerts.showAlert("Erreur", infoCompte.MessageErreur, thisObject);
                        } else {
                            txtNumCpte.setEnabled(false);
                            //r�cuperation des infos du compte
                            txtNomClt.setText(infoCompte.ETCIV_NOMREDUIT);

                            byte[] bb = new byte[]{0, 0, 0, 0};
                            // affichage de la photo
                            if (Arrays.equals(infoCompte.FP_PHOTO, bb)) {
                                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_img);
                                imgClient.setImageBitmap(bm);
                            } else {
                                final Bitmap b = getBitMap(infoCompte.FP_PHOTO);

                                Matrix m = new Matrix();
                                //m.postRotate(-90, b.getWidth()/2, b.getHeight()/2);
                                Bitmap bmp = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);

                                imgClient.setImageBitmap(bmp);
                            }
                            txtNumDepot.setText(num_depot);
                        }
                    } else {
                        Alerts.showAlert("Erreur", "Numero de compte invalide.", thisObject);
                    }
                    break;
                case 2:
                    pDialog.dismiss(); //ferme la boite de progression
                    if (infoCompte != null) {
                        if (TextUtils.isEmpty(infoCompte.MessageErreur)) {
                            Alerts.showAlert("Erreur", infoCompte.MessageErreur, thisObject);
                        } else {
                            txtNumCpte.setEnabled(false);
                            //r�cuperation des infos du compte
                            txtNomClt.setText(infoCompte.ETCIV_NOMREDUIT);

                            byte[] bb = new byte[]{0, 0, 0, 0};
                            // affichage de la photo
                            if (Arrays.equals(infoCompte.FP_PHOTO, bb)) {
                                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_img);
                                imgClient.setImageBitmap(bm);
                            } else {
                                final Bitmap b = getBitMap(infoCompte.FP_PHOTO);

                                Matrix m = new Matrix();
                                //m.postRotate(-90, b.getWidth()/2, b.getHeight()/2);
                                Bitmap bmp = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);

                                imgClient.setImageBitmap(bmp);
                            }
                            txtNumDepot.setText(num_depot);
                        }
                    } else {
                        Alerts.showAlert("Erreur", "Empreinte incorrecte ou Compte introuvable.", thisObject);
                    }
                    break;
                case 3:
                    pDialog.dismiss();
                    if (Safe.SaveDemandeResult != null) {
                        if (Safe.SaveDemandeResult.Success) {

                            Toast.makeText(Safe.MasterObject, "La demande a été soumise pour être traitée",
                                    Toast.LENGTH_LONG).show();
                            thisObject.finish();
                        } else

                            Toast.makeText(Safe.MasterObject, "Erreur interne, veuillez réessayer plus tard",
                                    Toast.LENGTH_LONG).show();
                        thisObject.finish();
                    } else

                        Toast.makeText(Safe.MasterObject, "Demande non transmise, veuillez réessayer plus tard",
                                Toast.LENGTH_LONG).show();
                    thisObject.finish();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private static Bitmap getBitMap(byte[] bitmapdata) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0,
                bitmapdata.length);
        return bitmap;
    }

    private byte[] getByte(Bitmap bitmap) {

        try {
            /*
			 * int bytes = bitmap.getByteCount(); // or we can calculate bytes
			 * this way. Use a different value than 4 // if you don't use 32bit
			 * images. // int bytes = bitmap.getWidth() * bitmap.getHeight() *
			 * 4;
			 * 
			 * ByteBuffer buffer = ByteBuffer.allocate(bytes); // Create a new
			 * // buffer bitmap.copyPixelsToBuffer(buffer); // Move the byte
			 * data to the // buffer imageInByte = buffer.array();
			 */

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);

            return stream.toByteArray();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public void getMatch() {
        pDialog = ProgressDialog.show(thisObject, "", "Recherche du compte en cours ...");
        Thread t = new Thread(thisObject);
        OperationType = 2;
        t.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fingerClient:
                try {
                    if (!Safe.application.isConnect()) {
                        startActivity(new Intent(thisObject, ConnectActivity.class));
                        return;
                    } else {
                        initData();
                    }
                    countFinger = 1;
                    showProgressDialog(R.string.print_finger);
                    getFingerprint.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
                } catch (Exception ex) {
                    ToastUtil.showToast(thisObject, ex.getMessage());
                }
                break;
            case R.id.btnValidateDemande:
                try {
                    if (!TextUtils.isEmpty(txtNumCpte.getText().toString()) && !TextUtils.isEmpty(txtNomClt.getText().toString())
                            && !TextUtils.isEmpty(txtMontant.getText().toString()) && !TextUtils.isEmpty(txtDuree.getText().toString())
                            && !TextUtils.isEmpty(txtDateVisite.getText().toString()) && !TextUtils.isEmpty(txtComments.getText().toString())) {

                        ab = new AlertDialog.Builder(thisObject);
                        ab.setTitle("Confirmer opération?");
                        ab.setMessage("Voulez-vous valider cette demande?")
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                        pDialog = ProgressDialog.show(thisObject, "", "Validation de la demande en cours ...");
                                        Thread t = new Thread(thisObject);
                                        OperationType = 3;
                                        t.start();
                                    }
                                })

                                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        ab.show();
                    } else
                        Toast.makeText(thisObject, "Veuillez renseigner tous les champs SVP.", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    ToastUtil.showToast(thisObject, e.getMessage());
                }
                break;
            case R.id.btnCancelDemande:
                try {
                    txtNumDepot.setText("");
                    txtNumCpte.setEnabled(true);
                    imgClient.setImageResource(R.drawable.default_img);

                } catch (Exception e) {
                    ToastUtil.showToast(thisObject, e.getMessage());
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void run() {
        switch (OperationType) {
            case 1:
                try {
                    emp = new byte[]{0, 0, 0, 0};
                    num_depot = WsCommunicator.NumeroDepot(thisObject);
                    infoCompte = WsCommunicator.InfoCompte(thisObject, txtNumCpte.getText().toString(), emp, Safe.CurrentUser.ETCIV_MATRICULE, "CAI20", "XOF");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    emp = getByte(bitmapFinger);
//    			String s = Base64.encodeToString(emp, Base64.DEFAULT);
                    num_depot = WsCommunicator.NumeroDepot(thisObject);
                    infoCompte = WsCommunicator.InfoCompte(thisObject, "", emp, Safe.CurrentUser.ETCIV_MATRICULE, "CAI10", "XOF");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                try {
                    String codeTypeEngag = ((TYPE_ENGAGEMENT) spinTypeCredit.getSelectedItem()).CODE_TYPE_ENGAGEMENT;
                    String codeLocalite = ((LOCALITE) spinLocalite.getSelectedItem()).LOCA_CODE;
                    String codeSectAct = ((SECTEUR_ACTIV) spinSectAct.getSelectedItem()).SECTACT_CODE;
                    String codePeriodic = ((PERIODICITE) spinPeriodic.getSelectedItem()).PERIODIC_CODE;
                    String codeObjet = ((OBJET) spinObjet.getSelectedItem()).OBJPRET_CODE;

                    Safe.SaveDemandeResult = WsCommunicator.ValiderDemande(thisObject, txtNumDepot.getText().toString(), infoCompte.ETCIV_MATRICULE,
                            Safe.CurrentUser.AGENCE_CODE, Integer.parseInt(txtMontant.getText().toString()), codeTypeEngag, codeObjet,
                            codeLocalite, codeSectAct, codePeriodic, Integer.parseInt(txtDuree.getText().toString()), txtDateVisite.getText().toString(),
                            txtComments.getText().toString(), Safe.CurrentUser.USER_CODE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        handler.sendEmptyMessage(0);
    }

    private void updateLabel(EditText et) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

        et.setText(sdf.format(cal.getTime()));
    }

}
