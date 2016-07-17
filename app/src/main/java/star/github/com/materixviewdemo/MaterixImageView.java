package star.github.com.materixviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IntDef;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by xiongxingxing on 16/7/16.
 */

public class MaterixImageView extends ImageView {

    private static final String TAG = "MaterixImageView";
    private final static int MODE_NONE = 0;
    private final static int MODE_DRAG = 1;
    private final static int MODE_ZOOM = 2;
    private Matrix mSaveMatrix;//保存之前的矩阵状态
    private Matrix mCurrentMatrix;//当前的矩阵状态
    private PointF mStartPoint;
    private PointF mCenterPoint;
    private double mSaveRotate;
    private double mCurrentRotate;
    private int mCurrentMode = MODE_NONE;//当前模式
    private float mSaveInterval;//保存scale之前的两指间的间隔
    private float mCurrentInterval;//当前的间隔
    private boolean mSupportRotate;
    private boolean supportScale;
    private int mMaterixScaleType;


    public MaterixImageView(Context context) {
        this(context, null);
    }

    public MaterixImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterixImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MaterixImageView,
                defStyleAttr, 0);
        mSupportRotate = a.getBoolean(R.styleable.MaterixImageView_supportRotate, false);
        supportScale = a.getBoolean(R.styleable.MaterixImageView_supportScale, true);
        mMaterixScaleType = a.getInt(R.styleable.MaterixImageView_supportScaleType, 1);
    }

    private void init() {
        mSaveMatrix = new Matrix();
        mCurrentMatrix = new Matrix();
        mStartPoint = new PointF();
        mCenterPoint = new PointF();
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!hasDrawable()) {
            return super.onTouchEvent(event);
        }
        switch (MotionEventCompat.getActionMasked(event)) {
            case MotionEvent.ACTION_DOWN:
//                Log.e(TAG, "MotionEvent.ACTION_DOWN");
                mSaveMatrix.set(mCurrentMatrix);
                mStartPoint.set(event.getX(), event.getY());
                mCurrentMode = MODE_DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.e(TAG, "MotionEvent.ACTION_MOVE");
                if (mCurrentMode == MODE_DRAG) {
                    mCurrentMatrix.set(mSaveMatrix);//重置
                    float dx = event.getX() - mStartPoint.x;
                    float dy = event.getY() - mStartPoint.y;
                    mCurrentMatrix.postTranslate(dx, dy);
                } else if (mCurrentMode == MODE_ZOOM && event.getPointerCount() == 2) {
                    mCurrentMatrix.set(mSaveMatrix);
                    mCurrentInterval = caculateInterval(event);
                    if (mCurrentInterval > 10f) {
                        float scale = mCurrentInterval / mSaveInterval;
                        mCurrentMatrix.postScale(scale, scale, mCenterPoint.x, mCenterPoint.y);
                    }
                    if (mSupportRotate) {
                        mCurrentRotate = caculateRotate(event);
                        float degrees = (float) (mCurrentRotate - mSaveRotate);
                        mCurrentMatrix.postRotate(degrees, getMeasuredWidth() / 2, getMeasuredHeight() / 2);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
//                Log.e(TAG, "MotionEvent.ACTION_UP");
                mCurrentMode = MODE_NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //caculate on two pointer interval
                mSaveInterval = caculateInterval(event);
                Log.e(TAG, "MotionEvent.ACTION_POINTER_DOWN  mSaveInterval == " + mSaveInterval);
                if (mSaveInterval > dp2px(12)) {//scale  rote
                    mSaveMatrix.set(mCurrentMatrix);
                    getCenterPoint(mCenterPoint, event);
                    mCurrentMode = MODE_ZOOM;
                }
                if (mSupportRotate) {
                    mSaveRotate = caculateRotate(event);
                }
                break;
        }
        setImageMatrix(mCurrentMatrix);
        return true;
    }

    private float caculateInterval(MotionEvent event) {
        return (float) Math.hypot(event.getX(0) - event.getX(1), event.getY(0) - event.getY(1));
    }

    private float dp2px(float dp) {
        return getResources().getDisplayMetrics().density * dp + 0.5f;
    }

    private void getCenterPoint(PointF centerPoint, MotionEvent event) {
        centerPoint.set((event.getX(0) + event.getX(1)) / 2, (event.getY(0) + event.getY(1)) / 2);
    }

    private double caculateRotate(MotionEvent event) {
        double angrd = Math.atan2(event.getY(0) - event.getY(1), event.getX(0) - event.getX(1));
        return Math.toDegrees(angrd);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (hasDrawable()) {
            float drawableWidth = getDrawableWidth();
            float drawableHeight = getDrawableHeight();
            mCurrentMatrix.reset();
            mCurrentMatrix.setTranslate((w - drawableWidth) / 2F,
                    (h - drawableHeight) / 2F);
            setImageMatrix(mCurrentMatrix);
        }
    }

    public void update() {
        if (hasDrawable()) {
            float drawableWidth = getDrawableWidth();
            float drawableHeight = getDrawableHeight();
            mCurrentMatrix.reset();
            mCurrentMatrix.setTranslate((getMeasuredWidth() - drawableWidth) / 2F,
                    (getMeasuredHeight() - drawableHeight) / 2F);
            setImageMatrix(mCurrentMatrix);
        }
    }

    private float getDrawableWidth() {
        return getDrawable().getIntrinsicWidth();
    }

    private float getDrawableHeight() {
        return getDrawable().getIntrinsicHeight();
    }

    public boolean hasDrawable() {
        return getDrawable() != null && getDrawable() instanceof BitmapDrawable;
    }


    public static final int SCALE_TYPE_NONE = 0;
    public static final int SCALE_TYPE_MIN = 1;
    public static final int SCALE_TYPE_MAX = 2;
    @IntDef({SCALE_TYPE_NONE, SCALE_TYPE_MIN, SCALE_TYPE_MAX})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MaterixScaleType {}

}
