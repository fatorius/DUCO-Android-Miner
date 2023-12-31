package com.fatorius.duinocoinminer.tcp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    Socket socket;
    PrintWriter socketSender;
    BufferedReader socketReader;

    public Client(String ip, int port) throws IOException {
        socket = new Socket(ip, port);

        socketSender = new PrintWriter(socket.getOutputStream(), true);
        socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void send(String msg){
        Log.d("Sending to socket", msg);
        socketSender.println(msg);
    }

    public String readLine() throws IOException {
        String receivedMsg = socketReader.readLine();

        Log.d("Received from socket", receivedMsg);

        return receivedMsg;
    }

    public void closeConnection() throws IOException {
        socket.close();
        socketSender.close();
        socketReader.close();
    }
}
