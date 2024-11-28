package com.snmp.book;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.snmp.utils.SnmpApplication;

public class SearchListView extends ListView {
	protected SearchListAdapter mAdapter;

	public SearchListView(Context context) {
		super(context);
	}

	public SearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SearchListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public void init() {
		mAdapter = new SearchListAdapter(null);
		setAdapter(mAdapter);
		setOnItemClickListener(mItemClickListener);
	}

	private AdapterView.OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> av, View view, int position,
				long id) {
			SearchListView.this.onItemClick(view, id);
		}
	};

	protected void onItemClick(View view, long id) {
		int realPosition = (int) id;
		BookItemData item = (BookItemData) mAdapter.getItem(realPosition);
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

	@Override
	@ExportedProperty(category = "drawing")
	public boolean hasOverlappingRendering() {
		return false;
	}

	public class SearchListAdapter extends BaseAdapter {
		private ArrayList<BookItemData> mDataList = new ArrayList<BookItemData>();

		public SearchListAdapter(ArrayList<BookItemData> appData) {
			mDataList = appData;
		}

		public void refresh(ArrayList<BookItemData> data) {
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
				covertView = LayoutInflater.from(getContext()).inflate(
						R.layout.book_list_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mIcon = (ImageView) covertView
						.findViewById(R.id.book_list_item_img);
				viewHolder.mTitle = (TextView) covertView
						.findViewById(R.id.book_list_item_title);
				covertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) covertView.getTag();
			}

			String itemTxt = mDataList.get(position).mType + "/"
					+ mDataList.get(position).mName;
			viewHolder.mTitle.setText(itemTxt);
			return covertView;
		}

		public ArrayList<BookItemData> getDataList() {
			return mDataList;
		}
	}

	private class ViewHolder {
		ImageView mIcon;
		TextView mTitle;
	}
}
