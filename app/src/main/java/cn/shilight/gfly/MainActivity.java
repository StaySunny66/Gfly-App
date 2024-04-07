package cn.shilight.gfly;

import android.content.Intent;
import android.os.Bundle;

import cn.shilight.gfly.R;
import cn.shilight.gfly.mqtt.MqttManager;
import cn.shilight.gfly.util.BatteryUtil;
import cn.shilight.gfly.util.NetworkUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity {

    private myHandler mhandler = new myHandler();

    ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);


        setContentView(R.layout.activity_main);




        progressBar = findViewById(R.id.progressBar);


        new Thread(new Runnable() {
            @Override
            public void run() {
                // 检测网络

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if(NetworkUtil.isInternetConnected(getApplicationContext())){
                    //所要发送的内容
                    Message msg = Message.obtain();

                    msg.what = 20;
                    //将信息发送给你要发送到的对象
                    mhandler.sendMessage(msg);

                    Log.i("first chick","网络通过");

                }else {
                    Message msg = Message.obtain();

                    msg.what = -1;
                    //将信息发送给你要发送到的对象
                    mhandler.sendMessage(msg);
                    Log.i("first chick","不通过");

                    return;

                }
                // 检测电量

                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                if(BatteryUtil.getBatteryLevel(getApplicationContext())>20){
                    //所要发送的内容

                    Message msg = Message.obtain();

                    msg.what = 40;
                    //将信息发送给你要发送到的对象
                    mhandler.sendMessage(msg);

                    Log.i("first chick","电量通过");


                }else {
                    Message msg = Message.obtain();

                    msg.what = -1;
                    //将信息发送给你要发送到的对象
                    mhandler.sendMessage(msg);
                    Log.i("first chick","电量不通过");

                    return;


                }
                // 连接MQTT

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                MqttManager mqttManager = MqttManager.getInstance(getApplicationContext(),"tcp://mqtt.gfly.shilight.cn:1883","gxyos");
                MqttAndroidClient mqttAndroidClient = mqttManager.getMqttClient();
                    MqttConnectOptions options = new MqttConnectOptions();
                    options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
                    options.setCleanSession(true);
                    options.setUserName("app"); // 设置用户名
                    options.setPassword("gxy7788521".toCharArray()); // 设置密码
                try {
                    IMqttToken token = mqttAndroidClient.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // 连接成功
                            Message msg = Message.obtain();
                            msg.what = 60;
                            //将信息发送给你要发送到的对象
                            mhandler.sendMessage(msg);

                            Log.i("first chick","MQTT 通过");
                        }

                        @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Message msg = Message.obtain();

                            msg.what = -1;
                            //将信息发送给你要发送到的对象
                            mhandler.sendMessage(msg);

                            Log.i("first chick","MQTT 不 通过");

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }



            }

        }).start();












    }


    class myHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if(msg.what>=0){
                Intent intent = new Intent(MainActivity.this,MainActivityMenu.class);
                startActivity(intent);
                progressBar.setProgress(msg.what);

                if(msg.what==100){

                }

            }else {

                switch (msg.what){
                    case -1 :
                        Log.i("handle callback",String.valueOf(-1));
                        break;


                }

            }


        }
    }


}

