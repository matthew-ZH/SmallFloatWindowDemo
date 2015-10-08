package com.example.smallfloatwindowdemo.app;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mattlyzheng on 2015/10/8.
 */
public class MyWindowManager {

    private static FloatWindowSmallView smallWindow;

    private static FloatWindowBigView bigWindow;

    private static WindowManager.LayoutParams smallWindowParams;

    private static WindowManager.LayoutParams bigWindowParams;

    private static WindowManager mWindowManager;

    private static ActivityManager mActivityManager;

    public static String getUsedPercentValue(Context context) {
        String dir = "/proc/meminfo";
        try{
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
            br.close();
            Log.d("zheng",subMemoryLine.replace("\\D+",""));
            Pattern p = Pattern.compile("\\D+");
            Matcher m = p.matcher(subMemoryLine);
            long totalMemorySize = Integer.parseInt(m.replaceAll("").trim());
            long availableSize = getAvailableMemory(context) /1024;
            int percent = (int)((totalMemorySize - availableSize)/(float) totalMemorySize * 100);
            return  percent +"%";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Ðü¸¡´°";
    }

    private static long getAvailableMemory(Context context) {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        getActivityManager(context).getMemoryInfo(mi);
        return mi.availMem;
    }

    private static ActivityManager getActivityManager(Context context) {
        if (mActivityManager == null) {
            mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        }
        return mActivityManager;
    }

    public static void createSmallWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (smallWindow == null) {
            smallWindow = new FloatWindowSmallView(context);
            if (smallWindowParams == null) {
                smallWindowParams = new WindowManager.LayoutParams();
                smallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.height = FloatWindowSmallView.viewHeight;
                smallWindowParams.width = FloatWindowSmallView.viewWidth;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight/2;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);
        }
    }

    public static void createBigWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (bigWindow == null) {
            bigWindow = new FloatWindowBigView(context);
            if (bigWindowParams == null) {
                bigWindowParams =  new WindowManager.LayoutParams();
                bigWindowParams.x = screenWidth /2 - FloatWindowBigView.viewWidth /2;
                bigWindowParams.y =  screenHeight/2 - FloatWindowBigView.viewHeight/2;
                bigWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                bigWindowParams.format = PixelFormat.RGBA_8888;
                bigWindowParams.gravity = Gravity.LEFT| Gravity.TOP;
                bigWindowParams.width = FloatWindowBigView.viewWidth;
                bigWindowParams.height = FloatWindowBigView.viewHeight;
            }
            windowManager.addView(bigWindow, bigWindowParams);
        }
    }

    public static boolean isWindowShowing() {
        return smallWindow != null ||bigWindow!= null;
    }

    public static void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }

    public static void removeBigWindow(Context context) {
        if (bigWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(bigWindow);
            bigWindow = null;
        }
    }

    public static void updateUsedPercent(Context context) {
        if (smallWindow != null) {
            TextView percentView = (TextView) smallWindow.findViewById(R.id.percent);
            percentView.setText(getUsedPercentValue(context));
        }
    }

    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return  mWindowManager;
    }

}
