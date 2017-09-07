package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 黑名单数据库创建
 * Created by clever boy on 2017/8/4.
 */

public class BlackNumberOpenHelper extends SQLiteOpenHelper {

    public BlackNumberOpenHelper(Context context){
        super(context,"blacknumber.db",null,1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table blacknumber (_id integer primary key autoincrement, number varchar(20), mode integer)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
