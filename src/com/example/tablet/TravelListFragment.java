package com.example.tablet;

import java.util.Date;

import com.actionbarsherlock.app.SherlockListFragment;

public class TravelListFragment extends SherlockListFragment {

	public void onStart() {
		super.onStart();
		String[] events = getResources().getStringArray(R.array.EventiFuffa);
		TitledItem[] items = new TitledItem[events.length];

		int i = 0;
		for (String s : events) {
			String[] itms = s.split(",");
			Date d = new Date(Date.parse(itms[0]));
			SavedEvent e = new SavedEvent(d, itms[1]);
			items[i++] = new EventItem(e);
		}

		TitledAdapter adapter = new TitledAdapter(getSherlockActivity(), items);
		//ListView listView = (ListView) getSherlockActivity().findViewById(R.id.listViewEventi);
		setListAdapter(adapter);

	}
}
