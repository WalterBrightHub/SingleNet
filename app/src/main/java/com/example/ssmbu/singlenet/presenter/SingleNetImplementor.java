package com.example.ssmbu.singlenet.presenter;

import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import com.example.ssmbu.singlenet.model.SingleNetObject;
import com.example.ssmbu.singlenet.utils.SMSUtils;
import com.example.ssmbu.singlenet.view.SingleNetView;


public class SingleNetImplementor implements SingleNetPresenter {
    private static final String SINGLENETNUMBER = "106593005";
    //private SingleNetObject model;
    private SingleNetView view;
    private MySmsManager mySmsManager;
    public SingleNetImplementor(Context context){
        //this.context=context;
        mySmsManager=new MySmsManager(context);
    }

    @Override
    public void initPswd() {
        SingleNetObject model=new SingleNetObject();
        model.readData();
        if(model.isEmpty()){
            view.waitData();
            mySmsManager.sendMM();
        }
        else {
            if(model.isOverdue()){
                view.waitNewData();
                mySmsManager.sendMM();
                view.sendMM();
            }
            else {
                view.loadData(model);
            }
        }
    }

    @Override
    public void refreshPswd() {
        SingleNetObject model = new SingleNetObject();
        model.readData();
        if (model.isEmpty()) {
            view.waitData();
            mySmsManager.sendMM();
            view.sendMM();
        } else {
            if (model.isOverdue()) {
                view.waitNewData();
                mySmsManager.sendMM();
                view.sendMM();
            } else {
                view.notOverdue();
            }
        }
    }

    @Override
    public void sendMM() {
        mySmsManager.sendMM();
        view.sendMM();
    }

    @Override
    public void expirePswd() {
        String overdueMsg="尊敬的闪讯用户，您的宽带上网密码是：765876,密码在2018-06-04 05:29:59以前有效";
        SingleNetObject model=new SingleNetObject(overdueMsg);
        model.writeData();
        view.loadData(model);
        view.expireData();
    }

    @Override
    public void clearPswd() {
        SingleNetObject.writeEmptyData();
        SingleNetObject model=new SingleNetObject();
        view.loadData(model);
        view.clearData();
    }

    @Override
    public void attachView(SingleNetView view) {
        this.view=view;
    }

    @Override
    public void saveMessage(Intent intent) {
        String fullMessage= SMSUtils.getFullMessage(SINGLENETNUMBER,intent);
        if (!"".equals(fullMessage)) {
            SingleNetObject model=new SingleNetObject(fullMessage);
            model.writeData();
            view.loadData(model);
        }
    }
}
