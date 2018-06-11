package com.example.ssmbu.singlenet.presenter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.example.ssmbu.singlenet.MainActivity;
import com.example.ssmbu.singlenet.MyApplication;
import com.example.ssmbu.singlenet.utils.SIMUtils;

public class MySmsManager {
    private Context context;
    private static final String SINGLENETNUMBER = "106593005";
    private static final String SINGLENETMSG = "mm";

    public MySmsManager(Context context){
        this.context=context;
    }

    public void sendMM(){
        if(SIMUtils.isTwoSim()){
            SharedPreferences sp= MyApplication.getContext().getSharedPreferences("settings",Context.MODE_PRIVATE);
            int sim=sp.getInt("sim",0);
            int subId= SIMUtils.subIdFromSIM(sim);
            SmsManager smsManager=SmsManager.getSmsManagerForSubscriptionId(subId);
            PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(), 0);
            smsManager.sendTextMessage(SINGLENETNUMBER, null, SINGLENETMSG, pi, null);
        }
        else {
            final SmsManager smsManager=SmsManager.getDefault();
            PendingIntent pi = PendingIntent.getActivity(context, 0, new Intent(), 0);
            smsManager.sendTextMessage(SINGLENETNUMBER, null, SINGLENETMSG, pi, null);
        }

    }
}
