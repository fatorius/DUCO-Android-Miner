package com.fatorius.duinocoinminer.algorithms;

public class DUCOS1Hasher {
    private int hashrate;

    public DUCOS1Hasher(){
    }

    public native int findNonce(String lastHash, String expectedHash, int difficulty, float efficiency);

    public float getHashrate(){
        return hashrate;
    }

}
