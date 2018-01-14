package com.cergi.ruralbanking.rbm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cergi.ruralbanking.MenuCard;
import com.cergi.ruralbanking.MenusAdapter;
import com.cergi.ruralbanking.R;
import com.cergi.ruralbanking.Safe;
import com.cergi.ruralbanking.comms.WsCommunicator;
import com.cergi.ruralbanking.fingerprints.MyApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david.dzakpasu on 14/10/2016.
 */

public class Main extends AppCompatActivity implements Runnable, LocationListener {
    Main main;

    private RecyclerView recyclerView;
    private MenusAdapter adapter;
    private List<MenuCard> menuList;

    private Toolbar toolbar;

    LocationManager locationManager;

    ProgressDialog pDialog;
    int OperationType;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Safe.CurrentUser.USER_ABREGE);
        menu.add(R.string.logout);
        menu.getItem(0).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.getItem(1).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                Intent intent = new Intent(main, LoginActivity.class);
                startActivity(intent);
                main.finish();
                return false;
            }
        });
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Safe.MasterObject = this;
        main = this;
        Safe.application = (MyApplication) main.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(false);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);

        /*Criteria critere = new Criteria();

        // Pour indiquer la précision voulue
        // On peut mettre ACCURACY_FINE pour une haute précision ou ACCURACY_COARSE pour une moins bonne précision
        critere.setAccuracy(Criteria.ACCURACY_FINE);

        // Est-ce que le fournisseur doit être capable de donner une altitude ?
        critere.setAltitudeRequired(true);

        // Est-ce que le fournisseur doit être capable de donner une direction ?
        critere.setBearingRequired(true);

        // Est-ce que le fournisseur peut être payant ?
        critere.setCostAllowed(false);

        // Pour indiquer la consommation d'énergie demandée
        // Criteria.POWER_HIGH pour une haute consommation, Criteria.POWER_MEDIUM pour une consommation moyenne et Criteria.POWER_LOW pour une basse consommation
        critere.setPowerRequirement(Criteria.POWER_MEDIUM);

        // Est-ce que le fournisseur doit être capable de donner une vitesse ?
        critere.setSpeedRequired(true);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        ArrayList<LocationProvider> providers = new ArrayList<LocationProvider>();
        List<String> names = locationManager.getProviders(critere, true);
        String best = locationManager.getBestProvider(critere, true);

        for (String name : names)
            providers.add(locationManager.getProvider(name));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, new LocationListener() {

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onLocationChanged(Location location) {
                Log.d("GPS", "Latitude " + location.getLatitude() + " et longitude " + location.getLongitude());
            }
        });*/


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        menuList = new ArrayList<>();
        adapter = new MenusAdapter(this, menuList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareItems();

        if (savedInstanceState == null && !Safe.start) {
            pDialog = ProgressDialog.show(main, "", "Initialisation en cours... Veuillez patienter.");
            Thread t = new Thread(main);
            OperationType = 1;
            t.start();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        outState.putString("mTitle", mTitle.toString());
//        setTitle(mTitle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        mTitle = savedInstanceState.getString("mTitle");
//        setTitle(mTitle);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pDialog.dismiss();
            switch (OperationType) {
                case 1:
                    Safe.start = true;
                    break;
            }
        }
    };

    /**
     * Adding few items for testing
     */
    private void prepareItems() {
        int[] covers = new int[]{
                R.drawable.wallet,
                R.drawable.piggy_bank,
                R.drawable.get_money,
                R.drawable.money_transfer,
                R.drawable.mortgage,
                R.drawable.transfer,
                R.drawable.exchange};

        MenuCard a = new MenuCard("OUVERTURE DE COMPTE", covers[0]);
        menuList.add(a);

        a = new MenuCard("VERSEMENT", covers[1]);
        menuList.add(a);

        a = new MenuCard("RETRAIT", covers[2]);
        menuList.add(a);

        a = new MenuCard("TRANSFERT D'ARGENT", covers[3]);
        menuList.add(a);

        a = new MenuCard("DEMANDE DE CREDIT", covers[4]);
        menuList.add(a);

        a = new MenuCard("VIREMENT", covers[5]);
        menuList.add(a);

        a = new MenuCard("CHANGE", covers[6]);
        menuList.add(a);

        adapter.notifyDataSetChanged();
    }

    @Override
    public void run() {
        switch (OperationType) {
            case 1:
                try {
                    Safe.ListPays = WsCommunicator.ListPays(main);
                    Safe.ListNaturePieceId = WsCommunicator.ListNaturePieceId(main);
                    Safe.ListSitMatrim = WsCommunicator.ListSitMatrim(main);
                    Safe.ListStatut = WsCommunicator.ListStatut(main);
                    Safe.ListActivite = WsCommunicator.ListActivite(main);
                    //Safe.ListTypeIntervenant = WsCommunicator.ListTypeIntervenant(thisObject);
                    Safe.ListProfession = WsCommunicator.ListProfession(main);
                    Safe.ListDevise = WsCommunicator.ListDevise(main);
                    Safe.ListPeriodicite = WsCommunicator.ListPeriodicite(main);
                    Safe.ListSecteurActiv = WsCommunicator.ListSecteurActiv(main);
                    Safe.ListLocal = WsCommunicator.ListLocalite(main);
                    Safe.ListObjets = WsCommunicator.ListObjet(main);
                    Safe.ListTypeEngagement = WsCommunicator.ListTypeEngagement(main);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
