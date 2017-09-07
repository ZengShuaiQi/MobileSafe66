package activity;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.cleverboy.mobilesafe66.R;

import java.util.ArrayList;

import db.dao.CommonNumberDao;

/**常用号码查询
 * Created by clever boy on 2017/9/5.
 */

public class CommonNumberActivity extends Activity {

    private ExpandableListView elvList;
    private CommonNumberAdapter commonNumberAdapter;
    private ArrayList<CommonNumberDao.GroupInfo> mGroupList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_numberr);
        elvList = (ExpandableListView) findViewById(R.id.elv_list);
        mGroupList = CommonNumberDao.getCommonNumberGroups();

        commonNumberAdapter = new CommonNumberAdapter();
        elvList.setAdapter(commonNumberAdapter);
        //设置组的孩子点击监听
        elvList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                CommonNumberDao.ChildInfo child = commonNumberAdapter.getChild(groupPosition, childPosition);
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" +child.number));
                startActivity(intent);
                return false;
            }
        });
    }

    /**
     * ExpandableListView适配器
     */
    class CommonNumberAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return mGroupList.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mGroupList.get(groupPosition).children.size();
        }

        @Override
        public CommonNumberDao.GroupInfo getGroup(int groupPosition) {
            return mGroupList.get(groupPosition);
        }

        @Override
        public CommonNumberDao.ChildInfo getChild(int groupPosition, int childPosition) {
            return mGroupList.get(groupPosition).children.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView view = new TextView(getApplicationContext());
            view.setTextColor(Color.RED);
            view.setTextSize(20);
            String name = getGroup(groupPosition).name;
            view.setText("       "+name);
            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            TextView view = new TextView(getApplicationContext());
            view.setTextColor(Color.BLACK);
            view.setTextSize(18);
            CommonNumberDao.ChildInfo child = getChild(groupPosition, childPosition);
            view.setText(child.name + "\n" + child.number);
            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
