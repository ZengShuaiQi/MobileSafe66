package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.cleverboy.mobilesafe66.R;

import utils.PreUtils;
import view.SettingsItemView;


/**
 * 设置向导2
 *
 * @author Kevin
 */
public class Setup2Activity extends BaseActivity {

    private SettingsItemView sivBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        sivBind = (SettingsItemView) findViewById(R.id.siv_bind);
        //判断是否开启sim卡绑定
        String bindSim = PreUtils.getString("bind_sim", null, this);
        if(TextUtils.isEmpty(bindSim)){
        sivBind.setChecked(false);
        }else{
            sivBind.setChecked(true);
        }

        sivBind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sivBind.isChecked()){
                    sivBind.setChecked(false);
                    PreUtils.remove("bind_sim",getApplicationContext());
                }else{
                    sivBind.setChecked(true);
                    //初始化电话管理器
                    TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                    String simserialNumber = tm.getSimSerialNumber();//获取sim卡序列号
                    PreUtils.putString("bind_sim",simserialNumber,getApplicationContext());
                }
            }
        });
    }



    public void showPrevious() {
        startActivity(new Intent(this, Setup1Activity.class));
        finish();
        //activity切换动画
        overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);
    }



    public void showNext() {

        //判断是否绑定sim卡
        String bindSim = PreUtils.getString("bind_sim", null, this);
        if(TextUtils.isEmpty(bindSim)){
            Toast.makeText(this, "必须绑定sim卡", Toast.LENGTH_SHORT).show();
            return;
        }
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        //activity切换动画
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }


}
