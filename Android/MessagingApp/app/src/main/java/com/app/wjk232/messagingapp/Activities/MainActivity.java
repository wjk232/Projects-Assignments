package com.app.wjk232.messagingapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.app.wjk232.messagingapp.APIController;
import com.app.wjk232.messagingapp.BackgroundService;
import com.app.wjk232.messagingapp.Models.ContactInfo;
import com.app.wjk232.messagingapp.API.Crypto;
import com.app.wjk232.messagingapp.DataBaseHandler;
import com.app.wjk232.messagingapp.Models.Message;
import com.app.wjk232.messagingapp.Adapters.MessageAdapter;
import com.app.wjk232.messagingapp.Notification;
import com.app.wjk232.messagingapp.R;
import com.app.wjk232.messagingapp.API.ServerAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;


public class MainActivity extends AppCompatActivity {


    private Context context;
    private List<Message> messages;
    private DataBaseHandler db ;
    ArrayAdapter<Message> adapter;
    private  KeyPair keyPair;
    Activity activity = this;
    ServerAPI.Listener listen;
    APIController api;
    Subscription sub;
    static int lasMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;
        api = APIController.getInstance(getApplicationContext());
        SharedPreferences sharedPref = getSharedPreferences("mysettings",Context.MODE_PRIVATE);
        boolean status = sharedPref.getBoolean("login", false);
        if(status)
            api.startPollingThread();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //display the list of messages
    public void listOfMessage(){
        messages = db.getAllMessages();
        if(adapter == null) {
            for(int i =0;i < db.getMessagesCount();i++){
                if(messages.get(i).percentLeftToLive() <= 0){
                    db.deleteMessage(messages.get(i));
                    messages.remove(i);
                }
            }

            ListView listView = (ListView) findViewById(R.id.list_view);
            adapter = new MessageAdapter(activity, messages);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Message msgObj = adapter.getItem(position);
                    Intent intent = new Intent(context, ReadActivity.class);
                    intent.putExtra("msg", msgObj);
                    startActivity(intent);
                }
            });

        }

    }

    //update list of messages onResume
    @Override
    protected void onStart(){
        super.onStart();
        stopService(new Intent(getBaseContext(), BackgroundService.class));
        Intent intent = getIntent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("onDestroy","on Destroyed");
        SharedPreferences prefs = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        if(prefs.getBoolean("login",false)) {
            Intent intent = new Intent(this, BackgroundService.class);
            startService(intent);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences sharedPref = getSharedPreferences("mysettings",Context.MODE_PRIVATE);
        boolean status = sharedPref.getBoolean("login", false);
        if(status) {
            sub.unsubscribe();
        }
        db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = DataBaseHandler.getInstance(this);
        SharedPreferences sharedPref = getSharedPreferences("mysettings",Context.MODE_PRIVATE);
        boolean status = sharedPref.getBoolean("login", false);

        if(status) {
            ListView listView = (ListView) findViewById(R.id.list_view);
            listView.setVisibility(View.VISIBLE);
            listOfMessage();
            setObserver();
        }else{
            ListView listView = (ListView) findViewById(R.id.list_view);
            listView.setVisibility(View.INVISIBLE);
        }

    }

    // go to settings intent
    public void toSettings(MenuItem item){
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        return;
    }

    //go to contacts intent
    public void toContacts(MenuItem item){
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        return;
    }

    //go to compose message intent
    public void toCompose(MenuItem item){
        Intent intent = new Intent(this, ComposeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        return;
    }

    //message observer
    public void setObserver(){
        Observable <Message> observable = api.getMessage();
        Observer ob = (new Observer<Message>() {
            @Override
            public void onCompleted() {
            }
            @Override
            public void onError(Throwable e) {
                Log.d("debug","error debugging " + e
);
            }

            @Override
            public void onNext(Message message) {
                Log.d("debug","debugging " + message.getTimeToLive_ms() + " " + message.getId() + " " );
                if(lasMessage != message.getId()) {
                    adapter.add(message);
                    lasMessage = message.getId();
                }

            }
        });
        sub = observable.subscribe(ob);
    }

    //get public and private encode string
    public KeyPair keypairGen() {
        keyPair = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);            // initialize key generator
            keyPair = keyGen.generateKeyPair(); // generate pair of keys

        } catch (Exception e) {
            System.out.println("KeyGen Error");
        }
        return keyPair;
    }

    //keys convert to string
    public String convertKey(PublicKey publickey, PrivateKey privateKey){
        String keystring;
        if(publickey != null)
            keystring = Base64.encodeToString(publickey.getEncoded(),Base64.DEFAULT);
        else
            keystring = Base64.encodeToString(privateKey.getEncoded(),Base64.DEFAULT);
        return keystring;
    }
    private String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
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
        return directory.getAbsolutePath();
    }



}
