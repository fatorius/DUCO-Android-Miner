#include "jni.h"

#include <string>
#include <sstream>
#include <iostream>
#include <iomanip>
#include <vector>
#include <thread>
#include <chrono>

#include "sha1.h"

extern "C"
JNIEXPORT int JNICALL
Java_com_fatorius_duinocoinminer_algorithms_DUCOS1Hasher_findNonce(JNIEnv *env, jobject thiz,
                                                                   jstring lastHash,
                                                                   jstring expectedHash,
                                                                   jint miningDifficulty,
                                                                   jfloat miningEfficiency) {

    const char *last_hash = env->GetStringUTFChars(lastHash, JNI_FALSE);
    const char *expected_hash = env->GetStringUTFChars(expectedHash, JNI_FALSE);
    const int mining_difficulty = static_cast<int>(miningDifficulty);
    const auto mining_efficiency = static_cast<float>(miningEfficiency);

    SHA1 hasher;
    hasher.update(last_hash);

    for (int nonce = 0; nonce <= (100 * mining_difficulty); nonce++){
        SHA1 temp_hasher = hasher.copy();
        temp_hasher.update(std::to_string(nonce));
        const std::string result = temp_hasher.final();

        if (mining_efficiency > 0){
            if (nonce % 5000 == 0){
                std::this_thread::sleep_for(std::chrono::seconds((long) mining_efficiency / 100));
            }
        }

        if (result == expected_hash){
            return nonce;
        }
    }

    return 0;
}