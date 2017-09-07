package service;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cleverboy.mobilesafe66.R;

import activity.BackgroundActivity;
import utils.PreUtils;

/**
 * Created by clever boy on 2017/8/2.
 */

public class RocketAnimService extends Service {

    private WindowManager mWM;
    private View mView;
    private int startX;
    private int startY;
    private int screenHeight;
    private int screenWidth;
    private WindowManager.LayoutParams mParams;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showToast();
    }
    private void showToast(){
        //窗口管理器
        mWM = (WindowManager) getSystemService(WINDOW_SERVICE);

        screenWidth = mWM.getDefaultDisplay().getWidth();
        screenHeight = mWM.getDefaultDisplay().getHeight();

        // 初始化布局参数
        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mParams.format = PixelFormat.TRANSLUCENT;
        mParams.type = WindowManager.LayoutParams.TYPE_PHONE;// 修改窗口类型为TYPE_PHONE,保证可以触摸
        mParams.setTitle("Toast");
        mParams.gravity = Gravity.LEFT + Gravity.TOP;

        //初始化布局
        mView = View.inflate(this, R.layout.rocket,null);
        //开启帧动画
        ImageView ivRocket = (ImageView) mView.findViewById(R.id.iv_rocket);
        AnimationDrawable anim = (AnimationDrawable) ivRocket.getBackground();
        anim.start();

//        int address_style = PreUtils.getInt("address_style", 0, this);
//        int[] bgIds = new int[]{R.drawable.call_locate_white,
//                R.drawable.call_locate_orange,R.drawable.call_locate_blue,
//                R.drawable.call_locate_gray,R.drawable.call_locate_green,};
//        textView.setBackgroundResource(bgIds[address_style]);

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
                        mParams.x = mParams.x + dX;
                        mParams.y = mParams.y + dY;
                        //更新窗口布局
                        mWM.updateViewLayout(mView, mParams);
                        //防止布局超出边界
                        if(mParams.x< 0 ){
                            mParams.x = 0;
                        }
                        if(mParams.x > screenWidth - mView.getWidth()){
                            mParams.x = screenWidth - mView.getWidth();
                        }
                        if(mParams.y < 0 ){
                            mParams.y = 0;
                        }
                        if(mParams.y > screenHeight - mView.getHeight() - 30){
                            mParams.y = screenHeight - mView.getHeight() - 30;
                        }
//                        ivDrag.layout(l, t, r, b);//根据最新边距修改布局位置
                        //重新初始化起始点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        if(mParams.x > screenWidth/2 - 300 && mParams.x<screenWidth/2+300 && mParams.y>screenHeight -400){
                            //启动烟雾页面
                            Intent intent = new Intent(getApplicationContext(), BackgroundActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            sendRocket();

                        }

                        break;
                }

                return true;
            }
        });
        mWM.addView(mView, mParams);

    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            mParams.y = msg.arg1;
            mWM.updateViewLayout(mView,mParams);
        }
    };

    /**
     * 发射火箭
     */
    private void sendRocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int distance = mParams.y;
                for(int i = 0;i<= 10;i++){

                    int y = distance - i * distance/10;
                   Message msg = Message.obtain();
                    msg.arg1 = y;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mWM!=null&&mView!=null){
            mWM.removeView(mView);
        }
    }
}
