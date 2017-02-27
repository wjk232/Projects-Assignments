package com.app.wjk232.messagingapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.wjk232.messagingapp.Models.ContactInfo;
import com.app.wjk232.messagingapp.Models.Message;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by rogelio Espinoza on 6/25/2016.
 */
public class DataBaseHandler extends SQLiteOpenHelper{
    private static DataBaseHandler mInstance = null;
    public static DataBaseHandler getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new DataBaseHandler(ctx.getApplicationContext());
        }
        return mInstance;
    }
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "contacts_db";
    // Contacts table name
    private static final String TABLE_CONTACTS = "contacts";
    private static final String TABLE_MESSAGES = "messages";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FIRSTNAME = "firstname";
    private static final String KEY_LASTNAME = "lastname";
    private static final String KEY_PUBLIC = "public_key";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_IMAGE = "image";

    // Messages Table Columns names
    private static final String KEY_MSG_ID = "id";
    private static final String KEY_MSG_USERNAME = "username";
    private static final String KEY_MSG_SUBJECT = "subject";
    private static final String KEY_MSG_MESSAGE = "message";
    private static final String KEY_MSG_BT = "btime";
    private static final String KEY_TTL = "ttl";

    private DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        return;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_USERNAME + " TEXT,"
                + KEY_FIRSTNAME + " TEXT," + KEY_LASTNAME + " TEXT," + KEY_PUBLIC + " TEXT,"
                + KEY_EMAIL + " TEXT," + KEY_IMAGE + " TEXT" +  ")";
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + KEY_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_MSG_USERNAME + " TEXT,"
                + KEY_MSG_SUBJECT + " TEXT," + KEY_MSG_MESSAGE + " TEXT,"
                + KEY_MSG_BT + " INTEGER,"+ KEY_TTL+ " INTEGER" + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_MESSAGES_TABLE);
        return;
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);

        // Creating tables again
        onCreate(db);
        return;
    }

    // Adding new contact
    public void addContact(ContactInfo contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_USERNAME, contact.getUserName()); // Contact UserName
        values.put(KEY_FIRSTNAME, contact.getFirstName()); // Contact FirstName
        values.put(KEY_LASTNAME, contact.getLastName()); // Contact LastName
        values.put(KEY_PUBLIC, contact.getPublicKey()); // Contact public key
        values.put(KEY_EMAIL, contact.getEmailAddress()); // Contact EmailAddress
        values.put(KEY_IMAGE, contact.getImageName()); // Contact Image
        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        return;
    }
    // Getting one contact
    public ContactInfo getContact(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTACTS, new String[]{KEY_ID,
                        KEY_USERNAME, KEY_FIRSTNAME, KEY_LASTNAME, KEY_PUBLIC, KEY_EMAIL, KEY_IMAGE}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ContactInfo contact = new ContactInfo(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
       // return Contact
        return contact;
    }
    // Getting All Contact
    public List<ContactInfo> getAllContacts() {
        List<ContactInfo> contactsList = new ArrayList<ContactInfo>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ContactInfo contact = new ContactInfo();
                contact.setId(Integer.parseInt(cursor.getString(0)));
                contact.setUserName(cursor.getString(1));
                contact.setFirstName(cursor.getString(2));
                contact.setLastName(cursor.getString(3));
                contact.setPublicKey(cursor.getString(4));
                contact.setEmailAddress(cursor.getString(5));
                contact.setImageName(cursor.getString(6));
                // Adding contact to list
                contactsList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactsList;
    }
    // Getting All Contact
    public ArrayList<String> getAllContactsNames() {
        ArrayList<String> contactsList = new ArrayList<String>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding contact to list
                contactsList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        // return contact list
        return contactsList;
    }
    // Getting contacts Count
    public int getContactsCount() {
        String countQuery = "SELECT * FROM " + TABLE_CONTACTS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);


        // return count
        return cursor.getCount();
    }

   // Updating a Contact
    public int updateContact(ContactInfo contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, contact.getUserName()); // Contact UserName
        values.put(KEY_FIRSTNAME, contact.getFirstName()); // Contact FirstName
        values.put(KEY_LASTNAME, contact.getLastName()); // Contact LastName
        values.put(KEY_PUBLIC, contact.getPublicKey()); // Contact public key
        values.put(KEY_EMAIL, contact.getEmailAddress()); // Contact EmailAddress
        values.put(KEY_IMAGE, contact.getImageName()); // Contact Image

        // updating row
        return db.update(TABLE_CONTACTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
    }

    // Deleting a contact
    public void deleteContact(ContactInfo contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getId()) });
        return;
    }
    /**
     * Remove all users and groups from database.
     */
    /*public void removeAllContacts()
    {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_CONTACTS, null, null);
        //db.delete(TAB_USERS_GROUP, null, null);
    }*/

    //messages database structure
    // Adding new Message
    public long addMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_MSG_USERNAME, message.getUserName()); // msg UserName
        values.put(KEY_MSG_SUBJECT, message.getSubject()); // msg LastName
        values.put(KEY_MSG_MESSAGE, message.getMessage()); // msg Name
        values.put(KEY_MSG_BT, message.getBornTime_ms()); // msg bt
        values.put(KEY_TTL, message.getTimeToLive_ms()); // msg ttl
        // Inserting Row
        long id = db.insert(TABLE_MESSAGES, null, values);
        return id;
    }
    // Getting one message
    public Message getMessage(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MESSAGES, new String[]{KEY_MSG_ID,
                        KEY_MSG_USERNAME, KEY_MSG_SUBJECT, KEY_MSG_MESSAGE, KEY_MSG_BT, KEY_TTL}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Message message = new Message(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3),Long.parseLong(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));
        cursor.close();
        // return msg
        return message;
    }
    // Getting All msgs
    public List<Message> getAllMessages() {
        List<Message> messagesList = new ArrayList<Message>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Message message = new Message();
                message.setId(Integer.parseInt(cursor.getString(0)));
                message.setUserName(cursor.getString(1));
                message.setSubject(cursor.getString(2));
                message.setMessage(cursor.getString(3));
                message.setBornTime_ms(Long.parseLong(cursor.getString(4)));
                message.setTimeToLive_ms(Long.parseLong(cursor.getString(5)));
                // Adding contact to list
                messagesList.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // return contact list
        return messagesList;
    }
    // Getting contacts Count
    public int getMessagesCount() {
        String countQuery = "SELECT * FROM " + TABLE_MESSAGES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        return cursor.getCount();
    }
    public void update_byID(int id,int ttl){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TTL, ttl);
        db.update(TABLE_MESSAGES, values, KEY_MSG_ID+"="+id, null);
    }
    // Deleting a Message
    public void deleteMessage(Message message) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, KEY_ID + " = ?",
                new String[] { String.valueOf(message.getId()) });
        return;
    }
    /**
     * Remove all messages
     */
    public void removeAllMessages()
    {
        SQLiteDatabase db = this.getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(TABLE_MESSAGES, null, null);
        return;
    }
}
