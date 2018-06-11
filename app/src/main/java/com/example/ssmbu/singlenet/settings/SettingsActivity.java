package com.example.ssmbu.singlenet.settings;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssmbu.singlenet.R;
import com.example.ssmbu.singlenet.settings.model.Settings;
import com.example.ssmbu.singlenet.utils.SIMUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends AppCompatActivity {

    Settings settings=Settings.getInstance();
    @BindView(R.id.btn_setSIM)
    Button btnSetSIM;
    @BindView(R.id.check_dev)
    CheckBox checkDev;
    @BindView(R.id.txt_simInfo)
    TextView txtSimInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);


        initView();
    }

    @OnClick({R.id.btn_setSIM, R.id.check_dev})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_setSIM:
                if(SIMUtils.isTwoSim()) {
                    selectSIM();
                }
                else{
                    AlertDialog.Builder builder=new AlertDialog.Builder(this);
                    builder.setTitle("您是单卡用户");
                    builder.setMessage("您无法选择SIM卡哦！");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ;
                        }
                    });
                    builder.show();
                }
                break;
            case R.id.check_dev:
                switchDev();
                if(checkDev.isChecked()){
                    Toast.makeText(SettingsActivity.this,"您启用了开发者选项！",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SettingsActivity.this,"您关闭了开发者选项！",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void initView(){
        //int sim=Settings.getInstance().getSIM();
        String simInfo="当前SIM卡：SIM"+(settings.getSim()+1) + "\n"+ SIMUtils.numberFromSIM(settings.getSim());
        txtSimInfo.setText(simInfo);
        checkDev.setChecked(settings.getDevOpen());
    }

    private int checkedSIM;

    private void selectSIM() {
        String[] items = {"sim 1", "sim 2"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择SIM卡");
        checkedSIM = settings.getSim();
        builder.setSingleChoiceItems(items, checkedSIM, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkedSIM = which;
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveSIM(checkedSIM);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private void switchDev(){
        settings.setDevOpen(checkDev.isChecked());
        settings.writeSettings();
    }

    private void saveSIM(int which) {
        settings.setSim(which);
        settings.writeSettings();
        String simInfo="当前SIM卡：SIM"+(which+1) + "\n"+ SIMUtils.numberFromSIM(which);
        txtSimInfo.setText(simInfo);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_OK, intent);
        finish();
    }
}
