package activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.cleverboy.mobilesafe66.R;

import receiver.AdminReceiver;
import utils.PreUtils;


/**
 * 设置向导4
 *
 * @author Kevin
 */
public class Setup4Activity extends BaseActivity {

    private CheckBox mCb;
    private DevicePolicyManager mDPM;
    private ComponentName componentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        //设备管理器初始化
        mDPM = (DevicePolicyManager) getSystemService(this.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);
        //复选框初始化
        mCb = (CheckBox) findViewById(R.id.cb_protect);
        boolean isChecked = PreUtils.getBoolean("protect", false, this);
        if(isChecked){
            mCb.setChecked(true);
            mCb.setText("防盗保护已经开启");
        }else{
            mCb.setChecked(false);
            mCb.setText("你没有开启防盗保护");
        }
        //设置勾选监听
        mCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mCb.setText("防盗保护已经开启");
                    PreUtils.putBoolean("protect",true,getApplicationContext());
                }else{
                    mCb.setText("你没有开启防盗保护");
                    PreUtils.putBoolean("protect",false,getApplicationContext());
                }
            }
        });
    }

    @Override
    public void showPrevious() {
        startActivity(new Intent(this, Setup3Activity.class));
        finish();
        //activity切换动画
        overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);
    }

    @Override
    public void showNext() {
        if(mDPM.isAdminActive(componentName)) {
            PreUtils.putBoolean("configed", true, this);// 表示已经展示向导页了,下次不再展示
            startActivity(new Intent(this, LostAndFindActivity.class));
            finish();
            //activity切换动画
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        }else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "超级管理员，牛逼啊!!!");
            startActivity(intent);
        }
    }

}
