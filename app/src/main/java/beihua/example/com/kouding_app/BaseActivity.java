package beihua.example.com.kouding_app;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

/**
 * 基本的activity
 * Created by 李明 on 2017/1/12.
 */
public abstract class BaseActivity extends FragmentActivity  {

    protected  PlayService playService;

    private  boolean isBound = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //绑定servier
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            //获取service中内部类的对象
           PlayService.PlayBinder playBinder = (PlayService.PlayBinder) iBinder;
            //通过getPlayService返回playservice这个context
            playService =playBinder.getPlayService();

            //调用状态接口回调
            playService.setMusicUpdataListener(musicUpdataListener);
            musicUpdataListener.onChange(playService.getCurrentPostion());

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            playService =null;
            isBound=false;
        }
    };

    //···········································································································接口回调
    private PlayService.MusicUpdataListener  musicUpdataListener = new PlayService.MusicUpdataListener() {
        @Override
        public void onPublish(int progess) {
            publish(progess);
        }

        @Override
        public void onChange(int position) {
            change(position);
        }
    };

    //定两个抽象的方法，用来在子类中具体实现该方法
    public abstract void publish(int progress);
    public abstract void change(int position);



    //···········································································································



    //绑定服务
    public void bindPlayService(){
        if (!isBound){


        Intent intent = new Intent(this,PlayService.class);
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
            isBound=true;
        }
    }
    //解除绑定服务
    public void unbindPlayService(){
        //如果isBound等于true
        if (isBound) {
            unbindService(conn);
            isBound =false;
        }
    }

}
