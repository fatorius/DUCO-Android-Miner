package com.fatorius.duinocoinminer.algorithms;

import android.os.SystemClock;

public class DUCOS1Hasher {
    private int lastNonce;
    private float hashingTimeDeltaSeconds;

    public DUCOS1Hasher(){
    }

    public int mine(String lastHash, String expectedHash, int difficulty, float efficiency){
        float hashingStartTimeSeconds = SystemClock.elapsedRealtime() / 1000.0f;

        lastNonce = findNonce(lastHash, expectedHash, difficulty, efficiency);
        float hashingEndTimeSeconds = SystemClock.elapsedRealtime() / 1000.0f;

        hashingTimeDeltaSeconds = hashingEndTimeSeconds - hashingStartTimeSeconds;

        return lastNonce;
    }

    public native int findNonce(String lastHash, String expectedHash, int difficulty, float efficiency);

    public float getTimeElapsed(){
        return hashingTimeDeltaSeconds;
    }

    public float getHashrate(){
        return lastNonce / (hashingTimeDeltaSeconds);
    }
}
