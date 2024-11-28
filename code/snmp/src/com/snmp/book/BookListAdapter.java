package com.snmp.book;

import java.util.ArrayList;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.snmp.crypto.R;
import com.snmp.utils.Utils;

public class BookListAdapter extends BaseAdapter {
    private static final String TAG = "BookListAdapter";
    private ArrayList<BookItemData> mDataList = new ArrayList<BookItemData>();

    protected BookListAdapter(BookListView listView) {
    }

    public void clearData() {
        synchronized (mDataList) {
            mDataList.clear();
        }
    }

    public void setAllData(ArrayList<BookItemData> data) {
        synchronized (mDataList) {
            mDataList.clear();
            addPageData(data);
        }
    }

    public void addPageData(ArrayList<BookItemData> data) {
        synchronized (mDataList) {
            mDataList.addAll(data);
        }
    }

    public int getCount() {
        synchronized (mDataList) {
            return mDataList.size();
        }
    }

    public BookItemData getItem(int position) {
        if (position < 0 || position >= getCount()) {
            return null;
        }
        synchronized (mDataList) {
            return mDataList.get(position);
        }
    }

    public long getItemId(int position) {
        return position;
    }

    public ArrayList<BookItemData> getDataList() {
        synchronized (mDataList) {
            return mDataList;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = Utils.getInflater().inflate(R.layout.book_list_item,
                    null);
            holder = new ViewHolder();
            holder.mTitle = (TextView) convertView
                    .findViewById(R.id.book_list_item_title);
            holder.mIcon = (ImageView) convertView
                    .findViewById(R.id.book_list_item_img);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String name = mDataList.get(position).mName;
        holder.mTitle.setText(name.replace(".txt", ""));
        // holder.mIcon.setImageDrawable(mDataList[position].drawable);
        return convertView;
    }

    public static class ViewHolder {
        ImageView mIcon;
        TextView mTitle;
    }

}
