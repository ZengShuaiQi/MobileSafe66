package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cleverboy.mobilesafe66.R;

import java.util.ArrayList;

import db.dao.BlackNumberDao;
import domain.BlackNumberInfo;
import utils.MD5Utils;
import utils.PreUtils;

/**
 * Created by clever boy on 2017/8/3.
 */

public class BlackNumberActivity extends Activity {

    private ListView lvBlack;
    private BlackNumberDao mDao;
    private ArrayList<BlackNumberInfo> mList;
    private BlackNumberAdapter mAdapter;
    private ProgressBar progressBar;
    private int mIndex = 0;
    private boolean isLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balck_number);

        mDao = BlackNumberDao.getInstance(this);

        lvBlack = (ListView) findViewById(R.id.lv_blackNo);
        progressBar = (ProgressBar) findViewById(R.id.pb_loading);

        //滑动监听
        lvBlack.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {//滑动空闲
                    //判断是否到底部
                    //判断当前屏幕展示的最后一个条目是否是集合中最后一个元素
                    int lastVisiblePosition = lvBlack.getLastVisiblePosition();
                    if (lastVisiblePosition >= mList.size() - 1 && !isLoading) {
                        //判断是否到最后一页
                        //获取数据库数据的总个数，和当前集合中的个数进行对比
                        int totalCount = mDao.getTotalCount();
                        if (mList.size() < totalCount) {
                            initData();
                            Toast.makeText(BlackNumberActivity.this, "加载", Toast.LENGTH_LONG).show();

                        } else {
                            Toast.makeText(BlackNumberActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        initData();
    }

    private void initData() {
        isLoading = true;
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (mList == null) {
                    mList = mDao.findPart(mIndex);
                } else {
                    ArrayList<BlackNumberInfo> part = mDao.findPart(mIndex);
                    mList.addAll(part);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAdapter == null) {
                            mAdapter = new BlackNumberAdapter();
                            lvBlack.setAdapter(mAdapter);
                        } else {
                            mAdapter.notifyDataSetChanged();
                        }

                        mIndex = mList.size();

                        //隐藏进度条
                        progressBar.setVisibility(View.GONE);
                        isLoading = false;
                    }
                });

            }
        }).start();

    }

    class BlackNumberAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public BlackNumberInfo getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //listView重用
        //1.重用convertView
        //2.使用静态的ViewHolder,减少findViewById的次数
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = null;
            ViewHolder holder = null;
            if (convertView == null) {
                view = View.inflate(getApplicationContext(), R.layout.list_item_black_number, null);
                holder = new ViewHolder();

                TextView tvNumber = (TextView) view.findViewById(R.id.tv_number);
                TextView tvMode = (TextView) view.findViewById(R.id.tv_mode);
                ImageView ivDelete = (ImageView) view.findViewById(R.id.iv_delete);

                holder.tvNumber = tvNumber;
                holder.tvMode = tvMode;
                holder.ivDelete = ivDelete;
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            final BlackNumberInfo info = getItem(position);
            holder.tvNumber.setText(info.number);
            switch (info.mode) {
                case 1:
                    holder.tvMode.setText("拦截电话");
                    break;
                case 2:
                    holder.tvMode.setText("拦截短信");
                    break;
                case 3:
                    holder.tvMode.setText("拦截全部");
                    break;
            }
            //给删除按钮添加点击事件
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDao.delete(info.number);
                    mList.remove(info);
                    mAdapter.notifyDataSetChanged();
                    if(mList.size()<6){
                        initData();
                    }
                }
            });

            return view;
        }
    }

    static class ViewHolder {
        TextView tvNumber;
        TextView tvMode;
        ImageView ivDelete;
    }

    public void addBlackNumber(View view) {
        showInputPwdDialog();

    }

    private void showInputPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_add_black_num, null);
        dialog.setView(view);
        dialog.show();

        Button btn_add = (Button) view.findViewById(R.id.btn_add);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        final EditText etBlackNum = (EditText) view.findViewById(R.id.et_black_num);
        final RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.rg_group);

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blackNum = etBlackNum.getText().toString().trim();

                if (!TextUtils.isEmpty(blackNum)) {
                    int checkedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                    int mode = 1;
                    switch (checkedRadioButtonId) {
                        case R.id.rb_phone:
                            mode = 1;
                            break;
                        case R.id.rb_sms:
                            mode = 2;
                            break;
                        case R.id.rb_all:
                            mode = 3;
                            break;
                    }
                    mDao.add(blackNum,mode);
                    //给集合添加元素
                    BlackNumberInfo addInfo = new BlackNumberInfo();
                    addInfo.number = blackNum;
                    addInfo.mode = mode;
                    mList.add(0,addInfo);
                    //刷新数据
                    mAdapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(BlackNumberActivity.this, "输入内容不能为空", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
