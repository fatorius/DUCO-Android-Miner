package com.fatorius.duinocoinminer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fatorius.duinocoinminer.R;
import com.fatorius.duinocoinminer.infos.HardwareStats;

public class InsertDataActivity extends AppCompatActivity {
    static {
        System.loadLibrary("cpuinfo");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_data);

        SharedPreferences sharedPreferences = getSharedPreferences("com.fatorius.duinocoinminer", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent warningIntent = new Intent(this, WarningActivity.class);

        Button startButton = findViewById(R.id.startButton);

        EditText yourNameInput = findViewById(R.id.yourNameInput);
        EditText miningKeyInput = findViewById(R.id.miningKeyInput);
        EditText miningThreadsInput = findViewById(R.id.miningThreadsInput);

        miningThreadsInput.setHint("Mining threads (Max recommended: " + HardwareStats.getNumberOfCPUCores() + ")");

        TextView intensityDisplay = findViewById(R.id.intensityDisplay);

        SeekBar miningIntensityBar = findViewById(R.id.miningIntensityInput);

        startButton.setOnClickListener(view -> {
            String yourNameInputText = yourNameInput.getText().toString().strip();

            if (yourNameInputText.equals("")){
                yourNameInput.setHintTextColor(Color.RED);
                yourNameInput.setTextColor(Color.RED);
                return;
            }

            String miningThreadsInputText = miningThreadsInput.getText().toString();
            if (miningThreadsInputText.equals("")){
                miningThreadsInputText = "4";
            }

            editor.putString("username_value", yourNameInputText);
            editor.putString("mining_key_value", miningKeyInput.getText().toString().strip());
            editor.putInt("threads_value", Integer.parseInt(miningThreadsInputText));
            editor.putInt("mining_intensity_value", miningIntensityBar.getProgress());
            editor.putBoolean("isThereDataSaved", true);

            editor.apply();

            startActivity(warningIntent);
        });

        miningIntensityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                intensityDisplay.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}