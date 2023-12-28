package com.fatorius.duinocoinminer.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class JobRequester implements Runnable{
    private final String ip;
    private final String ducoUsername;
    private final int port;

    private TcpCallback callback;

    public JobRequester(String ip, int port, String username, TcpCallback callback){
        this.ip = ip;
        this.port = port;
        ducoUsername = username;
        this.callback = callback;
    }

    @Override
    public void run() {
        /*
        String responseLength;
        String responseData;

        Socket socket = new Socket(ip, port);
        PrintWriter socketSender = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        socketSender.println("JOB," + ducoUsername + ",LOW");

        //3.0
        responseLength = socketReader.readLine();

        //32d87953f66baea4b3bb3e7fee68e27d6b12692d,2924aba17c470d457c3e9310b1fe34d58d32b820,25000
        responseData = socketReader.readLine();

        socket.close();
        socketSender.close();
        socketReader.close();
        */

        String responseData = "32d87953f66baea4b3bb3e7fee68e27d6b12692d,2924aba17c470d457c3e9310b1fe34d58d32b820,25000";
        String[] values = responseData.split(",");

        callback.onJobReceived(values[0], values[1], Integer.parseInt(values[2]));
    }
}
