package cn.shilight.gfly.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                // 判断网络类型是否为移动数据或者Wi-Fi
                return (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) || (activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
            }
        }
        return false;
    }
}
