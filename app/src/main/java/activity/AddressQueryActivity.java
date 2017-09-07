package activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cleverboy.mobilesafe66.R;

import db.dao.AddressDao;

/**
 * Created by clever boy on 2017/7/20.
 */

public class AddressQueryActivity extends Activity {

    private EditText etNumber;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_que);
        etNumber = (EditText) findViewById(R.id.et_number);
        tvResult = (TextView) findViewById(R.id.tv_result);

    }

    public void query(View view) {
        String number = etNumber.getText().toString().trim();
        if (!TextUtils.isEmpty(number)) {
            String address = AddressDao.getAddress(number);
            tvResult.setText(address);
        } else {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.shake);

            etNumber.startAnimation(animation);
        }
    }

    /**
     * 振动
     * 需要权限：
     */
    private void vibrator() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //vibrator.vibrate(2000);
        vibrator.vibrate(new long[]{1000,2000,3000,4000},-1);//参1：振动模式，奇数位表示休息时间，偶数位表示振动时间
                                                              // 参2:-1表示不重复，0表示从第0个位置开始重复

    }

}
