package com.snmp.book;

import android.os.Environment;

public class BookConstant {

    public static String BOOK_PATH = Environment
            .getExternalStorageDirectory() + "/apk/snmp/";
    public static String BOOK_ZIP_FILE = Environment
            .getExternalStorageDirectory() + "/apk/snmp2.zip/";
    public static boolean SNMP_AES = false;
    public static boolean SNMP_PROGUARD = false;

}

