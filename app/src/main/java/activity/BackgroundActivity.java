package activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.example.cleverboy.mobilesafe66.R;

/**
 * Created by clever boy on 2017/8/3.
 */

public class BackgroundActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        ImageView ivTop = (ImageView) findViewById(R.id.iv_top);
        ImageView ivBottom = (ImageView) findViewById(R.id.iv_bottom);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0,1);
        alphaAnimation.setDuration(2000);

        ivBottom.startAnimation(alphaAnimation);
        ivTop.startAnimation(alphaAnimation);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        },1200);

    }
}
