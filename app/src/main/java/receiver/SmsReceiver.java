package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.example.cleverboy.mobilesafe66.R;

import activity.LockScreenActivity;

/**
 * Created by clever boy on 2017/7/16.
 */

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //读取短信内容
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object obj : objs) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
            String originatingAddress = sms.getOriginatingAddress();
            String messageBody = sms.getMessageBody();

            if("#*alarm*#".equals(messageBody)){
                //播放音乐
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setVolume(1f,1f);
                player.setLooping(true);
                player.start();
                abortBroadcast();//中断短信传递
            }else if("#*location*#".equals(messageBody)){

            }else if("#*lock*#".equals(messageBody)){
                context.startActivity(new Intent(context, LockScreenActivity.class));
            }
        }
    }
}
