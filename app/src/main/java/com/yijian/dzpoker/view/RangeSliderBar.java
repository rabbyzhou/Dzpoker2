package com.yijian.dzpoker.view;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Region;
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

public class RangeSliderBar extends View {
  private static final String TAG = RangeSliderBar.class.getSimpleName();

  private static final long RIPPLE_ANIMATION_DURATION_MS = TimeUnit.MILLISECONDS.toMillis(700);

  private static final int DEFAULT_PAINT_STROKE_WIDTH = 5;

  private static final int DEFAULT_FILLED_COLOR = Color.parseColor("#FFA500");

  private static final int DEFAULT_EMPTY_COLOR = Color.parseColor("#C3C3C3");



  private static final int DEFAULT_RANGE_COUNT = 5;

  private static final int DEFAULT_HEIGHT_IN_DP = 50;//默认高度,为画图部分的高度

  private  Drawable mCursorBG;

  protected Paint paint;

  protected Paint ripplePaint;
  protected Paint mPaint;
  protected Paint mPaint2;

  private Rect mCursorRect;

  protected float radius;

  private int currentIndex;

  private float currentSlidingX;

  private float currentSlidingY;

  private float[] circlePositions;

  private int filledColor = DEFAULT_FILLED_COLOR;

  private int emptyColor = DEFAULT_EMPTY_COLOR;

  private int rangeCount = DEFAULT_RANGE_COUNT;

  private int barHeight;

  private float rippleRadius = 0.0f;

  private float downX;

  private float downY;

  private Path innerPath = new Path();

  private Path outerPath = new Path();



  private int layoutHeight;

  private int mCursorWidth,mCursonHeight;
  private int mBarLength;

  private  String[] arrTitle;

  public RangeSliderBar(Context context) {
    this(context, null);
  }

  public RangeSliderBar(Context context, AttributeSet attrs) {
    this(context, attrs, -1);
  }

  public RangeSliderBar(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);


    if (attrs != null) {
      TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RangeSliderView);
      TypedArray sa = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.layout_height});
      try {
        layoutHeight = sa.getLayoutDimension(
                0, 0);
        rangeCount = a.getInt(
                R.styleable.RangeSliderView_rangeCount, DEFAULT_RANGE_COUNT);


        filledColor = a.getColor(
                R.styleable.RangeSliderView_filledColor, DEFAULT_FILLED_COLOR);
        emptyColor = a.getColor(
                R.styleable.RangeSliderView_emptyColor, DEFAULT_EMPTY_COLOR);

        //设置背景图片
        mCursorBG=context.getResources().getDrawable(a.getResourceId(
                R.styleable.RangeSliderView_cursorDrawable, R.mipmap.cursor));
        mCursorWidth=mCursorBG.getIntrinsicWidth();
        mCursonHeight=mCursorBG.getIntrinsicHeight();

        barHeight=a.getDimensionPixelSize(R.styleable.RangeSliderView_barHeight,10);
        radius=barHeight*4/3;

      } finally {
        a.recycle();
        sa.recycle();
      }
    }
    //arrTitle=new String[rangeCount];
    mCursorRect=new Rect();
    setRangeCount(rangeCount);


    circlePositions = new float[rangeCount];
    //画笔5px
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setStrokeWidth(DEFAULT_PAINT_STROKE_WIDTH);
    paint.setStyle(Paint.Style.FILL_AND_STROKE);

    ////画笔2px
    ripplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    ripplePaint.setStrokeWidth(2.0f);
    ripplePaint.setStyle(Paint.Style.FILL_AND_STROKE);

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
        preComputeDrawingPosition();

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
    arrTitle=new String[rangeCount];
    circlePositions = new float[rangeCount];
  }

  public void setTitle(String[] title) {
    arrTitle=title;

  }

  public void setProgress(int index) {
    currentIndex=index;
    invalidate();

  }


  /**
   * Perform all the calculation before drawing, should only run once
   * 此处画图，要计算位置
   */
  private void preComputeDrawingPosition() {
    int w = getWidthWithPadding();
    int h = getHeightWithPadding();

    //此处宽度计算，两边的点，需要各留一个游标图片的宽度，目的有2：1.游标图片中心点重合，需要左右各留半个长度，2：上面的字体，有可能会比游标图片长，所以再多预留半个游标图片

    w-=mCursorWidth*2;
    mBarLength=w;

    /** Space between each circle */
    int spacing = mBarLength / (rangeCount-1);

    /** Center vertical */
    int y = getPaddingTop() + h +mCursonHeight / 2;
    currentSlidingY = y;
    int x = getPaddingLeft() + mCursorWidth ;

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
          int range= ((int) currentSlidingX-getPaddingLeft()-mCursorWidth +mBarLength/(2*(rangeCount-1)))/(mBarLength/(rangeCount-1)) ;
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
    mCursorRect.top = getPaddingTop()+getHeightWithPadding()-mCursonHeight;
    mCursorRect.right =(int)currentSlidingX+mCursorWidth/2;
    mCursorRect.bottom = getPaddingTop()+getHeightWithPadding();
    mCursorBG.setBounds(mCursorRect);
    mCursorBG.draw(canvas);
  }

  private void drawDefaultCircle(Canvas canvas) {
    paint.setColor(emptyColor);
    int h = getHeightWithPadding();
    int y = getPaddingTop()+ h- mCursonHeight/2;
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


        canvas.drawText(text2draw,circlePositions[i]-textWidth/2, getPaddingTop()+ h- mCursonHeight-mTextHeight,mPaint2);

      }else{
        //未选中
        final String text2draw =arrTitle[i];
        final float textWidth = mPaint.measureText(text2draw);
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        float mTextHeight = (int) Math.ceil(fm.leading - fm.ascent) - 2;
        canvas.drawText(arrTitle[i],circlePositions[i]-textWidth/2, getPaddingTop()+ h- mCursonHeight-mTextHeight,mPaint);

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
    int y = getPaddingTop() +h - (mCursonHeight >> 1);
    canvas.drawRect(from, y - half, to, y + half, paint);
  }



  @Override
  public void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    int w = getWidthWithPadding();
    int h = getHeightWithPadding();
    //这个要去掉图片的宽度,以及左右留空
    w-=mCursorWidth*2;

    int spacing = w / (rangeCount-1);

    int border = (spacing >> 1);
    int x0 = getPaddingLeft() + border;
    /** Draw empty bar */
    drawBar(canvas, (int) circlePositions[0], (int) circlePositions[rangeCount - 1], emptyColor);

    /** Draw filled bar */
    // drawBar(canvas, x0, (int) currentSlidingX, filledColor);

    /** Draw the selected range circle */
    paint.setColor(filledColor);
    drawDefaultCircle(canvas);
    //保留代码原型，此处用了图片做滑动游标，所以不要画圆，仅仅需要设置字的颜色
    //drawSlidRangeCircle(canvas,(int)currentSlidingX,spacing);
//    drawRippleEffect(canvas);

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
