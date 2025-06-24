package com.snmp.book;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ListAdapter;

import com.snmp.book.SearchListView.SearchListAdapter;
import com.snmp.crypto.R;
import com.snmp.utils.LogUtils;
import com.snmp.utils.Utils;

public class SearchDialog {
    private static final String TAG = "SearchDialog";
    private static String mLastKeyworkd = "";
    private static String mSearchResult = "";
    private static ArrayList<BookItemData> mSearchDataList = new ArrayList<BookItemData>();
    private static String name2;

    public static void showSearchDialog(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        name2 = "snmp";
        builder.setTitle("please input keyword");
        final EditText editText = new EditText(activity);
        editText.setText(mLastKeyworkd);
        builder.setView(editText);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String result = editText.getText().toString();
                if ("1983".equals(result)) {
                    PreferenceManager.putLong("last_enter_time", System.currentTimeMillis());
                    Intent intent = new Intent();
                    intent.setClassName(SnmpApplication.getInstance().getPackageName(),
                            WatchActivity.class.getName());
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    SnmpApplication.getInstance().startActivity(intent);
                    return;
                }
                if ("2000".equals(result)) {
                    Intent intent = new Intent();
                    intent.setClassName(SnmpApplication.getInstance().getPackageName(),
                            CryptoHomeActivity.class.getName());
                    intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                    SnmpApplication.getInstance().startActivity(intent);
                    return;
                }
                if (!TextUtils.isEmpty(result)) {
                    mSearchResult = "";
                    mLastKeyworkd = result;
                    search(activity, result);
                } else {
                    Utils.toast("null input");
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
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
    }

    public static void showSearchResultDialog(final Activity activity) {
        SearchListView searchListView = new SearchListView(activity);
        searchListView.init();
        searchListView.setBackgroundColor(activity.getResources().getColor(R.color.title_background));
        ListAdapter adapter = searchListView.getAdapter();
        ((SearchListAdapter) adapter).refresh(mSearchDataList);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("search result list");
        // final TextView result = new TextView(activity);
        // result.setText(mSearchResult);
        // result.setTextColor(Color.WHITE);
        builder.setView(searchListView);

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
    }

    public static void search(final Activity activity, String keyword) {
        mSearchDataList.clear();

        File file = new File(BookConstant.BOOK_PATH);
        File[] files = file.listFiles();
        if (files == null) {
            LogUtils.e(TAG, BookConstant.BOOK_PATH + " root no file");
            return;
        }

        for (int i = 0; i < files.length; i++) {
            String value = files[i].getName();
            LogUtils.i(TAG, "snmp file " + value);
            searchSub(value, keyword);
        }

        showSearchResultDialog(activity);
    }

    private static void searchSub(String type, String keyword) {
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

        for (int i = 0; i < dataList.size(); i++) {
            String value = dataList.get(i);
            searchSubData(type, value, keyword);
        }
    }

    private static void searchSubData(String type, String name, String keyword) {
        File file = new File(BookConstant.BOOK_PATH + "/" + type + "/" + name);
        if (!file.exists()) {
            Utils.toast("file error");
            return;
        }

        FileInputStream fin = null;
        try {
            int length = (int) file.length();
            byte[] buff = new byte[length];
            fin = new FileInputStream(file);
            fin.read(buff);
            fin.close();
            String content = new String(buff, "GBK");
            if (BookConstant.SNMP_AES) {
                String password = BookAESCipher.getPassword() + name2;
                try {
                    content = BookAESCipher.decrypt(password, content);
                    // LogUtils.i(TAG, "snmp file aaa111 " + mContent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (content.contains(keyword)) {
                LogUtils.i(TAG, "contains file " + name);
                mSearchResult += type + "/" + name + "\n";

                BookItemData item = new BookItemData();
                item.mType = type;
                item.mName = name;
                mSearchDataList.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
