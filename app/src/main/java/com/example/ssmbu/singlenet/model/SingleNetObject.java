package com.example.ssmbu.singlenet.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ssmbu.singlenet.MyApplication;
import com.example.ssmbu.singlenet.utils.SMSUtils;

import java.text.ParseException;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class SingleNetObject  {
    private String pswd="";
    private String vld="";
    private String smsBody="";
    public SingleNetObject(){
    }
    public SingleNetObject(String fullMessage){
        smsBody=fullMessage;
        //"尊敬的闪讯用户，您的宽带上网密码是：164126,密码在2018-05-31 05:29:59以前有效";
        pswd = fullMessage.substring(18, 24);
        vld = fullMessage.substring(28, 47);

    }

    public String getPswd() {
        return pswd;
    }

    public String getVld() {
        return vld;
    }

    public Boolean isEmpty() {
        return "".equals(pswd)||"".equals(vld);
    }

    public Boolean isOverdue() {
        Date vldDate,now;
        now=new Date();
        try {
            vldDate= SMSUtils.ft.parse(vld);
            if(now.before(vldDate)){
                return false;
            }
            else {
                return true;
            }
        }
        catch (ParseException e){
            Log.e(TAG, "isOverdue: ", e);
        }
        return false;
    }
    public void readData(){
        SharedPreferences sp= MyApplication.getContext().getSharedPreferences("data",Context.MODE_PRIVATE);
        smsBody=sp.getString("smsBody","");
        pswd=sp.getString("pswd","");
        vld=sp.getString("vld","");
    }
    public void writeData(){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("smsBody",smsBody);
        editor.putString("pswd", pswd);
        editor.putString("vld", vld);
        editor.apply();
    }
    public static void writeEmptyData(){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("data", Context.MODE_PRIVATE).edit();
        editor.putString("smsBody","");
        editor.putString("pswd", "");
        editor.putString("vld", "");
        editor.apply();

    }

}
