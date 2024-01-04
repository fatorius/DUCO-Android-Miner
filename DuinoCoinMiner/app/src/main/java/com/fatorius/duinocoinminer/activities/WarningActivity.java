package com.fatorius.duinocoinminer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.fatorius.duinocoinminer.R;

public class WarningActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        Intent miningIntent = new Intent(this, MiningActivity.class);

        Button continueButton = findViewById(R.id.continueButtonFromWarning);

        continueButton.setOnClickListener(view -> {
            startActivity(miningIntent);
        });
    }
}