package com.example.ssmbu.singlenet.bean;

public class Sysauth {
    private String sysauth;
    private String stok;

    public Sysauth(String sysauth, String stok) {
        this.sysauth = sysauth;
        this.stok = stok;
    }

    public String getSysauth() {
        return sysauth;
    }

    public void setSysauth(String sysauth) {
        this.sysauth = sysauth;
    }

    public String getStok() {
        return stok;
    }

    public void setStok(String stok) {
        this.stok = stok;
    }
}
