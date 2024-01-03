package com.fatorius.duinocoinminer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = getSharedPreferences("com.fatorius.duinocoinminer", MODE_PRIVATE);
        Intent insertDataIntent = new Intent(this, InsertDataActivity.class);
        Intent miningIntent = new Intent(this, MiningActivity.class);

        boolean isThereDataSaved = sharedPreferences.getBoolean("isThereDataSaved", false);

        if (!isThereDataSaved) {
            startActivity(insertDataIntent);
        }
        else{
            startActivity(miningIntent);
        }
    }
}