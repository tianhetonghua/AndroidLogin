cmake_minimum_required(VERSION 3.4.1)

# 添加共享库，列出所有源文件
add_library(
        vm_protection
        SHARED
        jni.c                # JNI 方法的实现文件
        vm_protection.c      # 虚拟机保护的实现文件
)

# 找到 Android 的日志库（liblog.so）
find_library(
        log-lib
        log
)

# 链接日志库（liblog.so）
target_link_libraries(
        vm_protection
        ${log-lib}
)
