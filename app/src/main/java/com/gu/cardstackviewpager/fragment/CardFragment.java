package com.gu.cardstackviewpager.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gu.cardstackviewpager.R;
import com.gu.cardstackviewpager.activity.DatasDBHelper;
import com.gu.cardstackviewpager.activity.MoviePageActivity;


/**
 * Created by Nate on 2016/7/22.
 */
public class CardFragment extends Fragment {
    private static final String INDEX_KEY = "mov_name";
    private static final String INDEX_NUM = "mov_number";
    private static String TAG = "movie";
    private DatasDBHelper datasDBHelper;
    /*private WatchedDBHelper watchedDBHelper;*/
    private int[] images = new int[]{
            R.drawable.p0, R.drawable.p1, R.drawable.p2,R.drawable.p3,R.drawable.p4,R.drawable.p5,R.drawable.p6,R.drawable.p7,R.drawable.p8,
            R.drawable.p9,R.drawable.p10,R.drawable.p11,R.drawable.p12,R.drawable.p13,R.drawable.p14,R.drawable.p15,R.drawable.p16,R.drawable.
            p17,R.drawable.p18, R.drawable.p19,R.drawable.p20,R.drawable.p21,R.drawable.p22,R.drawable.p23,R.drawable.p24};

    public static CardFragment newInstance(String s, int i) {
        CardFragment cardFragment = new CardFragment();
        Bundle bundle = new Bundle();
        bundle.putString(INDEX_KEY, s);
        bundle.putInt(INDEX_NUM, i);
        cardFragment.setArguments(bundle);
        return cardFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_card, container, false);
        TextView cardNumTv = (TextView) v.findViewById(R.id.card_num_tv);

        datasDBHelper = new DatasDBHelper(getActivity(), "Data.db", null, 1);


        ImageView post = v.findViewById(R.id.post);
        TextView mov_name = v.findViewById(R.id.movname);
        TextView num = v.findViewById(R.id.num);
        Button want = v.findViewById(R.id.btn_1);

        final Bundle bundle = getArguments();
        final int Num = bundle.getInt(INDEX_NUM, 0);
        Log.i(TAG, "num" + Num);

        if (bundle != null) {
            mov_name.setText(bundle.getString(INDEX_KEY));
            num.setText(bundle.getInt(INDEX_NUM, 0) + "");
            post.setBackgroundDrawable(getResources().getDrawable(images[Num]));
        }

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),MoviePageActivity.class);
                intent.putExtra("number", bundle.getInt(INDEX_NUM, 0) + "");
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
                        if (pages == Integer.parseInt(bundle.getInt(INDEX_NUM, 0) + "")) {
                            values.clear();
                            Toast.makeText(getActivity(), "请勿重复添加", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            values.put("numWant", Integer.parseInt(bundle.getInt(INDEX_NUM, 0) + ""));
                            db.insert("Want", null, values);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
            }
        });

        /*cardNumTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "点击了" + bundle.getInt(INDEX_KEY, 0) + "", Toast.LENGTH_SHORT).show();
            }
        });*/
        return v;
    }
}