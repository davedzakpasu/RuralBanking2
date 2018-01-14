package com.cergi.ruralbanking.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;

import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.fingerprints.FpHome;
import com.cergi.ruralbanking.fingerprints.MyApplication;
import com.cergi.ruralbanking.fingerprints.utils.ToastUtil;

public class InfoDigitFragment extends Fragment {
    // Button btConnBT = null;
    Switch switchBT;
    Fragment f = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rv = inflater.inflate(R.layout.fragment_info_digits, container, false);

        switchBT = (Switch) rv.findViewById(R.id.swBT);
        switchBT.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        if (!Safe.application.isConnect()) {
                            getActivity().startActivity(new Intent(getActivity(), com.cergi.ruralbanking.fingerprints.ConnectActivity.class));
                        } else {
                            f = new FpHome();
                            FragmentTransaction transaction = getFragmentManager().beginTransaction();
                            transaction.replace(R.id.otherFrame, f);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
//						BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
//						adapter.disable();

                    f = new Fragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.otherFrame, f);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            }
        });

        Safe.ViewInfoDigit = rv;
        return rv;
    }
}
