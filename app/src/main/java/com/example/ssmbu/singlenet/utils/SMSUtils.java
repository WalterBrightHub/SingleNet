package com.example.ssmbu.singlenet.utils;

import android.content.Intent;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;

public class SMSUtils {

    public static final SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getFullMessage(String number,Intent intent){
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        //由于短信的字数限制，一条短信可能被拆分为多条
        String fullMessage = "";
        for (Object pdu : pdus) {
            //创建一个短信对象
            SmsMessage fromPdu = SmsMessage.createFromPdu((byte[]) pdu);
            //获取来信号码
            String originatingAddress = fromPdu.getOriginatingAddress();
            if (number.equals(originatingAddress)) {
                fullMessage += fromPdu.getMessageBody();
            }
        }
        return fullMessage;
    }
}
