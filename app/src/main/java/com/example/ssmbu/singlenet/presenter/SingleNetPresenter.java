package com.example.ssmbu.singlenet.presenter;

import com.example.ssmbu.singlenet.view.SingleNetView;

public interface SingleNetPresenter {
    void initPswd();
    void refreshPswd();
    void sendMM();
    void expirePswd();
    void clearPswd();
    void attachView(SingleNetView view);
}
