package com.example.ssmbu.singlenet.presenter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;

import com.example.ssmbu.singlenet.MainActivity;

public class MySmsManager {
    private Context context;
    private static final String SINGLENETNUMBER = "106593005";
    private static final String SINGLENETMSG = "mm";

    public MySmsManager(Context context){
        this.context=context;
    }

    public void sendMM(){
        final SmsManager smsManager=SmsManager.getDefault();
        PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(), 0);
        smsManager.sendTextMessage(SINGLENETNUMBER, null, SINGLENETMSG, pi, null);

    }
}
