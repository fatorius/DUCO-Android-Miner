package com.fatorius.duinocoinminer.threads;

import android.os.Build;
import android.util.Log;

import com.fatorius.duinocoinminer.algorithms.DUCOS1Hasher;
import com.fatorius.duinocoinminer.infos.MinerInfo;
import com.fatorius.duinocoinminer.tcp.Client;

import java.io.IOException;

public class MiningThread implements Runnable{
    static{
        System.loadLibrary("ducohasher");
    }

    public final static String MINING_THREAD_NAME_ID = "duinocoin_mining_thread";

    String ip;
    int port;

    int threadNo;

    Client tcpClient;

    String username;

    DUCOS1Hasher hasher;

    float miningEfficiency;

    ServiceCommunicationMethods service;

    public MiningThread(String ip, int port, String username, float miningEfficiency, int threadNo, ServiceCommunicationMethods service) throws IOException {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.miningEfficiency = miningEfficiency;
        this.service = service;
        this.threadNo = threadNo;

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

            while (!Thread.currentThread().isInterrupted()) {
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

                service.newShareSent();

                tcpClient.send(nonce + "," + (int) hashrate + "," + MinerInfo.MINER_NAME + "," + Build.MODEL);

                String shareResult;
                try {
                    shareResult = tcpClient.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (shareResult.contains("GOOD")) {
                    service.newShareAccepted(threadNo, (int) hashrate, timeElapsed, nonce);
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
                Log.d("Mining thread " + threadNo, "Thread " + threadNo + " interrupted");
            } catch (IOException e) {
                Log.w("Miner thread", "Couldn't properly end socket connection");
            }
        }
    }
}
