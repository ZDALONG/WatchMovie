package com.gu.cardstackviewpager.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gu.cardstackviewpager.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.media.CamcorderProfile.get;

public class GridView1Activity extends AppCompatActivity implements Runnable{
    ArrayList<Integer>counts = new ArrayList<>();
    List<Integer> count = new ArrayList<>();
    private DatasDBHelper datasDBHelper;
    private HrefDBHelper hrefDBHelper;
    protected ArrayList<String> movHref = new ArrayList<>();
    Handler handler;
    private static String TAG = "movie";
    private GridView mGV1;
    private String[] titles = new String[] {"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24"};
    private int[] images = new int[]{
            R.drawable.p0, R.drawable.p1, R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5,R.drawable.p6,R.drawable.p7,R.drawable.p8,
            R.drawable.p9,R.drawable.p10,R.drawable.p11,R.drawable.p12,R.drawable.p13,R.drawable.p14,R.drawable.p15,R.drawable.p16,R.drawable.
            p17,R.drawable.p18, R.drawable.p19,R.drawable.p20,R.drawable.p21,R.drawable.p22,R.drawable.p23,R.drawable.p24};
    MyGridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_view1);
        mGV1=findViewById(R.id.GV1);

        hrefDBHelper = new HrefDBHelper(GridView1Activity.this,"Href.db",null,1);
        SQLiteDatabase db = hrefDBHelper.getWritableDatabase();
        Cursor cursor = db.query("Href", null, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String pages = cursor.getString(cursor.getColumnIndex("href"));
                movHref.add(pages);
            }while(cursor.moveToNext());
        }
        cursor.close();
        Log.d("xfhy","movHref = "+movHref);



        //开启子线程，启动run方法
      Thread d = new Thread(this);
        d.start();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    Bundle bdl = (Bundle) msg.obj;
                    count = bdl.getIntegerArrayList("data");
                    Log.i(TAG,"count = "+count);

                    adapter = new MyGridViewAdapter(titles,images, GridView1Activity.this);
                    mGV1.setAdapter(adapter);
                }
                super.handleMessage(msg);
            }
        };


       mGV1.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                LinearLayout linearLayout = (LinearLayout) mGV1.getAdapter().getView(position,v,null);
                TextView textView = (TextView) linearLayout.getChildAt(1);
                Log.i(TAG, "title=" + textView.getText());

                Intent intent = new Intent(GridView1Activity.this,MoviePageActivity.class);
                intent.putExtra("number", textView.getText());
                startActivity(intent);

            }
        });

    mGV1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
           @Override
           public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
               Log.i(TAG, "onItemLongClick: 长按列表项position=" + position);
               //删除操作
               //构造对话框进行确认操作
                AlertDialog.Builder builder = new AlertDialog.Builder(GridView1Activity.this);
                builder.setTitle("提示").setMessage("请确认是否删除当前数据").setPositiveButton("是",new DialogInterface.OnClickListener(){

                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       Log.i(TAG, "onClick: 对话框事件处理");

                       LinearLayout linearLayout = (LinearLayout) mGV1.getAdapter().getView(position,view,null);
                       TextView textView = (TextView) linearLayout.getChildAt(1);
                       String s = (String) textView.getText();
                       Log.i(TAG, "title=" + textView.getText());

                       datasDBHelper  = new DatasDBHelper(GridView1Activity.this,"Datas.db",null,1);
                       SQLiteDatabase db = datasDBHelper.getWritableDatabase();
                       db.delete("Want", "numWant = ?",new String[]{String.valueOf(s)});

                       Iterator iterator = count.iterator();
                       while (iterator.hasNext()){
                           if (String.valueOf(iterator.next()).equals(s)){
                               iterator.remove();
                           }
                       }

                       Log.i(TAG,"newcount = "+count);
                       adapter = new MyGridViewAdapter(titles,images, GridView1Activity.this);
                       mGV1.setAdapter(adapter);

                       linearLayout.removeAllViews();
                       adapter.notifyDataSetChanged();//刷新gridview
                   }
               }).setNegativeButton("否",null);
               builder.create().show();
               //Log.i(TAG, "onItemLongClick: size=" + listItems.size());

               return true;
           }
       });

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
        Bundle bundle = new Bundle();
        datasDBHelper  = new DatasDBHelper(GridView1Activity.this,"Datas.db",null,1);
        SQLiteDatabase db = datasDBHelper.getWritableDatabase();
        Cursor cursor = db.query("Want", null, null, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                int pages = cursor.getInt(cursor.getColumnIndex("numWant"));
                counts.add(pages);
                Log.d("xfhy","pages :"+pages +"counts = "+counts);
            }while(cursor.moveToNext());
        }
        //cursor.close();
        bundle.putIntegerArrayList("data", counts);

        Message msg = handler.obtainMessage(5);
        //msg.what = 5;
        msg.obj = bundle;
        handler.sendMessage(msg);

    }

    public class MyGridViewAdapter extends BaseAdapter {

        //声明引用
        private LayoutInflater inflater;
        private List<Picture> pictures;

        public MyGridViewAdapter(String[] titles, int[] images, Context context) {
            super();
            pictures = new ArrayList<Picture>();
            inflater = LayoutInflater.from(context);
            if (count != null){
                for (int x = 0; x < count.size(); x++)
                {
                    int t = count.get(x);
                    Picture picture = new Picture(titles[t], images[t]);
                    pictures.add(picture);
                }
            }
        }

        @Override
        public int getCount() {
            if (null != pictures)
            {
                return pictures.size();
            } else
            {
                return 0;
            }
        }
        @Override
        public Object getItem(int position) {
            return pictures.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public  View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null)
            {
                convertView = inflater.inflate(R.layout.gridview1_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title = convertView.findViewById(R.id.title);
                viewHolder.image = convertView.findViewById(R.id.image);
                convertView.setTag(viewHolder);
            } else
            {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.title.setText(pictures.get(position).getTitle());
            viewHolder.image.setImageResource(pictures.get(position).getImageId());
            return convertView;
        }

        public class ViewHolder {
            public TextView title;
            public ImageView image;
        }
    }
}

class Picture {
    private String title;
    private int imageId;

    public Picture(int image, String title)
    {
        super();
    }

    public Picture(String title, int imageId){
        super();
        this.title = title;
        this.imageId = imageId;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public int getImageId()
    {
        return imageId;
    }
    public void setImageId(int imageId)
    {
        this.imageId = imageId;
    }

}