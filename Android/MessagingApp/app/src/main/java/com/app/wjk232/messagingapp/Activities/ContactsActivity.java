package com.app.wjk232.messagingapp.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.app.wjk232.messagingapp.APIController;
import com.app.wjk232.messagingapp.Adapters.ContactAdapter;
import com.app.wjk232.messagingapp.Models.ContactInfo;
import com.app.wjk232.messagingapp.API.Crypto;
import com.app.wjk232.messagingapp.DataBaseHandler;
import com.app.wjk232.messagingapp.Models.Message;
import com.app.wjk232.messagingapp.Notification;
import com.app.wjk232.messagingapp.R;
import com.app.wjk232.messagingapp.API.ServerAPI;

import java.util.HashMap;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.subjects.BehaviorSubject;

public class ContactsActivity extends AppCompatActivity {

    private Context context;
    private ListView list;
    private List<ContactInfo> contacts;
    public static Activity contactsActivity;
    APIController api;
    DataBaseHandler db;
    Subscription sub;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        contactsActivity = this;
        context=this;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
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

    //create list of contacts
    public void listOfContacts(){
        contacts = db.getAllContacts();

        Integer imgint= R.drawable.contacts;

        ContactAdapter contactAdapter = new
                ContactAdapter(this,contacts,imgint);
        list=(ListView)findViewById(R.id.contacts_list_view);
        list.setAdapter(contactAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                ImageView imageView = (ImageView) view.findViewById(R.id.img);
                if(imageView.getVisibility() == View.VISIBLE) {
                    Intent intent = new Intent(context,ComposeActivity.class);
                    intent.putExtra("email", contacts.get(position).getEmailAddress());
                    intent.putExtra("username", contacts.get(position).getUserName());
                    intent.putExtra("user", contacts.get(position));

                    if(getIntent().getBooleanExtra("option",false)) {
                        setResult(400, intent);
                        finish();
                    }else{
                        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    }
                }
            }
        });
        list.getAdapter().getCount();

    }

    public void addContact(MenuItem item){
        Intent intent = new Intent(this, ContactActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        boolean status = prefs.getBoolean("login", false);
        if(status)
            sub.unsubscribe();
        db.close();
    }

    @Override
    protected void onResume(){
        super.onResume();
        System.out.println(getIntent().getCategories());
        db = DataBaseHandler.getInstance(this);
        SharedPreferences prefs = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        boolean status = prefs.getBoolean("login", false);
        api = APIController.getInstance(getApplicationContext());
        if(status) {
            listOfContacts();
            setObserver();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //contacts observer
    public void setObserver(){
        if(db.getAllContactsNames() != null)
            api.doRegisterContact("rogelioE", db.getAllContactsNames());
        Observable<Notification> observable = api.getStatus();
        Observer<Notification> ob = (new Observer<Notification>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("debug","error debugging " + e);
            }

            @Override
            public void onNext(Notification notification) {
                List<ContactInfo> contact = db.getAllContacts();
                for(int i=0;i < contact.size();i++) {
                    if (notification instanceof Notification.LogIn) {
                        if(contact.get(i).getUserName().equals(((Notification.LogIn) notification).username)) {
                            View v = list.getChildAt(i - list.getFirstVisiblePosition());
                            if (v == null)
                                break;
                            ImageView imageView = (ImageView) v.findViewById(R.id.img);
                            v.setEnabled(true);
                            imageView.setVisibility(View.VISIBLE);
                        }
                    }
                    if (notification instanceof Notification.LogOut) {
                        if(contact.get(i).getUserName().equals((((Notification.LogOut) notification).username))) {
                            View v = list.getChildAt(i - list.getFirstVisiblePosition());
                            if (v == null)
                                break;
                            ImageView imageView = (ImageView) v.findViewById(R.id.img);
                            v.setEnabled(false);
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
        sub = observable.subscribe(ob);
    }
}
