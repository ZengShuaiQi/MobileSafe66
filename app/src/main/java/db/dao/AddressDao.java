package db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by clever boy on 2017/7/22.
 */

public class AddressDao {

    private static final String PATH = "/data/data/com.example.cleverboy.mobilesafe66/files/address.db";

    /**
     * 根据电话号码返回归属地
     *
     * @param number
     */
    public static String getAddress(String number) {

        String address = "未知号码";

        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
                SQLiteDatabase.OPEN_READONLY);
        //判断是否是手机号码
        if (number.matches("^1[3-8]\\d{9}$")) {
            Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)",
                    new String[]{number.substring(0, 7)});

            if (cursor.moveToFirst()) {
                address = cursor.getString(0);
            }
            cursor.close();
        }
        database.close();

        return address;
    }

}
