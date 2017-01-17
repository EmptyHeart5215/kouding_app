package beihua.example.com.kouding_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import beihua.example.com.kouding_app.R;
import beihua.example.com.kouding_app.bean.Mp3Info;
import beihua.example.com.kouding_app.utils.MediaUtils;

/**
 * Created by 李明 on 2017/1/11.
 */
public class MyMusicListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Mp3Info> mp3Infos;

    public MyMusicListAdapter(Context context, ArrayList<Mp3Info> mp3Infos) {
        this.context = context;
        this.mp3Infos = mp3Infos;

    }

    //因为有可能需要改变值，所以建立set方法
    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int i) {
        return mp3Infos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_music_list, null);
            vh = new ViewHolder();
            vh.textView1_title = (TextView) convertView.findViewById(R.id.textView1_title);
            vh.textView2_singer = (TextView) convertView.findViewById(R.id.textView2_singer);
            vh.textView3_time = (TextView) convertView.findViewById(R.id.textView3_time);

            convertView.setTag(vh);

        }

        vh = (ViewHolder) convertView.getTag();
        Mp3Info mp3Info = mp3Infos.get(position);
        vh.textView1_title.setText(mp3Info.getTitle());
        vh.textView2_singer.setText(mp3Info.getArList());
        vh.textView3_time.setText(MediaUtils.formatTime(mp3Info.getDuration()));


        return convertView;
    }


    static class ViewHolder {

        ImageView imagView1_icn;
        TextView textView1_title;
        TextView textView2_singer;
        TextView textView3_time;


    }
}
