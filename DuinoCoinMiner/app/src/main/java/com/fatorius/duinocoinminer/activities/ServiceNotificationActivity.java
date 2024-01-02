package com.fatorius.duinocoinminer.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ServiceNotificationActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        handleNotificationAction();
    }

    private void handleNotificationAction(){
        Toast.makeText(this, "Butao", Toast.LENGTH_LONG).show();
    }
}
