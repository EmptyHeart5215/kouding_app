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
import beihua.example.com.kouding_app.bean.SearchResult;
import beihua.example.com.kouding_app.utils.MediaUtils;

/**
 * Created by 李明 on 2017/1/16.
 */
public class NetMusicAdapter   extends BaseAdapter {
    private Context context;
    private ArrayList<SearchResult> searchResults;

    public NetMusicAdapter(Context context, ArrayList<SearchResult> searchResults) {
        this.context = context;
        this.searchResults = searchResults;

    }
    public ArrayList<SearchResult>  getSearchResults(){return  searchResults;}
    //因为有可能需要改变值，所以建立set方法
    public void setSearchResults(ArrayList<SearchResult> searchResults) {
        this.searchResults = searchResults;
    }

    @Override
    public int getCount() {
        return searchResults.size();
    }

    @Override
    public Object getItem(int i) {
        return searchResults.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_net_list, null);
            vh = new ViewHolder();
            vh.textView1_title = (TextView) convertView.findViewById(R.id.textView1_title);
            vh.textView2_singer = (TextView) convertView.findViewById(R.id.textView2_singer);

            convertView.setTag(vh);

        }

        vh = (ViewHolder) convertView.getTag();
        SearchResult result =searchResults.get(position);
        vh.textView1_title.setText(result.getMusicName());
        vh.textView2_singer.setText(result.getArtist());


        return convertView;
    }


    static class ViewHolder {
        TextView textView1_title;
        TextView textView2_singer;



    }
}
