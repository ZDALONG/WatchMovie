package com.gu.cardstackviewpager.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.gu.cardstackviewpager.R;
import com.gu.cardstackviewpager.adapter.ContentFragmentAdapter;
import com.gu.cardstackviewpager.fragment.CardFragment;
import com.gu.library.OrientedViewPager;
import com.gu.library.transformer.VerticalStackTransformer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nate on 2016/7/22.
 */
public class HomeActivity extends AppCompatActivity implements Runnable{
    private Handler handler;
    private OrientedViewPager mOrientedViewPager;
    String movname;
    String movhref;
    private static String TAG = "movie";
    String data = "赎罪";
    String movie[] = {"肖申克的救赎", "霸王别姬", "这个杀手不太冷", "阿甘正传", "美丽人生", "泰坦尼克号", "千与千寻", "辛德勒的名单", "盗梦空间", "忠犬八公的故事", "机器人总动员", "三傻大闹宝莱坞", "海上钢琴师", "放牛班的春天", "楚门的世界", "大话西游之大圣娶亲", "星际穿越", "龙猫", "教父", "熔炉", "无间道", "疯狂动物城", "当幸福来敲门", "怦然心动", "触不可及"};
    private ArrayList<String>list1 = new ArrayList<>();
    private DatasDBHelper datasDBHelper;
    private HrefDBHelper hrefDBHelper;

    private ContentFragmentAdapter mContentFragmentAdapter;
    private List<Fragment> mFragments = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mOrientedViewPager = findViewById(R.id.view_pager);

        //制造数据
        for (int i = 0; i < 10; i++) {
            mFragments.add(CardFragment.newInstance(movie[i],i));
            Log.i(TAG,"neme = "+movie[i]+"number="+i);
        }

        mContentFragmentAdapter = new
                ContentFragmentAdapter(getSupportFragmentManager(), mFragments);
        //设置viewpager的方向为竖直
        mOrientedViewPager.setOrientation(OrientedViewPager.Orientation.VERTICAL);
        //设置limit
        mOrientedViewPager.setOffscreenPageLimit(4);
        //设置transformer
        mOrientedViewPager.setPageTransformer(true, new VerticalStackTransformer(getApplicationContext()));
        mOrientedViewPager.setAdapter(mContentFragmentAdapter);

        //跳转关于我的界面
        findViewById(R.id.about_iv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, GridView1Activity.class);
                startActivity(intent);
            }
        });

        //开启子线程，启动run方法
        Thread t = new Thread(this);
        t.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    list1 = (ArrayList<String>) msg.obj;

                    hrefDBHelper = new HrefDBHelper(HomeActivity .this,"Href.db",null,1);
                    SQLiteDatabase db = hrefDBHelper.getWritableDatabase();
                    db.delete("Href", null, null);  //这里表示删除Book表里的全部数据
                    ContentValues values = new ContentValues();
                    for (int i=0;i<list1.size();i++){
                        values.put("href",list1.get(i));
                        db.insert("Href",null,values);
                    }

                    //ContentValues values = new ContentValues();
                    //db.delete("Href",null,null);//这里表示删除Href表里的全部数据
                    //存入数据库

                    Toast.makeText(HomeActivity.this, "已更新数据", Toast.LENGTH_SHORT).show();
                    Log.i(TAG,"movie:list1="+ list1);
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void run() {
        Log.i(TAG, "run: run()......");
        for (int i = 1; i < 3; i++) {
            Log.i(TAG, "run: i=" + i);
            try {
                Thread.sleep(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //获取网络数据并解析成字符串
        Bundle bundle = new Bundle();
        org.jsoup.nodes.Document doc = null;
        ArrayList<String>retList = new ArrayList<String>();
        ArrayList<String>hrefList = new ArrayList<String>();
        try {

            for (int count = 0; count<40; count=count+25){
                String url = String.format("https://movie.douban.com/top250?start=%s&filter=",count);
                doc = Jsoup.connect(url).get();

                Elements imgs = (Elements) doc.getElementsByTag("img");
                Elements as = (Elements) doc.getElementsByTag("a");
                int y = 28;
                for (int i=0;i<25;i++){
                    Element img = imgs.get(i);
                    //movList [i] = img.attr("alt");
                    movname = img.attr("alt");
                    retList.add(movname);//数据带回页面
                    //list1.add(movname);
                    bundle.putStringArrayList("mov_name",retList);
                    Log.i(TAG,"movie:movlist="+i+ "-->"+movname);
                }
                for (int x=28;x<77;x=x+2){
                    Element a = as.get(x);
                    movhref = a.attr("href");

                    hrefList.add(movhref);
                    list1.add(movhref);


                    bundle.putStringArrayList("mov_href",hrefList);
                    Log.i(TAG,"movie:href="+x+ "-->"+movhref);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //把获取到的数据存入bundel
        //获取Msg对象，用于返回主线程
        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        msg.obj = list1;
        msg.setData(bundle);
        handler.sendMessage(msg);

    }
    //将输入流InputStream转换为String的方法
    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "utf-8");
        while (true) {
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }

}
