package cn.shilight.gfly.mqtt;

import android.content.Context;
import org.eclipse.paho.android.service.MqttAndroidClient;

public class MqttManager {
    private static MqttManager instance;
    private MqttAndroidClient mqttClient;

    private MqttManager(Context context, String serverUri, String clientId) {
        mqttClient = new MqttAndroidClient(context, serverUri, clientId);
        // 连接MQTT服务器等其他初始化操作
    }

    public static synchronized MqttManager getInstance(Context context, String serverUri, String clientId) {
        if (instance == null) {
            instance = new MqttManager(context, serverUri, clientId);
        }
        return instance;
    }

    public MqttAndroidClient getMqttClient() {
        return mqttClient;
    }
}
