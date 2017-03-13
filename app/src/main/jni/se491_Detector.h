/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class capstone_se491_1phm_Detector */

#ifndef _Included_capstone_se491_1phm_Detector
#define _Included_capstone_se491_1phm_Detector
#ifdef __cplusplus
extern "C" {
#endif
#undef capstone_se491_1phm_Detector_INTERVAL_MS
#define capstone_se491_1phm_Detector_INTERVAL_MS 20L
#undef capstone_se491_1phm_Detector_DURATION_S
#define capstone_se491_1phm_Detector_DURATION_S 10L
#undef capstone_se491_1phm_Detector_N
#define capstone_se491_1phm_Detector_N 500L
#undef capstone_se491_1phm_Detector_OFFSET_X
#define capstone_se491_1phm_Detector_OFFSET_X 0L
#undef capstone_se491_1phm_Detector_OFFSET_Y
#define capstone_se491_1phm_Detector_OFFSET_Y 500L
#undef capstone_se491_1phm_Detector_OFFSET_Z
#define capstone_se491_1phm_Detector_OFFSET_Z 1000L
#undef capstone_se491_1phm_Detector_OFFSET_X_LPF
#define capstone_se491_1phm_Detector_OFFSET_X_LPF 1500L
#undef capstone_se491_1phm_Detector_OFFSET_Y_LPF
#define capstone_se491_1phm_Detector_OFFSET_Y_LPF 2000L
#undef capstone_se491_1phm_Detector_OFFSET_Z_LPF
#define capstone_se491_1phm_Detector_OFFSET_Z_LPF 2500L
#undef capstone_se491_1phm_Detector_OFFSET_X_HPF
#define capstone_se491_1phm_Detector_OFFSET_X_HPF 3000L
#undef capstone_se491_1phm_Detector_OFFSET_Y_HPF
#define capstone_se491_1phm_Detector_OFFSET_Y_HPF 3500L
#undef capstone_se491_1phm_Detector_OFFSET_Z_HPF
#define capstone_se491_1phm_Detector_OFFSET_Z_HPF 4000L
#undef capstone_se491_1phm_Detector_OFFSET_X_D
#define capstone_se491_1phm_Detector_OFFSET_X_D 4500L
#undef capstone_se491_1phm_Detector_OFFSET_Y_D
#define capstone_se491_1phm_Detector_OFFSET_Y_D 5000L
#undef capstone_se491_1phm_Detector_OFFSET_Z_D
#define capstone_se491_1phm_Detector_OFFSET_Z_D 5500L
#undef capstone_se491_1phm_Detector_OFFSET_SV_TOT
#define capstone_se491_1phm_Detector_OFFSET_SV_TOT 6000L
#undef capstone_se491_1phm_Detector_OFFSET_SV_D
#define capstone_se491_1phm_Detector_OFFSET_SV_D 6500L
#undef capstone_se491_1phm_Detector_OFFSET_SV_MAXMIN
#define capstone_se491_1phm_Detector_OFFSET_SV_MAXMIN 7000L
#undef capstone_se491_1phm_Detector_OFFSET_Z_2
#define capstone_se491_1phm_Detector_OFFSET_Z_2 7500L
#undef capstone_se491_1phm_Detector_OFFSET_FALLING
#define capstone_se491_1phm_Detector_OFFSET_FALLING 8000L
#undef capstone_se491_1phm_Detector_OFFSET_IMPACT
#define capstone_se491_1phm_Detector_OFFSET_IMPACT 8500L
#undef capstone_se491_1phm_Detector_OFFSET_LYING
#define capstone_se491_1phm_Detector_OFFSET_LYING 9000L
#undef capstone_se491_1phm_Detector_SIZE
#define capstone_se491_1phm_Detector_SIZE 9500L
#undef capstone_se491_1phm_Detector_FALLING_WAIST_SV_TOT
#define capstone_se491_1phm_Detector_FALLING_WAIST_SV_TOT 0.6
#undef capstone_se491_1phm_Detector_IMPACT_WAIST_SV_TOT
#define capstone_se491_1phm_Detector_IMPACT_WAIST_SV_TOT 2.0
#undef capstone_se491_1phm_Detector_IMPACT_WAIST_SV_D
#define capstone_se491_1phm_Detector_IMPACT_WAIST_SV_D 1.7
#undef capstone_se491_1phm_Detector_IMPACT_WAIST_SV_MAXMIN
#define capstone_se491_1phm_Detector_IMPACT_WAIST_SV_MAXMIN 2.0
#undef capstone_se491_1phm_Detector_IMPACT_WAIST_Z_2
#define capstone_se491_1phm_Detector_IMPACT_WAIST_Z_2 1.5
/*
 * Class:     capstone_se491_1phm_Detector
 * Method:    initiate
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_capstone_se491_1phm_Detector_initiate
  (JNIEnv *, jclass, jobject);

/*
 * Class:     capstone_se491_1phm_Detector
 * Method:    acquire
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_capstone_se491_1phm_Detector_acquire
  (JNIEnv *, jclass);

/*
 * Class:     capstone_se491_1phm_Detector
 * Method:    buffer
 * Signature: ()[D
 */
JNIEXPORT jdoubleArray JNICALL Java_capstone_se491_1phm_Detector_buffer
  (JNIEnv *, jclass);

/*
 * Class:     capstone_se491_1phm_Detector
 * Method:    position
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_capstone_se491_1phm_Detector_position
  (JNIEnv *, jclass);

/*
 * Class:     capstone_se491_1phm_Detector
 * Method:    release
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_capstone_se491_1phm_Detector_release
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
