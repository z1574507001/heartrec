package com.example.zero.memorandum;

import android.app.Application;

/**
 * Created by Developer on 2017/7/3.
 */

public class AppData extends Application {

    private static String imageFilePath = "sdcard/MyMemorandum/paint/";
    private static String recordFilePath = "sdcard/MyMemorandum/record/";
    private static int penColor = 0xff0000;//画笔颜色，默认红色
    private static int penSize = 9;//画笔尺寸

    public static String getImageFilePath() {
        return imageFilePath;
    }

    public static void setImageFilePath(String imageFilePath) {
        AppData.imageFilePath = imageFilePath;
    }

    public static String getRecordFilePath() {
        return recordFilePath;
    }

    public static void setRecordFilePath(String recordFilePath) {
        AppData.recordFilePath = recordFilePath;
    }

    public static int getPenColor() {
        return penColor;
    }

    public static void setPenColor(int penColor) {
        AppData.penColor = penColor;
    }

    public static int getPenSize() {
        return penSize;
    }

    public static void setPenSize(int penSize) {
        AppData.penSize = penSize;
    }
}