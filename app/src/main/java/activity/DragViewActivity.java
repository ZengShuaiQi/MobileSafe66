package activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cleverboy.mobilesafe66.R;

import utils.PreUtils;

/**
 * Created by clever boy on 2017/7/30.
 */

public class DragViewActivity extends Activity {

    private ImageView ivDrag;
    private int startX;
    private int startY;
    private int screenHeight;
    private int screenWidth;
    private TextView tvBottom;
    private TextView tvTop;

    long[] mHits = new long[2];// 数组长度就是多击次数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drag_view_activity);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        screenHeight = wm.getDefaultDisplay().getHeight();

        ivDrag = (ImageView) findViewById(R.id.iv_drag);
        tvTop = (TextView) findViewById(R.id.tv_top);
        tvBottom = (TextView) findViewById(R.id.tv_bottom);

        int lastX = PreUtils.getInt("lastX", 0, this);
        int lastY = PreUtils.getInt("lastY", 0, this);

        if (lastY > screenHeight / 2) {
            tvBottom.setVisibility(View.INVISIBLE);
            tvTop.setVisibility(View.VISIBLE);
        } else {
            tvBottom.setVisibility(View.VISIBLE);
            tvTop.setVisibility(View.INVISIBLE);
        }

//      ivDrag.layout(lastX,lastY,lastX+ivDrag.getWidth(),
//      lastY + ivDrag.getHeight());此方法不能在onCreate中执行,因为布局还没有开始绘制

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) ivDrag.getLayoutParams();
        layoutParams.topMargin = lastY;
        layoutParams.leftMargin = lastX;

        //触摸事件监听
        ivDrag.setOnTouchListener(new View.OnTouchListener() {
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
                        int l = ivDrag.getLeft() + dX;
                        int r = ivDrag.getRight() + dX;
                        int t = ivDrag.getTop() + dY;
                        int b = ivDrag.getBottom() + dY;

                        if (l < 0 || r > screenWidth) {
                            return true;
                        }
                        if (t < 0 || b > screenHeight - 60) {
                            return true;
                        }
                        if (t > screenHeight / 2) {
                            tvBottom.setVisibility(View.INVISIBLE);
                            tvTop.setVisibility(View.VISIBLE);
                        } else {
                            tvBottom.setVisibility(View.VISIBLE);
                            tvTop.setVisibility(View.INVISIBLE);
                        }

                        ivDrag.layout(l, t, r, b);//根据最新边距修改布局位置
                        //重新初始化起始点坐标
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();

                        break;
                    case MotionEvent.ACTION_UP:
                        PreUtils.putInt("lastX", ivDrag.getLeft(), getApplicationContext());
                        PreUtils.putInt("lastY", ivDrag.getTop(), getApplicationContext());
                        break;
                }

                return false;
            }
        });

        ivDrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 数组拷贝:参1:原数组;参2:原数组拷贝起始位置;参3:目标数组;参4:目标数组起始拷贝位置;参5:拷贝数组长度
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();// 手机开机时间
                if (SystemClock.uptimeMillis() - mHits[0] <= 500) {
                    // 布局居中显示
                    ivDrag.layout(screenWidth / 2 - ivDrag.getWidth() / 2,
                            ivDrag.getTop(),
                            screenWidth / 2 + ivDrag.getWidth() / 2,
                            ivDrag.getBottom());
                }
            }
        });
    }
}
