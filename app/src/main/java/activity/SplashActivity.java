package activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cleverboy.mobilesafe66.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import utils.PreUtils;
import utils.StreamUtils;

public class SplashActivity extends AppCompatActivity {

    private static final int CODE_UPDATE_DIALOG = 1;
    private static final int CODE_ENTER_HOME = 2;
    private static final int CODE_NETWORK_ERROR = 3;
    private static final int CODE_JOSO_ERROR = 4;
    private TextView textView;
    private String mVersionName;
    private int mVersionCode;
    private String mDes;
    private String mUrl;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CODE_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                case CODE_ENTER_HOME:
                    enterHome();
                    break;
                case CODE_NETWORK_ERROR:
                    Toast.makeText(SplashActivity.this, "网络异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
                case CODE_JOSO_ERROR:
                    Toast.makeText(SplashActivity.this, "数据解析异常", Toast.LENGTH_SHORT).show();
                    enterHome();
                    break;
            }
        }
    };
    private RelativeLayout rlRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        textView = (TextView) findViewById(R.id.tv_name);
        textView.setText("版本名：" + getVersionName());
//        SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
        boolean auto_update = PreUtils.getBoolean("auto_update", true, this);
        if (auto_update) {
            checkVersion();
        } else {
            mHandler.sendEmptyMessageDelayed(CODE_ENTER_HOME, 2000);
        }

        //渐变动画
        rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1);
        alphaAnimation.setDuration(2000);
        rlRoot.startAnimation(alphaAnimation);

        copyDb("address.db");
        copyDb("commonnum.db");
    }

    /**
     * 检查版本
     */
    private void checkVersion() {
        new Thread() {
            @Override
            public void run() {
                Message message = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL("http://10.0.2.2:8080/update66.json").openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    conn.setReadTimeout(2000);
                    conn.connect();
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream in = conn.getInputStream();
                        String result = StreamUtils.stream2String(in);
                        JSONObject jsonObject = new JSONObject(result);
                        mVersionName = jsonObject.getString("versionName");
                        mVersionCode = jsonObject.getInt("versionCode");
                        mDes = jsonObject.getString("des");
                        mUrl = jsonObject.getString("url");
                        if (getVersionCode() < mVersionCode) {
                            //showUpdateDialog();
                            message.what = CODE_UPDATE_DIALOG;
                        } else {
                            message.what = CODE_ENTER_HOME;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = CODE_NETWORK_ERROR;
                } catch (JSONException e) {
                    e.printStackTrace();
                    message.what = CODE_JOSO_ERROR;
                } finally {
                    long endTime = System.currentTimeMillis();
                    long timeUsed = endTime - startTime;

                    try {
                        if (timeUsed < 2000) {
                            Thread.sleep(2000 - timeUsed);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendMessage(message);
                }
            }
        }.start();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("发现新版本：" + mVersionCode);
        builder.setMessage(mDes);

        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadApk();
            }
        });
        builder.setNegativeButton("以后再说", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterHome();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                enterHome();
            }
        });
        builder.show();
    }

    private void downloadApk() {

    }

    /**
     * 获取版本名称
     */
    private String getVersionName() {
        PackageManager packageManager = this.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取版本号
     */
    private int getVersionCode() {
        PackageManager packageManager = this.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 跳到主页面
     */
    private void enterHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    /**
     * 拷贝数据库
     */
    private void copyDb(String dbName) {

        File filesDir = getFilesDir();
        File file = new File(filesDir, dbName);
        //判断文件是否存在，如果存在，无需拷贝
        if(file.exists()){
            return;
        }
        InputStream in = null;
        FileOutputStream out = null;
        try {
            //获取assets文件夹数据
            AssetManager assets = getAssets();
            in = assets.open(dbName);
            //data/data/包名

            out = new FileOutputStream(file);
            //写入
            int len = 0;
            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
