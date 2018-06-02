package com.example.edisonoffice.backgroun_uploading_service;

/**
 * Created by edison office on 5/3/2018.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.CharBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.edisonoffice.backgroun_uploading_service.DataBaseHandler_new.CLIENT_ID;
import static com.example.edisonoffice.backgroun_uploading_service.DataBaseHandler_new.CLIENT_IP;
import static com.example.edisonoffice.backgroun_uploading_service.DataBaseHandler_new.TABLE_NAME;
import static com.example.edisonoffice.backgroun_uploading_service.MainActivity.outMsg;


public class BackgroundService extends Service {

    public Context context = this;
    public Handler handler = null;
    public static Runnable runnable = null;
    int TCP_SERVER_PORT = 8888;
    static String c;
    static String message;
    String one, two, three, four, five, six;
    InetAddress serverAddr;
    public static final String SERVERIP = "192.168.0.6";
    public static final int SERVERPORT = 9599;
    PrintWriter out;
    public static Socket socket;
    String deviceUniqueIdentifier;
    public Socket s;
    public BufferedReader in;
    public BufferedWriter outbuf;
    private boolean mRun = false;
    public static PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;
    private String mes_tosend;
    private String mServerMessage;
    private OnMessageReceived mMessageListener = null;
    static int flag;
    public static String inMsg;
    int rows;
    DataBaseHandler_new db = null;

    private SQLiteDatabase dataBase;
    private String imei;

    private ArrayList<String> refer_arr = new ArrayList<String>();
    ArrayList<String> name_arr = new ArrayList<String>();
    ArrayList<String> client_arr = new ArrayList<String>();
    ArrayList<String> imei_arr = new ArrayList<String>();


    private ArrayList<String> client_ip_arr = new ArrayList<String>();
    ArrayList<String> message_arr = new ArrayList<String>();
    ArrayList<String> datebyme_arr = new ArrayList<String>();
    ArrayList<String> clientid_arr = new ArrayList<String>();
    ArrayList<String> ack_arra = new ArrayList<String>();

    private String dateToStr;
    private String serverMessage;
    Calendar calendar;
    SimpleDateFormat mdformat;
    String strDate;
    private Cursor mCursor;
    private String clientid;

    private String[] clientids_stringarray = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};


    private String[] time_sending_array1 = {"11:10", "11:25", "11:40", "11:55", "12:10", "12:25", "12:40", "12:55", "13:10", "13:25", "13:40", "13:55", "14:10", "14:25", "14:40",
            "14:55", "15:10", "15:25", "15:40", "15:55", "16:10", "16:25", "16:40", "16:55", "17:10", "17:25", "17:40", "17:55", "18:10", "18:25",};


    private String[] time_sending_array = {"15:27", "11:30", "11:45", "12:00", "12:15", "12:30", "12:45", "13:00", "13:15", "13:30", "13:45", "14:00", "14:15", "14:30", "14:45",
            "15:09", "15:15", "15:18", "15:45", "16:00", "16:15", "16:30", "16:45", "17:00", "17:15", "17:30", "17:45", "18:00", "18:15", "18:30"};


    private String[] time_sending_array2 = {"11:20", "11:35", "11:50", "12:05", "12:20", "12:35", "12:50", "13:05", "13:20", "13:35", "13:50", "14:05", "14:20", "14:35", "14:50",
            "15:05", "15:20", "15:35", "15:50", "16:05", "16:20", "16:35", "16:50", "17:05", "17:20", "17:35", "17:50", "18:05", "18:20", "18:35"};

    private String[] time_sending_array3 = {"11:15", "11:30", "11:45", "12:00", "12:15", "12:30", "12:45", "13:00", "13:15", "13:30", "13:45", "14:00", "14:15", "14:30", "14:45",
            "15:00", "15:15", "15:30", "15:45", "16:00", "16:15", "16:30", "16:45", "17:00", "17:15", "17:30", "17:45", "18:00", "18:15", "18:30"};




    private String mPhoneNumber="9876543212";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        //  Toast.makeText(this, "Service created!", Toast.LENGTH_LONG).show();
        db = new DataBaseHandler_new(BackgroundService.this);
        db.getWritableDatabase();
        name_arr.clear();
        client_arr.clear();
        imei_arr.clear();
        refer_arr.clear();




        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss a");
        dateToStr = format.format(today);



        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        deviceUniqueIdentifier = telephonyManager.getDeviceId();
     //   mPhoneNumber = telephonyManager.getLine1Number();
      //  Log.d("TAG", mPhoneNumber);
        Log.d("TAG", deviceUniqueIdentifier);
        Runnable timeconnect = new timecheck();
        new Thread(timeconnect).start();


    }

    @Override
    public void onStart(Intent intent, int startid) {
        //  Toast.makeText(this, "Service started by user.", Toast.LENGTH_LONG).show();

    }

    public void sendMessage(String message) {

        Log.d("TAG", "iniated send message method ");
        if (mBufferOut != null && !mBufferOut.checkError()) {
            //  mBufferOut.println(message);
            mBufferOut.write(message);
            mBufferOut.flush();
            Log.d("TAG", "sendmessage text " + message);


        }
    }

    public BackgroundService(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    public BackgroundService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println("I am in on start");
        //  Toast.makeText(this,"Service created ...", Toast.LENGTH_LONG).show();
        Runnable connect = new connectSocket();
        new Thread(connect).start();

        //write one more connection socket


        return START_STICKY;
    }


    public class connectSocket implements Runnable {

        public void run() {

            mRun = true;


            try {
                //    socket = new Socket("192.168.15.62", 8888);

                Log.d("TAG", "socket closed" + "'");
                socket = new Socket("101.53.131.42", 8888);
                Log.d("TAG", " socket again connecting....");
                //   Toast.makeText(getApplicationContext(), "Socket trying to connect ...", Toast.LENGTH_LONG).show();

                try {
                    //sends the message to the server
                    mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    //receives the message which the server sends back
                    mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    flag = 1;

                    Log.d("TAG", " socket again connecting....2");
                    dataBase = db.getWritableDatabase();
                    Cursor mCursor = dataBase.rawQuery("SELECT * FROM "
                            + DataBaseHandler_new.TABLE_NAME_1, null);
                    Log.d("TAG", " socket again connecting....3");
                    try {

                        if (mCursor.moveToFirst()) {
                            do {
                                imei = mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.IMEI));
                                Log.d("TAG", deviceUniqueIdentifier + " imei" + imei);
                                if (deviceUniqueIdentifier.equals(imei)) {

                                    name_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.NAME)));
                                    Log.d("TAG", " cursore");
                                    //  Toast.makeText(getApplicationContext(), "Cursor method ..." + name_arr, Toast.LENGTH_LONG).show();

                                    refer_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.REFERED_BY)));
                                    imei_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.IMEI)));
                                    client_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.CLIENT_ID)));
                                }
                            } while (mCursor.moveToNext());
                            //do above till data exhausted
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        Log.d("TAG", ee.toString());

                    }

                    //in this while the client listens for the messages sent by the server
                    if (socket != null) {
                        Log.d("TAG", "socket not null ");
                        for (int i = 0; i < name_arr.size(); i++) {
                            Log.d("TAG", " for loop" + name_arr);
                            if (deviceUniqueIdentifier.equals(imei_arr.get(i))) {
                                outMsg = "*" + name_arr.get(i) + ";" + refer_arr.get(i) + ";" + imei_arr.get(i) + ";" + client_arr.get(i) + "#";
                                Log.d("TAG", " for loop" + outMsg);
                            }
                        }
                        mes_tosend = outMsg;
                    }
                    if (mes_tosend != null)
                        mBufferOut.write(mes_tosend);
                    Log.d("TAG", "S: sending Message: '" + mes_tosend + "'");

                    mBufferOut.flush();
                    Log.d("TAG", "flush...");

                    while (mRun) {
                        inMsg = mBufferIn.readLine() + System.getProperty("line.separator");
                        mServerMessage = inMsg;

                        Log.d("TAG", "S: Received Message: '" + mServerMessage + "'");
                        String edittext_data = inMsg;
                        edittext_data = edittext_data.substring(1, edittext_data.length() - 1);
                        Log.d("TAG", "removing 1st and last char..." + edittext_data);

                        String splitTime1[] = edittext_data.split(";");
                        one = splitTime1[0];
                        //  String splitTime[] = one.split("(");
                        Log.d("TAG", "clientip..." + one);

                        two = splitTime1[1];
                        Log.d("TAG", "clientID..." + two);

                        three = splitTime1[2];
                        Log.d("TAG", "Date time..." + three);

                        String four1 = splitTime1[3];
                        String splitTime[] = four1.split("#");
                        four = splitTime[0];
                        Log.d("TAG", "message..." + four);

                        Log.d("TAG", "date time byme..." + dateToStr);


                        db.insertDataAdmin_upload(one, two, dateToStr, three, four, "no");
                        Log.d("TAG", "inserted data to database: '");




/*

                        Cursor Cursor = dataBase.rawQuery("SELECT * FROM "
                                + DataBaseHandler_new.TABLE_NAME, null);
                        Log.d("TAG", " after inster db");
                        try {

                            if (Cursor.moveToFirst()) {
                                do {

                                    String clientid = Cursor.getString(Cursor.getColumnIndex(DataBaseHandler_new.CLIENT_ID_S));
                                    if(clientid.equals("1")) {
                                        client_ip_arr.add(Cursor.getString(Cursor.getColumnIndex(DataBaseHandler_new.CLIENT_IP)));
                                        Log.d("TAG", " after inster db cursor" + client_ip_arr);
                                        //  Toast.makeText(getApplicationContext(), "Cursor method ..." + name_arr, Toast.LENGTH_LONG).show();
                                        clientid_arr.add(Cursor.getString(Cursor.getColumnIndex(DataBaseHandler_new.CLIENT_ID_S)));
                                        Log.d("TAG", " after inster db cursor" + clientid_arr);
                                        datebyme_arr.add(Cursor.getString(Cursor.getColumnIndex(DataBaseHandler_new.DATE_BYME)));
                                        Log.d("TAG", " after inster db cursor" + datebyme_arr);
                                        message_arr.add(Cursor.getString(Cursor.getColumnIndex(DataBaseHandler_new.MESSAGE)));
                                        Log.d("TAG", " after inster db cursor" + message_arr);
                                    }
                                } while (Cursor.moveToNext());

                                //do above till data exhausted
                            }
                            Log.d("TAG", client_ip_arr+"    "+clientid_arr+"  "+message_arr);
                        } catch (Exception ee) {
                            ee.printStackTrace();
                            Log.d("TAG", ee.toString());

                        }
*/



                    }

                } catch (Exception e) {
                    Log.d("TAG", "S: internet error: '" + e + "'");
                    onDestroy();

                }
            } catch (Exception e) {
                // socket_conect();
                Log.d("TAG", "S:  error:trying to connect again '" + e + "'");
                socket_conect();
                Log.d("TCP", "C: Error", e);
            }

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            flag = 0;
            socket.close();
            Log.d("TAG", "socket closed" + "'");


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        socket = null;
    }

    public interface OnMessageReceived {
        public void messageReceived(String message);
    }


    public void socket_conect() {
        try {

            Log.d("TAG", "socket connect method conneting socket" + "'");
            socket = new Socket("101.53.131.42", 8888);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public class timecheck implements Runnable {

        private int hour, minute, second;

        public void run() {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    calendar = Calendar.getInstance();
                    mdformat = new SimpleDateFormat("HH:mm");
                    strDate = mdformat.format(calendar.getTime());


                   /* for(int i = 0;i<client_ip_arr.size();i++){
                        if(strDate.equals(time_sending_array[i])){
                            Thread t =new Thread(){
                                public void run(){

                                    *//*final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    }, 5000)*//*;



                                    runTcpClient_tcp();
                                }
                            };t.start();
                        }
                    }
*/


                    if(strDate.equals("15:47")){
                     //   runTcpClient();

                        Thread t =new Thread(){
                            public void run(){

                                runTcpClient_tcp();
                            }
                        };t.start();


                    }


                 /*   if (clientids_stringarray[0].equals("1") && strDate.equals(time_sending_array[0])) {
                        runTcpClient();
                    } else if (clientids_stringarray[1].equals("2") && strDate.equals(time_sending_array[1])) {
                        runTcpClient();
                    }

                    if(strDate.equals("2:04")&& clientids_stringarray[0]=="1") {
                        Log.d("TAG","1clien..");
                        runTcpClient();
                        Log.d("TAG","1..");
                    }else   if(strDate.equals("1:58")&& clientids_stringarray[1]=="2") {
                        Log.d("TAG","2clien..");
                        runTcpClient();
                        Log.d("TAG","2..");
                    }else   if(strDate.equals("2:10")&& clientids_stringarray[1]=="3") {
                        Log.d("TAG","3clien..");
                        runTcpClient();
                        Log.d("TAG","3..");
                    }else   if(strDate.equals("16:02")&& clientids_stringarray[3]=="4") {
                        Log.d("TAG","4clien..");
                        runTcpClient();
                        Log.d("TAG","4.");
                    }else   if(strDate.equals("2:20")&& clientids_stringarray[1]=="5") {
                        Log.d("TAG","5clien..");
                        runTcpClient();
                        Log.d("TAG","5..");
                    }
                    Log.d("TAG", "current time" + strDate);
*/
/*

                    for (int i = 0; i < clientids_stringarray.length; i++) {

                        String string1 = time_sending_array1[i];
                        //   String string1 = "14:50";
                        Date time1 = null;
                        try {
                            time1 = new SimpleDateFormat("HH:mm").parse(string1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(time1);


                        String string2 = time_sending_array2[i];
                        //  String string2 = "18:50";
                        Date time2 = null;
                        try {
                            time2 = new SimpleDateFormat("HH:mm").parse(string2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.setTime(time2);
                        calendar2.add(Calendar.DATE, 1);

                        String someRandomTime = time_sending_array[i];
                        // String someRandomTime = "14:50";
                        Date d = null;
                        try {
                            d = new SimpleDateFormat("HH:mm").parse(someRandomTime);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Calendar calendar3 = Calendar.getInstance();
                        calendar3.setTime(d);
                        calendar3.add(Calendar.DATE, 1);

                        Date x = calendar3.getTime();
                        if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())&& clientids_stringarray[i].equals(cli)) {
                            //checkes whether the current time is between 14:49:00 and 20:11:13.
                            runTcpClient();
                        }
                    }
*/

                      /*  if (clientids_stringarray[i].equals("1") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            Log.d("TAG", "client 1");
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("2") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            Log.d("TAG", "client 2");
                            runTcpClient();
                        }

                        if (clientids_stringarray[i].equals("3") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }

                        if (clientids_stringarray[i].equals("4") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }

                        if (clientids_stringarray[i].equals("5") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("6") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("7") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("8") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            Log.d("TAG", "client 8");
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("9") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            Log.d("TAG", "client 9");
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("10") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            Log.d("TAG", "client 10");
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("11") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            Log.d("TAG", "client 11");
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("12") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            Log.d("TAG", "client 12");
                            runTcpClient();
                        }

                        if (clientids_stringarray[i].equals("13") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            Log.d("TAG", "client 13");
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("14") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            Log.d("TAG", "client 14");
                            runTcpClient();
                        }

                        if (clientids_stringarray[i].equals("15") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("16") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("17") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("18") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("19") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("20") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("21") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("22") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("23") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("24") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("25") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("26") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("27") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("28") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("29") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }
                        if (clientids_stringarray[i].equals("30") && x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                            runTcpClient();
                        }


                    }*//*
*/
                  //  }
                }
            }, 100, 50000);

            }



    }





public void  fetchdata(){

    }














    private void runTcpClient_tcp() {
        mRun=true;
        int rows_count = db.numberOfRows1();
        Log.i("TAG", "Tcp client connected ............ " );
        try {
            Log.i("TAG", "Tcp client connected ............ " );
            // Creating new socket connection to the IP (first parameter) and its opened port (second parameter)
            // Socket s = new Socket("192.168.0.39", TCP_SERVER_PORT);
            Socket s = new Socket("101.53.131.42", 4444);
            Log.i("TAG", "Tcp client connected ............ " );

            // Initialize output stream and input stream to write and read message to the socket stream
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            Log.i("TAG", "Tcp client connected ............1 " );

            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            Log.i("TAG", "Tcp client connected ............2 " );

            //send output msg

                try {

                    dataBase = db.getWritableDatabase();
                    Cursor Cursor = dataBase.rawQuery("SELECT * FROM "
                            + DataBaseHandler_new.TABLE_NAME, null);
                    Log.d("TAG", " after inster db");
                    try {

                        if (Cursor.moveToFirst()) {
                            do {

                                String clientid = Cursor.getString(Cursor.getColumnIndex(DataBaseHandler_new.CLIENT_ID_S));
                             //   if(clientid.equals("1")) {
                             //   for(int i =0;i<=rows_count;i++){
                                  //  Log.d("TAG", " for loop "+i);
                                    client_ip_arr.add(Cursor.getString(Cursor.getColumnIndex(DataBaseHandler_new.CLIENT_IP)));
                                    Log.d("TAG", " for loop "+ client_ip_arr);
                                    //  Toast.makeText(getApplicationContext(), "Cursor method ..." + name_arr, Toast.LENGTH_LONG).show();
                                    clientid_arr.add(Cursor.getString(Cursor.getColumnIndex(DataBaseHandler_new.CLIENT_ID_S)));
                                    Log.d("TAG", " for loop "+  clientid_arr);
                                    datebyme_arr.add(Cursor.getString(Cursor.getColumnIndex(DataBaseHandler_new.DATE_BYME)));
                                    Log.d("TAG", " for loop "+  datebyme_arr);
                                    message_arr.add(Cursor.getString(Cursor.getColumnIndex(DataBaseHandler_new.MESSAGE)));
                                    Log.d("TAG", " for loop "+  message_arr);
                            //    }
                            } while (Cursor.moveToNext());

                            //do above till data exhausted
                        }
                       // Log.d("TAG", client_ip_arr+"    "+clientid_arr+"  "+message_arr);
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        Log.d("TAG", ee.toString());

                    }




                    for (int i = 0; i < client_ip_arr.size(); i++) {

                 //   for (int i = 0; i < time_sending_array.length; i++) {
                     //   if (clientids_stringarray[i].equals(clientid) && strDate.equals(time_sending_array[i])) {
                            Log.d("TAG", " for loop....for sending" + i);
                            outMsg = client_ip_arr.get(i) + "*" + datebyme_arr.get(i) + "*" + clientid_arr.get(i) + "*" + message_arr.get(i) + "*" + name_arr.get(i) + "*" + mPhoneNumber;
                            //  outMsg = "edison" + "*" + "password" + "*" + "1.2" + "*" + "12" + "*" + "nsjans" + "*" + "bwbs"; //+ System.getProperty("line.separator");


                            Log.d("TAG", " for loop" + outMsg);

                            PrintWriter p = new PrintWriter(out);
                            //write message to stream
                            out.write(outMsg);
                            //flush the data to indicate that end of message
                            out.flush();


                            Log.d("TAG", "sent: " + outMsg);
                            //accept server response
                            String inMsg = in.readLine() + System.getProperty("line.separator");
                            Log.d("TAG", "received: " + inMsg);
                            if(inMsg.equals("Ok")){
                                db.updateData("YES", client_ip_arr.get(i));
                            }

                        }
                  //  }
                /* while (mRun) {

                inMsg = mBufferIn.readLine() + System.getProperty("line.separator");
                mServerMessage = inMsg;
                Log.d("TAG", "Socket 2: Received Message: '" + mServerMessage + "'");
               *//* if (mServerMessage.equals("Ok")) {
                    Log.d("TAG", "Socket 2: update method: '" + mes_tosend + "'");
                    //   db.updateData("YES", client_ip_arr.get(i));
                    Log.d("TAG", "Socket 2: updated success: '" + mes_tosend + "'");
                }*//*
                 }*/


                }catch (Exception e){
                    e.printStackTrace();
                }
            //close connection
            s.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }















    public void  runTcpClient() {
        try {
            socket = new Socket("101.53.131.42", 4444);
            Log.d("TAG", " socket 2 connection started");
            client_ip_arr.clear();clientid_arr.clear();message_arr.clear();datebyme_arr.clear();ack_arra.clear();
            mRun = true;
            try {

                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                flag = 1;

                Log.d("TAG", " socket 2 ....1");

                dataBase = db.getWritableDatabase();
                Cursor mCursor = dataBase.rawQuery("SELECT * FROM "
                        + DataBaseHandler_new.TABLE_NAME, null);
                Log.d("TAG", " socket 2 ....2");
                try {

                    if (mCursor.moveToFirst()) {
                        do {

                            client_ip_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.CLIENT_IP)));
                            clientid_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.CLIENT_ID)));
                            message_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.MESSAGE)));
                            datebyme_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.DATE_BYME)));
                            ack_arra.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.ACK)));


                            Log.d("TAG", " socket 2 ....3"+client_ip_arr);
                        } while (mCursor.moveToNext());
                        //do above till data exhausted
                    }
                } catch (Exception ee) {
                    ee.printStackTrace();
                    Log.d("TAG", ee.toString());

                }


                //in this while the client listens for the messages sent by the server
                if (socket != null) {
                    Log.d("TAG", "socket2 not null ");



                        Log.d("TAG", " for loop" + client_ip_arr);

                    for (int i = 0;  i< client_ip_arr.size(); i++) {
                        Log.d("TAG", " for loop" + i);
                        outMsg = client_ip_arr.get(i) + "*" + datebyme_arr.get(i) + "*" + clientid_arr.get(i) + "*" + message_arr.get(i) + "*" + name_arr.get(i) + "*" + mPhoneNumber;
                        Log.d("TAG", " for loop" + outMsg);
                        //  outMsg = client_ip_arr + "*" + datebyme_arr + "*" + clientid_arr + "*" + message_arr + "*" + name_arr + "*" + mPhoneNumber;
                        //  }


                        mes_tosend = outMsg;

                        if (mes_tosend != null)
                            mBufferOut.write(mes_tosend);

                        Log.d("TAG", "Socket 2: sending Message: '" + mes_tosend + "'");

                        mBufferOut.flush();

                    }
                        while (mRun) {

                            inMsg = mBufferIn.readLine() + System.getProperty("line.separator");
                            mServerMessage = inMsg;
                            Log.d("TAG", "Socket 2: Received Message: '" + mServerMessage + "'");
                            if (mServerMessage.equals("Ok")) {
                                Log.d("TAG", "Socket 2: update method: '" + mes_tosend + "'");
                             //   db.updateData("YES", client_ip_arr.get(i));
                                Log.d("TAG", "Socket 2: updated success: '" + mes_tosend + "'");
                            }
                        }
                   // }
                }

            } catch (Exception e) {
                Log.d("TAG", "S: internet error: '" + e + "'");
               // onDestroy();

            }
        } catch (Exception e) {
            // socket_conect();
            Log.d("TAG", "S:  error:trying to connect again '" + e + "'");
            socket_conect();
            Log.d("TCP", "C: Error", e);
        }

    }
























    public class connectSocket1 implements Runnable {

        public void run() {

            mRun = true;


            try {
                //    socket = new Socket("192.168.15.62", 8888);

                Log.d("TAG", "socket closed" + "'");
                socket = new Socket("101.53.131.42", 4444);
                Log.d("TAG", " socket again connecting....");
                //   Toast.makeText(getApplicationContext(), "Socket trying to connect ...", Toast.LENGTH_LONG).show();

                try {
                    //sends the message to the server
                    mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    //receives the message which the server sends back
                    mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    flag = 1;

                    dataBase = db.getWritableDatabase();
                    Cursor mCursor = dataBase.rawQuery("SELECT * FROM "
                            + DataBaseHandler_new.TABLE_NAME, null);
                    Log.d("TAG", " socket 2 ....2");
                    try {

                        if (mCursor.moveToFirst()) {
                            do {

                                client_ip_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.CLIENT_IP)));
                                clientid_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.CLIENT_ID)));
                                message_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.MESSAGE)));
                                datebyme_arr.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.DATE_BYME)));
                                ack_arra.add(mCursor.getString(mCursor.getColumnIndex(DataBaseHandler_new.ACK)));


                                Log.d("TAG", " socket 2 ....3" + client_ip_arr);
                            } while (mCursor.moveToNext());
                            //do above till data exhausted
                        }
                    } catch (Exception ee) {
                        ee.printStackTrace();
                        Log.d("TAG", ee.toString());

                    }


                    //in this while the client listens for the messages sent by the server
                    if (socket != null) {
                        Log.d("TAG", "socket2 not null ");


                        Log.d("TAG", " for loop" + client_ip_arr);

                        for (int i = 0; i < client_ip_arr.size(); i++) {
                            Log.d("TAG", " for loop" + i);
                            outMsg = client_ip_arr.get(i) + "*" + datebyme_arr.get(i) + "*" + clientid_arr.get(i) + "*" + message_arr.get(i) + "*" + name_arr.get(i) + "*" + mPhoneNumber;
                            Log.d("TAG", " for loop" + outMsg);
                            //  outMsg = client_ip_arr + "*" + datebyme_arr + "*" + clientid_arr + "*" + message_arr + "*" + name_arr + "*" + mPhoneNumber;


                            mes_tosend = outMsg;

                            if (mes_tosend != null)
                                mBufferOut.write(mes_tosend);

                            Log.d("TAG", "Socket 2: sending Message: '" + mes_tosend + "'");

                            mBufferOut.flush();

                        }
                        while (mRun) {

                            inMsg = mBufferIn.readLine() + System.getProperty("line.separator");
                            mServerMessage = inMsg;
                            Log.d("TAG", "Socket 2: Received Message: '" + mServerMessage + "'");
                            if (mServerMessage.equals("Ok")) {
                                Log.d("TAG", "Socket 2: update method: '" + mes_tosend + "'");
                                //   db.updateData("YES", client_ip_arr.get(i));
                                Log.d("TAG", "Socket 2: updated success: '" + mes_tosend + "'");
                            }
                        }
                        // }
                    }

                } catch (Exception e) {
                    Log.d("TAG", "S: internet error: '" + e + "'");
                    // onDestroy();

                }
            } catch (Exception e) {
                // socket_conect();
                Log.d("TAG", "S:  error:trying to connect again '" + e + "'");
                socket_conect();
                Log.d("TCP", "C: Error", e);
            }
        }
    }












}