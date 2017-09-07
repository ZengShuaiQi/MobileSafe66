package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.cleverboy.mobilesafe66.R;

import org.w3c.dom.Text;

import utils.PreUtils;


/**
 * 设置向导3
 *
 * @author Kevin
 */
public class Setup3Activity extends BaseActivity {
    private EditText tvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        tvPhone = (EditText) findViewById(R.id.tv_phone);
        String phone = PreUtils.getString("phone", "", this);
        tvPhone.setText(phone);
    }

    @Override
    public void showPrevious() {
        startActivity(new Intent(this, Setup2Activity.class));
        finish();
        //activity切换动画
        overridePendingTransition(R.anim.anim_pre_in, R.anim.anim_pre_out);
    }

    @Override
    public void showNext() {
        String phone = tvPhone.getText().toString().trim();
        if(!TextUtils.isEmpty(phone)){
            PreUtils.putString("phone",phone,this);
            startActivity(new Intent(this, Setup4Activity.class));
            finish();
            //activity切换动画
            overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        }


    }

    public void selectContact(View view) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * 接受ContactActivity传过来的数据
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            if(data!= null) {
                String phone = data.getStringExtra("phone");
                phone = phone.replaceAll("-", "").replaceAll(" ", "");
                tvPhone.setText(phone);
            }
        }
    }
}
