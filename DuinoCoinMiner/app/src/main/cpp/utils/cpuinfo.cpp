#include "jni.h"

#include <sys/sysconf.h>

extern "C"
JNIEXPORT jint JNICALL
Java_com_fatorius_duinocoinminer_infos_HardwareStats_getNumberOfCPUCores(JNIEnv *env,
                                                                         jclass clazz) {
    return sysconf(_SC_NPROCESSORS_CONF);
}