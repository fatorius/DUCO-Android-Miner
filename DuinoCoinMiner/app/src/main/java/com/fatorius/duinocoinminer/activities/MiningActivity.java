package com.fatorius.duinocoinminer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fatorius.duinocoinminer.R;
import com.fatorius.duinocoinminer.services.MinerBackgroundService;
import com.fatorius.duinocoinminer.threads.MiningThread;
import com.fatorius.duinocoinminer.threads.UIThreadMethods;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class MiningActivity extends AppCompatActivity { //implements UIThreadMethods {
    RequestQueue requestQueue;
    JsonObjectRequest getMiningPoolRequester;

    TextView miningNodeDisplay;
    TextView acceptedSharesTextDisplay;
    TextView hashrateDisplay;
    TextView miningLogsTextDisplay;
    TextView performancePerThread;

    Button stopMining;

    ProgressBar gettingPoolProgress;

    Handler handler;

    String poolName;
    String poolIp;
    int poolPort;
    String poolServerName;

    String ducoUsername;
    float efficiency;
    int numberOfMiningThreads;

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

        getMiningPoolRequester = new JsonObjectRequest(
                Request.Method.GET, GET_MINING_POOL_URL, null,

                response -> {
                    gettingPoolProgress.setVisibility(View.GONE);

                    try {
                        poolName = response.getString("name");
                        poolIp = response.getString("ip");
                        poolPort = response.getInt("port");
                        poolServerName = response.getString("server");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    String newMiningText = "Mining node: " + poolName;
                    miningNodeDisplay.setText(newMiningText);

                    Intent miningServiceIntent = new Intent(miningActivity, MinerBackgroundService.class);

                    miningServiceIntent.putExtra("poolIp", poolIp);
                    miningServiceIntent.putExtra("numberOfThreads", numberOfMiningThreads);
                    miningServiceIntent.putExtra("poolPort", poolPort);
                    miningServiceIntent.putExtra("ducoUsername", ducoUsername);
                    miningServiceIntent.putExtra("efficiency", efficiency);

                    miningActivity.startForegroundService(miningServiceIntent);

                    stopMining.setOnClickListener(view -> {
                        miningActivity.stopService(miningServiceIntent);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        finish();
                    });
                },

                error -> {
                    gettingPoolProgress.setVisibility(View.GONE);

                    String errorMsg = "Fail getting mining node";

                    String errorType = error.toString();

                    switch (errorType){
                        case "com.android.volley.TimeoutError":
                            errorMsg = "Error: Timeout connecting to server.duinocoin.com";
                            break;
                        case "com.android.volley.NoConnectionError: java.net.UnknownHostException: Unable to resolve host \"server.duinocoin.com\": No address associated with hostname":
                            errorMsg = "Error: no internet connection";
                            break;
                        case "com.android.volley.ServerError":
                            errorMsg = "Error: server.duinocoin.com internal error";
                            break;
                    }

                    Log.i("Resquest error", error.toString());

                    miningNodeDisplay.setText(errorMsg);

                    String stopMiningNewText = "Back";

                    stopMining.setText(stopMiningNewText);

                    stopMining.setOnClickListener(view -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        finish();
                    });
                }
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
        performancePerThread = findViewById(R.id.threadPerformanceView);

        stopMining = findViewById(R.id.stopMiningButton);

        gettingPoolProgress = findViewById(R.id.gettingPoolLoading);

        sharedPreferences = getSharedPreferences("com.fatorius.duinocoinminer", MODE_PRIVATE);
        ducoUsername = sharedPreferences.getString("username_value", "---------------------");
        numberOfMiningThreads = sharedPreferences.getInt("threads_value", 1);

        int miningIntensity = sharedPreferences.getInt("mining_intensity_value", 0);

        handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                System.out.println(msg.toString());
            }
        };

        efficiency = calculateEfficiency(miningIntensity);

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getMiningPoolRequester);
    }

    @Override
    public void onResume(){
        super.onResume();

        Log.d("App", "Vortano");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("App", "Pausano");
    }


    /*
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
    public void sendHashrate(int threadNo, int hr) {
        runOnUiThread(() -> {
            threadsHashrate.add(threadNo, hr);

            int totalHashrate = 0;
            StringBuilder displayHashratePerThread = new StringBuilder();

            int threadHs;

            for (int th = 0; th < numberOfMiningThreads; th++){
                threadHs = threadsHashrate.get(th);
                totalHashrate += threadHs;
                displayHashratePerThread.append("Thread ").append(th).append(": ").append(threadHs).append("h/s \n");
            }

            hashrateDisplay.setText(convertHashrate(totalHashrate));
            performancePerThread.setText(displayHashratePerThread.toString());
        });
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
    }*/

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