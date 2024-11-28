package com.snmp.book;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.snmp.utils.LogUtils;

public class BookFile {
    private static final String SNMP2_ZIP = "snmp2.zip";
    private static final String TAG = BookFile.class.getSimpleName();

    public static void CopyFile(Context context, boolean assetzip) {
        String path = context.getFilesDir().getAbsolutePath() + "/";
        String filename = path + SNMP2_ZIP;
        File file = new File(filename);

        FileOutputStream mOutput = null;
        InputStream in = null;
        FileInputStream inzip = null;
        try {
            mOutput = new FileOutputStream(file);
            if (assetzip) {
                AssetManager assetManager = context.getAssets();
                in = assetManager.open(SNMP2_ZIP);
            } else {
                File filezip = new File(BookConstant.BOOK_ZIP_FILE);
                in = new FileInputStream(filezip);
                LogUtils.i(TAG, "snmp file aaa000 " + filezip.exists() + in);
            }

            byte[] buf = new byte[1024];
            int n;

            while ((n = in.read(buf)) != -1) {
                mOutput.write(buf, 0, n);
            }

            mOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (mOutput != null) {
                    mOutput.close();
                }
                if (in != null) {
                    in.close();
                }
                if (inzip != null) {
                    inzip.close();
                }
            } catch (IOException e) {

            }
        }

        LogUtils.i(TAG, "snmp file aaa111");
        UnZip(path, filename);
    }

    public static void UnZip(String path, String zipFile) {
        try {
            BufferedInputStream fin = new BufferedInputStream(new FileInputStream(zipFile));
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            byte[] buf = new byte[1024];
            int n = 0;
            int len = 0;

            while ((ze = zin.getNextEntry()) != null) {
                LogUtils.i(TAG, "snmp file aaa111 " + ze + " " + ze.getName());
                if (ze.isDirectory()) {
                    _dirChecker(path, ze.getName());
                } else {
                    BufferedOutputStream fout = new BufferedOutputStream(new FileOutputStream(path + ze.getName()));

                    while ((n = zin.read(buf, 0, 1024)) > 0) {
                        fout.write(buf, 0, n);
                    }

                    zin.closeEntry();
                    fout.close();
                }
            }

            zin.close();
        } catch (Exception e) {
            Log.e(TAG, "unzip", e);
        }

    }

    private static void _dirChecker(String path, String dir) {
        File f = new File(path + dir);

        if (!f.isDirectory()) {
            f.mkdirs();
        } else {
            f.mkdir();
        }
    }

}
