package com.yijian.dzpoker.activity.game;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.activity.base.BaseBackActivity;
import com.yijian.dzpoker.view.RangeSliderBar;

public class GameSetHighGradeActivity extends BaseBackActivity implements RangeSliderBar.OnSelectRangeBarListener {
    private RangeSliderBar rangeSliderBar;
    private TextView tv_min_in,tv_max_in,tv_total_in,tv_ante,tv_rb_straddle,tv_rb_auto_card,tv_rb_ip_limit,tv_rb_27,tv_rb_gps_limit;
    private Button btn_confirm;
    private RadioButton rb_straddle,rb_auto_card,rb_ip_limit,rb_27,rb_gps_limit;
    private SeekBar seekBar_min_in,seekBar_max_in,seekBar_total_in,seekBar_ante;
    private int mPlayerNumber;
    private int mMin_in,mMax_in,mTotal_in,mAnte;
    private int mBaseMinIn;//此处为小盲注的200倍
    private int mMinInInterval,mMaxInInterval,mTotalInInterval;
    private boolean bControlIn;






    private int[] mGamePlayerNumber=new int[]{2,3,4,5,6,7,8,9};
    private Boolean bIsStraddle ,bISAutoCard,bIsIpLimit,bIs27,bIsGpsLimit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBaseMinIn=application.getGameParam().blinds*100;
        mMinInInterval=application.getGameParam().blinds*100;
        mMaxInInterval=application.getGameParam().blinds*200;
        mTotalInInterval=application.getGameParam().blinds*200;
        bIsStraddle=application.getGameParam().isStraddle;
        bIs27=application.getGameParam().is27;
        bControlIn=application.getGameParam().isControlIn;
        bISAutoCard=application.getGameParam().isAotoCard;
        bIsGpsLimit=application.getGameParam().isGpsLimit;
        bIsIpLimit=application.getGameParam().isIpLimit;
        initData();
        setToolbarTitle("高级设置");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_game_set_high_grade;
    }

   private void initData(){
       //初始化界面的值
       //此处值是放在application里面的
       //人数的值
       rangeSliderBar.setProgress(application.getGameParam().player_number-2);
       //最小带入
       mMin_in=application.getGameParam().min_in;
       int progress=mMin_in/mMinInInterval-1;
       seekBar_min_in.setProgress(progress);
       tv_min_in.setText(mMin_in+"");
       //最大带入
       mMax_in=application.getGameParam().max_in;
       if (mMax_in!=0) {
           progress=mMax_in /mMaxInInterval- 1;
           seekBar_max_in.setProgress(progress);
           tv_max_in.setText(mMax_in+"");
       }else{
           seekBar_max_in.setProgress(seekBar_max_in.getMax());
           tv_max_in.setText("无上限");
       }



       //总带入
       mTotal_in=application.getGameParam().total_in;
       if (application.getGameParam().isControlIn){
           //此处不要判断最大值的情况，最大值就是无上限，就是不控制带入，在滑动代码中进行控制掉
           bControlIn=true;//这个要有个标志，无上限是不控制，0，也是控制总带入，虽然这种情况不应该存在
           progress=mTotal_in/mTotalInInterval;
           seekBar_total_in.setProgress(progress);
           tv_total_in.setText(mTotal_in+"");

       }else{
           bControlIn=false;
           tv_total_in.setText("无上限");
           seekBar_total_in.setProgress(seekBar_total_in.getMax());
       }
       //ante 此处，ante分为10份，计算取整，
       mAnte=application.getGameParam().ante;
       tv_ante.setText(mAnte+"");
       if (mAnte==0){
           seekBar_ante.setProgress(0);
       }else{
           int maxValue=application.getGameParam().blinds *2;
           double interval=((double)maxValue)/10 ;
           int value[]=new int[10];
           for (int i=0;i<seekBar_ante.getMax();i++){
               value[i]=(int)(interval*i+0.5);
              if (mAnte==value[i]){
                  seekBar_ante.setProgress(i);
                  break;
              }
           }


//           for (int i=1;i<seekBar_ante.getMax()+1;i++){
//               if (maxValue*i/10> mAnte){
//                   seekBar_ante.setProgress(i-1);
//               }
//           }
       }

       if (application.getGameParam().isStraddle){
           rb_straddle.setChecked(true);
       }

       if (application.getGameParam().isAotoCard){
           rb_auto_card.setChecked(true);
       }

       if (application.getGameParam().isGpsLimit){
           rb_gps_limit.setChecked(true);
       }
       if (application.getGameParam().is27){
           rb_27.setChecked(true);
       }
       if (application.getGameParam().isIpLimit){
           rb_ip_limit.setChecked(true);
       }

       seekBar_min_in.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                  mMin_in=mMinInInterval*(seekBar_min_in.getProgress()+1);
                  tv_min_in.setText(mMin_in+"");
                  if (mMin_in>mMax_in){
                      //设置maxin的值,这里需要计算最大的progress的值

                      mMax_in=mMin_in;
                      progress=(mMax_in+ mMaxInInterval/2)/mMaxInInterval- 1;
                      seekBar_max_in.setProgress(progress);
                      tv_max_in.setText(mMax_in+"");

                  }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_max_in.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress==seekBar.getMax()){
                    mMax_in=0;
                    tv_max_in.setText("无上限");
                }else{
                    mMax_in=mMaxInInterval*(seekBar_max_in.getProgress()+1);
                    tv_max_in.setText(mMax_in+"");
                    if (mMin_in>mMax_in){
                        //设置minin的值
                        mMin_in=mMax_in;
                        progress=mMin_in /mMinInInterval- 1;
                        seekBar_min_in.setProgress(progress);
                        tv_min_in.setText(mMin_in+"");

                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_total_in.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress==seekBar.getMax()){
                    mTotal_in=0;
                    //bControlIn=false;
                    tv_total_in.setText("无上限");
                }else{
                    //bControlIn=true;
                    mTotal_in=mTotalInInterval*seekBar_total_in.getProgress();
                    tv_total_in.setText(mTotal_in+"");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        seekBar_ante.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                   //ante.这个要计算取整的

                mAnte=(application.getGameParam().blinds *2 *seekBar.getProgress()+5)/10;
                tv_ante.setText(mAnte+"");



            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


   }

    @Override
    protected void initViews() {
        super.initViews();
        rangeSliderBar = (RangeSliderBar)findViewById(R.id.rsv_gamer_number);
        rangeSliderBar.setRangeCount(8);
        String[] title=new String[]{"2人","3人","4人","5人","6人","7人","8人","9人"};
        rangeSliderBar.setTitle(title);
        rangeSliderBar.setOnSelectRangeBarListener(this);

        tv_min_in=(TextView)findViewById(R.id.tv_min_in);
        tv_max_in=(TextView)findViewById(R.id.tv_max_in);
        tv_total_in=(TextView)findViewById(R.id.tv_total_in);
        tv_ante=(TextView)findViewById(R.id.tv_ante);
        tv_rb_straddle=(TextView)findViewById(R.id.tv_rb_straddle);
        tv_rb_auto_card=(TextView)findViewById(R.id.tv_rb_auto_card);
        tv_rb_ip_limit=(TextView)findViewById(R.id.tv_rb_ip_limit);
        tv_rb_27=(TextView)findViewById(R.id.tv_rb_27);
        tv_rb_gps_limit=(TextView)findViewById(R.id.tv_rb_gps_limit);

        btn_confirm=(Button)findViewById(R.id.btn_confirm);
        rb_straddle=(RadioButton)findViewById(R.id.rb_straddle);
        rb_auto_card=(RadioButton)findViewById(R.id.rb_auto_card);
        rb_ip_limit=(RadioButton)findViewById(R.id.rb_ip_limit);
        rb_27=(RadioButton)findViewById(R.id.rb_27);
        rb_gps_limit=(RadioButton)findViewById(R.id.rb_gps_limit);

        tv_rb_straddle.setOnClickListener(this);
        tv_rb_auto_card.setOnClickListener(this);
        tv_rb_ip_limit.setOnClickListener(this);
        tv_rb_27.setOnClickListener(this);
        tv_rb_gps_limit.setOnClickListener(this);

        btn_confirm.setOnClickListener(this);
        rb_straddle.setOnClickListener(this);
        rb_auto_card.setOnClickListener(this);
        rb_ip_limit.setOnClickListener(this);
        rb_27.setOnClickListener(this);
        rb_gps_limit.setOnClickListener(this);

//        tv_back.setOnClickListener(this);
        //TODO:qipu
        seekBar_min_in=(SeekBar)findViewById(R.id.seekBar_min_in);
        seekBar_max_in=(SeekBar)findViewById(R.id.seekBar_max_in);
        seekBar_total_in=(SeekBar)findViewById(R.id.seekBar_total_in);
        seekBar_ante=(SeekBar)findViewById(R.id.seekBar_ante);





    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.btn_confirm:
                //确认，返回,保证回调的时候修改上一界面的值
                application.getGameParam().player_number=mPlayerNumber;
                application.getGameParam().min_in=mMin_in;
                application.getGameParam().max_in=mMax_in;
                application.getGameParam().isControlIn=bControlIn;
                application.getGameParam().total_in=mTotal_in;
                application.getGameParam().ante=mAnte;
                application.getGameParam().isStraddle=bIsStraddle;
                application.getGameParam().is27=bIs27;
                application.getGameParam().isAotoCard=bISAutoCard;
                application.getGameParam().isGpsLimit=bIsGpsLimit;
                application.getGameParam().isIpLimit=bIsIpLimit;
                setResult(9);
                finish();
                break;
            case (R.id.tv_rb_straddle ) :
                if (bIsStraddle){
                    bIsStraddle=false;
                    rb_straddle.setChecked(false);

                }else{
                    bIsStraddle=true;
                    rb_straddle.setChecked(true);

                }
                break;
            case R.id.rb_straddle:
                if (bIsStraddle){
                    bIsStraddle=false;
                    rb_straddle.setChecked(false);

                }else{
                    bIsStraddle=true;
                    rb_straddle.setChecked(true);

                }
                break;
            case R.id.tv_rb_auto_card:

                if (bISAutoCard){
                    bISAutoCard=false;
                    rb_auto_card.setChecked(false);

                }else{
                    bISAutoCard=true;
                    rb_auto_card.setChecked(true);

                }
                break;
            case R.id.rb_auto_card:
                if (bISAutoCard){
                    bISAutoCard=false;
                    rb_auto_card.setChecked(false);

                }else{
                    bISAutoCard=true;
                    rb_auto_card.setChecked(true);

                }
                break;
            case R.id.tv_rb_ip_limit:
                if (bIsIpLimit){
                    bIsIpLimit=false;
                    rb_ip_limit.setChecked(false);

                }else{
                    bIsIpLimit=true;
                    rb_ip_limit.setChecked(true);

                }
                break;
            case R.id.rb_ip_limit:
                if (bIsIpLimit){
                    bIsIpLimit=false;
                    rb_ip_limit.setChecked(false);
                }else{
                    bIsIpLimit=true;
                    rb_ip_limit.setChecked(true);
                }
                break;
            case R.id.tv_rb_27:
                if (bIs27){
                    bIs27=false;
                    rb_27.setChecked(false);
                }else{
                    bIs27=true;
                    rb_27.setChecked(true);
                }
                break;
            case R.id.rb_27:
                if (bIs27){
                    bIs27=false;
                    rb_27.setChecked(false);
                }else{
                    bIs27=true;
                    rb_27.setChecked(true);
                }
                break;
            case R.id.tv_rb_gps_limit:
                if (bIsGpsLimit){
                    bIsGpsLimit=false;
                    rb_gps_limit.setChecked(false);
                }else{
                    bIsGpsLimit=true;
                    rb_gps_limit.setChecked(true);
                }
                break;
            case R.id.rb_gps_limit:
                if (bIsGpsLimit){
                    bIsGpsLimit=false;
                    rb_gps_limit.setChecked(false);
                }else{
                    bIsGpsLimit=true;
                    rb_gps_limit.setChecked(true);
                }
                break;
        }

    }
    @Override
    public void OnSelectRange(int rangeNum) {
        mPlayerNumber=rangeNum+2;

    }
}
