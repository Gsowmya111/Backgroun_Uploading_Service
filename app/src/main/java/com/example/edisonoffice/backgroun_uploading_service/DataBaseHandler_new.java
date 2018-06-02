package com.example.edisonoffice.backgroun_uploading_service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;


public class DataBaseHandler_new extends SQLiteOpenHelper {
    //database name initialising
    public static final String DATABASE_NAME = "servicestable.db";
    //databse version initialisng
    public static final String DATABASE_VERSION = "1";
    public static final String TABLE_NAME = "Details_table";
    public static final String TABLE_NAME_1 = "Vps_table";

//creating ID's/columns to table
//creating ID's/columns to details_table

    public static final String IDS = "ID";
    public static final String CLIENT_ID_S= "TIME";
    public static final String CLIENT_IP = "CLIENT_IP";
    public static final String DATE_BYME = "DATE_BYME";
    public static final String DATE_BY_THEM = "DATE_BY_THEM";
    public static final String MESSAGE= "MESSAGE";
    public static final String ACK= "ACK";



    public static final String ID = "ID";
    public static final String NAME = "NAME";
    public static final String REFERED_BY = "REFERED_BY";
    public static final String IMEI = "IMEI";
    public static final String CLIENT_ID = "TIME";



    public static SQLiteDatabase db = null;

    //creating table
   private String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" + IDS + " INTEGER PRIMARY KEY , " +
           CLIENT_ID_S + " TEXT ," + CLIENT_IP + " TEXT ," + DATE_BYME + " TEXT ," +
            DATE_BY_THEM + " TEXT ," + MESSAGE + " TEXT ," + ACK + " TEXT " + ");";




    private String CREATE_TABLE_1 = "CREATE TABLE " + TABLE_NAME_1 + "(" + ID + " INTEGER PRIMARY KEY , " +
            NAME + " TEXT ," + REFERED_BY + " TEXT ," + IMEI + " TEXT ," + CLIENT_ID + " TEXT " + ");";




    //default constructor
    public DataBaseHandler_new(Context context) {
        super(context, DATABASE_NAME, null, 1);

      //  mcontext = context;
      //  DATABASE_PATH = "/data/data/" + mcontext.getPackageName() + "/databases/";

    }



    @Override
    public void onCreate(SQLiteDatabase db) {
     //   db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,USERNAME TEXT,PASSWORD TEXT,MOBILENO TEXT,LOGIN_TYPE TEXT)");

        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE_1);
        Log.d("TAG","DATABASE TABLE1 CREATED");

    }
//updating the database to new version if old version exist
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        onCreate(db);
    }


///inserting data into table
public  boolean insertDataAdmin_upload(String clientip,String clientid,String datebyme,String datebythem,String message,String ack ) {
    SQLiteDatabase db = this.getWritableDatabase();
    ContentValues contentValues1 = new ContentValues();
    //  contentValues1.put(ID, id);

    contentValues1.put(CLIENT_IP, clientip);
    contentValues1.put(CLIENT_ID, clientid);
    contentValues1.put(DATE_BYME, datebyme);
    contentValues1.put(DATE_BY_THEM, datebythem);
    contentValues1.put(MESSAGE, message);
    contentValues1.put(ACK, ack);
    //  long result = db.insertWithOnConflict(TABLE_NAME, null, contentValues1,SQLiteDatabase.CONFLICT_REPLACE);
    boolean result = db.insert(TABLE_NAME, null, contentValues1)>0;
    return  result;
        /*if (result == -1)
            return false;
        else
            return true;*/
}


    //inserting data into table
    public boolean insertDataAdmin(String name, String refer, String imei, String client) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues1 = new ContentValues();
        //  contentValues1.put(ID, id);
        contentValues1.put(NAME, name);
        contentValues1.put(REFERED_BY, refer);
        contentValues1.put(IMEI, imei);
        contentValues1.put(CLIENT_ID, client);
        //  long result = db.insertWithOnConflict(TABLE_NAME, null, contentValues1,SQLiteDatabase.CONFLICT_REPLACE);
        boolean result = db.insert(TABLE_NAME_1, null, contentValues1) > 0;
        return result;
        /*if (result == -1)
            return false;
        else
            return true;*/
    }




//getting number of rows from table
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db,TABLE_NAME_1);
        return numRows;
    }

    public int numberOfRows1(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db,TABLE_NAME);
        return numRows;
    }
//getting data basedon username
    public Cursor getData(String clientid) {
        clientid="'"+clientid+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where CLIENT_ID="+clientid, null );
        if (res != null)
            res.moveToFirst();

        return res;
    }

    public Cursor getDataa(int id) {
        id= Integer.parseInt("'"+id+"'");
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where ID="+id, null );
        if (res != null)
            res.moveToFirst();

        return res;
    }


    //getting data based on logintype
    public Cursor getData1(String mobil) {
        mobil="'"+mobil+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where IMEI="+mobil, null );
        if (res != null)
            res.moveToFirst();

        return res;
    }

    public Cursor getDatalogintype(String type) {
        type="'"+type+"'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TABLE_NAME+" where LOGIN_TYPE="+type, null );
        if (res != null)
            res.moveToFirst();

        return res;
    }



//updating database
    public boolean updateData(String ackmesage, String clientit) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
       // contentValues.put(COL_1, id);
        contentValues.put(ACK, ackmesage);
        contentValues.put(CLIENT_IP, clientit);

     //   long result1 = db.insertWithOnConflict(TABLE_NAME, null, contentValues,SQLiteDatabase.CONFLICT_REPLACE);
        db.update(TABLE_NAME, contentValues, "CLIENT_IP = ?", new String[]{clientit});
        return true;
    }

    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }



    public static String getSinlgeEntry(String userName)
    {
        Cursor cursor=db.query("kaapi_table", null, " USERNAME=?", new String[]{userName}, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String password= cursor.getString(cursor.getColumnIndex("PASSWORD"));
        cursor.close();
        return password;
    }
    public static String getSinlgeEntry1(String id)
    {
        Cursor cursor=db.query("kaapi_table", null, " PASSWORD=?", new String[]{id}, null, null, null);
        if(cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }
        cursor.moveToFirst();
        String unam= cursor.getString(cursor.getColumnIndex("ID"));
        cursor.close();
        return unam;
    }
}