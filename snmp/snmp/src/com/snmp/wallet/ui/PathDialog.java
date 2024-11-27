package com.snmp.wallet.ui;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.ListAdapter;

import com.snmp.crypto.R;
import com.snmp.wallet.BtcAccount;
import com.snmp.wallet.ui.PathListView.ItemData;
import com.snmp.wallet.ui.PathListView.PathListAdapter;

public class PathDialog {
    private static final String TAG = PathDialog.class.getSimpleName();
    private static ArrayList<ItemData> mDataList = new ArrayList<ItemData>();

    public static void showDialog(final Activity activity) {
        mDataList.clear();
        mDataList.add(getItem(0));
        mDataList.add(getItem(1));
        mDataList.add(getItem(2));
        mDataList.add(getItem(3));
        mDataList.add(getItem(4));
        mDataList.add(getBech32Item(0));
        mDataList.add(getEthItem());

        PathListView listView = new PathListView(activity);
        listView.init();
        listView.setBackgroundColor(activity.getResources().getColor(R.color.title_background));
        ListAdapter adapter = listView.getAdapter();
        ((PathListAdapter) adapter).refresh(mDataList);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("select main path");
        builder.setView(listView);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final Dialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
            }
        });

        dialog.show();
        ((PathListAdapter) adapter).refresh(mDataList);
        listView.setDialog(dialog);
    }

    private static ItemData getItem(int index) {
        ItemData itemData = new ItemData();
        itemData.mPath = "m/44'/0'/0'/0/" + index;
        itemData.mName = BtcAccount.getInstance().getAddressByPath(itemData.mPath);
        return itemData;
    }

    private static ItemData getBech32Item(int index) {
        ItemData itemData = new ItemData();
        itemData.mPath = "m/84'/0'/0'/0/" + index;
        itemData.mName = BtcAccount.getInstance().getAddressByPath(itemData.mPath);
        return itemData;
    }

    private static ItemData getEthItem() {
        ItemData itemData = new ItemData();
        itemData.mPath = "m/44'/60'/0'/0/0";
        itemData.mName = BtcAccount.getInstance().getAddressByPath(itemData.mPath);
        return itemData;
    }
}
