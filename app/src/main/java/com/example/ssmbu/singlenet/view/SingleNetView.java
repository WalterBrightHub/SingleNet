package com.example.ssmbu.singlenet.view;

public interface SingleNetView {
    void waitData();
    void waitNewData();
    void loadData(String pswd,String vld);
    void notOverdue();
    void sendMM();
    void expireData();
    void clearData();
}
