package com.example.ssmbu.singlenet.view;

import com.example.ssmbu.singlenet.model.SingleNetObject;

public interface SingleNetView {
    void waitData();
    void waitNewData();
    void loadData(SingleNetObject model);
    void notOverdue();
    void sendMM();
    void expireData();
    void clearData();
}
