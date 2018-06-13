package com.example.ssmbu.singlenet.model;

import android.util.Base64;
import android.util.Log;


import com.example.ssmbu.singlenet.bean.Sysauth;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeixunModel {
    private static final String TAG = "FeixunModel";
    private static final String BASEURL = "http://192.168.2.1";
    //private OkHttpClient client;
    public FeixunModel(){
        //client=new OkHttpClient();
    }
    public Observable<Sysauth> getSysauth(final String pswd){
        return Observable.create(new ObservableOnSubscribe<Sysauth>() {
            @Override
            public void subscribe(final ObservableEmitter<Sysauth> emitter) throws Exception {

                String encodedPswd = Base64.encodeToString(pswd.getBytes(), Base64.NO_WRAP);
                String loginUrl = BASEURL + "/cgi-bin/luci/admin/h5/login";
                FormBody formBody = new FormBody.Builder()
                        .add("login", "h5login")
                        .add("username", "admin")
                        .add("password", encodedPswd)
                        .build();
                //String loginUrl = BASEURL + "/cgi-bin/luci/admin/h5/login";f
                final Request request = new Request.Builder()
                        .post(formBody)
                        .url(loginUrl)
                        .build();

                //拦截重定向请求，日后自己请求重定向后的网页
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .followRedirects(false)
                        .followRedirects(false)
                        .build();

                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: 路由器登录失败，请检查网络设置");
                        emitter.onError(new Exception("路由器登录失败，请检查网络设置"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (302 == response.code()) {
                            Log.d(TAG, "onResponse: 路由器登录成功");
                            String location = response.header("Location");
                            String setCookie = response.header("Set-Cookie");
                            //sysauth=0dd2acbb94d4c79ee4a59e4cad7c31c7; path=/cgi-bin/luci/;stok=a4d47ab8ef2485c1e30701839d7b71cd
                            Log.d(TAG, "onResponse: " + setCookie);
                            String sysauth = setCookie.substring(8, 40);
                            String path = "/cgi-bin/luci/";
                            String stok = setCookie.substring(67, 99);
                            Log.d(TAG, "onResponse: " + sysauth + " " + stok);

                            emitter.onNext(new Sysauth(sysauth,stok));
                        }
                        else if(200==response.code()){
                            //斐讯密码错误,要求重新登陆
                            Log.d(TAG, "onResponse: 路由器登录失败，请检查密码");
                            emitter.onError(new Exception("路由器登录失败，请检查密码"));
                        }
                        else {
                            //未知
                            Log.d(TAG, "onResponse: 路由器被小怪兽吃掉了");
                            emitter.onError(new Exception("路由器被小怪兽吃掉了"));
                        }
                        emitter.onComplete();
                    }
                });
            }
        });
    }

    public Observable<String> getPppoeUser(final Sysauth sysauth){
        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {
                //http://192.168.2.1/cgi-bin/luci/;stok=6b5575d889fb985fbdef38bd8e6a4a1a/admin/h5/linktointernet

                String networksetUrl = BASEURL + "/cgi-bin/luci/" + ";stok=" + sysauth.getStok() + "/admin/h5/linktointernet";
                networksetUrl = BASEURL + "/cgi-bin/luci/" + ";stok=" + sysauth.getStok() + "/admin/h5/linktointernet";
                Request request = new Request.Builder()
                        .get()
                        .addHeader("Cookie", "sysauth=" + sysauth.getSysauth())
                        .url(networksetUrl)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: 获取闪讯账号失败");
                        emitter.onError(new Exception("获取闪讯账号失败"));
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (200 == response.code()) {
                            //得到网页文本，该文本包含了闪讯账号
                            final String body=response.body().string();
                            String pppoeUser= getPppoeUser(body);
                            Log.d(TAG, "onResponse: 获取闪讯账号成功："+pppoeUser);
                            emitter.onNext(pppoeUser);
                        }
                        else {
                            Log.d(TAG, "onResponse: 获取闪讯账号失败 "+response.code());
                        }
                        emitter.onComplete();

                    }
                });
            }
        });
    }
    public Observable<Boolean> postPppoePass(final String pppoeUser,final String pppoePass,final Sysauth sysauth){
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(final ObservableEmitter<Boolean> emitter) throws Exception {
                FormBody formBody = new FormBody.Builder()
                        .add("wanlinkWay", "pppoe")
                        .add("staticNetmask", "255.255.255.0")
                        .add("autodetect", "off")
                        .add("pppoeUser", pppoeUser)
                        .add("pppoePass", pppoePass)
                        .build();
                //http://192.168.2.1/cgi-bin/luci/;stok=e83e9cab79d1a65ec928b637e8a6d4b7/admin/h5/internetset
                String internetsetUrl = BASEURL + "/cgi-bin/luci/" + ";stok=" + sysauth.getStok() + "/admin/h5/internetset";
                Log.d(TAG, "postPppoePass: " + internetsetUrl);
                Request request = new Request.Builder()
                        .post(formBody)
                        .addHeader("Cookie", "sysauth=" + sysauth.getSysauth())
                        .url(internetsetUrl)
                        .build();
                OkHttpClient client = new OkHttpClient();
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.d(TAG, "onFailure: 路由器设置密码失败！");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (200 == response.code()) {
                            Log.d(TAG, "onResponse: " + "路由器设置密码开始！");
                            //Log.d(TAG, "onResponse: "+response.body().string());
                            emitter.onNext(true);
                        }
                        emitter.onComplete();
                    }
                });
            }
        });
    }

    //从网页体提取闪讯账号
    /*<input type="text" class="form-control" name="pppoeUser" id="pppoeUser" value="18958049892@GDPF.XY">*/
    private String getPppoeUser(String body){
        Document document= Jsoup.parse(body);
        Elements element=document.select("#pppoeUser");
        return element.val();
    }
}
