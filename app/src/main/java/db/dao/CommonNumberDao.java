package db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by clever boy on 2017/7/22.
 */

public class CommonNumberDao {

    private static final String PATH = "/data/data/com.example.cleverboy.mobilesafe66/files/commonnum.db";

    /**
     * 获取常用号码组的信息
     */
    public static ArrayList<GroupInfo> getCommonNumberGroups() {

        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null,
                SQLiteDatabase.OPEN_READONLY);//打开数据库,只支持从data/data目录打开，不能从assets目录打开
        //从数据库查询数据
        Cursor cursor = database.query("classlist", new String[]{"name", "idx"}
                , null, null, null, null, null);
        ArrayList<GroupInfo> list = new ArrayList<>();//存放组信息对象
        while (cursor.moveToNext()) {

            GroupInfo info = new GroupInfo();
            String name = cursor.getString(0);
            String idx = cursor.getString(1);
            info.name = name;
            info.idx = idx;
            info.children = getCommonNumberChildren(idx,database);
            list.add(info);
        }
        cursor.close();
        database.close();

        return list;
    }

    /**
     * 获取某个组孩子的信息
     *
     * @param idx
     * @param database
     */
    public static ArrayList<ChildInfo> getCommonNumberChildren(String idx, SQLiteDatabase database) {
        Cursor cursor = database.query("table" + idx, new String[]{"number", "name"}
                , null, null, null, null, null);
        ArrayList<ChildInfo> list = new ArrayList<>();//存放某个组的孩子信息对象
        while (cursor.moveToNext()) {

            ChildInfo info = new ChildInfo();
            String number = cursor.getString(0);
            String name = cursor.getString(1);
            info.name = name;
            info.number = number;

            list.add(info);
        }
        cursor.close();
        return list;
    }

    public static class GroupInfo {
        public String name;
        public String idx;
        public ArrayList<ChildInfo> children;
    }

    public static class ChildInfo {
        public String name;
        public String number;

    }

}
