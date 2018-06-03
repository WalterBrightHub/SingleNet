package com.example.ssmbu.singlenet;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import android.telephony.SmsMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private Button btn_sendMM;
    private Button btn_forceSendMM;
    private Button btn_clearVld;
    private Button btn_clearAll;
    private RadioButton sim1,sim2;
    private TextView txt_simInfo,txt_vld,txt_pswd;
    //private IntentFilter intentFilter;
    private MessageReceiver messageReceiver;
    private static final String SINGLENETNUMBER="106593005";
    private static final String SINGLENETMSG="mm";
    private String saved_pswd,saved_vld;
    private static final String TAG = "MainActivity";
    //闪讯短信中的格式
    private static final SimpleDateFormat ft=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initPermission();
        initReceiver();
        initListener();
        initData();






    }
    private void initPermission(){
        //运行时权限，发送短信
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
    }
    private void initView(){
        sim1=(RadioButton)findViewById(R.id.sim1);
        sim2=(RadioButton)findViewById(R.id.sim2);
        btn_sendMM=(Button)findViewById(R.id.btn_sendMM);
        btn_forceSendMM=(Button)findViewById(R.id.btn_forceSendMM);
        btn_clearVld=(Button)findViewById(R.id.btn_clearVld);
        btn_clearAll=(Button)findViewById(R.id.btn_clearAll);
        txt_simInfo=(TextView)findViewById(R.id.txt_simInfo);
        txt_vld=(TextView)findViewById(R.id.txt_vld);
        txt_pswd=(TextView)findViewById(R.id.txt_pswd);

    }
    private void sendMM(SmsManager smsManager){
        //final SmsManager smsManager=SmsManager.getDefault();
        PendingIntent pi = PendingIntent.getActivity(MainActivity.this,0,new Intent(),0);
        smsManager.sendTextMessage(SINGLENETNUMBER,null,SINGLENETMSG,pi,null);
        Toast.makeText(MainActivity.this, "短信已发送", Toast.LENGTH_SHORT).show();

    }
    private void initListener(){
        final SmsManager smsManager=SmsManager.getDefault();
        btn_sendMM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("".equals(saved_vld)){
                    sendMM(smsManager);
                    delay_btn_sendMM();
                }
                else{
                    Date vld,now;
                    now=new Date();

                    try{
                        vld=ft.parse(saved_vld);
                        if(now.before(vld)){
                            update_pswd_vld();
                            Toast.makeText(MainActivity.this,"密码未过期",Toast.LENGTH_SHORT).show();
                            //hintDialog("密码未过期","您无需更新密码。\n如有必要，请点击强制更新密码。");
                        }
                        else {
                            sendMM(smsManager);
                            //10秒内不能按下，防止疯狂按下
                            delay_btn_sendMM();
                        }
                    }catch (ParseException e){
                        Log.e(TAG, "onClick: ", e);
                    }
                }

            }
        });
        btn_forceSendMM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("强制发送短信");
                dialog.setMessage("我们将发送一条短信。");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        sendMM(smsManager);
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
        btn_clearVld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saved_pswd="&&&&&&";
                saved_vld="2018-05-1 05:29:59";
                saveMessage();
                Toast.makeText(MainActivity.this, "已重置密码与过期时间", Toast.LENGTH_SHORT).show();
            }
        });
        btn_clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saved_pswd="";
                saved_vld="";
                saveMessage();
                Toast.makeText(MainActivity.this, "已清空密码与过期时间", Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
    }

    private void initReceiver(){
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messageReceiver=new MessageReceiver();
        registerReceiver(messageReceiver,intentFilter);
    }
    class MessageReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            //得到短息主体
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            //由于短信的字数限制，一条短信可能被拆分为多条
            String fullMessage="";
            for (Object pdu : pdus) {
                //创建一个短信对象
                SmsMessage fromPdu = SmsMessage.createFromPdu((byte[]) pdu);
                //获取来信号码
                String originatingAddress = fromPdu.getOriginatingAddress();
                if (SINGLENETNUMBER.equals(originatingAddress)) {
                    fullMessage+=fromPdu.getMessageBody();
                }
            }
            if(!"".equals(fullMessage)){
                //"尊敬的闪讯用户，您的宽带上网密码是：164126,密码在2018-05-31 05:29:59以前有效";
                saved_pswd=fullMessage.substring(18,24);
                saved_vld=fullMessage.substring(28,47);
                saveMessage();
            }
        }
    }
    //保存密码和到期时间。只在收到闪讯短信时调用。
    private void saveMessage(){
        update_pswd_vld();

        SharedPreferences.Editor editor=getSharedPreferences("data",MODE_PRIVATE).edit();
        editor.putString("pswd",saved_pswd);
        editor.putString("vld",saved_vld);
        editor.apply();
        Toast.makeText(MainActivity.this, "短信已保存", Toast.LENGTH_SHORT).show();
    }
    //更新密码，当前时间和到期时间。在保存信息和活动启动时调用。
    private void update_pswd_vld(){
        txt_pswd.setText(saved_pswd);
        //SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String msg="当前时间："+ft.format(new Date())+"\n过期时间："+saved_vld;
        txt_vld.setText(msg);
    }
    //从本地读取密码与过期时间
    private void initData(){

        final SmsManager smsManager=SmsManager.getDefault();
        SharedPreferences preferences=getSharedPreferences("data",MODE_PRIVATE);
        saved_pswd=preferences.getString("pswd","??????");
        saved_vld=preferences.getString("vld","");
        if("".equals(saved_pswd)){
            txt_pswd.setText("??????");
            txt_vld.setText("正在初始化密码……");
            sendMM(smsManager);
        }
        else{
            Date vld,now;
            now=new Date();

            try{
                vld=ft.parse(saved_vld);
                if(now.before(vld)){
                    update_pswd_vld();
                }
                else {
                    txt_pswd.setText("******");
                    txt_vld.setText("密码已过期，正在更新中……");
                    sendMM(smsManager);
                }
            }catch (ParseException e){
                Log.e(TAG, "onClick: ", e);
            }
        }

    }
    //延时十秒不能按下，待改进
    private void delay_btn_sendMM(){
        btn_sendMM.setClickable(false);
        TimerTask timerTask=new TimerTask() {
            @Override
            public void run() {
                btn_sendMM.setClickable(true);

            }
        };
        Timer timer=new Timer();
        timer.schedule(timerTask,10000);
    }
    //可重用函数

    //只是个小小的提示。点击确定，对话框消失
    private void hintDialog(String title,String message){
        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(true);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

}
