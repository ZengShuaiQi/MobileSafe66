package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import utils.PreUtils;

/**
 * Created by clever boy on 2017/7/13.
 */

public class BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean protect = PreUtils.getBoolean("protect", false, context);
        if(!protect){
            return;
        }
        String savaSim = PreUtils.getString("bind_sim", null, context);
        if(TextUtils.isEmpty(savaSim)){
            //获取当前sim卡，和保存的sim卡进行比对
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String currentSim = tm.getSimSerialNumber();
            if(savaSim.equals(currentSim)){
                System.out.print("手机安全，放心使用");
            }else{
                //发送报警短信
                String safe_phone = PreUtils.getString("phone", "", context);
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(safe_phone,null,"sim card changed",null,null);
            }
        }
    }
}
