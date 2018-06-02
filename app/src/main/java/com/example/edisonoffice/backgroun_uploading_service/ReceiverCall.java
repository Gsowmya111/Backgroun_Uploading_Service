package com.example.edisonoffice.backgroun_uploading_service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by edison office on 5/7/2018.
 */

public class ReceiverCall extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      //  Log.i("service stopped","ohhhhh");
       // context.startService(new Intent(context,BackgroundService.class));

        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            Intent serviceIntent = new Intent(context, BackgroundService.class);
            context.startService(serviceIntent);
        }


    }
}
