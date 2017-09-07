package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.cleverboy.mobilesafe66.R;

/**
 * Created by clever boy on 2017/7/13.
 */

public abstract class BaseActivity extends Activity {
    private GestureDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //e1.getX();相对父控件的坐标
        //e1.getRawX()相对屏幕的坐标
        detector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                //e1.getX();相对父控件的坐标
                //e1.getRawX()相对屏幕的坐标
                if (Math.abs(e2.getRawY() - e1.getRawY()) > 100) {
                    Toast.makeText(getApplicationContext(), "不能这样划哦！", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if(Math.abs(velocityX) <100){
                    Toast.makeText(getApplicationContext(), "滑动太慢了哦！", Toast.LENGTH_SHORT).show();
                    return true;
                }

                if (e2.getRawX() - e1.getRawX() > 100) {//向右划,上一页
                    showPrevious();
                    return true;
                }
                if (e1.getRawX() - e2.getRawX() > 100) {//向左划，下一页
                    showNext();
                    return true;
                }

                return false;
            }
        });
    }
    public void previous(View view) {
        showPrevious();
    }

    public abstract void showPrevious() ;



    /**
     * 下一页
     *
     * @param view
     */
    public void next(View view) {
        showNext();
    }

    public abstract void showNext();
    /**
     * 当前界面被触摸时，走此方法
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);//将当前事件委托给手势识别器
        return super.onTouchEvent(event);
    }
}
