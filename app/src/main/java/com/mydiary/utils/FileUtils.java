package com.mydiary.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    public static String getFileNameFromPath(String fullFileName) {
        String res = fullFileName;
        if (res.contains("/")) {
            res = res.substring(res.lastIndexOf("/") + 1);
        }
        if (res.contains(".")) {
            res = res.substring(0, res.lastIndexOf("."));
        }
        return res;
    }

    public static File getPhotoFile(Context context, String fileName, String extension, boolean needCreate) {
        if (isExternalStorageAvailable()) {
            File dir = new File(context.getFilesDir(), "photo");
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    return null;
                }
            }
            File image = null;
            if (needCreate) {
                try {
                    image = File.createTempFile(
                            getFileNameFromPath(fileName),  /* prefix */
                            extension,         /* suffix */
                            dir      /* directory */
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                image = new File(dir + File.separator + getFileNameFromPath(fileName) + extension);
            }
            return image;
        } else {
            return null;
        }
    }

    public static String getAuthority(Context context) {
        return context.getApplicationContext().getPackageName().concat(".FileProvider");
    }

    public static Uri getPhotoFileUri(Context context, String fileName, String tag) {
        return getPhotoFileUri(context, fileName, tag, false);
    }

    public static Uri getPhotoFileUri(Context context, String fileName, String tag, boolean needCreate) {
        return getPhotoFileUri(context, fileName, tag, needCreate, false);
    }

    public static String tempFileName;

    public static Uri getPhotoFileUri(Context context, String fileName, String tag, boolean needCreate, boolean resultAsContent) {
        if (isExternalStorageAvailable()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                File file = getPhotoFile(context, fileName, ".jpg", needCreate);
                Uri result;
                if (needCreate && file != null) {
                    tempFileName = file.getAbsolutePath();
                }
                if (needCreate || resultAsContent) {
                    result = FileProvider.getUriForFile(context,
                            getAuthority(context),
                            file);
                } else {
                    result = Uri.parse(tempFileName);
                }
                return result;
            } else {
                File mediaStorageDir = new File(
                        context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), tag);

                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                    Log.d(tag, "failed to create directory");
                }

                if (fileName == null) {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    fileName = "JPEG_" + timeStamp + "_" + "jpg";
                }

                return Uri.fromFile(new File(mediaStorageDir.getPath() + File.separator + fileName));
            }
        }
        return null;
    }

    private static boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

}