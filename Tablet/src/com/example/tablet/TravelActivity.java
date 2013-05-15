package com.example.tablet;

import android.os.Bundle;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

public class TravelActivity extends SherlockFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final ActionBar ab = getSupportActionBar();
		ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		ab.setHomeButtonEnabled(true);
		ab.setDisplayHomeAsUpEnabled(true);

		/** TabHost will have Tabs */
		String tab1_txt = getResources().getString(R.string.saved_travel);

		Tab tab1 = ab
				.newTab()
				.setText(tab1_txt)
				.setTabListener(
						new TabListener<TravelListFragment>(this, "tab2",
								TravelListFragment.class));
		ab.addTab(tab1);
		
	}

	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
		// TODO Auto-generated method stub
		com.actionbarsherlock.view.MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.travel, menu);
		return super.onCreateOptionsMenu(menu);
	}

}
