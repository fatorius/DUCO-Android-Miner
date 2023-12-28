package com.fatorius.duinocoinminer.tcp;

public interface TcpCallback {
    void onJobReceived(String lastBlockHash, String expectedHash, int difficulty);
}
