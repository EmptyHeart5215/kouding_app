package beihua.example.com.kouding_app;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import beihua.example.com.kouding_app.bean.Mp3Info;
import beihua.example.com.kouding_app.utils.MediaUtils;

/**
 * 注意：在activity中不能直接通过调用servier对象里的方法
 * 音乐播放的组件
 * 实现功能
 * 1·播放
 * 2·暂停
 * 3·上一首
 * 4·下一首
 * 5·获取当前的播放进度
  */
public class PlayService extends Service  implements MediaPlayer.OnCompletionListener , MediaPlayer.OnErrorListener {

    private MediaPlayer mPlayer;
    //标记：表示当前正在播放歌曲的位置
    private int  currentPostion;

    ArrayList<Mp3Info> mp3Infos;
    //声明接口属性
    private MusicUpdataListener musicUpdataListener;

    //创建一个线程池
    private ExecutorService es= Executors.newSingleThreadExecutor();

    private boolean isPause =false;

    //三种播放模式标签
    public  static  final int  ORDER_PLAU =1; //顺序
    public static  final  int RANDOM_PLAY = 2;//随机
    public  static  final  int SINGLE_PLAY= 3;//单曲

    private int play_mode =ORDER_PLAU;


    //获取播放模式
    public int getPlay_mode() {
        return play_mode;
    }
    //设置播放模式
    public void setPlay_mode(int play_mode) {
        this.play_mode = play_mode;
    }

    //初始化 随机
    private Random random =new Random();
    //下面两种是实现MediaPlayer接口的父类方法
    @Override
    public void onCompletion(MediaPlayer mp) {

        switch (play_mode){
            case ORDER_PLAU:
                next();
                break;
            case  RANDOM_PLAY:
                  play( random.nextInt(mp3Infos.size()));
                break;
            case  SINGLE_PLAY:

                play(currentPostion);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }


    public boolean isPause(){
        return isPause;
    }


    public PlayService() {

    }

    //当前播放的Postion ，也就是第几首歌
    public int getCurrentPostion(){
        return currentPostion;
    }




    /**
     * 通过内部类形式，返回PlayService的对象
     */
    class  PlayBinder extends Binder{

        public PlayService getPlayService(){

            return PlayService.this;
        }
    }




    @Override
    public IBinder onBind(Intent intent) {

       return new PlayBinder();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        //注册事件
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnErrorListener(this);
        //得到mp3的列表
        mp3Infos = MediaUtils.getMp3Infos(this);
        //开始线程
        es.execute(updateStatusRunnable);
    }

    //销毁线程
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (es!=null && !es.isShutdown()){
            es.shutdown();
            es=null;
        }
    }

    //更新状态       isPlaying 播放
    Runnable updateStatusRunnable = new Runnable() {
        @Override
        public void run() {

            while (true){
                if (musicUpdataListener!=null &&mPlayer!=null &&mPlayer.isPlaying()){
                    musicUpdataListener.onPublish(getCurrentProgress());

                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };







    //从头开始播放
    public void play(int position){

        if (position>=0&&position<mp3Infos.size()) {
            Mp3Info mp3Info = mp3Infos.get(position);

            try {
                mPlayer.reset();
                mPlayer.setDataSource(this, Uri.parse(mp3Info.getUrl()));
                mPlayer.prepare();
                mPlayer.start();
                currentPostion = position;

            } catch (IOException e) {
                e.printStackTrace();
            }
            if (musicUpdataListener!=null){
                musicUpdataListener.onChange(currentPostion);
            }
        }

    }
    //暂停
    public void pause(){
            if (mPlayer.isPlaying()){
                mPlayer.pause();
                isPause = true;
            }

    }
    //下一首
    public void next() {
        if (currentPostion == 0) {
            currentPostion++;

        } else if (currentPostion + 1 >= mp3Infos.size() - 1) {
            currentPostion = 0;
        } else {

            currentPostion++;
        }
        play(currentPostion);
     }


    //上一首
    public void prev(){
        if (currentPostion-1<0 ){
            currentPostion = mp3Infos.size()-1;
        }else{
            currentPostion --;
        }
        play(currentPostion);

    }
    //查看当前是否在播放状态
    public boolean isPlaying(){
        if (mPlayer != null){
            return  mPlayer.isPlaying();
        }
        return  false;
    }

    //当暂停时，进行播放
    public void start(){

        //如果不在播放状态，则。。
        if(mPlayer !=null && !mPlayer.isPlaying()){
            mPlayer.start();


        }

    }

    //获取当前进度值
    public int getCurrentProgress(){
        //如果！=null和长在播放状态，获取当前进度
        if (mPlayer!=null&&mPlayer.isPlaying()){

            return mPlayer.getCurrentPosition();
        }

        return 0;
    }




    //获取当前播放持续时间
    public int getDuration(){


        return mPlayer.getDuration();
    }

    //跳转到某地
    public void seekTo(int msec){
        mPlayer.seekTo(msec);
    }





    //·············································································································接口回调
    //定义更新状态接口  在BaseActivity中去实现
    public interface MusicUpdataListener{

            //状态条
            public void onPublish(int progess);
            //是否在当前位置
            public void onChange(int position);

    }


    public void setMusicUpdataListener(MusicUpdataListener musicUpdataListener) {
        this.musicUpdataListener = musicUpdataListener;
    }
}
