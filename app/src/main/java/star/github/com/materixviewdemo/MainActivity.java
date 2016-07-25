package star.github.com.materixviewdemo;

import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = "MainActivity";
    private final float[] mMatrixValues = new float[9];
    private ImageView mMaterixImageView;
    private Matrix mBaseMatrix = new Matrix();//代表当前ImageView的矩阵
    private Matrix mSaveMatrix = new Matrix();//备份当前的ImageView,便于恢复
    private ScaleGestureDetector mScaleGestureDetector;
    private GestureDetector mGestureDetector;
    @NonNull
    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float scale = getScale();
            float scaleFactor = 0;
            if (scale < mMidScale) {
                scaleFactor = mMidScale / scale;
            } else if (scale < mMaxScale) {
                scaleFactor = mMaxScale / scale;
            } else if (scale >= mMaxScale) {
                scaleFactor = mDefaultScale / scale;
            }
            mBaseMatrix.postScale(scaleFactor, scaleFactor, getImageWidth() / 2, getImageHeight() / 2);
            mMaterixImageView.setImageMatrix(mBaseMatrix);
            return true;
        }
    };
    private float mMinScale = 0.5f;
    private float mDefaultScale = 1f;
    private float mMidScale = 1.75f;
    private float mMaxScale = 3f;
    @NonNull
    private ScaleGestureDetector.SimpleOnScaleGestureListener mSimpleOnScaleGestureListener = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
                return false;
            }
            // set min scale scope, max scale scope
            Log.e(TAG, "onScale == sacleFactor == " + scaleFactor + "getScale() == " + getScale());

            if ((getScale() > mMinScale || scaleFactor > 1f) && (getScale() < mMaxScale || scaleFactor < 1f)) {
                mBaseMatrix.postScale(scaleFactor, scaleFactor, getImageWidth() / 2, getImageHeight() / 2);
                mMaterixImageView.setImageMatrix(mBaseMatrix);
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMaterixImageView = (ImageView) findViewById(R.id.materix_image_view);
        mMaterixImageView.setScaleType(ImageView.ScaleType.MATRIX);
        mMaterixImageView.setOnTouchListener(this);

        mScaleGestureDetector = new ScaleGestureDetector(this, mSimpleOnScaleGestureListener);
        mGestureDetector = new GestureDetector(this, mSimpleOnGestureListener);
        Picasso.with(this)
                .load("http://pbs.twimg.com/media/Bist9mvIYAAeAyQ.jpg")
                .fit().centerInside()
                .into(mMaterixImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        update();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        boolean flag = mGestureDetector.onTouchEvent(event);
        flag |= mScaleGestureDetector.onTouchEvent(event);
        return flag;
    }

    public void update() {
        if (!hasDrawable()) {
            return;
        }
        mBaseMatrix.postTranslate((getImageWidth() - getDrawableWidth()) / 2, (getImageHeight() - getDrawableHeight()) / 2);
        mSaveMatrix.set(mBaseMatrix);
        mMaterixImageView.setImageMatrix(mBaseMatrix);
    }

    private boolean hasDrawable() {
        if (mMaterixImageView != null && mMaterixImageView.getDrawable() != null && mMaterixImageView.getDrawable() instanceof BitmapDrawable) {
            return true;
        }
        return false;
    }

    public float getScale() {
        return (float) Math.hypot(getValue(mBaseMatrix, Matrix.MSCALE_X), getValue(mBaseMatrix, Matrix.MSCALE_Y));
    }

    public float getScale(Matrix matrix) {
        return (float) Math.hypot(getValue(matrix, Matrix.MSCALE_X), getValue(matrix, Matrix.MSCALE_Y));
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    private float getImageWidth() {
        return mMaterixImageView.getMeasuredWidth() - mMaterixImageView.getPaddingLeft() - mMaterixImageView.getPaddingRight();
    }

    private float getImageHeight() {
        return mMaterixImageView.getMeasuredHeight() - mMaterixImageView.getPaddingTop() - mMaterixImageView.getPaddingBottom();
    }

    private float getDrawableWidth() {
        return mMaterixImageView.getDrawable().getIntrinsicWidth();
    }

    private float getDrawableHeight() {
        return mMaterixImageView.getDrawable().getIntrinsicHeight();
    }
}
