package com.example.smsvalidator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class SmsListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Listener 11");
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            System.out.println("Listener work-----------------------------------------");
        }
    }
}
