package com.fatorius.duinocoinminer.threads;

import android.util.Log;

import com.fatorius.duinocoinminer.algorithms.DUCOS1Hasher;
import com.fatorius.duinocoinminer.tcp.Client;

import java.io.IOException;

public class MiningThread implements Runnable{
    String ip;
    int port;

    Client tcpClient;

    String username;

    DUCOS1Hasher hasher;

    float miningEfficiency;

    UIThreadMethods uiThreadMethods;

    public MiningThread(String ip, int port, String username, float miningEfficiency, UIThreadMethods uiThreadMethods) throws IOException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.miningEfficiency = miningEfficiency;

        this.uiThreadMethods = uiThreadMethods;

        hasher = new DUCOS1Hasher();
    }

    @Override
    public void run() {
        try {
            tcpClient = new Client(ip, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /*
        String responseLength;
        String responseData;

        tcpClient.send("JOB," + ducoUsername + ",LOW");

        //3.0
        responseLength = tcpClient.readLine();

        //32d87953f66baea4b3bb3e7fee68e27d6b12692d,2924aba17c470d457c3e9310b1fe34d58d32b820,25000
        responseData = tcpClient.readLine();

        */

        String responseData = "32d87953f66baea4b3bb3e7fee68e27d6b12692d,2924aba17c470d457c3e9310b1fe34d58d32b820,25000";
        Log.d("JOB received", responseData);

        String[] values = responseData.split(",");

        String lastBlockHash = values[0];
        String expectedHash = values[1];

        int difficulty = Integer.parseInt(values[2]);

        int nonce = hasher.mine(lastBlockHash, expectedHash, difficulty, miningEfficiency);

        float timeElapsed = hasher.getTimeElapsed();
        float hashrate = hasher.getHashrate();

        Log.d("Nonce found", nonce + " Time elapsed: " + timeElapsed + " Hashrate: " + (int) hashrate);

        uiThreadMethods.sendSomeData(nonce + " Time elapsed: " + timeElapsed + " Hashrate: " + (int) hashrate);
    }
}
