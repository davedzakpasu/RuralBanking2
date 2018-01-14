package com.cergi.ruralbanking.account;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.entities.ACTIVITE;
import com.cergi.ruralbanking.entities.INFO_CLIENT;
import com.cergi.ruralbanking.entities.NATURE_PIECE_ID;
import com.cergi.ruralbanking.entities.OpResult;
import com.cergi.ruralbanking.entities.PROFESSION;
import com.cergi.ruralbanking.entities.SIT_MATRIM;
import com.cergi.ruralbanking.entities.STATUT;
import com.cergi.ruralbanking.fingerprints.MyApplication;

import java.util.Calendar;

@SuppressLint({"HandlerLeak", "SimpleDateFormat"})
public class AccountActivity extends AppCompatActivity implements Runnable, OnClickListener {
    AccountActivity thisObject;

    INFO_CLIENT ic = null;

    private Toolbar toolbar;

    View idFrag, idInfoGeneFrag, idDigitFrag;

    EditText txtNom, txtPrenom, txtLieuNais, txtDateNaiss, txtVilleRes, txtAdres, txtTel, txtEmail,
            txtRefPiece, txtDateDeliv, txtDelivPar, txtNomMere, txtPrenomMere,
            txtNomPere, txtPrenomPere, txtNaissMere, txtNaissPere;

    Spinner spStatut, spNation, spActivite, spPaysRes, spSitMat, spNaturePiece, spProfession;

    ViewPager viewPager;
    TabLayout tabLayout;
    DatePicker dpB, dpD;

    Switch swbt;

    ProgressDialog pDialog;
    int OperationType = 0;

    MenuItem cpt, src;

    Button etciv;

    OpResult UpdateEtciv;

    String[] tabsTitles = {"Identité", "Infos. Générales", "Infos. Digitales"};

    Calendar cal = Calendar.getInstance();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        cpt = menu.findItem(R.id.cpt);
        // Associate searchable configuration with the SearchView
        SearchManager srcManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView srcView = (SearchView) menu.findItem(R.id.search).getActionView();
        srcView.setSearchableInfo(srcManager.getSearchableInfo(getComponentName()));
        srcView.setIconifiedByDefault(true);

        srcView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                Log.i("well", " this worked");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                Log.i("well", " this worked");
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cpt:
                pDialog = ProgressDialog.show(thisObject, "", "Veuillez patienter...");
                Thread t = new Thread(thisObject);
                OperationType = 2;
                t.start();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if (TextUtils.isEmpty(Safe.Matricule))
            cpt.setEnabled(false);
        else
            cpt.setEnabled(true);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        thisObject = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_viewpager);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Set Home Button Enabled
        //getSupportActionBar().setHomeButtonEnabled(true);

        // Add subtitle
        getSupportActionBar().setSubtitle(R.string.open_account);

        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(new TabsPagerAdapter(thisObject, getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 1) {
                    idFrag = Safe.ViewIdentite;
                    idInfoGeneFrag = Safe.ViewInfoGene;

                    txtNom = (EditText) idFrag.findViewById(R.id.txtNom);
                    txtPrenom = (EditText) idFrag.findViewById(R.id.txtPrenoms);
                    txtLieuNais = (EditText) idFrag.findViewById(R.id.txtLieuNaiss);
                    txtDateNaiss = (EditText) idFrag.findViewById(R.id.txtDateNaiss);

                    txtVilleRes = (EditText) idInfoGeneFrag.findViewById(R.id.txtVilleResi);
                    txtAdres = (EditText) idInfoGeneFrag.findViewById(R.id.txtAdresse);
                    txtTel = (EditText) idInfoGeneFrag.findViewById(R.id.txtTel);
                    txtEmail = (EditText) idInfoGeneFrag.findViewById(R.id.txtEmail);

                    txtRefPiece = (EditText) idInfoGeneFrag.findViewById(R.id.txtRef);
                    txtDateDeliv = (EditText) idInfoGeneFrag.findViewById(R.id.txtDateDeliv);
                    txtDelivPar = (EditText) idInfoGeneFrag.findViewById(R.id.txtDelivrePar);

                    txtNomMere = (EditText) idFrag.findViewById(R.id.txtNomMere);
                    txtPrenomMere = (EditText) idFrag.findViewById(R.id.txtPrenomMere);
                    txtNaissMere = (EditText) idFrag.findViewById(R.id.txtNaissMere);

                    txtNomPere = (EditText) idFrag.findViewById(R.id.txtNomPere);
                    txtPrenomPere = (EditText) idFrag.findViewById(R.id.txtPrenomPere);
                    txtNaissPere = (EditText) idFrag.findViewById(R.id.txtNaissPere);

                    etciv = (Button) idInfoGeneFrag.findViewById(R.id.btnSaveId);

                    etciv.setOnClickListener(thisObject);
                } else if (tab.getPosition() == 2) {
                    idDigitFrag = Safe.ViewInfoDigit;
                    swbt = (Switch) idDigitFrag.findViewById(R.id.swBT);
                    if (TextUtils.isEmpty(Safe.Matricule)) {
                        swbt.setEnabled(false);
                    } else {
                        swbt.setEnabled(true);
                    }
                }
            }

            @Override
            public void onTabUnselected(Tab tab) {

            }

            @Override
            public void onTabReselected(Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!TextUtils.isEmpty(Safe.Matricule) && ic == null) {
//			Toast.makeText(Safe.MasterObject, "Matricule est ["+Safe.Matricule+"]", Toast.LENGTH_LONG).show();
            pDialog = ProgressDialog.show(thisObject, "", "Veuillez patienter...");
            Thread t = new Thread(thisObject);
            OperationType = 3;
            t.start();
        }
    }

    public String checkDigit(int number) {
        return number < 10 ? "0" + number : String.valueOf(number);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            switch (OperationType) {
                case 1:
                    pDialog.dismiss();
                    if (!TextUtils.isEmpty(Safe.Matricule)) {
                        Toast.makeText(Safe.MasterObject, "Etat Civil [" + Safe.Matricule + "] créé", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(thisObject, "Une erreur est survenue. Veuillez réessayer.", Toast.LENGTH_LONG).show();
                    }
                    break;
                case 3:
                    pDialog.dismiss();
                    if (ic == null) {
                        Toast.makeText(thisObject, "Client introuvable.", Toast.LENGTH_LONG).show();
                    } else {
                        //instantiation des views (fragments)
                        idFrag = Safe.ViewIdentite;
                        idInfoGeneFrag = Safe.ViewInfoGene;

                        //Initialisation des edittext, spinners, datepickers
                        txtNom = (EditText) idFrag.findViewById(R.id.txtNom);
                        txtPrenom = (EditText) idFrag.findViewById(R.id.txtPrenoms);
                        txtLieuNais = (EditText) idFrag.findViewById(R.id.txtLieuNaiss);
                        txtDateNaiss = (EditText) idFrag.findViewById(R.id.txtDateNaiss);
                        txtVilleRes = (EditText) idInfoGeneFrag.findViewById(R.id.txtVilleResi);
                        txtAdres = (EditText) idInfoGeneFrag.findViewById(R.id.txtAdresse);
                        txtTel = (EditText) idInfoGeneFrag.findViewById(R.id.txtTel);
                        txtEmail = (EditText) idInfoGeneFrag.findViewById(R.id.txtEmail);

                        txtRefPiece = (EditText) idInfoGeneFrag.findViewById(R.id.txtRef);
                        txtDateDeliv = (EditText) idInfoGeneFrag.findViewById(R.id.txtDateDeliv);
                        txtDelivPar = (EditText) idInfoGeneFrag.findViewById(R.id.txtDelivrePar);

                        txtNomMere = (EditText) idFrag.findViewById(R.id.txtNomMere);
                        txtPrenomMere = (EditText) idFrag.findViewById(R.id.txtPrenomMere);
                        txtNaissMere = (EditText) idFrag.findViewById(R.id.txtNaissMere);

                        txtNomPere = (EditText) idFrag.findViewById(R.id.txtNomPere);
                        txtPrenomPere = (EditText) idFrag.findViewById(R.id.txtPrenomPere);
                        txtNaissPere = (EditText) idFrag.findViewById(R.id.txtNaissPere);

                        spStatut = (Spinner) idFrag.findViewById(R.id.spinnerStatut);
                        spNation = (Spinner) idFrag.findViewById(R.id.spinnerNation);
                        spPaysRes = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerPaysResi);

                        spSitMat = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerSitMatrim);
                        spActivite = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerActivite);
                        spProfession = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerProfession);
                        spNaturePiece = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerNatureID);

                        //Affectation des valeurs
                        txtNom.setText(ic.PERSPHYS_NOM);
                        txtPrenom.setText(ic.PERSPHYS_PRENOM);
                        txtLieuNais.setText(ic.PERSPHYS_LIEUNAISS);
                        txtDateNaiss.setText(ic.PERSPHYS_DATENAISS.substring(0, 10));
                        txtVilleRes.setText(ic.ETCIV_VILLE_RESIDENCE);
                        txtAdres.setText(ic.ETCIV_ADRESS_GEOG1);
                        txtTel.setText(ic.ETCIV_TELEPHONE);
                        txtEmail.setText(ic.ETCIV_INTERNET);

                        txtRefPiece.setText(ic.PERSPHYS_PIECE_ID);
                        txtDateDeliv.setText(ic.PERSPHYS_PIECE_ID_DATE.substring(0, 10));
                        txtDelivPar.setText(ic.PERSPHYS_PIECE_ID_ORGAN);

                        txtNomMere.setText(ic.PERSPHYS_MERE_NOM);
                        txtPrenomMere.setText(ic.PERSPHYS_MERE_PRENOM);
                        txtNaissMere.setText(ic.PERSPHYS_MERE_DATENAISS.substring(0, 10));

                        txtNomPere.setText(ic.PERSPHYS_PERE_NOM);
                        txtPrenomPere.setText(ic.PERSPHYS_PERE_PRENOM);
                        txtNaissPere.setText(ic.PERSPHYS_PERE_DATENAISS.substring(0, 10));

                        String s = ic.STATUT_CODE.trim();
                        for (int i = 0; i < Safe.ListStatut.size(); i++) {
                            String ls = Safe.ListStatut.get(i).STATUT_CODE.trim();
                            if (ls.equals(s)) {
                                spStatut.setSelection(i);
                                break;
                            }
                        }

                        String p = ic.PAYS_CODE.trim();
                        String pr = ic.ETCIV_PAYS_RESIDENCE.trim();
                        for (int i = 0; i < Safe.ListPays.size(); i++) {
                            String lp = Safe.ListPays.get(i).PAYS_CODE.trim();
                            if (lp.equals(p))
                                spNation.setSelection(i);
                            if (lp.equals(pr))
                                spPaysRes.setSelection(i);
                        }

                        String act = ic.ACTBCEAO_CODE.trim();
                        for (int i = 0; i < Safe.ListActivite.size(); i++) {
                            String la = Safe.ListActivite.get(i).ACTBCEAO_CODE.trim();
                            if (la.equals(act)) {
                                spActivite.setSelection(i);
                                break;
                            }
                        }

                        String pro = ic.PROFESS_CODE.trim();
                        for (int i = 0; i < Safe.ListProfession.size(); i++) {
                            String lp = Safe.ListProfession.get(i).PROFESS_CODE.trim();
                            if (lp.equals(pro)) {
                                spProfession.setSelection(i);
                                break;
                            }
                        }

                        String sm = ic.SITMATRIM_CODE.trim();
                        for (int i = 0; i < Safe.ListSitMatrim.size(); i++) {
                            String lp = Safe.ListSitMatrim.get(i).SITMATRIM_CODE.trim();
                            if (lp.equals(sm)) {
                                spSitMat.setSelection(i);
                                break;
                            }
                        }

                        String nat = ic.NATURE_ID_CODE.trim();
                        for (int i = 0; i < Safe.ListNaturePieceId.size(); i++) {
                            String lp = Safe.ListNaturePieceId.get(i).NATURE_ID_CODE.trim();
                            if (lp.equals(nat)) {
                                spNaturePiece.setSelection(i);
                                break;
                            }
                        }

                        Safe.Photo = ic.FP_PHOTO;
                    }
                    break;
                case 4:
                    pDialog.dismiss();
                    if (UpdateEtciv.Success == true) {
                        etciv.setEnabled(false);
                        Toast.makeText(Safe.MasterObject, "Etat Civil [" + Safe.Matricule + "] mis à jour", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(thisObject, "Une erreur est survenue. Veuillez réessayer.", Toast.LENGTH_LONG).show();
                    }
                    break;
            }
        }

        ;
    };

    public void run() {
        switch (OperationType) {
            case 1:
                try {
                    //Initializing components
                    spStatut = (Spinner) idFrag.findViewById(R.id.spinnerStatut);
                    spNation = (Spinner) idFrag.findViewById(R.id.spinnerNation);
                    spPaysRes = (Spinner) idFrag.findViewById(R.id.spinnerPaysResi);
                    spActivite = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerActivite);


                    spSitMat = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerSitMatrim);
                    spProfession = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerProfession);
                    spNaturePiece = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerNatureID);

                    //Getting values
                    String UserCode = Safe.CurrentUser.USER_CODE;
                    String agence = Safe.CurrentUser.AGENCE_CODE;

                    String codeStatut = ((STATUT) spStatut.getSelectedItem()).STATUT_CODE;

                    String nomclient = txtNom.getText().toString();
                    String prenomclient = txtPrenom.getText().toString();

                    String lieunaiss = txtLieuNais.getText().toString();

                    String datenaiss = txtDateNaiss.getText().toString();

                    Safe.NomReduit = nomclient + " " + prenomclient;

                    String nationalite = Safe.ListPays.get(spNation.getSelectedItemPosition()).PAYS_CODE;

                    String codeActivite = ((ACTIVITE) spActivite.getSelectedItem()).ACTBCEAO_CODE;

//				String paysres = Safe.ListPays.get(spPaysRes.getSelectedItemPosition()).PAYS_CODE;
                    String paysres = "280";
                    String villeresid = txtVilleRes.getText().toString();
                    String address = txtAdres.getText().toString();
                    String telephone = txtTel.getText().toString();
                    String email = txtEmail.getText().toString();

                    String codeSitMat = ((SIT_MATRIM) spSitMat.getSelectedItem()).SITMATRIM_CODE;

                    String codeNaturePiece = ((NATURE_PIECE_ID) spNaturePiece.getSelectedItem()).NATURE_ID_CODE;

                    String piece_id_ref = txtRefPiece.getText().toString();

                    String delivre_le = txtDateDeliv.getText().toString();

                    String delivre_par = txtDelivPar.getText().toString();

                    String profession = ((PROFESSION) spProfession.getSelectedItem()).PROFESS_CODE;

                    String nomMere = txtNomMere.getText().toString();
                    String prenomMere = txtPrenomMere.getText().toString();
                    String naissMere = txtNaissMere.getText().toString();

                    String nomPere = txtNomPere.getText().toString();
                    String prenomPere = txtPrenomPere.getText().toString();
                    String naissPere = txtNaissPere.getText().toString();

                    Safe.Matricule = WsCommunicator.CreerEtatCivil(thisObject, UserCode, agence, "01", codeStatut, nomclient, prenomclient, datenaiss, lieunaiss, nationalite,
                            codeActivite, email, paysres, villeresid, address, telephone, codeSitMat, codeNaturePiece, piece_id_ref, delivre_le, delivre_par, profession,
                            nomMere, prenomMere, naissMere, nomPere, prenomPere, naissPere);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case 2:
                Intent intent = new Intent(thisObject, NewAccountActivity.class);
                startActivity(intent);
                thisObject.finish();
                break;
            case 3:
                try {
                    ic = WsCommunicator.InfoClient(thisObject, Safe.Matricule);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                //Initializing components
                spStatut = (Spinner) idFrag.findViewById(R.id.spinnerStatut);
                spNation = (Spinner) idFrag.findViewById(R.id.spinnerNation);
                spActivite = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerActivite);
                spPaysRes = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerPaysResi);

                spSitMat = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerSitMatrim);
                spProfession = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerProfession);
                spNaturePiece = (Spinner) idInfoGeneFrag.findViewById(R.id.spinnerNatureID);

                String UserCode = Safe.CurrentUser.USER_CODE;
                String agence = Safe.CurrentUser.AGENCE_CODE;

                String codeStatut = ((STATUT) spStatut.getSelectedItem()).STATUT_CODE;

                String nomclient = txtNom.getText().toString();
                String prenomclient = txtPrenom.getText().toString();

                String lieunaiss = txtLieuNais.getText().toString();

                String datenaiss = txtDateNaiss.getText().toString();

                Safe.NomReduit = nomclient + " " + prenomclient;

                String nationalite = Safe.ListPays.get(spNation.getSelectedItemPosition()).PAYS_CODE;

                String codeActivite = ((ACTIVITE) spActivite.getSelectedItem()).ACTBCEAO_CODE;

                String paysres = Safe.ListPays.get(spPaysRes.getSelectedItemPosition()).PAYS_CODE;

                String villeresid = txtVilleRes.getText().toString();
                String address = txtAdres.getText().toString();
                String telephone = txtTel.getText().toString();
                String email = txtEmail.getText().toString();

                String codeSitMat = ((SIT_MATRIM) spSitMat.getSelectedItem()).SITMATRIM_CODE;

                String codeNaturePiece = ((NATURE_PIECE_ID) spNaturePiece.getSelectedItem()).NATURE_ID_CODE;

                String piece_id_ref = txtRefPiece.getText().toString();

                String delivre_le = txtDateDeliv.getText().toString();

                String delivre_par = txtDelivPar.getText().toString();

                String profession = ((PROFESSION) spProfession.getSelectedItem()).PROFESS_CODE;

                String nomMere = txtNomMere.getText().toString();
                String prenomMere = txtPrenomMere.getText().toString();
                String naissMere = txtNaissMere.getText().toString();

                String nomPere = txtNomPere.getText().toString();
                String prenomPere = txtPrenomPere.getText().toString();
                String naissPere = txtNaissPere.getText().toString();

                UpdateEtciv = WsCommunicator.MajEtatCivil(thisObject, Safe.Matricule, UserCode, agence, codeStatut, nomclient, prenomclient, datenaiss, lieunaiss, nationalite,
                        codeActivite, email, paysres, villeresid, address, telephone, codeSitMat, codeNaturePiece, piece_id_ref, delivre_le, delivre_par, profession, nomMere, prenomMere, naissMere, nomPere, prenomPere, naissPere);
                break;
            default:
                break;
        }
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSaveId:
                try {
                    if (!TextUtils.isEmpty(txtNom.getText().toString()) && !TextUtils.isEmpty(txtPrenom.getText().toString()) && !TextUtils.isEmpty(txtLieuNais.getText().toString())
                            && !TextUtils.isEmpty(txtDateNaiss.getText().toString()) && !TextUtils.isEmpty(txtVilleRes.getText().toString()) && !TextUtils.isEmpty(txtAdres.getText().toString())
                            && !TextUtils.isEmpty(txtTel.getText().toString()) && !TextUtils.isEmpty(txtEmail.getText().toString()) && !TextUtils.isEmpty(txtRefPiece.getText().toString()) && !TextUtils.isEmpty(txtDateDeliv.getText().toString())
                            && !TextUtils.isEmpty(txtDelivPar.getText().toString()) && !TextUtils.isEmpty(txtNomMere.getText().toString()) && !TextUtils.isEmpty(txtPrenomMere.getText().toString())) {
                        if (TextUtils.isEmpty(Safe.Matricule)) {
                            pDialog = ProgressDialog.show(thisObject, "", "Création de l'Etat Civil en cours...");
                            Thread t = new Thread(thisObject);
                            OperationType = 1;
                            t.start();
                        } else {
                            pDialog = ProgressDialog.show(thisObject, "", "Mise à jour de l'Etat Civil en cours...");
                            Thread t = new Thread(thisObject);
                            OperationType = 4;
                            t.start();
                        }
                    } else
                        Toast.makeText(thisObject, "Veuillez renseigner tous les champs SVP.", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public class TabsPagerAdapter extends FragmentPagerAdapter {
        Context context;
        final int PAGE_COUNT = 3;

        public TabsPagerAdapter(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case 0:
                    return IdentiteFragment.newInstance();
                case 1:
                    return new InfoGeneFragment();
                case 2:
                    return new InfoDigitFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabsTitles[position];
        }
    }
}