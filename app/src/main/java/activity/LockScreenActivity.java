package activity;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.widget.Toast;

import receiver.AdminReceiver;

/**
 * Created by clever boy on 2017/7/19.
 */

public class LockScreenActivity extends Activity {

    private DevicePolicyManager mDPM;
    private ComponentName componentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDPM = (DevicePolicyManager) getSystemService(this.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, AdminReceiver.class);
        if(mDPM.isAdminActive(componentName)){
            mDPM.lockNow();
            finish();
        }else{
            Toast.makeText(this, "没有激活超级管理员", Toast.LENGTH_SHORT).show();
        }
    }
}
