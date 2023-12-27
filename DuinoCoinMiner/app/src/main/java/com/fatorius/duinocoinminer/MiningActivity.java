package com.fatorius.duinocoinminer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MiningActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    JsonObjectRequest getMiningPool;

    TextView miningNodeDisplay;

    String poolName;
    String poolIp;
    String poolPort;
    String poolServerName;

    static String GET_MINING_POOL_URL = "https://server.duinocoin.com/getPool";
    public MiningActivity(){

         getMiningPool = new JsonObjectRequest(
                Request.Method.GET, GET_MINING_POOL_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            poolName = response.getString("name");
                            poolIp = response.getString("ip");
                            poolPort = response.getString("port");
                            poolServerName = response.getString("server");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        miningNodeDisplay.setText(new String("Mining on " + poolName));
                        System.out.println(response.toString());
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

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(getMiningPool);
    }
}