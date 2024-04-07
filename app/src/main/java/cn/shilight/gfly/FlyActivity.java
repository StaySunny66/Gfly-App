package cn.shilight.gfly;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.MyLocationStyle;
import cn.shilight.gfly.R;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class FlyActivity extends AppCompatActivity {

    private PowerManager.WakeLock wakeLock;

    private boolean defaultViewMode = true;




    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    ConstraintLayout bigview;
    CardView smallwiew;


    SocketClientTask socketClientTask;


    MapView mMapView = null;


    private  void changeViewMode(){
        if(defaultViewMode==true){
            defaultViewMode = false;

            int index1 = smallwiew.indexOfChild(mMapView);
            int index2 = bigview.indexOfChild(surfaceView);

            smallwiew.removeView(mMapView);
            bigview.removeView(surfaceView);

            smallwiew.addView(surfaceView, index1);
            bigview.addView(mMapView, index2);

            defaultViewMode = false;


        }else {

            int index1 = smallwiew.indexOfChild(surfaceView);
            int index2 = bigview.indexOfChild(mMapView);

            smallwiew.removeView(surfaceView);
            bigview.removeView(mMapView);

            smallwiew.addView(mMapView, index1);
            bigview.addView(surfaceView, index2);


            defaultViewMode = true;


        }



    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // G-fly  飞行主界面

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);


        setContentView(R.layout.activity_fly);


        ///////////////////////////////////////////////////////////////////////////////////////
        smallwiew = findViewById(R.id.smallwindows);
        bigview = findViewById(R.id.bigwindow);



        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);


        // 初始化WakeLock
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, "MyApp:WakeLock");

        // 获取屏幕常亮权限
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();

        // 高德地图 区块


        AMap aMap = null;
        
        if (aMap == null) {
            aMap = mMapView.getMap();

            MyLocationStyle myLocationStyle;
            myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            aMap.getUiSettings().setMyLocationButtonEnabled(false);
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。

        }









        surfaceView.setOnClickListener(view -> {



            changeViewMode();

        });

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                // Surface创建时开始连接服务器
                socketClientTask = new SocketClientTask();
                socketClientTask.execute();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                // Surface销毁时停止连接

                Log.i("surface callback","Destroyed");
                if (socketClientTask != null) {
                   // socketClientTask.cancel(true);
                }
            }
        });




    }


    @Override
    protected void onResume() {
        super.onResume();
        wakeLock.acquire();

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    private class SocketClientTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Socket socket = new Socket("192.168.31.126", 5555);
                InputStream inputStream = socket.getInputStream();
                DataInputStream dataInputStream = new DataInputStream(inputStream);

                while (!isCancelled()) {
                    // 读取帧大小
                    byte[] sizeBuffer = new byte[4];
                    dataInputStream.read(sizeBuffer);
                    int size = ByteBuffer.wrap(sizeBuffer).getInt();

                    // 读取帧数据
                    byte[] buffer = new byte[size];
                    dataInputStream.readFully(buffer);


                    try {

                        // 在Surface上绘制帧数据
                        Canvas canvas = surfaceHolder.lockCanvas();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, size);

                        float scaleX = (float) canvas.getWidth() / bitmap.getWidth();
                        float scaleY = (float) canvas.getHeight() / bitmap.getHeight();
                        float scale = Math.max(scaleX, scaleY);

                        // 计算居中位置
                        float scaledWidth = bitmap.getWidth() * scale;
                        float scaledHeight = bitmap.getHeight() * scale;
                        float translateX = (canvas.getWidth() - scaledWidth) / 2;
                        float translateY = (canvas.getHeight() - scaledHeight) / 2;

                        // 设置Matrix进行缩放和平移
                        Matrix matrix = new Matrix();
                        matrix.setScale(scale, scale);
                        matrix.postTranslate(translateX, translateY);

                        canvas.drawBitmap(bitmap, matrix, null);
                        surfaceHolder.unlockCanvasAndPost(canvas);

                    }catch (Exception E){



                    }


                }

                dataInputStream.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }



}

