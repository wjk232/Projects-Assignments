package com.app.wjk232.messagingapp;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.app.wjk232.messagingapp.API.Crypto;
import com.app.wjk232.messagingapp.API.ServerAPI;
import com.app.wjk232.messagingapp.Models.ContactInfo;
import com.app.wjk232.messagingapp.Models.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import rx.Observable;
import rx.subjects.BehaviorSubject;

/**
 * Created by rogerycecy on 7/24/2016.
 */
public class APIController extends AppCompatActivity implements Serializable {

    final ServerAPI serverAPI;
    final Crypto myCrypto;
    final Context context;
    static String imgPath;
    ServerAPI.Listener listen;
    HashMap<String,ServerAPI.UserInfo> myUserMap = new HashMap<>();
    BehaviorSubject<Notification> loginStatus = BehaviorSubject.create();
    BehaviorSubject<Notification> loginStatusUser = BehaviorSubject.create();
    BehaviorSubject<Message> receivedMessage = BehaviorSubject.create();
    ContactInfo contact;
    Toast toast;
    Toast toastStatus;

    private String getUserName(){
        return "rogelioE";
    }
    private String getServerName(){
        return "129.115.27.54";
    }

    private static APIController ourInstance;

    public Observable<Notification> getStatus(){
        return this.loginStatus;
    }
    public Observable<Message> getMessage(){return this.receivedMessage;}
    public Observable<Notification> getUserStatus(){return this.loginStatusUser;}
    public static APIController getInstance(Context applicationContext) {
        if(ourInstance==null){
            ourInstance = new APIController(applicationContext);
        }
        return ourInstance;
    }

    public APIController (final Context context) {
        SharedPreferences prefs = context.getSharedPreferences("keys",Context.MODE_PRIVATE);
        String serverName = prefs.getString("ServerName","129.115.27.54");
        toast = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        toastStatus = Toast.makeText(context,"",Toast.LENGTH_SHORT);
        this.context = context;
        myCrypto = new Crypto(prefs);
        myCrypto.saveKeys(prefs);
        serverAPI = ServerAPI.getInstance(context.getApplicationContext(),
                myCrypto);

        serverAPI.setServerName(getServerName());
        serverAPI.setServerPort("25666");
        listen = new ServerAPI.Listener() {
            @Override
            public void onCommandFailed(String commandName, VolleyError volleyError) {
                toast.setText(String.format("command %s failed!", commandName));
                toast.show();
                volleyError.printStackTrace();
            }

            @Override
            public void onGoodAPIVersion() {
                toast.setText("API Version Matched!");
                toast.show();
            }

            @Override
            public void onBadAPIVersion() {
                toast.setText("API Version Mismatch!");
                toast.show();
            }

            @Override
            public void onRegistrationSucceeded() {
                toast.setText("Registered!");
                toast.show();
            }

            @Override
            public void onRegistrationFailed(String reason) {
                toast.setText("Not registered!");
                toast.show();
            }

            @Override
            public void onLoginSucceeded() {
                toastStatus.setText("Logged in!");
                toastStatus.show();
                SharedPreferences prefs = context.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("login", true);
                editor.commit();
                loginStatusUser.onNext(new Notification.LogIn("rogelioE"));
            }

            @Override
            public void onLoginFailed(String reason) {
                toastStatus.setText("Not logged in!");
                toastStatus.show();
            }

            @Override
            public void onLogoutSucceeded() {
                toastStatus.setText("Logged out!");
                toastStatus.show();
                SharedPreferences prefs = context.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("login", false);
                editor.commit();
                loginStatusUser.onNext(new Notification.LogOut("rogelioE"));
            }

            @Override
            public void onLogoutFailed(String reason) {
                toastStatus.setText("Not logged out!");
                toastStatus.show();
            }

            @Override
            public void onUserInfo(ServerAPI.UserInfo info) {
                myUserMap.put(info.username, info);
            }

            @Override
            public void onUserNotFound(String username) {
                toast.setText(String.format("user %s not found!", username));
                toast.show();
            }

            @Override
            public void onContactLogin(String username) {
                toastStatus.setText(String.format("user %s logged in", username));
                toastStatus.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 800);
                //checkLogin(username, 1);
                loginStatus.onNext(new Notification.LogIn(username));
            }

            @Override
            public void onContactLogout(String username) {
                toastStatus.setText(String.format("user %s logged out", username));
                toastStatus.show();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, 800);
                //checkLogin(username, 1);
                loginStatus.onNext(new Notification.LogOut(username));
            }

            @Override
            public void onSendMessageSucceeded(Object key) {
                toast.setText(String.format("sent a message"));
                toast.show();
            }

            @Override
            public void onSendMessageFailed(Object key, String reason) {
                toast.setText(String.format("failed to send a message"));
                toast.show();
            }

            @Override
            public void onMessageDelivered(String sender, String recipient, String subject, String body, long born_on_date, long time_to_live) {
                toast.setText(String.format("got message from %s", sender));
                toast.show();
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        toast.cancel();
//                    }
//                }, 800);
                DataBaseHandler db = DataBaseHandler.getInstance(context);
                long id = db.addMessage(new Message(0, sender, subject, body, System.currentTimeMillis(), time_to_live));
                Message m = db.getMessage((int) id);
                m.setOrgTTL(db.getMessage((int) id).getTimeToLive_ms());
                receivedMessage.onNext(m);
                db.close();
            }
        };
        serverAPI.registerListener(listen);
        getUserInfo(getUserName());

    }

    public Crypto getMyCrypto (){
        return this.myCrypto;
    }

    public void getUserInfo(String name){
        serverAPI.setServerName(getServerName());
        serverAPI.getUserInfo(name);
    }

    public void Login() {
        serverAPI.setServerName(getServerName());
        serverAPI.login(getUserName(), myCrypto);
    }
    public void Logout() {
        serverAPI.setServerName(getServerName());
        serverAPI.logout(getUserName(),myCrypto);
    }
    public void Register() {
        serverAPI.setServerName(getServerName());

        InputStream is;
        byte[] buffer = new byte[0];
        try {
            is = context.getAssets().open("images/contacts.png");
            buffer = new byte[is.available()];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String username = getUserName();
        serverAPI.register(username, Base64.encodeToString(buffer,Base64.DEFAULT).trim(), myCrypto.getPublicKeyString());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                getUserInfo(getUserName());
                return;
            }
        }, 500);
        return;
    }
    public void doRegisterContact(String username, ArrayList<String> list){
        serverAPI.setServerName(getServerName());
        ArrayList<String> contacts = list;
        if(contact != null)
            contacts.add(username);
        serverAPI.registerContacts(getUserName(),contacts);
        return;
    }
    public ContactInfo getContactInfo(final String name){
        serverAPI.setServerName(getServerName());
        serverAPI.getUserInfo(name);

        if(myUserMap.containsKey(name)){
            ContactInfo contact = new ContactInfo(0,myUserMap.get(name).username,myUserMap.get(name).username,myUserMap.get(name).username, Base64.encodeToString(myUserMap.get(name).publicKey.getEncoded(),Base64.DEFAULT),myUserMap.get(name).username,myUserMap.get(name).image);
            return contact;
        }
        return null;

    }

    boolean shouldStopPolling = false;

    public void unregisterListener(){
        serverAPI.unregisterListener(listen);
    }

    synchronized private boolean shouldContinuePolling(){return !shouldStopPolling;}

    synchronized public void stopPolling(){shouldStopPolling = true;}

    public void startPollingThread(){
        shouldStopPolling=false;
        (new Thread(){
            @Override
            public void run(){
                while(shouldContinuePolling()) {
                    try {
                        Thread.sleep(2000);
                        serverAPI.startPushListener("rogelioE");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void sendMessaage(String name , Message msg){
        serverAPI.setServerName(getServerName());
        serverAPI.getUserInfo(name);

        if(myUserMap.containsKey(name)) {
            serverAPI.sendMessage(new Object(), // I don't have an object to keep track of, but I need one!
                    myUserMap.get(name).publicKey,
                    getUserName(),
                    name,
                    msg.getSubject(),
                    msg.getMessage(),
                    System.currentTimeMillis(),
                    msg.getTimeToLive_ms());
        } else {
            Log.d("Main",name + " info not available");
        }
        return;
    }

    public void removeContact(String contact){
        serverAPI.removeContact(getUserName(),contact);
    }

    public void saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imagedir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"contacts.png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.imgPath = directory.getAbsolutePath();
    }

    public Bitmap loadImage()
    {

        try {
            File f=new File(imgPath, "contacts.png");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
