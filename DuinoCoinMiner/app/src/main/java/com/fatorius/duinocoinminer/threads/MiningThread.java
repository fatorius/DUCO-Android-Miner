package com.fatorius.duinocoinminer.threads;

import android.os.Build;
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
        String responseData;

        try {
            tcpClient = new Client(ip, port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true){
            tcpClient.send("JOB," + username + ",LOW");

            try {
                responseData = tcpClient.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Log.d("JOB received", responseData);

            String[] values = responseData.split(",");

            String lastBlockHash = values[0];
            String expectedHash = values[1];

            int difficulty = Integer.parseInt(values[2]);

            int nonce = hasher.mine(lastBlockHash, expectedHash, difficulty, miningEfficiency);

            float timeElapsed = hasher.getTimeElapsed();
            float hashrate = hasher.getHashrate();

            Log.d("Nonce found", nonce + " Time elapsed: " + timeElapsed + " Hashrate: " + (int) hashrate);

            tcpClient.send(nonce + "," + (int) hashrate + ",Android Miner," + Build.MODEL);

            try {
                System.out.println(tcpClient.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            uiThreadMethods.sendSomeData(nonce + " Time elapsed: " + timeElapsed + " Hashrate: " + (int) hashrate);
        }
    }
}
