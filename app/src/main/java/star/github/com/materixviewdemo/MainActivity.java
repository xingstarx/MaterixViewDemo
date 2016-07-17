package star.github.com.materixviewdemo;

import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private static final String TAG = "MainActivity";
    private ImageView mMaterixImageView;
    private Matrix mBaseMatrix = new Matrix();
    private final float[] mMatrixValues = new float[9];

    private ScaleGestureDetector mScaleGestureDetector;
    private float mMinScale = 0.5f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMaterixImageView = (ImageView) findViewById(R.id.materix_image_view);
        mMaterixImageView.setScaleType(ImageView.ScaleType.MATRIX);
        mMaterixImageView.setOnTouchListener(this);

        mScaleGestureDetector = new ScaleGestureDetector(this, this);
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
        boolean flag = mScaleGestureDetector.onTouchEvent(event);
        Log.e(TAG, "flag == " + flag);
        return flag;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleFactor = detector.getScaleFactor();
        Log.e(TAG, "onScale == sacleFactor == " + scaleFactor);
        if (Float.isNaN(scaleFactor) || Float.isInfinite(scaleFactor)) {
            return false;
        }
        if (getScale() > mMinScale || scaleFactor > 1f) {
            mBaseMatrix.postScale(scaleFactor, scaleFactor, getImageWidth() / 2, getImageHeight() / 2);
            mMaterixImageView.setImageMatrix(mBaseMatrix);
        }
        return true;
    }

    public void update() {
        if (!hasDrawable()) {
            return;
        }
        mBaseMatrix.postTranslate((getImageWidth() - getDrawableWidth()) / 2, (getImageHeight() - getDrawableHeight()) / 2);
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

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }
}
