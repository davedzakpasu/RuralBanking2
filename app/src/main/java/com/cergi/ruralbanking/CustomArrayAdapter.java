package com.cergi.ruralbanking;

import java.util.ArrayList;

import com.cergi.ruralbanking.entities.INFO_CLIENT;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomArrayAdapter extends ArrayAdapter<INFO_CLIENT>{

	public CustomArrayAdapter(Context context, ArrayList<INFO_CLIENT> clients) {
	       super(context, 0, clients);
	    }

	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	       // Get the data item for this position
	    	INFO_CLIENT client = getItem(position);    
	       // Check if an existing view is being reused, otherwise inflate the view
	       if (convertView == null) {
	          convertView = LayoutInflater.from(getContext()).inflate(R.layout.client_list, parent, false);
	       }
	       // Lookup view for data population
	       TextView tvName = (TextView) convertView.findViewById(R.id.nom_client);
	       TextView tvMatricule = (TextView) convertView.findViewById(R.id.matricule_client);
	       // Populate the data into the template view using the data object
	       tvName.setText(client.PERSPHYS_NOM +" "+client.PERSPHYS_PRENOM);
	       tvMatricule.setText(client.ETCIV_MATRICULE);
	       // Return the completed view to render on screen
	       return convertView;
	   }
}
