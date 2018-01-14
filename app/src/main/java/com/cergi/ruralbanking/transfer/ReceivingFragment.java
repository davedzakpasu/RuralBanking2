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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cergi.ruralbanking.Alerts;
import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.entities.DEVISE;
import com.cergi.ruralbanking.entities.INFO_TRANSFERT;
import com.cergi.ruralbanking.entities.PAYS_NATIONAL;
import com.cergi.ruralbanking.fingerprints.utils.ToastUtil;

/**
 * Created by CERGI on  21 janv. 2015
 *
 */
public class ReceivingFragment extends Fragment implements View.OnClickListener{
	
	Context thisObject;
	
	Spinner spinOper, spinPays, spinDev;
	
	EditText etCodeRecep, etNomExp, etPrenomExp, etMontantRecept, 
	etNomBenef, etPrenomBenef, etAdressBenef, etTelBenef, etBPBenef, etProfessBenef, 
	etMotifRecept, etQuestion, etReponse;
	
	Button btnValideEnvoi, btnCancelEnvoi;
	
	AlertDialog.Builder ab;
	
	INFO_TRANSFERT infoTransfert;
	
	ProgressDialog pDialog;
	int OperationType;
	
	GridLayout grid;
	
	ArrayAdapter<CharSequence> adapter;

	public ReceivingFragment() {

	}

	public static ReceivingFragment newInstance() {
		return new ReceivingFragment();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		thisObject = getActivity().getApplicationContext();
		View rv = inflater.inflate(R.layout.fragment_receiving, container, false);
		
		spinOper = (Spinner) rv.findViewById(R.id.spinnerOpR);
		spinPays = (Spinner) rv.findViewById(R.id.spinnerPaysProv);
		spinDev = (Spinner) rv.findViewById(R.id.spinnerDeviseMtR);
		
		etCodeRecep = (EditText) rv.findViewById(R.id.txtCodeRecep);
		etNomExp = (EditText) rv.findViewById(R.id.txtNomProv);
		etPrenomExp = (EditText) rv.findViewById(R.id.txtPrenomProv);
		etMontantRecept = (EditText) rv.findViewById(R.id.txtMontantRecept);
		
		etNomBenef = (EditText) rv.findViewById(R.id.txtNomBenef);
		etPrenomBenef = (EditText) rv.findViewById(R.id.txtPrenomBenef);
		etAdressBenef = (EditText) rv.findViewById(R.id.txtAdressBenef);
		etTelBenef = (EditText) rv.findViewById(R.id.txtTelBenef);
		etBPBenef = (EditText) rv.findViewById(R.id.txtBPBenef);
		etProfessBenef = (EditText) rv.findViewById(R.id.txtProfessBenef);
		
		etMotifRecept = (EditText) rv.findViewById(R.id.txtMotifRecept);
		etQuestion = (EditText) rv.findViewById(R.id.txtQuestRecept);
		etReponse = (EditText) rv.findViewById(R.id.txtRespRecept);
		
		btnValideEnvoi = (Button) rv.findViewById(R.id.btnValiderRecept);
		btnCancelEnvoi = (Button) rv.findViewById(R.id.btnAnnulerRecept);
		

		etCodeRecep.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (etCodeRecep.getText().length() == 1 && etCodeRecep.getText().toString() != "0") {
					//		btnFinger.setClickable(false);
							pDialog = ProgressDialog.show(getActivity(), "", "Recherche de l'opération en cours ...");
						    new Thread(new Runnable() {
						    	
								@SuppressLint("HandlerLeak")
								private Handler handler = new Handler() {

									@Override
									public void handleMessage(Message msg) {
										super.handleMessage(msg);
										pDialog.dismiss(); //ferme la boite de progression
						                if(infoTransfert != null) {
//						                   if(TextUtils.isEmpty(infoTransfert.MessageErreur)) {
//						                	   Alerts.showAlert("Erreur", infoTransfert.MessageErreur, thisObject);
//						                   }
//						                   else {
						                	   etCodeRecep.setEnabled(false);
						                	   //récupération des infos du transfert
						                	   etNomExp.setText(infoTransfert.TRANSF_NOMEXP);
						                	   etPrenomExp.setText(infoTransfert.TRANSF_PRENOMEXP);
						                	   etMontantRecept.setText(infoTransfert.TRANSF_MONTANT);
						                	   
						                	   etNomBenef.setText(infoTransfert.TRANSF_NOMBENEF);
						                	   etPrenomBenef.setText(infoTransfert.TRANSF_PRENOMBENEF);
						                	   
//						                	   etQuestion.setText(infoTransfert.TRANSF_QUESTION);
//						                	   etReponse.setText(infoTransfert.TRANSF_REPONSE);

						                	   String oper = infoTransfert.TRANSF_OPERATEUR.trim();
												for(int i=0; i < adapter.getCount(); i++) {
													String lp = adapter.getItem(i).toString();
													  if(lp.equals(oper)){
														  spinOper.setSelection(i);
													    break;
													  }
												}
												
												String pays_prov = infoTransfert.TRANSF_PAYSPROV.trim();
												for(int i=0; i < Safe.ListPays.size(); i++) {
													String lp = Safe.ListPays.get(i).PAYS_CODE.trim();
													  if(lp.equals(pays_prov)){
														  spinPays.setSelection(i);
													    break;
													  }
												}
						                }
						                else {                        
						                	  Alerts.showAlert("Erreur", "Code de réception invalide.", getActivity());
						                }
									}
								};
								
								@Override
								public void run() {
									infoTransfert = WsCommunicator.InfoTransfer(getActivity(), Integer.parseInt(etCodeRecep.getText().toString()));
									handler.sendEmptyMessage(0);
								}
							}).start();
					    }
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		// liste des types de piece
		adapter = ArrayAdapter.createFromResource(getActivity(), R.array.type_operateur, android.R.layout.simple_spinner_item);
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
			case R.id.btnValiderRecept:
				try {
					if (!TextUtils.isEmpty(etNomExp.getText().toString()) && !TextUtils.isEmpty(etPrenomExp.getText().toString()) && !TextUtils.isEmpty(etMontantRecept.getText().toString()) 
						&& !TextUtils.isEmpty(etNomBenef.getText().toString()) && !TextUtils.isEmpty(etPrenomBenef.getText().toString()) && !TextUtils.isEmpty(etAdressBenef.getText().toString()) 
						&& !TextUtils.isEmpty(etTelBenef.getText().toString()) && !TextUtils.isEmpty(etBPBenef.getText().toString()) && !TextUtils.isEmpty(etProfessBenef.getText().toString()) 
						&& !TextUtils.isEmpty(etMotifRecept.getText().toString()) && !TextUtils.isEmpty(etQuestion.getText().toString()) && !TextUtils.isEmpty(etReponse.getText().toString())) {
						
						ab = new AlertDialog.Builder(getActivity());
	            		ab.setMessage("Voulez-vous valider cette opération?")
	            		.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
	             			@Override
	             			public void onClick(DialogInterface dialog, int which) {	
	             				dialog.cancel();
	             				pDialog = ProgressDialog.show(getActivity(), "", "Validation de l'opération en cours ...");
	             				new Thread(new Runnable() {
									@SuppressLint("HandlerLeak")
									private Handler handler = new Handler() {

										@Override
										public void handleMessage(Message msg) {
											super.handleMessage(msg);
											pDialog.dismiss();
											if(Safe.TransferReceive !=null) {
							        			if(Safe.TransferReceive.Success) {
							                        Toast.makeText(Safe.MasterObject, "Retrait effectué.", Toast.LENGTH_LONG).show();
							                        getActivity().finish();
							                    }
							        			else
							        				
							                        Toast.makeText(Safe.MasterObject, "Erreur interne, veuillez réessayer plus tard",
							    							Toast.LENGTH_LONG).show();
							        			getActivity().finish();
											}
											else
												
												Toast.makeText(Safe.MasterObject, "Retrait non effectué, veuillez réessayer plus tard",
														Toast.LENGTH_LONG).show();
											getActivity().finish();
										}
										
									};
									
									@Override
									public void run() {
										if (!etQuestion.getText().toString().equals(infoTransfert.TRANSF_QUESTION) || !etReponse.getText().toString().equals(infoTransfert.TRANSF_REPONSE)) {
											Toast.makeText(thisObject,	"Veuillez vérifier la question et/ou la réponse",	Toast.LENGTH_LONG).show();
										}
										else {
										String operateur = spinOper.getSelectedItem().toString();
										String devise = ((DEVISE) spinDev.getSelectedItem()).CODE;
										String pays = ((PAYS_NATIONAL) spinPays.getSelectedItem()).PAYS_CODE;
										Safe.TransferReceive = WsCommunicator.ValiderTransfer(thisObject, operateur, Integer.parseInt(etCodeRecep.getText().toString()), etNomBenef.getText().toString(), etPrenomBenef.getText().toString(), 
												Integer.parseInt(etMontantRecept.getText().toString()), devise, pays, "", etNomExp.getText().toString(), etPrenomExp.getText().toString(),
												etAdressBenef.getText().toString(), etTelBenef.getText().toString(), etBPBenef.getText().toString(), etProfessBenef.getText().toString(), 
												etMotifRecept.getText().toString(),	etQuestion.getText().toString(), etReponse.getText().toString(), Safe.CurrentUser.USER_CODE);
										}
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
					}
					else 
						Toast.makeText(thisObject,	"Veuillez renseigner tous les champs SVP.",	Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					ToastUtil.showToast(thisObject, e.getMessage()); 
				}
				break;
			case R.id.btnAnnulerRecept:
				try {					

				} catch (Exception e) {
					ToastUtil.showToast(thisObject, e.getMessage());
				}
				break;
			default:
				break;
		}
	}
}
