package com.mydiary.utils;

import android.content.res.Resources;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Converters {
    public static int convertDpToPixels(int dp) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static List<Uri> convertStringToUri(String stringUri) {
        if (stringUri == null) {
            return new ArrayList<>();
        } else {
            List<Uri> uriList = new ArrayList<>();
            for (String string : stringUri.split(",")) {
                uriList.add(Uri.parse(string));
            }
            return uriList;
        }

    }

    public static String convertUriToString(List<Uri> uriList) {
        if (uriList == null || uriList.size() == 0) {
            return null;
        } else {
            List<String> stringList = new ArrayList<>();
            for (Uri uri : uriList) {
                stringList.add(uri.toString());
            }
            StringBuilder out = new StringBuilder();
            for (Object o : stringList)
            {
                out.append(o.toString());
                out.append(",");
            }
            return out.toString();
        }

    }
}
