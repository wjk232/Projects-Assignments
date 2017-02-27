package com.app.wjk232.messagingapp;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.app.wjk232.messagingapp.API.Crypto;
import com.app.wjk232.messagingapp.API.ServerAPI;
import com.app.wjk232.messagingapp.Models.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;

/**
 * Created by rogerycecy on 7/24/2016.
 */

public class BackgroundService extends Service  {
    Thread thread;
    DataBaseHandler db;
    APIController api;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("debug","got to the background service" );
        SharedPreferences prefss = getApplicationContext().getSharedPreferences("keys",Context.MODE_PRIVATE);
        db = DataBaseHandler.getInstance(getApplicationContext());
        api = APIController.getInstance(getApplicationContext());
        api.startPollingThread();
    }

    @Override
    public void sendBroadcast(Intent intent) {
        super.sendBroadcast(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(1000);
                        List<Message> messages = db.getAllMessages();
                        for(int i =0;i < messages.size();i++){
                            if(messages.get(i).percentLeftToLive() <= 0){
                                db.deleteMessage(messages.get(i));
                            }
                        }
                    } catch (InterruptedException e) {
                        return;
                    }

                }

            }
        });
        thread.start();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.interrupt();
        db.close();
    }

}
