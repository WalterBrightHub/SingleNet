package com.example.ssmbu.singlenet.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.ContentValues.TAG;

public class SingleNetObject  {
    private String pswd;
    private String vld;
    private String smsBody;
    private static final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public SingleNetObject(Context context){
        SharedPreferences sp=context.getSharedPreferences("data",Context.MODE_PRIVATE);
        pswd=sp.getString("pswd","");
        vld=sp.getString("vld","");
    }

    public String getPswd() {
        return pswd;
    }

    public String getVld() {
        return vld;
    }

    public Boolean isEmptyPswd() {
        return "".equals(pswd);
    }

    public Boolean isOverdue() {
        Date vldDate,now;
        now=new Date();
        try {
            vldDate=ft.parse(vld);
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

}
