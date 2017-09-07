package view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.cleverboy.mobilesafe66.R;

/**
 * Created by clever boy on 2017/6/24.
 */

public class SettingsItemClickView extends RelativeLayout {


    private TextView tvTitle;
    private TextView tvDesc;

    public SettingsItemClickView(Context context) {
        super(context);
        initView();
    }

    public SettingsItemClickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public SettingsItemClickView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    public void initView(){
        View child = View.inflate(getContext(), R.layout.settings_item_click_view, null);
        tvTitle = (TextView) child.findViewById(R.id.tv_title);
        tvDesc = (TextView) child.findViewById(R.id.tv_desc);
        this.addView(child);
    };

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title){
        tvTitle.setText(title);
    }

    /**
     * 设置描述
     * @param desc
     */
    public void setDesc(String desc){
        tvDesc.setText(desc);
    }

}
