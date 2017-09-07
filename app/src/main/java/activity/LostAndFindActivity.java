package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cleverboy.mobilesafe66.R;

import utils.PreUtils;

/**
 * Created by clever boy on 2017/7/9.
 */

public class LostAndFindActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //判断是否第一次进入
        boolean configed = PreUtils.getBoolean("configed", false, this);
        if(!configed){
            //进入设置向导页面
            startActivity(new Intent(this,Setup1Activity.class));
            finish();
        }else{
            setContentView(R.layout.activity_lost_and_find);
            TextView tvPhone = (TextView) findViewById(R.id.tv_safe_phone);
            ImageView ivLock = (ImageView) findViewById(R.id.iv_lock);
            //更新控件
            String safe_phone = PreUtils.getString("phone", "", this);
            tvPhone.setText(safe_phone);
            boolean protect = PreUtils.getBoolean("protect", false, this);
            if(protect){
                ivLock.setImageResource(R.drawable.lock);
            }else{
                ivLock.setImageResource(R.drawable.unlock);
            }

        }
    }
    public void reSetup(View view){
        startActivity(new Intent(this,Setup1Activity.class));
        finish();
    }
}
