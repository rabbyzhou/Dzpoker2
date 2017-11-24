package com.yijian.dzpoker.activity.game;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.club.ClubInfoActivity;
import com.yijian.dzpoker.activity.club.ClubInfoManageActivity;
import com.yijian.dzpoker.constant.Constant;
import com.yijian.dzpoker.service.SocketService;
import com.yijian.dzpoker.util.DisplayHelper;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.util.Util;
import com.yijian.dzpoker.view.CircleTransform;
import com.yijian.dzpoker.view.RangeSliderBar2;
import com.yijian.dzpoker.view.TextMoveLayout;
import com.yijian.dzpoker.view.adapter.ControlInApplyAdapter;
import com.yijian.dzpoker.view.adapter.SelectCityAdapter;
import com.yijian.dzpoker.view.data.ApplyInfo;
import com.yijian.dzpoker.view.data.CardInfo;
import com.yijian.dzpoker.view.data.City;
import com.yijian.dzpoker.view.data.GameUser;
import com.yijian.dzpoker.view.data.PlayerHole;
import com.yijian.dzpoker.view.data.TableInfo;
import com.yijian.dzpoker.view.data.TablePlayerInfo;
import com.yijian.dzpoker.view.data.TableSeatInfo;
import com.yijian.dzpoker.view.data.User;
import com.yijian.dzpoker.view.data.WaitAction;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.content.EventNotificationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.enums.ConversationType;
import cn.jpush.im.android.api.event.MessageEvent;
import cn.jpush.im.android.api.model.GroupInfo;
import cn.jpush.im.android.api.model.UserInfo;
import cn.jpush.im.android.api.options.MessageSendingOptions;
import cn.jpush.im.android.tasks.GetEventNotificationTaskMng;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Request;
import okhttp3.Response;

import static com.yijian.dzpoker.R.id.tv_service_coin;
import static com.yijian.dzpoker.R.id.view;
import static com.yijian.dzpoker.R.id.visible;
import static com.yijian.dzpoker.util.Util.calculatePopWindowPos;

public class GameActivity extends AppCompatActivity {

    private LinearLayout layout_parent;
    private PopupWindow popupWindow,popMenu,popApplyWindow;
    private SocketService.SocketBinder myBinder;
    private String ip;
    private int port;
    private int operation;
    private int gameId;
    private String gameHouseName;
    private boolean flag=false;//控制，初次连接的时候，进行业务操作
    private DzApplication application;
    private ImageView iv_voice,iv_info,iv_menu,iv_apply,iv_game_info;

//    private HashMap<Integer,User> mTableUser=new HashMap<Integer,User>();//记录进入牌桌的玩家，用户ID为key
//    private HashMap<Integer,Integer> mUserSeat=new HashMap<Integer,Integer>();//seatindex 与 user映射
    private HashMap<Integer,GameUser> mGameUser=new HashMap<Integer,GameUser>();// userid 为key

    private HashMap<Integer,View> mSeatObjects=new HashMap<Integer,View>();//本地座位对象映射
    private HashMap<Integer,View> mChipObjects=new HashMap<Integer,View>();//下注筹码对象映射
    private HashMap<Integer,View> mTipObjects=new HashMap<Integer,View>();//用户最后操作
    private HashMap<Integer,View> mCardBackObjects=new HashMap<Integer,View>();//用户有牌的时候的背景
    private HashMap<Integer,CountDownTimer> mTimerObjects=new HashMap<Integer,CountDownTimer>();//倒计时控件对象
    private HashMap<Integer,View> mTimerViewObjects=new HashMap<Integer,View>();//倒计时的显示
    private HashMap<Integer,View> mPoolViewObjects=new HashMap<Integer,View>();//倒计时的显示
    //private HashMap<Integer,Integer> mUserSeatObject=new HashMap<Integer,Integer>();//用户ID与座位index的映射，比如 1006 坐在1号位置
    private HashMap<Integer,ImageView> mGameCardsObjects=new HashMap<Integer,ImageView>();//公牌
    private HashMap<Integer,ImageView> mMyCardsObjects=new HashMap<Integer,ImageView>();//自己的牌
    private HashMap<Integer,View> mPoolObjects=new HashMap<Integer,View>();//底池
    private HashMap<Integer,TextView> mPoolOTextbjects=new HashMap<Integer,TextView>();//底池

    private  View mD;//庄家
    private View mMessage;//提示用户的信息
    private View mMyCards;//自己的牌，兼容奥马哈4张牌
    private View mShowCards;//赢牌之后的翻牌
    private View mGameCards;//公牌
    private TextMoveLayout textMoveLayout;
    private Button button_bottom,button_middle,button_top;//滑动下注的三个显示按钮，无事件操作
    private View mAction;
    private Button mButtonFold,mButtonRaise,mButtonCheck;
    private TextView mTVFold,mTVRaise,mTVCheck;


    private DisplayHelper displayHelper;

    private TableInfo mTableInfo;//这个是记录牌桌信息的
    private AbsoluteLayout layout_game;
    private int iSeatValue[][];//座位坐标，含用户名，头像，剩余记分牌
    private int iTipLocation[][];//Tip坐标，显示用户的最后一次操作
    private int iAmountChipLocation[][];//用户已下注筹码坐标
    private int iD[][];//庄家坐标
    private int iCardBack[][];//有牌的用户加上这个背景

    private Context mContext;
    private int mSeatViewWidth=120; //座位
    private int mSeatViewHeight=140;
    private int mNameTextHeight=30;
    private int mHeadImageHeight=80;
    private int mHeadImageWidth=80;
    private int mTipViewWidth=60;//Tip view的宽度
    private int mTipViewHeight=35;
    private int mAmountChipViewWidth=90;//用户下注 view的宽度
    private int mAmountChipVieHeight=35;
    private int mDViewWidth=40;  //显示庄家位的view
    private int mDViewHeight=40;
    private int mCardBackWidth=40;
    private int mCardBackHeight=38;
   // private  int mUserIndex=-1;//用户在游戏中的坐标
    private int mPoolWidth=320;
    private int mPoolHeight=300;

    private Button btnStartGame,btnReturnSeat;//开始座位，回到座位按钮
    private View  mReturnSeat;
    private int mScreenWidth,mScreenHeight;


    private int minChip=0;
    private int maxChip=1000;
    private int maxpaidchips=0;
    private int actionChip=0;//下注额
    private float downX,downY;
    private boolean isFirstBuyCore=false;//是否首次购买记分牌
    private int mWantSeatIndex;

    private List<ApplyInfo> mlistApplyInfo= new ArrayList<ApplyInfo>();
    private RecyclerView rv_apply_info;
    private LinearLayoutManager mLayoutManager ;
    private ControlInApplyAdapter mAdapter;


    //购买记分牌的弹出窗体
    /*
    tv_buy_coin购买金币
    iv_close  关闭
    tv_message 提示消息
    tv_core 带入记分牌
    seekbar_core  选择记分牌
    tv_coin   个人财富
    tv_service_coin 服务费
    tv_in_coin 已带入/总带入
    button_apply 申请*/
    private  TextView tv_buy_coin,tv_message,tv_core,tv_coin,tv_service_coin,tv_in_coin;
    private RangeSliderBar2 seekbar_core;
    private ImageView iv_close;
    private  Button button_apply;
    private int takeinchips;        //当前带入筹码
    private int permittakeinchips;  //当前应许带入筹码
    private int tablecreateuserid;

    private MediaPlayer mp;
    private int currentY;
    private CountDownTimer timer;

    private HashMap<Integer,PlayerHole> mPlayerHolebjects=new HashMap<Integer,PlayerHole>();//座位和底牌的映射关系，每一把牌局中的底牌记录

    private NotificationManager notificationManager;//状态栏通知

    private Boolean bInitView=false;





    //收到消息之后，进行UI更新
    private  final static int MESSAGE_START_GAME=0x1002;//开始


    private  final static int MESSAGE_INFO_TABLE=0x2001;//收到桌子信息
    private  final static int MESSAGE_INFO_SIT_SEAT=0x2002;//牌局有人坐下
    private  final static int MESSAGE_INFO_DOACTION=0x2003;//牌局操作通知
    private  final static int MESSAGE_INFO_START_TABLE=0x2004;//
    private  final static int MESSAGE_INFO_FLOP =0x2005;//
    private  final static int MESSAGE_INFO_TURN=0x2006;//
    private  final static int MESSAGE_INFO_RIVER=0x2007;//
    private  final static int MESSAGE_INFO_HOLE=0x2008;//底牌
    private  final static int MESSAGE_INFO_DO_POT=0x2009;//底池
    private  final static int MESSAGE_INFO_INIT_ROUND =0x2010;//底牌
    private  final static int MESSAGE_INFO_START_ROUND=0x2011;//底池
    private  final static int MESSAGE_INFO_WAIT_ACTION=0x2012;//等待操作
    private  final static int MESSAGE_INFO_SEE_NEXT_CARD=0x2013;//看牌
    private  final static int MESSAGE_INFO_SHOW_CARD=0x2014;//看赢家的牌
    private  final static int MESSAGE_INFO_LEAVE_SEAT=0x2015;//离开座位

    private  final static int MESSAGE_INFO_ADD_CHIPS=0x2016;//购买筹码
    private  final static int MESSAGE_INFO_DO_END=0x2017;//
    private  final static int MESSAGE_INFO_PAUSE_TABLE=0x2018;//
    private  final static int MESSAGE_INFO_LEAVE_TABLE=0x2019;//
    private  final static int MESSAGE_INFO_HOLD_SEAT=0x2020;//
    private  final static int MESSAGE_INFO_BACK_SEAT=0x2021;//
    private  final static int MESSAGE_INFO_DISPOSE_TABLE=0x2022;//
    private  final static int MESSAGE_INFO_TABLE_TIMEOUT=0x2023;//
    private  final static int MESSAGE_INFO_ALARM_TIMEOUT=0x2024;//
    private  final static int MESSAGE_INFO_WIN_27=0x2025;//
    private  final static int MESSAGE_INFO_DO_ANIMATION=0x2026;//
    private  final static int MESSAGE_INFO_INFO_BUY_SURANCE=0x2027;//







    private  final static int MESSAGE_DISMISS_POPWINDOW=0x9001;//去掉购买记分牌界面
    private  final static int MESSAGE_DISMISS_POPMENU=0x9002;//去掉菜单的弹出界面
    private  final static int MESSAGE_TAKEIN_INFO=0x9003;//从后台请求带入信息
    private  final static int MESSAGE_APPLY_INFO=0x9004;//更新带入申请菜单
    private  final static int MESSAGE_UPDATE_APPLY_INFO=0x9005;//更新带入申请菜单的列表数据






     /*//客户端向后台发的信息
    public static final String GAME_CREATE_TABLE = "createtable";
    public static final String GAME_START_TABLE = "starttablegame";
    public static final String GAME_PAUSE_TABLE = "pausetablegame";
    public static final String GAME_DISPOSE_TABLE = "disposetable";
    public static final String GAME_ENTER_TABLE = "entertable";
    public static final String GAME_SIT_SEAT = "sitseat";
    public static final String GAME_LEAVE_SEAT = "leaveseat";
    public static final String GAME_HOLD_SEAT = "holdseat";
    public static final String GAME_BACK_SEAT = "backseat";
    public static final String GAME_LEAVE_TABLE = "leavetable";
    public static final String GAME_DO_ACTION = "doaction";
    public static final String GAME_ADD_CHIPS = "addchips";
    public static final String GAME_SEE_NEXT_CARD = "seenextcard";
    public static final String GAME_SHOW_CARD = "showcard";
    public static final String GAME_DO_ANIMATION = "doanimation";
    public static final String GAME_BUY_SURANCE = "buysurance";

    //后台向客户端发送的信息
    public static final String RET_CREATE_TABLE = "createtableret";
    public static final String RET_START_GAME = "startgameret";
    public static final String RET_PAUSE_GAME = "pausegameret";
    public static final String RET_DISPOSE_TABLE = "disposetableret";
    public static final String RET_ENTER_TABLE = "entertableret";
    public static final String RET_SIT_TABLE = "sittableret";
    public static final String RET_LEAVE_SEAT = "leaveseatret";
    public static final String RET_HOLD_SEAT = "holdseatret";
    public static final String RET_BACK_SEAT = "backseatret";
    public static final String RET_LEAVE_TABLE = "leavetableret";
    public static final String RET_DO_ACTION= "doactionret";
    public static final String RET_ADD_CHIP = "addchipret";
    public static final String RET_SHOW_CARD = "showcardret";
    public static final String RET_SEE_NEXT_CARD = "seenextcardret";
    public static final String RET_DO_ANIMATION = "doanimationret";
    public static final String RET_BUY_SURANCE = "buysuranceret";


    public static final String INFO_TABLE_INFO = "inftableinfo";
    public static final String INFO_ACTION = "infaction";
    public static final String INFO_ADD_CHIPS = "infaddchips";
    public static final String INFO_WAIT_ACTION = "infwaitaction";
    public static final String INFO_INIT_ROUND = "infinitround";
    public static final String INFO_START_ROUND = "infstartround";
    public static final String INFO_DO_END = "infdoend";
    public static final String INFO_HOLE = "infhole";

    public static final String INFO_FLOP = "infflop";
    public static final String INFO_TURN = "infturn";
    public static final String INFO_RIVER = "infriver";
    public static final String INFO_START_TABLE = "infstarttablegame";
    public static final String INFO_PAUSE_TABLE = "infpausetablegame";
    public static final String INFO_ENTER_TABLE = "infentertable";
    public static final String INFO_LEAVE_TABLE = "infleavetable";
    public static final String INFO_SIT_SEAT = "infsitseat";
    public static final String INFO_HOLD_SEAT = "infholdseat";
    public static final String INFO_BACK_SEAT = "infbackseat";

    public static final String INFO_LEAVE_SEAT = "infleaveseat";
    public static final String INFO_DISPOSE_TABLE = "disposetable";
    public static final String INFO_TABLE_TIMEOUT = "inftabletimeout";
    public static final String INFO_ALARM_TIMEOUT = "infalarmtimeout";
    public static final String INFO_DO_POT = "infdopot";
    public static final String INFO_SEE_NEXT_CARD = "infseenextcard";
    public static final String INFO_SHOW_CARD  = "infshowcard";
    public static final String INFO_WIN_27 = "infwin27";
    public static final String INFO_DO_ANIMATION = "infdoanimotion";
    public static final String INFO_BUY_SURANCE= "infbuysurance";*/


    private Handler handler = new Handler() {

        // 该方法运行在主线程中
        // 接收到handler发送的消息，对UI进行操作
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what){
                case MESSAGE_INFO_TABLE:
                    Log.v("RABBY","MESSAGE_INFO_TABLE");
                    //游戏牌桌的信息
                    InitGameInfo();
                    iv_voice.setClickable(true);
                    iv_info.setClickable(true);
                    iv_menu.setClickable(true);
                    iv_apply.setClickable(true);
                    iv_game_info.setClickable(true);

                    break;
                case MESSAGE_INFO_SIT_SEAT:
                    //收到坐下的信息，包含自己的坐下
                    try {
                        mp=MediaPlayer.create(GameActivity.this,R.raw.chairsitsound);
                        mp.start();
                        String dataInfo = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_SIT_SEAT:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataInfo);
                        int userid=jsonReturn.getInt("userid");
                        int seatIndex=jsonReturn.getInt("seatindex");
                        int intochips=jsonReturn.getInt("intochips");

                        mGameUser.get(userid).seatindex=seatIndex;
                        mGameUser.get(userid).remainchips=intochips;
                        if (userid==application.getUserId()){
                            //本人进座位，则移位置
                            int iSeatnumber=mTableInfo.seats.length;
                            for (int i = 0; i < iSeatnumber; i++) {
                                int moveto= (i-seatIndex+iSeatnumber)%iSeatnumber;

                                setPosition(mSeatObjects.get(i),mSeatViewWidth,mSeatViewHeight,iSeatValue[moveto][0],iSeatValue[moveto][1]);
                                View view=mSeatObjects.get(i);
                                view.setVisibility(View.VISIBLE);
                                ImageView iv = (ImageView) view.findViewById(R.id.iv_user_head);
                                iv.setClickable(false);
                                setPosition(mChipObjects.get(i),mAmountChipViewWidth,mAmountChipVieHeight,iAmountChipLocation[moveto][0],iAmountChipLocation[moveto][1]);
                                setPosition(mTipObjects.get(i),mTipViewWidth,mTipViewHeight,iTipLocation[moveto][0],iTipLocation[moveto][1]);
                                setPosition(mCardBackObjects.get(i),mCardBackWidth,mCardBackHeight,iCardBack[moveto][0],iCardBack[moveto][1]);
                            }
                        }

                        //坐下
                        View view=mSeatObjects.get(seatIndex);
                        view.setVisibility(View.VISIBLE);
                        ImageView iv = (ImageView) view.findViewById(R.id.iv_user_head);
                        TextView tv_name = (TextView) view.findViewById(R.id.tv_user_name);
                        TextView tv_goldcoin = (TextView) view.findViewById(R.id.tv_goldcoin);

                        tv_name.setText(mGameUser.get(userid).nickName);
                        tv_name.setVisibility(View.VISIBLE);

                        tv_goldcoin.setText(intochips+"");
                        tv_goldcoin.setVisibility(View.VISIBLE);

                        if (mGameUser.get(userid).userHeadPic!=null && !mGameUser.get(userid).userHeadPic.equals("")) {
                            Picasso.with(getApplicationContext())
                                    .load(mGameUser.get(userid).userHeadPic)
                                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .resize(80, 80)
                                    .error(R.drawable.seat_empty)
                                    .transform(new CircleTransform())
                                    .into(iv);
                        }
                        iv.setClickable(false);



                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理收到的牌桌信息错误，错误内容为处理用户坐下信息错误"+e.getMessage());

                    }

                    break;
                case MESSAGE_TAKEIN_INFO:
                    //从服务器收到带入的相关信息
                    showWindow(layout_parent);
                    break;
                case MESSAGE_DISMISS_POPWINDOW:
                    if (popupWindow!=null) {
                        popupWindow.dismiss();
                    }
                    break;
                case MESSAGE_DISMISS_POPMENU:
                    if (popMenu!=null) {
                        popMenu.dismiss();
                    }
                    break;
                case MESSAGE_INFO_DOACTION:
                    //收到操作信息
                    /*{
                                public int userid;
                                public int seatindex;
                                public PlayAction action;
                                public int amountchips;
                                public int remainchips;
                                public RoundState rstate;
                                public DateTime actiontime;
                            }*/
                    try {
                        String dataInfo = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_DOACTION:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataInfo);
                        int userid=jsonReturn.getInt("userid");
                        int seatIndex=jsonReturn.getInt("seatindex");
                        int action=jsonReturn.getInt("action");
                        int amountchips=jsonReturn.getInt("amountchips");
                        int remainchips=jsonReturn.getInt("remainchips");
                        mGameUser.get(userid).remainchips=remainchips;
                        //收到操作，做展现
                        //更改用户的筹码额度
                        if (userid==application.getUserId()){
                            //自己操作，则隐藏按钮
                            mButtonRaise.setVisibility(View.INVISIBLE);
                            mButtonCheck.setVisibility(View.INVISIBLE);
                            mButtonFold.setVisibility(View.INVISIBLE);
                            mTVRaise.setVisibility(View.INVISIBLE);
                            mTVCheck.setVisibility(View.INVISIBLE);
                            mTVFold.setVisibility(View.INVISIBLE);
                        }

                        int iRealIndex=seatIndex;
                        if (getUserIndex()!=-1){
                            iRealIndex=(iRealIndex+mTableInfo.seats.length-getUserIndex())%mTableInfo.seats.length;
                        }
                        if (mTimerObjects.containsKey(iRealIndex)){
                            mTimerObjects.get(iRealIndex).cancel();
                            mTimerViewObjects.get(iRealIndex).setVisibility(View.INVISIBLE);

                        }


                        View view=mSeatObjects.get(seatIndex);
                        TextView tv_goldcoin = (TextView) view.findViewById(R.id.tv_goldcoin);
                        tv_goldcoin.setText(remainchips+"");
                        View viewTip=mTipObjects.get(seatIndex);
                        TextView tv_tip=(TextView)viewTip.findViewById(R.id.tv_tip);
                        View viewChip=mChipObjects.get(seatIndex);
                        viewChip.setVisibility(View.VISIBLE);
                        TextView tv_chip=(TextView)viewChip.findViewById(R.id.tv_chip);
                        tv_chip.setText(amountchips+"");
                        View viewCardBack=mCardBackObjects.get(seatIndex);
//                        viewCardBack.setVisibility(View.VISIBLE);
//                        ImageView ivCardBack=(ImageView)view.findViewById(R.id.iv_cardback);

                        switch (action){
                            case 0:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("下注");
                                mp=MediaPlayer.create(GameActivity.this,R.raw.chips_to_table);
                                mp.start();
                                break;
                            case 1:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("跟注");
                                mp=MediaPlayer.create(GameActivity.this,R.raw.chips_to_table);
                                mp.start();

                                break;
                            case 2:
                                mp=MediaPlayer.create(GameActivity.this,R.raw.foldcardsound);
                                mp.start();
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("弃牌");
                                viewCardBack.setVisibility(View.INVISIBLE);
                                break;

                            case 3:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("看牌");
                                mp=MediaPlayer.create(GameActivity.this,R.raw.checksound);
                                mp.start();
                                break;
                            case 4:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("加注");
                                mp=MediaPlayer.create(GameActivity.this,R.raw.chips_to_table);
                                mp.start();
                                break;
                            case 6:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("AllIn");
                                mp=MediaPlayer.create(GameActivity.this,R.raw.chips_to_table);
                                mp.start();
                                break;
                            case 8:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("小盲");
                                break;
                            case 9:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("大盲");
                                break;
                            case 10:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("Straddle");
                                break;
                            case 11:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("补盲");
                                break;
                            case 12:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("Ante");
                                break;
                        }

                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理用户操作信息错误，错误内容为"+e.getMessage());
                    }
                    break;
                case MESSAGE_INFO_START_TABLE:
                    /* public class StartTableGameParam
                    {
                        public int userid;
                        public int tableid;
                    }*/
                    String dataInfo = (String) msg.obj;
                    //这里收到就好，不做任何处理

                    break;
                case MESSAGE_INFO_HOLE:
                    try {
                        mp=MediaPlayer.create(GameActivity.this,R.raw.dealcardhole);
                        mp.start();
                        //先清空对象
                        mPlayerHolebjects = new HashMap<Integer, PlayerHole>();
                        String dataPlayerHole = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_HOLE:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataPlayerHole);

                        JSONArray temp = jsonReturn.getJSONArray("playerhole");

                        for(int i=0;i<temp.length();i++){
                            //这里要判断，是否有值，否则报异常
                            if (!temp.get(i).toString().equals("null")) {
                                JSONObject jsonPlayerHole = new JSONObject(temp.get(i).toString());//每一个人的底牌
                                PlayerHole playerHole=new PlayerHole();
                                playerHole.seatindex= jsonPlayerHole.getInt("seatindex");
                                playerHole.userid= jsonPlayerHole.getInt("userid");

                                JSONArray jsonCards=jsonPlayerHole.getJSONArray("hole");

                                if (!jsonCards.toString().equals("null")) {
                                    CardInfo[] playerHoleCards=new CardInfo[jsonCards.length()] ;
                                    for(int j=0;j<jsonCards.length();j++){
                                        JSONObject jsonCard=new JSONObject(jsonCards.get(j).toString());
                                        CardInfo cardInfo=new CardInfo();
                                        cardInfo.suit=jsonCard.getInt("suit");
                                        cardInfo.member=jsonCard.getInt("member");
                                        cardInfo.name=jsonCard.getString("name");
                                        playerHoleCards[j]=cardInfo;

                                    }
                                    playerHole.hole=playerHoleCards;
                                }

                                mCardBackObjects.get(playerHole.seatindex).setVisibility(View.VISIBLE);
                                mPlayerHolebjects.put(playerHole.userid,playerHole);
                                if (playerHole.userid==application.getUserId()){
                                    //显示两张底牌
                                    mMyCards.setVisibility(View.VISIBLE);
                                    for (int key : mMyCardsObjects.keySet()) {
                                        mMyCardsObjects.get(key).setVisibility(View.INVISIBLE);
                                    }
                                    for (int j=0;j<playerHole.hole.length;j++){
                                        mMyCardsObjects.get(j).setVisibility(View.VISIBLE);
                                        mMyCardsObjects.get(j).setImageBitmap(drawSingleCard(playerHole.hole[j].suit,playerHole.hole[j].member));
                                    }
                                }
                            }
                        }

                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理底牌信息错误，错误内容为"+e.getMessage());
                    }

                    break;
                case MESSAGE_INFO_FLOP:
                    try {
                        mp=MediaPlayer.create(GameActivity.this,R.raw.dealcardflop);
                        mp.start();
                        String dataFlop = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_FLOP:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataFlop);
                        JSONArray temp = jsonReturn.getJSONArray("flopcards");
                        mGameCards.setVisibility(View.VISIBLE);
                        for (int key : mGameCardsObjects.keySet()) {
                            mGameCardsObjects.get(key).setVisibility(View.INVISIBLE);
                        }
                        CardInfo[] flopCards=new CardInfo[temp.length()];//公牌
                        for(int i=0;i<temp.length();i++){
                            //这里要判断，是否有值，否则报异常
                            if (!temp.get(i).toString().equals("null")) {
                                JSONObject jsonCard=new JSONObject(temp.get(i).toString());
                                CardInfo cardInfo=new CardInfo();
                                cardInfo.suit=jsonCard.getInt("suit");
                                cardInfo.member=jsonCard.getInt("member");
                                cardInfo.name=jsonCard.getString("name");
                                flopCards[i]=cardInfo;
                                mGameCardsObjects.get(i).setVisibility(View.VISIBLE);
                                mGameCardsObjects.get(i).setImageBitmap(drawSingleCard( flopCards[i].suit, flopCards[i].member));
                            }
                        }

                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理公牌信息错误，错误内容为"+e.getMessage());
                    }
                    break;
                case MESSAGE_INFO_TURN:
                    //{"turncard":
                    try {
                        mp=MediaPlayer.create(GameActivity.this,R.raw.dealcard);
                        mp.start();
                        String dataTurn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_TURN:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataTurn);
                        JSONObject jsonCard = new JSONObject(jsonReturn.getString("turncard"));
                        CardInfo turncard=new CardInfo();//转牌
                        turncard.suit=jsonCard.getInt("suit");
                        turncard.member=jsonCard.getInt("member");
                        turncard.name=jsonCard.getString("name");
                        mGameCardsObjects.get(3).setVisibility(View.VISIBLE);
                        mGameCardsObjects.get(3).setImageBitmap(drawSingleCard( turncard.suit, turncard.member));



                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理turn信息错误，错误内容为"+e.getMessage());
                    }
                    break;

                case MESSAGE_INFO_RIVER:
                    try {
                        mp=MediaPlayer.create(GameActivity.this,R.raw.dealcard);
                        mp.start();
                        String dataRiver = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_RIVER:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataRiver);
                        JSONObject jsonCard = new JSONObject(jsonReturn.getString("rivercard"));
                        CardInfo rivercard=new CardInfo();//转牌
                        rivercard.suit=jsonCard.getInt("suit");
                        rivercard.member=jsonCard.getInt("member");
                        rivercard.name=jsonCard.getString("name");
                        mGameCardsObjects.get(4).setVisibility(View.VISIBLE);
                        mGameCardsObjects.get(4).setImageBitmap(drawSingleCard( rivercard.suit, rivercard.member));

                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理turn信息错误，错误内容为"+e.getMessage());
                    }
                    break;
                case MESSAGE_INFO_DO_POT:
                    try {
                        //所有用户chip清空，不展现
                        mp=MediaPlayer.create(GameActivity.this,R.raw.chips_to_pot);
                        mp.start();
                        for (int key : mChipObjects.keySet()) {
                            mChipObjects.get(key).setVisibility(View.INVISIBLE);
                        }
                        String pots = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_DO_POT:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(pots);
                        JSONArray jsonPots=jsonReturn.getJSONArray("pots");
                        int[] potsInfo=new int[jsonPots.length()];//底池
                        for (int i=0;i<jsonPots.length();i++){
                            potsInfo[i]=jsonPots.getInt(i);
                            mPoolObjects.get(i).setVisibility(View.VISIBLE);
                            mPoolOTextbjects.get(i).setText(potsInfo[i]+"");
                        }
                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理底池信息错误，错误内容为"+e.getMessage());
                    }

                    break;
                case MESSAGE_INFO_INIT_ROUND:
                    Log.v("RABBY","MESSAGE_INFO_INIT_ROUND:");
                     //庄家位
                     mD.setVisibility(View.INVISIBLE);
                    //游戏牌
                    for (int key : mGameCardsObjects.keySet()) {
                        mGameCardsObjects.get(key).setVisibility(View.INVISIBLE);
                    }
                    //自己的牌
                    for (int key : mMyCardsObjects.keySet()) {
                        mMyCardsObjects.get(key).setVisibility(View.INVISIBLE);
                    }
                    //tip
                    for (int key : mTipObjects.keySet()) {
                        mTipObjects.get(key).setVisibility(View.INVISIBLE);
                    }
                    //chip
                    for (int key : mChipObjects.keySet()) {
                        mChipObjects.get(key).setVisibility(View.INVISIBLE);
                    }
                    mMessage.setVisibility(View.INVISIBLE);
                    //pot
                    for (int key : mPoolObjects.keySet()) {
                        mPoolObjects.get(key).setVisibility(View.INVISIBLE);
                    }

                    //cardback
                    for (int key : mCardBackObjects.keySet()) {
                        mCardBackObjects.get(key).setVisibility(View.INVISIBLE);
                    }

                    //showcards
                    mShowCards.setVisibility(View.INVISIBLE);



                    break;
                case MESSAGE_INFO_START_ROUND:
                    /* public class InfStartRoundParam
                     {
                         //标识庄家和盲注位
                         public int buttonseatindex;
                         public int smallblindseatindex;
                         public int bigblindseatindex;
                         public int straddleseatindex;

                     }*/
                    try {
                        //设置庄家位, 只做D的选择
                        int buttonseatindex,smallblindseatindex,bigblindseatindex,straddleseatindex;
                        String dataReturn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_START_ROUND:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataReturn);
                        buttonseatindex=jsonReturn.getInt("buttonseatindex");
                        if (getUserIndex()!=-1){
                            buttonseatindex=(buttonseatindex-getUserIndex()+mTableInfo.seats.length)%mTableInfo.seats.length;
                        }
                        // 显示庄家位
                        mD.setVisibility(View.VISIBLE);
                        setPosition(mD,mDViewWidth,mDViewHeight,iD[buttonseatindex][0],iD[buttonseatindex][1]);


                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"设置庄家信息错误，错误内容为"+e.getMessage());
                    }

                    break;
                case MESSAGE_INFO_WAIT_ACTION:

                    try {
                        //等待操作
                        String dataReturn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_WAIT_ACTION:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataReturn);

                        if(jsonReturn.getInt("userid")==application.getUserId()){
                            //自己
                            mp=MediaPlayer.create(GameActivity.this,R.raw.waitaction);
                            mp.start();
                            mAction.setVisibility(View.VISIBLE);
                            mButtonRaise.setVisibility(View.INVISIBLE);
                            mTVRaise.setVisibility(View.INVISIBLE);
                            mButtonCheck.setVisibility(View.INVISIBLE);
                            mTVCheck.setVisibility(View.INVISIBLE);
                            mButtonFold.setVisibility(View.INVISIBLE);
                            mTVFold.setVisibility(View.INVISIBLE);

                            JSONArray jsonNeedAction=jsonReturn.getJSONArray("needaction");
                            for (int i=0;i<jsonNeedAction.length();i++){
                                int action=jsonNeedAction.getInt(i);
                                TextView tv_goldcoin = (TextView) mSeatObjects.get(getUserIndex()).findViewById(R.id.tv_goldcoin);
                                maxChip=Integer.parseInt(tv_goldcoin.getText().toString());
                                switch (action){
                                    case 0:
                                        //bet
                                        mButtonRaise.setVisibility(View.VISIBLE);
                                        mTVRaise.setVisibility(View.VISIBLE);
                                        mTVRaise.setText(" bet");
                                        minChip=1;

                                        break;
                                    case 1:
                                        mButtonCheck.setVisibility(View.VISIBLE);
                                        mTVCheck.setVisibility(View.VISIBLE);
                                        mTVCheck.setText("跟注");

                                        break;
                                    case 2:
                                        mButtonFold.setVisibility(View.VISIBLE);
                                        mTVFold.setVisibility(View.VISIBLE);
                                        mTVFold.setText("弃牌");

                                        break;
                                    case 3:
                                        mButtonCheck.setVisibility(View.VISIBLE);
                                        mTVCheck.setVisibility(View.VISIBLE);
                                        mTVCheck.setText("看牌");

                                        break;
                                    case 4:
                                        mButtonRaise.setVisibility(View.VISIBLE);
                                        mTVRaise.setVisibility(View.VISIBLE);
                                        mTVRaise.setText("加注");
                                        maxpaidchips=jsonReturn.getInt("maxpaidchips");
                                        TextView tv_chip=(TextView)mChipObjects.get(getUserIndex()).findViewById(R.id.tv_chip);
                                        minChip=maxpaidchips-Integer.parseInt(tv_chip.getText().toString());;

                                        break;
                                    case 6:
                                        mButtonRaise.setVisibility(View.VISIBLE);
                                        mTVRaise.setVisibility(View.VISIBLE);
                                        mTVRaise.setText("AllIn");
                                        break;
                                }
                            }
                            timer = new CountDownTimer(jsonReturn.getInt("waitseconds")*1000, 1000) {

                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    mp=MediaPlayer.create(GameActivity.this,R.raw.timeovertipsound);
                                    mp.start();

                                }
                            };
                            timer.start();



                        }else{
                            int seatindex=jsonReturn.getInt("seatindex");
                            if (getUserIndex()!=-1)
                            {
                                seatindex=(seatindex-getUserIndex()+mTableInfo.seats.length)%mTableInfo.seats.length;
                            }
                            //展现时钟
                            addTimer(seatindex,jsonReturn.getInt("waitseconds"));
                        }



                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"等待操作处理错误，错误内容为"+e.getMessage());
                    }

                    break;
                case MESSAGE_INFO_SEE_NEXT_CARD:

                    try {
                        String dataReturn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_SEE_NEXT_CARD:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataReturn);
                        int roundState=jsonReturn.getInt("state");

                        JSONArray temp = jsonReturn.getJSONArray("card");
                        switch (roundState){
                            case 4:
                                //3张牌
                                CardInfo[] flopCards=new CardInfo[temp.length()];//公牌
                                for(int i=0;i<3;i++){
                                    if (!temp.get(i).toString().equals("null")) {
                                        JSONObject jsonCard=new JSONObject(temp.get(i).toString());;//每一个人的底牌
                                        flopCards[i].suit=jsonCard.getInt("suit");
                                        flopCards[i].member=jsonCard.getInt("member");
                                        flopCards[i].name=jsonCard.getString("name");
                                        mGameCardsObjects.get(i).setVisibility(View.VISIBLE);
                                        mGameCardsObjects.get(i).setImageBitmap(drawSingleCard( flopCards[i].suit, flopCards[i].member));
                                    }
                                }
                                break;
                            case 6:
                                //1张牌
                                CardInfo[] turnCards=new CardInfo[temp.length()];//公牌
                                for(int i=0;i<1;i++){
                                    if (!temp.get(i).toString().equals("null")) {
                                        JSONObject jsonCard=new JSONObject(temp.get(i).toString());;//每一个人的底牌
                                        turnCards[i].suit=jsonCard.getInt("suit");
                                        turnCards[i].member=jsonCard.getInt("member");
                                        turnCards[i].name=jsonCard.getString("name");
                                        mGameCardsObjects.get(3).setVisibility(View.VISIBLE);
                                        mGameCardsObjects.get(3).setImageBitmap(drawSingleCard( turnCards[i].suit, turnCards[i].member));
                                    }
                                }
                                break;
                            case 8:
                                //1张牌
                                //1张牌
                                CardInfo[] riverCards=new CardInfo[temp.length()];//公牌
                                for(int i=0;i<1;i++){
                                    if (!temp.get(i).toString().equals("null")) {
                                        JSONObject jsonCard=new JSONObject(temp.get(i).toString());;//每一个人的底牌
                                        riverCards[i].suit=jsonCard.getInt("suit");
                                        riverCards[i].member=jsonCard.getInt("member");
                                        riverCards[i].name=jsonCard.getString("name");
                                        mGameCardsObjects.get(4).setVisibility(View.VISIBLE);
                                        mGameCardsObjects.get(4).setImageBitmap(drawSingleCard( riverCards[i].suit, riverCards[i].member));
                                    }
                                }
                                break;


                        }
                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理seenextcard信息错误，错误内容为"+e.getMessage());
                    }


                    break;
                case MESSAGE_INFO_SHOW_CARD:

                    try {
                        //等待操作
                        String dataReturn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_SHOW_CARD:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataReturn);
                        int seatindex=jsonReturn.getInt("seatindex");
                        int userid=jsonReturn.getInt("userid");
                        if (userid!=application.getUserId()) {
                            mShowCards.setVisibility(View.VISIBLE);
                            int iRealIndex = seatindex;
                            if (getUserIndex() != -1) {
                                iRealIndex = (seatindex - getUserIndex() + mTableInfo.seats.length) % (mTableInfo.seats.length);
                            }

                            setPosition(mShowCards, 120, 80, iSeatValue[iRealIndex][0], iSeatValue[iRealIndex][1] + mNameTextHeight);
                            ImageView iv5 = (ImageView) mShowCards.findViewById(R.id.iv_card1);
                            iv5.setImageBitmap(drawSingleCard(mPlayerHolebjects.get(userid).hole[0].suit, mPlayerHolebjects.get(userid).hole[0].member));
                            ImageView iv6 = (ImageView) mShowCards.findViewById(R.id.iv_card2);
                            iv6.setImageBitmap(drawSingleCard(mPlayerHolebjects.get(userid).hole[1].suit, mPlayerHolebjects.get(userid).hole[1].member));
                        }


                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"showcard错误，错误内容为"+e.getMessage());
                    }
                    break;
                case MESSAGE_START_GAME:
                    try {
                        String dataReturn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_START_GAME:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataReturn);
                        if (jsonReturn.getInt("ret")==0){
                            //开始游戏按钮隐藏
                            btnStartGame.setVisibility(View.INVISIBLE);
                            mMessage.setVisibility(View.INVISIBLE);
                            //判断桌上有几个用户，如果只有一个，那么提示等待别的用户开始
                        }else{
                            ToastUtil.showToastInScreenCenter(GameActivity.this,"游戏开始异常！");
                        }
                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"showcard错误，错误内容为"+e.getMessage());
                    }
                    break;
                case MESSAGE_INFO_LEAVE_SEAT:
                    //离开座位
                    //头像去掉，显示隐藏，相关的数据都要清空
                    /*   public int userid;
                        public int tableid;
                         public int seatindex;*/
                    try {
                        mp=MediaPlayer.create(GameActivity.this,R.raw.chairstandsound);
                        mp.start();
                        //等待操作
                        String dataReturn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_LEAVE_SEAT:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataReturn);
                        int seatindex=jsonReturn.getInt("seatindex");
                        int userid=jsonReturn.getInt("userid");

                        View view=mSeatObjects.get(seatindex);
                        ImageView iv = (ImageView) view.findViewById(R.id.iv_user_head);
                        TextView tv_name = (TextView) view.findViewById(R.id.tv_user_name);
                        TextView tv_goldcoin = (TextView) view.findViewById(R.id.tv_goldcoin);
                        tv_name.setText("");
                        tv_name.setVisibility(View.INVISIBLE);
                        tv_goldcoin.setText("");
                        tv_goldcoin.setVisibility(View.INVISIBLE);
                        iv.setImageResource(R.drawable.seat_empty);

                        mGameUser.get(userid).seatindex=-1;

                        mTipObjects.get(seatindex).setVisibility(View.INVISIBLE);
                        mChipObjects.get(seatindex).setVisibility(View.INVISIBLE);
                        mCardBackObjects.get(seatindex).setVisibility(View.INVISIBLE);

                        //先离开座位，然后判断是否自己，如果是自己，别的空位置按钮全部可以坐下
                        if (userid==application.getUserId()) {

                            //将空座位设置成可坐 ，就是说可以click
                            for (int i=0;i<mTableInfo.seats.length;i++){
                                View viewSeat=mSeatObjects.get(i);
                                ImageView ivHead = (ImageView) viewSeat.findViewById(R.id.iv_user_head);
                                ivHead.setClickable(true);

                            }
                            for (int key : mGameUser.keySet()) {
                                if (mGameUser.get(key).seatindex!=-1) {
                                    View viewSeat = mSeatObjects.get(mGameUser.get(key).seatindex);
                                    ImageView ivHead = (ImageView) viewSeat.findViewById(R.id.iv_user_head);
                                    ivHead.setClickable(false);
                                }
                            }
                        }

                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"离开座位错误，错误内容为"+e.getMessage());
                    }


                    break;
                case MESSAGE_INFO_ADD_CHIPS:
                   try {

                        //等待操作
                        String dataReturn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_ADD_CHIPS:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataReturn);

                        int userid=jsonReturn.getInt("userid");
                        int chips=jsonReturn.getInt("chips");
                        mGameUser.get(userid).remainchips+=chips;
                        View view=mSeatObjects.get( mGameUser.get(userid).seatindex);
                        TextView tv_goldcoin = (TextView) view.findViewById(R.id.tv_goldcoin);
                        tv_goldcoin.setText(mGameUser.get(userid).remainchips);

                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理INFO_ADD_CHIPS错误，错误内容为"+e.getMessage());
                    }


                    break;
                case MESSAGE_INFO_HOLD_SEAT:
                    try {
                       //收到保位留桌
                        String dataReturn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_HOLD_SEAT:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataReturn);

                        int userid=jsonReturn.getInt("userid");
                        int seatindex=jsonReturn.getInt("seatindex");
                        int holdseconds=jsonReturn.getInt("holdseconds");

                        View view=mSeatObjects.get(seatindex);
                        ImageView iv = (ImageView) view.findViewById(R.id.iv_user_head);
                        TextView tv_name = (TextView) view.findViewById(R.id.tv_user_name);
                        TextView tv_goldcoin = (TextView) view.findViewById(R.id.tv_goldcoin);
                        tv_name.setText("");
                        tv_name.setVisibility(View.INVISIBLE);
                        tv_goldcoin.setText("");
                        tv_goldcoin.setVisibility(View.INVISIBLE);
                        iv.setImageResource(R.drawable.seat_empty);


                        mTipObjects.get(seatindex).setVisibility(View.INVISIBLE);
                        mChipObjects.get(seatindex).setVisibility(View.INVISIBLE);
                        mCardBackObjects.get(seatindex).setVisibility(View.INVISIBLE);

                        //先离开座位，然后判断是否自己，如果是自己，显示回到座位按钮，别人则显示倒计时
                        if (userid==application.getUserId()) {
                            //显示保位留桌
                            addTimer(0,holdseconds);
                            mReturnSeat.setVisibility(View.VISIBLE);

                        }else{

                            if (getUserIndex()!=-1)
                            {
                                seatindex=(seatindex-getUserIndex()+mTableInfo.seats.length)%mTableInfo.seats.length;
                            }
                            //展现时钟
                            addTimer(seatindex,holdseconds);

                        }

                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理INFO_ADD_CHIPS错误，错误内容为"+e.getMessage());
                    }


                    break;
                case MESSAGE_INFO_DO_END:
                    //动画显示谁赢了，加上筹码

                    break;

                case MESSAGE_INFO_BACK_SEAT:

                    /*
                    public class BackSeatParam
                    {
                        public int userid;
                        public int tableid;
                        public int seatindex;
                    }
                    */
                    try {
                        String dataReturn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_BACK_SEAT:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataReturn);

                        int userid=jsonReturn.getInt("userid");
                        int seatindex=jsonReturn.getInt("seatindex");

                        mReturnSeat.setVisibility(View.INVISIBLE);
                        int iRealIndex=seatindex;
                        if (getUserIndex()!=-1){
                            iRealIndex=(iRealIndex+mTableInfo.seats.length-getUserIndex())%mTableInfo.seats.length;
                        }
                        if (mTimerObjects.containsKey(iRealIndex)){
                            mTimerObjects.get(iRealIndex).cancel();
                            mTimerViewObjects.get(iRealIndex).setVisibility(View.INVISIBLE);

                        }




                        View view=mSeatObjects.get(seatindex);
                        view.setVisibility(View.VISIBLE);
                        ImageView iv = (ImageView) view.findViewById(R.id.iv_user_head);
                        TextView tv_name = (TextView) view.findViewById(R.id.tv_user_name);
                        TextView tv_goldcoin = (TextView) view.findViewById(R.id.tv_goldcoin);

                        tv_name.setText(mGameUser.get(userid).nickName);
                        tv_name.setVisibility(View.VISIBLE);
                        tv_goldcoin.setText(mGameUser.get(userid).remainchips);

                       //mTableUser.get( mUserSeat.get(seatindex)).
                        //tv_goldcoin.setText(intochips+"");
                        tv_goldcoin.setVisibility(View.VISIBLE);

                        if (mGameUser.get(userid).userHeadPic!=null && !mGameUser.get(userid).userHeadPic.equals("")) {
                            Picasso.with(getApplicationContext())
                                    .load(mGameUser.get(userid).userHeadPic)
                                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .resize(80, 80)
                                    .error(R.drawable.seat_empty)
                                    .transform(new CircleTransform())
                                    .into(iv);
                        }
                        iv.setClickable(false);

                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理IMESSAGE_INFO_BACK_SEAT错误，错误内容为"+e.getMessage());
                    }


                    break;
                case MESSAGE_INFO_DISPOSE_TABLE:
                    //弹出提示，确认之后退出

                    try {
                        String dataReturn = (String) msg.obj;
                        Log.v("RABBY","MESSAGE_INFO_DISPOSE_TABLE:"+(String)msg.obj);
                        JSONObject jsonReturn = new JSONObject(dataReturn);

                        int userid=jsonReturn.getInt("userid");
                        if (userid==application.getUserId()){
                            new AlertDialog.Builder(GameActivity.this).setTitle("系统提示")//设置对话框标题

                                    .setMessage("牌局已被解散，点确定退出牌局！")//设置显示的内容

                                    .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮

                                       @Override
                                        public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                            // TODO Auto-generated method stub
                                           finish();

                                        }

                                    }).show();//在按键响应事件中显示此对话框

                        }




                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理MESSAGE_INFO_DISPOSE_TABLE错误，错误内容为"+e.getMessage());
                    }



                    break;

                case MESSAGE_APPLY_INFO:

                    //popApplyWindow
                    int layoutId = R.layout.popupwindow_apply;   // 布局ID
                    View contentView = LayoutInflater.from(GameActivity.this).inflate(layoutId, null);
                    //绑定数据
                    ImageView iv_close_Apply=(ImageView)contentView.findViewById(R.id.iv_close);
                    iv_close_Apply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //关闭popwndow
                            popApplyWindow.dismiss();
                        }
                    });
                    rv_apply_info=(RecyclerView) contentView.findViewById(R.id.rv_apply_info);
                    mLayoutManager = new LinearLayoutManager(GameActivity.this);
                    rv_apply_info.setLayoutManager(mLayoutManager);
                    //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
                    rv_apply_info.setHasFixedSize(true);
                    rv_apply_info.addItemDecoration(new DividerItemDecoration(
                            GameActivity.this, DividerItemDecoration.VERTICAL));
                    //创建并设置Adapter



                    //mAdapter = new SelectCityAdapter(mCity);
                    mAdapter = new ControlInApplyAdapter(GameActivity.this, new ControlInApplyAdapter.onButtonClick() {
                        @Override
                        public void onButtonClick(final ApplyInfo applyInfo) {
                            //提交后台
                            Thread thread=new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    try{
                                        //拼装url字符串

                                        DzApplication applicatoin=(DzApplication)getApplication();
                                        JSONObject jsonObj = new JSONObject();
                                        jsonObj.put("userid",applyInfo.requestuserid);
                                        jsonObj.put("requestid",applyInfo.requestid);
                                        Boolean bPermit=false;
                                        if (applyInfo.ispermit==0){
                                            bPermit=false;
                                        }else if (applyInfo.ispermit==1){
                                            bPermit=true;
                                        }
                                        jsonObj.put("ispermit",bPermit);



                                        String strURL=getString(R.string.url_remote);
                                        strURL+="func=setrequesttakeininfo&param="+jsonObj.toString();

                                        URL url = new URL(strURL);
                                        Request request = new Request.Builder().url(strURL).build();
                                        Response response = DzApplication.getHttpClient().newCall(request).execute();
                                        String result=response.body().string();
                                        //{"requesttakeininfo":[{"requestid":16,"requestuserid":1008,"usernickname":"我是无名","requesttakeinchips":200,"requestpermittakeinchips":200,"ispermit":2}]}
                                        JSONObject jsonObject=new JSONObject(result);
                                        if (jsonObject.getInt("ret")==0) {
                                            //告诉申请者，审核结果RequestTakeInRet|{‘IsPermit’:true,’permittakein’:300}
                                            JSONObject json=new JSONObject();
                                            json.put("IsPermit",bPermit);
                                            json.put("permittakein",applyInfo.requesttakeinchips);
                                            String sendstr="RequestTakeInRet|"+json.toString();
                                            cn.jpush.im.android.api.model.Message message=JMessageClient.createSingleTextMessage(applyInfo.requestuserid+"",getString(R.string.app_key),sendstr);

                                            message.setOnSendCompleteCallback(new BasicCallback() {
                                                @Override
                                                public void gotResult(int responseCode, String responseDesc) {
                                                    if (responseCode == 0) {

                                                        // 消息发送成功
                                                    } else {
                                                        // 消息发送失败
                                                        ToastUtil.showToastInScreenCenter(GameActivity.this,"发送审核信息失败，请重新申请！");
                                                    }
                                                }
                                            });
                                            JMessageClient.sendMessage(message);
                                            jsonObj = new JSONObject();
                                            jsonObj.put("userid",applicatoin.getUserId());
                                            jsonObj.put("tableid",gameId);


                                            strURL=getString(R.string.url_remote);
                                            strURL+="func=getrequesttakeininfo&param="+jsonObj.toString();

                                            url = new URL(strURL);
                                            request = new Request.Builder().url(strURL).build();
                                            response = DzApplication.getHttpClient().newCall(request).execute();
                                            result=response.body().string();
                                            //{"requesttakeininfo":[{"requestid":16,"requestuserid":1008,"usernickname":"我是无名","requesttakeinchips":200,"requestpermittakeinchips":200,"ispermit":2}]}
                                            jsonObject=new JSONObject(result);
                                            mlistApplyInfo=new ArrayList<ApplyInfo>();
                                            JSONArray jsonApplyArray=new JSONArray(jsonObject.getString("requesttakeininfo"));
                                            for(int i=0;i<jsonApplyArray.length();i++){
                                                JSONObject jsonApplyInfo=new JSONObject(jsonApplyArray.get(i).toString());
                                                ApplyInfo applyInfo=new ApplyInfo();
                                                applyInfo.tableId=gameId;
                                                applyInfo.tablename=gameHouseName;
                                                applyInfo.requestid=jsonApplyInfo.getInt("requestid");
                                                applyInfo.requestuserid=jsonApplyInfo.getInt("requestuserid");
                                                applyInfo.usernickname=jsonApplyInfo.getString("usernickname");
                                                applyInfo.requesttakeinchips=jsonApplyInfo.getInt("requesttakeinchips");
                                                applyInfo.requestpermittakeinchips=jsonApplyInfo.getInt("requestpermittakeinchips");
                                                applyInfo.ispermit=jsonApplyInfo.getInt("ispermit");
                                                mlistApplyInfo.add(applyInfo);


                                            }





                                            handler.sendEmptyMessage(MESSAGE_UPDATE_APPLY_INFO);
                                        }else{
                                            ToastUtil.showToastInScreenCenter(GameActivity.this,"带入申请审核不成功!");
                                        }

                                    }catch (Exception e){
                                        ToastUtil.showToastInScreenCenter(GameActivity.this,"带入申请审核异常，请稍后重试!"+e.toString());
                                    }

                                }
                            });
                            thread.start();



                        }
                    });
                    rv_apply_info.setAdapter(mAdapter);
                    mAdapter.setData(mlistApplyInfo);

                    int  width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
                    int  height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
                    contentView.measure(width,height);
                    int height1=contentView.getMeasuredHeight();
                    int  width1=contentView.getMeasuredWidth();
                    popApplyWindow = new PopupWindow(contentView,600 ,620 , true);

                    popApplyWindow.setFocusable(true);
                    // 设置允许在外点击消失
                    popApplyWindow.setOutsideTouchable(true);
                    // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
                    popApplyWindow.setBackgroundDrawable(new ColorDrawable());
                    // 设置好参数之后再show
                    popApplyWindow.showAtLocation(GameActivity.this.getWindow().getDecorView(), Gravity.CENTER,0,0);;

                    break;
                case  MESSAGE_UPDATE_APPLY_INFO:
                    mAdapter.setData(mlistApplyInfo);

            }



        }
    };


    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder = ( SocketService.SocketBinder) service;
            SocketService socketService = myBinder.getService();
            socketService.setCallback(new SocketService.Callback() {
                @Override
                public void onReciveData(String data) {
                    //收到数据，进行处理，这里都是处理之后的数据
                    try{
                        String[] recData=data.split("\\|");
                        recData[0]=recData[0].substring(1);
                        if (recData[0].equals(Constant.RET_CREATE_TABLE)){
                            //创建牌局返回,的调用进去牌桌接口
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")==0){
                                String msg = "$" + Constant.GAME_ENTER_TABLE + "|";
                                JSONObject jsonSend = new JSONObject();
                                jsonSend.put("userid", application.getUserId());
                                jsonSend.put("tableid", gameId);
                                jsonSend.put("nickname",application.getUser().nickName);
                                jsonSend.put("headpic",application.getUser().userHeadPic);
                                msg+=jsonSend.toString().replace("$","￥");
                                myBinder.sendInfo(msg);


                            }else{
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"创建牌局失败，错误原因为："+jsonReturn.getString("msg"));
                            }

                        }else if (recData[0].equals(Constant.RET_ENTER_TABLE)){
                            //进入牌局返回
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){

                                ToastUtil.showToastInScreenCenter(GameActivity.this,"进入牌桌失败，错误原因为："+jsonReturn.getString("msg"));
                                finish();
                            }
                        }else if (recData[0].equals(Constant.INFO_ENTER_TABLE)){
                            //收到别人进牌局的消息，如果是自己，则不作任何处理，是别人的，记录下来，也可以将来做提示，某人进入了牌桌
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            GameUser user=new GameUser();
                            user.userId=jsonReturn.getInt("userid");
                            user.nickName=jsonReturn.getString("nickname");
                            user.userHeadPic=jsonReturn.getString("headpic");
                            user.seatindex=-1;
                            //jsonReturn.getInt("userid")这里不保留桌号
                            if (!mGameUser.containsKey( user.userId)){
                                mGameUser.put(user.userId,user);
                            }

                        }else if (recData[0].equals(Constant.INFO_TABLE_INFO)){
                            //收到牌桌信息,进入牌桌的时候都会收到，有可能是正在进行中的游戏数据
                            //根据state来判断显示，如果是自己的要显示开始按钮之类的
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            mTableInfo=new TableInfo();
                            mTableInfo.state=jsonReturn.getInt("state");//游戏状态


                            JSONArray temp=jsonReturn.getJSONArray("pots");
                            mTableInfo.pots=new int[temp.length()];//底池
                            for(int i=0;i<temp.length();i++){
                                mTableInfo.pots[i]=temp.getInt(i);
                            }

                            temp=jsonReturn.getJSONArray("comunitycards");
                            mTableInfo.comunitycards=new CardInfo[temp.length()];//公牌
                            for(int i=0;i<temp.length();i++){
                                //这里要判断，是否有值，否则报异常
                                if (!temp.get(i).toString().equals("null")) {
                                    JSONObject jsonTemp = new JSONObject(temp.get(i).toString());
                                    mTableInfo.comunitycards[i].suit = jsonTemp.getInt("suit");
                                    mTableInfo.comunitycards[i].member = jsonTemp.getInt("member");
                                    mTableInfo.comunitycards[i].name = jsonTemp.getString("name");
                                }
                            }

                            temp=jsonReturn.getJSONArray("seats");
                            mTableInfo.seats=new TableSeatInfo[temp.length()];
                            for(int i=0;i<temp.length();i++){
                                if (!temp.get(i).toString().equals("null")) {
                                    JSONObject jsonTemp = new JSONObject(temp.get(i).toString());
                                    TableSeatInfo tableSeatInfo= new TableSeatInfo();

                                    tableSeatInfo.isbutton = jsonTemp.getBoolean("isbutton");
                                    tableSeatInfo.issb = jsonTemp.getBoolean("issb");
                                    tableSeatInfo.isbb = jsonTemp.getBoolean("isbb");
                                    tableSeatInfo.state = jsonTemp.getInt("state");
                                    tableSeatInfo.curholdtime = jsonTemp.getInt("curholdtime");
                                    tableSeatInfo.userid = jsonTemp.getInt("userid");
                                    mTableInfo.seats[i]=tableSeatInfo;



//                                    mTableInfo.seats[i].isbutton = jsonTemp.getBoolean("isbutton");
//                                    mTableInfo.seats[i].issb = jsonTemp.getBoolean("issb");
//                                    mTableInfo.seats[i].isbb = jsonTemp.getBoolean("isbb");
//                                    mTableInfo.seats[i].state = jsonTemp.getInt("state");
//                                    mTableInfo.seats[i].curholdtime = jsonTemp.getInt("curholdtime");
//                                    mTableInfo.seats[i].userid = jsonTemp.getInt("userid");
                                }
                            }

                            temp=jsonReturn.getJSONArray("players");
                            mTableInfo.players=new TablePlayerInfo[temp.length()];
                            for(int i=0;i<temp.length();i++){
                                if (!temp.get(i).toString().equals("null")) {
                                    JSONObject jsonTemp = new JSONObject(temp.get(i).toString());
                                    TablePlayerInfo tablePlayerInfo=new TablePlayerInfo();

                                    tablePlayerInfo.userid = jsonTemp.getInt("userid");
                                    tablePlayerInfo.nickname = jsonTemp.getString("nickname");
                                    tablePlayerInfo.headpic = jsonTemp.getString("headpic");
                                    tablePlayerInfo.remainchips = jsonTemp.getInt("remainchips");
                                    tablePlayerInfo.takeinchips = jsonTemp.getInt("takeinchips");
                                    tablePlayerInfo.amountchips = jsonTemp.getInt("amountchips");


                                    if (!jsonTemp.get("cards").toString().equals("null")) {
                                        //这里后台传来的牌是空的
                                        JSONArray temp2 = jsonTemp.getJSONArray("cards");

                                        tablePlayerInfo.cards = new CardInfo[temp2.length()];
                                        for (i = 0; i < temp2.length(); i++) {
                                            if (!temp2.get(i).toString().equals("null")) {
                                                jsonTemp = new JSONObject(temp2.get(i).toString());
                                                tablePlayerInfo.cards[i].suit = jsonTemp.getInt("suit");
                                                tablePlayerInfo.cards[i].member = jsonTemp.getInt("member");
                                                tablePlayerInfo.cards[i].name = jsonTemp.getString("name");
                                            }
                                        }
                                    }

                                    tablePlayerInfo.seatindex = jsonTemp.getInt("seatindex");
                                    tablePlayerInfo.curwaitactiontime = jsonTemp.getInt("curwaitactiontime");
                                    tablePlayerInfo.isconnected = jsonTemp.getBoolean("isconnected");
                                    tablePlayerInfo.lastplayaction = jsonTemp.getInt("lastplayaction");
                                    if (!jsonTemp.get("waitactionparam").toString().equals("null")) {
                                        JSONObject jsonWaitAction = new JSONObject(jsonTemp.get("waitactionparam").toString());
                                        WaitAction waitAction = new WaitAction();

                                        waitAction.userid = jsonWaitAction.getInt("userid");
                                        waitAction.seatindex = jsonWaitAction.getInt("seatindex");
                                        waitAction.tableid = jsonWaitAction.getInt("tableid");
                                        waitAction.waitseconds = jsonWaitAction.getInt("waitseconds");
                                        List<Integer> listAction = new ArrayList<Integer>();
                                        JSONArray jsonAction = new JSONArray(jsonWaitAction.get("needaction").toString());
                                        for (int iii = 0; iii < jsonAction.length(); iii++) {
                                            listAction.add(jsonAction.getInt(iii));
                                        }
                                        waitAction.needaction = listAction;
                                        waitAction.maxpaidchips = jsonWaitAction.getInt("maxpaidchips");
                                        tablePlayerInfo.waitactionparam = waitAction;
                                    }

                                    mTableInfo.players[i]=tablePlayerInfo;
//                                    //加入mGameUser
//                                    GameUser user=new GameUser();
//                                    user.userId=jsonTemp.getInt("userid");
//                                    user.nickName=jsonTemp.getString("nickname");
//                                    user.userHeadPic=jsonTemp.getString("headpic");
//                                    user.seatindex=-1;
//                                    //jsonReturn.getInt("userid")这里不保留桌号
//
//                                    if (!mTableUser.containsKey( user.userId)){
//                                        mTableUser.put(user.userId,user);
//                                    }
                                }
                            }
                            mTableInfo.starttime=jsonReturn.getString("starttime");
                            mTableInfo.gametype=jsonReturn.getInt("gametype");
                            mTableInfo.smallblind=jsonReturn.getInt("smallblind");
                            mTableInfo.bigblind=jsonReturn.getInt("bigblind");
                            mTableInfo.mintakeinchips=jsonReturn.getInt("mintakeinchips");
                            mTableInfo.issurance=jsonReturn.getBoolean("isinsurance");
                            mTableInfo.isstraddle=jsonReturn.getBoolean("isstraddle");
                            mTableInfo.is27=jsonReturn.getBoolean("is27");
                            mTableInfo.ante=jsonReturn.getInt("ante");
                            mTableInfo.isgpsrestrict=jsonReturn.getBoolean("isgpsrestrict");
                            mTableInfo.isiprestrict=jsonReturn.getBoolean("isiprestrict");
                            mTableInfo.iscontroltakein=jsonReturn.getBoolean("iscontroltakein");
                            mTableInfo.maxtakeinchips=jsonReturn.getInt("maxtakeinchips");
                            mTableInfo.takeinchipsuplimit=jsonReturn.getInt("takeinchipsuplimit");
                            mTableInfo.createuserid=jsonReturn.getInt("createuserid");
                            mTableInfo.curactionseatindex=jsonReturn.getInt("curactionseatindex");
                            mTableInfo.curactionwaittime=jsonReturn.getInt("curactionwaittime");
                            mTableInfo.tablename=jsonReturn.getString("tablename");
                            gameHouseName=mTableInfo.tablename;

                            //此处注册接受消息


                            JMessageClient.registerEventReceiver(GameActivity.this);

                            //根据tableinfo设置界面
                            handler.sendEmptyMessage(MESSAGE_INFO_TABLE);
                        } else if (recData[0].equals(Constant.RET_START_GAME)){
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_START_GAME;
                            handler.sendMessage(message);


                        }else  if (recData[0].equals(Constant.INFO_SIT_SEAT)){
                            //收到坐下的消息，通知界面隐藏购买的popwindow
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_SIT_SEAT;
                            handler.sendMessage(message);

                        }else  if (recData[0].equals(Constant.RET_SIT_TABLE)){
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            /*  public int ret; //0成功
                                public string msg;*/
                            if (jsonReturn.getInt("ret")==0){
                                application.getUser().goldcoin-=Integer.parseInt(tv_service_coin.getText().toString())+Integer.parseInt(tv_core.getText().toString());
                                handler.sendEmptyMessage(MESSAGE_DISMISS_POPWINDOW);

                            }else if (jsonReturn.getInt("ret")==1){
                                if(jsonReturn.getString("msg").equals("剩余记分牌为零")){
                                    isFirstBuyCore=true;
                                    getTakeInChipsFromServer();
                                }else{
                                    ToastUtil.showToastInScreenCenter(GameActivity.this,jsonReturn.getString("msg"));
                                }

                            }else{
                                handler.sendEmptyMessage(MESSAGE_DISMISS_POPWINDOW);
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"处理服务器返回的坐下数据异常,错误信息："+jsonReturn.getString("msg"));
                            }

                        }else  if (recData[0].equals(Constant.INFO_ACTION)){
                            //收到action
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_DOACTION;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.INFO_START_TABLE)){
                            //收到action
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_START_TABLE;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.INFO_HOLE)){
                            //收到action
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_HOLE;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.INFO_FLOP)){
                            //收到action
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_FLOP;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.INFO_RIVER)){
                            //收到action
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_RIVER;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.INFO_TURN)){
                            //收到action
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_TURN;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.INFO_DO_POT)){
                            //收到action
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_DO_POT;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.INFO_START_ROUND)){
                            //牌回合开始
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_START_ROUND;
                            handler.sendMessage(message);

                        }else  if (recData[0].equals(Constant.INFO_INIT_ROUND)){
                            //收到初始化牌局信息

                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_INIT_ROUND;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.INFO_WAIT_ACTION)){
                            //收到action
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_WAIT_ACTION;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.INFO_SEE_NEXT_CARD)){
                            //收到action
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_SEE_NEXT_CARD;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.INFO_SHOW_CARD)){
                            //收到action
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_SHOW_CARD;
                            handler.sendMessage(message);
                        }else  if (recData[0].equals(Constant.RET_DO_ACTION)){
                            //收到action操作返回
                            if (timer!=null){
                                timer.cancel();
                            }
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"操作失败，错误原因为："+jsonReturn.getString("msg"));
                            }
                        }else  if (recData[0].equals(Constant.RET_LEAVE_SEAT)){
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"离开座位操作失败，错误原因为："+jsonReturn.getString("msg"));
                            }
                        }else  if (recData[0].equals(Constant.RET_HOLD_SEAT)){
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"保位留桌操作失败，错误原因为："+jsonReturn.getString("msg"));
                            }
                        }else  if (recData[0].equals(Constant.RET_DISPOSE_TABLE)){
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"解散牌桌操作失败，错误原因为："+jsonReturn.getString("msg"));
                            }
                        }else  if (recData[0].equals(Constant.RET_LEAVE_TABLE)){
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"离开牌桌操作失败，错误原因为："+jsonReturn.getString("msg"));
                            }
                        }else  if (recData[0].equals(Constant.RET_ADD_CHIP)){

                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"购买记分牌成功失败，错误原因为："+jsonReturn.getString("msg"));
                            }else{
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"购买记分牌成功！");
                            }
                            handler.sendEmptyMessage(MESSAGE_DISMISS_POPWINDOW);
                        } else  if (recData[0].equals(Constant.INFO_ADD_CHIPS)){
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_ADD_CHIPS;
                            handler.sendMessage(message);

                        }else  if (recData[0].equals(Constant.RET_BACK_SEAT)){

                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"回到座位操作失败，错误原因为："+jsonReturn.getString("msg"));
                            }else{

                            }

                        }else  if (recData[0].equals(Constant.RET_SHOW_CARD)){
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"show card操作失败，错误原因为："+jsonReturn.getString("msg"));
                            }

                        }else  if (recData[0].equals(Constant.RET_SEE_NEXT_CARD)){
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"SEE_NEXT_CARD操作失败，错误原因为："+jsonReturn.getString("msg"));
                            }

                        }else  if (recData[0].equals(Constant.RET_PAUSE_GAME)){
                            JSONObject jsonReturn=new JSONObject(recData[1]);
                            if (jsonReturn.getInt("ret")!=0){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"SEE_NEXT_CARD操作失败，错误原因为："+jsonReturn.getString("msg"));
                            }

                        } else  if (recData[0].equals(Constant.INFO_DO_END)){
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_DO_END;
                            handler.sendMessage(message);

                        }else  if (recData[0].equals(Constant.INFO_LEAVE_SEAT)){
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_LEAVE_SEAT;
                            handler.sendMessage(message);

                        }else  if (recData[0].equals(Constant.INFO_HOLD_SEAT)){
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_HOLD_SEAT;
                            handler.sendMessage(message);

                        }else  if (recData[0].equals(Constant.INFO_BACK_SEAT)){
                            Message message = Message.obtain();
                            message.obj = recData[1];
                            message.what = MESSAGE_INFO_BACK_SEAT;
                            handler.sendMessage(message);

                        }








                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"处理服务器返回的数据异常！");

                    }

                }

                @Override
                public void onSocketError() {
                    //socket异常，提示用户是否重连 弹出对话框，是否重连，不连接，则退出游戏界面
                    myBinder.connect(ip,port);

                }

                @Override
                public void onScoketConnectSuccess() {
                    //成功连接
                    if (flag){
                        flag=false;
                        if (operation==1){
                            //创建牌局
                            try {
                                String msg = "$" + Constant.GAME_CREATE_TABLE + "|";
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("userid", application.getUserId());
                                jsonObject.put("tableid", gameId);
                                msg+=jsonObject.toString().replace("$","￥");
                                myBinder.sendInfo(msg);
                            }catch (Exception e){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"创建牌局失败！");
                            }

                        }else if (operation==2){
                            //加入牌局,要判断自己是否还在牌局中
                            try {
                                String msg = "$" + Constant.GAME_ENTER_TABLE + "|";
                                JSONObject jsonSend = new JSONObject();
                                jsonSend.put("userid", application.getUserId());
                                jsonSend.put("tableid", gameId);
                                jsonSend.put("nickname",application.getUser().nickName);
                                jsonSend.put("headpic",application.getUser().userHeadPic);
                                msg+=jsonSend.toString().replace("$","￥");
                                myBinder.sendInfo(msg);
                            }catch (Exception e){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"加入牌局失败！");
                            }

                        }

                    }


                }


            });

            //绑定成功之后，进行socket连接操作,启动读，和传数据的接口
            myBinder.connect(ip,port);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        displayHelper = new DisplayHelper(this);
        //得到屏幕的真实像素宽度高度，用来计算位置
        mScreenWidth=displayHelper.getSCREEN_WIDTH_PIXELS();
        mScreenHeight=displayHelper.getSCREEN_HEIGHT_PIXELS();
        setContentView(R.layout.activity_game);

        application=(DzApplication)getApplication();
        mContext=getApplicationContext();
        notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        initViews();

        //获得传来的值
        /*intent.putExtra("operation",1);//1表示创建牌局，2表示加入牌局
                                intent.putExtra("gameid",ssid);
                                intent.putExtra("ip",ip);
                                intent.putExtra("port",port);*/
        Intent intent = getIntent();
        operation = intent.getIntExtra("operation",0);
        gameId= Integer.parseInt(intent.getStringExtra("gameid"));
        ip=intent.getStringExtra("ip");
        port= intent.getIntExtra("port",0);
        flag=true;
        //启动service ,绑定service
        Intent startIntent = new Intent(this, SocketService.class);
        startService(startIntent);
        bindService(startIntent, connection, BIND_AUTO_CREATE);
    }


    @SuppressLint("WrongViewCast")
    private void initViews(){

        layout_parent=(LinearLayout)findViewById(R.id.layout_main);
        //此处设置成clickable =false,在INFOtable中放开，怕接受服务器消息的时间差
        iv_voice=(ImageView)findViewById(R.id.iv_voice);
        iv_voice.setClickable(false);
        iv_info=(ImageView)findViewById(R.id.iv_info);
        iv_info.setClickable(false);
        iv_menu=(ImageView)findViewById(R.id.iv_menu);
        iv_menu.setClickable(false);

        iv_apply=(ImageView)findViewById(R.id.iv_apply);
        iv_apply.setClickable(false);
        iv_game_info=(ImageView)findViewById(R.id.iv_game_info);
        iv_game_info.setClickable(false);


        iv_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // getTakeInChipsFromServer();

               // ToastUtil.showToastInScreenCenter(GameActivity.this,"hahahahah1");
            }
        });

        iv_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //ToastUtil.showToastInScreenCenter(GameActivity.this,"hahahahah2");
            }
        });

        //点击弹出申请界面
        iv_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //先到后台去取数据
                //发送申请
                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try{
                            //拼装url字符串

                            DzApplication applicatoin=(DzApplication)getApplication();
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("userid",applicatoin.getUserId());
                            jsonObj.put("tableid",gameId);


                            String strURL=getString(R.string.url_remote);
                            strURL+="func=getrequesttakeininfo&param="+jsonObj.toString();

                            URL url = new URL(strURL);
                            Request request = new Request.Builder().url(strURL).build();
                            Response response = DzApplication.getHttpClient().newCall(request).execute();
                            String result=response.body().string();
                            //{"requesttakeininfo":[{"requestid":16,"requestuserid":1008,"usernickname":"我是无名","requesttakeinchips":200,"requestpermittakeinchips":200,"ispermit":2}]}
                            JSONObject jsonObject=new JSONObject(result);
                            mlistApplyInfo=new ArrayList<ApplyInfo>();
                            JSONArray jsonApplyArray=new JSONArray(jsonObject.getString("requesttakeininfo"));
                            for(int i=0;i<jsonApplyArray.length();i++){
                                JSONObject jsonApplyInfo=new JSONObject(jsonApplyArray.get(i).toString());
                                ApplyInfo applyInfo=new ApplyInfo();
                                applyInfo.tableId=gameId;
                                applyInfo.tablename=gameHouseName;
                                applyInfo.requestid=jsonApplyInfo.getInt("requestid");
                                applyInfo.requestuserid=jsonApplyInfo.getInt("requestuserid");
                                applyInfo.usernickname=jsonApplyInfo.getString("usernickname");
                                applyInfo.requesttakeinchips=jsonApplyInfo.getInt("requesttakeinchips");
                                applyInfo.requestpermittakeinchips=jsonApplyInfo.getInt("requestpermittakeinchips");
                                applyInfo.ispermit=jsonApplyInfo.getInt("ispermit");
                                mlistApplyInfo.add(applyInfo);
//                                mlistApplyInfo.add(applyInfo);
//                                mlistApplyInfo.add(applyInfo);
                            }
                            handler.sendEmptyMessage(MESSAGE_APPLY_INFO);

                        }catch (Exception e){
                            ToastUtil.showToastInScreenCenter(GameActivity.this,"查询申请带入异常，请稍后重试!"+e.toString());
                        }

                    }
                });
                thread.start();




            }
        });


        iv_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View contentView = getPopupMenuWindowContentView();
                //根据值来设置菜单
                //先判断是否坐下，根据muserindex
                if (getUserIndex()==-1){
                    //没有坐下
                    View view=contentView.findViewById(R.id.layout_4);
                    view.setVisibility(View.GONE);
                    view=contentView.findViewById(R.id.layout_5);
                    view.setVisibility(View.GONE);
                    view=contentView.findViewById(R.id.layout_6);
                    view.setVisibility(View.GONE);
                }
                if (mTableInfo.createuserid!=application.getUserId()){
                    View view=contentView.findViewById(R.id.layout_7);
                    view.setVisibility(View.GONE);
                }
                //规则说明
                TextView tv_1=(TextView)contentView.findViewById(R.id.tv_1);
                tv_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(MESSAGE_DISMISS_POPMENU);

                    }
                });

                //设置
                TextView tv_2=(TextView)contentView.findViewById(R.id.tv_2);
                tv_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(MESSAGE_DISMISS_POPMENU);

                    }
                });

                //商城
                TextView tv_3=(TextView)contentView.findViewById(R.id.tv_3);
                tv_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(MESSAGE_DISMISS_POPMENU);

                    }
                });

                //补充记分牌
                TextView tv_4=(TextView)contentView.findViewById(R.id.tv_4);
                tv_4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(MESSAGE_DISMISS_POPMENU);
                        //调用请求
                        isFirstBuyCore=false;
                        getTakeInChipsFromServer();

                    }
                });

                //站起围观
                TextView tv_5=(TextView)contentView.findViewById(R.id.tv_5);
                tv_5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* public class LeaveSeatParam
                        {
                            public int userid;
                            public int tableid;
                            public int seatindex;
                        }*/
                        handler.sendEmptyMessage(MESSAGE_DISMISS_POPMENU);
                        try{
                            //ToastUtil.showToastInScreenCenter(GameActivity.this,v.getTag().toString());
                            String msg = "$" + Constant.GAME_LEAVE_SEAT + "|";

                            JSONObject jsonSend = new JSONObject();
                            jsonSend.put("userid", application.getUserId());
                            jsonSend.put("tableid", gameId);
                            jsonSend.put("seatindex",getUserIndex());
                            msg+=jsonSend.toString().replace("$","￥");
                            myBinder.sendInfo(msg);
                        }catch (Exception e){
                            ToastUtil.showToastInScreenCenter(GameActivity.this,"离开座位出错！");

                        }


                    }
                });

                //保位留桌
                TextView tv_6=(TextView)contentView.findViewById(R.id.tv_6);
                tv_6.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(MESSAGE_DISMISS_POPMENU);
                        try{
                            //ToastUtil.showToastInScreenCenter(GameActivity.this,v.getTag().toString());
                            String msg = "$" + Constant.GAME_HOLD_SEAT + "|";

                            JSONObject jsonSend = new JSONObject();
                            jsonSend.put("userid", application.getUserId());
                            jsonSend.put("tableid", gameId);
                            jsonSend.put("seatindex",getUserIndex());
                            jsonSend.put("holdseconds",0);
                            msg+=jsonSend.toString().replace("$","￥");
                            myBinder.sendInfo(msg);
                        }catch (Exception e){
                            ToastUtil.showToastInScreenCenter(GameActivity.this,"离开座位出错！");

                        }


                    }
                });

                //解散房间
                TextView tv_7=(TextView)contentView.findViewById(R.id.tv_7);
                tv_7.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(MESSAGE_DISMISS_POPMENU);
                        new AlertDialog.Builder(GameActivity.this).setTitle("系统提示")//设置对话框标题

                                .setMessage("确认解散牌局？")//设置显示的内容

                                .setPositiveButton("确定",new DialogInterface.OnClickListener() {//添加确定按钮

                                    @Override

                                    public void onClick(DialogInterface dialog, int which) {//确定按钮的响应事件

                                        try{

                                            //ToastUtil.showToastInScreenCenter(GameActivity.this,v.getTag().toString());
                                            String msg = "$" + Constant.GAME_DISPOSE_TABLE + "|";
                                            JSONObject jsonSend = new JSONObject();
                                            jsonSend.put("userid", application.getUserId());
                                            jsonSend.put("tableid", gameId);
                                            msg+=jsonSend.toString().replace("$","￥");
                                            myBinder.sendInfo(msg);
                                        }catch (Exception e){
                                            ToastUtil.showToastInScreenCenter(GameActivity.this,"解散房间出错！");

                                        }
                                    }

                                }).setNegativeButton("返回",new DialogInterface.OnClickListener() {//添加返回按钮

                            @Override

                            public void onClick(DialogInterface dialog, int which) {//响应事件

                                // TODO Auto-generated method stub

                                Log.i("alertdialog"," 请保存数据！");

                            }

                        }).show();//在按键响应事件中显示此对话框


                    }
                });

                //退出牌局
                TextView tv_8=(TextView)contentView.findViewById(R.id.tv_8);
                tv_8.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try{
                            handler.sendEmptyMessage(MESSAGE_DISMISS_POPMENU);
                            //ToastUtil.showToastInScreenCenter(GameActivity.this,v.getTag().toString());
                            String msg = "$" + Constant.GAME_LEAVE_TABLE + "|";
                            JSONObject jsonSend = new JSONObject();
                            jsonSend.put("userid", application.getUserId());
                            jsonSend.put("tableid", gameId);
                            msg+=jsonSend.toString().replace("$","￥");
                            myBinder.sendInfo(msg);
                        }catch (Exception e){
                            ToastUtil.showToastInScreenCenter(GameActivity.this,"离开座位出错！");

                        }

                    }
                });



                int  width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
                int  height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
                contentView.measure(width,height);
                int height1=contentView.getMeasuredHeight();
                int  width1=contentView.getMeasuredWidth();
                popMenu = new PopupWindow(contentView,width1 ,height1 , true);

                popMenu.setFocusable(true);
                // 设置允许在外点击消失
                popMenu.setOutsideTouchable(true);
                // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
                popMenu.setBackgroundDrawable(new ColorDrawable());
                // 设置好参数之后再show
                popMenu.showAsDropDown(v);
                //popMenu.showAtLocation(GameActivity.this.getWindow().getDecorView(), Gravity.CENTER,0,0);

                //contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                //int xOffset = parent.getWidth()  - contentView.getMeasuredWidth() ;
                //popupWindow.showAsDropDown(parent,xOffset,20);    // 在mButton2的中间显示
               // int windowPos[] = calculatePopWindowPos((View)parent, contentView);
                //popupWindow.showAtLocation(GameActivity.this.getWindow().getDecorView(), Gravity.CENTER,0,0);
                // popupWindow.showAtLocation(parent,Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
            }
        });

        layout_game=(AbsoluteLayout)findViewById(R.id.layout_game);

        //先根据最大数目9来创建控件,此处，将创建和展示分开，9个座位可以先展示
        int maxNumber=9;

        iSeatValue=new int[maxNumber][2];
        iTipLocation=new int[maxNumber][2];//大小盲注,用户操作的展现位置
        iAmountChipLocation=new int[maxNumber][2];//已下注金额的展现位置
        iD=new int[maxNumber][2];//庄家D位置
        iCardBack=new int[maxNumber][2];//有牌的用户背景

        initSeatXY(maxNumber);

        for(int i=0;i<maxNumber;i++) {

            //xml中写死了宽度和高度，忽略名字长短引起的变化

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.layout_seat, null);
            //iv_user_head  //tv_user_name tv_goldcoin
            ImageView iv = (ImageView) view.findViewById(R.id.iv_user_head);
            TextView tv_name = (TextView) view.findViewById(R.id.tv_user_name);
            TextView tv_goldcoin = (TextView) view.findViewById(R.id.tv_goldcoin);
            tv_name.setVisibility(View.INVISIBLE);//INVISIBLE继续占用布局空间
            tv_goldcoin.setVisibility(View.INVISIBLE);
            iv.setImageBitmap(getEmptySeatBitMap(80, 80, "坐下"));
            iv.setTag(i + "");
            //此处响应事件在需要的时候加
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //给空位置加上响应，这个后面做坐下的时候来处理
//                    int iRealIndex;
//                    if (mUserIndex==-1){
//                        iRealIndex=(int)v.getTag();
//                    }else{
//                        iRealIndex=((int)v.getTag()-mUserIndex+mTableInfo.seats.length)%mTableInfo.seats.length;
//                    }

                    //发送坐下
                    try{
                        //ToastUtil.showToastInScreenCenter(GameActivity.this,v.getTag().toString());
                        String msg = "$" + Constant.GAME_SIT_SEAT + "|";
                        /* public class SitSeatParam
                        {
                            public int userid;
                            public int tableid;
                            public int seatindex;
                            public int intochips;
                            public string ip;
                            public double gpsx;
                            public double gpsy;
                        }*/

                        JSONObject jsonSend = new JSONObject();
                        jsonSend.put("userid", application.getUserId());
                        jsonSend.put("tableid", gameId);
                        mWantSeatIndex=Integer.parseInt((String) v.getTag());
                        jsonSend.put("seatindex",mWantSeatIndex);
                        jsonSend.put("intochips",0);
                        jsonSend.put("ip",Util.getLocalIpAddress());
                        jsonSend.put("gpsx",application.getLatitude());
                        jsonSend.put("gpsy",application.getLongitude());
                        msg+=jsonSend.toString().replace("$","￥");
                        myBinder.sendInfo(msg);
                    }catch (Exception e){
                        ToastUtil.showToastInScreenCenter(GameActivity.this,"坐下座位出错！");

                    }

                }
            });
            setPosition(view,mSeatViewWidth,mSeatViewHeight,iSeatValue[i][0],iSeatValue[i][1]);

            view.setTag(i + "");
            layout_game.addView(view);
            mSeatObjects.put(i,view);//记录view对象
        }

        //开始按钮
        addStartButon();
        //回到座位按钮
        addReturnSeatButton();
        //信息布局
        addLayoutMessage();
        //增加5张公牌
        addGameCards();
//        mGameCards.setVisibility(View.VISIBLE);
        ImageView iv7=(ImageView)mGameCards.findViewById(R.id.iv_card1);
        mGameCardsObjects.put(0,iv7);
        ImageView iv8=(ImageView)mGameCards.findViewById(R.id.iv_card2);
        mGameCardsObjects.put(1,iv8);
        ImageView iv9=(ImageView)mGameCards.findViewById(R.id.iv_card3);
        mGameCardsObjects.put(2,iv9);
        ImageView iv10=(ImageView)mGameCards.findViewById(R.id.iv_card4);
        mGameCardsObjects.put(3,iv10);
        ImageView iv11=(ImageView)mGameCards.findViewById(R.id.iv_card5);
        mGameCardsObjects.put(4,iv11);


        // 增加自己的牌
        addMyCards();
        ImageView iv1=(ImageView)mMyCards.findViewById(R.id.iv_card1);
        mMyCardsObjects.put(0,iv1);
        ImageView iv2=(ImageView)mMyCards.findViewById(R.id.iv_card2);
        mMyCardsObjects.put(1,iv2);
        ImageView iv3=(ImageView)mMyCards.findViewById(R.id.iv_card3);
        mMyCardsObjects.put(2,iv3);
        ImageView iv4=(ImageView)mMyCards.findViewById(R.id.iv_card4);
        mMyCardsObjects.put(3,iv4);

        //增加赢家翻牌
        addshowcards();
        mShowCards.setVisibility(View.INVISIBLE);
//        setPosition(mShowCards,120,80,iSeatValue[2][0],iSeatValue[2][1]+mNameTextHeight);
//        ImageView iv5=(ImageView)mShowCards.findViewById(R.id.iv_card1);
//        iv5.setImageBitmap(drawSingleCard(3,11));
//        ImageView iv6=(ImageView)mShowCards.findViewById(R.id.iv_card2);
//        iv6.setImageBitmap(drawSingleCard(2,9));

        //加筹码位置
        for (int i=0;i<maxNumber;i++){
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View viewChip = inflater.inflate(R.layout.layout_chip, null);
            TextView tv=(TextView)viewChip.findViewById(R.id.tv_chip);
            tv.setText("000");
            setPosition(viewChip,mAmountChipViewWidth,mAmountChipVieHeight,iAmountChipLocation[i][0],iAmountChipLocation[i][1]);
            layout_game.addView(viewChip);
            mChipObjects.put(i,viewChip);
            viewChip.setVisibility(View.INVISIBLE);
        }

        //cardback
        for (int i=0;i<maxNumber;i++){

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.layout_cardback, null);
            ImageView ivCardBack=(ImageView)view.findViewById(R.id.iv_cardback);
            ivCardBack.setImageBitmap(drawCardBack());
            setPosition(view,mCardBackWidth,mCardBackHeight, iCardBack[i][0],iCardBack[i][1]);
            layout_game.addView(view);
            mCardBackObjects.put(i,view);
            view.setVisibility(View.INVISIBLE);
        }


        //tip
        for (int i=0;i<maxNumber;i++){

            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.layout_tip, null);
            TextView tv_tip=(TextView)view.findViewById(R.id.tv_tip);
            tv_tip.setText("盲注");
            setPosition(view,mTipViewWidth,mTipViewHeight,iTipLocation[i][0],iTipLocation[i][1]);
            layout_game.addView(view);
            mTipObjects.put(i,view);
            view.setVisibility(View.INVISIBLE);
        }

//        addTimer(2,60);
//        addTimer(6,30);
//        mTimerObjects.get(2).start();
//        mTimerObjects.get(6).start();



        //庄家位
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mD = inflater.inflate(R.layout.layout_button, null);
        layout_game.addView(mD);
        mD.setVisibility(View.INVISIBLE);

//        显示庄家位
//        mD.setVisibility(View.VISIBLE);
//        setPosition(mD,mDViewWidth,mDViewHeight,iD[3][0],iD[3][1]);

        //用户操作的按钮view
        addActionView();
        //底池显示
        addPoolView();

    }


    private void addTimer ( final int seatindex,int timelen){
        TextView tv=new TextView(this);
        tv.setGravity(Gravity.CENTER);
        tv.setText(timelen+"");
        tv.setTextColor(Color.RED);
        layout_game.addView(tv);
        setPosition(tv,mHeadImageWidth,mHeadImageHeight,iSeatValue[seatindex][0]+(mSeatViewWidth-mHeadImageWidth)/2,iSeatValue[seatindex][1]+mNameTextHeight);
        mTimerViewObjects.put(seatindex,tv);

        CountDownTimer timer = new CountDownTimer(timelen*1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                ((TextView) (mTimerViewObjects.get(seatindex))).setText(millisUntilFinished / 1000+"");

            }

            @Override
            public void onFinish() {
                ((TextView) (mTimerViewObjects.get(seatindex))).setVisibility(View.GONE);
            }
        };
        timer.start();
        mTimerObjects.put(seatindex,timer);



    }

    //画空座位的图
    public Bitmap getEmptySeatBitMap(int width,int height,String text) {

        //边框：#92a8bd  背景：#20364e
        //文字颜色#8ba6c1
        int line_width=6;//设置画线的宽度
        Bitmap newBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(newBitmap);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(line_width);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#92a8bd"));
        canvas.drawCircle(width/2,height/2,width/2-line_width/2,paint);

        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setStrokeWidth(line_width);
        paint2.setStyle(Paint.Style.FILL_AND_STROKE);
        paint2.setColor(Color.parseColor("#20364e"));
        canvas.drawCircle(width/2,height/2,width/2-line_width,paint2);
        //canvas.drawBitmap(bmp, 0, 0, null);
        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(24.0F);
        textPaint.setColor(Color.parseColor("#8ba6c1"));
        //canvas.drawText();text是左下角的坐标为画的起始值

        float textWidth = textPaint.measureText(text);
        Paint.FontMetrics fm = textPaint.getFontMetrics();
        float textHeight = (int) Math.ceil(fm.leading - fm.ascent) - 2;
        canvas.drawText(text,width/2-textWidth/2, height/2+textHeight/2,textPaint);
        return newBitmap;
    }

    // 收到tableinfo之后，初始化牌桌的信息
    private void InitGameInfo(){

//        if (!bInitView){
//            bInitView=true;
//        }


        //先根据收到的table信息初始化桌面的控件
        //用户在创建table，或者加入别人的table，或者中途退出进入，从非活动转为活动会收到信息
        int iSeatnumber=mTableInfo.seats.length;//牌局座位数目，其中对象可以为空

//        if (!bInitView) {
            initSeatXY(iSeatnumber);//根据人数来设定座位的位置
            //重新设置座位位置  （筹码位置，D的位置，tip的位置，cardback ，翻牌位置在需要的时候设置）
            if (iSeatnumber != 9) {
                //如果不为9，重新设置控件的位置,座位的显示，chip，tip，cardback的显示位置
                for (int i = 0; i < iSeatnumber; i++) {
                    View view = mSeatObjects.get(iSeatnumber);
                    AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(mSeatViewWidth, mSeatViewHeight, 0, 0);
                    lp.x = iSeatValue[iSeatnumber][0];
                    lp.y = iSeatValue[iSeatnumber][1];
                    view.setLayoutParams(lp);

                }
                //chip
                for (int i = 0; i < iSeatnumber; i++) {
                    View view = mChipObjects.get(iSeatnumber);
                    AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(mAmountChipViewWidth, mAmountChipVieHeight, 0, 0);
                    lp.x = iAmountChipLocation[iSeatnumber][0];
                    lp.y = iAmountChipLocation[iSeatnumber][1];
                    view.setLayoutParams(lp);

                }

                //tip
                for (int i = 0; i < iSeatnumber; i++) {
                    View view = mTipObjects.get(iSeatnumber);
                    AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(mTipViewWidth, mTipViewHeight, 0, 0);
                    lp.x = iTipLocation[iSeatnumber][0];
                    lp.y = iTipLocation[iSeatnumber][1];
                    view.setLayoutParams(lp);

                }
                //cardback都需要设置
                for (int i = 0; i < iSeatnumber; i++) {
                    View view = mCardBackObjects.get(iSeatnumber);
                    AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(mCardBackWidth, mCardBackHeight, 0, 0);
                    lp.x = iCardBack[iSeatnumber][0];
                    lp.y = iCardBack[iSeatnumber][1];
                    view.setLayoutParams(lp);
                }
            }
//        }


        //底池
        for(int i=0;i<mTableInfo.pots.length;i++) {
            View view;
            TextView textView;

            switch (i) {
                case 0:
                    if (mTableInfo.pots[i]>0) {
                        view = findViewById(R.id.layout_pool1);
                        view.setVisibility(View.VISIBLE);
                        textView = (TextView) findViewById(R.id.tv_pool1);
                        textView.setText(mTableInfo.pots[i] + "");
                    }
                    break;
                case 1:
                    if (mTableInfo.pots[i]>0) {
                        view = findViewById(R.id.layout_pool2);
                        view.setVisibility(View.VISIBLE);
                        textView = (TextView) findViewById(R.id.tv_pool2);
                        textView.setText(mTableInfo.pots[i] + "");
                    }
                    break;
                case 2:
                    if (mTableInfo.pots[i]>0) {
                        view = findViewById(R.id.layout_pool3);
                        view.setVisibility(View.VISIBLE);
                        textView = (TextView) findViewById(R.id.tv_pool3);
                        textView.setText(mTableInfo.pots[i] + "");
                    }
                    break;
                case 3:
                    if (mTableInfo.pots[i]>0) {
                        view = findViewById(R.id.layout_pool4);
                        view.setVisibility(View.VISIBLE);
                        textView = (TextView) findViewById(R.id.tv_pool2);
                        textView.setText(mTableInfo.pots[i] + "");
                    }
                    break;
                case 4:
                    if (mTableInfo.pots[i]>0) {
                        view = findViewById(R.id.layout_pool5);
                        view.setVisibility(View.VISIBLE);
                        textView = (TextView) findViewById(R.id.tv_pool5);
                        textView.setText(mTableInfo.pots[i] + "");
                    }
                    break;
                case 5:
                    if (mTableInfo.pots[i]>0) {
                        view = findViewById(R.id.layout_pool6);
                        view.setVisibility(View.VISIBLE);
                        textView = (TextView) findViewById(R.id.tv_pool6);
                        textView.setText(mTableInfo.pots[i] + "");
                    }
                    break;
                case 6:
                    if (mTableInfo.pots[i]>0) {
                        view = findViewById(R.id.layout_pool7);
                        view.setVisibility(View.VISIBLE);
                        textView = (TextView) findViewById(R.id.tv_pool7);
                        textView.setText(mTableInfo.pots[i] + "");
                    }
                    break;
                case 7:
                    if (mTableInfo.pots[i]>0) {
                        view = findViewById(R.id.layout_pool8);
                        view.setVisibility(View.VISIBLE);
                        textView = (TextView) findViewById(R.id.tv_pool8);
                        textView.setText(mTableInfo.pots[i] + "");
                    }
                    break;
                case 8:
                    if (mTableInfo.pots[i]>0) {
                        view = findViewById(R.id.layout_pool9);
                        view.setVisibility(View.VISIBLE);
                        textView = (TextView) findViewById(R.id.tv_pool9);
                        textView.setText(mTableInfo.pots[i] + "");
                    }
                    break;
            }

        }
        //公牌
        mGameCards.setVisibility(View.VISIBLE);
        for(int i=0;i<mTableInfo.comunitycards.length;i++) {
            ImageView iv;
            if (mTableInfo.comunitycards[i]!=null){

                switch (i) {
                    case 0:
                        iv=(ImageView)mGameCards.findViewById(R.id.iv_card1);
                        iv.setVisibility(View.VISIBLE);
                        iv.setImageBitmap(drawSingleCard(mTableInfo.comunitycards[i].suit,mTableInfo.comunitycards[i].member));

                        break;
                    case 1:
                        iv=(ImageView)mGameCards.findViewById(R.id.iv_card2);
                        iv.setVisibility(View.VISIBLE);
                        iv.setImageBitmap(drawSingleCard(mTableInfo.comunitycards[i].suit,mTableInfo.comunitycards[i].member));
                        break;
                    case 2:
                        iv=(ImageView)mGameCards.findViewById(R.id.iv_card3);
                        iv.setVisibility(View.VISIBLE);
                        iv.setImageBitmap(drawSingleCard(mTableInfo.comunitycards[i].suit,mTableInfo.comunitycards[i].member));
                        break;
                    case 3:
                        iv=(ImageView)mGameCards.findViewById(R.id.iv_card4);
                        iv.setVisibility(View.VISIBLE);
                        iv.setImageBitmap(drawSingleCard(mTableInfo.comunitycards[i].suit,mTableInfo.comunitycards[i].member));
                        break;
                    case 4:
                        iv=(ImageView)mGameCards.findViewById(R.id.iv_card5);
                        iv.setVisibility(View.VISIBLE);
                        iv.setImageBitmap(drawSingleCard(mTableInfo.comunitycards[i].suit,mTableInfo.comunitycards[i].member));
                        break;
                }
            }

        }

        //先判断用户本身在不在座位中,同时记录 用户座位映射
        for ( int ii=0;ii<mTableInfo.players.length;ii++){
            GameUser gameUser=new GameUser();
            gameUser.userId=mTableInfo.players[ii].userid;
            gameUser.nickName=mTableInfo.players[ii].nickname;
            gameUser.remainchips=mTableInfo.players[ii].remainchips;
            gameUser.userHeadPic=mTableInfo.players[ii].headpic;
            gameUser.seatindex=mTableInfo.players[ii].seatindex;
            if (!mGameUser.containsKey(gameUser.userId)){
                mGameUser.put(gameUser.userId,gameUser);
            }

        }

        //如果用户在座位中，则先移位置,将用户的座位信息，下注信息，提示信息，cardback信息，做偏移
        int userindex=getUserIndex();
        if (userindex!=-1){
            //用户在座位中，则移位置
            for (int i = 0; i < iSeatnumber; i++) {
                int moveto= (i-userindex+iSeatnumber)%iSeatnumber;
                setPosition(mSeatObjects.get(i),mSeatViewWidth,mSeatViewHeight,iSeatValue[moveto][0],iSeatValue[moveto][1]);
                setPosition(mChipObjects.get(i),mAmountChipViewWidth,mAmountChipVieHeight,iAmountChipLocation[moveto][0],iAmountChipLocation[moveto][1]);
                setPosition(mTipObjects.get(i),mTipViewWidth,mTipViewHeight,iTipLocation[moveto][0],iTipLocation[moveto][1]);
                setPosition(mCardBackObjects.get(i),mCardBackWidth,mCardBackHeight,iCardBack[moveto][0],iCardBack[moveto][1]);
            }
        }

        //先根据用户状态来画座位
        for(int i=0;i<iSeatnumber;i++) {
            if (mTableInfo.seats[i]!=null){
                //来计算，该位置的对象应该在本地位置

                int userId=mTableInfo.seats[i].userid;
                for (int j=0;j<mTableInfo.players.length;j++){
                    if (mTableInfo.players[j].userid==userId){
                        int iRealIndex=i;
                        if (userindex!=-1) {
                            //计算偏移位置
                            iRealIndex=(mTableInfo.players[j].seatindex- userindex+iSeatnumber)%(iSeatnumber);
                        }
                        //设置头像，金币值，名字
                        //此处不判断seatindex的值，因为是根据座位中的userid来的
                        View view=mSeatObjects.get(mTableInfo.players[j].seatindex);
                        ImageView iv = (ImageView) view.findViewById(R.id.iv_user_head);
                        TextView tv_name = (TextView) view.findViewById(R.id.tv_user_name);
                        TextView tv_goldcoin = (TextView) view.findViewById(R.id.tv_goldcoin);

                        tv_name.setText(mTableInfo.players[j].nickname);
                        tv_name.setVisibility(View.VISIBLE);

                        tv_goldcoin.setText(mTableInfo.players[j].remainchips+"");
                        tv_goldcoin.setVisibility(View.VISIBLE);

                        if (mTableInfo.players[j].headpic!=null && !mTableInfo.players[j].headpic.equals("")) {
                            Picasso.with(getApplicationContext())
                                    .load(mTableInfo.players[j].headpic)
                                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                                    .resize(80, 80)
                                    .error(R.drawable.seat_empty)
                                    .transform(new CircleTransform())
                                    .into(iv);
                        }
                        iv.setClickable(false);
//                        if (mTableInfo.seats[i].isbb) {
//                            //Tip为大盲
//                            View viewTip=  mTipObjects.get(iRealIndex);
//                            viewTip.setVisibility(View.VISIBLE);
//                            TextView tv_tip=(TextView)view.findViewById(R.id.tv_tip);
//                            tv_tip.setText("大盲");
//                        }
//                        if (mTableInfo.seats[i].issb) {
//                            //Tip为小盲
//                            View viewTip=  mTipObjects.get(iRealIndex);
//                            viewTip.setVisibility(View.VISIBLE);
//                            TextView tv_tip=(TextView)view.findViewById(R.id.tv_tip);
//                            tv_tip.setText("小盲");
//                        }

                        //庄家位，
                        if (mTableInfo.seats[i].isbutton) {
                            setPosition(mD,mDViewWidth,mDViewHeight,iD[iRealIndex][0],iD[iRealIndex][1]);
                            mD.setVisibility(View.VISIBLE);
                        }

                        //state  显示tip 等待，或者显示

                        if (mTableInfo.seats[i].state==1) {
                            //Tip为大盲
                            View viewTip=  mTipObjects.get(mTableInfo.players[j].seatindex);
                            viewTip.setVisibility(View.VISIBLE);
                            TextView tv_tip=(TextView)viewTip.findViewById(R.id.tv_tip);
                            tv_tip.setText("等待");
                        }

                        if (mTableInfo.seats[i].state==3) {
                            //等待回来倒计时
                            addTimer(iRealIndex,mTableInfo.seats[i].needholdtime);
                            if (mTableInfo.players[j].userid==application.getUserId()){
                                mReturnSeat.setVisibility(View.VISIBLE);
                                //
                            }

                        }

                        //根据用户信息来判断


                        View viewTip=  mTipObjects.get(mTableInfo.players[j].seatindex);
                        TextView tv_tip=(TextView)viewTip.findViewById(R.id.tv_tip);

                        /*public enum PlayAction
    {
        Bet,    //押注 - 押上筹码
        Call,   //跟进 - 跟随众人押上同等的注额
        Fold,   //收牌 / 不跟 - 放弃继续牌局的机会
        Check,  // 让牌 - 在无需跟进的情况下选择把决定“让”给下一位
        Raise,  // 加注 - 把现有的注金抬高
        Reraise,// 再加注 - 再别人加注以后回过来再加注
        Allin,   //全押 - 一次过把手上的筹码全押上
        NoAction,
        SB,
        BB,
        Straddle,
        ADDBB,  //补盲新加入局的玩家，补一个大盲。
        ANTE,

    }*/

                        switch (mTableInfo.players[j].lastplayaction){
                            case 0:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("下注");
                                break;
                            case 1:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("跟注");
                                break;
                            case 2:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("弃牌");
                                break;
                            case 3:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("看牌");
                                break;
                            case 4:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("加注");
                                break;
                            case 6:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("All In");
                                break;
                            case 7:
                                viewTip.setVisibility(View.INVISIBLE);
                                tv_tip.setText("");
                                break;
                            case 8:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("小盲");
                                break;
                            case 9:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("大盲");
                                break;
                            case 10:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("Straddle");
                                break;
                            case 11:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("补盲");
                                break;
                            case 12:
                                viewTip.setVisibility(View.VISIBLE);
                                tv_tip.setText("Ante");
                                break;
                        }

                        //已下注
                        if (mTableInfo.players[j].amountchips>0){
                            View  viewChip=mChipObjects.get(mTableInfo.players[j].seatindex);
                            TextView tv=(TextView)viewChip.findViewById(R.id.tv_chip);
                            tv.setText(mTableInfo.players[j].amountchips+"");
                        }


                        //此处要重点测试一下
                        if(mTableInfo.players[j].needwaitactiontime>0){
                            addTimer(iRealIndex,mTableInfo.players[j].needwaitactiontime);
                            if (mTableInfo.players[j].userid==application.getUserId()){
                                mAction.setVisibility(View.VISIBLE);
                                mButtonRaise.setVisibility(View.INVISIBLE);
                                mTVRaise.setVisibility(View.INVISIBLE);
                                mButtonCheck.setVisibility(View.INVISIBLE);
                                mTVCheck.setVisibility(View.INVISIBLE);
                                mButtonFold.setVisibility(View.INVISIBLE);
                                mTVFold.setVisibility(View.INVISIBLE);

                                List<Integer> operate=mTableInfo.players[j].waitactionparam.needaction;
                                maxChip=mTableInfo.players[j].remainchips;
                                Iterator it = operate.iterator();
                                while(it.hasNext()) {
                                    int operateValue=(int)it.next();
                                    switch (operateValue) {
                                        case 0:
                                            mButtonRaise.setVisibility(View.VISIBLE);
                                            mTVRaise.setVisibility(View.VISIBLE);
                                            mTVRaise.setText(" bet");
                                            minChip=1;


                                            break;
                                        case 1:
                                            mButtonCheck.setVisibility(View.VISIBLE);
                                            mTVCheck.setVisibility(View.VISIBLE);
                                            mTVCheck.setText("跟注");
                                            break;
                                        case 2:
                                            mButtonFold.setVisibility(View.VISIBLE);
                                            mTVFold.setVisibility(View.VISIBLE);
                                            mTVFold.setText("弃牌");
                                            break;

                                        case 3:
                                            mButtonCheck.setVisibility(View.VISIBLE);
                                            mTVCheck.setVisibility(View.VISIBLE);
                                            mTVCheck.setText("看牌");
                                            break;
                                        case 4:
                                            mButtonRaise.setVisibility(View.VISIBLE);
                                            mTVRaise.setVisibility(View.VISIBLE);
                                            mTVRaise.setText("加注");
                                            maxpaidchips=mTableInfo.players[j].waitactionparam.maxpaidchips;

                                            minChip=maxpaidchips-mTableInfo.players[j].amountchips;;
                                            break;
                                        case 6:
                                            mButtonRaise.setVisibility(View.VISIBLE);
                                            mTVRaise.setVisibility(View.VISIBLE);
                                            mTVRaise.setText("AllIn");
                                            break;
                                    }
                                }

                            }

                        }


                        //有牌
                        if(mTableInfo.players[j].cards!=null  &&  mTableInfo.players[j].cards.length>0){
                            //用户本身有牌，显示
                            if (mTableInfo.players[j].userid==application.getUserId()){
                                for (int k=0;k<mTableInfo.players[j].cards.length;k++){
                                    ImageView ivMyCards;
                                    switch (i) {
                                        case 0:
                                            iv=(ImageView)mMyCards.findViewById(R.id.iv_card1);
                                            iv.setVisibility(View.VISIBLE);
                                            iv.setImageBitmap(drawSingleCard(mTableInfo.players[j].cards[k].suit,mTableInfo.players[j].cards[k].member));

                                            break;
                                        case 1:
                                            iv=(ImageView)mMyCards.findViewById(R.id.iv_card2);
                                            iv.setVisibility(View.VISIBLE);
                                            iv.setImageBitmap(drawSingleCard(mTableInfo.players[j].cards[k].suit,mTableInfo.players[j].cards[k].member));
                                            break;
                                        case 2:
                                            iv=(ImageView)mMyCards.findViewById(R.id.iv_card3);
                                            iv.setVisibility(View.VISIBLE);
                                            iv.setImageBitmap(drawSingleCard(mTableInfo.players[j].cards[k].suit,mTableInfo.players[j].cards[k].member));
                                            break;
                                        case 3:
                                            iv=(ImageView)mMyCards.findViewById(R.id.iv_card4);
                                            iv.setVisibility(View.VISIBLE);
                                            iv.setImageBitmap(drawSingleCard(mTableInfo.players[j].cards[k].suit,mTableInfo.players[j].cards[k].member));
                                            break;
                                    }
                                }
                            }else{
                                //显示cardback
                                View viewCardBack=  mCardBackObjects.get(mTableInfo.players[j].seatindex);
                                viewCardBack.setVisibility(View.VISIBLE);

                            }
                        }
                    }
                }
            }
        }
        /*public enum TableState
     {
        prepare,
        start,
        pause,
        stop,
        timeout
     }*/
        //根据牌局状态来判断
        switch (mTableInfo.state){
            case 0:
                if (mTableInfo.createuserid==application.getUserId()){
                    btnStartGame.setVisibility(View.VISIBLE);

                }else{
                    mMessage.setVisibility(View.VISIBLE);
                    TextView tv_message=(TextView)mMessage.findViewById(R.id.tv_message);
                    tv_message.setText("等待房主开始游戏");

                }

                break;
            case 2:
                if (mTableInfo.createuserid==application.getUserId()){
                    btnStartGame.setVisibility(View.VISIBLE);

                }else{
                    mMessage.setVisibility(View.VISIBLE);
                    TextView tv_message=(TextView)mMessage.findViewById(R.id.tv_message);
                    tv_message.setText("游戏暂停中，等待房主开始游戏");

                }
                break;

        }



//






            //此段代码不用，因为写死了不需要动态判断宽度和高度
//            int  width =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//            int  height =View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
//            view.measure(width,height);
//            int height1=view.getMeasuredHeight();
//            int  width1=view.getMeasuredWidth();

    }

    private void addStartButon(){
        btnStartGame=new Button(this);
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(226, 60, 0, 0);
        lp.x =(mScreenWidth-226)/2;
        lp.y = (mScreenHeight-60)/2;
        btnStartGame.setBackgroundResource(R.drawable.startgame);
        btnStartGame.setLayoutParams(lp);
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GAME_START_TABLE
                try {
                    String msg = "$" + Constant.GAME_START_TABLE + "|";
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("userid", application.getUserId());
                    jsonObject.put("tableid", gameId);
                    msg+=jsonObject.toString().replace("$","￥");
                    myBinder.sendInfo(msg);
                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(GameActivity.this,"开始牌局失败！");
                }


            }
        });
        layout_game.addView(btnStartGame);
        btnStartGame.setVisibility(View.INVISIBLE);
    }

    private void addPoolView(){
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(mPoolWidth, mPoolHeight, 0, 0);
        lp.x =(mScreenWidth-mPoolWidth)/2;
        lp.y = mSeatViewHeight+mAmountChipVieHeight+20;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.layout_pool, null);
        view.setLayoutParams(lp);
        layout_game.addView(view);
        //将底池，边池的控件设为不可见
        LinearLayout layout_pool1=(LinearLayout)findViewById(R.id.layout_pool1);
        layout_pool1.setVisibility(View.INVISIBLE);
        mPoolObjects.put(0,layout_pool1);
        LinearLayout layout_pool2=(LinearLayout)findViewById(R.id.layout_pool2);
        layout_pool2.setVisibility(View.INVISIBLE);
        mPoolObjects.put(1,layout_pool2);
        LinearLayout layout_pool3=(LinearLayout)findViewById(R.id.layout_pool3);
        layout_pool3.setVisibility(View.INVISIBLE);
        mPoolObjects.put(2,layout_pool3);
        LinearLayout layout_pool4=(LinearLayout)findViewById(R.id.layout_pool4);
        layout_pool4.setVisibility(View.INVISIBLE);
        mPoolObjects.put(3,layout_pool4);
        LinearLayout layout_pool5=(LinearLayout)findViewById(R.id.layout_pool5);
        layout_pool5.setVisibility(View.INVISIBLE);
        mPoolObjects.put(4,layout_pool5);
        LinearLayout layout_pool6=(LinearLayout)findViewById(R.id.layout_pool6);
        layout_pool6.setVisibility(View.INVISIBLE);
        mPoolObjects.put(5,layout_pool6);
        LinearLayout layout_pool7=(LinearLayout)findViewById(R.id.layout_pool7);
        layout_pool7.setVisibility(View.INVISIBLE);
        mPoolObjects.put(6,layout_pool7);
        LinearLayout layout_pool8=(LinearLayout)findViewById(R.id.layout_pool8);
        layout_pool8.setVisibility(View.INVISIBLE);
        mPoolObjects.put(7,layout_pool8);
        LinearLayout layout_pool9=(LinearLayout)findViewById(R.id.layout_pool9);
        layout_pool9.setVisibility(View.INVISIBLE);
        mPoolObjects.put(8,layout_pool9);
        //textview
        TextView textView=(TextView)findViewById(R.id.tv_pool1);
        mPoolOTextbjects.put(0,textView);
        textView=(TextView)findViewById(R.id.tv_pool2);
        mPoolOTextbjects.put(1,textView);
        textView=(TextView)findViewById(R.id.tv_pool3);
        mPoolOTextbjects.put(2,textView);
        textView=(TextView)findViewById(R.id.tv_pool4);
        mPoolOTextbjects.put(3,textView);
        textView=(TextView)findViewById(R.id.tv_pool5);
        mPoolOTextbjects.put(4,textView);
        textView=(TextView)findViewById(R.id.tv_pool6);
        mPoolOTextbjects.put(5,textView);
        textView=(TextView)findViewById(R.id.tv_pool7);
        mPoolOTextbjects.put(6,textView);
        textView=(TextView)findViewById(R.id.tv_pool8);
        mPoolOTextbjects.put(7,textView);
        textView=(TextView)findViewById(R.id.tv_pool9);
        mPoolOTextbjects.put(8,textView);

    }

    private void addActionView(){
        addActionButon();
        addChipSelector();

        //增加按钮处理事件
        mButtonRaise.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = event.getY();
                float x = event.getX();
                final int action = event.getActionMasked();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        downX = x;
                        downY = y;
                        currentY=(int)y;
                        textMoveLayout.setVisibility(View.VISIBLE);
                        button_bottom.setVisibility(View.VISIBLE);
                        button_bottom.setText(minChip+"");
                        button_top.setText(maxChip+"");
                        button_middle.setVisibility(View.GONE);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (currentY-y>20 || y-currentY>20){
                            if (mp!=null){
                                mp.stop();
                                mp.reset();
                                mp=MediaPlayer.create(GameActivity.this,R.raw.slider);
                                //mp.setLooping(true);
                                //mp.prepare();
                                mp.start();


                            }
                            currentY=(int)y;
                        }


//                        mp.stop();
//                        mp.reset();
//                        mp.set(R.raw.slider);
//                        mediaPlayer.prepare();
//                        mediaPlayer.start();
//                        mp=MediaPlayer.create(GameActivity.this,R.raw.slider);
//                        mp.start();
                        if (y>downY)
                        {
                            button_bottom.setVisibility(View.VISIBLE);
                            button_middle.setVisibility(View.GONE);
                            actionChip=minChip;

                        }else{
                            if (downY-y>350){

                                button_bottom.setVisibility(View.GONE);
                                button_middle.setText("All In");
                                button_middle.layout(0,0,90,50);
                                actionChip=maxChip;

                            }else {
                                button_bottom.setVisibility(View.GONE);
                                button_middle.setVisibility(View.VISIBLE);
                                button_top.setText(maxChip+"");
                                float value=(downY-y)*(maxChip-minChip)/350;
                                actionChip=(int)value;
                                button_middle.setText((int)value+"");
                                button_middle.layout(0,(int)(350-(downY-y)),90,(int)(400-(downY-y)));
                            }
                        }

                        break;

                    case MotionEvent.ACTION_UP:
                        //
                        if(actionChip==maxChip) {
                            mp=MediaPlayer.create(GameActivity.this,R.raw.slider_top);
                            mp.start();
                        }
                        textMoveLayout.setVisibility(View.GONE);
                        if (actionChip>minChip) {
                            try {
                                //按钮上有两种状态，bet raise  ，bet的时候可以下全部，但是action是bet，raise的时候，allin 要发allin，amountchips要差值
                                String msg = "$" + Constant.GAME_DO_ACTION + "|";
                                JSONObject jsonSend = new JSONObject();
                                jsonSend.put("userid", application.getUserId());
                                jsonSend.put("seatindex", getUserIndex());
                                if (mTVRaise.getText().toString().equals("加注")) {
                                    if (actionChip == maxChip) {
                                        jsonSend.put("action", 6);
                                    } else {
                                        jsonSend.put("action", 4);
                                    }
                                }else{
                                    jsonSend.put("action", 0);
                                }
                                jsonSend.put("amountchips", actionChip-minChip);
                                jsonSend.put("tableid", gameId);
                                msg += jsonSend.toString().replace("$", "￥");
                                myBinder.sendInfo(msg);
                            } catch (Exception e) {
                                ToastUtil.showToastInScreenCenter(GameActivity.this, "操作出错！");
                            }
                        }
                        //发送下注
                        break;

                }
                return true;

            }
        });

        mButtonFold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弃牌按钮
                try {
                            /*public class InfDoActionParam
                            {
                                public int userid;
                                public int seatindex;
                                public PlayAction action;
                                public int amountchips;
                                public int tableid;
                            }*/
                    String msg = "$" + Constant.GAME_DO_ACTION + "|";
                    JSONObject jsonSend = new JSONObject();
                    jsonSend.put("userid", application.getUserId());
                    jsonSend.put("seatindex", getUserIndex());
                    jsonSend.put("action",2);
                    jsonSend.put("amountchips", 0);
                    jsonSend.put("tableid", gameId);
                    msg += jsonSend.toString().replace("$", "￥");
                    myBinder.sendInfo(msg);
                } catch (Exception e) {
                    ToastUtil.showToastInScreenCenter(GameActivity.this, "弃牌出错！");
                }

            }
        });

        mButtonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //右侧按钮
                try {

                    String msg = "$" + Constant.GAME_DO_ACTION + "|";
                    JSONObject jsonSend = new JSONObject();
                    jsonSend.put("userid", application.getUserId());
                    jsonSend.put("seatindex", getUserIndex());
                    switch (mTVCheck.getText().toString()){
                        case  "跟注":
                            jsonSend.put("action", 1);
                            jsonSend.put("amountchips",maxpaidchips);

                            break;
                        case "看牌":
                            jsonSend.put("action", 3);
                            jsonSend.put("amountchips",0);
                            break;
                        case "AllIn":
                            jsonSend.put("action", 6);
                            jsonSend.put("amountchips",maxChip);
                            break;
                    }
                    jsonSend.put("tableid", gameId);
                    msg += jsonSend.toString().replace("$", "￥");
                    myBinder.sendInfo(msg);
                } catch (Exception e) {
                    ToastUtil.showToastInScreenCenter(GameActivity.this, "弃牌出错！");
                }


            }
        });

        mAction.setVisibility(View.GONE);
    }

    private void addActionButon(){

        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(300, 590, 0, 0);
        lp.x =(mScreenWidth-300)/2;
        lp.y = (mScreenHeight-80)/2-180;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mAction = inflater.inflate(R.layout.layout_actionbutton, null);
        mAction.setLayoutParams(lp);
        layout_game.addView(mAction);
        //给三个按钮加响应事件
        mButtonFold=(Button)mAction.findViewById(R.id.button_fold);
        mButtonRaise=(Button)mAction.findViewById(R.id.button_raise);
        mButtonCheck=(Button)mAction.findViewById(R.id.button_check);
        mTVFold=(TextView)mAction.findViewById(R.id.tv_fold);
        mTVRaise=(TextView)mAction.findViewById(R.id.tv_raise);
        mTVCheck=(TextView)mAction.findViewById(R.id.tv_check);

    }

    private void addChipSelector(){

        //添加textMoveLayout ,宽度 90，高度200
        //按钮宽度90，高度50
        //虚线 宽度4，高度200，置于底部
        int width=90;
        int height=400;
        int button_height=50;
        int button_width=90;
        int line_width=4;
        ViewGroup.LayoutParams layoutParams;

        textMoveLayout=new TextMoveLayout(this);
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(width, height, 0, 0);

        lp.x = (300-90)/2;;
        lp.y =0;//  (mScreenHeight-80)/2-60;;
        textMoveLayout.setLayoutParams(lp);
        ((AbsoluteLayout)mAction).addView(textMoveLayout);

        //虚线
        ImageView iv_line=new ImageView(this);
        layoutParams = new ViewGroup.LayoutParams(4,height);
        textMoveLayout.addView(iv_line, layoutParams);
        iv_line.setBackgroundResource(R.drawable.line_bj);
        iv_line.layout((width-line_width)/2,0,(width-line_width)/2+line_width,height);


        //三个按钮
        button_bottom=new Button(this);
        button_bottom.setText("12");
        button_bottom.setTextColor(Color.WHITE);
        button_bottom.setTextSize(12);
        button_bottom.setBackgroundResource(R.drawable.bottom_bj);
        layoutParams = new ViewGroup.LayoutParams(90,50);
        //button_bottom.setGravity(Gravity.CENTER);
        textMoveLayout.addView(button_bottom, layoutParams);
        button_bottom.layout(0,height-button_height,width,height);


        button_top=new Button(this);
        button_top.setText("12");
        button_top.setTextColor(Color.WHITE);
        button_top.setTextSize(12);
        button_top.setBackgroundResource(R.drawable.top_bj);
        layoutParams = new ViewGroup.LayoutParams(90,50);
        textMoveLayout.addView(button_top, layoutParams);
        button_top.layout(0,0,width,button_height);


        button_middle=new Button(this);
        button_middle.setText("12");
        button_middle.setTextColor(Color.WHITE);
        button_middle.setTextSize(12);
        button_middle.setBackgroundResource(R.drawable.middle_bj);
        layoutParams = new ViewGroup.LayoutParams(90,50);
        textMoveLayout.addView(button_middle, layoutParams);
        button_middle.layout(0,150,width,150+button_height);

        textMoveLayout.setVisibility(View.GONE);


        /* button=new Button(this);
        button.setText("12");
        button.setTextColor(Color.WHITE);
        button.setTextSize(12);
        button.setBackgroundResource(R.drawable.middle_bj);

//        text = new TextView(this);
//        text.setTextColor(Color.WHITE);
//        text.setTextSize(12);
//        text.setSingleLine(true);
//        text.setText("20");
//        //text.setGravity(Gravity.CENTER);
//        //text.setGravity(Gravity.CENTER);
//        text.setBackgroundResource(R.drawable.middle_bj);
        layoutParams = new ViewGroup.LayoutParams(90,50);
        textMoveLayout.addView(button, layoutParams);
        button.layout(100,320,190,370);
*/



    }

    private void addReturnSeatButton(){
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mReturnSeat = inflater.inflate(R.layout.layout_returnseat, null);
        TextView tv=(TextView)mReturnSeat.findViewById(R.id.tv_returnseat) ;
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送回到座位
                try{
                    String msg = "$" + Constant.GAME_BACK_SEAT + "|";
                    JSONObject jsonSend = new JSONObject();
                    jsonSend.put("userid", application.getUserId());
                    jsonSend.put("tableid", gameId);
                    jsonSend.put("seatindex",getUserIndex());
                     msg+=jsonSend.toString().replace("$","￥");
                    myBinder.sendInfo(msg);
                }catch (Exception e){
                    ToastUtil.showToastInScreenCenter(GameActivity.this,"返回座位出错！");

                }

            }
        });

        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(90, 35, 0, 0);
        lp.x=iSeatValue[0][0]+mSeatViewWidth;
        lp.y=iSeatValue[0][1]+(mSeatViewHeight-35)/2;
        mReturnSeat.setLayoutParams(lp);
        layout_game.addView(mReturnSeat);
        mReturnSeat.setVisibility(View.INVISIBLE);
    }

    private void addLayoutMessage(){
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(mScreenWidth, 60, 0, 0);
        lp.x =0;
        lp.y = (mScreenHeight-60)/2;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        mMessage = inflater.inflate(R.layout.layout_message, null);

        TextView tv_message = (TextView) mMessage.findViewById(R.id.tv_message);
        tv_message.setText("等待开始" );
        mMessage.setLayoutParams(lp);
        layout_game.addView(mMessage);
        mMessage.setVisibility(View.INVISIBLE);

    }

    //赢家翻牌，只有一个控件
    private void addshowcards(){

            LayoutInflater inflater = LayoutInflater.from(mContext);
            mShowCards = inflater.inflate(R.layout.layout_showcard, null);
            layout_game.addView(mShowCards);
            mShowCards.setVisibility(View.INVISIBLE);

    }

    private void addMyCards(){

        LayoutInflater inflater = LayoutInflater.from(mContext);
        mMyCards = inflater.inflate(R.layout.layout_mycard, null);
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(239, 80, 0, 0);
        lp.x =iSeatValue[0][0]+mSeatViewWidth;
        lp.y = iSeatValue[0][1]+mNameTextHeight;
        mMyCards.setLayoutParams(lp);
        layout_game.addView(mMyCards);
        mMyCards.setVisibility(View.INVISIBLE);

    }

    private void addGameCards(){

        LayoutInflater inflater = LayoutInflater.from(mContext);
        mGameCards = inflater.inflate(R.layout.layout_gamecard, null);
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(369,100, 0, 0);
        lp.x =(mScreenWidth-369)/2;
        lp.y = (mScreenHeight-80)/2+100;
        mGameCards.setLayoutParams(lp);
        layout_game.addView(mGameCards);
        mGameCards.setVisibility(View.INVISIBLE);

    }

    private void setPosition(View view,int width,int height,int px,int py){
        AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(width,height, 0, 0);
        lp.x =px;
        lp.y = py;
        view.setLayoutParams(lp);

    }

    /*
    * 座位写死宽度 120 高度150px
    * */
    private  void initSeatXY(int seatNum){
//        iSeatValue=new int[mTableInfo.seats.length][2];
//        iTipLocation=new int[mTableInfo.seats.length][2];//大小盲注,用户操作的展现位置
//        iAmountChipLocation=new int[mTableInfo.seats.length][2];//已下注金额的展现位置
//        iD=new int[mTableInfo.seats.length][2];//庄家位置
//        iCardBack=new int[mTableInfo.seats.length][2];//有牌的用户背景


        int leftPading=15;
        int rightPading=15;
        int topPading=20;
        int bottomPading=10;
        switch(seatNum){
            case 2:

                break;
            case 3:

                break;
            case 4:


                break;
            case 5:


                break;
            case 6:


                break;
            case 7:


                break;
            case 8:

                break;
            case 9:
                iSeatValue[0][0]=(mScreenWidth-mSeatViewWidth)/2;
                iSeatValue[0][1]=mScreenHeight-mSeatViewHeight-bottomPading-60;//60为手机顶部状态栏的高度
                //底部，在名字的上面标明，是否大小盲注，然后再上面是下注额度
                iAmountChipLocation[0][0]=(mScreenWidth-mAmountChipViewWidth)/2;
                iAmountChipLocation[0][1]= iSeatValue[0][1]-mAmountChipVieHeight;
                iD[0][0]=iSeatValue[0][0]+mSeatViewWidth-(mSeatViewWidth-mHeadImageWidth)/2;
                iD[0][1]=iSeatValue[0][1]+mSeatViewHeight-mDViewHeight;
                iCardBack[0][0]=iSeatValue[0][0]+(mSeatViewWidth-mHeadImageWidth)/2;
                iCardBack[0][1]=iSeatValue[0][1]+mSeatViewHeight-mNameTextHeight-mCardBackHeight;
                iTipLocation[0][0]=iSeatValue[0][0]+mSeatViewWidth-mTipViewWidth;
                iTipLocation[0][1]=iSeatValue[0][1];


                iSeatValue[1][0]=0;
                iSeatValue[1][1]=topPading+(mScreenHeight-topPading-bottomPading-mSeatViewHeight)/4*3 ;
                iAmountChipLocation[1][0]=iSeatValue[1][0]+mSeatViewWidth-(mSeatViewWidth-mHeadImageWidth)/2;
                iAmountChipLocation[1][1]=iSeatValue[1][1] +(mSeatViewHeight-mAmountChipVieHeight)/2;
                iD[1][0]=iSeatValue[1][0]+mSeatViewWidth-(mSeatViewWidth-mHeadImageWidth)/2;
                iD[1][1]=iSeatValue[1][1]+mSeatViewHeight-mDViewHeight;
                iCardBack[1][0]=iSeatValue[1][0]+(mSeatViewWidth-mHeadImageWidth)/2;
                iCardBack[1][1]=iSeatValue[1][1]+mSeatViewHeight-mNameTextHeight-mCardBackHeight;
                iTipLocation[1][0]=iSeatValue[1][0]+mSeatViewWidth-mTipViewWidth;
                iTipLocation[1][1]=iSeatValue[1][1];

                iSeatValue[2][0]=0;
                iSeatValue[2][1]=topPading+(mScreenHeight-topPading-bottomPading-mSeatViewHeight)/4*2;
                iAmountChipLocation[2][0]=iSeatValue[1][0] +mSeatViewWidth-(mSeatViewWidth-mHeadImageWidth)/2;
                iAmountChipLocation[2][1]=iSeatValue[2][1] +(mSeatViewHeight-mAmountChipVieHeight)/2;
                iD[2][0]=iSeatValue[2][0]+mSeatViewWidth-(mSeatViewWidth-mHeadImageWidth)/2;
                iD[2][1]=iSeatValue[2][1]+mSeatViewHeight-mDViewHeight;
                iCardBack[2][0]=iSeatValue[2][0]+(mSeatViewWidth-mHeadImageWidth)/2;
                iCardBack[2][1]=iSeatValue[2][1]+mSeatViewHeight-mNameTextHeight-mCardBackHeight;
                iTipLocation[2][0]=iSeatValue[2][0]+mSeatViewWidth-mTipViewWidth;
                iTipLocation[2][1]=iSeatValue[2][1];

                iSeatValue[3][0]=0;
                iSeatValue[3][1]=topPading+(mScreenHeight-topPading-bottomPading-mSeatViewHeight)/4 ;
                iAmountChipLocation[3][0]=iSeatValue[3][0] +mSeatViewWidth-(mSeatViewWidth-mHeadImageWidth)/2;
                iAmountChipLocation[3][1]=iSeatValue[3][1] +(mSeatViewHeight-mAmountChipVieHeight)/2;
                iD[3][0]=iSeatValue[3][0]+mSeatViewWidth-(mSeatViewWidth-mHeadImageWidth)/2;
                iD[3][1]=iSeatValue[3][1]+mSeatViewHeight-mDViewHeight;
                iCardBack[3][0]=iSeatValue[3][0]+(mSeatViewWidth-mHeadImageWidth)/2;
                iCardBack[3][1]=iSeatValue[3][1]+mSeatViewHeight-mNameTextHeight-mCardBackHeight;
                iTipLocation[3][0]=iSeatValue[3][0]+mSeatViewWidth-mTipViewWidth;
                iTipLocation[3][1]=iSeatValue[3][1];



                iSeatValue[4][0]=(mScreenWidth-2*mSeatViewWidth)/3;
                iSeatValue[4][1]=topPading;
                //此处应该是正下方
                iAmountChipLocation[4][0]=iSeatValue[4][0]+(mSeatViewWidth-mAmountChipViewWidth)/2;
                iAmountChipLocation[4][1]=iSeatValue[4][1]+mSeatViewHeight+5 ;
                iD[4][0]=iSeatValue[4][0]+mSeatViewWidth-(mSeatViewWidth-mHeadImageWidth)/2;
                iD[4][1]=iSeatValue[4][1]+mSeatViewHeight-mDViewHeight;
                iCardBack[4][0]=iSeatValue[4][0]+(mSeatViewWidth-mHeadImageWidth)/2;
                iCardBack[4][1]=iSeatValue[4][1]+mSeatViewHeight-mNameTextHeight-mCardBackHeight;
                iTipLocation[4][0]=iSeatValue[4][0]+mSeatViewWidth-mTipViewWidth;
                iTipLocation[4][1]=iSeatValue[4][1];


                iSeatValue[5][0]=(mScreenWidth-2*mSeatViewWidth)/3*2+mSeatViewWidth;
                iSeatValue[5][1]=topPading;
                iAmountChipLocation[5][0]=iSeatValue[5][0]+(mSeatViewWidth-mAmountChipViewWidth)/2;
                iAmountChipLocation[5][1]=iSeatValue[5][1]+mSeatViewHeight+5 ;
                iD[5][0]=iSeatValue[5][0]+mSeatViewWidth-(mSeatViewWidth-mHeadImageWidth)/2;
                iD[5][1]=iSeatValue[5][1]+mSeatViewHeight-mDViewHeight;
                iCardBack[5][0]=iSeatValue[5][0]+(mSeatViewWidth-mHeadImageWidth)/2;
                iCardBack[5][1]=iSeatValue[5][1]+mSeatViewHeight-mNameTextHeight-mCardBackHeight;
                iTipLocation[5][0]=iSeatValue[5][0]+mSeatViewWidth-mTipViewWidth;
                iTipLocation[5][1]=iSeatValue[5][1];

                iSeatValue[6][0]=mScreenWidth-mSeatViewWidth;
                iSeatValue[6][1]=topPading+(mScreenHeight-topPading-bottomPading-mSeatViewHeight)/4;
                iAmountChipLocation[6][0]=iSeatValue[6][0] -mAmountChipViewWidth+(mSeatViewWidth-mHeadImageWidth)/2;
                iAmountChipLocation[6][1]=iSeatValue[6][1] +(mSeatViewHeight-mAmountChipVieHeight)/2;
                iD[6][0]=iSeatValue[6][0]-mDViewWidth+(mSeatViewWidth-mHeadImageWidth)/2;
                iD[6][1]=iSeatValue[6][1]+mSeatViewHeight-mDViewHeight;
                iCardBack[6][0]=iSeatValue[6][0]+(mSeatViewWidth-mHeadImageWidth)/2;
                iCardBack[6][1]=iSeatValue[6][1]+mSeatViewHeight-mNameTextHeight-mCardBackHeight;
                iTipLocation[6][0]=iSeatValue[6][0];
                iTipLocation[6][1]=iSeatValue[6][1];


                iSeatValue[7][0]=mScreenWidth-mSeatViewWidth;
                iSeatValue[7][1]=topPading+(mScreenHeight-topPading-bottomPading-mSeatViewHeight)/4*2;
                iAmountChipLocation[7][0]=iSeatValue[7][0] -mAmountChipViewWidth+(mSeatViewWidth-mHeadImageWidth)/2;
                iAmountChipLocation[7][1]=iSeatValue[7][1] +(mSeatViewHeight-mAmountChipVieHeight)/2;
                iD[7][0]=iSeatValue[7][0]-mDViewWidth+(mSeatViewWidth-mHeadImageWidth)/2;
                iD[7][1]=iSeatValue[7][1]+mSeatViewHeight-mDViewHeight;
                iCardBack[7][0]=iSeatValue[7][0]+(mSeatViewWidth-mHeadImageWidth)/2;
                iCardBack[7][1]=iSeatValue[7][1]+mSeatViewHeight-mNameTextHeight-mCardBackHeight;
                iTipLocation[7][0]=iSeatValue[7][0];
                iTipLocation[7][1]=iSeatValue[7][1];


                iSeatValue[8][0]=mScreenWidth-mSeatViewWidth;
                iSeatValue[8][1]=topPading+(mScreenHeight-topPading-bottomPading-mSeatViewHeight)/4*3 ;
                iAmountChipLocation[8][0]=iSeatValue[8][0] -mAmountChipViewWidth+(mSeatViewWidth-mHeadImageWidth)/2;
                iAmountChipLocation[8][1]=iSeatValue[8][1] +(mSeatViewHeight-mAmountChipVieHeight)/2;
                iD[8][0]=iSeatValue[8][0]-mDViewWidth+(mSeatViewWidth-mHeadImageWidth)/2;
                iD[8][1]=iSeatValue[8][1]+mSeatViewHeight-mDViewHeight;
                iCardBack[8][0]=iSeatValue[8][0]+(mSeatViewWidth-mHeadImageWidth)/2;
                iCardBack[8][1]=iSeatValue[8][1]+mSeatViewHeight-mNameTextHeight-mCardBackHeight;
                iTipLocation[8][0]=iSeatValue[8][0];
                iTipLocation[8][1]=iSeatValue[8][1];

                break;

        }

    }

    public Bitmap drawCardBack(){

        Bitmap newBitmap = Bitmap.createBitmap(150,142, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(newBitmap);
        // 获取资源文件的引用res
        Resources res = getResources();
        // 获取图形资源文件
        Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.card_bg);
        // 设置canvas画布背景为白色
        //canvas.drawColor(Color.WHITE);
        // 在画布上绘制缩放之前的位图，以做对比
        //屏幕上的位置坐标是0,0
        //canvas.drawBitmap(bmp, 0, 0, null);
        // 定义矩阵对象
        Matrix matrix = new Matrix();
        // 缩放原图
        matrix.postScale(1f, 1f);
        // 向左旋转45度，参数为正则向右旋转
        matrix.postRotate(-30);
        //bmp.getWidth(), 500分别表示重绘后的位图宽高
        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),

                matrix, true);

        // 在画布上绘制旋转后的位图
        //放在坐标为0,200的位置
        canvas.drawBitmap(dstbmp,0, 0, null);
        matrix = new Matrix();
        // 缩放原图
        matrix.postScale(1f, 1f);
        matrix.postRotate(-15);
        dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),

                matrix, true);

        // 在画布上绘制旋转后的位图

        canvas.drawBitmap(dstbmp, 40, 5, null);

        return newBitmap;
    }

    public Bitmap drawSingleCard(int suit ,int member){

        Bitmap newBitmap = Bitmap.createBitmap(88,120, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(newBitmap);
        // 获取资源文件的引用res
        Resources res = getResources();
        //三张图的位置
        int iPicLocation[][]=new  int[3][2];
        iPicLocation[0][0]=0;
        iPicLocation[0][1]=1;
        iPicLocation[1][0]=4;
        iPicLocation[1][1]=35;
        iPicLocation[2][0]=30;
        iPicLocation[2][1]=55;

        //画背景
        Bitmap bmpBackground;
        if (member==11){
            bmpBackground= BitmapFactory.decodeResource(res, R.drawable.rank_fg_11);

        }else if (member==12){

            bmpBackground= BitmapFactory.decodeResource(res, R.drawable.rank_fg_12);

        }else if (member==13){
            bmpBackground= BitmapFactory.decodeResource(res, R.drawable.rank_fg_13);

        }else{
            bmpBackground= BitmapFactory.decodeResource(res, R.drawable.fg);
        }

        //画牌的背景
        canvas.drawBitmap(bmpBackground, 0, 0, null);
        switch (member){
            case 2:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_2), iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3), iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3), iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_2),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_2),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_2),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 3:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_3),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_3),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_3),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_3),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 4:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_4),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_4),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_4),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_4),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 5:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_5),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_5),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_5),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_5),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 6:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_6),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_6),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_6),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_6),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 7:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_7),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_7),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_7),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_7),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 8:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_8),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_8),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_8),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_8),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 9:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_9),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_9),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_9),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_9),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 10:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_10),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_10),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_10),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_10),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 11:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_11),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_11),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_11),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_11),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 12:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_12),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_12),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_12),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_12),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 13:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_13),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_13),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_13),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_13),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
            case 14:
                switch(suit){
                    case 0:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_14),iPicLocation[0][0], iPicLocation[0][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_3),iPicLocation[1][0], iPicLocation[1][1],null);//梅花
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_3),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 1:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_14),iPicLocation[0][0], iPicLocation[0][1],null);//方块
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_2),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_2),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 2:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank1_14),iPicLocation[0][0], iPicLocation[0][1],null);//红心
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_1),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_1),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;
                    case 3:
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.rank2_14),iPicLocation[0][0], iPicLocation[0][1],null);//黑桃
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit1_4),iPicLocation[1][0], iPicLocation[1][1],null);
                        canvas.drawBitmap( BitmapFactory.decodeResource(res, R.drawable.suit2_4),iPicLocation[2][0], iPicLocation[2][1],null);
                        break;

                }
                break;
        }




//        // 定义矩阵对象
//        Matrix matrix = new Matrix();
//        // 缩放原图
//        matrix.postScale(1f, 1f);
//        // 向左旋转45度，参数为正则向右旋转
//        matrix.postRotate(-30);
//        //bmp.getWidth(), 500分别表示重绘后的位图宽高
//        Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
//
//                matrix, true);
//
//        // 在画布上绘制旋转后的位图
//        //放在坐标为0,200的位置
//        canvas.drawBitmap(dstbmp,0, 0, null);
//        matrix = new Matrix();
//        // 缩放原图
//        matrix.postScale(1f, 1f);
//        matrix.postRotate(-15);
//        dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(),
//
//                matrix, true);
//
//        // 在画布上绘制旋转后的位图
//        //放在坐标为0,200的位置
//        canvas.drawBitmap(dstbmp, 40, 5, null);


        return newBitmap;
    }


    private void showWindow(View parent) {

        View contentView = getPopupWindowContentView();
//        popupWindow = new PopupWindow(contentView,getResources().getDisplayMetrics().widthPixels-160,240
//                , true);
        popupWindow = new PopupWindow(contentView,600 ,620
                , true);
//        popupWindow = new PopupWindow(contentView, contentView.getMeasuredWidth(),contentView.getMeasuredHeight()
//                , true);
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 如果不设置PopupWindow的背景，有些版本就会出现一个问题：无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        // 设置好参数之后再show

        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        //int xOffset = parent.getWidth()  - contentView.getMeasuredWidth() ;
        //popupWindow.showAsDropDown(parent,xOffset,20);    // 在mButton2的中间显示
        int windowPos[] = calculatePopWindowPos((View)parent, contentView);
        popupWindow.showAtLocation(GameActivity.this.getWindow().getDecorView(), Gravity.CENTER,0,0);
        // popupWindow.showAtLocation(parent,Gravity.TOP | Gravity.START, windowPos[0], windowPos[1]);
    }

    private View getPopupMenuWindowContentView() {
        // 一个自定义的布局，作为显示的内容
        int layoutId = R.layout.item_popmenu;   // 布局ID
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);

        return contentView;
    }

    private View getPopupWindowContentView() {
        // 一个自定义的布局，作为显示的内容
        int layoutId = R.layout.popupwindow_buy_score;   // 布局ID
        View contentView = LayoutInflater.from(this).inflate(layoutId, null);


        tv_buy_coin=(TextView)contentView.findViewById(R.id.tv_buy_coin);
        tv_buy_coin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出购买金币界面
            }
        });

        tv_message=(TextView)contentView.findViewById(R.id.tv_message);
        tv_message.setVisibility(View.INVISIBLE);
        if (mTableInfo.mintakeinchips>application.getUser().goldcoin){
            tv_message.setVisibility(View.VISIBLE);
            tv_message.setText("个人财富不足，不能补充记分牌");
        }

        tv_core=(TextView)contentView.findViewById(R.id.tv_core);
        tv_core.setText(mTableInfo.mintakeinchips+"");

        tv_coin=(TextView)contentView.findViewById(R.id.tv_coin);
        tv_coin.setText(application.getUser().goldcoin+"");
        tv_service_coin=(TextView)contentView.findViewById(R.id.tv_service_coin);
        tv_service_coin.setText(mTableInfo.mintakeinchips/10+"");
        tv_in_coin=(TextView)contentView.findViewById(R.id.tv_in_coin);
        if (mTableInfo.iscontroltakein){
            //控制带入
            tv_in_coin.setText(takeinchips+"/"+permittakeinchips);
        }else{
            //非控制带入
            String value=mTableInfo.takeinchipsuplimit+"";
            if (value.equals("0")){
                value="无上限";
            }
            tv_in_coin.setText(takeinchips+"/"+value);
        }


        iv_close=(ImageView)contentView.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭popwndow
                popupWindow.dismiss();
            }
        });


        seekbar_core=(RangeSliderBar2)contentView.findViewById(R.id.seekbar_core);
        seekbar_core.setRangeCount((mTableInfo.maxtakeinchips-mTableInfo.mintakeinchips)/(mTableInfo.smallblind*100)+1);
        seekbar_core.setOnSelectRangeBarListener(new RangeSliderBar2.OnSelectRangeBarListener() {
            @Override
            public void OnSelectRange(int rangeNum) {
                //监听，负责更新数字
                int core=(rangeNum*mTableInfo.smallblind*100+mTableInfo.mintakeinchips)*11/10;
                if (core>application.getUser().goldcoin){
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("个人财富不足，不能补充记分牌");
                    button_apply.getText().equals("购买金币");
                }else{
                    tv_message.setVisibility(View.INVISIBLE);
                    if (mTableInfo.iscontroltakein){
                        //控制带入要判断
                        /*if (mTableInfo.iscontroltakein){
            //控制带入
            tv_in_coin.setText(takeinchips+"/"+permittakeinchips);
        }else{
            //非控制带入
            String value=mTableInfo.takeinchipsuplimit+"";
            if (value.equals("0")){
                value="无上限";
            }
            tv_in_coin.setText(takeinchips+"/"+value);
        }*/
                        //控制带入
                        if (core+takeinchips>permittakeinchips){
                            button_apply.setText("申请");

                        }else{
                            button_apply.setText("确定");
                        }

                    }else{
                        //非控制带入
                        button_apply.setText("确定");
                    }
                }
                tv_core.setText(rangeNum*mTableInfo.smallblind*100+mTableInfo.mintakeinchips+"");
                tv_service_coin.setText((rangeNum*mTableInfo.smallblind*100+mTableInfo.mintakeinchips)/10+"");


            }
        });

        button_apply=(Button)contentView.findViewById(R.id.button_apply);
        button_apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理按钮事件
                if (button_apply.getText().equals("购买金币")){
                    //弹出金币购买界面
                }else if (button_apply.getText().equals("确定")){
                    //提交带入申请，减去用户的金币
                    int buycore= Integer.parseInt(tv_core.getText().toString());
                    //判断已带入，总带入
                    if ((takeinchips+buycore)>mTableInfo.maxtakeinchips){

                    }else{

                        if (isFirstBuyCore) {
                            //首次购买，发送sitseat指令
                            try {
                                String msg = "$" + Constant.GAME_SIT_SEAT + "|";
                                JSONObject jsonSend = new JSONObject();
                                jsonSend.put("userid", application.getUserId());
                                jsonSend.put("tableid", gameId);
                                jsonSend.put("seatindex", mWantSeatIndex);
                                jsonSend.put("intochips", buycore);
                                jsonSend.put("ip", Util.getLocalIpAddress());
                                jsonSend.put("gpsx", application.getLatitude());
                                jsonSend.put("gpsy", application.getLongitude());
                                msg += jsonSend.toString().replace("$", "￥");
                                myBinder.sendInfo(msg);
                            } catch (Exception e) {
                                ToastUtil.showToastInScreenCenter(GameActivity.this, "坐下座位出错！");
                            }


                        }else{
                            //非首次购买，发送addchip指令

                            try {
                                String msg = "$" + Constant.GAME_ADD_CHIPS + "|";
                                JSONObject jsonSend = new JSONObject();
                                jsonSend.put("userid", application.getUserId());
                                jsonSend.put("tableid", gameId);
                                jsonSend.put("chips", buycore);

                                msg += jsonSend.toString().replace("$", "￥");
                                myBinder.sendInfo(msg);
                            } catch (Exception e) {
                                ToastUtil.showToastInScreenCenter(GameActivity.this, "坐下座位出错！");
                            }

                        }
                    }


                }else if(button_apply.getText().equals("申请")) {
                    //发送申请
                    Thread thread=new Thread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try{
                                //拼装url字符串

                                int applycore= Integer.parseInt(tv_core.getText().toString());
                                DzApplication applicatoin=(DzApplication)getApplication();
                                JSONObject jsonObj = new JSONObject();
                                jsonObj.put("userid",applicatoin.getUserId());
                                jsonObj.put("tableid",gameId);
                                jsonObj.put("requesttakeinchips",applycore);

                                String strURL=getString(R.string.url_remote);
                                strURL+="func=requesttakein&param="+jsonObj.toString();

                                URL url = new URL(strURL);
                                Request request = new Request.Builder().url(strURL).build();
                                Response response = DzApplication.getHttpClient().newCall(request).execute();
                                String result=response.body().string();

                                JSONObject jsonObject=new JSONObject(result);
                                if (jsonObject.getInt("ret")==0){
                                    // 创建牌局成功,发送IM消息RequestTakeIn|{‘tablename’:abc} tv_buy_coin
                                    //sendMessage(Message message, MessageSendingOptions options)
//                                    HashMap<String,String> info=new HashMap<String,String>();
//                                    info.put("applyid",applicatoin.getUserId()+"");
//                                    info.put("tablename","");
                                    //createSingleCustomMessage,创建自定义消息
                                    // cn.jpush.im.android.api.model.Message message=JMessageClient.createSingleCustomMessage(mTableInfo.createuserid+"",getString(R.string.app_key),info);

                                    JSONObject json=new JSONObject();
                                    json.put("tablename",gameHouseName);
                                    String sendstr="RequestTakeIn|"+json.toString();
                                    cn.jpush.im.android.api.model.Message message=JMessageClient.createSingleTextMessage(mTableInfo.createuserid+"",getString(R.string.app_key),sendstr);

//                                    MessageSendingOptions messageSendingOptions=new MessageSendingOptions();
//                                    messageSendingOptions.setShowNotification(true);
//                                    messageSendingOptions.setNotificationTitle("带入申请");
//                                    messageSendingOptions.setNotificationText("收到一条带入申请");
                                    message.setOnSendCompleteCallback(new BasicCallback() {
                                        @Override
                                        public void gotResult(int responseCode, String responseDesc) {
                                            if (responseCode == 0) {
                                                handler.sendEmptyMessage(MESSAGE_DISMISS_POPWINDOW);
                                                ToastUtil.showToastInScreenCenter(GameActivity.this,"提交申请带入成功，请等待房主或管理员审核！");
                                                // 消息发送成功
                                            } else {
                                                // 消息发送失败
                                                ToastUtil.showToastInScreenCenter(GameActivity.this,"提交申请带入失败，请重新申请！");
                                            }
                                        }
                                    });
                                    JMessageClient.sendMessage(message);
                                   // JMessageClient.sendMessage(message,messageSendingOptions);


                                }else {
                                    ToastUtil.showToastInScreenCenter(GameActivity.this,"申请带入失败，错误原因："+jsonObject.getString("msg"));
                                }



                            }catch (Exception e){
                                ToastUtil.showToastInScreenCenter(GameActivity.this,"提交申请带入异常，请稍后重试!"+e.toString());
                            }

                        }
                    });
                    thread.start();

                }


            }
        });

       if (mTableInfo.iscontroltakein){
           //控制带入
           if (mTableInfo.mintakeinchips*11/10>application.getUser().goldcoin){
               button_apply.setText("购买金币");
           }else if(mTableInfo.mintakeinchips>permittakeinchips){
               button_apply.setText("申请");
           }else {
               button_apply.setText("确定");
           }
          ;

       }else{
           //非控制带入
           button_apply.setText("确定");
           if (mTableInfo.mintakeinchips*11/10>application.getUser().goldcoin){
               button_apply.setText("购买金币");
           }

       }

        return contentView;
    }

    private void getTakeInChipsFromServer(){
        //启动线程到后台去请求数据
        Thread thread=new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try{
                    //拼装url字符串

                    JSONObject jsonObj = new JSONObject();
                    jsonObj.put("tableid",gameId);
                    jsonObj.put("userid",application.getUserId());

                    String strURL=getString(R.string.url_remote);
                    strURL+="func=getcontroltakeininfo&param="+jsonObj.toString();

                    URL url = new URL(strURL);
                    Request request = new Request.Builder().url(strURL).build();
                    Response response = DzApplication.getHttpClient().newCall(request).execute();
                    String result=response.body().string();

                    JSONObject jsonObject=new JSONObject(result);
                    takeinchips=jsonObject.getInt("takeinchips");
                    permittakeinchips=jsonObject.getInt("permittakeinchips");
                    tablecreateuserid=jsonObject.getInt("tablecreateuserid");
                    //告诉主线程，从后台取takein数据成功
                    handler.sendEmptyMessage(MESSAGE_TAKEIN_INFO);

                }catch (Exception e){

                    ToastUtil.showToastInScreenCenter(GameActivity.this,"到服务器取牌桌带入信息失败，请稍后重试!"+e.toString());
                }

            }
        });
        thread.start();
    }


    private int getUserIndex()
    {
        if (mGameUser.containsKey(application.getUserId())){
            return mGameUser.get(application.getUserId()).seatindex;
        }else{
            return -1;
        }

//        int userIndex=-1;
//        for (int key : mGameUser.keySet()) {
//            if (mGameUser.get(key).userId==application.getUserId()){
//                userIndex=mGameUser.get(key).seatindex;
//            }
//        }
//        return userIndex;
    }

    @Override
    protected void onStop() {


        super.onStop();

    }

    @Override
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        this.unbindService(connection);
        Intent Intent = new Intent(this, SocketService.class);
        stopService(Intent);
        super.onDestroy();
    }


    public void onEvent(GetEventNotificationTaskMng.EventEntity event){
        //do your own business
//        int i=0;
//        i+=1;
    }

    public void onEvent(MessageEvent event) {
        //接受消息

        final cn.jpush.im.android.api.model.Message message = event.getMessage();
        if (message.getContentType() == ContentType.text){

            String msgReturn=((TextContent) message.getContent()).getText();//message.getContent().getStringExtra("text");
            if (msgReturn.indexOf("RequestTakeIn")==0 && msgReturn.indexOf("RequestTakeInRet")==-1){
                //RequestTakeIn|{‘tablename’:我的房间}
                String[] recData=msgReturn.split("\\|");

                try {
                    String gameHouseName = new JSONObject(recData[1]).getString("tablename");
                    String from=message.getFromUser().getNickname();
                    String showMsg=gameHouseName+"有带入申请";
                    //新建状态栏通知
                    showNotification("带入申请",showMsg);
                }catch (Exception e){

                }


            }else if (msgReturn.indexOf("RequestTakeInRet")==0){
                /*RequestTakeInRet|{‘IsPermit’:true,’permittakein’:300}*/
                String[] recData=msgReturn.split("\\|");

                try {
                    JSONObject jsonReturn=new JSONObject(recData[1]);
                    Boolean isPermit =jsonReturn .getBoolean("IsPermit");
                    String showMsg;
                    if (isPermit){
                        showMsg="您的带入申请已同意，允许带入记分牌为"+jsonReturn.getInt("permittakein");
                    }else{
                        showMsg="您的带入申请被拒绝";
                    }
                    //新建状态栏通知
                    showNotification("带入申请处理结果",showMsg);
                }catch (Exception e){

                }


            }
        }


//        //若为群聊相关事件，如添加、删除群成员
//        if (message.getContentType() == ContentType.eventNotification) {
//            GroupInfo groupInfo = (GroupInfo) message.getTargetInfo();
//            long groupId = groupInfo.getGroupID();
//            EventNotificationContent.EventNotificationType type = ((EventNotificationContent) message
//                    .getContent()).getEventNotificationType();
//            if (groupId == mGroupId) {
//                switch (type) {
//                    case group_member_added:
//                        //添加群成员事件
//                        List<String> userNames = ((EventNotificationContent) message.getContent()).getUserNames();
//                        //群主把当前用户添加到群聊，则显示聊天详情按钮
//                        refreshGroupNum();
//                        if (userNames.contains(mMyInfo.getNickname()) || userNames.contains(mMyInfo.getUserName())) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mChatView.showRightBtn();
//                                }
//                            });
//                        }
//
//                        break;
//                    case group_member_removed:
//                        //删除群成员事件
//                        userNames = ((EventNotificationContent) message.getContent()).getUserNames();
//                        //群主删除了当前用户，则隐藏聊天详情按钮
//                        if (userNames.contains(mMyInfo.getNickname()) || userNames.contains(mMyInfo.getUserName())) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mChatView.dismissRightBtn();
//                                    GroupInfo groupInfo = (GroupInfo) mConv.getTargetInfo();
//                                    if (TextUtils.isEmpty(groupInfo.getGroupName())) {
//                                        mChatView.setChatTitle(R.string.group);
//                                    } else {
//                                        mChatView.setChatTitle(groupInfo.getGroupName());
//                                    }
//                                    mChatView.dismissGroupNum();
//                                }
//                            });
//                        } else {
//                            refreshGroupNum();
//                        }
//
//                        break;
//                    case group_member_exit:
//                        refreshGroupNum();
//                        break;
//                }
//            }
//        }
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (message.getTargetType() == ConversationType.single) {
//                    UserInfo userInfo = (UserInfo) message.getTargetInfo();
//                    String targetId = userInfo.getUserName();
//                    String appKey = userInfo.getAppKey();
//                    if (mIsSingle && targetId.equals(mTargetId) && appKey.equals(mTargetAppKey)) {
//                        cn.jpush.im.android.api.model.Message lastMsg = mChatAdapter.getLastMsg();
//                        if (lastMsg == null || message.getId() != lastMsg.getId()) {
//                            mChatAdapter.addMsgToList(message);
//                        } else {
//                            mChatAdapter.notifyDataSetChanged();
//                        }
//                    }
//                } else {
//                    long groupId = ((GroupInfo) message.getTargetInfo()).getGroupID();
//                    if (groupId == mGroupId) {
//                        cn.jpush.im.android.api.model.Message lastMsg = mChatAdapter.getLastMsg();
//                        if (lastMsg == null || message.getId() != lastMsg.getId()) {
//                            mChatAdapter.addMsgToList(message);
//                        } else {
//                            mChatAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//        });
    }
    @Override
    protected void onPause() {
        super.onPause();
//        JMessageClient.exitConversation();
//
    }

    @Override
    protected void onResume() {

//        if (mTableInfo!=null) {
//            JMessageClient.enterSingleConversation(mTableInfo.createuserid + "", getString(R.string.app_key));
//        }
        super.onResume();

    }

    private void showNotification(String title,String showmsg) {
        // TODO Auto-generated method stub
        Notification.Builder builder=new Notification.Builder(this);
        builder.setSmallIcon(R.drawable.icon);//设置图标
        builder.setTicker(showmsg);//手机状态栏的提示
        builder.setContentTitle(title);//设置标题
        builder.setContentText(showmsg);//设置通知内容
        builder.setWhen(System.currentTimeMillis());//设置通知时间
        builder.setContentIntent(null);//点击后的意图
        builder.setDefaults(Notification.DEFAULT_LIGHTS);//设置指示灯
        builder.setDefaults(Notification.DEFAULT_SOUND);//设置提示声音
        builder.setDefaults(Notification.DEFAULT_VIBRATE);//设置震动
        Notification notification=builder.getNotification();//4.1以上，以下要用getNotification()
        notificationManager.notify(0, notification);
    }


}
