package com.fatorius.duinocoinminer.tcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    String ip;
    int port;

    Socket socket;
    PrintWriter socketSender;
    BufferedReader socketReader;

    public Client(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;

        socket = new Socket(ip, port);

        socketSender = new PrintWriter(socket.getOutputStream(), true);
        socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        send("PING");
        socketReader.readLine();
    }

    public void send(String msg){
        socketSender.println(msg);
    }

    public String readLine() throws IOException {
        String receivedMsg = socketReader.readLine();

        if (receivedMsg == null){
            this.restartConnection();
            receivedMsg = "Connection restarted";
        }

        return receivedMsg;
    }

    public void closeConnection() throws IOException {
        socket.close();
        socketSender.close();
        socketReader.close();
    }

    public void restartConnection() throws IOException {
        socket.close();
        socketSender.close();
        socketReader.close();

        socket = new Socket(ip, port);

        socketSender = new PrintWriter(socket.getOutputStream(), true);
        socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        send("PING");
        socketReader.readLine();
    }
}
