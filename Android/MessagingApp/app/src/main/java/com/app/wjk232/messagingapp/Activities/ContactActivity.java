package com.app.wjk232.messagingapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.wjk232.messagingapp.APIController;
import com.app.wjk232.messagingapp.Models.ContactInfo;
import com.app.wjk232.messagingapp.DataBaseHandler;
import com.app.wjk232.messagingapp.R;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class ContactActivity extends AppCompatActivity {
    private ContactInfo contact= null;
    private DataBaseHandler db;
    private ContactInfo contactInfo;
    private APIController api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        api = APIController.getInstance(getApplicationContext());
        SharedPreferences prefs = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        boolean status = prefs.getBoolean("login", false);
        if(status) {
            displayContactInfo();
        }else{
            ImageButton trash = (ImageButton)findViewById(R.id.trash_button);
            ImageButton populate = (ImageButton)findViewById(R.id.populate);
            Button save = (Button) findViewById(R.id.save_button);
            trash.setEnabled(false);
            populate.setEnabled(false);
            save.setEnabled(false);
        }


    }
    //save contact if added
    public void saveContact(View view){
        try {
            if (contactInfo.getPublicKey() != null) {
                db.addContact(contactInfo);
                api.doRegisterContact("rogelioE",db.getAllContactsNames());
                db.close();
            }
        }catch(NullPointerException e){

        }
        finish();
    }
    //delete contact if there's one
    public void goContactsTrash(View view){
        if(contact != null) {
            api.removeContact(contact.getUserName());
            db.deleteContact(contact);
            db.close();
        }
        finish();
    }

    //populates contact info on adding a contact
    public void toPopulate(View view){
        contactInfo = new ContactInfo();
        final TextView key = (TextView)findViewById(R.id.key_view);
        final TextView filter = (TextView) findViewById(R.id.name_view);
        api.getUserInfo(filter.getText().toString());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                ContactInfo user = api.getContactInfo(filter.getText().toString());
                if(user != null){
                    contactInfo.setUserName(user.getUserName());
                    contactInfo.setPublicKey(user.getPublicKey());
                    contactInfo.setEmailAddress(user.getUserName()+"@email.com");
                    contactInfo.setFirstName(user.getUserName());
                    contactInfo.setLastName(user.getUserName());
                    contactInfo.setImageName(user.getImageName());
                    loadImage(contactInfo.getImageName());
                    key.setText(contactInfo.getPublicKey());
                }
                return;
            }
        }, 800);

    }

    //displays the contact info
    public void displayContactInfo(){
        Intent intent = getIntent();    //get intent
        contact = (ContactInfo)getIntent().getSerializableExtra("settings");
        if(contact != null) {
            EditText name = (EditText) findViewById(R.id.name_view);
            TextView key = (TextView) findViewById(R.id.key_view);
            Button save = (Button) findViewById(R.id.save_button);

            save.setEnabled(false);
            name.setKeyListener(null);
            save.setTextColor(Color.parseColor("#ffffff"));
            name.setText(contact.getUserName());
            key.setText(contact.getPublicKey());
            loadImage(contact.getImageName());
        }
    }

    private void loadImage(String userImg)
    {

        InputStream is = new ByteArrayInputStream(Base64.decode(userImg, Base64.DEFAULT));
        Bitmap image = BitmapFactory.decodeStream(is);
        ImageView img = (ImageView) findViewById(R.id.img_view);

        img.setImageBitmap(image);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
            db.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        db = DataBaseHandler.getInstance(getApplicationContext());
    }
}
