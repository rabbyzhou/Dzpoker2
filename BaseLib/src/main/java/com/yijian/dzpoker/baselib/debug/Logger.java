package com.yijian.dzpoker.baselib.debug;

import android.util.Log;

/** A easy log util. Wrapper for {@link Log}
 * @author QIPU
 * @date 2017/12/16.
 */
public class Logger {

    /**Log switch*/
    public static final boolean DEBUGABLE = true;

    /**Main log tag*/
    private static final String MAIN_TAG = "KINGLEORIC";

    private static final int LEVEL_I = 0;
    private static final int LEVEL_D = 1;
    private static final int LEVEL_V = 2;
    private static final int LEVEL_W = 3;
    private static final int LEVEL_E = 4;

    /**
     *  Wrapper for {@link Log} i;
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void i(Object tag, Object msg) {
        logInternal(tag, msg, LEVEL_I);
    }

    /**
     *  Wrapper for {@link Log} d;
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void d(Object tag, Object msg) {
        logInternal(tag, msg, LEVEL_D);
    }

    /**
     *  Wrapper for {@link Log} v;
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void v(Object tag, Object msg) {
        logInternal(tag, msg, LEVEL_V);
    }

    /**
     *  Wrapper for {@link Log} w;
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void w(Object tag, Object msg) {
        logInternal(tag, msg, LEVEL_W);
    }

    /**
     *  Wrapper for {@link Log} e;
     * @param tag Used to identify the source of a log message.  It usually identifies
     *            the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    public static void e(Object tag, Object msg) {
        logInternal(tag, msg, LEVEL_E);
    }

    private static void logInternal(Object tag, Object msg, int level) {
        if (!DEBUGABLE) {
            return;
        }
        if (null != tag && null != tag.getClass() && null != msg) {
            String subTag = tag instanceof String ? String.valueOf(tag) : tag.getClass().getSimpleName();
            String message = String.valueOf(msg);
            switch (level) {
                case LEVEL_I:
                    Log.i(MAIN_TAG, "[ " + subTag + " ] : " + message);
                    break;
                case LEVEL_D:
                    Log.d(MAIN_TAG, "[ " + subTag + " ] : " + message);
                    break;
                case LEVEL_V:
                    Log.v(MAIN_TAG, "[ " + subTag + " ] : " + message);
                    break;
                case LEVEL_W:
                    Log.w(MAIN_TAG, "[ " + subTag + " ] : " + message);
                    break;
                case LEVEL_E:
                    Log.e(MAIN_TAG, "[ " + subTag + " ] : " + message);
                    break;
                default:
                    break;
            }
        }
    }

}
