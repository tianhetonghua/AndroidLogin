#include <jni.h>
#include <string.h>
#include <android/log.h>
#include "vm_protection.h" // 引入头文件
#define LOG_TAG "JNI_DEBUG"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

JNIEXPORT jboolean JNICALL
Java_com_example_redrocker_JniBridge_verifyLogin(JNIEnv *env, jobject thiz, jstring input_username, jstring input_flag) {
    // 打印 enc_flag 的内容


    // 检查传入的用户名和密码
    if (input_username == NULL || input_flag == NULL) {
        LOGE("JNI verifyLogin failed: null parameters");
        return JNI_FALSE;
    }

    const char *username_chars = (*env)->GetStringUTFChars(env, input_username, NULL);
    const char *flag = (*env)->GetStringUTFChars(env, input_flag, NULL);

    if (username_chars == NULL || flag == NULL) {
        LOGE("JNI verifyLogin failed: GetStringUTFChars returned NULL");
        return JNI_FALSE;
    }

    // 创建一个整型数组来存储生成的用户名数组
    int generated_username[F_LEN];
    generate_username_array(username_chars, generated_username);

    // 将生成的用户名数组保存到全局变量 username
    for (int i = 0; i < F_LEN; i++) {
        username[i] = generated_username[i];
    }

    // 初始化虚拟机
    vm_cpu cpu = {0};
    vm_init(&cpu);

    // 检查 vm_stack 是否正确初始化
    if (vm_stack == NULL) {
        LOGE("JNI verifyLogin failed: vm_stack is NULL after initialization");
        (*env)->ReleaseStringUTFChars(env, input_username, username_chars);
        (*env)->ReleaseStringUTFChars(env, input_flag, flag);
        return JNI_FALSE;
    }

    // 将输入的密码复制到 vm_stack
    strcpy(vm_stack, flag);
    LOGD("vm_stack (copied from flag): %s", vm_stack);



    (*env)->ReleaseStringUTFChars(env, input_username, username_chars);
    (*env)->ReleaseStringUTFChars(env, input_flag, flag);

    // 启动虚拟机指令执行
    LOGD("Before vm_start, vm_stack: %s", vm_stack);
    vm_start(&cpu);
    LOGD("After vm_start, vm_stack: %s", vm_stack);

    // 验证 vm_stack 与 enc_flag 是否匹配
    int result = 1;
    for (int i = 0; i < F_LEN; i++) {
        LOGD("Comparing vm_stack[%d]: %d with enc_flag[%d]: %d", i, (int)vm_stack[i], i, enc_flag[i]);
        if ((int)vm_stack[i] != enc_flag[i]) {
            LOGD("Mismatch at index %d", i);
            result = 0;
            break;
        }
    }

    LOGD("JNI verifyLogin completed with result: %d", result);
    return result ? JNI_TRUE : JNI_FALSE;
}
