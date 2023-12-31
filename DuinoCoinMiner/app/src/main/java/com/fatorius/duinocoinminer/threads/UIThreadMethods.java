package com.fatorius.duinocoinminer.threads;

public interface UIThreadMethods {
    void newShareSent();
    void newShareAccepted();
    void sendHashrate(int hr);
    void sendNewLineFromMiner(String line);
}
