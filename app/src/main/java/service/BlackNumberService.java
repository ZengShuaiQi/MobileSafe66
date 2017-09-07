package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;


import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import db.dao.BlackNumberDao;

/**
 * Created by clever boy on 2017/8/10.
 */

public class BlackNumberService extends Service {

    private InnerSmsReceiver mReceiver;
    private BlackNumberDao mDao;
    private TelephonyManager mTM;
    private MyListener mListener;
    private MyObserver mObserver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mDao = BlackNumberDao.getInstance(this);
        //拦截短信
        mReceiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mReceiver, filter);

        // 拦截电话
        mTM = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);

        mListener = new MyListener();
        mTM.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销短信监听
        unregisterReceiver(mReceiver);
        mReceiver = null;

        // 取消来电监听
        mTM.listen(mListener, PhoneStateListener.LISTEN_NONE);
        mListener = null;
    }

    class InnerSmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String originatingAddress = sms.getOriginatingAddress();
                String messageBody = sms.getMessageBody();
                //判断当前号码是否属于黑名单
                if (mDao.find(originatingAddress)) {
                    int mode = mDao.findMode(originatingAddress);
                    if (mode > 1) {
                        abortBroadcast();
                    }
                }
            }

        }
    }

    class MyListener extends PhoneStateListener {

        // 电话状态发生变化后
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    // 电话铃响, 1, 3
                    int mode = mDao.findMode(incomingNumber);
                    if (mode == 1 || mode == 3) {
                        endCall();
                        mObserver = new MyObserver(new Handler(), incomingNumber);
                        getContentResolver().registerContentObserver(
                                Uri.parse("content://call_log/calls"),
                                true, mObserver);
                    }
                    break;
                default:
                    break;
            }

        }
    }

    /**
     * 挂断电话
     */
    private void endCall() {
        try {
            Class<?> clazz = Class.forName("android.os.ServiceManager");
            Method getService = clazz.getMethod("getService", String.class);
            IBinder b = (IBinder) getService.invoke(null, TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(b);
            iTelephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除通话记录 权限: <uses-permission
     * android:name="android.permission.WRITE_CONTACTS" />(2.3需要此权限)
     * <uses-permission android:name="android.permission.WRITE_CALL_LOG" />
     */
    public void deleteCalllog(String number) {
        getContentResolver().delete(Uri.parse("content://call_log/calls"),
                "number=?", new String[]{number});
    }

    // 内容观察者
    class MyObserver extends ContentObserver {

        private String incomingNumber;

        public MyObserver(Handler handler, String incomingNumber) {
            super(handler);
            this.incomingNumber = incomingNumber;
        }

        // 数据发生变化,就走此构造方法
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            System.out.println("通话记录发生变化!!!");
            deleteCalllog(incomingNumber);

            // 注销内容观察者
            getContentResolver().unregisterContentObserver(mObserver);
        }

    }

}
