package com.yijian.dzpoker.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.view.data.MainPageNews;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by c_huangl on 0006, 11/06/2017.
 */

public class NewsPageUtils {

    private static final String TAG = "NewsPageUtils";
    private static final String NEWS_CACHE_FOLDER = "/news";

    //内存缓存大小
    private static final int MEMO_CACHE_SIZE=((int)(Runtime.getRuntime().maxMemory()/1024));
    //图片内存缓存
    private static LruCache<String,Bitmap> mMemoryPicCache;

    private static volatile ArrayList<MainPageNews> mNewsList=new ArrayList<MainPageNews>();

    static {
        mMemoryPicCache=new LruCache<String, Bitmap>(MEMO_CACHE_SIZE){
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount()/1024;
            };
        };
    }

    public static ArrayList<MainPageNews> getNewsList(){
        return mNewsList;
    }

    /**
     * 获取首页新闻数据
     * @param context
     *
     * 有请求服务器操作, 需要在子线程中调用
     */
    public static void prepareNewsData(final Context context){
        mNewsList.clear();

        mNewsList = getNewsFromServer(context);

        //缓存图片和网页
        for( MainPageNews news : mNewsList){
            cachedPic(news, context);
        }
    }

    private static ArrayList<MainPageNews>  getNewsFromServer(Context context){
        ArrayList<MainPageNews> newsList = new ArrayList<MainPageNews>();

        String strURL = context.getString(R.string.url_remote);
        strURL+="func=getmainpagenews";

        try{

            URL url = new URL(strURL);
            Request request = new Request.Builder().url(strURL).build();
            Response response = DzApplication.getHttpClient().newCall(request).execute();

            String result=response.body().string();
            JSONArray jsonNewList=new JSONArray(result);

            Log.d(TAG, "Query successful ? " + response.isSuccessful());
            Log.d(TAG, " ----  " + jsonNewList.toString());

            for(int i=0;i<jsonNewList.length();i++){
                MainPageNews news = new MainPageNews();
                JSONObject jsonObject=new JSONObject(jsonNewList.get(i).toString());

                news.title=jsonObject.getString("title");
                news.link=jsonObject.getString("link");
                news.pic= jsonObject.getString("pic");
                news.isvalid=jsonObject.getInt("isvalid");
                news.createtime=jsonObject.getString("createtime");
                news.picName = getConvertedSimpleName(news.pic);
                news.linkName = getConvertedSimpleName(news.link);

                newsList.add(news);
            }
        } catch (Exception e){
            Log.d(TAG, "getNewsFromServer: " + e.getMessage());
        }

        return newsList;
    }

    private static Bitmap cachedPic(MainPageNews news, Context context){

        Bitmap map = null;
        String url = news.pic;
        if ( url == null || url.isEmpty()) return map;

        //获取网络图片
        map = getImageFromNet(url);

        String picName = news.picName;
        //缓存图片到本地
        if ( map != null ){

            String cachePath = context.getCacheDir().getPath();
            Log.d(TAG, "cachedPath: " + cachePath);
            String newsCachePath = cachePath + NEWS_CACHE_FOLDER;
            Log.d(TAG, "newsCachePath: " + newsCachePath);

            File file = new File(newsCachePath);
            if ( !file.exists()) {
                file.mkdir();
            }

            String filePath = newsCachePath + "/" + picName;
            Log.d(TAG, "filePath: " + filePath);
            saveBitmapTofile(map, filePath);
        }

        //添加到内存缓存
        addBitmapToMemory(picName, map);
        return map;
    }

    private static boolean saveBitmapTofile(Bitmap bmp, String filename) {
        if (bmp == null || filename == null)
            return false;

        Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
        int quality = 100;
        OutputStream stream = null;
        try {
            File file = new File(filename);
            file.createNewFile();
            stream = new FileOutputStream(filename, false);
            Log.d(TAG, "Save cached file successfully : " + filename);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmp.compress(format, quality, stream);
    }

    private static void addBitmapToMemory(String key,Bitmap bitmap){
        if(getBitmapFromMemory(key)==null){
            mMemoryPicCache.put(key, bitmap);
            Log.d(TAG, "add memory cache successful : " + key);
        }
    }

    private static  Bitmap getBitmapFromMemory(String key){
        return mMemoryPicCache.get(key);
    }

    private static Bitmap getImageFromNet(String imagePath){

        Bitmap pic = null;
        //Bitmap pic = BitmapFactory.decodeResource(getActivity().getResources(),R.drawable.background);

        if ( imagePath == null || imagePath.isEmpty()) return pic;

        try {
            URL url=new URL(imagePath);
            HttpURLConnection con=(HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(10*1000);
            InputStream is=con.getInputStream();
            //把流转换为bitmap
            pic = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return pic;
    }

    /**
     * 获取某个服务器上的指定图片
     * @param news
     * @param context
     * @return
     *
     * 有请求服务器操作, 需要在子线程中调用
     */
    public static Bitmap getBitmap(MainPageNews news, Context context){
        Bitmap bitmap = null;

        String picName = news.picName;

        //内存缓存中查找
        bitmap = getBitmapFromMemory(picName);
        if ( bitmap != null ) return bitmap;

        //本地缓存中查找
        bitmap = getBitmapFromFile(picName,context);
        //加入内存缓存
        addBitmapToMemory(picName,bitmap);
        if ( bitmap != null ) return bitmap;

        //都不存在, 去网络获取, 并添加入本地和内存缓存
        //bitmap = cachedPic(news, context);
        return bitmap;
    }

    private static String getConvertedSimpleName(String url){
        String result = "";
        String[] items = url.split("/");
        result = items[items.length-1];
        return result;
    }

    private static Bitmap getBitmapFromFile(String url, Context context){
        Bitmap map = null;

        String cachePath = context.getCacheDir().getPath();
        Log.d(TAG, "cachedPath: " + cachePath);
        String newsCachePath = cachePath + NEWS_CACHE_FOLDER;
        Log.d(TAG, "newsCachePath: " + newsCachePath);
        String filePath = newsCachePath + "/" + url;
        Log.d(TAG, "filePath: " + filePath);

        map = BitmapFactory.decodeFile(filePath);
        return map;
    }
}
