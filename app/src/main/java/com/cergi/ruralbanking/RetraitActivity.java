package com.cergi.ruralbanking;

import android.annotation.SuppressLint;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.entities.INFO_COMPTE;
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
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

//import javax.imageio.ImageIO;

/**
 * Created by Thierry on 03/03/14.
 */
@SuppressLint("HandlerLeak")
public class RetraitActivity extends AppCompatActivity implements Runnable {

    RetraitActivity thisObject;

    Toolbar toolbar;

    private AsyncFingerprint getFingerprint;

    private ImageButton srcFP;

    private ProgressDialog progressDialog;

    @SuppressWarnings("unused")
    private byte[] model;

    private int countFinger;

    private Bitmap bitmapFinger;

    /**
     * BLUETOOTH VARIABLES
     **/
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
    /** **/

    ProgressDialog pDialog = null;
    int OperationType;
    int Choice = 0;

    TextView txtRefOper = null;
    String ref_oper = "";
    String typiece = "";
    EditText txtNumCpte = null, txtNomReduit = null, txtNomBenef = null,
            txtAdresse = null, txtMontant = null, txtDateDeliv = null;
    CheckBox cbTitulaire = null;

    Button btnValid = null, btnCancel = null;
    //	ImageButton btnSearch = null;
    ImageView photo = null;
    Spinner type_piece = null;

    INFO_COMPTE infoCompte = null;
    byte[] emp;
    String matricule;

    AlertDialog.Builder ab = null;
    DialogInterface.OnClickListener dc, dc_yes = null;

    // nom de l'appareil
    String device = (Build.MODEL).replace("-", "");

    GridLayout grid = null;

    String fd;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Safe.CurrentUser.USER_ABREGE);
        menu.add(R.string.menu_disconnect);
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.getItem(1).setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Intent intent = new Intent(thisObject,
                                LoginActivity.class);
                        startActivity(intent);
                        thisObject.finish();
                        return false;
                    }
                });
        return true;
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        thisObject = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.retrait);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Home Button Enabled
        //getSupportActionBar().setHomeButtonEnabled(true);

        // Add subtitle
        getSupportActionBar().setSubtitle(R.string.retrait_esp);

        txtRefOper = (TextView) findViewById(R.id.txtRefOperRet);
        txtNumCpte = (EditText) findViewById(R.id.txtNumCpteRet);
        txtNomReduit = (EditText) findViewById(R.id.txtNomReduitRet);
        txtMontant = (EditText) findViewById(R.id.txtMontantRet);
        txtNomBenef = (EditText) findViewById(R.id.txtNomBenefic);
        txtAdresse = (EditText) findViewById(R.id.txtAdresseBen);
        txtDateDeliv = (EditText) findViewById(R.id.txtDateDeliv);

        cbTitulaire = (CheckBox) findViewById(R.id.cbTitulaire);

        // btnSearch = (ImageButton) findViewById(R.id.btnSrc);
        photo = (ImageView) findViewById(R.id.imgPreviewR);

        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnValid = (Button) findViewById(R.id.btnValidate);

        // liste des types de piece
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.type_piece, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type_piece = (Spinner) findViewById(R.id.spinnerTypePiece);
        type_piece.setAdapter(adapter);

        cbTitulaire.setEnabled(false);

        btnValid.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String libelle_cpte = txtNomReduit.getText().toString();
                String nom = txtNomBenef.getText().toString();
                String adress = txtAdresse.getText().toString();

                if (txtNumCpte.getText().toString().trim().isEmpty()) {
                    txtNumCpte.setError(getString(R.string.empty_field));
                } else if (libelle_cpte.trim().equals("")) {
                    txtNomReduit.setError(getString(R.string.empty_field));
                } else if (nom.trim().equals("")) {
                    txtNomBenef.setError(getString(R.string.empty_field));
                } else if (adress.trim().equals("")) {
                    txtAdresse.setError(getString(R.string.empty_field));
                } else if (txtMontant.getText().toString().trim().equals("0") || txtMontant.getText().toString().isEmpty()) {
                    txtMontant.setError(getString(R.string.empty_field));
                } else {
                    ab = new AlertDialog.Builder(thisObject);
                    ab.setTitle("Confirmer opération?");
                    ab.setMessage("Retrait de " + txtMontant.getText().toString() + "FCFA sur le compte : " + txtNumCpte.getText().toString() + " - " + txtNomReduit.getText().toString() + "\nVeuillez entrer votre code de sécurité pour confirmer :");
                    // Set an EditText view to get user input
                    final EditText input = new EditText(thisObject);
                    input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    ab.setView(input);
                    ab.setPositiveButton("Oui",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog,
                                        int which) {
                                    if (input.getText().toString().isEmpty())
                                        input.setError(getString(R.string.empty_field));
                                    else {
                                        pDialog = ProgressDialog
                                                .show(thisObject, "",
                                                        "Validation du retrait en cours ...");
                                        Thread t = new Thread(thisObject);
                                        OperationType = 2;
                                        t.start();
                                        dialog.dismiss();
                                    }
                                }
                            })

                            .setNegativeButton("Non",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
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
                cbTitulaire.setChecked(false);
                cbTitulaire.setEnabled(false);
                txtNomBenef.setText("");
                txtNomBenef.setEnabled(true);
                txtAdresse.setText("");
                txtAdresse.setEnabled(true);
                type_piece.setEnabled(true);
                txtDateDeliv.setText("");
                txtDateDeliv.setEnabled(true);
            }
        });

        txtNumCpte.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
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
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
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
        srcFP = (ImageButton) findViewById(R.id.finger);
    }

    private void initData() {
        try {
            getFingerprint = new AsyncFingerprint(Safe.application
                    .getHandlerThread().getLooper(),
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
                    Bitmap image = BitmapFactory.decodeByteArray(data, 0,
                            data.length);
                    if (bitmapFinger != null && !bitmapFinger.isRecycled()) {
                        bitmapFinger.recycle();
                        // doSOmething();
                    }
                    bitmapFinger = image;
                    // srcFP.setBackgroundDrawable(new BitmapDrawable(image));
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
                    doSOmething();
//					ToastUtil.showToast(thisObject, R.string.reg_model_success);
                }

                @Override
                public void onRegModelFail() {
                    cancelProgressDialog();
//					ToastUtil.showToast(thisObject, R.string.reg_model_fail);
                }
            });

            getFingerprint.setOnUpCharListener(new OnUpCharListener() {
                @Override
                public void onUpCharSuccess(byte[] model) {
                    cancelProgressDialog();
                    Log.i("whw", "#################model.length="
                            + model.length);
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

    public static double[] toDoubleArray(byte[] byteArray) {
        int times = Double.SIZE / Byte.SIZE;
        double[] doubles = new double[byteArray.length / times];
        for (int i = 0; i < doubles.length; i++) {
            doubles[i] = ByteBuffer.wrap(byteArray, i * times, times)
                    .getDouble();
        }
        return doubles;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (OperationType) {
                case 1:
                    pDialog.dismiss(); // ferme la boite de progression
                    if (infoCompte != null) {
                        if (infoCompte.MessageErreur.trim().compareTo("") == 0) {
                            Alerts.showAlert("Erreur", infoCompte.MessageErreur,
                                    thisObject);
                        } else {
                            txtNumCpte.setEnabled(false);
                            // r�cuperation des infos du compte
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

                            // reference de l'operation
                            txtRefOper.setText(ref_oper.replace("\\", ""));
                            // activation du checkbox (titulaire ou pas)
                            cbTitulaire.setEnabled(true);
                            cbTitulaire.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    if (cbTitulaire.isChecked()) {
                                        txtNomBenef.setText(infoCompte.ETCIV_NOMREDUIT);
                                        txtNomBenef.setEnabled(false);

                                        txtAdresse.setText(infoCompte.ETCIV_ADRESS_GEO1);
                                        txtAdresse.setEnabled(false);

                                        if (typiece == "AUT") {
                                            type_piece.setSelection(0);
                                        } else if (typiece == "CNI") {
                                            type_piece.setSelection(1);
                                        } else if (typiece == "PP") {
                                            type_piece.setSelection(2);
                                        }
                                        type_piece.setEnabled(false);

                                        txtDateDeliv.setText(infoCompte.PERSPHYS_PIECE_ID_DATE.substring(0, 10));
                                        txtDateDeliv.setEnabled(false);
                                    } else {
                                        txtNomBenef.setText("");
                                        txtNomBenef.setEnabled(true);

                                        txtAdresse.setText("");
                                        txtAdresse.setEnabled(true);

                                        type_piece.setEnabled(true);

                                        txtDateDeliv.setText("");
                                        txtDateDeliv.setEnabled(true);
                                    }
                                }
                            });
                        }
                    } else {
                        Alerts.showAlert("Erreur", "Numero de compte invalide.",
                                thisObject);
                    }
                    break;
                case 2:
                    pDialog.dismiss();
                    if (Safe.CashWithdrawResult != null) {
                        if (Safe.CashWithdrawResult.Success) {
                            String m = txtMontant.getText().toString();
//                                        for (int i = 0, count = grid.getChildCount(); i < count; ++i) {
//                                            View view = grid.getChildAt(i);
//                                            if (view instanceof EditText) {
//                                                ((EditText) view).getText().clear();
//                                            }
//                                        }

                            Toast.makeText(Safe.MasterObject, "Opération bien déroulée.\n Retrait de [" + m + "] effectué sur le Compte : [" + infoCompte.CPTEAUXIL_NUMCPT + " - " + infoCompte.ETCIV_NOMREDUIT + "]",
                                    Toast.LENGTH_LONG).show();
                            thisObject.finish();
                        } else {
                            Toast.makeText(Safe.MasterObject, "Erreur interne, veuillez réessayer plus tard", Toast.LENGTH_LONG).show();
                            thisObject.finish();
                        }
                    } else {
                        Toast.makeText(Safe.MasterObject, "Retrait non effectué, veuillez réessayer plus tard", Toast.LENGTH_LONG).show();
                        thisObject.finish();
                    }
                    break;
                case 4:
                    pDialog.dismiss(); //ferme la boite de progression
                    if (infoCompte != null) {
                        if (infoCompte.MessageErreur.trim().compareTo("") == 0) {
                            Alerts.showAlert("Erreur", infoCompte.MessageErreur,
                                    thisObject);
                        } else {
                            txtNumCpte.setEnabled(false); //desactiver champ NumCpte
                            txtRefOper.setText(ref_oper.replace("\\", "")); // r�f�rence de l'operation
                            // r�cuperation des infos du compte
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

                            // activation du checkbox (si titulaire ou pas)
                            cbTitulaire.setEnabled(true);
                            cbTitulaire.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View arg0) {
                                    if (cbTitulaire.isChecked()) {
                                        txtNomBenef.setText(infoCompte.ETCIV_NOMREDUIT);
                                        txtNomBenef.setEnabled(false);
                                        txtAdresse.setText(infoCompte.ETCIV_ADRESS_GEO1);
                                        txtAdresse.setEnabled(false);

                                        if (typiece.trim() == "AUT")
                                            type_piece.setSelection(0);
                                        if (typiece.trim() == "CNI")
                                            type_piece.setSelection(1);
                                        if (typiece.trim() == "PP")
                                            type_piece.setSelection(2);
                                        type_piece.setEnabled(false);

                                        txtDateDeliv.setText(infoCompte.PERSPHYS_PIECE_ID_DATE.substring(0, 10));
                                        txtDateDeliv.setEnabled(false);

                                    } else {
                                        txtNomBenef.setText("");
                                        txtNomBenef.setEnabled(true);

                                        txtAdresse.setText("");
                                        txtAdresse.setEnabled(true);

                                        type_piece.setEnabled(true);

                                        txtDateDeliv.setText("");
                                        txtDateDeliv.setEnabled(true);
                                    }
                                }
                            });
                        }
                    } else {
                        Alerts.showAlert("Erreur", "Empreinte incorrecte ou compte introuvable.",
                                thisObject);
                    }
                    break;
            }
        }
    };

    public static Bitmap getBitMap(byte[] bitmapdata) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        return bitmap;
    }

    public void run() {
        switch (OperationType) {
            case 1:
                try {
                    emp = new byte[]{0};
                    ref_oper = WsCommunicator.ReferenceOperation(thisObject, Safe.CurrentUser.USER_CODE);
                    infoCompte = WsCommunicator.InfoCompte(thisObject, txtNumCpte.getText().toString(), emp, Safe.CurrentUser.ETCIV_MATRICULE, "CAI10", "XOF");
                    typiece = infoCompte.NATURE_ID_CODE;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case 2:
                try {
                            Safe.CashWithdrawResult = WsCommunicator.ValiderOperation(thisObject, txtNumCpte.getText().toString(), Integer.parseInt(txtMontant.getText().toString()),
                                    txtNomBenef.getText().toString(), txtAdresse.getText().toString(), fd,
                                    device, txtRefOper.getText().toString(), "XOF", txtNomReduit.getText().toString(), 1, "CAISR",
                                    Safe.CurrentUser.USER_CODE, infoCompte.NATURE_ID_CODE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                try {
                    emp = getByte(bitmapFinger);
                    // String s = Base64.encodeToString(emp, Base64.DEFAULT);
                    ref_oper = WsCommunicator.ReferenceOperation(thisObject, Safe.CurrentUser.USER_CODE);
                    infoCompte = WsCommunicator.InfoCompte(thisObject, "", emp, Safe.CurrentUser.ETCIV_MATRICULE, "CAI10",
                            "XOF");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        handler.sendEmptyMessage(0);
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

    public void doSOmething() {
        pDialog = ProgressDialog.show(thisObject, "",
                "Recherche du compte en cours ...");
        Thread t = new Thread(thisObject);
        OperationType = 4;
        t.start();
    }
}
