package com.fatorius.duinocoinminer.threads;

import android.os.Build;

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
    }

    @Override
    public void run() {
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

                String[] values = responseData.split(",");

                String lastBlockHash = values[0];
                String expectedHash = values[1];

                int difficulty = Integer.parseInt(values[2]);

                int nonce = hasher.mine(lastBlockHash, expectedHash, difficulty, miningEfficiency);

                float timeElapsed = hasher.getTimeElapsed();
                float hashrate = hasher.getHashrate();

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
            }
        }
        catch (RuntimeException e){
            e.printStackTrace();
        }
        finally {
            try {
                tcpClient.closeConnection();
            } catch (IOException ignored) {
            }
        }
    }
}
