package com.vandine.ocrtencentapplication;

import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenUtils {

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 获取屏幕高度
     */
    public static DisplayMetrics getDisplayMetricsHeight(final Context pContext) {
        return pContext.getResources().getDisplayMetrics();
    }

    public static int getDisplayWidthPixelsHeight(final Context pContext) {
        return ScreenUtils.getDisplayMetricsHeight(pContext).widthPixels;
    }

    public static int getDisplayHeightPixelsHeight(final Context pContext) {
        return ScreenUtils.getDisplayMetricsHeight(pContext).heightPixels;
    }

    public static float getDisplayXDpiHeight(final Context pContext) {
        return ScreenUtils.getDisplayMetricsHeight(pContext).xdpi;
    }

    public static float getDisplayYDpiHeight(final Context pContext) {
        return ScreenUtils.getDisplayMetricsHeight(pContext).ydpi;
    }

    public static float getDisplayDensityHeight(final Context pContext) {
        return ScreenUtils.getDisplayMetricsHeight(pContext).density;
    }

    public static int dipToPxHeight(final Context pContext, final int pDip) {
        return (int) (pDip * ScreenUtils.getDisplayDensityHeight(pContext) + 0.5f);
    }

    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * 获取屏幕宽度
     */
    public static DisplayMetrics getDisplayMetricsWidth(final Context pContext) {
        return pContext.getResources().getDisplayMetrics();
    }

    public static int getDisplayWidthPixelsWidth(final Context pContext) {
        return ScreenUtils.getDisplayMetricsWidth(pContext).widthPixels;
    }

    public static int getDisplayHeightPixelsWidth(final Context pContext) {
        return ScreenUtils.getDisplayMetricsWidth(pContext).heightPixels;
    }

    public static float getDisplayXDpiWidth(final Context pContext) {
        return ScreenUtils.getDisplayMetricsWidth(pContext).xdpi;
    }

    public static float getDisplayYDpiWidth(final Context pContext) {
        return ScreenUtils.getDisplayMetricsWidth(pContext).ydpi;
    }

    public static float getDisplayDensityWidth(final Context pContext) {
        return ScreenUtils.getDisplayMetricsWidth(pContext).density;
    }

    public static int dipToPx(final Context pContext, final int pDip) {
        return (int) (pDip * ScreenUtils.getDisplayDensityWidth(pContext) + 0.5f);
    }


}