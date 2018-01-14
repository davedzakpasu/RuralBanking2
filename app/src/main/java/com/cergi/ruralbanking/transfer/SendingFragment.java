/**
 *
 */
package com.cergi.ruralbanking.transfer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.entities.DEVISE;
import com.cergi.ruralbanking.entities.PAYS_NATIONAL;
import com.cergi.ruralbanking.fingerprints.utils.ToastUtil;

/**
 * Created by CERGI on  21 janv. 2015
 */
public class SendingFragment extends Fragment implements View.OnClickListener {

    Context thisObject;

    Spinner spinOper, spinPays, spinDev;

    EditText etNomDest, etPrenomDest, etMontantDest, etVilleDest,
            etNomExp, etPrenomExp, etAdressExp, etTelExp, etBPExp, etProfessExp,
            etMotifEnvoi, etQuestion, etReponse;

    Button btnValideEnvoi, btnCancelEnvoi;

    AlertDialog.Builder ab;

    ProgressDialog pDialog;
    int OperationType;

    public SendingFragment() {

    }

    public static SendingFragment newInstance() {
        return new SendingFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rv = inflater.inflate(R.layout.fragment_sending, container, false);
        thisObject = getActivity().getApplicationContext();
        spinOper = (Spinner) rv.findViewById(R.id.spinnerOpS);
        spinPays = (Spinner) rv.findViewById(R.id.spinnerPaysDest);
        spinDev = (Spinner) rv.findViewById(R.id.spinnerDeviseMtS);

        etNomDest = (EditText) rv.findViewById(R.id.txtNomDest);
        etPrenomDest = (EditText) rv.findViewById(R.id.txtPrenomDest);
        etMontantDest = (EditText) rv.findViewById(R.id.txtMontantDest);
        etVilleDest = (EditText) rv.findViewById(R.id.txtVilleDest);

        etNomExp = (EditText) rv.findViewById(R.id.txtNomExp);
        etPrenomExp = (EditText) rv.findViewById(R.id.txtPrenomExp);
        etAdressExp = (EditText) rv.findViewById(R.id.txtAdressExp);
        etTelExp = (EditText) rv.findViewById(R.id.txtTelExp);
        etBPExp = (EditText) rv.findViewById(R.id.txtBPExp);
        etProfessExp = (EditText) rv.findViewById(R.id.txtProfessExp);

        etMotifEnvoi = (EditText) rv.findViewById(R.id.txtMotifEnvoi);
        etQuestion = (EditText) rv.findViewById(R.id.txtQuestExp);
        etReponse = (EditText) rv.findViewById(R.id.txtRespExp);

        btnValideEnvoi = (Button) rv.findViewById(R.id.btnValiderEnvoi);
        btnCancelEnvoi = (Button) rv.findViewById(R.id.btnAnnulerEnvoi);

        btnValideEnvoi.setOnClickListener(this);

        btnCancelEnvoi.setOnClickListener(this);

        // liste des opérateurs
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.type_operateur, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinOper.setAdapter(adapter);

        //liste des pays
        ArrayAdapter<PAYS_NATIONAL> daPays = new ArrayAdapter<PAYS_NATIONAL>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListPays);
        daPays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPays.setAdapter(daPays);
        spinPays.setSelection(0);

        //liste des devises
        ArrayAdapter<DEVISE> daDev = new ArrayAdapter<DEVISE>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListDevise);
        daDev.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinDev.setAdapter(daDev);
        spinDev.setSelection(16);

        return rv;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnValiderEnvoi:
                try {
                    if (!TextUtils.isEmpty(etNomDest.getText().toString()) && !TextUtils.isEmpty(etPrenomDest.getText().toString()) && !TextUtils.isEmpty(etMontantDest.getText().toString())
                            && !TextUtils.isEmpty(etVilleDest.getText().toString()) && !TextUtils.isEmpty(etNomExp.getText().toString()) && !TextUtils.isEmpty(etPrenomExp.getText().toString())
                            && !TextUtils.isEmpty(etAdressExp.getText().toString()) && !TextUtils.isEmpty(etTelExp.getText().toString()) && !TextUtils.isEmpty(etBPExp.getText().toString())
                            && !TextUtils.isEmpty(etProfessExp.getText().toString()) && !TextUtils.isEmpty(etMotifEnvoi.getText().toString()) && !TextUtils.isEmpty(etQuestion.getText().toString())
                            && !TextUtils.isEmpty(etReponse.getText().toString())) {

                        ab = new AlertDialog.Builder(getActivity());
                        ab.setMessage("Voulez-vous valider cette opération?")
                                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
//	             				dialog.cancel();
                                        pDialog = ProgressDialog.show(getActivity(), "", "Validation de l'opération en cours ...");
                                        new Thread(new Runnable() {
                                            @SuppressLint("HandlerLeak")
                                            private Handler handler = new Handler() {

                                                @Override
                                                public void handleMessage(Message msg) {
                                                    super.handleMessage(msg);
                                                    pDialog.dismiss();
                                                    if (Safe.TransferSend != null) {
                                                        if (Safe.TransferSend.Success) {

                                                            Toast.makeText(Safe.MasterObject, "Envoi effectué.",
                                                                    Toast.LENGTH_LONG).show();
                                                            getActivity().finish();
                                                        } else

                                                            Toast.makeText(Safe.MasterObject, "Erreur interne, veuillez réessayer plus tard",
                                                                    Toast.LENGTH_LONG).show();
                                                        getActivity().finish();
                                                    } else

                                                        Toast.makeText(Safe.MasterObject, "Envoi non effectué, veuillez réessayer plus tard",
                                                                Toast.LENGTH_LONG).show();
                                                    getActivity().finish();
                                                }

                                            };

                                            @Override
                                            public void run() {
                                                String operateur = spinOper.getSelectedItem().toString();
                                                String pays = ((PAYS_NATIONAL) spinPays.getSelectedItem()).PAYS_CODE;
                                                String devise = ((DEVISE) spinDev.getSelectedItem()).CODE;
                                                Safe.TransferSend = WsCommunicator.ValiderTransfer(thisObject, operateur, 0, etNomDest.getText().toString(), etPrenomDest.getText().toString(),
                                                        Integer.parseInt(etMontantDest.getText().toString()), devise, pays, etVilleDest.getText().toString(), etNomExp.getText().toString(), etPrenomExp.getText().toString(),
                                                        etAdressExp.getText().toString(), etTelExp.getText().toString(), etBPExp.getText().toString(), etProfessExp.getText().toString(), etMotifEnvoi.getText().toString(),
                                                        etQuestion.getText().toString(), etReponse.getText().toString(), Safe.CurrentUser.USER_CODE);
                                                handler.sendEmptyMessage(0);
                                            }
                                        }).start();
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
            case R.id.btnAnnulerEnvoi:
                try {
                    getActivity().finish();
                } catch (Exception e) {
                    ToastUtil.showToast(thisObject, e.getMessage());
                }
                break;
            default:
                break;
        }
    }

}
