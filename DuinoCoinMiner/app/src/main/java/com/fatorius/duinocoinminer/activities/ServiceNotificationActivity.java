package com.fatorius.duinocoinminer.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.fatorius.duinocoinminer.services.MinerBackgroundService;

public class ServiceNotificationActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MinerBackgroundService.class);
        intent.setAction(MinerBackgroundService.ACTION_STOP_BACKGROUND_MINING);
        startService(intent);

        finish();
    }
}
