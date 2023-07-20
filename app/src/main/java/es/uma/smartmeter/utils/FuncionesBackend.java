package es.uma.smartmeter.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class FuncionesBackend {
    private static String password;
    private static String tokenDispositivo = null;

    public static String getTokenDispositivo() {
        return tokenDispositivo;
    }

    public static void setTokenDispositivo(String tokenDispositivo) {
        FuncionesBackend.tokenDispositivo = tokenDispositivo;
    }

    public static String getWifi(Context context) {
        WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = mWifiManager.getConnectionInfo();

        return info.getSSID();
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        FuncionesBackend.password = password;
    }
}
