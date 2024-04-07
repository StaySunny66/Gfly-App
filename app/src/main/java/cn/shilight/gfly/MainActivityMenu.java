package cn.shilight.gfly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import cn.shilight.gfly.R;

public class MainActivityMenu extends AppCompatActivity {



    private Button flyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 全屏显示
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_main_menu);

        flyButton = findViewById(R.id.flybutton);



        // 起飞
        flyButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, FlyActivity.class);
            startActivity(intent);



        });

    }
}