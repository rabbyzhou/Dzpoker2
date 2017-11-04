package com.yijian.dzpoker.view;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import com.yijian.dzpoker.R;

import java.util.concurrent.TimeUnit;

public class RangeSliderBar2 extends View {
  private static final String TAG = RangeSliderBar2.class.getSimpleName();

  private static final long RIPPLE_ANIMATION_DURATION_MS = TimeUnit.MILLISECONDS.toMillis(700);

  private static final int DEFAULT_PAINT_STROKE_WIDTH = 5;

  private static final int DEFAULT_FILLED_COLOR = Color.parseColor("#3c78d8");

  private static final int DEFAULT_EMPTY_COLOR = Color.parseColor("#ff22cc");

//  private static final int DEFAULT_LINE_COLOR = Color.parseColor("#ff22cc");
//  private static final int DEFAULT_SELECTED_COLOR = Color.parseColor("#3c78d8");
  private static final int DEFAULT_RED_COLOR = Color.parseColor("#CC0000");

//蓝色 3c78d8   白色 fff22cc   红色cc0000

  private static final int DEFAULT_RANGE_COUNT = 5;

  private static final int DEFAULT_HEIGHT_IN_DP = 50;//默认高度,为画图部分的高度

  private  Drawable mCursorBG;

  protected Paint paint;





  protected Paint ripplePaint;
  protected Paint mPaint;
  protected Paint mPaint2;
  protected Paint mPaint3;

  private Rect mCursorRect;

  protected float radius;

  private int currentIndex;

  private float currentSlidingX;

  private float currentSlidingY;

  private float[] circlePositions;

  private int filledColor = DEFAULT_FILLED_COLOR;

  private int emptyColor = DEFAULT_EMPTY_COLOR;

  private int outColor = DEFAULT_RED_COLOR;



  private int rangeCount = DEFAULT_RANGE_COUNT;
  private int outRange = DEFAULT_RANGE_COUNT+1;

  private int barHeight;

  private float rippleRadius = 0.0f;

  private float downX;

  private float downY;

  private Path innerPath = new Path();

  private Path outerPath = new Path();



  private int layoutHeight;

  private int mCursorWidth,mCursorHeight;
  private int mBarLength;

  private  String[] arrTitle;

  public RangeSliderBar2(Context context) {
    this(context, null);
  }

  public RangeSliderBar2(Context context, AttributeSet attrs) {
    this(context, attrs, -1);
  }

  public RangeSliderBar2(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);


    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MySeekbarView);
      TypedArray sa = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.layout_height});
      try {
        /*attr name="rangeCount" format="integer"/>
        <attr name="outRange" format="integer"/>
        <attr name="filledColor" format="color"/>
        <attr name="emptyColor" format="color"/>
        <attr name="outColor" format="color"/>
        <attr name="cursorDrawable" format="reference"/><!-- 游标的图片-->*/
        layoutHeight = sa.getLayoutDimension(
                0, 0);
        rangeCount = a.getInt(
                R.styleable.MySeekbarView_rangeCount, DEFAULT_RANGE_COUNT);
        outRange = a.getInt(
                R.styleable.MySeekbarView_outRange, DEFAULT_RANGE_COUNT+1);


        filledColor = a.getColor(
                R.styleable.MySeekbarView_filledColor, DEFAULT_FILLED_COLOR);
        emptyColor = a.getColor(
                R.styleable.MySeekbarView_emptyColor, DEFAULT_EMPTY_COLOR);

        outColor= a.getColor(
                R.styleable.MySeekbarView_emptyColor, DEFAULT_EMPTY_COLOR);

        //设置背景图片
        mCursorBG=context.getResources().getDrawable(a.getResourceId(
                R.styleable.MySeekbarView_cursorDrawable, R.mipmap.circular_ico));
        mCursorWidth=mCursorBG.getIntrinsicWidth();
        mCursorHeight=mCursorBG.getIntrinsicHeight();



      } finally {
        a.recycle();
        sa.recycle();
      }
    }
    //arrTitle=new String[rangeCount];
    mCursorRect=new Rect();
    setRangeCount(rangeCount);

    //记录每一段的中心点
    circlePositions = new float[rangeCount];



    //画笔5px
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStrokeWidth(2.0f);
    paint.setStyle(Paint.Style.FILL_AND_STROKE);



    //写文字，未被选中
    mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint.setColor(Color.WHITE);
    mPaint.setTextSize(16);

    //写文字，被选中
    mPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
    mPaint2.setColor(Color.parseColor("#ad7b43"));
    mPaint2.setTextSize(24);

    getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
      @Override
      public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);

        // Update radius after we got new height
        //updateRadius(getHeight());

        // Compute drawing position again
        preComputeDrawingPosition();//计算值的位置

        // Ready to draw now
        return true;
      }
    });
    currentIndex = 0;
  }

  private void updateRadius(int height) {
    //barHeight = (int) (height * barHeightPercent);
//    radius = height * sliderRadiusPercent;
//    circleRadius = height * circleRadiusPercent;
  }

  public void setRangeCount(int rangeCount) {
    if (rangeCount < 2) {
      throw new IllegalArgumentException("rangeCount must be >= 2");
    }
    this.rangeCount = rangeCount;
    circlePositions = new float[rangeCount];

  }






  /**
   * Perform all the calculation before drawing, should only run once
   * 此处画图，要计算位置
   */
  private void preComputeDrawingPosition() {
    int w = getWidthWithPadding();
    int h = getHeightWithPadding();


    //整个界面，左右留半个图标的宽度 ，就是说真正刻度条的区间为控件长度减去滑动条的宽度

    w-=mCursorWidth;
    mBarLength=w;

    /** Space between each circle */
    int spacing = mBarLength / (rangeCount-1);

    /** Center vertical */
    int y = getPaddingTop() + h +mCursorHeight / 2;
    currentSlidingY = y;
    int x = getPaddingLeft() + mCursorWidth/2 ;

    /** Store the position of each circle index */
    for (int i = 0; i < rangeCount; ++i) {
      circlePositions[i] = x;
      if (i == currentIndex) {
        currentSlidingX = x;
      }
      x += spacing;
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
  }

  /**
   * Measures height according to the passed measure spec
   *
   * @param measureSpec int measure spec to use
   * @return int pixel size
   */
  private int measureHeight(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    int result;
    if (specMode == MeasureSpec.EXACTLY) {
      result = specSize;
    } else {
      final int height;
      if (layoutHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
        height = dpToPx(getContext(), DEFAULT_HEIGHT_IN_DP);
      } else if (layoutHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
        height = getMeasuredHeight();
      } else {
        height = layoutHeight;
      }
      result = height + getPaddingTop() + getPaddingBottom() + (2 * DEFAULT_PAINT_STROKE_WIDTH);
      if (specMode == MeasureSpec.AT_MOST) {
        result = Math.min(result, specSize);
      }
    }
    return result;
  }

  /**
   * Measures width according to the passed measure spec
   *
   * @param measureSpec int measure spec to use
   * @return int pixel size
   */
  private int measureWidth(int measureSpec) {
    int specMode = MeasureSpec.getMode(measureSpec);
    int specSize = MeasureSpec.getSize(measureSpec);
    int result;
    if (specMode == MeasureSpec.EXACTLY) {
      result = specSize;
    } else {
      result = specSize + getPaddingLeft() + getPaddingRight() + (2 * DEFAULT_PAINT_STROKE_WIDTH) + mCursorWidth*2;//这里必须游标图片超过circle的直径
      if (specMode == MeasureSpec.AT_MOST) {
        result = Math.min(result, specSize);
      }
    }
    return result;
  }

  private void updateCurrentIndex() {
    float min = Float.MAX_VALUE;
    int j = 0;
    /** Find the closest to x */
    for (int i = 0; i < rangeCount; ++i) {
      float dx = Math.abs(currentSlidingX - circlePositions[i]);
      if (dx < min) {
        min = dx;
        j = i;
      }
    }
    currentIndex = j;
    /** Correct position */
    currentSlidingX = circlePositions[j];
    downX = currentSlidingX;
    downY = currentSlidingY;
    animateRipple();
    invalidate();
  }

  private void animateRipple() {
    ObjectAnimator animator = ObjectAnimator.ofFloat(this, "radius", 0, radius);
    animator.setInterpolator(new AccelerateInterpolator());
    animator.setDuration(RIPPLE_ANIMATION_DURATION_MS);
    animator.start();
    animator.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {
        rippleRadius = 0;
      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {

      }
    });
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    float y = event.getY();
    float x = event.getX();
    final int action = event.getActionMasked();
    switch (action) {
      case MotionEvent.ACTION_DOWN:
        downX = x;
        downY = y;
        break;

      case MotionEvent.ACTION_MOVE:
        if (x >= circlePositions[0] && x <= circlePositions[rangeCount - 1]) {
          currentSlidingX = x;
          currentSlidingY = y;
          invalidate();
        }
        break;

      case MotionEvent.ACTION_UP:
        currentSlidingX = x;
        currentSlidingY = y;
        updateCurrentIndex();
        invalidate();
        if (onSelectRangeBarListener!=null){
          //这里采用加上半个间隔的值，再相除，
          int range= ((int) currentSlidingX-getPaddingLeft()-mCursorWidth/2 +mBarLength/(2*(rangeCount-1)))/(mBarLength/(rangeCount-1)) ;
          //int range = ((int)currentSlidingX-getPaddingLeft()-mCursorWidth+(getWidthWithPadding()/((rangeCount-1)*2)))/(getWidthWithPadding()/(rangeCount-1));
          // int range = ((int)currentSlidingX-getPaddingLeft()+((int)circleRadius+20)*3)/(getWidthWithPadding()/rangeCount)-1;
          onSelectRangeBarListener.OnSelectRange(range);
        }
        break;

    }
    return true;
  }

  private void drawCursor(Canvas canvas){

    mCursorRect.left = (int)currentSlidingX-mCursorWidth/2;
    mCursorRect.top = getPaddingTop()+getHeightWithPadding()-mCursorHeight;
    mCursorRect.right =(int)currentSlidingX+mCursorWidth/2;
    mCursorRect.bottom = getPaddingTop()+getHeightWithPadding();
    mCursorBG.setBounds(mCursorRect);
    mCursorBG.draw(canvas);

  }

  private void drawDefaultCircle(Canvas canvas) {
    paint.setColor(emptyColor);
    int h = getHeightWithPadding();
    int y = getPaddingTop()+ h- mCursorHeight/2;
    int range= ((int) currentSlidingX-getPaddingLeft()-mCursorWidth +mBarLength/(2*(rangeCount-1)))/(mBarLength/(rangeCount-1)) ;
    //int range = ((int)currentSlidingX-getPaddingLeft()+getWidthWithPadding()/(rangeCount*2))/(getWidthWithPadding()/rangeCount);
    for (int i = 0; i < rangeCount; ++i) {
      canvas.drawCircle(circlePositions[i], y, radius, paint);
      if (i==range){
        //选中，字体
        final String text2draw =arrTitle[i];
        final float textWidth = mPaint2.measureText(text2draw);
        Paint.FontMetrics fm = mPaint2.getFontMetrics();
        float mTextHeight = (int) Math.ceil(fm.leading - fm.ascent) - 2;


        canvas.drawText(text2draw,circlePositions[i]-textWidth/2, getPaddingTop()+ h- mCursorHeight-mTextHeight,mPaint2);

      }else{
        //未选中
        final String text2draw =arrTitle[i];
        final float textWidth = mPaint.measureText(text2draw);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        float mTextHeight = (int) Math.ceil(fm.leading - fm.ascent) - 2;
        canvas.drawText(arrTitle[i],circlePositions[i]-textWidth/2, getPaddingTop()+ h- mCursorHeight-mTextHeight,mPaint);

      }

      //canvas.drawText(String.valueOf(i),circlePositions[i]-10, y-30,mPaint);
    }
  }
  private void drawSlidRangeCircle(Canvas canvas,  int x0,int spc) {
//    paint.setColor(filledColor);
//    int h = getHeightWithPadding();
//    int y = getPaddingTop()+h- (mCursonHeight>> 1);
//    int range = (x0-getPaddingLeft()+((int)circleRadius+20)*3)/spc;
//    for (int i = 0; i < range; ++i) {
//      //canvas.drawCircle(circlePositions[i], y, circleRadius+20, paint);
//      //canvas.drawText(String.valueOf(i),circlePositions[i]-10, y+10,mPaint);
//    }
  }
  public int getHeightWithPadding() {
    return getHeight() - getPaddingBottom() - getPaddingTop();
  }

  public int getWidthWithPadding() {
    return getWidth() - getPaddingLeft() - getPaddingRight();
  }

  private void drawBar(Canvas canvas, int from, int to, int color) {
    paint.setColor(color);
    int h = getHeightWithPadding();
    int half = (barHeight >> 1);
    //int y = getPaddingTop() + (h >> 1);
    //这里设置成从底端画图
    int y = getPaddingTop() +h - (mCursorHeight >> 1);
    canvas.drawRect(from, y - half, to, y + half, paint);
  }

  private void drawBar(Canvas canvas, float from, float to,float height) {
    if ((from+to)/2<currentSlidingX){
      paint.setColor(filledColor);

    }else{
      paint.setColor(emptyColor);
    }
    int h = getHeightWithPadding();
    float top=getPaddingTop()+h-height-mCursorHeight/4;
    float bottom=getPaddingTop()+h-mCursorHeight/4;
    //int y = getPaddingTop() + (h >> 1);
    //这里设置成从底端画图
    int y = getPaddingTop() +h - (mCursorHeight >> 1);
    canvas.drawRect(from,top, to, bottom, paint);
  }



  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int w = getWidthWithPadding();
    int h = getHeightWithPadding();
    //这个要去掉图片的宽度,以及左右留空
    w-=mCursorWidth;

    int spacing = w /(rangeCount-1);

    //画图，要设置画笔的颜色
    //长度分成30份，高度为图片的高度，偏移量为1/4个图片的高
    float lineSpace=w/30; //将长度分为30等分
    float Topheight=mCursorHeight*3/4;
    float x0=getPaddingLeft()+mCursorWidth/2;
    for (int i=0;i<30;i++){
      drawBar(canvas,x0+1,x0+lineSpace/2-1,Topheight*i/30);
      x0+=lineSpace;
    }

   drawCursor(canvas);
  }

  @Override
  public Parcelable onSaveInstanceState() {
    Parcelable superState = super.onSaveInstanceState();
    SavedState ss = new SavedState(superState);
    ss.saveIndex = this.currentIndex;
    return ss;
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    if (!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }
    SavedState ss = (SavedState) state;
    super.onRestoreInstanceState(ss.getSuperState());
    this.currentIndex = ss.saveIndex;
  }

  static class SavedState extends BaseSavedState {
    int saveIndex;

    SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      this.saveIndex = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(this.saveIndex);
    }

    //required field that makes Parcelables from a Parcel
    public static final Creator<SavedState> CREATOR =
            new Creator<SavedState>() {
              public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
              }

              public SavedState[] newArray(int size) {
                return new SavedState[size];
              }
            };
  }

  /**
   * Helper method to convert dp to pixel
   * @param context
   * @param dp
   * @return
   */
  static int dpToPx(final Context context, final float dp) {
    return (int)(dp * context.getResources().getDisplayMetrics().density);
  }


  OnSelectRangeBarListener onSelectRangeBarListener;
  public interface OnSelectRangeBarListener{
    void OnSelectRange(int rangeNum);
  }
  public void setOnSelectRangeBarListener(OnSelectRangeBarListener onSelectRangeBarListener) {
    this.onSelectRangeBarListener = onSelectRangeBarListener;
  }
}
