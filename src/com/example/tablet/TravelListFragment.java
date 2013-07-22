package com.example.tablet;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.example.data.DatabaseHelper;
import com.example.data.Travel;

public class TravelListFragment extends SherlockListFragment {

	public void onStart() {
		super.onStart();
		
		DatabaseHelper db = DatabaseHelper.getInstance(getActivity().getApplicationContext());
		try {
			final List<Travel> travels = db.getTravels();
			TitledItem[] items = new TitledItem[travels.size()];
			
			int i = 0;
			for (Travel t : travels) {
				Date d = t.getData();
				SavedEvent e = new SavedEvent(d, t.getName());
				items[i++] = new EventItem(e);
			}
			
			TitledAdapter adapter = new TitledAdapter(getSherlockActivity(), items);
			//ListView listView = (ListView) getSherlockActivity().findViewById(R.id.listViewEventi);
			setListAdapter(adapter);
		
		ListView listView = getListView();
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Travel selected = travels.get(position);
								
				Intent intent = new Intent();
				intent.setClass(getActivity(), TravelSavedActivity.class);
				intent.putExtra("travel", selected);
				
				startActivity(intent);
			}

		});
		
		
		} catch (SQLException e1) {
			//TODO: toast
			e1.printStackTrace();
		}
//		String[] events = getResources().getStringArray(R.array.EventiFuffa);
//		TitledItem[] items = new TitledItem[events.length];
//
//		int i = 0;
//		for (String s : events) {
//			String[] itms = s.split(",");
//			Date d = new Date(Date.parse(itms[0]));
//			SavedEvent e = new SavedEvent(d, itms[1]);
//			items[i++] = new EventItem(e);
//		}
//
//		TitledAdapter adapter = new TitledAdapter(getSherlockActivity(), items);
//		//ListView listView = (ListView) getSherlockActivity().findViewById(R.id.listViewEventi);
//		setListAdapter(adapter);

	}
}
