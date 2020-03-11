package com.wzy.schedulingshare.base.Utils.TencentCloudUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @ClassName COSConfigManager
 * @Author Wei Zhouye
 * @Date 2020/3/3
 * @Version 1.0
 */
public class COSConfigManager {

    private static COSConfigManager INSTANCE;
    private static Context sContext;

    private final String sharedPreferenceKey = "TencentCloudConfig";

    private final String appidKey = "APPID_KEY";
    private final String secretIdKey = "SECRET_ID_KEY";
    private final String secretKeyKey = "SECRET_KEY_KEY";
    private final String signUrlKey = "SIGN_URL_KEY";

    // TODO: 这里必须修改为您自己的配置项
    private static String appid; // 对象存储的服务 appid
    private static String secretId;
    private static String secretKey;
    private static String signUrl = ""; // 后台授权服务的 url 地址

    private COSConfigManager(Context context) {
        sContext = context;
    }

    public static COSConfigManager getInstance(Context context) {

        if (INSTANCE == null) {

            synchronized (COSConfigManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new COSConfigManager(context);
                    getKey();
                }
            }
        }
        return INSTANCE;
    }

    private static void getKey() {
        SharedPreferences settings = sContext.getSharedPreferences("UserInfo", 0);
        appid = settings.getString("appid", "");
        secretId = settings.getString("secretId", "");
        secretKey = settings.getString("secretKey", "");
    }

    private COSConfigManager() {
    }

    public void loadFromDisk(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE);

        appid = sharedPreferences.getString(appidKey, appid);
        signUrl = sharedPreferences.getString(signUrlKey, signUrl);
        secretId = sharedPreferences.getString(secretIdKey, secretId);
        secretKey = sharedPreferences.getString(secretKeyKey, secretKey);
    }

    public void save2Disk(Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(sharedPreferenceKey, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(appidKey, appid);
        editor.putString(signUrlKey, signUrl);
        editor.putString(secretIdKey, secretId);
        editor.putString(secretKeyKey, secretKey);

        editor.apply();
    }


    public String getSecretKey() {
        return secretKey;
    }

    public String getSecretId() {
        return secretId;
    }

    public String getAppid() {
        return appid;
    }

    public String getSignUrl() {
        return signUrl;
    }


    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public void setSignUrl(String signUrl) {
        this.signUrl = signUrl;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isTemporarySignComplete() {

        return !TextUtils.isEmpty(appid) && !TextUtils.isEmpty(signUrl);
    }

    public boolean isForeverSignComplete() {

        return !TextUtils.isEmpty(appid) && !TextUtils.isEmpty(secretId) && !TextUtils.isEmpty(secretKey);
    }

}