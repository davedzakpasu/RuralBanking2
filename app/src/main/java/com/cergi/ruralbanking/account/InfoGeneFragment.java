package com.cergi.ruralbanking.account;

import java.text.SimpleDateFormat;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.entities.ACTIVITE;
import com.cergi.ruralbanking.entities.NATURE_PIECE_ID;
import com.cergi.ruralbanking.entities.PAYS_NATIONAL;
import com.cergi.ruralbanking.entities.PROFESSION;
import com.cergi.ruralbanking.entities.SIT_MATRIM;

/**
 * Created by Thierry on 03/02/14.
 */
public class InfoGeneFragment extends Fragment {

    Spinner spinNaturePieceId = null;
    Spinner spinSitMatrim = null;
    Spinner spinProfession = null;
    Spinner spinActivite = null;
    Spinner spinPays = null;
    
    EditText txtDateDeliv;
    
    Button save_etciv;
    
    DatePicker dp;
    GregorianCalendar now = new GregorianCalendar();
    Calendar cal = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    
    int year = now.get(Calendar.YEAR);
    int y, m, d;
    int min = year - 15;
    Date startDate = new GregorianCalendar(min,0,1).getTime();
    Date endDate = new GregorianCalendar(year,now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH)).getTime();
    
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_info_gene, container, false);

        save_etciv = (Button) rootView.findViewById(R.id.btnSaveId);
              
        txtDateDeliv = (EditText) rootView.findViewById(R.id.txtDateDeliv);
        
        txtDateDeliv.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {

	                public void onDateSet(DatePicker view, int year,  int monthOfYear, int dayOfMonth) {	            	
	                	y = year;
	                    m = monthOfYear;
	                    d = dayOfMonth;
	                	
	                	cal.set(Calendar.YEAR, year);
	                    cal.set(Calendar.MONTH, monthOfYear);
	                    cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

	                    updateLabel();
	                }
	            };
	            
	            DatePickerDialog dialog = new DatePickerDialog(getActivity(), pDateSetListener, y, m, d);
	            dialog.getDatePicker().setMinDate(startDate.getTime());
	            dialog.getDatePicker().setMaxDate(endDate.getTime());
	            dialog.show();
	        }
	    });
        
        spinNaturePieceId = (Spinner)rootView.findViewById(R.id.spinnerNatureID);
        spinSitMatrim = (Spinner)rootView.findViewById(R.id.spinnerSitMatrim);
        spinProfession = (Spinner)rootView.findViewById(R.id.spinnerProfession);
        spinActivite = (Spinner)rootView.findViewById(R.id.spinnerActivite);
        spinPays = (Spinner)rootView.findViewById(R.id.spinnerPaysResi);

        ArrayAdapter<PAYS_NATIONAL> daPays = new ArrayAdapter<PAYS_NATIONAL>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListPays);
        daPays.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinPays.setAdapter(daPays);
        spinPays.setSelection(0);
        
        ArrayAdapter<NATURE_PIECE_ID> daNaturePiece = new ArrayAdapter<NATURE_PIECE_ID>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListNaturePieceId);
        daNaturePiece.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinNaturePieceId.setAdapter(daNaturePiece);

        spinNaturePieceId.setSelection(3);

        ArrayAdapter<SIT_MATRIM> daSitMatrim = new ArrayAdapter<SIT_MATRIM>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListSitMatrim);
        daSitMatrim.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinSitMatrim.setAdapter(daSitMatrim);

        ArrayAdapter<PROFESSION> daProfession = new ArrayAdapter<PROFESSION>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListProfession);
        daProfession.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinProfession.setAdapter(daProfession);
        
      ArrayAdapter<ACTIVITE> daActivite = new ArrayAdapter<ACTIVITE>(Safe.MasterObject, android.R.layout.simple_spinner_item, Safe.ListActivite);
      daActivite.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinActivite.setAdapter(daActivite);

        Safe.ViewInfoGene = rootView;
        return rootView;
    }
	
	private void updateLabel() {
    	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
    	txtDateDeliv.setText(sdf.format(cal.getTime()));
    }
}