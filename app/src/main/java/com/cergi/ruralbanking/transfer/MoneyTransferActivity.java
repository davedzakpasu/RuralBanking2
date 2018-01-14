package com.cergi.ruralbanking.transfer;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.support.v7.widget.SearchView;

import com.cergi.ruralbanking.R;

public class MoneyTransferActivity extends AppCompatActivity {
	MoneyTransferActivity thisObject;
	
	//ProgressBar pb;
	ViewPager viewPager;
	Toolbar toolbar;
    TabLayout tabLayout;
	
	private TabsPagerAdapterMT tpAdapter;
	
	String[] tabs = { "Envoi", "Retrait"};
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.cpt).setVisible(false);
		SearchManager srcManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView srcView = (SearchView) menu.findItem(R.id.search).getActionView();
		srcView.setSearchableInfo(srcManager.getSearchableInfo(getComponentName()));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onCreate(Bundle b) {
		thisObject = this;
		super.onCreate(b);
		setContentView(R.layout.money_transfer);

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		//
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// Set Home Button Enabled
		//getSupportActionBar().setHomeButtonEnabled(true);

		// Add subtitle
		getSupportActionBar().setSubtitle(R.string.money_transfer);

		viewPager = (ViewPager) findViewById(R.id.pagerMT);
        viewPager.setAdapter(new TabsPagerAdapterMT(thisObject, getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });
	}

    public class TabsPagerAdapterMT extends FragmentPagerAdapter {
        Context context;
        final int PAGE_COUNT = 2;

        public TabsPagerAdapterMT(Context context, FragmentManager fm) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case 0:
                    return SendingFragment.newInstance();
                case 1:
                    return ReceivingFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }
    }

}
