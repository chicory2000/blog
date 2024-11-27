package com.snmp.book;

import java.util.ArrayList;

import org.json.JSONArray;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.snmp.utils.SnmpApplication;
import com.snmp.utils.ToastUtils;
import com.snmp.wallet.pin.PinMgr;

public class BookListView extends ListView {
    private static final String TAG = "BookListView";
    protected BookListAdapter mAdapter;
    protected String mUrl;

    protected boolean mOnFlingScroll = false;
    private ArrayList<OnScrollListener> mScrollListenerList = new ArrayList<OnScrollListener>();

    public BookListView(Context context) {
        super(context);
        init();
    }

    public BookListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BookListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        initSetting();
        initDataManager();
        initAdapter();
        setListView();
        initScrollListener();
        if (mAdapter == null) {
            throw new NullPointerException("mAdapter is null !");
        }
    }

    private void initSetting() {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                mOnFlingScroll = (scrollState == OnScrollListener.SCROLL_STATE_FLING);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
        });
    }

    protected void initAdapter() {
        mAdapter = new BookListAdapter(this);
    }

    protected void initDataManager() {
    }

    private void setListView() {
        setAdapter(mAdapter);
        setOnItemClickListener(mItemClickListener);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> av, View view, int position,
                long id) {
            BookListView.this.onItemClick(view, id);
        }
    };

    protected void onItemClick(View view, long id) {
        int realPosition = (int) id;
        BookItemData item = mAdapter.getItem(realPosition);
        try {
            Intent intent = new Intent();
            intent.setClassName(SnmpApplication.getInstance().getPackageName(),
            		BookDetailActivity.class.getName());

            Bundle bundle = new Bundle();
            bundle.putString("type", item.mType);
            bundle.putString("name", item.mName);
            intent.putExtras(bundle);
            intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);

            SnmpApplication.getInstance().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateList(ArrayList<BookItemData> list) {
        mAdapter.setAllData(list);
        requestLayout();
        mAdapter.notifyDataSetChanged();
    }

    public void updateAll(String type, JSONArray jarray) {
        ArrayList<BookItemData> list = new ArrayList<BookItemData>();
        for (int i = 0; i < jarray.length(); i++) {
            BookItemData data = new BookItemData();
            data.mType = type;
            data.mName = (String) jarray.optString(i);
            list.add(data);
        }
        mAdapter.setAllData(list);
        requestLayout();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        mScrollListenerList.add(listener);
    }

    private void initScrollListener() {
        super.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                for (OnScrollListener onScrollListener : mScrollListenerList) {
                    onScrollListener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
                for (OnScrollListener onScrollListener : mScrollListenerList) {
                    onScrollListener.onScroll(view, firstVisibleItem,
                            visibleItemCount, totalItemCount);
                }
            }
        });
    }

    public void clear() {
        mAdapter.clearData();
        notifyChange();
    }

    public void notifyChange() {
        mAdapter.notifyDataSetChanged();
    }

}
