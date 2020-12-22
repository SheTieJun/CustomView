package com.lebo;

import android.content.Context;
import android.text.TextUtils;

import com.hpplay.sdk.source.api.IBindSdkListener;
import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.ILelinkMirrorManager;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.LelinkPlayerInfo;
import com.hpplay.sdk.source.api.LelinkSourceSDK;
import com.hpplay.sdk.source.browse.api.AdInfo;
import com.hpplay.sdk.source.browse.api.IAPI;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;

import java.util.List;


/**
 *
 */
public class AllCast {

    private static final String TAG = "AllCast";

    public static final int MEDIA_TYPE_VIDEO = LelinkPlayerInfo.TYPE_VIDEO;
    public static final int MEDIA_TYPE_AUDIO = LelinkPlayerInfo.TYPE_AUDIO;
    public static final int MEDIA_TYPE_IMAGE = LelinkPlayerInfo.TYPE_IMAGE;
    public static final int RESOLUTION_HEIGHT = ILelinkMirrorManager.RESOLUTION_HIGH;
    public static final int RESOLUTION_MIDDLE = ILelinkMirrorManager.RESOLUTION_MID;
    public static final int RESOLUTION_AUTO = ILelinkMirrorManager.RESOLUTION_AUTO;
    public static final int BITRATE_HEIGHT = ILelinkMirrorManager.BITRATE_HIGH;
    public static final int BITRATE_MIDDLE = ILelinkMirrorManager.BITRATE_MID;
    public static final int BITRATE_LOW = ILelinkMirrorManager.BITRATE_LOW;

    private LelinkSourceSDK mLelinkPlayer;

    public AllCast(Context context, String appid, String appSecret,boolean isDebug,
                   IBindSdkListener mBindSdkListener,
                   IBrowseListener mBrowseListener,
                   IConnectListener mConnectListener,
                   ILelinkPlayerListener mPlayerListener) {
        LelinkSourceSDK.getInstance()
                .setBrowseResultListener(mBrowseListener)
                .setPlayListener(mPlayerListener)
                .setConnectListener(mConnectListener)
                .setBindSdkListener(mBindSdkListener)
                .setDebugMode(isDebug)
                .setSdkInitInfo(context.getApplicationContext(), appid, appSecret).bindSdk();
        mLelinkPlayer = LelinkSourceSDK.getInstance();
    }


    public List<LelinkServiceInfo> getConnectInfos() {
        return mLelinkPlayer.getConnectInfos();
    }


    public void stopBrowse() {
        mLelinkPlayer.stopBrowse();
    }

    public void connect(LelinkServiceInfo info) {
        mLelinkPlayer.connect(info);
    }

    public void disConnect(LelinkServiceInfo info) {
        if (info != null) {
            mLelinkPlayer.disConnect(info);
        }
    }


    public void playLocalMedia(String url, int type, String screenCode) {
        LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
        lelinkPlayerInfo.setType(type);
        lelinkPlayerInfo.setLocalPath(url);
        if (!TextUtils.isEmpty(screenCode)) {
            lelinkPlayerInfo.setOption(IAPI.OPTION_6, screenCode);
        }
        mLelinkPlayer.startPlayMedia(lelinkPlayerInfo);
    }

    public void playNetMedia(String url, int type, String screenCode,String referer) {
        LelinkPlayerInfo lelinkPlayerInfo = new LelinkPlayerInfo();
        if (!TextUtils.isEmpty(referer)) {
            lelinkPlayerInfo.setHeader("{\"Referer\":\"" + referer + "\"}");
        }
        lelinkPlayerInfo.setType(type);
        lelinkPlayerInfo.setUrl(url);
        if (!TextUtils.isEmpty(screenCode)) {
            lelinkPlayerInfo.setOption(IAPI.OPTION_6, screenCode);
        }
        lelinkPlayerInfo.setLoopMode(LelinkPlayerInfo.LOOP_MODE_SINGLE);
        mLelinkPlayer.startPlayMedia(lelinkPlayerInfo);
    }

    public void startWithLoopMode(String url, boolean isLocalFile) {
        LelinkPlayerInfo playerInfo = new LelinkPlayerInfo();
        if (isLocalFile) {
            playerInfo.setLocalPath(url);
        } else {
            playerInfo.setUrl(url);
        }
        playerInfo.setType(MEDIA_TYPE_VIDEO);
        playerInfo.setLoopMode(LelinkPlayerInfo.LOOP_MODE_SINGLE);
        mLelinkPlayer.startPlayMedia(playerInfo);
    }


    public void resume() {
        mLelinkPlayer.resume();
    }

    public void pause() {
        mLelinkPlayer.pause();
    }

    public void stop() {
        mLelinkPlayer.stopPlay();
    }

    public void seekTo(int position) {
        mLelinkPlayer.seekTo(position);
    }

    public void setVolume(int percent) {
        mLelinkPlayer.setVolume(percent);
    }

    public void voulumeUp() {
        mLelinkPlayer.addVolume();
    }

    public void voulumeDown() {
        mLelinkPlayer.subVolume();
    }


    public void onInteractiveAdShow(AdInfo adInfo, int status) {
        mLelinkPlayer.onAdShow(adInfo, status);
    }

    public void onInteractiveAdClosed(AdInfo adInfo, int duration, int status) {
        mLelinkPlayer.onAdClosed(adInfo, duration, status);
    }

    public void release() {
        mLelinkPlayer.stopPlay();
    }

    public void setOption(int opt, Object... values) {
        mLelinkPlayer.setOption(opt, values);
    }




}
