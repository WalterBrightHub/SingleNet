package com.example.ssmbu.singlenet.view;

public interface FeixunView {
    void getSysauthSuccess();

    void getSysauthFail(String message);

    void getPppoeUserSuccess();

    void getPppoeUserFail();

    void postPppoePassSuccess();

    void postPppoePassFail();
}
