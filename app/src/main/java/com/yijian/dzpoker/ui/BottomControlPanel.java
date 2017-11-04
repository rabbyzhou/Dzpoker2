package com.yijian.dzpoker.ui;

/**
 * Created by rabby on 2017/8/9.
 */

import java.util.ArrayList;
import java.util.List;



import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.yijian.dzpoker.R;
import com.yijian.dzpoker.constant.Constant;

public class BottomControlPanel extends RelativeLayout implements View.OnClickListener {
    private Context mContext;
    private ImageText mMainBtn = null;
    private ImageText mGameBtn = null;
    private ImageText mClubBtn = null;
    private ImageText mMeBtn = null;
    private int DEFALUT_BACKGROUND_COLOR = Color.rgb(243, 243, 243); //Color.rgb(192, 192, 192)
    private BottomPanelCallback mBottomCallback = null;
    private List<ImageText> viewList = new ArrayList<ImageText>();

    public interface BottomPanelCallback{
        public void onBottomPanelClick(int itemId);
    }
    public BottomControlPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }
    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        mMainBtn = (ImageText)findViewById(R.id.btn_main);
        mGameBtn = (ImageText)findViewById(R.id.btn_game);
        mClubBtn = (ImageText)findViewById(R.id.btn_club);
        mMeBtn = (ImageText)findViewById(R.id.btn_me);
        setBackgroundColor(DEFALUT_BACKGROUND_COLOR);
        viewList.add(mMainBtn);
        viewList.add(mGameBtn);
        viewList.add(mClubBtn);
        viewList.add(mMeBtn);
        super.onFinishInflate();

    }
    public void initBottomPanel(){
        if(mMainBtn != null){
            mMainBtn.setImage(R.drawable.message_unselected);
            mMainBtn.setText("首页");
        }
        if(mGameBtn != null){
            mGameBtn.setImage(R.drawable.contacts_unselected);
            mGameBtn.setText("游戏");
        }
        if(mClubBtn != null){
            mClubBtn.setImage(R.drawable.news_unselected);
            mClubBtn.setText("俱乐部");
        }
        if(mMeBtn != null){
            mMeBtn.setImage(R.drawable.setting_unselected);
            mMeBtn.setText("我");
        }
        setBtnListener();

    }
    private void setBtnListener(){
        int num = this.getChildCount();
        for(int i = 0; i < num; i++){
            View v = getChildAt(i);
            if(v != null){
                v.setOnClickListener(this);
            }
        }
    }
    public void setBottomCallback(BottomPanelCallback bottomCallback){
        mBottomCallback = bottomCallback;
    }
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        initBottomPanel();
        int index = -1;
        switch(v.getId()){
            case R.id.btn_main:
                index = Constant.BTN_FLAG_MAIN;
                mMainBtn.setChecked(Constant.BTN_FLAG_MAIN);
                break;
            case R.id.btn_game:
                index = Constant.BTN_FLAG_GAME;
                mGameBtn.setChecked(Constant.BTN_FLAG_GAME);
                break;
            case R.id.btn_club:
                index = Constant.BTN_FLAG_CLUB;
                mClubBtn.setChecked(Constant.BTN_FLAG_CLUB);
                break;
            case R.id.btn_me:
                index = Constant.BTN_FLAG_ME;
                mMeBtn.setChecked(Constant.BTN_FLAG_ME);
                break;
            default:break;
        }
        if(mBottomCallback != null){
            mBottomCallback.onBottomPanelClick(index);
        }
    }
    public void defaultBtnChecked(){

        if(mGameBtn != null){
            mGameBtn.setChecked(Constant.BTN_FLAG_GAME);
        }
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // TODO Auto-generated method stub
        super.onLayout(changed, left, top, right, bottom);
        layoutItems(left, top, right, bottom);
    }
    /**最左边和最右边的view由母布局的padding进行控制位置。这里需对第2、3个view的位置重新设置
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    private void layoutItems(int left, int top, int right, int bottom){
        int n = getChildCount();
        if(n == 0){
            return;
        }
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();
        Log.i("yanguoqi", "paddingLeft = " + paddingLeft + " paddingRight = " + paddingRight);
        int width = right - left;
        int height = bottom - top;
        Log.i("yanguoqi", "width = " + width + " height = " + height);
        int allViewWidth = 0;
        for(int i = 0; i< n; i++){
            View v = getChildAt(i);
            Log.i("yanguoqi", "v.getWidth() = " + v.getWidth());
            allViewWidth += v.getWidth();
        }
        int blankWidth = (width - allViewWidth - paddingLeft - paddingRight) / (n - 1);
        Log.i("yanguoqi", "blankV = " + blankWidth );

        LayoutParams params1 = (LayoutParams) viewList.get(1).getLayoutParams();
        params1.leftMargin = blankWidth;
        viewList.get(1).setLayoutParams(params1);

        LayoutParams params2 = (LayoutParams) viewList.get(2).getLayoutParams();
        params2.leftMargin = blankWidth;
        viewList.get(2).setLayoutParams(params2);
    }



}
