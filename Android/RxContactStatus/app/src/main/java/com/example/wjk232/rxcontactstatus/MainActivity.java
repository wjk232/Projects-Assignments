package com.example.wjk232.rxcontactstatus;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.MainThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.wjk232.rxcontactstatus.stages.GetChallengeStage;
import com.example.wjk232.rxcontactstatus.stages.GetServerKeyStage;
import com.example.wjk232.rxcontactstatus.stages.LogInStage;
import com.example.wjk232.rxcontactstatus.stages.PollingStage;
import com.example.wjk232.rxcontactstatus.stages.RegisterContactsStage;
import com.example.wjk232.rxcontactstatus.stages.RegistrationStage;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.schedulers.TimeInterval;
import rx.subjects.BehaviorSubject;

public class MainActivity extends AppCompatActivity {

    Crypto myCrypto;

    String username = "RxRe";
    String server_name = "http://129.115.27.54:25666";
    ArrayList<String> contacts;
    TextView txtAlice;
    TextView txtBob;
    BehaviorSubject<String> Alice = BehaviorSubject.create("");
    BehaviorSubject<String> Bob = BehaviorSubject.create("");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contacts = new ArrayList<>();
        contacts.add("alice");
        contacts.add("bob");
        txtAlice = (TextView)findViewById(R.id.alice);
        txtAlice.setText("alice");
        txtBob = (TextView)findViewById(R.id.bob);
        txtBob.setText("bob");

        Alice.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                if(s.equals("active"))
                    txtAlice.setBackgroundColor(Color.BLUE);
                if(s.equals("notactive"))
                    txtAlice.setBackgroundColor(Color.RED);
            }
        });
        Bob.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                if(s.equals("active"))
                    txtBob.setBackgroundColor(Color.BLUE);
                if(s.equals("notactive"))
                    txtBob.setBackgroundColor(Color.RED);
            }
        });

        myCrypto = new Crypto(getPreferences(Context.MODE_PRIVATE));

        Observable.just(0) // the value doesn't matter, it just kicks things off
                .observeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.newThread())
                .flatMap(new GetServerKeyStage(server_name))
                .flatMap(new RegistrationStage(server_name, username,
                                               getBase64Image(), myCrypto.getPublicKeyString()))
                .flatMap(new GetChallengeStage(server_name,username,myCrypto))
                .flatMap(new LogInStage(server_name, username))
                .flatMap(new RegisterContactsStage(server_name, username, contacts))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Notification>() {
            @Override
            public void onCompleted() {

                // now that we have the initial state, start polling for updates

                Observable.interval(0,1, TimeUnit.SECONDS, Schedulers.newThread())
                     //   .take(5) // would only poll five times
                     //   .takeWhile( <predicate> ) // could stop based on a flag variable
                        .subscribe(new Observer<Long>() {
                            @Override
                            public void onCompleted() {}

                            @Override
                            public void onError(Throwable e) {}

                            @Override
                            public void onNext(Long numTicks) {
                                Log.d("POLL","Polling "+numTicks);
                                Observable.just(0)
                                        .subscribeOn(Schedulers.newThread())
                                        .flatMap(new PollingStage(server_name,username,contacts))
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<Notification>() {
                                            @Override
                                            public void onCompleted() {
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }

                                            @Override
                                            public void onNext(Notification notification) {
                                                if(notification instanceof Notification.LogIn) {
                                                    if(((Notification.LogIn)notification).username.equals("bob"))
                                                        Bob.onNext("active");
                                                    if(((Notification.LogIn)notification).username.equals("alice"))
                                                        Alice.onNext("active");
                                                    Log.d("LOG","User "+((Notification.LogIn)notification).username+" is logged in");
                                                }
                                                if(notification instanceof Notification.LogOut) {
                                                    if(((Notification.LogOut)notification).username.equals("bob"))
                                                        Bob.onNext("notactive");
                                                    if(((Notification.LogOut)notification).username.equals("alice"))
                                                        Alice.onNext("notactive");
                                                    Log.d("LOG","User "+((Notification.LogOut)notification).username+" is logged out");
                                                }
                                            }
                                        });
                            }
                        });
            }

            @Override
            public void onError(Throwable e) {
                Log.d("LOG","Error: ",e);
            }

            @Override
            public void onNext(Notification notification) {
                // handle initial state here
                Log.d("LOG","Next "+ notification);
                if(notification instanceof Notification.LogIn) {
                    if(((Notification.LogIn)notification).username.equals("bob"))
                        Bob.onNext("active");
                    if(((Notification.LogIn)notification).username.equals("alice"))
                        Alice.onNext("active");
                    Log.d("LOG","User "+((Notification.LogIn)notification).username+" is logged in");
                }
                if(notification instanceof Notification.LogOut) {
                    if(((Notification.LogOut)notification).username.equals("bob"))
                        Bob.onNext("notactive");
                    if(((Notification.LogOut)notification).username.equals("alice"))
                        Alice.onNext("notactive");
                    Log.d("LOG","User "+((Notification.LogOut)notification).username+" is logged out");
                }

            }
        });

    }

    String getBase64Image(){
        InputStream is;
        byte[] buffer = new byte[0];
        try {
            is = getAssets().open("images/ic_android_black_24dp.png");
            buffer = new byte[is.available()];
            is.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(buffer,Base64.DEFAULT).trim();
    }

    boolean register(String username, String base64Image, String keyString){


        return true;
    }

}
