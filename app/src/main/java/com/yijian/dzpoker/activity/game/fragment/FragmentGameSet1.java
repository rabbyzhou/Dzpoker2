package com.yijian.dzpoker.activity.game.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.LoginActivity;
import com.yijian.dzpoker.activity.MainActivity;
import com.yijian.dzpoker.activity.game.GameActivity;
import com.yijian.dzpoker.activity.game.GameSetHighGradeActivity;
import com.yijian.dzpoker.activity.user.SysConfigActivity;
import com.yijian.dzpoker.util.DzApplication;
import com.yijian.dzpoker.util.ToastUtil;
import com.yijian.dzpoker.view.RangeSliderBar;
import com.yijian.dzpoker.view.SeekBarWithValue;
import com.yijian.dzpoker.view.data.GameParam;
import com.yijian.dzpoker.view.data.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentGameSet1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentGameSet1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentGameSet1 extends Fragment implements RangeSliderBar.OnSelectRangeBarListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private RangeSliderBar rangeSliderBar;
    private EditText et_game_name;
    private TextView tv_blinds,tv_min_in,tv_personl_money,tv_game_info,tv_control_in,tv_insurance;
    private SeekBar seekBar_blinds;
    private RadioButton rb_control_in,rb_insurance;
    private Button btn_high_grade_set, btn_new_game;
    private Boolean bRbControlIN ,bRbInsurance;
    private GameParam gameParam;

    private int[] blindsValue=new int[]{1,2,3,4,5,10,20,25,50,100,500};
    private int[] timelen=new int[]{30,60,120,240,300,360,420,480};
    private String[] title=new String[]{"30分钟","1小时","2小时","4小时","5小时","6小时","7小时","8小时"};


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private SeekBarWithValue customSeekBar;

    private OnFragmentInteractionListener mListener;

    public FragmentGameSet1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentGameSet1.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentGameSet1 newInstance(String param1, String param2) {
        FragmentGameSet1 fragment = new FragmentGameSet1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View layout=inflater.inflate(R.layout.fragment_fragment_game_set1, container, false);
        initViews(layout);
        //初始化值
        initData();



        return layout;
    }

    public void initViews(View parent){
        rangeSliderBar = (RangeSliderBar)parent.findViewById(R.id.rsv_custom);
        rangeSliderBar.setRangeCount(8);

        rangeSliderBar.setTitle(title);
        rangeSliderBar.setOnSelectRangeBarListener(this);

        et_game_name=(EditText)parent.findViewById(R.id.et_game_name);
        tv_blinds=(TextView)parent.findViewById(R.id.tv_blinds);
        tv_min_in=(TextView)parent.findViewById(R.id.tv_min_in);
        tv_personl_money=(TextView)parent.findViewById(R.id.tv_personl_money);
        tv_game_info=(TextView)parent.findViewById(R.id.tv_game_info);
        tv_control_in=(TextView)parent.findViewById(R.id.tv_control_in);
        tv_insurance=(TextView)parent.findViewById(R.id.tv_insurance);
        seekBar_blinds=(SeekBar)parent.findViewById(R.id.seekBar_blinds);
        rb_control_in=(RadioButton)parent.findViewById(R.id.rb_control_in);
        rb_insurance=(RadioButton)parent.findViewById(R.id.rb_insurance);
        btn_high_grade_set=(Button)parent.findViewById(R.id.btn_high_grade_set);
        btn_new_game=(Button)parent.findViewById(R.id.btn_new_game);


        rb_control_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //控制带入
                if (bRbControlIN){
                    bRbControlIN=false;
                    ((RadioButton)v).setChecked(false);
                    gameParam.isControlIn=false;
                    gameParam.total_in=0;
                }else{
                    bRbControlIN=true;
                    ((RadioButton)v).setChecked(true);
                    gameParam.isControlIn=true;
                    gameParam.total_in=0;
                }
                SetGameInfo();
            }
        });
        rb_insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bRbInsurance){

                    ((RadioButton)v).setChecked(false);
                    bRbInsurance=false;
                    gameParam.isInsurance=false;
                }else{
                    ((RadioButton)v).setChecked(true);
                    bRbInsurance=true;
                    gameParam.isInsurance=true;
                }
            }
        });

        tv_control_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bRbControlIN){
                    bRbControlIN=false;
                    rb_control_in.setChecked(false);
                    gameParam.isControlIn=false;
                    gameParam.total_in=0;
                }else{
                    bRbControlIN=true;
                    rb_control_in.setChecked(true);
                    gameParam.isControlIn=true;
                    gameParam.total_in=0;
                }
                SetGameInfo();
            }
        });

        tv_insurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bRbInsurance){
                    bRbInsurance=false;
                    rb_insurance.setChecked(false);
                    gameParam.isInsurance=false;
                }else{
                    bRbInsurance=true;
                    rb_insurance.setChecked(true);
                    gameParam.isInsurance=true;
                }
            }
        });

        btn_high_grade_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DzApplication application=(DzApplication)getActivity().getApplication();
                application.setGameParam(gameParam);

                Intent intent = new Intent();
                intent.setClass(getActivity(), GameSetHighGradeActivity.class);
                //此处要返回值，根据返回值回来更新界面的东西的
                startActivityForResult(intent,1);

            }
        });

        btn_new_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交后台游戏
                if (et_game_name.getText().toString().equals("")){
                    ToastUtil.showToastInScreenCenter(getActivity(),"请输入游戏的房间名");
                }
                Thread thread=new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        try{
                            //拼装url字符串
                            DzApplication applicatoin=(DzApplication)getActivity().getApplication();
                            JSONObject jsonObj = new JSONObject();
                            jsonObj.put("type",0);
                            jsonObj.put("name",et_game_name.getText().toString());
                            jsonObj.put("createuserid",applicatoin.getUserId());
                            jsonObj.put("clubid",0);
                            jsonObj.put("matchid",0);
                            jsonObj.put("duration",gameParam.timeLen);
                            jsonObj.put("BB",gameParam.blinds*2);
                            jsonObj.put("SB",gameParam.blinds);
                            jsonObj.put("maxplayers",gameParam.player_number);
                            jsonObj.put("mintakeinchips",gameParam.min_in);
                            jsonObj.put("isinsurance",gameParam.isInsurance);
                            jsonObj.put("isstraddle",gameParam.isStraddle);
                            jsonObj.put("is27",gameParam.is27);
                            jsonObj.put("ante",gameParam.ante);
                            jsonObj.put("isiprestrict",gameParam.isIpLimit);
                            jsonObj.put("isgpsrestrict",gameParam.isGpsLimit);
                            jsonObj.put("iscontroltakein",gameParam.isControlIn);
                            jsonObj.put("takeinchipsuplimit",gameParam.total_in);
                            jsonObj.put("maxtakeinchips",gameParam.max_in);

                            String strURL=getString(R.string.url_remote);
                            strURL+="func=creategametable&param="+jsonObj.toString();

                            URL url = new URL(strURL);
                            Request request = new Request.Builder().url(strURL).build();
                            Response response = DzApplication.getHttpClient().newCall(request).execute();
                            String result=response.body().string();

                            JSONObject jsonObject=new JSONObject(result);
                            if (jsonObject.getInt("ret")==0){
                                // 创建牌局成功
                                 String ssid=jsonObject.getString("msg");
                                String ip=jsonObject.getString("ip");
                                int port=jsonObject.getInt("port");

                                Intent intent = new Intent();
                                intent.putExtra("operation",1);//1表示创建牌局，2表示加入牌局
                                intent.putExtra("gameid",ssid);
                                intent.putExtra("ip",ip);
                                intent.putExtra("port",port);

                                intent.setClass(getActivity(), GameActivity.class);
                                startActivity(intent);

                            }else {
                                ToastUtil.showToastInScreenCenter(getActivity(),"创建牌局失败，错误原因："+jsonObject.getString("msg"));
                            }



                        }catch (Exception e){
                            ToastUtil.showToastInScreenCenter(getActivity(),"创建牌局异常，请稍后重试!"+e.toString());
                        }

                    }
                });
                thread.start();

            }
        });


        seekBar_blinds.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //跟随着大小盲注的变化，最小带入，最大带入也会发生变化,高级设置里的值也会变化
                gameParam.blinds=blindsValue[progress];
                gameParam.min_in=gameParam.blinds*200;
                gameParam.max_in=gameParam.blinds*1000;
                gameParam.player_number=9;//恢复默认值
                gameParam.total_in=0;
                gameParam.ante=0;
                gameParam.is27=true;
                gameParam.isGpsLimit=false;
                gameParam.isIpLimit=false;
                gameParam.isStraddle=false;
                gameParam.isAotoCard=true;
                gameParam.isInsurance=true;
                gameParam.isControlIn=false;
                rb_control_in.setChecked(false);
                rb_insurance.setChecked(true);
                SetGameInfo();
                tv_blinds.setText(gameParam.blinds+"/"+gameParam.blinds*2);
                tv_min_in.setText(gameParam.blinds*200+"");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }

    public void initData(){

        gameParam=new GameParam();
       //设置默认的值
        bRbControlIN=false ;
        rb_insurance.setChecked(false);
        bRbInsurance=true;
        rb_insurance.setChecked(true);
        seekBar_blinds.setProgress(5);
        tv_blinds.setText("10/20");
        tv_min_in.setText("2000");
        tv_game_info.setText("9人桌 | 最小带入：2000 | 最大带入：10000 | 开启2/7玩法 | 自动埋牌");
        DzApplication application =(DzApplication) getActivity().getApplication();
        tv_personl_money.setText(application.getUser().goldcoin+"");
        et_game_name.setText("我的房间");

        gameParam.player_number=9;
        gameParam.blinds=10;//小盲注的值
        gameParam.min_in=2000;
        gameParam.max_in=10000;
        gameParam.total_in=0;
        gameParam.ante=0;
        gameParam.is27=true;
        gameParam.isGpsLimit=false;
        gameParam.isIpLimit=false;
        gameParam.isStraddle=false;
        gameParam.isAotoCard=true;
        gameParam.timeLen=30;//30分钟
        gameParam.timeLenShow="30分钟";
        gameParam.isInsurance=true;

        //传到下一界面参数：人数，最小带入，最大带入，总带入，ante 以及5个参数

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void OnSelectRange(int rangeNum) {

        gameParam.timeLen=timelen[rangeNum];
        gameParam.timeLenShow=title[rangeNum];
    }

    public void SetGameInfo(){
        //游戏信息数据跟随变化而变化，所以这个写一个统一的方法来进行设置
        //7人桌/最小带入：8000/最大带入：28000/控制总带入：72000/Ante:24/开启强制straddle/开启IP限制/开启GPS限制/开启2/7玩法/自动埋牌
        String text="";
        text+=gameParam.player_number+"人桌";
        text+=" | 最小带入:"+gameParam.min_in;
        text+=" | 最大带入:"+gameParam.max_in;
        if (rb_control_in.isChecked()){
            text+=" | 控制总带入:"+gameParam.total_in;
        }
        if (gameParam.ante!=0){
            text+=" | Ante:"+gameParam.ante;
        }
        if (gameParam.isStraddle){
            text+=" | 开启强制Straddle";
        }
        if (gameParam.isIpLimit){
            text+=" | 开启IP限制";
        }
        if (gameParam.isGpsLimit){
            text+=" | 开启Gps限制";
        }
        if (gameParam.is27){
            text+=" | 开启2/7玩法";
        }
        if (gameParam.isAotoCard){
            text+=" | 自动埋牌";
        }
        tv_game_info.setText(text);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==9){
            //从Aplication里面取值，回写gameParam

            DzApplication application =(DzApplication)getActivity().getApplication();
            gameParam.player_number=application.getGameParam().player_number;
            gameParam.min_in=application.getGameParam().min_in;
            gameParam.max_in=application.getGameParam().max_in;
            gameParam.isControlIn=application.getGameParam().isControlIn;
            gameParam.total_in=application.getGameParam().total_in;
            gameParam.ante=application.getGameParam().ante;
            gameParam.isStraddle=application.getGameParam().isStraddle;
            gameParam.is27=application.getGameParam().is27;
            gameParam.isAotoCard=application.getGameParam().isAotoCard;
            gameParam.isGpsLimit=application.getGameParam().isGpsLimit;
            gameParam.isIpLimit=application.getGameParam().isIpLimit;
            SetGameInfo();
            bRbControlIN=gameParam.isControlIn;
            rb_control_in.setChecked(gameParam.isControlIn);
        }
    }
}
