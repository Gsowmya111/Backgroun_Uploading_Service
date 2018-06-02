package com.example.edisonoffice.backgroun_uploading_service;

/**
 * Created by edison office on 5/24/2018.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.edisonoffice.backgroun_uploading_service.BackgroundService.socket;


public class InternetConnector_receiver extends BroadcastReceiver {
    public InternetConnector_receiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        try {

            boolean isVisible = MyApplication.isActivityVisible();// Check if
            Log.i("Activity is Visible ", "Is activity visible : " + isVisible);
            Timer timer = new Timer();
            timer.schedule(new TimerTask()
            {
                @Override
                public void run()
                {
                    ConnectivityManager manager = (ConnectivityManager) context
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                   // ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

                    //For 3G check
                    boolean is3g = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
                            .isConnectedOrConnecting();
                    //For WiFi Check
                    boolean isWifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                            .isConnectedOrConnecting();


                    if (!is3g && !isWifi)
                    {
                       Log.d("TAG", "internet disconect close socket destroy "  + "'");
                        BackgroundService bd = new BackgroundService();
                      // bd.onDestroy();
                      bd.socket_conect();
                       /* try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        Log.d("TAG", "internet disconect close socket destroy2 "  + "'");
                    }
                    else
                    {
                      //  Log.d("TAG", "internet conneced state "  + "'");
                    }
                }
            }, 0, 1);   //in milli seconds



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}