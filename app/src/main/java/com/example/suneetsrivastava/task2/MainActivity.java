package com.example.suneetsrivastava.task2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private Button clickButton;
    private static ContentResolver contentResolver;
    FetchContacts fetchContacts;
    private int checkPermission;
    private static ArrayList<Contacts> contactsArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clickButton = (Button) findViewById(R.id.buttonClick);
        contactsArrayList = new ArrayList<>();
        checkPermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS);
        contentResolver=getContentResolver();
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
                }
                fetchContacts = new FetchContacts();
                fetchContacts.execute();
            }
        });
    }

    private static class FetchContacts extends AsyncTask<Void,Void,Void>{
        Cursor cursor;
        @Override
        protected Void doInBackground(Void... voids) {
            cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,
            null);
            if(cursor.getCount()>0)
            {
                while(cursor.moveToNext()){
                    String id=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name=cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String phneNo="";
                    if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0){
                        Cursor phneCursor=contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"=?",new String[]{id},null);
                        while(phneCursor.moveToNext()){

                            phneNo = phneCursor.getString(phneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contactsArrayList.add(new Contacts(phneNo,name));


                        }

                        phneCursor.close();
                    }

                }
            }
            cursor.close();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            exportToCSV();

        }
    }

    private static void exportToCSV(){



    }
}
