package com.fatorius.duinocoinminer.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fatorius.duinocoinminer.R;
import com.fatorius.duinocoinminer.algorithms.DUCOS1Hasher;
import com.fatorius.duinocoinminer.tcp.JobRequester;
import com.fatorius.duinocoinminer.tcp.TcpCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class MiningActivity extends AppCompatActivity implements TcpCallback {
    RequestQueue requestQueue;
    JsonObjectRequest getMiningPool;

    TextView miningNodeDisplay;

    String poolName;
    String poolIp;
    int poolPort;
    String poolServerName;

    String ducoUsername;
    float efficiency;

    SharedPreferences sharedPreferences;

    DUCOS1Hasher hasher;

    static String GET_MINING_POOL_URL = "https://server.duinocoin.com/getPool";
    public MiningActivity(){
        MiningActivity miningActivity = this;

        hasher = new DUCOS1Hasher();

        getMiningPool = new JsonObjectRequest(
                Request.Method.GET, GET_MINING_POOL_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            poolName = response.getString("name");
                            poolIp = response.getString("ip");
                            poolPort = response.getInt("port");
                            poolServerName = response.getString("server");
                        } catch (JSONException e) {
                            // TODO HANDLE DEVICE WITH NO NETWORK CONNECTION
                            throw new RuntimeException(e);
                        }

                        miningNodeDisplay.setText(new String("Mining on " + poolName));
                        System.out.println(response.toString());

                        Thread tcpThread = new Thread(new JobRequester(poolIp, poolPort, ducoUsername, miningActivity));

                        tcpThread.start();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error.toString());
                    }
                }
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mining);

        miningNodeDisplay = findViewById(R.id.miningNodeDisplay);

        sharedPreferences = getSharedPreferences("com.fatorius.duinocoinminer", MODE_PRIVATE);
        ducoUsername = sharedPreferences.getString("username_value", "---------------------");

        int miningIntensity = sharedPreferences.getInt("mining_intensity_value", 0);

        if (miningIntensity >= 90){
            efficiency = 0.005f;
        }
        else if (miningIntensity >= 70){
            efficiency = 0.1f;
        }
        else if (miningIntensity >= 50){
            efficiency = 0.8f;
        }
        else if (miningIntensity >= 30) {
            efficiency = 1.8f;
        }else{
            efficiency = 3;
        }

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getMiningPool);
    }

    @Override
    public void onJobReceived(String lastBlockHash, String expectedHash, int difficulty) {
        hasher.findNonce(lastBlockHash, expectedHash, difficulty, efficiency);

        System.out.println(lastBlockHash);
        System.out.println(expectedHash);
        System.out.println(difficulty);
    }
}