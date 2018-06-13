package com.example.ssmbu.singlenet.presenter;

import android.util.Log;


import com.example.ssmbu.singlenet.MyApplication;
import com.example.ssmbu.singlenet.bean.Sysauth;
import com.example.ssmbu.singlenet.model.FeixunModel;
import com.example.ssmbu.singlenet.utils.SharedPreferencesUtils;
import com.example.ssmbu.singlenet.view.FeixunView;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FeixunPresenter {
    private static final String TAG = "FeixunPresenter";
    private FeixunView feixunView;
    private FeixunModel feixunModel;
    private Sysauth mSysauth;
    private String mPppoeUser;

    public FeixunPresenter(FeixunView feixunView) {
        this.feixunView = feixunView;
        feixunModel=new FeixunModel();
    }


    public void getSysauth(){
        String k2Pass=(String)SharedPreferencesUtils.getFromSpfs(MyApplication.getContext(),"k2Pass","admin");
        feixunModel.getSysauth(k2Pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Sysauth>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Sysauth sysauth) {
                        mSysauth=sysauth;

                    }

                    @Override
                    public void onError(Throwable e) {
                        feixunView.getSysauthFail(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        feixunView.getSysauthSuccess();
                    }
                });
    }
    public void getPppoeUser(){
        feixunModel.getPppoeUser(mSysauth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        mPppoeUser=s;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: "+e.getMessage());
                        feixunView.getPppoeUserFail();
                    }

                    @Override
                    public void onComplete() {
                        feixunView.getPppoeUserSuccess();
                    }
                });
    }
    public void postPppoePass(){
        String pppoePass= (String) SharedPreferencesUtils.getFromSpfs(MyApplication.getContext(),"pswd","123456");
        feixunModel.postPppoePass(mPppoeUser,pppoePass,mSysauth)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        feixunView.postPppoePassFail();
                    }

                    @Override
                    public void onComplete() {
                        feixunView.postPppoePassSuccess();
                    }
                });
    }
}
