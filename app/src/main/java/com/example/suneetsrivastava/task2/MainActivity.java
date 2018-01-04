package com.example.suneetsrivastava.task2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class MainActivity extends AppCompatActivity {
    private Button clickButton;
    private static ContentResolver contentResolver;
    FetchContacts fetchContacts;
    private int checkPermission;
    static File f;
    private static Context context;
    private static ArrayList<String[]> contactsArrayList;
    static View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v =findViewById(R.id.linearLayout);
        clickButton = (Button) findViewById(R.id.buttonClick);
        contactsArrayList = new ArrayList<>();
        checkPermission = ActivityCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS);
        contentResolver=getContentResolver();
        context=this;
        clickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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
                            contactsArrayList.add(new String[]{phneNo,name});


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
            Toast.makeText(context, "Written to CSV File In Documents Directory", Toast.LENGTH_SHORT).show();

        }
    }

    private static void exportToCSV()  {

        f = new File(context.getExternalFilesDir(Environment.DIRECTORY_DCIM),"contacts.csv");

        try {
            FileWriter fileWriter = new FileWriter(f);
            CSVWriter csvWriter = new CSVWriter(fileWriter);
            csvWriter.writeAll(contactsArrayList);
            csvWriter.close();
            compressFile(f);
            Snackbar snackbar = Snackbar.make(v,"Zip File Created Succesfully",Snackbar.LENGTH_SHORT);
            snackbar.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private static void compressFile(File file){
        String zipFileName="contacts.zip";
        try{
                BufferedInputStream inputStream ;
                FileOutputStream fileOutputStream = new FileOutputStream(zipFileName);
                ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(fileOutputStream));
                byte data[]= new byte[1024];
                FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                inputStream = new BufferedInputStream(fileInputStream,1024);
                ZipEntry zipEntry = new ZipEntry(zipFileName);
                zipOutputStream.putNextEntry(zipEntry);
                int c;
                while ((c= inputStream.read(data,0,1024))!=-1){
                    zipOutputStream.write(data,0,c);
                }
                inputStream.close();
                fileOutputStream.close();
                zipOutputStream.close();

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
