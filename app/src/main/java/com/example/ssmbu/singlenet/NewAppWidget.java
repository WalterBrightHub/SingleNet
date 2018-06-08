package com.example.ssmbu.singlenet;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.ssmbu.singlenet.utils.SMSUtils;

import java.text.ParseException;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {
    private static final String TAG = "NewAppWidget";

     public static final String CLICK_ACTION="com.example.ssmbu.singlenet.action.CLICK";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        //views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

        //Intent intent = new Intent(CLICK_ACTION);
        //传递参数，否则MainActivity注销后点击小部件无响应。金立机有此问题。
        Intent intent=new Intent(context,NewAppWidget.class);
        intent.setAction(CLICK_ACTION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, R.id.yinxian_imageView, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.yinxian_imageView, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
        if (CLICK_ACTION.equals(intent.getAction())){


            //Toast.makeText(context,"click widget",Toast.LENGTH_SHORT).show();
            SharedPreferences preferences=context.getSharedPreferences("data",MODE_PRIVATE);
            String pswd=preferences.getString("pswd","");
            String vld=preferences.getString("vld","");
            if("".equals(pswd)){
                Toast.makeText(context,"没有数据",Toast.LENGTH_SHORT).show();

            }
            else {
                Date now=new Date();
                try{
                    Date vldDate= SMSUtils.ft.parse(vld);
                    if(now.before(vldDate)){
                        Toast.makeText(context,pswd,Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context,"密码已过期",Toast.LENGTH_SHORT).show();
                    }
                }catch (ParseException e){
                    Log.e(TAG, "onClick: ", e);
                }
            }


        }
    }
}

