package beihua.example.com.kouding_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * 闪屏页（初始页面）
 */
public class SqlashActivy extends Activity {


    private static final int START_ACTIVITY =0x1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_sqlash_activy);

        //在欢迎页面中就启动service
        startService(new Intent(this,PlayService.class));
        handler.sendEmptyMessageDelayed(START_ACTIVITY,1000);

    }

    /**
     * 在3秒后跳转到主界面
     */

    private Handler handler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case START_ACTIVITY:
                        startActivity(new Intent(SqlashActivy.this, MainActivity.class));
                        finish();
                    break;

            }
        }
    };
}
