package com.snmp.book;

import java.io.File;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.snmp.crypto.R;
import com.snmp.utils.LogUtils;
import com.snmp.utils.ToastUtils;
import com.snmp.wallet.pin.PinDialog;
import com.snmp.wallet.pin.PinMgr;

public class BookHomeActivity extends FragmentActivity {
    private static final String TAG = "BookHomeActivity";
    private ViewPager mPager;
    private String[] mTabTitles;
    private int[] mTabsImages;
    private ArrayList<BookFragment> mFragList = new ArrayList<BookFragment>();
    private ArrayList<TextView> mTabTextList = new ArrayList<TextView>();
    private int mCurIndex = 0;
    private int mTabNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // getWindow()
        // .addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // Window window = getWindow();
        // window.setStatusBarColor(getResources().getColor(R.color.title_color));
        // getWindow().setStatusBarColor(android.graphics.Color.RED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        initView();
    }

    private void initView() {
        setContentView(R.layout.book_home_activity);
        findViewById(R.id.book_home_title_search).setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                SearchDialog.showSearchDialog(BookHomeActivity.this);
                // AboutDialog.showAboutDialog(BookHomeActivity.this);
            }
        });
        mPager = (ViewPager) findViewById(R.id.book_home_pager);

        mTabTitles = getResources().getStringArray(R.array.book_home_frag_tab_title);
        mTabNum = mTabTitles.length;
        for (int i = 0; i < mTabNum; i++) {
            TextView tabTxt = getTabTxt(i);
            tabTxt.setText(mTabTitles[i]);
            mTabTextList.add(tabTxt);

            BookFragment pf = new BookFragment();
            pf.setTitle(mTabTitles[i]);
            mFragList.add(pf);
        }
        selectTab();
        initData();
        if (PinMgr.getInstance().isUnlockPinRequired()) {
            Runnable start = new Runnable() {
                @Override
                public void run() {
                    PinMgr.getInstance().setStartUpPinUnlocked(true);
                }
            };

            PinDialog _pinDialog = PinMgr.getInstance().runPinProtectedFunction(this, start, false);
        }
    }

    private TextView getTabTxt(int index) {
        int tabId = R.id.book_tab_0 + index;
        View tabView = findViewById(tabId);
        tabView.setVisibility(View.VISIBLE);
        TextView tabTxt = (TextView) (tabView.findViewById(R.id.book_tab_item_text));
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

    protected void initData() {
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return mFragList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mFragList.get(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTabTitles[position];
            }

            @Override
            public long getItemId(int position) {
                return mFragList.get(position).hashCode();
            }

            @SuppressLint("NewApi")
            public Drawable getPageImage(int position) {
                return getDrawable(mTabsImages[position]);
            }
        };

        mPager.setAdapter(adapter);
        mPager.setCurrentItem(mCurIndex);
        mPager.setOnPageChangeListener(mOnPageChangedListener);

        File filepath = new File(BookConstant.BOOK_PATH);
        File[] filespath = filepath.listFiles();
        if (filespath == null) {
            BookConstant.BOOK_PATH = getFilesDir().getAbsolutePath() + "/snmp2/";

            File filezip = new File(BookConstant.BOOK_ZIP_FILE);
            if (filezip != null && filezip.exists()) {
                LogUtils.e(TAG, BookConstant.BOOK_PATH + " file zip");

                BookFile.CopyFile(getApplication(), false);
            } else {
                LogUtils.e(TAG, BookConstant.BOOK_PATH + " file assets");

                BookConstant.SNMP_AES = true;
                BookFile.CopyFile(getApplication(), true);
            }
        }
        initFile();
    }

    private void initFile() {
        File file = new File(BookConstant.BOOK_PATH);
        File[] files = file.listFiles();
        if (files == null) {
            LogUtils.e(TAG, BookConstant.BOOK_PATH + " root no file");
            return;
        }

        for (int i = 0; i < files.length; i++) {
            String value = files[i].getName();
            LogUtils.i(TAG, "snmp file " + value);
        }
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
        }
    };
}
