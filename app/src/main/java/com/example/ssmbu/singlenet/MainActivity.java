package com.example.ssmbu.singlenet;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssmbu.singlenet.model.SingleNetObject;
import com.example.ssmbu.singlenet.presenter.SingleNetImplementor;
import com.example.ssmbu.singlenet.presenter.SingleNetPresenter;
import com.example.ssmbu.singlenet.settings.SettingsActivity;
import com.example.ssmbu.singlenet.settings.model.Settings;
import com.example.ssmbu.singlenet.utils.SMSUtils;
import com.example.ssmbu.singlenet.view.SingleNetView;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.txt_pswd)
    TextView txtPswd;
    @BindView(R.id.txt_now)
    TextView txtNow;
    @BindView(R.id.txt_vld)
    TextView txtVld;
    @BindView(R.id.btn_refreshPswd)
    Button btnSendMM;
    @BindView(R.id.btn_sendMM)
    Button btnForceSendMM;
    @BindView(R.id.textView4)
    TextView textView4;
    @BindView(R.id.btn_expirePswd)
    Button btnClearVld;
    @BindView(R.id.btn_clearAll)
    Button btnClearAll;
    @BindView(R.id.layout_dev)
    LinearLayout layoutDev;

    //监听短信广播
    private MessageReceiver messageReceiver;
    private SingleNetPresenter presenter;
    private Settings settings=Settings.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        initView();
        initPermission();
        initReceiver();

        presenter = new SingleNetImplementor(this);
        presenter.attachView(view);

        presenter.initPswd();
        new TimeThread().start();

    }

    class TimeThread extends Thread {
        @Override
        public void run() {
            do {
                try {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (true);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    long ct = System.currentTimeMillis();
                    String now = "当前时间：" + SMSUtils.ft.format(ct);
                    txtNow.setText(now);
            }
        }
    };


    private SingleNetView view = new SingleNetView() {
        @Override
        public void waitData() {
            txtPswd.setText("??????");
            txtVld.setText("正在初始化密码……");
        }

        @Override
        public void waitNewData() {
            txtPswd.setText("******");
            txtVld.setText("密码已过期，正在更新中……");
            delay_btn_sendMM();
        }

        @Override
        public void loadData(SingleNetObject model) {
            txtPswd.setText(model.getPswd());
            String vld = "过期时间：" + model.getVld();
            txtVld.setText(vld);
        }

        @Override
        public void notOverdue() {
            Toast.makeText(MainActivity.this, "密码未过期", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void sendMM() {
            Toast.makeText(MainActivity.this, "短信已发送", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void expireData() {
            Toast.makeText(MainActivity.this, "手动过期", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void clearData() {
            Toast.makeText(MainActivity.this, "手动清空", Toast.LENGTH_SHORT).show();
        }
    };

    private void initPermission() {
        //运行时权限，发送短信
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.RECEIVE_SMS}, 1);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(messageReceiver);
    }

    private void initView(){
        if(settings.getDevOpen()){
            layoutDev.setVisibility(View.VISIBLE);
        }
        else {
            layoutDev.setVisibility(View.INVISIBLE);
        }
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        messageReceiver = new MessageReceiver();
        registerReceiver(messageReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_Settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                //startActivity(intent);
                startActivityForResult(intent, 1);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                //设置页面返回
                if (Settings.getDevOpen()) {
                    layoutDev.setVisibility(View.VISIBLE);
                }
                else {
                    layoutDev.setVisibility(View.INVISIBLE);
                }
        }
    }

    @OnClick({R.id.btn_refreshPswd, R.id.btn_sendMM, R.id.btn_expirePswd, R.id.btn_clearAll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_refreshPswd:
                presenter.refreshPswd();
                break;
            case R.id.btn_sendMM:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("强制发送短信");
                dialog.setMessage("我们将发送一条短信。");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        presenter.sendMM();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                break;
            case R.id.btn_expirePswd:
                presenter.expirePswd();
                break;
            case R.id.btn_clearAll:
                presenter.clearPswd();
                break;
        }
    }

    class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            presenter.saveMessage(intent);
        }
    }


    //延时十秒不能按下，待改进
    private void delay_btn_sendMM() {
        btnSendMM.setClickable(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                btnSendMM.setClickable(true);

            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, 10000);
    }

}
