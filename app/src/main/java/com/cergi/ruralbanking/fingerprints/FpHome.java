package com.cergi.ruralbanking.fingerprints;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cergi.ruralbanking.EmptyListener;
import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.entities.Empreinte;
import com.cergi.ruralbanking.fingerprints.logic.VersionAPI;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnGenCharListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnGetImageListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnRegModelListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnUpCharListener;
import com.cergi.ruralbanking.fingerprints.utils.AsyncFingerprint.OnUpImageListener;

/**
 * @author Dave
 */
@SuppressLint({"DefaultLocale", "HandlerLeak"})
public class FpHome extends Fragment implements View.OnClickListener {

    private MyApplication application;

    private AsyncFingerprint registerFingerprintPo, registerFingerprinteIn,
            registerFingerprintMa, registerFingerprintAn,
            registerFingerprintAu;
    private AsyncFingerprint registerFingerprintDroitePo,
            registerFingerprintDroiteIn, registerFingerprintDroiteMa,
            registerFingerprintDroiteAn, registerFingerprintDroiteAu;

    private ImageButton fingerprintImgGauchePo, fingerprintImgGaucheIn,
            fingerprintImgGaucheMa, fingerprintImgGaucheAn,
            fingerprintImgGaucheAu;

    private ImageButton fingerprintImgDroitePo, fingerprintImgDroiteIn,
            fingerprintImgDroiteMa, fingerprintImgDroiteAn,
            fingerprintImgDroiteAu;

    private Button sendButton;

    private ProgressDialog progressDialog;

    @SuppressWarnings("unused")
    private byte[] model;

    private int countGaucheAu, countGaucheAn, countGaucheIn, countGaucheMa, countGauchePo;
    private int countDroitePo, countDroiteIn, countDroiteMa, countDroiteAn, countDroiteAu;

    private Bitmap bitmapGauchePo, bitmapGaucheIn, bitmapGaucheMa,
            bitmapGaucheAn, bitmapGaucheAu;
    private Bitmap bitmapDroitePo, bitmapDroiteIn, bitmapDroiteMa,
            bitmapDroiteAn, bitmapDroiteAu;
    private Bitmap bitmapImage;

    TextView myLabel, FName, LName, Matricule;
    // private Handler handler;
    // EditText myTextbox;

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
    private static final int REQUEST_ENABLE_BT = 0;

    /***
     * PHOTOS variables
     ***/
    // Activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;

    // directory name to store captured images
    private static final String IMAGE_DIRECTORY_NAME = "Clients";

    private Uri fileUri; // file url to store image

    private ImageButton imgPreview;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private ScrollView ll;
    private FragmentActivity fa;

    ProgressDialog pDialog = null;

    int OperationType = 0;

    Context c = null;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        fa = super.getActivity();
        ll = (ScrollView) inflater.inflate(R.layout.home, container, false);
        application = (MyApplication) fa.getApplicationContext();
        c = container.getContext();
        try {
            // getting Name / Surname of New Account
            View idFrag = Safe.ViewIdentite;
            //Matricule
            Matricule = (TextView) ll.findViewById(R.id.Matricule);
            Matricule.setText("MATRICULE : " + Safe.Matricule);
            // Last Name
            String nomclient = ((EditText) idFrag.findViewById(R.id.txtNom)).getText().toString();
            LName = (TextView) ll.findViewById(R.id.LName);
            LName.setText("NOM : " + nomclient.toUpperCase());
            // First Name
            String prenomclient = ((EditText) idFrag.findViewById(R.id.txtPrenoms)).getText().toString();
            FName = (TextView) ll.findViewById(R.id.FName);
            FName.setText("PRENOM(S) : " + prenomclient.toUpperCase());

            // ImageButton for PHOTO
            imgPreview = (ImageButton) ll.findViewById(R.id.imgPreview);
            final boolean hasDrawable = (imgPreview.getDrawable() != null);

            sendButton = (Button) ll.findViewById(R.id.send);
            // Safe.NomReduit
            myLabel = (TextView) ll.findViewById(R.id.label);
            // myTextbox = (EditText) findViewById(R.id.entry);
            initView();
            initViewListener();
            initData();

            // send data to database
            sendButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//							pDialog = ProgressDialog.show(fa, "","Veuillez patienter...");
                    new Thread(new Runnable() {

                        @SuppressLint("HandlerLeak")
                        private Handler handler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
//										pDialog.dismiss();
                                if (Safe.SavePhotoResult.Success == true
                                        && Safe.FingerprintsSaveResult.Success == true
                                        && Safe.FingerprintsHistoResult.Success == true)
                                    sendButton.setEnabled(false);
                                Toast.makeText(fa, "Photo et empreintes enregistrées", Toast.LENGTH_LONG).show();
                            }
                        };

                        @Override
                        public void run() {
                            try {
//									sendButton.setEnabled(false);

                                List<Empreinte> list = new LinkedList<Empreinte>();

                                list.add(new Empreinte(Empreinte.pouce, Empreinte.mainGauche,
                                        getByte(bitmapGauchePo)));
                                list.add(new Empreinte(Empreinte.index, Empreinte.mainGauche,
                                        getByte(bitmapGaucheIn)));
                                list.add(new Empreinte(Empreinte.majeur, Empreinte.mainGauche,
                                        getByte(bitmapGaucheMa)));
                                list.add(new Empreinte(Empreinte.annulaire, Empreinte.mainGauche,
                                        getByte(bitmapGaucheAn)));
                                list.add(new Empreinte(Empreinte.auriculaire, Empreinte.mainGauche,
                                        getByte(bitmapGaucheAu)));

                                list.add(new Empreinte(Empreinte.pouce, Empreinte.mainDroite,
                                        getByte(bitmapDroitePo)));
                                list.add(new Empreinte(Empreinte.index, Empreinte.mainDroite,
                                        getByte(bitmapDroiteIn)));
                                list.add(new Empreinte(Empreinte.majeur, Empreinte.mainDroite,
                                        getByte(bitmapDroiteMa)));
                                list.add(new Empreinte(Empreinte.annulaire, Empreinte.mainDroite,
                                        getByte(bitmapDroiteAn)));
                                list.add(new Empreinte(Empreinte.auriculaire, Empreinte.mainDroite,
                                        getByte(bitmapDroiteAu)));

                                Safe.ListEmpreinte = list;

//									Safe.Photo = baos.toByteArray();

                                //check if fingerprints exist in database; If yes then insert into HISTO table and delete from fingerprints table
                                Safe.FingerprintsHistoResult = WsCommunicator.HistoEmpreintes(fa.getApplicationContext(), Safe.Matricule);

                                //save each fingerprints to database
                                for (Empreinte emp : Safe.ListEmpreinte) {
                                    Safe.FingerprintsSaveResult = WsCommunicator.EnregistrerEmpreintes(fa.getApplicationContext(), Safe.Matricule, emp);
                                }

                                //save photo to database
                                Safe.SavePhotoResult = WsCommunicator.EnregistrerPhoto(fa.getApplicationContext(), Safe.Matricule, Safe.Photo);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }).start();
                }
            });
            byte[] bb = new byte[]{0, 0, 0, 0};
            if (Arrays.equals(Safe.Photo, bb)) {
                Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.default_img);
                imgPreview.setImageBitmap(bm);
            } else {
                //affichage de la photo
                final Bitmap b = getBitMap(Safe.Photo);
                Matrix m = new Matrix();
                m.postRotate(-90, b.getWidth() / 2, b.getHeight() / 2);
                Bitmap bmp = Bitmap.createBitmap(b, 0, 0, 125, b.getHeight(), m, true);
                imgPreview.setImageBitmap(bmp);
            }

            // Capture image button click event
            imgPreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (hasDrawable) {
                        // imageView has image in it
                        imgPreview.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.default_img));
                        // capture picture
                        captureImage();
                    } else {
                        // capture picture
                        captureImage();
                    }
                }
            });
        } catch (Exception e) {
            Toast.makeText(fa, e.getMessage(), Toast.LENGTH_LONG).show();
            e.getMessage();
            e.printStackTrace();
        }
        return ll;
    }


    /*
     * Capturing Camera Image will lauch camera app requrest image capture
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == -1) {
                // successfully captured the image
                // display it in image view
                previewCapturedImage();
            } else if (resultCode == 0) {
                // user cancelled Image capture
                Toast.makeText(fa.getApplicationContext(),
                        "L'utilisateur a annulé la capture d'image",
                        Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture image
                Toast.makeText(fa.getApplicationContext(),
                        "Echec de la capture d'image", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    /*
     * Display image from a path to ImageView
     */
    private void previewCapturedImage() {
        try {
            imgPreview.setVisibility(View.VISIBLE);

            // bitmap factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down-sizing image as it throws OutOfMemory Exception for larger images
            options.inSampleSize = 4;

            bitmapImage = BitmapFactory.decodeFile(fileUri.getPath(), options);

            //Compressing image to byte array
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, baos);

            //Resizing the image
            bitmapImage = Bitmap.createScaledBitmap(bitmapImage, 125, 125, true);

            //Rotating image
            Matrix mat = new Matrix();

            ExifInterface exif = new ExifInterface(fileUri.getPath());
            String orient = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
            int orientation = orient != null ? Integer.parseInt(orient) : ExifInterface.ORIENTATION_NORMAL;
            int rotateangle = 0;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90)
                rotateangle = 90;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180)
                rotateangle = 180;
            if (orientation == ExifInterface.ORIENTATION_ROTATE_270)
                rotateangle = 270;

            mat.setRotate(rotateangle, (float) bitmapImage.getWidth() / 2, (float) bitmapImage.getHeight() / 2);

//			File f = new File(fileUri.getPath());       
//			Bitmap bmpPic = BitmapFactory.decodeStream(new FileInputStream(f), null, null); 

            Bitmap bmp = Bitmap.createBitmap(bitmapImage, 0, 0, bitmapImage.getWidth(), bitmapImage.getHeight(), mat, true);
            Safe.Photo = getByte(bmp);
            //Displaying image
            imgPreview.setImageBitmap(bmp);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    /*
     * Here we restore the fileUri again
     */
    public void onRestoreInstanceState(Bundle b) {
        super.onSaveInstanceState(b);

        // get the file url
        fileUri = b.getParcelable("file_uri");
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    // This will find a bluetooth printer device
    void findBT() {
        boolean trouver = false;
        try {

            if (mBluetoothAdapter == null) {
                myLabel.setText("No bluetooth adapter available");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // MP300 is the name of the bluetooth printer device
                    if (device.getName().equals("MP300")) {
                        mmDevice = device;
                        trouver = true;
                        break;
                    }
                }
            }
            if (trouver) {
                myLabel.setText("Bluetooth Device Found");
            } else {
                myLabel.setText("Bluetooth Device Not Found");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            myLabel.setText("Bluetooth Opened");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "US-ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                myLabel.setText(data);
                                            }
                                        });

                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData() throws IOException {
        try {

            // the text typed by the user
            // String msg = myTextbox.getText().toString();
            // if (msg.isEmpty()) {
            // myLabel.setText("Veuillez écrire votre nom et prénoms");
            // return;
            // }
            // msg += "\n";
            //
            // mmOutputStream.write(msg.getBytes());
            // Intent i = new Intent(Intent.ACTION_VIEW);
            // i.setPackage("com.dynamixsoftware.printershare");
            // i.set;
            // startActivity(i);
            // stopWorker = true;
            // mmOutputStream.close();
            // mmInputStream.close();
            // mmSocket.close();

            // tell the user data were sent
            myLabel.setText("Data Sent");
            // closeBT();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth Closed");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        fa.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private boolean mIsConnected = false;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        if (mIsConnected) {
            menu.getItem(2).setEnabled(false);
            menu.getItem(3).setEnabled(true);
        } else {
            menu.getItem(2).setEnabled(true);
            menu.getItem(3).setEnabled(false);
        }
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//		case R.id.item1:
//			activeBluetooth();
//			break;
            case R.id.item2:
                desactiverBluetooth();
                return true;
        }
        return false;
    }

    public void activeBluetooth() {
        if (mBluetoothAdapter == null) {
            Context context = fa.getApplicationContext();
            CharSequence text = "Aucun dispositif bluetooth";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
            toast.show();
            mIsConnected = false;
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                mIsConnected = true;
            }
        }
    }

    public void desactiverBluetooth() {
        mBluetoothAdapter.disable();
        // out.append("TURN_OFF BLUETOOTH");
        Context context = fa.getApplicationContext();
        CharSequence text = "Déconnexion";
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
        mIsConnected = false;
    }

    private void initView() {
        fingerprintImgGauchePo = (ImageButton) ll.findViewById(R.id.gauche_po);
        fingerprintImgGaucheIn = (ImageButton) ll.findViewById(R.id.gauche_in);
        fingerprintImgGaucheMa = (ImageButton) ll.findViewById(R.id.gauche_ma);
        fingerprintImgGaucheAn = (ImageButton) ll.findViewById(R.id.gauche_an);
        fingerprintImgGaucheAu = (ImageButton) ll.findViewById(R.id.gauche_au);

        fingerprintImgDroitePo = (ImageButton) ll.findViewById(R.id.droite_po);
        fingerprintImgDroiteIn = (ImageButton) ll.findViewById(R.id.droite_in);
        fingerprintImgDroiteMa = (ImageButton) ll.findViewById(R.id.droite_ma);
        fingerprintImgDroiteAn = (ImageButton) ll.findViewById(R.id.droite_an);
        fingerprintImgDroiteAu = (ImageButton) ll.findViewById(R.id.droite_au);
    }

    private void initData() {
        try {
            // application = (MyApplication) this.getApplicationContext();
            registerFingerprintAn = new AsyncFingerprint(application
                    .getHandlerThread().getLooper(),
                    application.getChatService());

            registerFingerprintAn
                    .setOnGetImageListener(new OnGetImageListener() {
                        @Override
                        public void onGetImageSuccess() {
                            cancleProgressDialog();
                            registerFingerprintAn.PS_UpImage();
                            showProgressDialog(R.string.processing);
                        }

                        @Override
                        public void onGetImageFail() {
                            registerFingerprintAn.PS_GetImage();
                        }
                    });

            registerFingerprintAn.setOnUpImageListener(new OnUpImageListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onUpImageSuccess(byte[] data) {
                    Bitmap image = BitmapFactory.decodeByteArray(data, 0,
                            data.length);
                    if (bitmapGaucheAn != null && !bitmapGaucheAn.isRecycled()) {
                        bitmapGaucheAn.recycle();
                    }
                    bitmapGaucheAn = image;
                    fingerprintImgGaucheAn
                            .setBackgroundDrawable(new BitmapDrawable(image));
                    registerFingerprintAn.PS_GenChar(countGaucheAn);
                }

                @Override
                public void onUpImageFail() {
                    Log.i("whw", "up image fail");
                    cancleProgressDialog();
                }
            });

            registerFingerprintAn.setOnGenCharListener(new OnGenCharListener() {
                @Override
                public void onGenCharSuccess(int bufferId) {
                    if (bufferId == 1) {
                        cancleProgressDialog();
                        showProgressDialog(R.string.print_finger_again);
                        registerFingerprintAn.PS_GetImage();
                        countGaucheAn++;
                    } else if (bufferId == 2) {
                        registerFingerprintAn.PS_RegModel();
                    }
                }

                @Override
                public void onGenCharFail() {
                    cancleProgressDialog();
//					Toast.makeText(fa, R.string.generate_char_fail, Toast.LENGTH_LONG).show();
                }
            });

            registerFingerprintAn
                    .setOnRegModelListener(new OnRegModelListener() {

                        @Override
                        public void onRegModelSuccess() {
                            cancleProgressDialog();
                            registerFingerprintAn.PS_UpChar();
//							Toast.makeText(fa,	R.string.reg_model_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRegModelFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa,	R.string.reg_model_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintAn.setOnUpCharListener(new OnUpCharListener() {

                @Override
                public void onUpCharSuccess(byte[] model) {
                    cancleProgressDialog();
                    Log.i("whw", "#################model.length="
                            + model.length);
                    FpHome.this.model = model;
//					Toast.makeText(fa, R.string.register_success, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onUpCharFail() {
                    cancleProgressDialog();
//					Toast.makeText(fa, R.string.register_fail, Toast.LENGTH_LONG).show();
                }
            });

            registerFingerprintAu = new AsyncFingerprint(application
                    .getHandlerThread().getLooper(),
                    application.getChatService());

            registerFingerprintAu
                    .setOnGetImageListener(new OnGetImageListener() {
                        @Override
                        public void onGetImageSuccess() {
                            cancleProgressDialog();
                            registerFingerprintAu.PS_UpImage();
                            showProgressDialog(R.string.processing);

                        }

                        @Override
                        public void onGetImageFail() {
                            registerFingerprintAu.PS_GetImage();
                        }
                    });

            registerFingerprintAu.setOnUpImageListener(new OnUpImageListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onUpImageSuccess(byte[] data) {
                    Bitmap image = BitmapFactory.decodeByteArray(data, 0,
                            data.length);
                    if (bitmapGaucheAu != null && !bitmapGaucheAn.isRecycled()) {
                        bitmapGaucheAu.recycle();
                    }
                    bitmapGaucheAu = image;
                    fingerprintImgGaucheAu
                            .setBackgroundDrawable(new BitmapDrawable(image));
                    registerFingerprintAu.PS_GenChar(countGaucheAu);
                }

                @Override
                public void onUpImageFail() {
                    Log.i("whw", "up image fail");
                    cancleProgressDialog();
                }
            });

            registerFingerprintAu.setOnGenCharListener(new OnGenCharListener() {
                @Override
                public void onGenCharSuccess(int bufferId) {
                    if (bufferId == 1) {
                        cancleProgressDialog();
                        showProgressDialog(R.string.print_finger_again);
                        registerFingerprintAu.PS_GetImage();
                        countGaucheAu++;
                    } else if (bufferId == 2) {
                        registerFingerprintAu.PS_RegModel();
                    }
                }

                @Override
                public void onGenCharFail() {
                    cancleProgressDialog();
//					Toast.makeText(fa, R.string.generate_char_fail, Toast.LENGTH_LONG).show();
                }
            });

            registerFingerprintAu
                    .setOnRegModelListener(new OnRegModelListener() {

                        @Override
                        public void onRegModelSuccess() {
                            cancleProgressDialog();
                            registerFingerprintAu.PS_UpChar();
//							Toast.makeText(fa, R.string.reg_model_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRegModelFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.reg_model_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintAu.setOnUpCharListener(new OnUpCharListener() {

                @Override
                public void onUpCharSuccess(byte[] model) {
                    cancleProgressDialog();
                    Log.i("whw", "#################model.length="
                            + model.length);
                    FpHome.this.model = model;
//					Toast.makeText(fa, R.string.register_success, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onUpCharFail() {
                    cancleProgressDialog();
//					Toast.makeText(fa, R.string.register_fail, Toast.LENGTH_LONG).show();
                }
            });

            registerFingerprintMa = new AsyncFingerprint(application
                    .getHandlerThread().getLooper(),
                    application.getChatService());

            registerFingerprintMa
                    .setOnGetImageListener(new OnGetImageListener() {
                        @Override
                        public void onGetImageSuccess() {
                            cancleProgressDialog();
                            registerFingerprintMa.PS_UpImage();
                            showProgressDialog(R.string.processing);

                        }

                        @Override
                        public void onGetImageFail() {
                            registerFingerprintMa.PS_GetImage();
                        }
                    });

            registerFingerprintMa.setOnUpImageListener(new OnUpImageListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onUpImageSuccess(byte[] data) {
                    Bitmap image = BitmapFactory.decodeByteArray(data, 0,
                            data.length);
                    if (bitmapGaucheMa != null && !bitmapGaucheMa.isRecycled()) {
                        bitmapGaucheMa.recycle();
                    }
                    bitmapGaucheMa = image;
                    fingerprintImgGaucheMa
                            .setBackgroundDrawable(new BitmapDrawable(image));
                    registerFingerprintMa.PS_GenChar(countGaucheMa);
                }

                @Override
                public void onUpImageFail() {
                    Log.i("whw", "up image fail");
                    cancleProgressDialog();
                }
            });

            registerFingerprintMa.setOnGenCharListener(new OnGenCharListener() {
                @Override
                public void onGenCharSuccess(int bufferId) {
                    if (bufferId == 1) {
                        cancleProgressDialog();
                        showProgressDialog(R.string.print_finger_again);
                        registerFingerprintMa.PS_GetImage();
                        countGaucheMa++;
                    } else if (bufferId == 2) {
                        registerFingerprintMa.PS_RegModel();
                    }
                }

                @Override
                public void onGenCharFail() {
                    cancleProgressDialog();
//					Toast.makeText(fa, R.string.generate_char_fail, Toast.LENGTH_LONG).show();
                }
            });

            registerFingerprintMa
                    .setOnRegModelListener(new OnRegModelListener() {

                        @Override
                        public void onRegModelSuccess() {
                            cancleProgressDialog();
                            registerFingerprintMa.PS_UpChar();
//							Toast.makeText(fa, R.string.reg_model_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRegModelFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa,	R.string.reg_model_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintMa.setOnUpCharListener(new OnUpCharListener() {

                @Override
                public void onUpCharSuccess(byte[] model) {
                    cancleProgressDialog();
                    Log.i("whw", "#################model.length="
                            + model.length);
                    FpHome.this.model = model;
//					Toast.makeText(fa, R.string.register_success, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onUpCharFail() {
                    cancleProgressDialog();
//					Toast.makeText(fa, R.string.register_fail, Toast.LENGTH_LONG).show();
                }
            });

            registerFingerprintPo = new AsyncFingerprint(application
                    .getHandlerThread().getLooper(),
                    application.getChatService());

            registerFingerprintPo
                    .setOnGetImageListener(new OnGetImageListener() {
                        @Override
                        public void onGetImageSuccess() {
                            cancleProgressDialog();
                            registerFingerprintPo.PS_UpImage();
                            showProgressDialog(R.string.processing);

                        }

                        @Override
                        public void onGetImageFail() {
                            registerFingerprintPo.PS_GetImage();
                        }
                    });

            registerFingerprintPo.setOnUpImageListener(new OnUpImageListener() {
                @SuppressWarnings({"deprecation"})
                @Override
                public void onUpImageSuccess(byte[] data) {
                    Bitmap image = BitmapFactory.decodeByteArray(data, 0,
                            data.length);
                    if (bitmapGauchePo != null && !bitmapGauchePo.isRecycled()) {
                        bitmapGauchePo.recycle();
                    }
                    bitmapGauchePo = image;
                    fingerprintImgGauchePo
                            .setBackgroundDrawable(new BitmapDrawable(image));
                    registerFingerprintPo.PS_GenChar(countGauchePo);
                }

                @Override
                public void onUpImageFail() {
                    Log.i("whw", "up image fail");
                    cancleProgressDialog();
                }
            });

            registerFingerprintPo.setOnGenCharListener(new OnGenCharListener() {
                @Override
                public void onGenCharSuccess(int bufferId) {
                    if (bufferId == 1) {
                        cancleProgressDialog();
                        showProgressDialog(R.string.print_finger_again);
                        registerFingerprintPo.PS_GetImage();
                        countGauchePo++;
                    } else if (bufferId == 2) {
                        registerFingerprintPo.PS_RegModel();
                    }
                }

                @Override
                public void onGenCharFail() {
                    cancleProgressDialog();
//					Toast.makeText(fa, R.string.generate_char_fail, Toast.LENGTH_LONG).show();
                }
            });

            registerFingerprintPo
                    .setOnRegModelListener(new OnRegModelListener() {

                        @Override
                        public void onRegModelSuccess() {
                            cancleProgressDialog();
                            registerFingerprintPo.PS_UpChar();
//							Toast.makeText(fa,	R.string.reg_model_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRegModelFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa,	R.string.reg_model_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintPo.setOnUpCharListener(new OnUpCharListener() {

                @Override
                public void onUpCharSuccess(byte[] model) {
                    cancleProgressDialog();
                    Log.i("whw", "#################model.length="
                            + model.length);
                    FpHome.this.model = model;
//					Toast.makeText(fa, R.string.register_success, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onUpCharFail() {
                    cancleProgressDialog();
//					Toast.makeText(fa, R.string.register_fail, Toast.LENGTH_LONG).show();
                }
            });

            registerFingerprinteIn = new AsyncFingerprint(application
                    .getHandlerThread().getLooper(),
                    application.getChatService());

            registerFingerprinteIn
                    .setOnGetImageListener(new OnGetImageListener() {
                        @Override
                        public void onGetImageSuccess() {
                            cancleProgressDialog();
                            registerFingerprinteIn.PS_UpImage();
                            showProgressDialog(R.string.processing);

                        }

                        @Override
                        public void onGetImageFail() {
                            registerFingerprinteIn.PS_GetImage();
                        }
                    });

            registerFingerprinteIn
                    .setOnUpImageListener(new OnUpImageListener() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onUpImageSuccess(byte[] data) {
                            Bitmap image = BitmapFactory.decodeByteArray(data,
                                    0, data.length);
                            if (bitmapGaucheIn != null
                                    && !bitmapGaucheIn.isRecycled()) {
                                bitmapGaucheIn.recycle();
                            }
                            bitmapGaucheIn = image;
                            fingerprintImgGaucheIn
                                    .setBackgroundDrawable(new BitmapDrawable(
                                            image));
                            registerFingerprinteIn.PS_GenChar(countGaucheIn);
                        }

                        @Override
                        public void onUpImageFail() {
                            Log.i("whw", "up image fail");
                            cancleProgressDialog();
                        }
                    });

            registerFingerprinteIn
                    .setOnGenCharListener(new OnGenCharListener() {
                        @Override
                        public void onGenCharSuccess(int bufferId) {
                            if (bufferId == 1) {
                                cancleProgressDialog();
                                showProgressDialog(R.string.print_finger_again);
                                registerFingerprinteIn.PS_GetImage();
                                countGaucheIn++;
                            } else if (bufferId == 2) {
                                registerFingerprinteIn.PS_RegModel();
                            }
                        }

                        @Override
                        public void onGenCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa,	R.string.generate_char_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprinteIn
                    .setOnRegModelListener(new OnRegModelListener() {

                        @Override
                        public void onRegModelSuccess() {
                            cancleProgressDialog();
                            registerFingerprinteIn.PS_UpChar();
//							Toast.makeText(fa,	R.string.reg_model_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRegModelFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa,	R.string.reg_model_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprinteIn.setOnUpCharListener(new OnUpCharListener() {

                @Override
                public void onUpCharSuccess(byte[] model) {
                    cancleProgressDialog();
                    Log.i("whw", "#################model.length="
                            + model.length);
                    FpHome.this.model = model;
//					Toast.makeText(fa, R.string.register_success, Toast.LENGTH_LONG).show();

                }

                @Override
                public void onUpCharFail() {
                    cancleProgressDialog();
//					Toast.makeText(fa, R.string.register_fail, Toast.LENGTH_LONG).show();
                }
            });

            registerFingerprintDroiteAn = new AsyncFingerprint(application
                    .getHandlerThread().getLooper(),
                    application.getChatService());

            registerFingerprintDroiteAn
                    .setOnGetImageListener(new OnGetImageListener() {
                        @Override
                        public void onGetImageSuccess() {
                            cancleProgressDialog();
                            registerFingerprintDroiteAn.PS_UpImage();
                            showProgressDialog(R.string.processing);
                        }

                        @Override
                        public void onGetImageFail() {
                            registerFingerprintDroiteAn.PS_GetImage();
                        }
                    });

            registerFingerprintDroiteAn
                    .setOnUpImageListener(new OnUpImageListener() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onUpImageSuccess(byte[] data) {
                            Bitmap image = BitmapFactory.decodeByteArray(data,
                                    0, data.length);
                            if (bitmapDroiteAn != null
                                    && !bitmapDroiteAn.isRecycled()) {
                                bitmapDroiteAn.recycle();
                            }
                            bitmapDroiteAn = image;
                            fingerprintImgDroiteAn
                                    .setBackgroundDrawable(new BitmapDrawable(
                                            image));
                            registerFingerprintDroiteAn
                                    .PS_GenChar(countDroiteAn);
                        }

                        @Override
                        public void onUpImageFail() {
                            Log.i("whw", "up image fail");
                            cancleProgressDialog();
                        }
                    });

            registerFingerprintDroiteAn
                    .setOnGenCharListener(new OnGenCharListener() {
                        @Override
                        public void onGenCharSuccess(int bufferId) {
                            if (bufferId == 1) {
                                cancleProgressDialog();
                                showProgressDialog(R.string.print_finger_again);
                                registerFingerprintDroiteAn.PS_GetImage();
                                countDroiteAn++;
                            } else if (bufferId == 2) {
                                registerFingerprintDroiteAn.PS_RegModel();
                            }
                        }

                        @Override
                        public void onGenCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa,R.string.generate_char_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteAn
                    .setOnRegModelListener(new OnRegModelListener() {

                        @Override
                        public void onRegModelSuccess() {
                            cancleProgressDialog();
                            registerFingerprintDroiteAn.PS_UpChar();
//							Toast.makeText(fa,	R.string.reg_model_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRegModelFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.reg_model_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteAn
                    .setOnUpCharListener(new OnUpCharListener() {

                        @Override
                        public void onUpCharSuccess(byte[] model) {
                            cancleProgressDialog();
                            Log.i("whw", "#################model.length="
                                    + model.length);
                            FpHome.this.model = model;
//							Toast.makeText(fa, R.string.register_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onUpCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.register_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteAu = new AsyncFingerprint(application
                    .getHandlerThread().getLooper(),
                    application.getChatService());

            registerFingerprintDroiteAu
                    .setOnGetImageListener(new OnGetImageListener() {
                        @Override
                        public void onGetImageSuccess() {
                            cancleProgressDialog();
                            registerFingerprintDroiteAu.PS_UpImage();
                            showProgressDialog(R.string.processing);

                        }

                        @Override
                        public void onGetImageFail() {
                            registerFingerprintDroiteAu.PS_GetImage();
                        }
                    });

            registerFingerprintDroiteAu
                    .setOnUpImageListener(new OnUpImageListener() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onUpImageSuccess(byte[] data) {
                            Bitmap image = BitmapFactory.decodeByteArray(data,
                                    0, data.length);
                            if (bitmapDroiteAu != null
                                    && !bitmapDroiteAn.isRecycled()) {
                                bitmapDroiteAu.recycle();
                            }
                            bitmapDroiteAu = image;
                            fingerprintImgDroiteAu
                                    .setBackgroundDrawable(new BitmapDrawable(
                                            image));
                            registerFingerprintDroiteAu
                                    .PS_GenChar(countDroiteAu);
                        }

                        @Override
                        public void onUpImageFail() {
                            Log.i("whw", "up image fail");
                            cancleProgressDialog();
                        }
                    });

            registerFingerprintDroiteAu
                    .setOnGenCharListener(new OnGenCharListener() {
                        @Override
                        public void onGenCharSuccess(int bufferId) {
                            if (bufferId == 1) {
                                cancleProgressDialog();
                                showProgressDialog(R.string.print_finger_again);
                                registerFingerprintDroiteAu.PS_GetImage();
                                countDroiteAu++;
                            } else if (bufferId == 2) {
                                registerFingerprintDroiteAu.PS_RegModel();
                            }
                        }

                        @Override
                        public void onGenCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.generate_char_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteAu
                    .setOnRegModelListener(new OnRegModelListener() {

                        @Override
                        public void onRegModelSuccess() {
                            cancleProgressDialog();
                            registerFingerprintDroiteAu.PS_UpChar();
//							Toast.makeText(fa, R.string.reg_model_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRegModelFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.reg_model_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteAu
                    .setOnUpCharListener(new OnUpCharListener() {

                        @Override
                        public void onUpCharSuccess(byte[] model) {
                            cancleProgressDialog();
                            Log.i("whw", "#################model.length="
                                    + model.length);
                            FpHome.this.model = model;
//							Toast.makeText(fa, R.string.register_success, Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onUpCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.register_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteMa = new AsyncFingerprint(application
                    .getHandlerThread().getLooper(),
                    application.getChatService());

            registerFingerprintDroiteMa
                    .setOnGetImageListener(new OnGetImageListener() {
                        @Override
                        public void onGetImageSuccess() {
                            cancleProgressDialog();
                            registerFingerprintDroiteMa.PS_UpImage();
                            showProgressDialog(R.string.processing);

                        }

                        @Override
                        public void onGetImageFail() {
                            registerFingerprintDroiteMa.PS_GetImage();
                        }
                    });

            registerFingerprintDroiteMa
                    .setOnUpImageListener(new OnUpImageListener() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onUpImageSuccess(byte[] data) {
                            Bitmap image = BitmapFactory.decodeByteArray(data,
                                    0, data.length);
                            if (bitmapDroiteMa != null
                                    && !bitmapDroiteMa.isRecycled()) {
                                bitmapDroiteMa.recycle();
                            }
                            bitmapDroiteMa = image;
                            fingerprintImgDroiteMa
                                    .setBackgroundDrawable(new BitmapDrawable(
                                            image));
                            registerFingerprintDroiteMa
                                    .PS_GenChar(countDroiteMa);
                        }

                        @Override
                        public void onUpImageFail() {
                            Log.i("whw", "up image fail");
                            cancleProgressDialog();
                        }
                    });

            registerFingerprintDroiteMa
                    .setOnGenCharListener(new OnGenCharListener() {
                        @Override
                        public void onGenCharSuccess(int bufferId) {
                            if (bufferId == 1) {
                                cancleProgressDialog();
                                showProgressDialog(R.string.print_finger_again);
                                registerFingerprintDroiteMa.PS_GetImage();
                                countDroiteMa++;
                            } else if (bufferId == 2) {
                                registerFingerprintDroiteMa.PS_RegModel();
                            }
                        }

                        @Override
                        public void onGenCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.generate_char_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteMa
                    .setOnRegModelListener(new OnRegModelListener() {

                        @Override
                        public void onRegModelSuccess() {
                            cancleProgressDialog();
                            registerFingerprintDroiteMa.PS_UpChar();
//							Toast.makeText(fa, R.string.reg_model_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRegModelFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.reg_model_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteMa
                    .setOnUpCharListener(new OnUpCharListener() {

                        @Override
                        public void onUpCharSuccess(byte[] model) {
                            cancleProgressDialog();
                            Log.i("whw", "#################model.length="
                                    + model.length);
                            FpHome.this.model = model;
//							Toast.makeText(fa, R.string.register_success, Toast.LENGTH_LONG).show();

                        }

                        @Override
                        public void onUpCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.register_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroitePo = new AsyncFingerprint(application
                    .getHandlerThread().getLooper(),
                    application.getChatService());

            registerFingerprintDroitePo
                    .setOnGetImageListener(new OnGetImageListener() {
                        @Override
                        public void onGetImageSuccess() {
                            cancleProgressDialog();
                            registerFingerprintDroitePo.PS_UpImage();
                            showProgressDialog(R.string.processing);

                        }

                        @Override
                        public void onGetImageFail() {
                            registerFingerprintDroitePo.PS_GetImage();
                        }
                    });

            registerFingerprintDroitePo
                    .setOnUpImageListener(new OnUpImageListener() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onUpImageSuccess(byte[] data) {
                            Bitmap image = BitmapFactory.decodeByteArray(data,
                                    0, data.length);
                            if (bitmapDroitePo != null
                                    && !bitmapDroitePo.isRecycled()) {
                                bitmapDroitePo.recycle();
                            }
                            bitmapDroitePo = image;
                            fingerprintImgDroitePo
                                    .setBackgroundDrawable(new BitmapDrawable(
                                            image));
                            registerFingerprintDroitePo
                                    .PS_GenChar(countDroitePo);
                        }

                        @Override
                        public void onUpImageFail() {
                            Log.i("whw", "up image fail");
                            cancleProgressDialog();
                        }
                    });

            registerFingerprintDroitePo
                    .setOnGenCharListener(new OnGenCharListener() {
                        @Override
                        public void onGenCharSuccess(int bufferId) {
                            if (bufferId == 1) {
                                cancleProgressDialog();
                                showProgressDialog(R.string.print_finger_again);
                                registerFingerprintDroitePo.PS_GetImage();
                                countDroitePo++;
                            } else if (bufferId == 2) {
                                registerFingerprintDroitePo.PS_RegModel();
                            }
                        }

                        @Override
                        public void onGenCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.generate_char_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroitePo
                    .setOnRegModelListener(new OnRegModelListener() {

                        @Override
                        public void onRegModelSuccess() {
                            cancleProgressDialog();
                            registerFingerprintDroitePo.PS_UpChar();
//							Toast.makeText(fa, R.string.reg_model_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRegModelFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.reg_model_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroitePo
                    .setOnUpCharListener(new OnUpCharListener() {

                        @Override
                        public void onUpCharSuccess(byte[] model) {
                            cancleProgressDialog();
                            Log.i("whw", "#################model.length="
                                    + model.length);
                            FpHome.this.model = model;
//							Toast.makeText(fa, R.string.register_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onUpCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.register_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteIn = new AsyncFingerprint(application
                    .getHandlerThread().getLooper(),
                    application.getChatService());

            registerFingerprintDroiteIn
                    .setOnGetImageListener(new OnGetImageListener() {
                        @Override
                        public void onGetImageSuccess() {
                            cancleProgressDialog();
                            registerFingerprintDroiteIn.PS_UpImage();
                            showProgressDialog(R.string.processing);

                        }

                        @Override
                        public void onGetImageFail() {
                            registerFingerprintDroiteIn.PS_GetImage();
                        }
                    });

            registerFingerprintDroiteIn
                    .setOnUpImageListener(new OnUpImageListener() {
                        @SuppressWarnings("deprecation")
                        @Override
                        public void onUpImageSuccess(byte[] data) {
                            Bitmap image = BitmapFactory.decodeByteArray(data,
                                    0, data.length);
                            if (bitmapDroiteIn != null
                                    && !bitmapDroiteIn.isRecycled()) {
                                bitmapDroiteIn.recycle();
                            }
                            bitmapDroiteIn = image;
                            fingerprintImgDroiteIn
                                    .setBackgroundDrawable(new BitmapDrawable(
                                            image));
                            registerFingerprintDroiteIn
                                    .PS_GenChar(countDroiteIn);
                        }

                        @Override
                        public void onUpImageFail() {
                            Log.i("whw", "up image fail");
                            cancleProgressDialog();
                        }
                    });

            registerFingerprintDroiteIn
                    .setOnGenCharListener(new OnGenCharListener() {
                        @Override
                        public void onGenCharSuccess(int bufferId) {
                            if (bufferId == 1) {
                                cancleProgressDialog();
                                showProgressDialog(R.string.print_finger_again);
                                registerFingerprintDroiteIn.PS_GetImage();
                                countDroiteIn++;
                            } else if (bufferId == 2) {
                                registerFingerprintDroiteIn.PS_RegModel();
                            }
                        }

                        @Override
                        public void onGenCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.generate_char_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteIn
                    .setOnRegModelListener(new OnRegModelListener() {

                        @Override
                        public void onRegModelSuccess() {
                            cancleProgressDialog();
                            registerFingerprintDroiteIn.PS_UpChar();
//							Toast.makeText(fa, R.string.reg_model_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onRegModelFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.reg_model_fail, Toast.LENGTH_LONG).show();
                        }
                    });

            registerFingerprintDroiteIn
                    .setOnUpCharListener(new OnUpCharListener() {

                        @Override
                        public void onUpCharSuccess(byte[] model) {
                            cancleProgressDialog();
                            Log.i("whw", "#################model.length="
                                    + model.length);
                            FpHome.this.model = model;
//							Toast.makeText(fa, R.string.register_success, Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onUpCharFail() {
                            cancleProgressDialog();
//							Toast.makeText(fa, R.string.register_fail, Toast.LENGTH_LONG).show();
                        }
                    });

        } catch (Exception ex) {
            ex.getMessage();
            ex.printStackTrace();
        }
    }

    private void initViewListener() {
        fingerprintImgDroiteAn.setOnClickListener(this);
        fingerprintImgDroiteAu.setOnClickListener(this);
        fingerprintImgDroiteIn.setOnClickListener(this);
        fingerprintImgDroiteMa.setOnClickListener(this);
        fingerprintImgDroitePo.setOnClickListener(this);

        fingerprintImgGaucheAn.setOnClickListener(this);
        fingerprintImgGaucheAu.setOnClickListener(this);
        fingerprintImgGaucheIn.setOnClickListener(this);
        fingerprintImgGaucheMa.setOnClickListener(this);
        fingerprintImgGauchePo.setOnClickListener(this);

//		registerGauche.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gauche_an:
                try {
                    if (!application.isConnect()) {
                        startActivity(new Intent(fa, ConnectActivity.class));
                        return;
                    }
                    countGaucheAn = 1;
                    showProgressDialog(R.string.print_finger_gauche);
                    registerFingerprintAn.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
                } catch (Exception ex) {
                    Toast.makeText(fa,
                            ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.gauche_au:
                try {
                    if (!application.isConnect()) {
                        startActivity(new Intent(fa, ConnectActivity.class));
                        return;
                    }
                    countGaucheAu = 1;
                    showProgressDialog(R.string.print_finger_gauche);
                    registerFingerprintAu.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
                } catch (Exception ex) {
                    Toast.makeText(fa,
                            ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.gauche_in:
                try {
                    if (!application.isConnect()) {
                        startActivity(new Intent(fa, ConnectActivity.class));
                        return;
                    }
                    countGaucheIn = 1;
                    showProgressDialog(R.string.print_finger_gauche);
                    registerFingerprinteIn.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
                } catch (Exception ex) {
                    Toast.makeText(fa,
                            ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.gauche_ma:
                try {
                    if (!application.isConnect()) {
                        startActivity(new Intent(fa, ConnectActivity.class));
                        return;
                    }
                    countGaucheMa = 1;
                    showProgressDialog(R.string.print_finger_gauche);
                    registerFingerprintMa.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
                } catch (Exception ex) {
                    Toast.makeText(fa,
                            ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.gauche_po:
                try {
                    if (!application.isConnect()) {
                        startActivity(new Intent(fa, ConnectActivity.class));
                        return;
                    }
                    countGauchePo = 1;
                    showProgressDialog(R.string.print_finger_gauche);
                    registerFingerprintPo.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
//				Alerts.showAlert("Info", ""+fingerprintImgGauchePo.getHeight()+"\n"+fingerprintImgGauchePo.getWidth(), Home.this); 

                } catch (Exception ex) {
                    Toast.makeText(fa,
                            ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.droite_an:
                try {
                    if (!application.isConnect()) {
                        startActivity(new Intent(fa, ConnectActivity.class));
                        return;
                    }
                    countDroiteAn = 1;
                    showProgressDialog(R.string.print_finger_droite);
                    registerFingerprintDroiteAn.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
                } catch (Exception ex) {
                    Toast.makeText(fa,
                            ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.droite_au:
                try {
                    if (!application.isConnect()) {
                        startActivity(new Intent(fa, ConnectActivity.class));
                        return;
                    }
                    countDroiteAu = 1;
                    showProgressDialog(R.string.print_finger_droite);
                    registerFingerprintDroiteAu.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
                } catch (Exception ex) {
                    Toast.makeText(fa,
                            ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.droite_in:
                try {
                    if (!application.isConnect()) {
                        startActivity(new Intent(fa, ConnectActivity.class));
                        return;
                    }
                    countDroiteIn = 1;
                    showProgressDialog(R.string.print_finger_droite);
                    registerFingerprintDroiteIn.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
                } catch (Exception ex) {
                    Toast.makeText(fa,
                            ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.droite_ma:
                try {
                    if (!application.isConnect()) {
                        startActivity(new Intent(fa, ConnectActivity.class));
                        return;
                    }
                    countDroiteMa = 1;
                    showProgressDialog(R.string.print_finger_droite);
                    registerFingerprintDroiteMa.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
                } catch (Exception ex) {
                    Toast.makeText(fa,
                            ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.droite_po:
                try {
                    if (!application.isConnect()) {
                        startActivity(new Intent(fa, ConnectActivity.class));
                        return;
                    }
                    countDroitePo = 1;
                    showProgressDialog(R.string.print_finger_droite);
                    registerFingerprintDroitePo.PS_GetImage();
                    model = null;
                    Log.i("whw", "send end");
                } catch (Exception ex) {
                    Toast.makeText(fa,
                            ex.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
    }

    @SuppressWarnings("unused")
    private void showProgressDialog(String message) {
        progressDialog = new ProgressDialog(fa);
        progressDialog.setMessage(message);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void showProgressDialog(int resId) {
        try {
            progressDialog = new ProgressDialog(fa);
            progressDialog.setMessage(getResources().getString(resId));
            progressDialog.show();
        } catch (Exception ex) {
            ex.getMessage();
            ex.printStackTrace();
        }
    }

    private void cancleProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
            progressDialog = null;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            fa.finish();
        }
        return fa.onKeyDown(keyCode, event);
    }

    @Override
    public void onStop() {
        cancleProgressDialog();
        super.onStop();
    }

    public Runnable task = new Runnable() {

        @Override
        public void run() {
            VersionAPI asyncVersion = new VersionAPI(application.getChatService());
            final int version = asyncVersion.getVersion();
            fa.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(fa, "version=" + version, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

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

    private Bitmap getBitMap(byte[] bitmapdata) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        return bitmap;
    }

    public void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(fa);
        builder.setTitle("Info");
        builder.setMessage(message);

        EmptyListener pl = new EmptyListener();
        builder.setPositiveButton("OK", pl);

        AlertDialog ad = builder.create();

        ad.show();
    }
}
