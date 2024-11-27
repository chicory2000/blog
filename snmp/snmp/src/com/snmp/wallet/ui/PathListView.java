package com.snmp.wallet.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.snmp.crypto.R;
import com.snmp.utils.PreferenceManager;
import com.snmp.wallet.BtcAccount;

public class PathListView extends ListView {
    protected PathListAdapter mAdapter;
    private Dialog mDialog;

    public PathListView(Context context) {
        super(context);
    }

    public PathListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PathListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void init() {
        mAdapter = new PathListAdapter(null);
        setAdapter(mAdapter);
        setOnItemClickListener(mItemClickListener);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> av, View view, int position, long id) {
            PathListView.this.onItemClick(view, id);
        }
    };

    protected void onItemClick(View view, long id) {
        int realPosition = (int) id;
        ItemData item = (ItemData) mAdapter.getItem(realPosition);
        PreferenceManager.putString("wallet_path", item.mPath);
        BtcAccount.getInstance().reinitWallet();
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void setDialog(Dialog dialog) {
        mDialog = dialog;
    }

    @Override
    @ExportedProperty(category = "drawing")
    public boolean hasOverlappingRendering() {
        return false;
    }

    public class PathListAdapter extends BaseAdapter {
        private ArrayList<ItemData> mDataList = new ArrayList<ItemData>();

        public PathListAdapter(ArrayList<ItemData> appData) {
            mDataList = appData;
        }

        public void refresh(ArrayList<ItemData> data) {
            mDataList = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            if (mDataList != null) {
                return mDataList.size();
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int index) {
            if (mDataList != null) {
                return mDataList.get(index);
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int index) {
            return index;
        }

        @Override
        public View getView(int position, View covertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (covertView == null) {
                covertView = LayoutInflater.from(getContext()).inflate(R.layout.crypto_watch_list_item, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.mIcon = (ImageView) covertView.findViewById(R.id.watch_list_item_img);
                viewHolder.mTitle = (TextView) covertView.findViewById(R.id.watch_list_item_title);
                viewHolder.mDes = (TextView) covertView.findViewById(R.id.watch_list_item_des);
                covertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) covertView.getTag();
            }

            viewHolder.mTitle.setText(mDataList.get(position).mName);

            viewHolder.mDes.setText(mDataList.get(position).mPath);
            return covertView;
        }

        public ArrayList<ItemData> getDataList() {
            return mDataList;
        }
    }

    private class ViewHolder {
        ImageView mIcon;
        TextView mTitle;
        TextView mDes;
    }

    public static class ItemData {
        public String mPath = "";
        public String mType = "";
        public String mName = "";
        public String mSummary = "";
        public String mContentUrl = "";
        public String mOrigin = "";

    }

}
