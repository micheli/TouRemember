package com.example.data;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static DatabaseHelper instance;
	
	private static final String DATABASE_NAME = "travels.db";
	private static final int DATABASE_VERSION = 2;

	private Dao<Travel, Integer> travelDao;
	private Dao<TravelPoint, Integer> travelPointDao;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	static {
		instance = null;
	}
	
	public static DatabaseHelper getInstance(Context c) {
		if (instance == null) {
			instance = new DatabaseHelper(c.getApplicationContext());
		}
		return instance;
	}
	
	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Travel.class);
			TableUtils.createTable(connectionSource, TravelPoint.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to create datbases", e);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVer, int newVer) {
		try {
			TableUtils.dropTable(connectionSource, Travel.class, true);
			TableUtils.dropTable(connectionSource, TravelPoint.class, true);
			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVer + " to new "
					+ newVer, e);
		}
	}

	public Dao<Travel, Integer> getTravelDao() throws SQLException {
		if (travelDao == null) {
			travelDao = getDao(Travel.class);
		}
		return travelDao;
	}

	public Dao<TravelPoint, Integer> getTravelPointDao() throws SQLException {
		if (travelPointDao == null) {
			travelPointDao = getDao(TravelPoint.class);
		}
		return travelPointDao;
	}
	
	public void createTravel(Travel t) throws SQLException { 
		getTravelDao().create(t);
		for (TravelPoint p : t.getPoints()) {
			getTravelPointDao().create(p);
		}
	}

	public List<Travel> getTravels() throws SQLException {
		List<Travel> res = new LinkedList<Travel>();
		for(Travel t : getTravelDao()) {
			deserializeTravel(t);
			res.add(t);
		}
		
		return res;
	}

	private void deserializeTravel(Travel t) throws SQLException {
		List<TravelPoint> res = getTravelPointDao().query(getTravelPointDao().queryBuilder().orderBy("order", true).where().eq("travel_id", t.getIdTravel()).prepare());
		for (TravelPoint p : res) {
			t.addPoint(p);
		}		
	}
}
