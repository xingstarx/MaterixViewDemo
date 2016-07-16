package star.github.com.materixviewdemo;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by xiongxingxing on 16/7/16.
 */

public class MaterixImageView extends ImageView {

    private static final String TAG = "MaterixImageView";
    private Matrix mSaveMatrix;//保存之前的矩阵状态
    private Matrix mCurrentMatrix;//当前的矩阵状态
    private PointF mStartPoint;

    private final static int MODE_NONE = 0;
    private final static int MODE_DRAG = 1;
    private final static int MODE_ZOOM = 2;

    private int mCurrentMode = MODE_NONE;//当前模式

    public MaterixImageView(Context context) {
        this(context, null);
    }

    public MaterixImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaterixImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mSaveMatrix = new Matrix();
        mCurrentMatrix = new Matrix();
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.e(TAG, "MotionEvent.ACTION_DOWN");
                mSaveMatrix.set(mCurrentMatrix);
                mStartPoint = new PointF(event.getX(), event.getY());
                mCurrentMode = MODE_DRAG;
                break;
            case MotionEvent.ACTION_MOVE:
                Log.e(TAG, "MotionEvent.ACTION_MOVE");
                if (mCurrentMode == MODE_DRAG) {
                    mCurrentMatrix.set(mSaveMatrix);//重置
                    float dx = event.getX() - mStartPoint.x;
                    float dy = event.getY() - mStartPoint.y;
                    mCurrentMatrix.postTranslate(dx, dy);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "MotionEvent.ACTION_UP");
                mCurrentMode = MODE_NONE;
                break;
        }
        setImageMatrix(mCurrentMatrix);
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(hasDrawable()) {
            float drawableWidth = getDrawable().getIntrinsicWidth();
            float drawableHeight = getDrawable().getIntrinsicHeight();
            mCurrentMatrix.setTranslate((w - drawableWidth) / 2F,
                    (h - drawableHeight) / 2F);
            setImageMatrix(mCurrentMatrix);
        }
    }

    public boolean hasDrawable() {
        return getDrawable() != null && getDrawable() instanceof BitmapDrawable;
    }
}
