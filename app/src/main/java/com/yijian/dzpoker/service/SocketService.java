package com.yijian.dzpoker.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.yijian.dzpoker.baselib.debug.Logger;
import com.yijian.dzpoker.util.Util;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rabby on 2017/10/9.
 */

public class SocketService extends Service implements Runnable {

    private static final String TAG = "SocketService";
    private Socket socket;
    private BufferedReader reader;//
    private PrintWriter writer;//
    //private Binder binder;

    private SocketBinder mBinder = new SocketBinder();
    private Callback callback;

    private Thread td;// 线程，获取服务器端发送来的消息

    private String workStatus;// 当前工作状况，null表示正在处理，success表示处理成功，failure表示处理失败
    private String currAction; //标记当前请求头信息，在获取服务器端反馈的数据后，进行验证，以免出现反馈信息和当前请求不一致问题。比如现在发送第二个请求，但服务器端此时才响应第一个请求
    private String recMsg;
    private String serverIP;
    private int serverPort;

    private String needToExecMsg = "";

    ExecutorService handleSocketMsgThreadExecutor;
    ExecutorService handleSingleMsgThreadExecutor;
    ExecutorService handleSendSocketMsgThreadExecutor;

    private static String partOfNextMsg = "";

    private boolean canRead = false;

    @Override
    public void onCreate() {
        super.onCreate();
        handleSocketMsgThreadExecutor = Executors.newCachedThreadPool();
        handleSingleMsgThreadExecutor = Executors.newSingleThreadExecutor();
        handleSendSocketMsgThreadExecutor = Executors.newCachedThreadPool();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {

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
            if (socket != null) {
                socket.close();
            }
            canRead = false;
        } catch (Exception e) {

        }
        return super.onUnbind(intent);
    }


    /**
     * 连接服务器
     */
    private void connectService() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    recMsg = "";
                    //recMsg="fdsfsdf$sfsfsfs$dffsdfds";//创建service的时候为空
                    socket = new Socket();
                    SocketAddress socAddress = new InetSocketAddress(serverIP, serverPort);
                    socket.connect(socAddress, 3000);

                    reader = new BufferedReader(new InputStreamReader(
                            socket.getInputStream(), "utf8"));

                    writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                            socket.getOutputStream(), "utf8")), true);

                    td = new Thread(SocketService.this);// 启动线程
                    td.start();

                    canRead = true;

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
                            //读取服务器端返回的信息，以$结尾

                            canRead = true;
                            while (canRead) {
                                int bufferSize = 1024;
                                char[] singleBuffer = new char[bufferSize];
                                int len = reader.read(singleBuffer, 0, bufferSize);
                                if (len <= bufferSize) {
                                    canRead = false;
                                }
                                char[] recInfo = Arrays.copyOfRange(singleBuffer, 0, len);
                                recMsg += String.valueOf(recInfo);
                                Logger.i(TAG, "receive msg recInfo : " + String.valueOf(recInfo));
                            }
                            Logger.i(TAG, "receive msg recMsg : " + recMsg);
                            needToExecMsg = recMsg;
                            recMsg = "";
                            handleSocketMsgThreadExecutor.execute(new handleReceivedSocketMsg(needToExecMsg));
                        }
                    }
                }
            }
        } catch (Exception ex) {

            Logger.e(TAG, "readLine Exception: " + ex.getMessage());
            try {
                callback.onSocketError();
                socket.close();
                canRead = false;
            } catch (Exception e) {
                callback.onSocketError();
                e.printStackTrace();
                canRead = false;
            }

        }
    }


    private class handleReceivedSocketMsg implements Runnable {

        private String msg;

        public handleReceivedSocketMsg(String s) {
            this.msg = s;
            Logger.i(TAG, "handleReceivedSocketMsg s : " + s);
        }

        @Override
        public void run() {

            if (TextUtils.isEmpty(msg)) {
                return;
            }
            try {
                String[] messages = msg.split("\\$");
                for (String str : messages) {
                    Logger.i(TAG, "handleReceivedSocketMsg : " + str);
                }
                //没有$符号，可能是某个消息的一部分
                if (messages.length == 0) {
                    partOfNextMsg += msg;
                } else {
                    for (String data : messages) {
                        if (TextUtils.isEmpty(partOfNextMsg)) {
                            handleSingleMsgThreadExecutor.execute(new ExecSingleMsg(data));
                        } else {
                            handleSingleMsgThreadExecutor.execute(new ExecSingleMsg(partOfNextMsg + data));
                            partOfNextMsg = "";
                        }
                    }
                }

            } catch (Throwable throwable) {
                Logger.e(TAG, "handleReceivedSocketMsg error : " + throwable.getMessage());
            }

        }
    }

    private class ExecSingleMsg implements Runnable {

        private String data;

        public ExecSingleMsg(String data) {
            this.data = data;
        }

        @Override
        public void run() {
            callback.onReciveData(data);
        }
    }

    /**
     * 向服务器发送数据信息
     *
     * @param msg
     */
    private void sendMessage(final String msg) {
        handleSendSocketMsgThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                sendMessageInternal(msg);
            }
        });

    }

    private void sendMessageInternal(String msg) {
        if (!Util.isNetworkConnected(this))// 如果当前网络连接不可用,直接提示网络连接不可用，并退出执行。
        {
            callback.onSocketError();
            return;
        }
        if (socket == null)// 如果未连接到服务器，创建连接
            connectService();
        if (!SocketService.this.td.isAlive())// 如果当前线程没有处于存活状态，重启线程
            (td = new Thread(SocketService.this)).start();

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

        public void connect(String IP, int port) {
            //连接到服务器，启动读的进程
            serverIP = IP;
            serverPort = port;
            connectService();


        }

        public void sendInfo(String msg) {
            Logger.i(TAG, "sendInfo msg : " + msg);
            sendMessage(msg);
        }

    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onReciveData(String data);

        void onSocketError();//此方法是告诉外部socket异常，让外部进行处理

        void onScoketConnectSuccess();
    }

}
