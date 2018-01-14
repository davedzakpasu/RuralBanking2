package com.cergi.ruralbanking.account;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.entities.STATUT;

/**
 * Created by Thierry on 03/02/14.
 */
public class IdentiteFragment extends Fragment {

    Spinner spinStatut = null;
    Spinner spinNation = null;

    EditText txtDateNaiss, txtMereNaiss, txtPereNaiss;

    GregorianCalendar now = new GregorianCalendar();
    Calendar cal = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;

    int year = now.get(Calendar.YEAR);
    int y, m, d;
    int min = year - 80, max = year - 18;
    Date startDate = new GregorianCalendar(min, 0, 1).getTime();
    Date endDate = new GregorianCalendar(max, 11, 31).getTime();

    public IdentiteFragment() {

    }

    public static IdentiteFragment newInstance() {
        return new IdentiteFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_identification, container, false);

        txtDateNaiss = (EditText) rootView.findViewById(R.id.txtDateNaiss);
        txtMereNaiss = (EditText) rootView.findViewById(R.id.txtNaissMere);
        txtPereNaiss = (EditText) rootView.findViewById(R.id.txtNaissPere);

        txtDateNaiss.setKeyListener(null);
        txtMereNaiss.setKeyListener(null);
        txtPereNaiss.setKeyListener(null);

        txtDateNaiss.setOnClickListener(new OnClickListener() {
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

                        updateLabel(txtDateNaiss);
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), pDateSetListener, y, m, d);
                dialog.getDatePicker().setMinDate(startDate.getTime());
                dialog.getDatePicker().setMaxDate(endDate.getTime());
                dialog.getDatePicker().init(endDate.getYear(), endDate.getMonth(), endDate.getDay(), null);
                dialog.show();
            }
        });

        txtMereNaiss.setOnClickListener(new OnClickListener() {
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

                        updateLabel(txtMereNaiss);
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), pDateSetListener, y, m, d);
                dialog.getDatePicker().setMaxDate(endDate.getTime());
                dialog.getDatePicker().init(endDate.getYear(), endDate.getMonth(), endDate.getDay(), null);
                dialog.show();
            }
        });

        txtPereNaiss.setOnClickListener(new OnClickListener() {
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

                        updateLabel(txtPereNaiss);
                    }
                };

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), pDateSetListener, y, m, d);
                dialog.getDatePicker().setMaxDate(endDate.getTime());
                dialog.getDatePicker().init(endDate.getYear(), endDate.getMonth(), endDate.getDay(), null);
                dialog.show();
            }
        });

        spinStatut = (Spinner) rootView.findViewById(R.id.spinnerStatut);
        spinNation = (Spinner) rootView.findViewById(R.id.spinnerNation);

        ArrayAdapter<STATUT> daStatut = new ArrayAdapter<STATUT>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListStatut);
        daStatut.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinStatut.setAdapter(daStatut);
        spinStatut.setSelection(2);

        ArrayList<String> values = new ArrayList<String>();
        for (int i = 0; i < Safe.ListPays.size(); i++)
            values.add(Safe.ListPays.get(i).PAYS_NATION);
        ArrayAdapter<String> adNation = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, values);
        adNation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNation.setAdapter(adNation);
        spinNation.setSelection(0);

        Safe.ViewIdentite = rootView;
        return rootView;
    }

    private void updateLabel(EditText et) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);

        et.setText(sdf.format(cal.getTime()));
    }
}