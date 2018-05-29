package com.example.ssmbu.singlenet;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btn_sendMM;
    private RadioButton sim1,sim2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //运行时权限，发送短信
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }

        sim1=(RadioButton)findViewById(R.id.sim1);
        sim2=(RadioButton)findViewById(R.id.sim2);


        final SmsManager smsManager=SmsManager.getDefault();
        btn_sendMM=(Button)findViewById(R.id.btn_sendMM);
        btn_sendMM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number="106593005";
                String text="mm";
                PendingIntent pi = PendingIntent.getActivity(MainActivity.this,0,new Intent(),0);
                smsManager.sendTextMessage(number,null,text,pi,null);

            }
        });
    }
}
