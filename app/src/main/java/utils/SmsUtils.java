package utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by clever boy on 2017/8/14.
 */

public class SmsUtils {

    public static void smsBackup(Context ctx, File output, ProgressDialog dialog) {

        try {
            Cursor cursor = ctx.getContentResolver().query(Uri.parse("content://sms"),
                    new String[]{"address", "date", "type", "body"}, null, null, null);

            dialog.setMax(cursor.getCount());

            XmlSerializer xml = Xml.newSerializer();
            xml.setOutput(new FileOutputStream(output),"utf-8");
            xml.startDocument("utf-8",false);
            xml.startTag(null,"smss");

            int progress = 0;
            while (cursor.moveToNext()){
                xml.startTag(null,"sms");

                xml.startTag(null,"address");
                String address = cursor.getString(cursor.getColumnIndex("address"));
                xml.text(address);
                xml.endTag(null,"address");

                xml.startTag(null,"date");
                String date = cursor.getString(cursor.getColumnIndex("date"));
                xml.text(date);
                xml.endTag(null, "date");

                xml.startTag(null, "type");
                String type = cursor.getString(cursor.getColumnIndex("type"));
                xml.text(type);
                xml.endTag(null, "type");

                xml.startTag(null, "body");
                String body = cursor.getString(cursor.getColumnIndex("body"));
                xml.text(body);
                xml.endTag(null, "body");

                xml.endTag(null, "sms");

                dialog.setProgress(++progress);

                Thread.sleep(500);
            }
            xml.endTag(null,"smss");
            xml.endDocument();
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //短信备份的回调接口
    public interface SmsCallback {
        //备份前, count表示短信总数
        public void preSmsBackup(int count);

        //正在备份, progress备份进度
        public void onSmsBackup(int progress);
    }
}
