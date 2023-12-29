#include "jni.h"

#include <string>
#include <sstream>
#include <iostream>
#include <iomanip>
#include <vector>

extern "C"
JNIEXPORT jint JNICALL
Java_com_fatorius_duinocoinminer_algorithms_DUCOS1Hasher_findNonce(JNIEnv *env, jobject thiz,
                                                                   jstring lastHash,
                                                                   jstring expectedHash,
                                                                   jint miningDifficulty,
                                                                   jfloat miningEfficiency) {

    const char *last_hash = env->GetStringUTFChars(lastHash, JNI_FALSE);
    const char *expected_hash = env->GetStringUTFChars(expectedHash, JNI_FALSE);
    const int mining_difficulty = static_cast<int>(miningDifficulty);
    const float mining_efficiency = static_cast<float>(miningEfficiency);

    for (int nonce = 0; nonce <= (100 * mining_difficulty); nonce++){

    }
}