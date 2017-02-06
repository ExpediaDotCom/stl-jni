#include <stdio.h>
#include "com_expedia_stljni_Stl.h" // Required header for JNI

// Fortran routines have to be prototyped as extern, and parameters are
// passed by reference. Note also that for g77 the function name in C by
// default must be prefixed (postfixed?) by a "_". (We're using gfortran
// so I'm not sure whether this will apply.)

extern void stl_(
    float [], // y
    int *,    // n
    int *,    // np
    int *,    // ns
    int *,    // nt
    int *,    // nl
    int *,    // isdeg
    int *,    // itdeg
    int *,    // ildeg
    int *,    // nsjump
    int *,    // ntjump
    int *,    // nljump
    int *,    // ni
    int *,    // no
    float [],   // rw
    float [],   // season
    float [],   // trend
    float [][7] // work
);

// When calling C code from Java, main() must be replaced by a declaration
// similar to below, where the function name is given by "Java_" + the name
// of the class in the Java code that calls this C code, in this case
// "StlDriver", + "_" + the name of this C function called from Java, in this
// case "stlc". This is followed by at least two parameters as below,
// plus possibly more if more are required.

JNIEXPORT jint JNICALL Java_com_expedia_stljni_Stl_stlc(
      JNIEnv *env,         /* interface pointer */
      jobject obj,         /* "this" pointer */
      jfloatArray jy,
      jint np,
      jint ns,
      jint nt,
      jint nl,
      jint isdeg,
      jint itdeg,
      jint ildeg,
      jint nsjump,
      jint ntjump,
      jint nljump,
      jint ni,
      jint no,
      jfloatArray jrw,
      jfloatArray jseason,
      jfloatArray jtrend)
{

  jsize n = (*env)->GetArrayLength(env, jy);
  jfloat *y = (*env)->GetFloatArrayElements(env, jy, 0);
  jfloat *rw = (*env)->GetFloatArrayElements(env, jrw, 0);
  jfloat *season = (*env)->GetFloatArrayElements(env, jseason, 0);
  jfloat *trend = (*env)->GetFloatArrayElements(env, jtrend, 0);
  float work[n + 2 * np][7];

  for (int i = 0; i < n + 2 * np; i++) {
    for (int j = 0; j < 7; j++) {
      work[i][j] = 0.0;
    }
  }

  stl_(y, &n, &np, &ns, &nt, &nl,
    &isdeg, &itdeg, &itdeg,
    &nsjump, &ntjump, &nljump,
    &ni, &no,
    rw, season, trend, work);

  // Instead of ending as a normal C program, the pointers must be cleared
  // before returning to Java.
  (*env)->ReleaseFloatArrayElements(env, jy, y, 0);
  (*env)->ReleaseFloatArrayElements(env, jrw, rw, 0);
  (*env)->ReleaseFloatArrayElements(env, jseason, season, 0);
  (*env)->ReleaseFloatArrayElements(env, jtrend, trend, 0);

  return 0;
}
