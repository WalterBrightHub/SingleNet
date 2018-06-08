package com.example.ssmbu.singlenet.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.util.Log;

import com.example.ssmbu.singlenet.model.SingleNetObject;
import com.example.ssmbu.singlenet.view.SingleNetView;

import java.text.ParseException;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class SingleNetImplementor implements SingleNetPresenter {
    private SingleNetObject model;
    private SingleNetView view;
    private Context context;
    private MySmsManager mySmsManager;
    public SingleNetImplementor(Context context){
        this.context=context;
        mySmsManager=new MySmsManager(context);
    }

    @Override
    public void refreshPswd() {

    }

    @Override
    public void sendMM() {

    }

    @Override
    public void expirePswd() {

    }

    @Override
    public void clearPswd() {

    }

    @Override
    public void attachView(SingleNetView view) {
        this.view=view;
    }

    @Override
    public void initPswd() {
        final SmsManager smsManager = SmsManager.getDefault();
        model=new SingleNetObject(context);
        if(model.isEmptyPswd()){
            view.waitData();
            mySmsManager.sendMM();
        }
        else {
            if(model.isOverdue()){
                view.waitNewData();
                mySmsManager.sendMM();
            }
            else {
                view.loadData(model);
            }
        }
        /*


        SharedPreferences preferences = context.getSharedPreferences("data", MODE_PRIVATE);
        saved_pswd = preferences.getString("pswd", "??????");
        saved_vld = preferences.getString("vld", "");
        if ("".equals(saved_pswd)) {
            txtPswd.setText("??????");
            txtVld.setText("正在初始化密码……");
            sendMM(smsManager);
        } else {
            Date vld, now;
            now = new Date();

            try {
                vld = ft.parse(saved_vld);
                if (now.before(vld)) {
                    update_pswd_vld();
                } else {
                    txtPswd.setText("******");
                    txtVld.setText("密码已过期，正在更新中……");
                    sendMM(smsManager);
                }
            } catch (ParseException e) {
                Log.e(TAG, "onClick: ", e);
            }
        }*/
    }

}
