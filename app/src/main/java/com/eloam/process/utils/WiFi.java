package com.eloam.process.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

public class WiFi {

    // 打开WiFi
    public static void openWiFi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
    }

}
