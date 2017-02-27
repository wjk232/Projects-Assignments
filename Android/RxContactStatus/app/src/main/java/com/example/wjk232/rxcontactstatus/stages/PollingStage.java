package com.example.wjk232.rxcontactstatus.stages;

import android.util.Log;

import com.example.wjk232.rxcontactstatus.Notification;
import com.example.wjk232.rxcontactstatus.WebHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;

public class PollingStage implements Func1<Integer, Observable<Notification>> {
    final String server;
    final String username;
    final List<String> contacts;
    String userN;

    public PollingStage (String server, String username, List<String> contacts){
        this.server = server;
        this.username = username;
        this.contacts = contacts;
    }

    @Override
    public Observable<Notification> call(Integer integer) {
        try {
            JSONObject json = new JSONObject();
            json.put("username",username);
            String response = WebHelper.StringGet(server+"/wait-for-push/"+username);
            ArrayList<Notification> notifications = new ArrayList<>();
            JSONObject jResponse = new JSONObject(response);
            JSONArray status = jResponse.getJSONArray("notifications");

            for(int i=0; i < status.length();i++){
                JSONObject jObject = new JSONObject(String.valueOf(status.get(i)));
                if(jObject.getString("type").equals("login")) {
                    notifications.add(new Notification.LogIn(jObject.getString("username")));
                }
                if(jObject.getString("type").equals("logout")){
                    notifications.add(new Notification.LogOut(jObject.getString("username")));
                }
                if(jObject.getString("type").equals("message")){
                    JSONObject jMessage = new JSONObject(jObject.getString("content"));
                    Log.d("PollingStage","Got Message");
                }

            }

            return Observable.from(notifications);
        } catch (Exception e) {
            e.printStackTrace();
            return Observable.error(e);
        }
    }
}
