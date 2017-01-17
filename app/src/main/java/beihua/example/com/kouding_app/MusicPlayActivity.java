package beihua.example.com.kouding_app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import beihua.example.com.kouding_app.bean.Mp3Info;
import beihua.example.com.kouding_app.utils.MediaUtils;

public class MusicPlayActivity extends BaseActivity implements View.OnClickListener {
    //歌名 ，开始和结束时间
    private TextView tv_title, tv_start_time, tv_end_time;
    //专辑 , 播放模式 ,上一首 ,暂停 ,下一首
    private ImageView im_album, im_play_mode, im_previous, im_pause, im_next;
    private SeekBar seekBar; //进度条

    private ArrayList<Mp3Info> mp3Infos;


    private static final int UPDATE_TIME = 0x1;//跟新播放时间的标记
    //声明
    private MyHandler myhandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);
        initData();

    }

    private void initData() {
        myhandler = new MyHandler(this);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_start_time = (TextView) findViewById(R.id.tv_start_time);
        tv_end_time = (TextView) findViewById(R.id.tv_end_time);
        im_album = (ImageView) findViewById(R.id.im_album);
        im_play_mode = (ImageView) findViewById(R.id.im_play_mode);
        im_previous = (ImageView) findViewById(R.id.im_previous);
        im_pause = (ImageView) findViewById(R.id.im_pause);
        im_next = (ImageView) findViewById(R.id.im_next);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        im_play_mode.setOnClickListener(this);
        im_previous.setOnClickListener(this);
        im_pause.setOnClickListener(this);
        im_next.setOnClickListener(this);


        mp3Infos = MediaUtils.getMp3Infos(this);


    }

    @Override
    public void onResume() {
        super.onResume();
        //绑定service ，当绑定后会调用父类 musicUpdataListener.onChange(playService.getCurrentPostion()); 来更新UI
        //启动service在引导页面启动，在这里进行绑定播放服务
        bindPlayService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindPlayService();
    }

    //这里可以用软引用
    static class MyHandler extends Handler {

        MusicPlayActivity playActivity;

        public MyHandler(MusicPlayActivity playActivity) {
            this.playActivity = playActivity;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (playActivity != null) {
                switch (msg.what) {
                    case UPDATE_TIME:
                        playActivity.tv_start_time.setText(MediaUtils.formatTime(msg.arg1));
                        break;
                }
            }
        }
    }

    @Override
    public void publish(int progress) {
        // tv_start_time.setText(MediaUtils.formatTime(progress));
        //接收进度，因为不能在线程中更新UI，所以需要用到handler
        Message msg = myhandler.obtainMessage(UPDATE_TIME);
        msg.arg1 = progress;
        myhandler.sendMessage(msg);


        seekBar.setProgress(progress);
    }

    @Override
    public void change(int position) {

        //如果正在播放状态
        //     if (this.playService.isPlaying()){

        Mp3Info mp3Info = mp3Infos.get(position);
        tv_title.setText(mp3Info.getTitle());
        Bitmap albumBitmap = MediaUtils.getArtwork(this, mp3Info.getId(), mp3Info.getAlbumId(), true, false);
        im_album.setImageBitmap(albumBitmap);
        tv_end_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));

        seekBar.setProgress(0);
       // im_pause.setImageResource(R.mipmap.ad_play);
        seekBar.setMax((int) mp3Info.getDuration());

        if (playService.isPlaying()) {
            im_pause.setImageResource(R.mipmap.landscape_player_btn_pause_normal);
        } else {
            im_pause.setImageResource(R.mipmap.ring_btnstop);
        }

        switch (playService.getPlay_mode()){
            case PlayService.ORDER_PLAU:

                    im_play_mode.setImageResource(R.mipmap.order_play);
                    im_play_mode.setTag(PlayService.ORDER_PLAU);
                break;
            case PlayService.RANDOM_PLAY:

                im_play_mode.setImageResource(R.mipmap.random_play);
                im_play_mode.setTag( PlayService.RANDOM_PLAY);
                break;
            case PlayService.SINGLE_PLAY:

                im_play_mode.setImageResource(R.mipmap.single_play);
                im_play_mode.setTag(PlayService.SINGLE_PLAY);
                break;

        }

//}


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.im_pause:
                if (playService.isPlaying()) {

                    im_pause.setImageResource(R.mipmap.ring_btnstop);
                    playService.pause();

                } else {

                    //设置播放图标
                    if (playService.isPause()) {
                        im_pause.setImageResource(R.mipmap.landscape_player_btn_pause_normal);
                        playService.start();
                    } else {
                        //设置在当前歌曲，从头开始播放（测试先写0）
                        playService.play(0);
                    }

                }
                break;

            case R.id.im_next:
                playService.next();

                break;

            case R.id.im_previous:
                playService.prev();
                break;
            case R.id.im_play_mode:
                    int mode = (int) im_play_mode.getTag();
                        switch (mode){
                            case PlayService.ORDER_PLAU:
                                im_play_mode.setImageResource(R.mipmap.random_play);
                                im_play_mode.setTag(PlayService.RANDOM_PLAY);
                                playService.setPlay_mode(PlayService.RANDOM_PLAY);
                                break;
                            case PlayService.RANDOM_PLAY:
                                im_play_mode.setImageResource(R.mipmap.single_play);
                                im_play_mode.setTag(PlayService.SINGLE_PLAY);
                                playService.setPlay_mode(PlayService.SINGLE_PLAY);
                                break;
                            case PlayService.SINGLE_PLAY:
                                im_play_mode.setImageResource(R.mipmap.order_play);
                                im_play_mode.setTag(PlayService.ORDER_PLAU);
                                playService.setPlay_mode(PlayService.ORDER_PLAU);
                                break;
                            default:
                                break;


                        }

                break;


            default:
                break;

        }

    }
}
