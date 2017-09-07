package activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.cleverboy.mobilesafe66.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by clever boy on 2017/7/14.
 */

public class ContactActivity extends Activity {
    private ListView lvContact;
    private Handler mHandler;
    private ArrayList<HashMap<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvContact = (ListView) findViewById(R.id.lv_contact);
        readContact();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    list = (ArrayList<HashMap<String, String>>) msg.obj;
                    lvContact.setAdapter(new SimpleAdapter(getApplicationContext(), list,
                            R.layout.list_item_contact, new String[]{"name", "phone"},
                            new int[]{R.id.tv_name, R.id.tv_phone}));
                }
            }
        };


        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                HashMap<String, String> map = list.get(position);
                String phone = map.get("phone");

                //将电话号码信息放在intent中,回传给上一个页面
                Intent data = new Intent();
                data.putExtra("phone", phone);
                setResult(0, data);
                finish();
            }
        });
    }

    /**
     * 读取联系人数据 需要权限:android.permission.READ_CONTACTS
     */
    private void readContact() {
        // raw_contacts, data, mimetypes
        // 1. 先从raw_contacts中读取联系人的contact_id
        // 2. 根据contact_id,从data中查出联系人相关信息
        // 3. 根据mimetype_id, 查询mimetype表,得到信息类型(名称,号码)
        new Thread(new Runnable() {
            @Override
            public void run() {
                Cursor rawContactCursor = getContentResolver().query(
                        Uri.parse("content://com.android.contacts/raw_contacts"),
                        new String[]{"contact_id"}, null, null, null);

                ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();// 所有联系人集合
                while (rawContactCursor.moveToNext()) {// 遍历所有联系人
                    String contactId = rawContactCursor.getString(0);

                    // 系统查询的不是data这个表,而是view_data这个视图,视图是多个表的整合, data+mimetypes
                    Cursor dataCursor = getContentResolver().query(
                            Uri.parse("content://com.android.contacts/data"),
                            new String[]{"data1", "mimetype"}, "raw_contact_id=?",
                            new String[]{contactId}, null);

                    HashMap<String, String> map = new HashMap<String, String>();// 保存某个联系人的信息
                    while (dataCursor.moveToNext()) {// 遍历单个联系人所有字段信息
                        String data = dataCursor.getString(0);
                        String type = dataCursor.getString(1);

                        if ("vnd.android.cursor.item/phone_v2".equals(type)) {
                            // 电话号码
                            map.put("phone", data);
                        } else if ("vnd.android.cursor.item/name".equals(type)) {
                            // 名称
                            map.put("name", data);
                        }
                    }

                    dataCursor.close();

                    // 脏数据, 过滤掉
                    if (!TextUtils.isEmpty(map.get("name"))
                            && !TextUtils.isEmpty(map.get("phone"))) {
                        list.add(map);
                    }
                }
                rawContactCursor.close();
                //发消息
                Message message = new Message();
                message.what = 0;
                message.obj = list;
                mHandler.sendMessage(message);
            }
        }).start();


    }

}
