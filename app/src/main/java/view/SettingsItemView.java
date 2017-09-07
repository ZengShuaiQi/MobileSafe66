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

public class SettingsItemView extends RelativeLayout {

    private final String NAMESPACE = "http://schemas.android.com/apk/res-auto";
    private TextView tvTitle;
    private TextView tvDesc;
    private CheckBox cbCheck;
    private String desc_on;
    private String desc_off;

    public SettingsItemView(Context context) {
        super(context);
        initView();
    }

    public SettingsItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        String title = attrs.getAttributeValue(NAMESPACE, "title");
        desc_on = attrs.getAttributeValue(NAMESPACE, "desc_on");
        desc_off = attrs.getAttributeValue(NAMESPACE, "desc_off");
        setTitle(title);
    }

    public SettingsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化布局
     */
    public void initView(){
        View child = View.inflate(getContext(), R.layout.settings_item_view, null);
        tvTitle = (TextView) child.findViewById(R.id.tv_title);
        tvDesc = (TextView) child.findViewById(R.id.tv_desc);
        cbCheck = (CheckBox) child.findViewById(R.id.checkBox);
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

    /**
     * 判断是否选中
     * @return
     */
    public boolean isChecked(){
        return cbCheck.isChecked();
    }

    /**
     * 设置选中状态
     * @param checked
     */
    public void setChecked(boolean checked){
        cbCheck.setChecked(checked);
        if(checked){
            setDesc(desc_on);
        }else{
            setDesc(desc_off);
        }
    }
}
