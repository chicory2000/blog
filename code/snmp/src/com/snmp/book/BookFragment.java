package com.snmp.book;

import java.io.File;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import org.json.JSONArray;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.snmp.crypto.R;
import com.snmp.utils.LogUtils;

public class BookFragment extends Fragment {
    private static final String TAG = "BookFragment";
    private ViewGroup mRoot;
    private String mType;
    private BookListView mListview;

    public void setTitle(String title) {
        mType = title;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View pageView = inflater.inflate(R.layout.book_home_page, null);
        return pageView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRoot = (ViewGroup) view.findViewById(R.id.book_home_page_root);
        mListview = (BookListView) mRoot.findViewById(R.id.book_listview);
        initSubFile(mType);
    }

    private void initSubFile(String type) {
        File file = new File(BookConstant.BOOK_PATH + "/" + type);
        File[] files = file.listFiles();
        if (files == null) {
            LogUtils.e(TAG, type + " dir no file");
            return;
        }

        ArrayList<String> dataList = new ArrayList<String>();
        for (int i = 0; i < files.length; i++) {
            String value = files[i].getName();
            LogUtils.i(TAG, "list file " + value);
            dataList.add(value);
        }
        Collections.sort(dataList, Collator.getInstance(Locale.CHINA));

        JSONArray jarray = new JSONArray();
        for (int i = 0; i < dataList.size(); i++) {
            String value = dataList.get(i);
            jarray.put(value);
            LogUtils.i(TAG, "list file " + value);
        }

        mListview.updateAll(type, jarray);
    }
}
