package com.snmp.watch;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
import com.snmp.crypto.CyptoMgr;
import com.snmp.utils.SnmpApplication;

public class WatchListView extends ListView {
	protected WatchListAdapter mAdapter;

	public WatchListView(Context context) {
		super(context);
	}

	public WatchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public WatchListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
	}

	public void init() {
		mAdapter = new WatchListAdapter(null);
		setAdapter(mAdapter);
		setOnItemClickListener(mItemClickListener);
	}

	private AdapterView.OnItemClickListener mItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> av, View view, int position,
				long id) {
			WatchListView.this.onItemClick(view, id);
		}
	};

	protected void onItemClick(View view, long id) {
		int realPosition = (int) id;
		WatchItemData item = (WatchItemData) mAdapter.getItem(realPosition);
		try {
			Intent intent = new Intent();
			// intent.setClassName(SnmpApplication.getInstance().getPackageName(),
			// BookDetailActivity.class.getName());
			//
			// Bundle bundle = new Bundle();
			// bundle.putString("type", item.mType);
			// bundle.putString("name", item.mName);
			// intent.putExtras(bundle);
			// intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);

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

	public class WatchListAdapter extends BaseAdapter {
		private ArrayList<WatchItemData> mDataList = new ArrayList<WatchItemData>();

		public WatchListAdapter(ArrayList<WatchItemData> appData) {
			mDataList = appData;
		}

		public void refresh(ArrayList<WatchItemData> data) {
			mDataList = (ArrayList<WatchItemData>)data.clone();
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
						R.layout.crypto_watch_list_item, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.mIcon = (ImageView) covertView
						.findViewById(R.id.watch_list_item_img);
				viewHolder.mTitle = (TextView) covertView
						.findViewById(R.id.watch_list_item_title);
				viewHolder.mDes = (TextView) covertView
						.findViewById(R.id.watch_list_item_des);
				covertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) covertView.getTag();
			}

			viewHolder.mTitle.setText(getItemAddress(mDataList.get(position)));

			String itemDesText = getItemDesText(mDataList.get(position));
			viewHolder.mDes.setText(itemDesText);
			return covertView;
		}

	}

	private String getItemDesText(WatchItemData watchItem) {
		if (!TextUtils.isEmpty(watchItem.mQueryResult)) {
			return watchItem.mQueryResult;
		}

		if (CyptoMgr.getInstance().isEncrypt()) {
		    if (watchItem.getBalance() < 10000000) {
		        return "null";
		    }
		    return "";
		}

		double total = (double) watchItem.getBalance() / 100000000;
		return Constant.decimalFormat.format(total) + "";
	}

	private String getItemAddress(WatchItemData watchItem) {
        if (CyptoMgr.getInstance().isEncrypt()) {
            return "" + Math.abs(watchItem.getAddress().hashCode());
        }
		String address = watchItem.getAddress();
		String result = address.substring(0,6) + "xxxxxxxxxxxxx" + address.substring(20);
        return result;
	}

	private class ViewHolder {
		ImageView mIcon;
		TextView mTitle;
		TextView mDes;
	}
}
