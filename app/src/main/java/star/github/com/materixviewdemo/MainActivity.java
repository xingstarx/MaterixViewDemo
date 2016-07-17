package star.github.com.materixviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private MaterixImageView mMaterixImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMaterixImageView = (MaterixImageView) findViewById(R.id.materix_image_view);

        Picasso.with(this)
                .load("http://pbs.twimg.com/media/Bist9mvIYAAeAyQ.jpg")
                .fit().centerInside()
                .into(mMaterixImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mMaterixImageView.update();
                    }

                    @Override
                    public void onError() {
                    }
                });
    }
}
