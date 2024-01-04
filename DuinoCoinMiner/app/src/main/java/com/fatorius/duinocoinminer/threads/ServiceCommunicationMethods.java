package com.fatorius.duinocoinminer.threads;

public interface ServiceCommunicationMethods {
    void newShareSent();
    void newShareAccepted(int threadNo, int hashrate, float timeElapsed, int nonce);
}
