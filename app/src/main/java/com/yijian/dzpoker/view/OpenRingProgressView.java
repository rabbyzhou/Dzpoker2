package com.yijian.dzpoker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.yijian.dzpoker.R;

/**
 * Created by c_huangl on 0013, 11/13/2017.
 */

public class OpenRingProgressView extends View {

    private int mMaxProgress = 100;
    private int mCircleLineStrokeWidth = 12;
    private int mTxtStrokeWidth = 3;

    // 画圆所在的距形区域
    private RectF mProgressRectF;
    private Paint mProgressPaint;
    private Canvas mCanvas;

    // 画非封闭圆环需要的角度
    private int mStartAngle = -250;
    private int mSweepAngle = 320;
    private int mRadius;
    private Context mContext;

    // 画图时, 调用者传过来的参数
    int[] mProgress = null;
    int[] mColors = null; // 进度条的颜色, 和百分比显示的颜色一致
    int[] mTexts = null; // 进度条的说明文字
    int[] mTextColors = null; // 进度条的说明文字的颜色


    public OpenRingProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mProgressRectF = new RectF();
        mProgressPaint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        getRadius();
        drawBackgroundRecf();
        drawBackgroundArc();

        if (mProgress != null){
            drawView();
        }
    }

    private void drawBackgroundRecf() {
        // 位置
        mProgressRectF.left = mCircleLineStrokeWidth / 2;
        mProgressRectF.top = mCircleLineStrokeWidth / 2;
        mProgressRectF.right = mRadius - mCircleLineStrokeWidth / 2;
        mProgressRectF.bottom = mRadius - mCircleLineStrokeWidth / 2;
    }

    private void getRadius() {
        mRadius = this.getWidth() - mCircleLineStrokeWidth * 2;
    }

    private void drawBackgroundArc() {
        mCanvas.drawColor(Color.TRANSPARENT);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mContext.getResources().getColor(R.color.game_ring_process_bg));
        mProgressPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mCanvas.drawArc(mProgressRectF, mStartAngle, mSweepAngle, false, mProgressPaint);
    }

    private void drawView(){
        for (int i = 0; i < mProgress.length; i++) {
            drawProgress(mProgress[i],mColors[i]);
        }
        drawText(mProgress, mColors, mTexts,mTextColors);
    }

    private void drawProgress(int progress, int color) {

        mCanvas.drawColor(Color.TRANSPARENT);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mContext.getResources().getColor(color));
        mProgressPaint.setStrokeWidth(mCircleLineStrokeWidth);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        int sweepAngle = Math.round(((float)progress / (float)mMaxProgress) * mSweepAngle);
        mCanvas.drawArc(mProgressRectF, mStartAngle, sweepAngle, false, mProgressPaint);
    }

    private void drawText( int[] progress, int[] color, int[] texts, int[] textColors) {

        int totalNum = progress.length;

        int mediumSize = Math.round(mContext.getResources().getDimension(R.dimen.game_record_text_medium_size));
        int smallSize = Math.round(mContext.getResources().getDimension(R.dimen.game_record_text_small_size));
        int columnSpace = Math.round(mContext.getResources().getDimension(R.dimen.game_ring_progress_text_column_space));
        int onePartUsedSpace = mediumSize + smallSize + columnSpace;
        int allTextUsedSpace = onePartUsedSpace * totalNum;
        int startY = (mRadius - allTextUsedSpace)/(totalNum);

        for (int i = 0; i < totalNum; i++) {

            mProgressPaint.setStrokeWidth(mTxtStrokeWidth);
            mProgressPaint.setStyle(Paint.Style.FILL);

            //draw 说明文字
            mProgressPaint.setColor(mContext.getResources().getColor(textColors[i]));
            mProgressPaint.setTextSize(mediumSize);
            String textTitle = mContext.getResources().getString(texts[i]);

            //获得说明文字的宽度
            Paint textPaint = new Paint();
            int textWidth = Math.round(textPaint.measureText(textTitle));
            int titleX = (mRadius - textWidth)/2 - textWidth;
            int titleY = i*onePartUsedSpace + (i+1)*startY;

            mCanvas.drawText(textTitle, titleX, titleY, mProgressPaint);

            //draw 百分比
            mProgressPaint.setColor(mContext.getResources().getColor(color[i]));
            mProgressPaint.setTextSize(smallSize);
            String progressText = progress[i] + "%";

            //获得百分比文字的宽度
            Paint valuePaint = new Paint();
            int valueWidth = Math.round(valuePaint.measureText(progressText));
            int valueX = (mRadius - valueWidth)/2 - valueWidth;
            int valueY = titleY + columnSpace + mediumSize;
            mCanvas.drawText(progressText, valueX, valueY, mProgressPaint);
        }
    }

    public void setProgress(int[] progresses, int[] colors, int[] texts, int[] textColors) {
        setProgresses(progresses, colors, texts, textColors);
        this.invalidate();
    }

    public void setProgressNotInUiThread(int[] progresses, int[] colors, int[] texts, int[] textColors) {
        setProgresses(progresses, colors, texts, textColors);
        this.postInvalidate();
    }

    public void setProgresses(int[] progresses, int[] colors, int[] texts, int[] textColors) {
        this.mProgress = progresses;
        this.mColors = colors;
        this.mTexts = texts;
        this.mTextColors = textColors;
    }
    
    public void setCircleLineStrokeWidth(int mCircleLineStrokeWidth) {
        this.mCircleLineStrokeWidth = mCircleLineStrokeWidth;
        this.invalidate();
    }
}