package com.vliux.bee.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import java.util.Locale;

import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;

/**
 * Created by vliux on 17/5/12.
 * @author vliux
 * http://handstandsam.com/2017/05/04/identifying-an-android-device/
 */

public class DeviceUtil {
    public static class TeleOperator {
        public String operatorName;
        public String operatorMnc; // MCC + MNC (mobile country code + mobile network code)
    }
    
    public static String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }
    
    public static String getSystemVersion() {
        return android.os.Build.VERSION.RELEASE;
    }
    
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }
    
    public static String getDeviceBrand() {
        return android.os.Build.BRAND;
    }
    
    public static String getBuildSerial(){
        return Build.SERIAL;
    }
    
    public static String getManufacture(){
        return Build.MANUFACTURER;
    }
    
    public static String getDeviceName(){
        return Build.DEVICE;
    }
    
    @Nullable
    public static String getIMEI(@NonNull final Context context){
        if(Permissions.has(context, READ_PHONE_STATE)) {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getDeviceId();
        }else return null;
    }

    @Nullable
    public static String getAndroidId(@NonNull final Context context){
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
    
    @Nullable
    public static String getPhoneNumber(@NonNull final Context context){
        if(Permissions.has(context, READ_PHONE_STATE)
                || Permissions.has(context, READ_SMS)) {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getLine1Number();
        }else return null;
    }
    
    @Nullable
    public static String getIMSI(@NonNull final Context context){
        if(Permissions.has(context, READ_PHONE_STATE)) {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return tm.getSubscriberId();
        }else return null;
    }
    
    @NonNull
    public static TeleOperator getOperator(@NonNull final Context context){
        final TeleOperator op = new TeleOperator();
        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        op.operatorName = tm.getSimOperatorName();
        op.operatorMnc = tm.getSimOperator();
        return op;
    }
    
    public static String getScreenSize(@NonNull final Context context){
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        display.getSize(point);
        return String.format(Locale.US, "%d*%d",
                point.x > point.y ? point.y : point.x,
                point.x > point.y ? point.x : point.y);
    }
    
    @Nullable
    public static String getAppLabel(@NonNull final Context context){
        final ApplicationInfo applicationInfo = context.getApplicationInfo();
        return null != applicationInfo ?
                context.getPackageManager().getApplicationLabel(applicationInfo).toString()
                : null;
    }
    
    public static String getAppPackage(@NonNull final Context context){
        return context.getPackageName();
    }
    
    public static PackageInfo getAppPackageInfo(@NonNull final Context context){
        final PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (final PackageManager.NameNotFoundException e) {
            return null;
        }
    }
    
    @Nullable
    public static String getWebViewInfo(@NonNull final Context context){
        try {
            final WebView webView = new WebView(context);
            final WebSettings webSettings = webView.getSettings();
            return null != webSettings ?
                    webSettings.getUserAgentString()
                    : null;
        }catch (final Throwable e){
            Log.w(TAG, "failed to obtain WebView info", e);
            return null;
        }
    }
    
    private static final String TAG = "DeviceUtil";
}
