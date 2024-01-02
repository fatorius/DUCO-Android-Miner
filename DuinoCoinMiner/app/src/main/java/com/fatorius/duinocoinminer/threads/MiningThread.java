package com.fatorius.duinocoinminer.threads;

import android.os.Build;
import android.util.Log;

import com.fatorius.duinocoinminer.algorithms.DUCOS1Hasher;
import com.fatorius.duinocoinminer.tcp.Client;

import java.io.IOException;

public class MiningThread implements Runnable{
    String ip;
    int port;

    int threadNo;

    Client tcpClient;

    String username;

    DUCOS1Hasher hasher;

    float miningEfficiency;

    UIThreadMethods uiThreadMethods;

    public MiningThread(String ip, int port, String username, float miningEfficiency,
                        UIThreadMethods uiThreadMethods, int threadNo) throws IOException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.miningEfficiency = miningEfficiency;

        this.threadNo = threadNo;

        this.uiThreadMethods = uiThreadMethods;

        hasher = new DUCOS1Hasher();

        Log.d("Mining thread" + threadNo, threadNo + " created");
    }

    @Override
    public void run() {
        Log.d("Mining thread" + threadNo, threadNo + " started");

        try {
            String responseData;

            try {
                tcpClient = new Client(ip, port);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (true) {
                tcpClient.send("JOB," + username + ",LOW");

                try {
                    responseData = tcpClient.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                Log.d("Thread " + threadNo + " | JOB received", responseData);

                String[] values = responseData.split(",");

                String lastBlockHash = values[0];
                String expectedHash = values[1];

                int difficulty = Integer.parseInt(values[2]);

                int nonce = hasher.mine(lastBlockHash, expectedHash, difficulty, miningEfficiency);

                float timeElapsed = hasher.getTimeElapsed();
                float hashrate = hasher.getHashrate();

                Log.d("Thread " + threadNo + " | Nonce found", nonce + " Time elapsed: " + timeElapsed + "s Hashrate: " + (int) hashrate);

                uiThreadMethods.sendHashrate((int) hashrate);
                uiThreadMethods.newShareSent();
                uiThreadMethods.sendNewLineFromMiner("Thread " + threadNo + " | Nonce found: " + nonce + " | Time elapsed: " + timeElapsed + "s | Hashrate: " + (int) hashrate);

                tcpClient.send(nonce + "," + (int) hashrate + ",Android Miner," + Build.MODEL);

                String shareResult;
                try {
                    shareResult = tcpClient.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (shareResult.contains("GOOD")) {
                    uiThreadMethods.newShareAccepted();
                }

                Log.d("Share accepted", shareResult);
            }
        }
        catch (RuntimeException e){
            e.printStackTrace();
        }
        finally {
            try {
                tcpClient.closeConnection();
                Log.d("Mining thread" + threadNo, threadNo + " interrupted");
            } catch (IOException e) {
                Log.w("Miner thread", "Couldn't properly end socket connection");
            }
        }
    }
}
