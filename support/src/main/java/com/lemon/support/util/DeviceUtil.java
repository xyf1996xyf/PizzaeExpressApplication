package com.lemon.support.util;

import android.app.ActivityManager;
import android.content.Context;
import android.telephony.TelephonyManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by yerunjie on 18/1/14.
 */

public class DeviceUtil {
    /**
     * 获取机器唯一标识
     *
     * @param context
     * @return
     */
    public static String getLocaldeviceId(Context context) {
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (deviceId == null
                || deviceId.trim().length() == 0) {
            deviceId = String.valueOf(System
                    .currentTimeMillis());
        }
        return deviceId;
    }

    //获取可用运存大小
    public static long getAvailMemory(Context context) {
        // 获取android当前可用内存大小
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        return mi.availMem / (1024 * 1024);
    }

    //获取总运存大小
    public static long getTotalMemory() {
        String str1 = "/proc/meminfo";// 系统内存信息文件
        String str2;
        String[] arrayOfString;
        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(
                    localFileReader, 8192);
            str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
            arrayOfString = str2.split("\\s+");
            initial_memory = Long.valueOf(arrayOfString[1]);// 获得系统总内存，单位是KB，乘以1024转换为Byte
            localBufferedReader.close();
        } catch (IOException e) {
        }
        return initial_memory / 1024;
    }
}
