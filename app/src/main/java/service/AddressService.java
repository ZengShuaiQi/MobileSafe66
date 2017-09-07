package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.cleverboy.mobilesafe66.R;

import db.dao.AddressDao;
import utils.PreUtils;

/**
 * Created by clever boy on 2017/7/27.
 */


public class AddressService extends Service {

    private TelephonyManager mTM;
    private MyListener mListener;
    private InnerReceiver mReceiver;
    private WindowManager mWM;
    private View mView;
    private int startX;
    private int startY;
    private int screenHeight;
    private int screenWidth;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        //监听来电
        mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mListener = new MyListener();
        mTM.listen(mListener,PhoneStateListener.LISTEN_CALL_STATE);

        //监听去电
        //动态注册广播
        mReceiver = new InnerReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mReceiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消来电监听
        mTM.listen(mListener,PhoneStateListener.LISTEN_NONE);
        mListener = null;
        //取消去电监听
        unregisterReceiver(mReceiver);
        mReceiver = null;
    }
    class MyListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING:
                    //电话铃响
                    String address = AddressDao.getAddress(incomingNumber);//获取来电号码归属地
                    showToast(address);
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    //电话空闲状态
                    if(mWM!=null&&mView!=null){
                        mWM.removeView(mView);
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    //摘机状态，正在通话
                    break;
            }
        }
    }

    class InnerReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String number = getResultData();
            String address = AddressDao.getAddress(number);
            showToast(address);
        }
    }

    private void showToast(String address){
        //窗口管理器
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);

        screenWidth = mWM.getDefaultDisplay().getWidth();
        screenHeight = mWM.getDefaultDisplay().getHeight();

        // 初始化布局参数
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;// 修改窗口类型为TYPE_PHONE,保证可以触摸
        params.setTitle("Toast");
        params.gravity = Gravity.LEFT + Gravity.TOP;

        //初始化布局
        mView = View.inflate(this, R.layout.custom_toast,null);
        TextView textView = (TextView) mView.findViewById(R.id.tv_address);

        int address_style = PreUtils.getInt("address_style", 0, this);
//        int[] bgIds = new int[]{R.drawable.call_locate_white,
//                R.drawable.call_locate_orange,R.drawable.call_locate_blue,
//                R.drawable.call_locate_gray,R.drawable.call_locate_green,};
//        textView.setBackgroundResource(bgIds[address_style]);
        textView.setText(address);
        int lastX = PreUtils.getInt("lastX", 0, this);
        int lastY = PreUtils.getInt("lastY", 0, this);
        params.x = lastX;
        params.y = lastY;
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //记录起点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_MOVE:
                        //获取移动后的坐标
                        int endX = (int) event.getRawX();
                        int endY = (int) event.getRawY();
                        //计算移动偏量
                        int dX = endX - startX;
                        int dY = endY - startY;
                        //根据偏移量更新位置
                        params.x = params.x + dX;
                        params.y = params.y + dY;
                        //更新窗口布局
                        mWM.updateViewLayout(mView,params);
                        //防止布局超出边界
                        if(params.x< 0 ){
                            params.x = 0;
                        }
                        if(params.x > screenWidth - mView.getWidth()){
                            params.x = screenWidth - mView.getWidth();
                        }
                        if(params.y < 0 ){
                            params.y = 0;
                        }
                        if(params.y > screenHeight - mView.getHeight() - 60){
                            params.y = screenHeight - mView.getHeight() - 60;
                        }
//                        ivDrag.layout(l, t, r, b);//根据最新边距修改布局位置
                        //重新初始化起始点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        PreUtils.putInt("lastX", params.x, getApplicationContext());
                        PreUtils.putInt("lastY", params.y, getApplicationContext());
                        break;
                }

                return true;
            }
        });
        mWM.addView(mView,params);

    }

}
