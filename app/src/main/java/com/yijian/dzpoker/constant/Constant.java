package com.yijian.dzpoker.constant;

/**
 * Created by rabby on 2017/8/9.
 */

public class Constant {
    //Btn的标识
    public static final int BTN_FLAG_MAIN = 0x01;
    public static final int BTN_FLAG_GAME = 0x01 << 1;
    public static final int BTN_FLAG_CLUB = 0x01 << 2;
    public static final int BTN_FLAG_ME = 0x01 << 3;

    //Fragment的标识
    public static final String FRAGMENT_FLAG_MAIN = "首页";
    public static final String FRAGMENT_FLAG_GAME= "游戏";
    public static final String FRAGMENT_FLAG_CLUB = "俱乐部";
    public static final String FRAGMENT_FLAG_ME = "我";
    public static final String FRAGMENT_FLAG_SIMPLE = "simple";


    public static final String TAG_CONNECTFAILURE= "";
    public static final String TAG_FAILURE= "";
  /* public const string ComCreateTable = "createtable";
            //param  CreateTableParam
        public const string ComStarTabletGame = "starttablegame";
            //param StartTableGameParam
        public const string ComPauseTableGame = "pausetablegame";
            //param PauseTableGameParam
        public const string ComDisposeTable = "disposetable";
            //param DisposeTableParam
        public const string ComEnterTable = "entertable";
            //param  EnterTableParam   发送entertable后会收到tableinfo
        public const string ComSitSeat = "sitseat";
            //param SitSeatParam
        public const string ComLeaveSeat = "leaveseat";
            //param LeaveSeatParam
        public const string ComHoldSeat = "holdseat";
            //param HoldSeatParam
        public const string ComBackSeat = "backseat";
            //param BackSeatParam
        public const string ComLeaveTable = "leavetable";
            //param LeaveTableParam
        public const string ComDoAction = "doaction";
            //param DoActionParam
        public const string ComAddChips = "addchips";
            //param AddChipParam
        public const string ComSeeNextCard = "seenextcard";
        //param SeeNextCardParam
        public const string ComShowCard = "showcard";
        //param ShowCardParam
        public const string ComDoAnimation = "doanimation";
        //param DoAnimationParam
        public const string ComBuySurance = "buysurance";
        //param BuySuranceParam*/

   //客户端向后台发的信息
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
    public static final String INFO_INIT_ROUND = "infinitround";//清空桌面，下一把开始
    public static final String INFO_START_ROUND = "infstartround"; //设置庄家位
    public static final String INFO_DO_END = "infdoend"; //牌局结束
    public static final String INFO_HOLE = "infhole";//自己的底牌

    public static final String INFO_FLOP = "infflop";//公牌
    public static final String INFO_TURN = "infturn";//转牌，第四张
    public static final String INFO_RIVER = "infriver";//河牌，第五张
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
    public static final String INFO_DO_POT = "infdopot";//桌面筹码进池，底池展现
    public static final String INFO_SEE_NEXT_CARD = "infseenextcard";
    public static final String INFO_SHOW_CARD  = "infshowcard";
    public static final String INFO_WIN_27 = "infwin27";
    public static final String INFO_DO_ANIMATION = "infdoanimotion";
    public static final String INFO_BUY_SURANCE= "infbuysurance";

    //标题栏中, 调用者页面title对应的key
    public static final String INTENT_KEY_BACKTEXT = "backText";
    //标题栏中, 显示的 title
    public static final String INTENT_KEY_TITLE = "title";

}

