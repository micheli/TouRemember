//package com.example.database;
//
//public class DBHelper extends SQLiteOpenHelper {
//    
//   private static final String DATABASE_NAME = "touRemember.db";
//   private static final int DATABASE_VERSION = 1;
//
//   // Lo statement SQL di creazione del database
//   private static final String DATABASE_CREATE = "create stop  (_id integer primary key autoincrement, latitude float not null, longitude float not  null, url_image text not null);";
//
//   // Costruttore
//   public DatabaseHelper(Context context) {
//           super(context, DATABASE_NAME, null, DATABASE_VERSION);
//   }
//
//   // Questo metodo viene chiamato durante la creazione del database
//   @Override
//   public void onCreate(SQLiteDatabase database) {
//           database.execSQL(DATABASE_CREATE);
//   }
//
//   // Questo metodo viene chiamato durante l'upgrade del database, ad esempio quando viene incrementato il numero di versione
//   @Override
//   public void onUpgrade( SQLiteDatabase database, int oldVersion, int newVersion ) {
//            
//           database.execSQL("DROP TABLE IF EXISTS touRemember");
//           onCreate(database);
//            
//   }
//}
/////////////////