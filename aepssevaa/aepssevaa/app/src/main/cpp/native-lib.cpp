#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_wts_aepssevaa_retrofit_ApiController_getAPIHost(
        JNIEnv* env,
        jobject /* this */) {
    std::string apiKey = "http://api.aepsseva.in/Vr1.0/876870/IWFY7438DISYD83/73748FSIF3I8SJD/API/";

    return env->NewStringUTF(apiKey.c_str());

}


extern "C" JNIEXPORT jstring JNICALL
Java_com_wts_aepssevaa_retrofit_ApiController_getAPIHostSecond(
        JNIEnv* env,
        jobject /* this */) {
    std::string apiKey = "http://www.api.aepsseva.in/api/";


    return env->NewStringUTF(apiKey.c_str());

}

    extern "C" JNIEXPORT jstring JNICALL
    Java_com_wts_aepssevaa_retrofit_ApiController_getBasicAuth(
            JNIEnv* env,
            jobject /* this */) {
        std::string apiKey = "Basic d2VidGVjaCMkJV5zb2x1dGlvbiQkJiZAQCZeJmp1bHkyazIxOmJhc2ljJSUjI0AmJmF1dGgmIyYjJiMmQEAjJnBhc1d0UzIwMjE=";

        return env->NewStringUTF(apiKey.c_str());
    }

