package com.example.ssmbu.singlenet.settings.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.ssmbu.singlenet.MyApplication;

public class Settings {
    private Settings(){}
    private static Settings instance=new Settings();
    public static Settings getInstance() {
        readSettings();
        return instance;
    }
    private static int sim;
    private static Boolean devOpen;
    private static void readSettings(){
        SharedPreferences sp = MyApplication.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE);
        //sim = sp.getString("sim", "0");
        sim=sp.getInt("sim",0);
        devOpen=sp.getBoolean("devOpen",false);
    }
    public static void writeSettings(){
        SharedPreferences.Editor editor = MyApplication.getContext().getSharedPreferences("settings", Context.MODE_PRIVATE).edit();
        editor.putInt("sim", sim);
        editor.putBoolean("devOpen",devOpen);
        editor.apply();
    }

    public static int getSim() {
        return sim;
    }

    public static void setSim(int sim) {
        Settings.sim = sim;
    }

    public static Boolean getDevOpen() {
        return devOpen;
    }

    public static void setDevOpen(Boolean devOpen) {
        Settings.devOpen = devOpen;
    }
}
