package com.fatorius.duinocoinminer.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ServiceNotificationActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        handleNotificationAction();
    }

    private void handleNotificationAction(){
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
