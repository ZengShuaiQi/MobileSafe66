package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.cleverboy.mobilesafe66.R;

/**
 * Created by clever boy on 2017/7/9.
 */

public class Setup1Activity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showPrevious() {

    }

    @Override
    public void showNext() {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();
        //activity切换动画
        overridePendingTransition(R.anim.anim_in,R.anim.anim_out);
    }
}
