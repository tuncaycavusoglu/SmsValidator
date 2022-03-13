package com.example.smsvalidator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView simpleList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        readSms();
    }

    public void readSms(){
        final String SMS_URI_INBOX = "content://sms/inbox";
        Uri uri = Uri.parse(SMS_URI_INBOX);
        String[] select = new String[] { "address", "body", "date" };
        //Cursor cursor = getContentResolver().query(uri, select, "address LIKE '%TURKCELL%'", null, "date desc"+ " LIMIT 100");

        Cursor cursor = getContentResolver().query(uri, select, "address IN ("+senderList()+")", null, "date desc"+ " LIMIT 150");

        String nameList[] = new String[cursor.getCount()];
        String mesajList[] = new String[cursor.getCount()];
        String saatList[] = new String[cursor.getCount()];


        int sayac = 0;
        while (cursor.moveToNext()){
            nameList[sayac] = cursor.getString(0) + " - " + (String) DateFormat.format("dd/MM/yyyy", Long.parseLong(cursor.getString(2)));
            mesajList[sayac] = cursor.getString(1);
            saatList[sayac] = (String) DateFormat.format("hh:MI", Long.parseLong(cursor.getString(2)));
            sayac++;
        }

        simpleList = (ListView) findViewById(R.id.lvItems);
        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, nameList, mesajList, saatList);

        simpleList.setAdapter(customAdapter);
    }
    private String senderList(){
        String senderListText = "'TURKCELL','ISTURKCELL','PAYCELL','SUPERONLINE','LIFEBOX','TVplus','GUVENCELL','TCELL PASAJ','DERGILIK','BIP','TVPLUS','fizy','GAMEPLUS','GNC'";
        return senderListText;
    }
}