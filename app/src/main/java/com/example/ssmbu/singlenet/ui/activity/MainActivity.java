package com.example.ssmbu.singlenet.ui.activity;

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
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssmbu.singlenet.MyApplication;
import com.example.ssmbu.singlenet.R;
import com.example.ssmbu.singlenet.presenter.FeixunPresenter;
import com.example.ssmbu.singlenet.presenter.SingleNetPresenter;
import com.example.ssmbu.singlenet.utils.SMSUtils;
import com.example.ssmbu.singlenet.utils.SharedPreferencesUtils;
import com.example.ssmbu.singlenet.view.FeixunView;
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
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    //监听短信广播
    private MessageReceiver messageReceiver;
    private SingleNetPresenter singleNetPresenter;
    private FeixunPresenter feixunPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        initView();
        initPermission();
        initReceiver();

        singleNetPresenter = new SingleNetPresenter(singleNetView);
        feixunPresenter = new FeixunPresenter(feixunView);


        singleNetPresenter.initPswd();
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

    private FeixunView feixunView = new FeixunView() {
        @Override
        public void getSysauthSuccess() {
            feixunPresenter.getPppoeUser();
        }

        @Override
        public void getSysauthFail(String message) {
            Toast.makeText(MyApplication.getContext(), message, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void getPppoeUserSuccess() {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("确定修改K2闪讯密码？")
                    .setMessage("乱改是要被室友抓起来打的。")
                    .setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Toast.makeText(MyApplication.getContext(), "您取消了修改", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("手滑", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(MyApplication.getContext(), "您取消了修改", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            feixunPresenter.postPppoePass();
                        }
                    }).create().show();
        }

        @Override
        public void getPppoeUserFail() {

            Toast.makeText(MyApplication.getContext(), "请求用户名失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void postPppoePassSuccess() {
            Toast.makeText(MyApplication.getContext(), "修改K2闪讯密码成功！请坐和放宽", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void postPppoePassFail() {
            Toast.makeText(MyApplication.getContext(), "修改K2闪讯密码失败！心态崩了", Toast.LENGTH_SHORT).show();
        }
    };


    private SingleNetView singleNetView = new SingleNetView() {
        @Override
        public void hideSwipeRefreshLayout() {
            swipeRefreshLayout.setRefreshing(false);
        }

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
        public void loadData(String pswd, String vld) {
            txtPswd.setText(pswd);
            txtVld.setText("过期时间：" + vld);
        }

        @Override
        public void notOverdue() {
            Toast.makeText(MainActivity.this, "密码未过期", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }

        @Override
        public void sendMM() {
            Toast.makeText(MainActivity.this, "短信已发送", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void expireData() {
            //Toast.makeText(MainActivity.this, "手动过期", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void clearData() {
            //Toast.makeText(MainActivity.this, "手动清空", Toast.LENGTH_SHORT).show();
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

    private void initView() {
        boolean devOpen = (boolean) SharedPreferencesUtils.getFromSpfs(MyApplication.getContext(), "devOpen", false);
        if (devOpen) {
            layoutDev.setVisibility(View.VISIBLE);
        } else {
            layoutDev.setVisibility(View.INVISIBLE);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                singleNetPresenter.refreshPswd();
            }
        });
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
                boolean devOpen = (boolean) SharedPreferencesUtils.getFromSpfs(MyApplication.getContext(), "devOpen", false);
                if (devOpen) {
                    layoutDev.setVisibility(View.VISIBLE);
                } else {
                    layoutDev.setVisibility(View.INVISIBLE);
                }
        }
    }

    @OnClick({R.id.btn_refreshPswd, R.id.btn_sendMM, R.id.btn_postK2Pass, R.id.btn_expirePswd, R.id.btn_clearAll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_refreshPswd:
                singleNetPresenter.refreshPswd();
                break;
            case R.id.btn_sendMM:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("强制发送短信");
                dialog.setMessage("我们将发送一条短信。");
                dialog.setCancelable(true);
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        singleNetPresenter.sendMM();
                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
                break;
            case R.id.btn_postK2Pass:
                feixunPresenter.getSysauth();
                break;

            case R.id.btn_expirePswd:
                singleNetPresenter.expirePswd();
                break;
            case R.id.btn_clearAll:
                singleNetPresenter.clearPswd();
                break;
        }
    }

    class MessageReceiver extends BroadcastReceiver {
        /**
         * 收到短信
         * @param context
         * @param intent
         */
        @Override
        public void onReceive(Context context, Intent intent) {
            singleNetPresenter.saveMessage(intent);
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
