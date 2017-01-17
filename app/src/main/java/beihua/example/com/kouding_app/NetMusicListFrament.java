package beihua.example.com.kouding_app;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import beihua.example.com.kouding_app.adapter.NetMusicAdapter;
import beihua.example.com.kouding_app.bean.SearchResult;
import beihua.example.com.kouding_app.utils.Constant;

/**
 * Created by 李明 on 2017/1/11.
 * funtion:网络音乐
 */
public class NetMusicListFrament extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {


    private MainActivity mainActivity;
    private ListView listView_net_music;
    private LinearLayout load_layout;
    private LinearLayout ll_search_btn_container;
    private LinearLayout ll_search_container;
    private ImageButton ib_search_content;
    private EditText et_search_content;
    private NetMusicAdapter netMusicAdapter;
    private int page = 1; //搜素音乐页码
    private ArrayList<SearchResult> serarchResults = new ArrayList<>();


    public static NetMusicListFrament newInstance() {
        NetMusicListFrament net = new NetMusicListFrament();
        return net;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.net_music_list_layout, null);


        indata(view);
        loadNetDate();

        return view;

    }


    /**
     * UI组建的初始化
     * @param view
     */
    private void indata(View view) {

        listView_net_music = (ListView) view.findViewById(R.id.listView_net_nusic);
        load_layout = (LinearLayout) view.findViewById(R.id.load_layout);  //
        ll_search_btn_container = (LinearLayout) view.findViewById(R.id.ll_search_btn_container); //显示搜索框
        ll_search_container = (LinearLayout) view.findViewById(R.id.ll_search_container);//隐藏
        ib_search_content = (ImageButton) view.findViewById(R.id.ib_search_btn);  //隐藏搜索的搜索ImageButton
        et_search_content = (EditText) view.findViewById(R.id.et_search_content);  //隐藏所搜的EditText


        listView_net_music.setOnItemClickListener(this);
        ll_search_btn_container.setOnClickListener(this);
        ll_search_container.setOnClickListener(this);


    }

    /**
     * 加载网络音乐
     */
    private void loadNetDate() {
        load_layout.setVisibility(View.VISIBLE);  //显示布局

        //助兴异步加载网络音乐任务
        new loadNetDataTask().execute(Constant.BAIDU_URL+ Constant.BAIDU_DAYHOT);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.ll_search_btn_container:

                ll_search_btn_container.setVisibility(View.GONE);
                ll_search_container.setVisibility(View.VISIBLE);
                break;

            case R.id.ib_search_btn :
                //搜索事件处理
                searchMusic();
                break;
        }


    }

    private void searchMusic() {



    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    /**
     * 加载音乐的异步任务
     */
    private class loadNetDataTask  extends AsyncTask<String,Integer,Integer>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            load_layout.setVisibility(View.VISIBLE);
            listView_net_music.setVisibility(View.GONE);
            serarchResults.clear();
        }

        @Override
        protected Integer doInBackground(String... params) {

            String url =params[0];

            try {
                Document doc = Jsoup.connect(url).userAgent(Constant.USER_AGENT).timeout(6*1000).get();
            //    System.out.println(doc);

                Elements songTitles = doc.select("span.song-title"); //得到： <span class="song-title " style="width: 305px;"><a href="/song/266322583" target="_blank" title="周杰伦前世情人" data-film="null">前世情人</a></span>
                Elements artists  =doc.select("span.author_list"); //得到：<span class="singer" style="width: 240px;"> <span class="author_list" title="周杰伦"> <a hidefocus="true" href="/artist/7994">周杰伦</a> </span> </span>

                for (int i =0;i<songTitles.size();i++){

                    SearchResult  searchResult =new SearchResult();
                    Elements urls = songTitles.get(i).getElementsByTag("a"); //得到：<a href="/song/266322583" target="_blank" title="周杰伦前世情人" data-film="null">前世情人</a>
                    searchResult.setUrl(urls.get(0).attr("href"));  //得到：/song/266322583

                    searchResult.setMusicName(urls.get(0).text()); //得到：前世情人 ，不是title里面的值

                    Elements artistElements = artists.get(i).getElementsByTag("a"); //得到： <a hidefocus="true" href="/artist/7994">周杰伦</a>

                    searchResult.setArtist(artistElements.get(0).text());  //得到：周杰伦

                    searchResult.setAlbum("热搜榜");
                    serarchResults.add(searchResult);




                }
                    System.out.println(serarchResults);

            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }


            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            if (integer ==1){
                netMusicAdapter = new NetMusicAdapter(mainActivity,serarchResults);
                listView_net_music.setAdapter(netMusicAdapter);
              //  listView_net_music.addFooterView(LayoutInflater.from(mainActivity).inflate(R.layout.,f));



            }

            load_layout.setVisibility(View.GONE);
            listView_net_music.setVisibility(View.VISIBLE);


        }
    }
}
