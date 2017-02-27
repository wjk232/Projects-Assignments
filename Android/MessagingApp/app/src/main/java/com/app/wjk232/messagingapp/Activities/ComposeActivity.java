package com.app.wjk232.messagingapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.app.wjk232.messagingapp.API.Crypto;
import com.app.wjk232.messagingapp.APIController;
import com.app.wjk232.messagingapp.Models.ContactInfo;
import com.app.wjk232.messagingapp.Models.Message;
import com.app.wjk232.messagingapp.R;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class ComposeActivity extends AppCompatActivity {

    private Message message =null;
    private String subjectmsg;
    private String userName;
    private ImageButton button1;
    private Context context;
    private Message msg = new Message();
    private EditText messageContent;
    private byte[] encryptMessage;
    EditText subject;
    TextView username;
    APIController api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_popup, menu);
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

    public void senderInfo(){
        subjectmsg = (String) getIntent().getSerializableExtra("subject");
        userName = (String) getIntent().getSerializableExtra("username");
        subject = (EditText) findViewById(R.id.subject_line);
        username = (TextView) findViewById(R.id.to_name);
        messageContent = (EditText)findViewById(R.id.insert_msg);
        if( userName != null){
            username.setText(userName);
            if (subjectmsg != null)
                subject.setText(subjectmsg);
        }
    }
    //popup menu for TTL
    public void popup_menu(){

        button1 = (ImageButton) findViewById(R.id.ttl_button);
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(context, button1);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        //Toast.makeText(context, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        if(item.getItemId() == R.id.three_hundred)
                            msg.setTimeToLive_ms(300000);
                        if(item.getItemId() == R.id.sixty)
                            msg.setTimeToLive_ms(60000);
                        if(item.getItemId() == R.id.fifteen)
                            msg.setTimeToLive_ms(15000);
                        if(item.getItemId() == R.id.five)
                            msg.setTimeToLive_ms(5000);
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });

    }
    public void goToContacts(View view){
        Intent intent = new Intent(this, ContactsActivity.class);
        intent.putExtra("option",true);
        startActivityForResult(intent,400);
    }

    public void goHomeTrash(View view){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        finish();
    }

    public void goHomeSend(View view){
        final Intent intent = new Intent(this, MainActivity.class);
        api.getUserInfo(username.getText().toString());
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                ContactInfo user = api.getContactInfo(username.getText().toString());
                if(user != null){
                    encryptMessage = Encrypt(messageContent.getText().toString(), user.getPublicKey());
                    msg.setUserName(username.getText().toString());
                    msg.setSubject(subject.getText().toString());
                    msg.setMessage(messageContent.getText().toString());
                    api.sendMessaage(username.getText().toString(),msg);
                    finish();
                }
                return;
            }

        }, 600);
    }
    //encrypt message
    public byte[] Encrypt(String message, String publicKey) {
        try {

            byte[] bytePublicKey = Base64.decode(publicKey,Base64.DEFAULT);
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(bytePublicKey);
            KeyFactory keySpec = KeyFactory.getInstance("RSA");
            PublicKey publicKeyN = keySpec.generatePublic(X509publicKey);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKeyN);
            return cipher.doFinal(message.getBytes());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 400){
            subjectmsg = (String) data.getSerializableExtra("subject");
            userName = (String) data.getSerializableExtra("username");
            subject = (EditText) findViewById(R.id.subject_line);
            username = (TextView) findViewById(R.id.to_name);
            messageContent = (EditText)findViewById(R.id.insert_msg);
            if( userName != null){
                username.setText(userName);
                if (subjectmsg != null)
                    subject.setText(subjectmsg);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        api = APIController.getInstance(getApplicationContext());
        senderInfo();
        context = this;
        popup_menu();
    }


}
