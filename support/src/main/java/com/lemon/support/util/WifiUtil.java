package com.lemon.support.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by legend on 18/1/14.
 */

public class WifiUtil {
    public List<WifiModel> readWifiInfo() throws Exception {
        List<WifiModel> wifiInfos = new ArrayList<WifiModel>();

        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());
            dataOutputStream
                    .writeBytes("cat /data/misc/wifi/*.conf\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
                throw e;
            }
        }


        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiConf.toString());
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();
            Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
            Matcher ssidMatcher = ssid.matcher(networkBlock);

            if (ssidMatcher.find()) {
                WifiModel wifiInfo = new WifiModel();
                wifiInfo.Ssid = ssidMatcher.group(1);
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);
                if (pskMatcher.find()) {
                    wifiInfo.Password = pskMatcher.group(1);
                } else {
                    wifiInfo.Password = "无密码";
                }
                wifiInfos.add(wifiInfo);
            }

        }

        return wifiInfos;
    }

    public class WifiModel {
        public String Ssid = "";
        public String Password = "";
    }

    public WifiInfo getConnectWifi(Context context) {
        NetworkInfo networkInfo = getCurrentNetworkInfo(context);
        WifiInfo wifiinfo = null;
        if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {

            if (networkInfo.getType() == android.net.ConnectivityManager.TYPE_WIFI) {
                WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                wifiinfo = wifiMgr.getConnectionInfo();
                String extrainfo = wifiinfo.getBSSID();
                Log.d("WifiUtil",wifiinfo.toString());
                int subtype = wifiinfo.getNetworkId();

                Log.d("WifiUtil", "getConnectWifi info.."+extrainfo+" = "+subtype);
            }
        }
        return wifiinfo;
    }

    private NetworkInfo getCurrentNetworkInfo(Context context) {
        ConnectivityManager conn = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        return conn.getActiveNetworkInfo();
    }
}