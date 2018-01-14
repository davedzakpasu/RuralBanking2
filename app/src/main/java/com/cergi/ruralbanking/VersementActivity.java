package com.cergi.ruralbanking;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.entities.INFO_COMPTE;
import com.cergi.ruralbanking.fingerprints.ConnectActivity;
import com.cergi.ruralbanking.fingerprints.MyApplication;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnGenCharListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnGetImageListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnRegModelListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnUpCharListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnUpImageListener;
import com.cergi.ruralbanking.fingerprints.utils.ToastUtil;
import com.cergi.ruralbanking.rbm.LoginActivity;

/**
 * Created by Thierry on 03/03/14.
 */
@SuppressLint("HandlerLeak")
public class VersementActivity extends AppCompatActivity implements Runnable {

    VersementActivity thisObject;

    private Toolbar toolbar;

    private MyApplication application;

    private AsyncFingerprint getFingerprint;

    private ImageButton srcFP;

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

    ProgressDialog pDialog = null;
    int OperationType = 0;
    int Choice;

    TextView txtRefOper = null;
    String ref_oper = "";
    EditText txtNumCpte = null;
    EditText txtNomReduit = null;
    EditText txtNomDeposant = null;
    EditText txtAdresse = null;
    EditText txtMontant = null;

    Button btnValid = null;
    Button btnCancel = null;
    ImageButton btnSearch = null;
    ImageView photo = null;

    INFO_COMPTE infoCompte = null;
    byte[] emp;

    AlertDialog.Builder ab = null;
//    DialogInterface.OnClickListener dc, dc_yes = null;

    //nom de l'appareil
    String device = (Build.MODEL).replace("-", "");

    GridLayout grid = null;

    String fd;

    DecimalFormat df = new DecimalFormat("#,###.###");

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

    @SuppressLint({"WrongViewCast", "SimpleDateFormat"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisObject = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.versement);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Home Button Enabled
        //getSupportActionBar().setHomeButtonEnabled(true);

        // Add subtitle
        getSupportActionBar().setSubtitle(R.string.versement_esp);

        txtRefOper = (TextView) findViewById(R.id.txtRefOper);
        txtNumCpte = (EditText) findViewById(R.id.txtNumCpte);
        txtNomReduit = (EditText) findViewById(R.id.txtNomReduit);
        txtNomDeposant = (EditText) findViewById(R.id.txtNomDeposant);
        txtAdresse = (EditText) findViewById(R.id.txtAdresse);
        txtMontant = (EditText) findViewById(R.id.txtMontant);

        photo = (ImageView) findViewById(R.id.imgPreviewV);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnValid = (Button) findViewById(R.id.btnValider);
        // btnValid.setEnabled(false);

        btnValid.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String libelle_cpte = txtNomReduit.getText().toString();
                String nom = txtNomDeposant.getText().toString();
                String adress = txtAdresse.getText().toString();

                if (txtNumCpte.getText().toString().trim().isEmpty()) {
                    txtNumCpte.setError(getString(R.string.empty_field));
                } else if (libelle_cpte.trim().isEmpty()) {
                    txtNomReduit.setError(getString(R.string.empty_field));
                } else if (nom.trim().isEmpty() || adress.trim().isEmpty()) {
                    txtNomDeposant.setError(getString(R.string.empty_field));
                    txtAdresse.setError(getString(R.string.empty_field));
                } else if (txtMontant.getText().toString().trim().equals("0") || txtMontant.getText().toString().isEmpty()) {
                    txtMontant.setError(getString(R.string.empty_field));
                } else {
                    ab = new AlertDialog.Builder(thisObject);
                    ab.setTitle("Confirmer opération?");
                    ab.setMessage("Versement de " + df.format(Double.parseDouble(txtMontant.getText().toString())) + "FCFA sur le compte : " + txtNumCpte.getText().toString() + " - " + txtNomReduit.getText().toString() + "\nVeuillez entrer votre code de sécurité pour confirmer :");
                    // Set an EditText view to get user input
                    final EditText input = new EditText(thisObject);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    ab.setView(input);
                    ab.setPositiveButton("Valider", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (input.getText().toString().isEmpty())
                                input.setError(getString(R.string.empty_field));
                            else {
                                dialog.cancel();
                                pDialog = ProgressDialog.show(thisObject, "", "Validation du versement en cours ...");
                                Thread t = new Thread(thisObject);
                                OperationType = 2;
                                t.start();
                            }
                        }
                    })

                            .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    ab.show();
                }

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                txtRefOper.setText("");
                txtNumCpte.setText("");
                txtNumCpte.setEnabled(true);
                photo.setImageResource(R.drawable.default_img);
                txtNomReduit.setText("");
                txtNomReduit.setEnabled(true);
                txtNomDeposant.setText("");
                txtNomDeposant.setEnabled(true);
                txtAdresse.setText("");
                txtAdresse.setEnabled(true);
            }
        });

        txtNumCpte.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                if (txtNumCpte.getText().toString().trim().equals(""))
                    srcFP.setClickable(true);
                else if (txtNumCpte.getText().length() == 12) {
                    srcFP.setClickable(false);
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

        Date d = Calendar.getInstance().getTime();
        java.text.SimpleDateFormat simpleDateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
        fd = simpleDateFormat.format(d);

        initView();
        initViewListener();
        initData();
    }

    private void initView() {
        srcFP = (ImageButton) findViewById(R.id.fingerV);
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
//						doSOmething();
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
//					ToastUtil.showToast(thisObject, R.string.generate_char_fail);
                }
            });

            getFingerprint.setOnRegModelListener(new OnRegModelListener() {

                @Override
                public void onRegModelSuccess() {
                    cancelProgressDialog();
                    getFingerprint.PS_UpChar();
                    doSomething();
//							ToastUtil.showToast(thisObject, R.string.reg_model_success);
                }

                @Override
                public void onRegModelFail() {
                    cancelProgressDialog();
//							ToastUtil.showToast(thisObject, R.string.reg_model_fail);
                }
            });

            getFingerprint.setOnUpCharListener(new OnUpCharListener() {
                @Override
                public void onUpCharSuccess(byte[] model) {
                    cancelProgressDialog();
                    Log.i("whw", "#################model.length=" + model.length);
                    thisObject.model = model;
//					ToastUtil.showToast(thisObject, R.string.register_success);
                }

                @Override
                public void onUpCharFail() {
                    cancelProgressDialog();
//					ToastUtil.showToast(thisObject, R.string.register_fail);
                }
            });
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }

    private void initViewListener() {
        srcFP.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
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
            }
        });
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

    private static Bitmap getBitMap(byte[] bitmapdata) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0,
                bitmapdata.length);
        return bitmap;
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

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (OperationType) {
                case 1:
                    pDialog.dismiss(); //ferme la boite de progression
                    if (infoCompte != null) {
                        if (infoCompte.MessageErreur.trim().compareTo("") == 0) {
                            Alerts.showAlert("Erreur", infoCompte.MessageErreur, thisObject);
                        } else {
                            txtNumCpte.setEnabled(false);
                            //récuperation des infos du compte
                            txtNomReduit.setText(infoCompte.ETCIV_NOMREDUIT);
                            txtNomDeposant.setText(infoCompte.ETCIV_NOMREDUIT);
                            txtAdresse.setText(infoCompte.ETCIV_ADRESS_GEO1);

                            byte[] bb = new byte[]{0, 0, 0, 0};
                            // affichage de la photo
                            if (Arrays.equals(infoCompte.FP_PHOTO, bb)) {
                                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_img);
                                photo.setImageBitmap(bm);
                            } else {
                                final Bitmap b = getBitMap(infoCompte.FP_PHOTO);

                                Matrix m = new Matrix();
                                m.postRotate(-90, b.getWidth() / 2, b.getHeight() / 2);
                                Bitmap bmp = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);

                                photo.setImageBitmap(bmp);
                            }

                            //reference de l'operation
                            txtRefOper.setText(ref_oper.replace("\\", ""));
                        }
                    } else {
                        Alerts.showAlert("Erreur", "Numero de compte invalide.", thisObject);
                    }
                    break;
                case 2:
                    pDialog.dismiss();
                    if (Safe.CashDepositResult != null) {
                        if (Safe.CashDepositResult.Success) {
                            String m = txtMontant.getText().toString();
//                            for (int i = 0, count = grid.getChildCount(); i < count; ++i) {
//                                View view = grid.getChildAt(i);
//                                if (view instanceof EditText) {
//                                    ((EditText) view).getText().clear();
//                                }
//                            }
                            Toast.makeText(Safe.MasterObject, "Opération bien déroulée.\n Versement de " + m + "FCFA effectué sur le Compte : [" + infoCompte.CPTEAUXIL_NUMCPT + " - " + infoCompte.ETCIV_NOMREDUIT + "]",
                                    Toast.LENGTH_LONG).show();
                            thisObject.finish();
                        } else

                            Toast.makeText(Safe.MasterObject, "Erreur interne, veuillez réessayer plus tard",
                                    Toast.LENGTH_LONG).show();
                        thisObject.finish();
                    } else

                        Toast.makeText(Safe.MasterObject, "Versement non effectué, veuillez réessayer plus tard",
                                Toast.LENGTH_LONG).show();
                    thisObject.finish();
                    break;
                case 4:
                    pDialog.dismiss(); //ferme la boite de progression
                    if (infoCompte != null) {
                        if (infoCompte.MessageErreur.trim().compareTo("") == 0) {
                            Alerts.showAlert("Erreur", infoCompte.MessageErreur, thisObject);
                        } else {
                            txtNumCpte.setEnabled(false); //désactiver champ NumCpte
                            txtRefOper.setText(ref_oper.replace("\\", ""));//référence de l'operation
                            //récuperation des infos du compte
                            txtNumCpte.setText(infoCompte.CPTEAUXIL_NUMCPT);
                            txtNomReduit.setText(infoCompte.ETCIV_NOMREDUIT);

                            byte[] bb = new byte[]{0, 0, 0, 0};
                            // affichage de la photo
                            if (Arrays.equals(infoCompte.FP_PHOTO, bb)) {
                                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_img);
                                photo.setImageBitmap(bm);
                            } else {
                                final Bitmap b = getBitMap(infoCompte.FP_PHOTO);

                                Matrix m = new Matrix();
                                m.postRotate(-90, b.getWidth() / 2, b.getHeight() / 2);
                                Bitmap bmp = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);

                                photo.setImageBitmap(bmp);
                            }
                        }
                    } else {
                        Alerts.showAlert("Erreur", "Empreinte incorrecte ou compte introuvable.", thisObject);
                    }
                    break;
            }
        }
    };

    public void run() {
        switch (OperationType) {
            case 1:
                try {
                    emp = new byte[]{0, 0, 0, 0};
                    ref_oper = WsCommunicator.ReferenceOperation(thisObject, Safe.CurrentUser.USER_CODE);
                    infoCompte = WsCommunicator.InfoCompte(thisObject, txtNumCpte.getText().toString(), emp, Safe.CurrentUser.ETCIV_MATRICULE, "CAI20", "XOF");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    //TODO: ADD DATECOMPTA TO PARAMS
                    Safe.CashDepositResult = WsCommunicator.ValiderOperation(thisObject, txtNumCpte.getText().toString(),
                            Integer.parseInt(txtMontant.getText().toString()), txtNomDeposant.getText().toString(),
                            txtAdresse.getText().toString(), "05/01/2015", device, txtRefOper.getText().toString(), "XOF",
                            txtNomReduit.getText().toString(), 1, "CAISV", Safe.CurrentUser.USER_CODE, infoCompte.NATURE_ID_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                try {
                    emp = getByte(bitmapFinger);
//        			String s = Base64.encodeToString(emp, Base64.DEFAULT);
                    ref_oper = WsCommunicator.ReferenceOperation(thisObject, Safe.CurrentUser.USER_CODE);
                    infoCompte = WsCommunicator.InfoCompte(thisObject, "", emp, Safe.CurrentUser.ETCIV_MATRICULE, "CAI10", "XOF");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        handler.sendEmptyMessage(0);
    }

    public void doSomething() {
        pDialog = ProgressDialog.show(thisObject, "", "Recherche du compte en cours ...");
        Thread t = new Thread(thisObject);
        OperationType = 4;
        t.start();
    }

}
