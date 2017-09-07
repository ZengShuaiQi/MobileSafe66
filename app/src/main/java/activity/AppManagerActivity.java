package activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cleverboy.mobilesafe66.R;

import java.util.ArrayList;

import domain.AppInfo;
import engine.AppInfoProvider;

/**
 * Created by clever boy on 2017/8/29.
 */

public class AppManagerActivity extends Activity implements View.OnClickListener {

    private ListView listView;
    private ArrayList<AppInfo> mList;
    private ArrayList<AppInfo> mUserList;
    private ArrayList<AppInfo> mSystemList;
    private LinearLayout ll;
    private TextView tvHeader;
    private PopupWindow mPopupWindow;
    private View contentView;
    private AnimationSet set;
    private AppInfoAdapter appInfoAdapter;
    private AppInfo mCurrentAppinfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);
        //绑定控件
        TextView sdcardAvail = (TextView) findViewById(R.id.tv_sdcard_avail);
        TextView romAvail = (TextView) findViewById(R.id.tv_rom_avail);
        listView = (ListView) findViewById(R.id.lv_list);
        ll = (LinearLayout) findViewById(R.id.ll_loading);
        tvHeader = (TextView) findViewById(R.id.tv_header);
        //获取内存和外部存储空间
        String sdcardSpace = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        String romSpace = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());
        sdcardAvail.setText("sdcard可用：" + sdcardSpace);
        romAvail.setText("内部存储可用：" + romSpace);

        //listView设置滑动监听
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mUserList != null && mSystemList != null) {
                    if (firstVisibleItem <= mUserList.size()) {
                        tvHeader.setText("用户应用（" + mUserList.size() + ")");
                    } else {
                        tvHeader.setText("系统应用（" + mUserList.size() + ")");
                    }
                }
            }
        });

        //listView设置点击监听
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo info = appInfoAdapter.getItem(position);
                if(info != null){
                    mCurrentAppinfo = info;
                    showPopupWindow(view);
                }
            }
        });

        initData();
    }

    /**
     * PopupWindow弹窗
     * @param view
     */
    private void showPopupWindow(View view) {
        if (contentView == null) {
            //PopupWindow布局初始化
            contentView = View.inflate(this, R.layout.popup_item, null);
            mPopupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
            TextView tvUninstall = (TextView) contentView.findViewById(R.id.tv_uninstall);
            TextView tvLaunch = (TextView) contentView.findViewById(R.id.tv_launch);
            TextView tvShare = (TextView) contentView.findViewById(R.id.tv_share);
            tvUninstall.setOnClickListener(this);
            tvLaunch.setOnClickListener(this);
            tvShare.setOnClickListener(this);

            //渐变动画
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            alphaAnimation.setDuration(500);
            //缩放动画
            ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(500);
            //动画集合
            set = new AnimationSet(true);
            set.addAnimation(alphaAnimation);
            set.addAnimation(scaleAnimation);
        }

        mPopupWindow.showAsDropDown(view, view.getWidth() / 5 - 20, -view.getHeight());
        contentView.startAnimation(set);

    }

    /**
     * 初始化数据
     */
    public void initData() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                mList = AppInfoProvider.getIntalledApps(getApplicationContext());
                mUserList = new ArrayList<AppInfo>();
                mSystemList = new ArrayList<AppInfo>();
                for (AppInfo appInfo : mList) {
                    if (appInfo.isUser) {
                        mUserList.add(appInfo);
                    } else {
                        mSystemList.add(appInfo);

                    }
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appInfoAdapter = new AppInfoAdapter();
                        listView.setAdapter(appInfoAdapter);
                        ll.setVisibility(View.GONE);
                    }
                });
            }
        }).start();


    }

    /**
     * 获取可用空间
     *
     * @param path
     */
    private String getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        long availableBlocks = statFs.getAvailableBlocks();
        long blockSize = statFs.getBlockSize();
        //可用存储空间
        long availSize = availableBlocks * blockSize;

        return Formatter.formatFileSize(this, availSize);
    }



    /**
     * ListView适配器AppInfoAdapter
     */
    class AppInfoAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mUserList.size() + mSystemList.size() + 2;//加入两个标题栏
        }

        @Override
        public AppInfo getItem(int position) {

            if (position == 0 || position == mUserList.size() + 1) {//碰到标题栏了
                return null;
            }
            if (position < mUserList.size() + 1) {
                return mUserList.get(position - 1);
            } else {
                return mSystemList.get(position - mUserList.size() - 2);
            }

        }

        /**
         * 获取布局数量
         * @return
         */
        @Override
        public int getViewTypeCount() {
            return 2;
        }

        /**
         * 获取布局类型
         * @param position
         * @return
         */
        @Override
        public int getItemViewType(int position) {
            if (position == 0 || position == mUserList.size() + 1) {//碰到标题栏了
                return 0;//标题栏类型
            } else {
                return 1;//普通类型
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
//            View view = null;
            int type = getItemViewType(position);
            switch (type) {
                case 0://标题栏类型
                    HeaderHolder Hholder = null;
                    if (convertView == null) {
                        convertView = View.inflate(getApplicationContext()
                                , R.layout.list_item_header, null);
                        Hholder = new HeaderHolder();
                        Hholder.tvHeader = (TextView) convertView.findViewById(R.id.tv_header);
                        convertView.setTag(Hholder);
                    } else {
                        Hholder = (HeaderHolder) convertView.getTag();
                    }
                    if (position == 0) {
                        Hholder.tvHeader.setText("用户应用（" + mUserList.size() + ")");
                    } else {
                        Hholder.tvHeader.setText("系统应用（" + mSystemList.size() + ")");
                    }

                    break;
                case 1://普通类型
                    ViewHolder holder = null;
                    if (convertView == null) {
                        convertView = View.inflate(getApplicationContext()
                                , R.layout.list_item_appinfo, null);
                        holder = new ViewHolder();
                        holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
                        holder.tvSpace = (TextView) convertView.findViewById(R.id.tv_space);
                        holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                        convertView.setTag(holder);
                    } else {
//                view = convertView;
                        holder = (ViewHolder) convertView.getTag();
                    }
                    holder.tvName.setText(getItem(position).name);
//            holder.tvSpace.setText(mList.get(position).packageName);
                    holder.ivIcon.setImageDrawable(getItem(position).icon);
                    if (getItem(position).isRom) {
                        holder.tvSpace.setText("手机内存");
                    } else {
                        holder.tvSpace.setText("外置存储卡");
                    }
                    break;
            }


            return convertView;
        }
    }

    static class ViewHolder {
        public TextView tvName;
        public TextView tvSpace;
        public ImageView ivIcon;
    }

    static class HeaderHolder {
        public TextView tvHeader;
    }

    /**
     * 点击事件
     * @param v
     */
    @Override
    public void onClick(View v) {

        mPopupWindow.dismiss();//弹窗消失
        switch (v.getId()) {
            case R.id.tv_uninstall:
                uninstall();
                break;
            case R.id.tv_launch:
                launch();
                break;
            case R.id.tv_share:
                share();
                break;
        }
    }

    /**
     * 分享
     */
    private void share() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");//分享纯文本
        intent.putExtra(Intent.EXTRA_TEXT,"分享给你一个很好的应用");
        startActivity(intent);
    }

    /**
     * 启动
     */
    private void launch() {
        PackageManager packageManager = getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(mCurrentAppinfo.packageName);
        if(intent!=null){
            startActivity(intent);
        }else{
            Toast.makeText(this, "找不到启动页面", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 卸载
     */
    private void uninstall() {
        if(mCurrentAppinfo.isUser){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + mCurrentAppinfo.packageName));
            startActivityForResult(intent,0);
        }else{
            Toast.makeText(this, "无法卸载", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //
        initData();
    }
}
