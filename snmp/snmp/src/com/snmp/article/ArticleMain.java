package com.snmp.article;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.res.AssetManager;
import android.view.View;
import android.widget.ListAdapter;

import com.snmp.article.ArticleListView.ArticleListAdapter;
import com.snmp.crypto.CryptoFragment;
import com.snmp.crypto.R;
import com.snmp.utils.LogUtils;

public class ArticleMain extends CryptoFragment {
    private static final String TAG = "article";
    private static ArrayList<ArticleItemData> mSearchDataList = new ArrayList<ArticleItemData>();

    public ArticleMain(Activity activity) {
        super(activity);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public int onGetLayoutId() {
        return R.layout.crypto_article;
    }

    public void initView(View root) {
        loadAssets();

        ArticleListView articleListView = new ArticleListView(getActivity());
        articleListView.init();
        ListAdapter adapter = articleListView.getAdapter();
        ((ArticleListAdapter) adapter).refresh(mSearchDataList);
        addView(articleListView);
    }

    private void loadAssets() {
        mSearchDataList.clear();

        String[] listFile = null;
        AssetManager assets = getActivity().getAssets();
        try {
            listFile = assets.list("");
        } catch (IOException e) {
            e.printStackTrace();
            LogUtils.e(TAG, "asset no file");
        }
        LogUtils.i(TAG, "list article: " + listFile.length);

        for (int i = 0; i < listFile.length; i++) {
            String name = listFile[i];
            if (!name.contains(".")) {
                continue;
            }
            if (name.contains("000")) {
                continue;
            }
            ArticleItemData item = new ArticleItemData();
            item.mName = name;
            mSearchDataList.add(item);
            LogUtils.i(TAG, "list article: " + name);
        }
    }
}
