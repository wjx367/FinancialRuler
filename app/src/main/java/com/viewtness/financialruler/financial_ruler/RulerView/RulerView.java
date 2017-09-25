package com.viewtness.financialruler.financial_ruler.RulerView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Scroller;

import com.viewtness.financialruler.financial_ruler.R;

/**
 * Title:RulerView
 * Package:com.viewtness.financialruler.financial_ruler.RulerView
 * Description:TODO
 * Author: viewtness
 * Date: 2017/9/25
 * Version: V1.0.0
 */
public class RulerView extends View {
    //最大刻度
    private int maxValue;
    //最小刻度
    private int minValue;
    //刻度字体大小
    private int scaleTextSize;
    //刻度字体颜色
    private int scaleTextColor;
    //刻度超过颜色
    private int scaleSelectColor;
    //刻度超过背景
    private int scaleSelectBackgroundColor;
    //刻度未超颜色
    private int scaleUnSelectColor;
    //标记颜色
    private int cursorColor;
    //标尺开始位置
    private int currLocation = 0;
    //一屏显示Item
    private int showItemSize;
    //一个刻度的大小
    private int oneItemValue;
    //画线Paint
    private Paint paint;
    //画字Paint
    private Paint paintText;
    //屏幕宽度
    private int screenWidth;
    //尺子控件总宽度
    private float viewWidth;
    //尺子控件总宽度
    private float viewHeight;
    //刻度宽度
    private int scaleWidth;
    //刻度高度
    private float scaleHeight = 40;
    //滚动器
    private Scroller scroller;
    // 手势识别
    private GestureDetector gestureDetector;

    //滚动偏移量
    private int scrollingOffset;
    // 是否在滚动
    private boolean isScrollingPerformed;
    // 最低速度
    private static final int MIN_DELTA_FOR_SCROLLING = 1;
    //最后一次滚动到哪
    private int lastScrollX;
    // 消息
    private final int MESSAGE_SCROLL = 0;

    private OnRulerChangeListener onRulerChangeListener;
    private int yDown;
    private int yMove;
    //    private PtrClassicFrameLayout ptrClassicFrameLayout;
    private Context context;
    private AttributeSet attrs;
    public void setOnRulerChangeListener(OnRulerChangeListener onRulerChangeListener) {
        this.onRulerChangeListener = onRulerChangeListener;
    }

    public void setCurrLocation(int currLocation) {
        this.currLocation = currLocation -minValue;
    }

    public RulerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        //最大刻度
        maxValue = typedArray.getDimensionPixelOffset(R.styleable.RulerView_max_value, 102000);
        //最小刻度
        minValue = typedArray.getDimensionPixelOffset(R.styleable.RulerView_min_value, 2000);
        //刻度字体大小
        scaleTextSize = typedArray.getDimensionPixelOffset(R.styleable.RulerView_scale_text_size, 38);
        //刻度字体颜色
        scaleTextColor = typedArray.getColor(R.styleable.RulerView_scale_text_color, Color.parseColor("#e1e3eb"));
        //刻度超过颜色
        scaleSelectColor = typedArray.getColor(R.styleable.RulerView_scale_select_color, Color.parseColor("#e1e3eb"));
        //刻度超过颜色背景
        scaleSelectBackgroundColor = typedArray.getColor(R.styleable.RulerView_scale_select_background_color, Color.parseColor("#e1e3eb"));
        //刻度未超颜色
        scaleUnSelectColor = typedArray.getColor(R.styleable.RulerView_scale_unselect_color, Color.parseColor("#e1e3eb"));
        //标记颜色
        cursorColor = typedArray.getColor(R.styleable.RulerView_tag_color, Color.parseColor("#e1e3eb"));
        if (currLocation == 0) {
            //标尺开始显示位置
            currLocation = typedArray.getDimensionPixelOffset(R.styleable.RulerView_start_location, 0);
        }
        //一屏显示Item
        showItemSize = typedArray.getInteger(R.styleable.RulerView_show_item_size, 5);
        //一个刻度的大小
        oneItemValue = typedArray.getInteger(R.styleable.RulerView_show_item_size, 1000);
        //回收
        typedArray.recycle();
        //一个刻度的宽度
        scaleWidth = (screenWidth / (showItemSize * 10));
        //尺子长度总的个数*一个的宽度
        viewWidth = (maxValue -minValue) / oneItemValue * scaleWidth;
        //滚动计算器
        scroller = new Scroller(context);
        //手势解析器
        gestureDetector = new GestureDetector(context, gestureListener);
        gestureDetector.setIsLongpressEnabled(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawBottomLine(canvas);
        drawScale(canvas);
        drawCursor(canvas);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = View.MeasureSpec.getSize(heightMeasureSpec);

    }

    private void drawBottomLine(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(8);
        paint.setColor(scaleUnSelectColor);
        //计算游标开始绘制的位置
        float startLocation = (screenWidth / 2) - ((scaleWidth * (currLocation / oneItemValue)));
        Rect bounds = new Rect();
        float location = startLocation + (maxValue-minValue) / oneItemValue * scaleWidth;
        canvas.drawLine(startLocation, viewHeight, location - bounds.width() / 2, viewHeight, paint);
    }


    private void drawCursor(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(6);
        paint.setColor(cursorColor);
        canvas.drawLine(screenWidth / 2, viewHeight / 5, screenWidth / 2, viewHeight, paint);
    }

    private void drawScale(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(2);
        //计算游标开始绘制的位置
        float startLocation = (screenWidth / 2) - ((scaleWidth * (currLocation / oneItemValue)));
        for (int i = 0; i <= (maxValue -minValue) / oneItemValue; i++) {
            //判断当前刻度是否小于当前刻度
            if (i * oneItemValue <= currLocation) {
                paint.setColor(scaleSelectColor);
            } else {
                paint.setColor(scaleUnSelectColor);
            }
            float location = startLocation + i * scaleWidth;
            if (i % 10 == 0) {
                canvas.drawLine(location, (float) (viewHeight - scaleHeight/0.8), location, viewHeight, paint);
                paintText = new Paint(Paint.ANTI_ALIAS_FLAG);
                paintText.setTextSize(scaleTextSize);
                if (i * oneItemValue <= currLocation) {
                    paintText.setColor(scaleSelectColor);
                } else {
                    paintText.setColor(scaleTextColor);
                }
                String drawStr = oneItemValue * i + minValue + "";
                Rect bounds = new Rect();
                paintText.getTextBounds(drawStr, 0, drawStr.length(), bounds);
                canvas.drawText(drawStr, location - bounds.width() / 2, (float) (viewHeight - (scaleHeight/0.8 + 15)), paintText);
            } else {
                canvas.drawLine(location, (float) (viewHeight - scaleHeight / 1.5), location, viewHeight, paint);
            }
//            //绘制选中的背景
//            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            paint.setStyle(Paint.Style.FILL);
//            paint.setColor(scaleSelectBackgroundColor);
//            canvas.drawRect(startLocation, viewHeight - scaleHeight / 3, startLocation + currLocation / oneItemValue * scaleWidth, viewHeight, paint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
//        if (ptrClassicFrameLayout != null) {
//            ptrClassicFrameLayout.requestDisallowInterceptTouchEvent(false);
//        }
////
//        int y = (int) event.getY();
//
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:
//                ptrClassicFrameLayout.requestDisallowInterceptTouchEvent(true);
                break;
        }

        return true;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
//        if (ptrClassicFrameLayout != null) {
//            ptrClassicFrameLayout.requestDisallowInterceptTouchEvent(true);
//        }
        return super.dispatchTouchEvent(event);
    }

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            if (!isScrollingPerformed) {
                isScrollingPerformed = true;
            }
            doScroll((int) -distanceX);
            invalidate();
            return true;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            lastScrollX = getCurrentItem() * getItemWidth() + scrollingOffset;
            int maxX = getItemsCount()
                    * getItemWidth();
            int minX = 0;
            scroller.fling(lastScrollX, 0, (int) (-velocityX / 1.5), 0, minX, maxX, 0, 0);
            setNextMessage(MESSAGE_SCROLL);
            return true;
        }
    };

    private void setNextMessage(int message) {
        animationHandler.removeMessages(MESSAGE_SCROLL);
        animationHandler.sendEmptyMessage(message);
    }

    // 动画处理
    private Handler animationHandler = new Handler() {
        public void handleMessage(Message msg) {
            scroller.computeScrollOffset();
            int currX = scroller.getCurrX();
            int delta = lastScrollX - currX;
            lastScrollX = currX;
            if (delta != 0) {
                doScroll(delta);
            }
            // 滚动还没有完成，到最后，完成手动
            if (Math.abs(currX - scroller.getFinalX()) <= MIN_DELTA_FOR_SCROLLING) {
                scroller.forceFinished(true);
            }
            if (!scroller.isFinished()) {
                animationHandler.sendEmptyMessage(msg.what);
            } else {
                finishScrolling();
            }
        }
    };


    private void finishScrolling() {
        if (isScrollingPerformed) {
            isScrollingPerformed = false;
        }
        invalidate();
    }

    private void doScroll(int delta) {
        //偏移量叠加
        scrollingOffset += delta;
        //总共滚动了多少个Item
        int count = scrollingOffset / getItemWidth();
        //当前位置
        int pos = getCurrentItem() - count;
        //限制滚到范围
        if (isScrollingPerformed) {
            if (pos < 0) {
                count = getCurrentItem();
                pos = 0;
            } else if (pos >= getItemsCount()) {
                count = getCurrentItem() - getItemsCount() ;
                pos = getItemsCount() ;
            }
        }
        int offset = scrollingOffset;
        //移动了一个Item的距离，就更新页面
        if (pos != getCurrentItem()) {
            setCurrentItem(pos);
        }

        // 重新更新一下偏移量
        scrollingOffset = offset - count * getItemWidth();

    }

    public int getItemWidth() {
        return scaleWidth;
    }

    public int getCurrentItem() {
        return currLocation / oneItemValue;
    }

    public int getItemsCount() {
        return (maxValue -minValue) / oneItemValue;
    }

    public void setCurrentItem(int index) {
        scrollingOffset = 0;
        currLocation = index * oneItemValue;
        invalidate();
        if (onRulerChangeListener != null) {
            onRulerChangeListener.onChanged(currLocation + minValue);
        }
    }

    public int getCurrLocation() {
        return currLocation;
    }

//    public void setParentLayout(PtrClassicFrameLayout layout){
//        ptrClassicFrameLayout = layout;
//
//    }

    public void setMaxValue(int maxValue) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        this.maxValue = typedArray.getDimensionPixelOffset(R.styleable.RulerView_max_value, maxValue);
        typedArray.recycle();
        invalidate();
    }

    public void setMinValue(int minValue) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        this.minValue = typedArray.getDimensionPixelOffset(R.styleable.RulerView_min_value, minValue);
        typedArray.recycle();
        invalidate();
    }

    public void setOneItemValue(int oneItemValue) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RulerView);
        this.oneItemValue = typedArray.getDimensionPixelOffset(R.styleable.RulerView_one_item_value, oneItemValue);
        typedArray.recycle();
        invalidate();
    }
}
