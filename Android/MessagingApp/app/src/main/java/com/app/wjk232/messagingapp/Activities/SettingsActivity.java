package com.app.wjk232.messagingapp.Activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.wjk232.messagingapp.APIController;
import com.app.wjk232.messagingapp.Models.ContactInfo;
import com.app.wjk232.messagingapp.API.Crypto;
import com.app.wjk232.messagingapp.DataBaseHandler;
import com.app.wjk232.messagingapp.Notification;
import com.app.wjk232.messagingapp.R;
import com.app.wjk232.messagingapp.API.ServerAPI;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import rx.Observer;

public class SettingsActivity extends AppCompatActivity {

    APIController api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true)


    }
    public void displayUserInfo(){
        api.getUserStatus().subscribe(new Observer<Notification>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Notification notification) {
                if(notification instanceof Notification.LogOut){
                    TextView keyTextView = (TextView)findViewById(R.id.key_view);
                    keyTextView.setText("");
                    ImageView img = (ImageView) findViewById(R.id.img_view);
                    img.setImageBitmap(null);

                }
                if(notification instanceof Notification.LogIn ){
                    TextView keyTextView = (TextView)findViewById(R.id.key_view);
                    keyTextView.setText("Public: " + api.getMyCrypto().getPublicKeyString() + "\n/Private: \n" + api.getMyCrypto().getPrivateKeyString());
                    Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.contacts);
                    ImageView img = (ImageView) findViewById(R.id.img_view);
                    img.setImageBitmap(image);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        api = APIController.getInstance(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("mysettings",Context.MODE_PRIVATE);
        boolean status = prefs.getBoolean("login", false);
        if(status){
            TextView keyTextView = (TextView)findViewById(R.id.key_view);
            keyTextView.setText("Public: " + api.getMyCrypto().getPublicKeyString() + "\n/Private: \n" + api.getMyCrypto().getPrivateKeyString());
            Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.contacts);
            ImageView img = (ImageView) findViewById(R.id.img_view);
            img.setImageBitmap(image);
        }
        displayUserInfo();
    }


    public void doLogin(View view) {
        api.Login();
        api.startPollingThread();
        return;
    }
    public void doLogout(View view) {
        api.Logout();
        api.stopPolling();
        return;

    }
    public void doRegister (View view){
        api.Register();
        return;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

}