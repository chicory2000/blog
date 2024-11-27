package com.snmp.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.os.Environment;

import com.snmp.book.BookAESCipher;
import com.snmp.book.BookConstant;

public class BookEncrypt {
    private static final String TAG = BookEncrypt.class.getSimpleName();
    public static final String BOOK_PATH = Environment.getExternalStorageDirectory() + "/apk/snmp/";
    public static final String BOOK_PATH2 = Environment.getExternalStorageDirectory() + "/apk/snmp2/";
    private static String mContent;

    public static void initFile() {
        File file = new File(BookConstant.BOOK_PATH);
        File[] files = file.listFiles();
        if (files == null) {
            LogUtils.e(TAG, BookConstant.BOOK_PATH + " root no file");
            return;
        }

        for (int i = 0; i < files.length; i++) {
            String value = files[i].getName();
            LogUtils.i(TAG, "snmp file " + value);
            initSubFile(value);
        }
    }

    public static void initSubFile(String type) {
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

        for (int i = 0; i < dataList.size(); i++) {
            String value = dataList.get(i);
            LogUtils.i(TAG, "list file " + value);
            initData(type, value);
        }

    }

    public static void initData(String type, String name) {
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
            mContent = new String(buff, "GBK");
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

        LogUtils.i(TAG, "list fileaaaaa1 " + mContent.length());
        String password = BookAESCipher.getPassword() + "snmp";

        try {
            mContent = BookAESCipher.encrypt(password, mContent);
            //LogUtils.i(TAG, "snmp file aaa111 " + string);
            //string = BookAESCipher.decrypt(password, string);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.i(TAG, "list fileaaaaa2 " + mContent.length());
        writeFile(type, name);
        // mTextView.setText(mContent);
    }

    public static void writeFile(String type, String name) {
        File file = new File(BOOK_PATH2 + "/" + type + "/" + name);
        FileOutputStream mOutput = null;
        try {
            mOutput = new FileOutputStream(file);
            byte[] buff = mContent.getBytes();
            int length = buff.length;
            byte[] data = new byte[length + 100];

            mOutput.write(buff, 0, length);

            mOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mOutput != null) {
                    mOutput.close();
                }
            } catch (IOException e) {

            }
        }
    }

}
