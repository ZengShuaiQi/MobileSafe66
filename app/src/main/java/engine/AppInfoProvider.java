package engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import domain.AppInfo;

/**
 * Created by clever boy on 2017/8/30.
 */

public class AppInfoProvider {

    /**
     * 获取已安装应用
     */
    public static ArrayList<AppInfo> getIntalledApps(Context ctx) {

        PackageManager pm = ctx.getPackageManager();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);

        ArrayList<AppInfo> list = new ArrayList();
        for (PackageInfo packageInfo : installedPackages) {
            String packageName = packageInfo.packageName;
            AppInfo appInfo = new AppInfo();
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            String name = applicationInfo.loadLabel(pm).toString();
            Drawable icon = applicationInfo.loadIcon(pm);

            appInfo.packageName = packageName;
            appInfo.name = name;
            appInfo.icon = icon;

            int flags = applicationInfo.flags;
            if((flags&applicationInfo.FLAG_EXTERNAL_STORAGE) == applicationInfo.FLAG_EXTERNAL_STORAGE){
                //安装在sdcard
                appInfo.isRom = false;
            }else{
                //安装在手机内存
                appInfo.isRom = true;
            }
            if((flags&applicationInfo.FLAG_SYSTEM) == applicationInfo.FLAG_SYSTEM){
                //安装在sdcard
                appInfo.isUser = false;
            }else{
                //安装在手机内存
                appInfo.isUser = true;
            }

            list.add(appInfo);
        }

        return list;

    }
}
