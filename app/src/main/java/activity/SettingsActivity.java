package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.example.cleverboy.mobilesafe66.R;

import service.AddressService;
import service.BlackNumberService;
import service.RocketAnimService;
import utils.PreUtils;
import utils.ServiceStatusUtils;
import view.SettingsItemClickView;
import view.SettingsItemView;

/**
 * Created by clever boy on 2017/6/24.
 */

public class SettingsActivity extends Activity {

    private SettingsItemView sivUpdate;
    private SettingsItemView sivAddress;
    private SettingsItemClickView sicStyle;
    private SettingsItemClickView sicLocation;
    private SettingsItemView sivRocket;
    private SettingsItemView sivBlackNum;
    //    private String[] mItems = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
//        sp = getSharedPreferences("config", MODE_PRIVATE);
        initUpdate();
        initAddress();
//        initAddressStyle();
        initAddressLocation();
        initRocketAnim();
        initBlackNumber();
    }

    /**
     * 黑名单设置
     */
    private void initBlackNumber() {

        sivBlackNum = (SettingsItemView) findViewById(R.id.siv_black_number);

        final Intent service = new Intent(getApplicationContext(), BlackNumberService.class);

        boolean isBlackNumberServiceRunning = PreUtils.getBoolean("isBlackNumberServiceRunning", true, this);
        sivBlackNum.setChecked(isBlackNumberServiceRunning);
        if(isBlackNumberServiceRunning){
            startService(service);
        }else{
            stopService(service);
        }
//
//        判断服务是否正在运行
//        boolean serviceRunning = ServiceStatusUtils.isServiceRunning("com.example.cleverboy.mobilesafe66.service.AddressService"
//                , this);

        sivBlackNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sivBlackNum.isChecked()){
                    sivBlackNum.setChecked(false);
                    //关闭AddressService
                    stopService(service);
                    PreUtils.putBoolean("isBlackNumberServiceRunning",false,getApplicationContext());
                }else{
                    sivBlackNum.setChecked(true);
                    //开启AddressService
                    startService(service);
                    PreUtils.putBoolean("isBlackNumberServiceRunning",true,getApplicationContext());
                }
            }
        });
    }

    private void initRocketAnim() {
        sivRocket = (SettingsItemView) findViewById(R.id.siv_rocket);
        final Intent service = new Intent(getApplicationContext(), RocketAnimService.class);

        boolean isRocketAnimServiceRunning = PreUtils.getBoolean("isRocketAnimServiceRunning", false, this);
        sivRocket.setChecked(isRocketAnimServiceRunning);
        if(isRocketAnimServiceRunning){
            startService(service);
        }else{
            stopService(service);
        }

        sivRocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sivRocket.isChecked()){
                    sivRocket.setChecked(false);
                    //关闭AddressService
                    stopService(service);
                    PreUtils.putBoolean("isRocketAnimServiceRunning",false,getApplicationContext());
                }else{
                    sivRocket.setChecked(true);
                    //开启AddressService
                    startService(service);
                    PreUtils.putBoolean("isRocketAnimServiceRunning",true,getApplicationContext());
                }
            }
        });
    }

    private void initAddressLocation() {
        sicLocation = (SettingsItemClickView) findViewById(R.id.sic_location);
        sicLocation.setTitle("归属地提示框位置");
        sicLocation.setDesc("设置归属地提示框的显示位置");
        sicLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),DragViewActivity.class));
            }
        });

    }

//    private void initAddressStyle() {
//        sicStyle = (SettingsItemClickView) findViewById(R.id.sic_style);
//        sicStyle.setTitle("归属地提示框风格");
//        int address_style = PreUtils.getInt("address_style", 0, this);
//        sicStyle.setDesc(mItems[address_style]);
//
//        sicStyle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showChooseDialog();
//            }
//        });
//    }

    /**
     * 归属地风格选择弹窗
     */
//    private void showChooseDialog() {
//        int address_style = PreUtils.getInt("address_style", 0, this);
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("归属地提示框风格");
//        builder.setIcon(R.mipmap.ic_launcher);
//
//        builder.setSingleChoiceItems(mItems, address_style, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                PreUtils.putInt("address_style",which,getApplicationContext());
//                dialog.dismiss();
//                sicStyle.setDesc(mItems[which]);
//            }
//        });
//        builder.setNegativeButton("取消",null);
//        builder.show();
//    }

    private void initAddress() {
        sivAddress = (SettingsItemView) findViewById(R.id.siv_address);
        final Intent service = new Intent(getApplicationContext(), AddressService.class);

        boolean isAddressServiceRunning = PreUtils.getBoolean("isAddressServiceRunning", true, this);
        sivAddress.setChecked(isAddressServiceRunning);
        if(isAddressServiceRunning){
            startService(service);
        }else{
            stopService(service);
        }

        //判断服务是否正在运行
        boolean serviceRunning = ServiceStatusUtils.isServiceRunning("com.example.cleverboy.mobilesafe66.service.AddressService"
                , this);

        sivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if(sivAddress.isChecked()){
                    sivAddress.setChecked(false);
                    //关闭AddressService
                    stopService(service);
                    PreUtils.putBoolean("isAddressServiceRunning",false,getApplicationContext());
                }else{
                    sivAddress.setChecked(true);
                    //开启AddressService
                    startService(service);
                    PreUtils.putBoolean("isAddressServiceRunning",true,getApplicationContext());
                }
            }
        });
    }



    /**
     * 初始化自动更新
     */
    private void initUpdate() {
        sivUpdate = (SettingsItemView) findViewById(R.id.siv_update);
        boolean auto_update = PreUtils.getBoolean("auto_update",true,this);
        sivUpdate.setTitle("自动更新设置");
//        if (auto_update) {
//            sivUpdate.setDesc("自动更新已开启");
//            sivUpdate.setChecked(true);
//
//        } else {
//            sivUpdate.setDesc("自动更新已关闭");
//            sivUpdate.setChecked(false);
//        }
        sivUpdate.setChecked(auto_update);

        //设置点击事件
        sivUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sivUpdate.isChecked()) {
                    sivUpdate.setChecked(false);
//                    sivUpdate.setDesc("自动更新已关闭");
                    PreUtils.putBoolean("auto_update",false,getApplicationContext());
                } else {
                    sivUpdate.setChecked(true);
//                    sivUpdate.setDesc("自动更新已开启");
                    PreUtils.putBoolean("auto_update",true,getApplicationContext());
                }
            }
        });
    }
}
