package com.example.ssmbu.singlenet.presenter;

import android.content.Intent;

import com.example.ssmbu.singlenet.MyApplication;
import com.example.ssmbu.singlenet.model.SingleNetModel;
import com.example.ssmbu.singlenet.utils.SMSUtils;
import com.example.ssmbu.singlenet.view.SingleNetView;


public class SingleNetPresenter {
    private static final String SINGLENETNUMBER = "106593005";
    //private SingleNetModel model;
    private SingleNetView view;
    private MySmsManager mySmsManager=new MySmsManager(MyApplication.getContext());
    public SingleNetPresenter(SingleNetView singleNetView){
        //this.context=context;
        //mySmsManager=new MySmsManager(context);
        view=singleNetView;
    }

    public void initPswd() {
        SingleNetModel model=new SingleNetModel();
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
                view.loadData(model.getPswd(),model.getVld());
            }
        }
    }

    public void refreshPswd() {
        SingleNetModel model = new SingleNetModel();
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

    public void sendMM() {
        mySmsManager.sendMM();
        view.sendMM();
    }

    public void expirePswd() {
        String overdueMsg="尊敬的闪讯用户，您的宽带上网密码是：765876,密码在2018-06-04 05:29:59以前有效";
        SingleNetModel model=new SingleNetModel(overdueMsg);
        model.writeData();
        view.loadData(model.getPswd(),model.getVld());
        view.expireData();
    }


    public void clearPswd() {
        SingleNetModel.writeEmptyData();
        view.loadData("??????","????-??-?? ??:??:??");
        view.clearData();
    }



    public void saveMessage(Intent intent) {
        String fullMessage= SMSUtils.getFullMessage(SINGLENETNUMBER,intent);
        if (!"".equals(fullMessage)) {
            SingleNetModel model=new SingleNetModel(fullMessage);
            model.writeData();
            view.loadData(model.getPswd(),model.getVld());
        }
    }
}
