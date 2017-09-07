package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cleverboy.mobilesafe66.R;

import utils.MD5Utils;
import utils.PreUtils;

/**
 * Created by clever boy on 2017/6/6.
 */

public class HomeActivity extends Activity {

    private GridView gvHome;
    private String[] mHomeNames = new String[] { "手机防盗", "通讯卫士", "软件管理",
            "进程管理", "流量统计", "手机杀毒", "缓存清理", "高级工具", "设置中心" };
    private int[] mImageIds = new int[] { R.mipmap.home_safe,
            R.mipmap.home_callmsgsafe, R.mipmap.home_apps,
            R.mipmap.home_taskmanager, R.mipmap.home_netmanager,
            R.mipmap.home_trojan, R.mipmap.home_sysoptimize,
            R.mipmap.home_tools, R.mipmap.home_settings };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        gvHome = (GridView) findViewById(R.id.gv_home);
        gvHome.setAdapter(new HomeAdapter());
        gvHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        showSafeDialog();
                        break;
                    case 1:
                        startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(getApplicationContext(),AToolsActivity.class));
                        break;
                    case 8:
                        Intent intent = new Intent(getApplicationContext(),SettingsActivity.class);
                        startActivity(intent);
                        break;
                }
            }
        });
    }
/*
弹出两种对话框
 */
    private void showSafeDialog() {
        String pwd = PreUtils.getString("password",null,this);
        if(TextUtils.isEmpty(pwd)){

            showSetPwdDialog();
        }else{
            showInputPwdDialog();
        }
    }

    private void showInputPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this,R.layout.dialog_input_pwd,null);
        dialog.setView(view);
        dialog.show();

        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        final EditText etPwd = (EditText) view.findViewById(R.id.et_pwd);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = etPwd.getText().toString().trim();

                if(!TextUtils.isEmpty(pwd)){
                    String password = PreUtils.getString("password", null, getApplicationContext());
                    if(MD5Utils.encode(pwd).equals(password)){
                        dialog.dismiss();
                        //跳到手机防盗
                        startActivity(new Intent(getApplicationContext(),LostAndFindActivity.class));
                    }else{
                        Toast.makeText(HomeActivity.this, "输入错误", Toast.LENGTH_SHORT).show();

                    }
                }else{
                    Toast.makeText(HomeActivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showSetPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this,R.layout.dialog_set_pwd,null);
        dialog.setView(view);
        dialog.show();

        Button btn_ok = (Button) view.findViewById(R.id.btn_ok);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        final EditText etPwd = (EditText) view.findViewById(R.id.et_pwd);
        final EditText etPwdConfirm = (EditText) view.findViewById(R.id.et_pwd_confirm);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pwd = etPwd.getText().toString().trim();
                String pwdConfirm = etPwdConfirm.getText().toString().trim();
                if(!TextUtils.isEmpty(pwd)&& !TextUtils.isEmpty(pwdConfirm)){
                    if(pwd.equals(pwdConfirm)){
                        //保存密码
                        PreUtils.putString("password", MD5Utils.encode(pwd),getApplicationContext());
                        //跳到手机防盗
                        startActivity(new Intent(getApplicationContext(),LostAndFindActivity.class));
                        dialog.dismiss();
                    }else{
                        Toast.makeText(HomeActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(HomeActivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    class HomeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mHomeNames.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getApplicationContext(),
                    R.layout.list_item_home, null);

            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            ImageView ivIcon = (ImageView) view.findViewById(R.id.iv_icon);

            tvName.setText(mHomeNames[position]);
            ivIcon.setImageResource(mImageIds[position]);

            return view;
        }

    }
}
