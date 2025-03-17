
package com.example.redrocker;

import android.util.Log;


public class JniBridge {


    static {
        try {
            System.loadLibrary("vm_protection");
            Log.d("JNI", "本地库加载成功");
        } catch (UnsatisfiedLinkError e) {
            Log.e("JNI", "本地库加载失败", e);
        }
    }

    public native boolean verifyLogin(String username, String inputPassword);
}
