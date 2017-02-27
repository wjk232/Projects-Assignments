package com.app.wjk232.messagingapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.app.wjk232.messagingapp.API.Crypto;
import com.app.wjk232.messagingapp.APIController;
import com.app.wjk232.messagingapp.Models.ContactInfo;
import com.app.wjk232.messagingapp.DataBaseHandler;
import com.app.wjk232.messagingapp.Models.Message;
import com.app.wjk232.messagingapp.R;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ReadActivity extends AppCompatActivity {

    private Message msg;
    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //display message
             displayMessage();

    }

    //display message
    public void displayMessage() {
        Intent intent = getIntent();    //get intent
        msg = (Message) getIntent().getSerializableExtra("msg"); //get message object
        TextView displayMsg = (TextView) findViewById(R.id.view_msg);
        TextView senderName = (TextView) findViewById(R.id.sender_name);
        TextView subjectMsg = (TextView) findViewById(R.id.subject_line);
        senderName.setText(msg.getUserName());
        subjectMsg.setText(msg.getSubject());
        displayMsg.setText(msg.getMessage());
        // Get a handler that can be used to post to the main thread
        Observable.interval(0,1, TimeUnit.SECONDS, Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {}

                    @Override
                    public void onError(Throwable e) {}

                    @Override
                    public void onNext(Long numTicks) {
                        TextView TTL = (TextView)findViewById(R.id.TTL);
                        TTL.setText("" + (int)msg.percentLeftToLive()/1000+ "s");
                        if((int)msg.percentLeftToLive()/1000 <= 0)
                            finish();

                    }
                });

        thread = new Thread(new Runnable() {
            @Override
            public void run() {


            }
        });
        thread.start();
    }

    //on click reply button go to compose with appropriate insertion
    public void goComposeReply(View view){
        Intent intent = new Intent(this,ComposeActivity.class);
        intent.putExtra("subject", msg.getSubject());
        intent.putExtra("username", msg.getUserName());
        startActivity(intent);
    }

    //on click trash go to main activity and delete message
    public void goHomeTrash(View view){
        Intent intent = new Intent(this,MainActivity.class);
        if(msg != null) {
            DataBaseHandler db = DataBaseHandler.getInstance(this);
            db.deleteMessage(msg);
            db.close();
        }
        startActivity(intent);
        finish();
    }
    //Decrypt message
    public String Decrypt(String message, String privateKey){

        try {
            byte[] bytesMessage = Base64.decode(message,Base64.DEFAULT);
            byte[] bytePrivateKey = Base64.decode(privateKey,Base64.DEFAULT);
            PKCS8EncodedKeySpec X509privateKey = new PKCS8EncodedKeySpec(bytePrivateKey);
            KeyFactory keySpec = KeyFactory.getInstance("RSA");
            PrivateKey privateKeyN = keySpec.generatePrivate(X509privateKey);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE,privateKeyN);
            return new String (cipher.doFinal(bytesMessage)) ;
            //return cipher.doFinal(message.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
