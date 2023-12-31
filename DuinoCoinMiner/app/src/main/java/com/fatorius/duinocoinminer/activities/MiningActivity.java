package com.fatorius.duinocoinminer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
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

public class MiningActivity extends AppCompatActivity implements UIThreadMethods {
    RequestQueue requestQueue;
    JsonObjectRequest getMiningPool;

    TextView miningNodeDisplay;
    TextView textToDisplay;

    String poolName;
    String poolIp;
    int poolPort;
    String poolServerName;

    String ducoUsername;
    float efficiency;

    SharedPreferences sharedPreferences;

    static String GET_MINING_POOL_URL = "https://server.duinocoin.com/getPool";

    public MiningActivity(){
        MiningActivity miningActivity = this;
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

                    String newMiningText = "Mining on " + poolName;
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
        textToDisplay = findViewById(R.id.msgToReceive);

        sharedPreferences = getSharedPreferences("com.fatorius.duinocoinminer", MODE_PRIVATE);
        ducoUsername = sharedPreferences.getString("username_value", "---------------------");

        int miningIntensity = sharedPreferences.getInt("mining_intensity_value", 0);
        efficiency = calculateEfficiency(miningIntensity);

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getMiningPool);
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

    @Override
    public void sendSomeData(String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textToDisplay.setText(msg);
            }
        });
    }
}