package com.example.ssmbu.singlenet.utils;

import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;

import com.example.ssmbu.singlenet.MyApplication;

import java.util.List;

public class SIMUtils {
    static SubscriptionManager manager= SubscriptionManager.from(MyApplication.getContext());
    private static List<SubscriptionInfo> infos=manager.getActiveSubscriptionInfoList();
    public static Boolean isTwoSim(){
        if(infos!=null){
            return infos.size()==2;
        }
        else return false;
    }
    public static int subIdFromSIM(int sim){
        SubscriptionInfo info=infos.get(sim);
        return info.getSubscriptionId();
    }
    public static String numberFromSIM(int sim){
        if(infos==null)return "-1";
        SubscriptionInfo info=infos.get(sim);
        //return info.toString();
        return info.getCarrierName()+" "+info.getNumber();
    }
}
