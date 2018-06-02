package com.example.edisonoffice.backgroun_uploading_service;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.edisonoffice.backgroun_uploading_service.BackgroundService.flag;
import static com.example.edisonoffice.backgroun_uploading_service.BackgroundService.socket;

public class MainActivity extends AppCompatActivity {
    EditText et_name, et_referby, et_clientid;
    Button submit;
    String namee, referby;
    Integer clientid;
    public  String deviceUniqueIdentifier;
    DataBaseHandler_new db = null;
    private SQLiteDatabase dataBase;
    public static ArrayList<String> imei_array = new ArrayList<String>();
    public ArrayList<String> namearr = new ArrayList<String>();
    public ArrayList<String> refearr = new ArrayList<String>();
    public ArrayList<String> cliarr = new ArrayList<String>();
    private int rows;
    String token;
    public static String mes_conected;
    public static String outMsg;
    static String name_str, refer_str, imei_str, client_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_name = (EditText) findViewById(R.id.edit_name);
        et_referby = (EditText) findViewById(R.id.edit_refered_by);
        et_clientid = (EditText) findViewById(R.id.edit_client_id);

        submit = (Button) findViewById(R.id.buton_submit);

        imei_array.clear();

        db = new DataBaseHandler_new(MainActivity.this);
        db.getWritableDatabase();
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
        String mPhoneNumber = tMgr.getLine1Number();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceUniqueIdentifier = telephonyManager.getDeviceId();
        Toast.makeText(MainActivity.this, deviceUniqueIdentifier+"phone number"+mPhoneNumber, Toast.LENGTH_LONG).show();
        Log.d("TAG", deviceUniqueIdentifier);

        rows = db.numberOfRows();
        if (rows <= 0) {
            db.insertDataAdmin("abc", "def", "3214567891", "0");
        }
      if(rows==2){
          Intent i = new Intent(MainActivity.this,Second.class);
          startActivity(i);
          finish();
      }


        dataBase = db.getWritableDatabase();
        Cursor mCursor = dataBase.rawQuery("SELECT * FROM "
                + DataBaseHandler_new.TABLE_NAME_1, null);

        try {

            if (mCursor.moveToFirst()) {
                do {

                   // imei_array.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.IMEI)));
                  //  Log.d("TAG", String.valueOf(imei_array));

                    imei_array.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.IMEI)));
                    namearr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.NAME)));
                    cliarr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.CLIENT_ID)));
                    refearr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.REFERED_BY)));
                    Log.d("TAG",imei_array+"|"+namearr+"|"+cliarr+"|"+refearr );


                } while (mCursor.moveToNext());
                //do above till data exhausted
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            Log.d("TAG", ee.toString());
        }


       /* try {

            name_str = null;
            refer_str = null;
            client_str = null;
            imei_str = null;
            if (deviceUniqueIdentifier != null) {
                //cursor retriving the admin data
                Cursor rs = db.getData1(deviceUniqueIdentifier);
                if (rs.getCount() > 0) {
                    name_str = rs.getString(rs.getColumnIndex(DataBaseHandler_new.NAME));
                    refer_str = rs.getString(rs.getColumnIndex(DataBaseHandler_new.REFERED_BY));
                    client_str = rs.getString(rs.getColumnIndex(DataBaseHandler_new.CLIENT_ID));
                    imei_str = rs.getString(rs.getColumnIndex(DataBaseHandler_new.IMEI));
                }
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            Log.d("TAG", ee.toString());
        }
*/



      /*  if (flag == 0) {
            if (socket == null) {
                //   Toast.makeText(MainActivity.this, "service starting  ", Toast.LENGTH_LONG).show();
                Log.d("TAG", "service starting " + "'");
                startService(new Intent(MainActivity_New.this, BackgroundService.class));
            }
        }*/


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t = new Thread() {
                    public void run() {
                        token = "submit";
                        namee = et_name.getText().toString();
                        referby = et_referby.getText().toString();
                        clientid = Integer.valueOf(et_clientid.getText().toString());

                       /* if (imei_array.contains(deviceUniqueIdentifier)) {
                            Intent  i = new Intent(MainActivity_New.this,Second.class);
                            startActivity(i);

                        }else*/


                        if (namee != null && referby != null && clientid != null && !imei_array.contains(deviceUniqueIdentifier)) {
                            outMsg = "*" + namee + ";" + referby + ";" + deviceUniqueIdentifier + ";" + clientid + "#";

                            if (flag == 0) {
                                if (socket == null) {
                                    // for(int i = 0;i<imei_array.size();i++){

                                    if (!imei_array.contains(deviceUniqueIdentifier)) {

                                        db.insertDataAdmin(namee, referby, deviceUniqueIdentifier, String.valueOf(clientid));
                                        // startService(new Intent(MainActivity_New.this, BackgroundService.class));
                                    }

                                    //   }
                                    Log.d("TAG", "service starting " + "'");
                                    startService(new Intent(MainActivity.this, BackgroundService.class));

                                }
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Toast.makeText(MainActivity.this, "Please check credentials properly ", Toast.LENGTH_LONG).show();

                                }
                            });
                        }

                        /*}else{

                            //startService(new Intent(MainActivity.this, BackgroundService.class));

                            Toast.makeText(MainActivity.this, "socket connection closed ", Toast.LENGTH_LONG).show();

                        }
*/

                    }
                };
                t.start();

            }
        });


        dataBase = db.getWritableDatabase();
        Cursor mCursor1 = dataBase.rawQuery("SELECT * FROM "
                + DataBaseHandler_new.TABLE_NAME, null);

        try {

            if (mCursor1.moveToFirst()) {
                do {

                    imei_array.add(mCursor1.getString(mCursor1.getColumnIndex(DataBaseHandler_new.IMEI)));
                    namearr.add(mCursor1.getString(mCursor1.getColumnIndex(DataBaseHandler_new.NAME)));
                    cliarr.add(mCursor1.getString(mCursor1.getColumnIndex(DataBaseHandler_new.CLIENT_ID)));
                    refearr.add(mCursor1.getString(mCursor1.getColumnIndex(DataBaseHandler_new.REFERED_BY)));
                    Log.d("TAG",imei_array+"|"+namearr+"|"+cliarr+"|"+refearr );

                } while (mCursor1.moveToNext());
                //do above till data exhausted
            }
        } catch (Exception ee) {
            ee.printStackTrace();
            Log.d("TAG", ee.toString());
        }




    }







}
