package com.app.wjk232.messagingapp.Models;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import java.io.Serializable;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by rogelioespinoza on 6/19/2016.
 */
public class ContactInfo extends AppCompatActivity implements Serializable {

    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String publicKey;
    private String privateKey;
    private String imageName;
    private String emailAddress;


    public ContactInfo(){}

    public ContactInfo(int id,String username, String firstName, String lastName, String publicKey, String emailAddress,String imageName) {
        this.id = id;
        this.imageName = imageName;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.publicKey = publicKey;
        this.emailAddress = emailAddress;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return firstName +" " +  lastName ;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String keystoString() {
        return publicKey+" / "+privateKey ;
    }
}
