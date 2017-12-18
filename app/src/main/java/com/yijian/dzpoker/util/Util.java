package com.yijian.dzpoker.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.yijian.dzpoker.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by rabby on 2017/8/9.
 */
public class Util {
    public static byte[] readStream(InputStream inputStream) throws Exception {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            bout.write(buffer, 0, len);
        }
        bout.close();
        inputStream.close();

        return bout.toByteArray();
    }

    public static File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        try {
            File image = File.createTempFile(imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    Environment.getExternalStorageDirectory()      /* directory */);
            return image;
        } catch (IOException e) {
            //do noting
            String ss=e.getMessage();
            return null;
        }
    }

    //获得IP
    public static String getLocalIpAddress() {

        try {

            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();

                 en.hasMoreElements();) {

                NetworkInterface intf = en.nextElement();

                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses();

                     enumIpAddr.hasMoreElements();) {

                    InetAddress inetAddress = enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress()) {

                        return inetAddress.getHostAddress().toString();

                    }
                }
            }

        } catch (SocketException ex) {
        }

        return null;

    }

    //判断是否网络连接
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int getConnectedType(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null && mNetworkInfo.isAvailable()) {
                return mNetworkInfo.getType();
            }
        }
        return -1;
    }

    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static Drawable loadImageFromNetwork(String imageUrl)
    {
        Drawable drawable = null;
        try {
            // 可以在这里通过文件名来判断，是否本地有此图片
            drawable = Drawable.createFromStream(
                    new URL(imageUrl).openStream(), "image.jpg");
        } catch (IOException e) {
            Log.d("test", e.getMessage());
        }
        if (drawable == null) {
            Log.d("test", "null drawable");
        } else {
            Log.d("test", "not null drawable");
        }

        return drawable ;
    }

    public static int[] calculatePopWindowPos(final View anchorView, final View contentView) {
        final int windowPos[] = new int[2];
        final int anchorLoc[] = new int[2];
        // 获取锚点View在屏幕上的左上角坐标位置
        anchorView.getLocationOnScreen(anchorLoc);
        final int anchorHeight = anchorView.getHeight();
        // 获取屏幕的高宽
        final int screenHeight = getScreenHeight(anchorView.getContext());
        final int screenWidth = getScreenWidth(anchorView.getContext());
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        // 计算contentView的高宽
        final int windowHeight = contentView.getMeasuredHeight();
        final int windowWidth = contentView.getMeasuredWidth();
        // 判断需要向上弹出还是向下弹出显示
        final boolean isNeedShowUp = (screenHeight - anchorLoc[1] - anchorHeight < windowHeight);
        if (isNeedShowUp) {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] - windowHeight;
        } else {
            windowPos[0] = screenWidth - windowWidth;
            windowPos[1] = anchorLoc[1] + anchorHeight;
        }
        return windowPos;
    }

    /**
     * 获取屏幕高度(px)
     */
    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }
    /**
     * 获取屏幕宽度(px)
     */
    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


    /**
     * 解析服务器返回的日期, 得到如下格式日期: xx月xx日
     */
    public static String getFormatDate(Context context, String timestamp) {

        String[] list = timestamp.split("T");
        if ( list.length == 2){
            String date = list[0].trim();
            String[] dates = date.split("-");
            return context.getResources().getString(R.string.date_year_month, dates[1],dates[2]);
        } else {
            return "";
        }
    }

    /**
     * 解析服务器返回的时间, 得到如下格式时间: xx:xx
     */
    public static String getFormatTime(Context context, String timestamp) {

        String[] list = timestamp.split("T");
        if ( list.length == 2){
            String time = list[1].trim();
            String[] times = time.split(":");
            return times[0] + ":" + times[1];
        } else {
            return "";
        }
    }


    /**
     * 解析服务器返回的时间, 得到如下格式时间: xx月xx日 xx:xx
     *
     */
    public static String getFormatDateTime(Context context, String timestamp) {

        StringBuilder result = new StringBuilder();
        result.append(getFormatDate(context,timestamp))
                .append(" ")
                .append(getFormatTime(context,timestamp));

        return  result.toString();
    }

    /**
     * 获取两个时间戳的时间差
     */
    public static String getTimeDuration(String startTime, String endTime){
        StringBuilder result = new StringBuilder();

        SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//如2016-08-10 20:40
        startTime = startTime.replace("T"," ");
        endTime = endTime.replace("T"," ");
        try {
            java.util.Date begin=simpleFormat.parse(startTime);
            java.util.Date end = simpleFormat.parse(endTime);

            long between=(end.getTime()-begin.getTime())/1000;//除以1000是为了转换成秒

            long day=between/(24*3600);
            long hour=between%(24*3600)/3600;
            long minute=between%3600/60;
            long second=between%60/60;

            if ( day > 0) result.append(day).append("天");
            if ( hour > 0) result.append(hour).append("小时");
            if ( minute > 0) result.append(minute).append("分钟");
            if ( second > 0) result.append(second).append("秒");

        } catch (Exception e){

        }
        return result.toString();
    }
}
