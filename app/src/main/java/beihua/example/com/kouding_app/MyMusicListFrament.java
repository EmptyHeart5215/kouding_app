package beihua.example.com.kouding_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import beihua.example.com.kouding_app.adapter.MyMusicListAdapter;
import beihua.example.com.kouding_app.bean.Mp3Info;
import beihua.example.com.kouding_app.utils.MediaUtils;

/**
 * Created by 李明 on 2017/1/11.
 * function:我的音乐列表
 */
public class MyMusicListFrament extends Fragment  implements AdapterView.OnItemClickListener,View.OnClickListener {

    private ListView listView_my_music;
    private ImageView imageView_album;
    private TextView  textView_songName,textView2_singer;
    private ImageView play_pause,next_singer;
    private ArrayList<Mp3Info> mp3Infos;
    private MainActivity mainActivity;
    private MyMusicListAdapter mymusicAdapter;



    /**
     * 在Frament刚加载时候，拿到mainAction中的context
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    /**
     * 通过静态方法，去实例化
     *  当有参数的时候newInstance(int position)，这里没有所以去掉
     * @return
     */
    public static MyMusicListFrament newInstance() {
        MyMusicListFrament mymusic = new MyMusicListFrament();

        return mymusic;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_music_list_layout,null);
        listView_my_music = (ListView) view.findViewById(R.id.listview_my_music);

        imageView_album = (ImageView) view.findViewById(R.id.imageView_album);//专辑图片
        play_pause = (ImageView) view.findViewById(R.id.play_pause); //播放暂停
        next_singer = (ImageView) view.findViewById(R.id.next_singer); //下一首

        textView_songName = (TextView) view.findViewById(R.id.textView_songName); //歌名
        textView2_singer = (TextView) view.findViewById(R.id.textView2_singer); //歌手

        loadData();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //启动service在引导页面启动，在这里进行绑定播放服务
        mainActivity.bindPlayService();
    }

    //在销毁的时候，解决绑定播放服务
    @Override
    public void onPause() {
        super.onPause();
        mainActivity.unbindPlayService();
    }

    /**
     * 加载本地音乐列表
     */
    private void loadData() {

        mp3Infos = MediaUtils.getMp3Infos(mainActivity);
        mymusicAdapter = new MyMusicListAdapter(mainActivity,mp3Infos);
        listView_my_music.setAdapter(mymusicAdapter);
        //注册事件
       listView_my_music.setOnItemClickListener(this);
        play_pause.setOnClickListener(this);
        next_singer.setOnClickListener(this);
        imageView_album.setOnClickListener(this);



    }

    //当单击列表的时候，进行播放
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        mainActivity.playService.play(position);
//        changUIStatusOnPlay(position);
    }


    //回调播放状态下的UI设置
    public void changUIStatusOnPlay(int position){
        if (position>=0&&position<mp3Infos.size()){

            Mp3Info mp3Info = mp3Infos.get(position);
            textView_songName.setText(mp3Info.getTitle());
            textView2_singer.setText(mp3Info.getArList());

            //设置专辑图片
            Bitmap albumBitmap = MediaUtils.getArtwork(mainActivity,mp3Info.getId(),mp3Info.getAlbumId(),true,true);

            imageView_album.setImageBitmap(albumBitmap);

            if (mainActivity.playService.isPlaying()){

                play_pause.setImageResource(R.mipmap.landscape_player_btn_pause_normal);
            }else {
                play_pause.setImageResource(R.mipmap.ring_btnplay);

            }

        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.play_pause:
                   if (mainActivity.playService.isPlaying()){
                       //设置暂停图标
                       play_pause.setImageResource(R.mipmap.ring_btnstop);
                       mainActivity.playService.pause();


                   }else {
                       //设置播放图标
                       if (mainActivity.playService.isPause()){
                           play_pause.setImageResource(R.mipmap.landscape_player_btn_pause_normal);
                           mainActivity.playService.start();
                       }else {
                           //设置在当前歌曲，从头开始播放（测试先写0）
                           mainActivity.playService.play(0);
                       }

                   }
                break;

            case R.id.next_singer:
                mainActivity.playService.next();
                break;

            case R.id.imageView_album:
                Intent intent = new Intent(mainActivity,MusicPlayActivity.class);
                startActivity(intent);

                break;
            default:
                break;

        }


    }
}
