package activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.example.cleverboy.mobilesafe66.R;

import java.io.File;

import utils.SmsUtils;

/**
 * Created by clever boy on 2017/7/20.
 */

public class AToolsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 电话归属地查询
     *
     * @param view
     */
    public void addressQuery(View view) {
        startActivity(new Intent(this, AddressQueryActivity.class));
    }

    /**
     * 短信备份
     *
     * @param view
     */
    public void smsBackup(View view) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("正在备份短信...");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    File output = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/sms66.xml");
                    SmsUtils.smsBackup(getApplicationContext(), output, dialog);

                    dialog.dismiss();
                }
            }).start();

        } else {
            Toast.makeText(this, "sdcard不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 常用号码查询
     * @param view
     */
    public void commonNumberQuery(View view){
        startActivity(new Intent(this,CommonNumberActivity.class));
    }
}
