package com.example.ssmbu.singlenet.ui.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ssmbu.singlenet.MyApplication;
import com.example.ssmbu.singlenet.R;
import com.example.ssmbu.singlenet.utils.SIMUtils;
import com.example.ssmbu.singlenet.utils.SharedPreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    //Settings settings=Settings.getInstance();
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

    @OnClick({R.id.btn_setSIM, R.id.check_dev,R.id.btn_editK2Pass})
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
            case R.id.btn_editK2Pass:
                final EditText txt_K2Pass=new EditText(this);

                txt_K2Pass.setText((String)SharedPreferencesUtils.getFromSpfs(MyApplication.getContext(),"k2Pass","admin"));
                //final String k2Pass= (String) SharedPreferencesUtils.getFromSpfs(MyApplication.getContext(),"k2Pass");
                new AlertDialog.Builder(this)
                        .setTitle("设置路由器密码")
                        .setView(txt_K2Pass)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String k2Pass=txt_K2Pass.getText().toString();
                                SharedPreferencesUtils.putToSpfs(MyApplication.getContext(),"k2Pass",k2Pass);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .create().show();

                break;
        }
    }

    private void initView(){
        int sim=(int)SharedPreferencesUtils.getFromSpfs(MyApplication.getContext(),"sim",0);
        String simInfo="当前SIM卡：SIM"+(sim+1) + "\n"+ SIMUtils.numberFromSIM(sim);
        txtSimInfo.setText(simInfo);
        boolean devOpen=(boolean)SharedPreferencesUtils.getFromSpfs(MyApplication.getContext(),"devOpen",false);
        checkDev.setChecked(devOpen);
    }

    private int checkedSIM;

    private void selectSIM() {
        String[] items = {"sim 1", "sim 2"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择SIM卡");
         checkedSIM = (int)SharedPreferencesUtils.getFromSpfs(MyApplication.getContext(),"sim",0);
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
        SharedPreferencesUtils.putToSpfs(MyApplication.getContext(),"devOpen",checkDev.isChecked());
        //settings.setDevOpen(checkDev.isChecked());
        //settings.writeSettings();
    }

    private void saveSIM(int which) {
        SharedPreferencesUtils.putToSpfs(MyApplication.getContext(),"sim",which);
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
