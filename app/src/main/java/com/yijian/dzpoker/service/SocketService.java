package com.yijian.dzpoker.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.icu.text.IDNA;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.Log;


import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.util.Util;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 * Created by rabby on 2017/10/9.
 */

public class SocketService extends Service implements Runnable {

    private Socket socket;
    private BufferedReader reader;//
    private PrintWriter writer;//
    //private Binder binder;

    private SocketBinder mBinder = new SocketBinder();

    public static final String TAG = "SocketService";
    private Callback callback;

    private Thread td;// 线程，获取服务器端发送来的消息

    private String workStatus;// 当前工作状况，null表示正在处理，success表示处理成功，failure表示处理失败
    private String currAction; //标记当前请求头信息，在获取服务器端反馈的数据后，进行验证，以免出现反馈信息和当前请求不一致问题。比如现在发送第二个请求，但服务器端此时才响应第一个请求
    private String  recMsg;
    private String serverIP;
    private int serverPort;

    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (socket!=null) {
                socket.close();
            }
        }catch (Exception e){

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        try {
            if (socket!=null) {
                socket.close();
            }
        }catch (Exception e){

        }
        return super.onUnbind(intent);
    }


    /**
     * 连接服务器
     */
    private void connectService() {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    recMsg="";
                    //recMsg="fdsfsdf$sfsfsfs$dffsdfds";//创建service的时候为空
                    socket = new Socket();
                    SocketAddress socAddress = new InetSocketAddress(serverIP,serverPort);
                    socket.connect(socAddress, 3000);

                    reader = new BufferedReader(new InputStreamReader(
                            socket.getInputStream(), "utf8"));

                    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream(), "utf8")), true);

                    td = new Thread(SocketService.this);// 启动线程
                    td.start();

                    callback.onScoketConnectSuccess();

                } catch (SocketException ex) {
                    callback.onSocketError();
                    return;
                } catch (SocketTimeoutException ex) {
                    callback.onSocketError();
                    return;
                } catch (Exception ex) {
                    callback.onSocketError();
                    return;
                }
            }
        });
        thread.start();

    }

    public void run() {
        try {
            while (true) {
                Thread.sleep(500);// 休眠0.5s
                if (socket != null && !socket.isClosed()) {// 如果socket没有被关闭
                    if (socket.isConnected()) {// 判断socket是否连接成功
                        if (!socket.isInputShutdown()) {
                            //读取服务器端返回的信息，以$开头
                            Boolean canRead=true;
                            while(canRead) {
                                int charLen=1024;
                                char[] cha = new char[charLen];
                                int len = reader.read(cha,0,charLen);//这里缓冲区设置成1024
                                if (len<=charLen && cha[len-1]=='}' )
                                {
                                    canRead=false;   //判断依据：最后一个是}，或者长度小于，丢包情况下，需要等下一个完整包
                                }
                                char[] recInfo=Arrays.copyOfRange(cha, 0, len);
                                Log.v("RABBY", "receivedata"+String.valueOf(recInfo));
                                recMsg += String.valueOf(recInfo);
                                Log.v("RABBY", "recMsg:"+recMsg);
                            }
                            //读完之后，解析字符串，调用回调函数

                            if (recMsg.length()>1){
                                if (recMsg.indexOf("$")==-1){
                                    //这个串是无用的串，抛弃
                                    recMsg="";
                                    Log.v("RABBY", "recMsg:"+recMsg);
                                }else{
                                    //截取字符串，保证从$开始
//                                    recMsg=recMsg.substring(recMsg.indexOf("$"));
                                    Log.v("RABBY", "recMsg:"+recMsg);
                                    while(recMsg.indexOf("$",1)>0){
                                        String msgInfo=recMsg.substring(0,recMsg.indexOf("$",1));
                                        recMsg=recMsg.substring(recMsg.indexOf("$",1));
                                        callback.onReciveData(msgInfo);
                                        Log.v("RABBY", "Msg:"+msgInfo);
                                        Log.v("RABBY", "recMsg:"+recMsg);
                                    }
                                    Log.v("RABBY", "Msg:"+recMsg);
                                    Log.v("RABBY", "recMsg:"+recMsg);

                                    callback.onReciveData(recMsg);
                                    recMsg="";

//                                    if(recMsg.indexOf("$",1)==-1){
//                                        //只有一个，那么则认为是一条完整消息
//                                        String msg=recMsg;
//                                        recMsg="";
//                                        Log.v("RABBY", "Msg:"+msg);
//                                        Log.v("RABBY", "recMsg:"+recMsg);
//                                        callback.onReciveData(msg);
//                                    }else{
//                                        //截取字符串，只做一条消息处理
//                                        String msg=recMsg.substring(0,recMsg.indexOf("$",1)-1);
//                                        callback.onReciveData(msg);
//                                        recMsg=recMsg.substring(recMsg.indexOf("$",1));
//                                        Log.v("RABBY", "Msg:"+msg);
//                                        Log.v("RABBY", "recMsg:"+recMsg);
//                                        callback.onReciveData(msg);
//                                    }
                                }

                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {

            try {
                callback.onSocketError();
                socket.close();
            } catch (Exception e) {
                callback.onSocketError();
                e.printStackTrace();
            }

        }
    }

    /**
     * 向服务器发送数据信息
     *
     * @param msg
     */
    private void sendMessage(String msg) {
        if (!Util.isNetworkConnected(this))// 如果当前网络连接不可用,直接提示网络连接不可用，并退出执行。
        {
            callback.onSocketError();
            return;
        }
        if (socket == null)// 如果未连接到服务器，创建连接
            connectService();
        if (!SocketService.this.td.isAlive())// 如果当前线程没有处于存活状态，重启线程
            (td = new Thread(SocketService.this)).start();
//        if (!socket.isConnected() || (socket.isClosed())) // isConnected（）返回的是是否曾经连接过，isClosed()返回是否处于关闭状态，只有当isConnected（）返回true，isClosed（）返回false的时候，网络处于连接状态
//        {
//            Log.v("QLQ", "workStatus is not connected!111222");
//            for (int i = 0; i < 3 && workStatus == null; i++) {// 如果连接处于关闭状态，重试三次，如果连接正常了，跳出循环
//                socket = null;
//                connectService();
//                if (socket.isConnected() && (!socket.isClosed())) {
//                    Log.v("QLQ", "workStatus is not connected!11333");
//                    break;
//                }
//            }
//            if (!socket.isConnected() || (socket.isClosed()))// 如果此时连接还是不正常，提示错误，并跳出循环
//            {
//                workStatus = Constant.TAG_CONNECTFAILURE;
//                Log.v("QLQ", "workStatus is not connected!111444");
//                return;
//            }
//
//        }

        if (!socket.isOutputShutdown()) {// 输入输出流是否关闭
            try {
                writer.print(msg);
                writer.flush();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                callback.onSocketError();
            }
        } else {
            callback.onSocketError();
        }
    }


    public static <T> T[] concat(T[] first, T[] second) {
        T[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    public class SocketBinder extends Binder {

        public SocketService getService() {
            return SocketService.this;
        }

        public void connect(String IP,int port) {
           //连接到服务器，启动读的进程
            serverIP=IP;
            serverPort=port;
            connectService();



        }

        public void sendInfo(String msg){
            sendMessage(msg);
        }

    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static interface Callback {
         void onReciveData(String data);
         void onSocketError();//此方法是告诉外部socket异常，让外部进行处理
         void onScoketConnectSuccess();
     }

}
