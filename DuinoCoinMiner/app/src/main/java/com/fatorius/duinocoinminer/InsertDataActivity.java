package com.fatorius.duinocoinminer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class InsertDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_data);

        SharedPreferences sharedPreferences = getSharedPreferences("com.fatorius.duinocoinminer", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Intent miningIntent = new Intent(this, MiningActivity.class);

        Button startButton = (Button) findViewById(R.id.startButton);

        EditText yourNameInput = (EditText) findViewById(R.id.yourNameInput);
        EditText miningKeyInput = (EditText) findViewById(R.id.miningKeyInput);
        EditText miningThreadsInput = (EditText) findViewById(R.id.miningThreadsInput);

        TextView intensityDisplay = (TextView) findViewById(R.id.intensityDisplay);

        SeekBar miningIntensityBar = (SeekBar) findViewById(R.id.miningIntensityInput);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("username_value", yourNameInput.getText().toString());
                editor.putString("mining_key_value", miningKeyInput.getText().toString());
                editor.putInt("threads_value", Integer.valueOf(miningThreadsInput.getText().toString()));
                editor.putInt("mining_intensity_value", Integer.valueOf(miningIntensityBar.getProgress()));
                editor.putBoolean("isThereDataSaved", true);

                startActivity(miningIntent);
            }
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