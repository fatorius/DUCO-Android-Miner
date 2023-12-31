package com.fatorius.duinocoinminer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fatorius.duinocoinminer.R;
import com.fatorius.duinocoinminer.threads.MiningThread;
import com.fatorius.duinocoinminer.threads.UIThreadMethods;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MiningActivity extends AppCompatActivity implements UIThreadMethods {
    RequestQueue requestQueue;
    JsonObjectRequest getMiningPool;

    TextView miningNodeDisplay;
    TextView acceptedSharesTextDisplay;
    TextView hashrateDisplay;
    TextView miningLogsTextDisplay;

    Button stopMining;

    String poolName;
    String poolIp;
    int poolPort;
    String poolServerName;

    String ducoUsername;
    float efficiency;

    int sentShares;
    int acceptedShares;

    float acceptedPercetange;

    List<String> minerLogLines;

    SharedPreferences sharedPreferences;

    static String GET_MINING_POOL_URL = "https://server.duinocoin.com/getPool";

    public MiningActivity(){
        MiningActivity miningActivity = this;

        sentShares = 0;
        acceptedShares = 0;
        acceptedPercetange = 100.0f;

        minerLogLines = new ArrayList<>();

        getMiningPool = new JsonObjectRequest(
                Request.Method.GET, GET_MINING_POOL_URL, null,

                response -> {
                    try {
                        poolName = response.getString("name");
                        poolIp = response.getString("ip");
                        poolPort = response.getInt("port");
                        poolServerName = response.getString("server");
                    } catch (JSONException e) {
                        // TODO HANDLE DEVICE WITH NO NETWORK CONNECTION
                        throw new RuntimeException(e);
                    }

                    String newMiningText = "Mining node: " + poolName;
                    miningNodeDisplay.setText(newMiningText);

                    Thread miningThread;

                    try {
                        miningThread = new Thread(new MiningThread(poolIp, poolPort, ducoUsername, efficiency, miningActivity));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    miningThread.start();
                },
                error -> System.out.println(error.toString())
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mining);

        miningNodeDisplay = findViewById(R.id.miningNodeDisplay);
        acceptedSharesTextDisplay = findViewById(R.id.acceptedSharesDisplay);
        hashrateDisplay = findViewById(R.id.hashrateDisplayText);
        miningLogsTextDisplay = findViewById(R.id.minerlogsMultiline);

        stopMining = findViewById(R.id.stopMiningButton);

        stopMining.setOnClickListener(view -> {
            // TODO CALL TCP SOCKET CONNECTION CLOSE

            // TODO STOP ALL THREADS SOMEHOW

            // TODO GO BACK TO PREVIOUS ACTIVITY

            // TODO DELETE ALL SHARED PREFERENCES PREFERENCES
        });

        sharedPreferences = getSharedPreferences("com.fatorius.duinocoinminer", MODE_PRIVATE);
        ducoUsername = sharedPreferences.getString("username_value", "---------------------");

        int miningIntensity = sharedPreferences.getInt("mining_intensity_value", 0);
        efficiency = calculateEfficiency(miningIntensity);

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getMiningPool);
    }

    @Override
    public void newShareSent() {
        runOnUiThread(() -> sentShares++);
    }

    @Override
    public void newShareAccepted() {
        runOnUiThread(() -> {
            acceptedShares++;
            updatePercentage();
        });
    }

    @Override
    public void sendHashrate(int hr) {
        runOnUiThread(() -> hashrateDisplay.setText(convertHashrate(hr)));
    }

    @Override
    public void sendNewLineFromMiner(String line) {
        runOnUiThread(() -> {
            long currentTimeMillis = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date currentDate = new Date(currentTimeMillis);
            String formattedDate = sdf.format(currentDate);

            String newLine = formattedDate + " | " + line;
            minerLogLines.add(0, newLine);

            if (minerLogLines.size() > 8){
                minerLogLines.remove(8);
            }

            StringBuilder newMultiLineText = new StringBuilder();

            for (int i = 0; i < minerLogLines.size(); i++){
                newMultiLineText.append(minerLogLines.get(i)).append("\n");
            }

            miningLogsTextDisplay.setText(newMultiLineText.toString());
        });
    }

    private void updatePercentage(){
        acceptedPercetange = ((float) (acceptedShares / sentShares)) * 10000;
        acceptedPercetange = Math.round(acceptedPercetange) / 100.0f;

        String newText = "Accepted shares: " + acceptedShares + "/" + sentShares + " (" + acceptedPercetange + "%)";

        acceptedSharesTextDisplay.setText(newText);
    }

    static float calculateEfficiency(int eff){
        if (eff >= 90){
            return 0.005f;
        }
        else if (eff >= 70){
            return  0.1f;
        }
        else if (eff >= 50){
            return  0.8f;
        }
        else if (eff >= 30) {
            return 1.8f;
        }

        return 3.0f;
    }

    static String convertHashrate(int hr){
        float roundedHashrate;

        if (hr >= 1_000_000){
            hr /= 1_000;
            roundedHashrate = hr / 1_000.0f;

            return roundedHashrate + "M";
        }
        else if (hr >= 1_000){
            hr /= 100;
            roundedHashrate = hr / 10.0f;
            return roundedHashrate + "k";
        }

        return String.valueOf(hr);
    }
}