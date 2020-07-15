package com.denzo.daschess.util;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.annotation.NonNull;

public enum  NetHelper {
    INSTANCE;

    public static final int TYPE_DISCONNECT = 0;
    public static final int TYPE_WIFI = 1;
    public static final int TYPE_MOBILE = 2;


    private int mCurNetStatus;
    private Context mContext;

    public void init(Context context){
        mContext = context;
        checkNet();
    }

    /**
     * 检测当前网络状态
     */
    public void checkNet(){
        try {
            ConnectivityManager connectivity = (ConnectivityManager) mContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null&& info.isAvailable()) {
                    // 判断当前网络是否已经连接
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        if(info.getType() == ConnectivityManager.TYPE_WIFI)
                            mCurNetStatus =  TYPE_WIFI;
                        if(info.getType() == ConnectivityManager.TYPE_MOBILE)
                            mCurNetStatus =  TYPE_MOBILE;
                    }
                } else{
                    mCurNetStatus = TYPE_DISCONNECT;
                }
            }
        } catch (Exception e) {
            Log.v("error",e.toString());
            e.printStackTrace();
            mCurNetStatus = TYPE_DISCONNECT;
        }
    }


    @NonNull
    public Boolean getNetEnabled(){
        return mCurNetStatus == TYPE_MOBILE || mCurNetStatus == TYPE_WIFI;
    }

    /**
     * 是否处于移动网络状态
     * @return
     */
    @NonNull
    public Boolean isMobileStatus(){
        return mCurNetStatus == TYPE_MOBILE;
    }

    /**
     * 获取当前网络状态
     * @return
     */
    public int getNetStatus() {
        return mCurNetStatus;
    }
}