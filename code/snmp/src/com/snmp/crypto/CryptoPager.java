package com.snmp.crypto;

import java.util.ArrayList;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.snmp.article.ArticleMain;
import com.snmp.cold.ColdMain;
import com.snmp.crypto.R;
import com.snmp.generate.GenerateMain;
import com.snmp.utils.LogUtils;
import com.snmp.wallet.WalletMain;
import com.snmp.watch.WatchMain;

public class CryptoPager {
    public static final String TAG = CryptoPager.class.getSimpleName();
    private View mRootView;
    private Activity mActivity;
    private ViewPager mPager;
    private ArrayList<String> mTabTitles = new ArrayList<String>();
    private ArrayList<CryptoFragment> mPageViewList = new ArrayList<CryptoFragment>();
    private ArrayList<TextView> mTabTextList = new ArrayList<TextView>();
    private int mCurIndex = 0;
    private int mTabNum = 0;

    public CryptoPager(Activity activity, View rootView) {
        mActivity = activity;
        mRootView = rootView;
        initView();
    }

    private void initView() {
        mPager = (ViewPager) mRootView.findViewById(R.id.crypto_home_pager);

        initFragment();

        selectTab();
        initData();
    }

    private void initFragment() {
        // initTab(new WatchMain(mActivity));
        initTab(new ColdMain(mActivity));

        initTab(new WalletMain(mActivity));

        initTab(new GenerateMain(mActivity));

        if (!CyptoMgr.getInstance().isEncrypt()) {
            initTab(new ArticleMain(mActivity));
        }
    }

    public void setVisibility(boolean show, String reason) {
        mPager.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    protected void initData() {
        PagerAdapter adapter = new PagerAdapter() {

            @Override
            public int getCount() {
                return mPageViewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object o) {
                return view == o;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                CryptoFragment pageView = mPageViewList.get(position);
                ((ViewPager) container).addView(pageView, 0);
                return pageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                ((ViewPager) container).removeView((View) object);
            }

        };

        mPager.setAdapter(adapter);
        mPager.setCurrentItem(mCurIndex);
        mPager.setOnPageChangeListener(mOnPageChangedListener);
    }

    private void selectTab() {
        for (int i = 0; i < mTabNum; i++) {
            mTabTextList.get(i).setSelected(i == mCurIndex);
        }
    }

    private final OnPageChangeListener mOnPageChangedListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageSelected(int position) {
            mCurIndex = position;
            selectTab();
            LogUtils.d(TAG, "onPageSelected=" + position);
        }
    };

    private TextView getTabTxt(int index) {
        int tabId = R.id.crypto_tab_0 + index;
        View tabView = mRootView.findViewById(tabId);
        tabView.setVisibility(View.VISIBLE);
        TextView tabTxt = (TextView) (tabView.findViewById(R.id.crypto_tab_item_text));
        tabTxt.setTag(index);
        tabTxt.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                mCurIndex = (Integer) view.getTag();
                mPager.setCurrentItem(mCurIndex);
                selectTab();
            }
        });
        return tabTxt;
    }

    private void initTab(CryptoFragment item) {
        mTabTitles.add(item.getName());

        TextView tabTxt = getTabTxt(mTabNum);
        tabTxt.setText(item.getName());
        mTabTextList.add(tabTxt);

        mPageViewList.add(item);
        mTabNum++;
    }

}