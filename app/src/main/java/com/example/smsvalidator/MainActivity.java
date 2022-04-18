package com.example.smsvalidator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private ListView simpleList;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        readSms();
        swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(
                () -> {
                    Log.i("LOG_TAG", "onRefresh called from SwipeRefreshLayout");
                    readSms();
                    swipeRefreshLayout.setRefreshing(false);
                }
        );
        //SmsListener listener = new SmsListener();
        //listener.onReceive(getApplicationContext(),getParentActivityIntent());
    }

    public void readSms() {
        final String SMS_URI_INBOX = "content://sms/inbox";
        Uri uri = Uri.parse(SMS_URI_INBOX);
        String[] select = new String[]{"address", "body", "date"};

        Cursor cursor = getContentResolver().query(uri, select, "address IN (" + senderList() + ")", null, "date desc" + " LIMIT 150");

        String nameList[] = new String[cursor.getCount()];
        String mesajList[] = new String[cursor.getCount()];
        String saatList[] = new String[cursor.getCount()];

        int sayac = 0;
        while (cursor.moveToNext()) {
            nameList[sayac] = cursor.getString(0) + " - " + (String) DateFormat.format("dd/MM/yyyy", Long.parseLong(cursor.getString(2)));
            mesajList[sayac] = cursor.getString(1);
            saatList[sayac] = (String) DateFormat.format("hh:MI", Long.parseLong(cursor.getString(2)));
            sayac++;
        }
        simpleList = (ListView) findViewById(R.id.lvItems);
        CustomAdapter customAdapter = new CustomAdapter(MainActivity.this, nameList, mesajList, saatList);
        simpleList.setAdapter(customAdapter);
    }
    private String senderList() {
        String senderListText = "'TURKCELL','ISTURKCELL','PAYCELL','SUPERONLINE','LIFEBOX','TVplus','GUVENCELL','TCELL PASAJ','DERGILIK','BIP','TVPLUS','fizy','GAMEPLUS','GNC'";
        return senderListText;
    }
}