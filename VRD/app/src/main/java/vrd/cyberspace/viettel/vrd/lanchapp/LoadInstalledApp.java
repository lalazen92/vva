package vrd.cyberspace.viettel.vrd.lanchapp;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thanhtn10 on 3/8/16.
 */
public class LoadInstalledApp {

    //static  public Map<String, String> packageInfoList = new HashMap<String,String>();
    static public ArrayList<PInfo> packageInfoList;
    public void launchApp(String packageName, Context context) {
        Intent mIntent = context.getPackageManager().getLaunchIntentForPackage(
                packageName);
        if (mIntent != null) {
            try {
                context.startActivity(mIntent);
            } catch (ActivityNotFoundException err) {
                Toast t = Toast.makeText(context.getApplicationContext(),
                        "Lanch Application", Toast.LENGTH_SHORT);
                t.show();
            }
        }
    }

    public String getPackNameInfo(String description){
        for (int i = 0; i < packageInfoList.size(); i++){
            if(description.contains(packageInfoList.get(i).appName) || packageInfoList.get(i).appName.contains(description))
                return packageInfoList.get(i).pName;
        }
        return "";
    }

    class PInfo {
        private String appName = "";
        private String pName = "";
        private String versionName = "";
        private int versionCode = 0;
        private Drawable icon;

    }

    public void getPackageInforMap(Context context) {
        packageInfoList = getInstalledApps(false, context); /* false = no system packages */
    }

    private ArrayList<PInfo> getInstalledApps(boolean getSysPackages, Context context) {
        ArrayList<PInfo> res = new ArrayList<PInfo>();
        List<PackageInfo> packs = context.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if ((!getSysPackages) && (p.versionName == null)) {
                continue ;
            }
            PInfo newInfo = new PInfo();
            newInfo.appName = p.applicationInfo.loadLabel(context.getPackageManager()).toString();
            newInfo.pName = p.packageName;
            newInfo.versionName = p.versionName;
            newInfo.versionCode = p.versionCode;
            newInfo.icon = p.applicationInfo.loadIcon(context.getPackageManager());
            res.add(newInfo);
        }
        return res;
    }
    public void processCommand(String command, Context context){

        String arr[] = command.split(" ");
        if(arr.length!=2)
            return ;
        if(!arr[0].equalsIgnoreCase("VVA") || arr[1].length() == 0)
            return;
        String packageItemInfo = getPackNameInfo(arr[1]);
        packageItemInfo = packageItemInfo.toLowerCase();
        if(packageItemInfo == null || packageItemInfo.length() == 0)
            return;
        launchApp(packageItemInfo, context);
    }
    public void processVoiceCommand(String command, Context context){
        if(command.length() == 0)
            return;
        String packageItemInfo = getPackNameInfo(command);
        packageItemInfo = packageItemInfo.toLowerCase();
        if(packageItemInfo == null || packageItemInfo.length() == 0)
            return;
        launchApp(packageItemInfo, context);
    }
}
