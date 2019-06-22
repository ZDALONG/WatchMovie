package com.gu.cardstackviewpager.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gu.cardstackviewpager.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

public class MoviePageActivity extends AppCompatActivity implements Runnable{
    Handler handler1;
    //private DataDBHelper dataDBHelper;
    private DatasDBHelper datasDBHelper;
    private HrefDBHelper hrefDBHelper;
    ImageView post = null;
    TextView name;
    TextView date;
    TextView grade;
    TextView intro;
    Button want;
    Button web;
    private  String number;
    private String weblink;
    String[] a ;
    protected ArrayList<String> movHref = new ArrayList<>();
    String imgUrl, movname, movdate, movintro, movscore;
    private final String TAG = "Movie";
    String MovHref[]={"https://movie.douban.com/subject/1292052/", "https://movie.douban.com/subject/1291546/",
            "https://movie.douban.com/subject/1295644/", "https://movie.douban.com/subject/1292720/", "https://movie.douban.com/subject/1292063/",
            "https://movie.douban.com/subject/1292722/", "https://movie.douban.com/subject/1291561/", "https://movie.douban.com/subject/1295124/",
            "https://movie.douban.com/subject/3541415/", "https://movie.douban.com/subject/3011091/", "https://movie.douban.com/subject/2131459/",
            "https://movie.douban.com/subject/3793023/", "https://movie.douban.com/subject/1292001/", "https://movie.douban.com/subject/1291549/",
            "https://movie.douban.com/subject/1292064/", "https://movie.douban.com/subject/1292213/", "https://movie.douban.com/subject/1889243/",
            "https://movie.douban.com/subject/1291560/", "https://movie.douban.com/subject/1291841/", "https://movie.douban.com/subject/5912992/",
            "https://movie.douban.com/subject/1307914/", "https://movie.douban.com/subject/25662329/", "https://movie.douban.com/subject/1849031/",
            "https://movie.douban.com/subject/3319755/", "https://movie.douban.com/subject/6786002/"};
    private int[] images = new int[]{
            R.drawable.p0, R.drawable.p1, R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5,R.drawable.p6,R.drawable.p7,R.drawable.p8,
            R.drawable.p9,R.drawable.p10,R.drawable.p11,R.drawable.p12,R.drawable.p13,R.drawable.p14,R.drawable.p15,R.drawable.p16,R.drawable.
            p17,R.drawable.p18, R.drawable.p19,R.drawable.p20,R.drawable.p21,R.drawable.p22,R.drawable.p23,R.drawable.p24};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_page);

        this.post = findViewById(R.id.post);
        name = findViewById(R.id.name);
        date = findViewById(R.id.date);
        grade = findViewById(R.id.grade);
        intro = findViewById(R.id.intro);
        web = findViewById(R.id.web_link);
        want = findViewById(R.id.want);


        Intent intent = getIntent();
        number = intent.getStringExtra("number");
        Log.i(TAG,"Num = "+number);
        post.setBackgroundDrawable(getResources().getDrawable(images[Integer.parseInt(number)]));



        hrefDBHelper = new HrefDBHelper(MoviePageActivity.this,"Href.db",null,1);
        SQLiteDatabase db = hrefDBHelper.getWritableDatabase();
        Cursor cursor = db.query("Href", null, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String pages = cursor.getString(cursor.getColumnIndex("href"));
                movHref.add(pages);
            }while(cursor.moveToNext());
        }
        //cursor.close();
        Log.d("xfhy","movHref = "+movHref);

        datasDBHelper = new DatasDBHelper(MoviePageActivity.this, "Data.db", null, 1);

        //开启子线程，启动run方法
        Thread d = new Thread(this);
        d.start();

        handler1 = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 7) {
                    Bundle bdl = (Bundle) msg.obj;
                    name.setText(bdl.getString("mov_name"));
                    date.setText("上映日期："+bdl.getString("mov_date"));
                    intro.setText(bdl.getString("intro"));
                    grade.setText("评分："+bdl.getString("mov_score"));
                    weblink = bdl.getString("yugao");
                }
                super.handleMessage(msg);
            }
        };

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(WebViewActivity.URL_KEY, weblink);
                Intent intent = new Intent(MoviePageActivity.this, WebViewActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        want.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                SQLiteDatabase db = datasDBHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

                Cursor cursor = db.query("Want", null, null, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        int pages = cursor.getInt(cursor.getColumnIndex("numWant"));
                        Log.d("xfhy", "pageswant :" + pages);
                        if (pages == Integer.parseInt(number)) {
                            values.clear();
                            Toast.makeText(MoviePageActivity.this, "请勿重复添加", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            values.put("numWant", Integer.parseInt(number));
                            db.insert("Want", null, values);
                        }
                    } while (cursor.moveToNext());
                }
                //cursor.close();
            }
        });


    }

    @Override
    public void run() {
        Log.i(TAG, "run: run()......");
        for (int i = 1; i < 3; i++) {
            Log.i(TAG, "run: i=" + i);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //获取网络数据并解析成字符串
        Bundle bundle = new Bundle();
        Document doc = null;
        try {
            //String url = "https://movie.douban.com/subject/1950148/";
            String url = movHref.get(Integer.parseInt(number));
            //String url = MovHref[0];
            doc = Jsoup.connect(url).get();
            Log.i(TAG, "run: " + doc.title());
            //获取电影名字
            Elements h = doc.getElementsByTag("h1");
            Log.i(TAG, "run:h1=" + h.text());
            bundle.putString("mov_name",h.text());
            //获取电影名字
            Elements spans = doc.getElementsByTag("span");
           /* for (int i =0;i<spans.size();i++){
               Element span1 = spans.get(i);
                String sp = span1.text();
                Log.i(TAG,"run:span="+ i +"  "+sp);
            }*/
            Element span5 = spans.get(5);
            // name.setText(span5.text());
            bundle.putString("mov_name", span5.text());
            Log.i(TAG, "run: name=" + span5.text());

            //获取上映时间
            Elements date = doc.getElementsByAttributeValue("property","v:initialReleaseDate");
            Log.i(TAG,"property = "+date.text());
            /*Element span21 = spans.get(21);
            Element span22 = spans.get(22);*/
            //Element span23 = spans.get(23);
            bundle.putString("mov_date",  date.text());


            //获取剧情简介
            Elements intro = doc.getElementsByAttributeValue("property","v:summary");
            bundle.putString("intro",intro.text());
            Log.i(TAG, "run: intro=" + intro.text());

            //获取评分
            Elements grade = doc.getElementsByAttributeValue("property","v:average");
            /*Elements strongs = doc.getElementsByTag("strong");
            Element strong = strongs.get(0);*/
            bundle.putString("mov_score", grade.text());
            //grade.setText("评分：" + strong.text());
            Log.i(TAG, "run: grade=" + grade.text());
           /* for (int i=0;i<strongs.size();i++){
                Element strong = strongs.get(i);
                Log.i(TAG,"run:strong="+ i +"  "+strong.text());
            }*/

            Elements vedio = doc.getElementsByAttributeValue("title","预告片");
            bundle.putString("yugao",vedio.attr("href"));
            Log.i(TAG,"a=" +vedio.attr("href"));


        } catch (IOException e) {
            e.printStackTrace();
        }

        //把获取到的数据存入bundel
        //获取Msg对象，用于返回主线程
        Message msg = handler1.obtainMessage(7);
        //msg.what = 5;
        msg.obj = bundle;
        handler1.sendMessage(msg);

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
