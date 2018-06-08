package com.example.ssmbu.singlenet.presenter;

import android.content.Intent;

import com.example.ssmbu.singlenet.view.SingleNetView;

public interface SingleNetPresenter {
    void attachView(SingleNetView view);
    void initPswd();
    void refreshPswd();
    void sendMM();
    void saveMessage(Intent intent);
    void expirePswd();
    void clearPswd();
}
