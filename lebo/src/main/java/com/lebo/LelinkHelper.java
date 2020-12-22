package com.lebo;

import android.content.Context;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.hpplay.sdk.source.api.IBindSdkListener;
import com.hpplay.sdk.source.api.IConnectListener;
import com.hpplay.sdk.source.api.ILelinkPlayerListener;
import com.hpplay.sdk.source.api.LelinkSourceSDK;
import com.hpplay.sdk.source.browse.api.IAPI;
import com.hpplay.sdk.source.browse.api.IBrowseListener;
import com.hpplay.sdk.source.browse.api.LelinkServiceInfo;

import java.util.List;


public class LelinkHelper {


    private static final String TAG = "LelinkHelper";

    private static final String APP_ID = "13265";
    private static final String APP_SECRET = "216d49919057c9891607425c41e5a487";

    private static LelinkHelper sLelinkHelper;
    private UIHandler mUIHandler;
    private AllCast mAllCast;
    // 数据
    private List<LelinkServiceInfo> mInfos;

    public static LelinkHelper getInstance(Context context) {
        if (sLelinkHelper == null) {
            sLelinkHelper = new LelinkHelper(context);
        }
        return sLelinkHelper;
    }

    private LelinkHelper(Context context) {
        mUIHandler = new UIHandler(Looper.getMainLooper());
        mAllCast = new AllCast(context.getApplicationContext(),
                APP_ID, APP_SECRET,false,
                mBindSdkListener,
                mBrowseListener,
                mConnectListener,
                mPlayerListener);
    }

    public void setUIUpdateListener(IUIUpdateListener listener) {
        mUIHandler.setUIUpdateListener(listener);
    }

    private IBindSdkListener mBindSdkListener = new IBindSdkListener() {
        @Override
        public void onBindCallback(boolean b) {
            Log.i("onBindCallback", "--------->" + b);
            LelinkSourceSDK.getInstance().setOption(IAPI.OPTION_49, true);
        }
    };

    public List<LelinkServiceInfo> getInfos() {
        return mInfos;
    }

    public List<LelinkServiceInfo> getConnectInfos() {
        return mAllCast.getConnectInfos();
    }

    public void connect(LelinkServiceInfo info) {
        mAllCast.connect(info);
    }

    public void disConnect(LelinkServiceInfo info) {
        mAllCast.disConnect(info);
    }

    public void playNetMedia(String url, int mediaType, String screencode,String referer) {
        mAllCast.playNetMedia(url, mediaType, screencode,referer);
    }

    public void resume() {
        mAllCast.resume();
    }

    public void pause() {
        mAllCast.pause();
    }

    public void stop() {
        mAllCast.stop();
    }

    public void seekTo(int progress) {
        mAllCast.seekTo(progress);
    }

    public void setVolume(int percent) {
        mAllCast.setVolume(percent);
    }

    public void voulumeUp() {
        mAllCast.voulumeUp();
    }

    public void voulumeDown() {
        mAllCast.voulumeDown();
    }


    public void startWithLoopMode(String url, boolean isLocalFile) {
        mAllCast.startWithLoopMode(url, isLocalFile);
    }


    public void setOption(int opt, Object... values) {
        mAllCast.setOption(opt, values);
    }

    public void release() {
        mAllCast.release();
    }

    private Message buildMessageDetail(int state, String text) {
        return buildMessageDetail(state, text, null);
    }

    private Message buildMessageDetail(int state, String text, Object object) {
        MessageDeatail deatail = new MessageDeatail();
        deatail.text = text;
        deatail.obj = object;

        Message message = Message.obtain();
        message.what = state;
        message.obj = deatail;
        return message;
    }

    private IBrowseListener mBrowseListener = new IBrowseListener() {

        @Override
        public void onBrowse(int resultCode, List<LelinkServiceInfo> list) {
            mInfos = list;
            if (resultCode == IBrowseListener.BROWSE_SUCCESS) {
                StringBuffer buffer = new StringBuffer();
                if (null != mInfos) {
                    for (LelinkServiceInfo info : mInfos) {
                        buffer.append("name：").append(info.getName())
                                .append(" uid: ").append(info.getUid())
                                .append(" type:").append(info.getTypes()).append("\n");
                    }
                    buffer.append("---------------------------\n");
                    if (null != mUIHandler) {
                        // 发送文本信息
                        String text = buffer.toString();
                        if (mInfos.isEmpty()) {
                            mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_SEARCH_NO_RESULT, text));
                        } else {
                            mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_SEARCH_SUCCESS, text));
                        }
                    }
                }
            } else {
                if (null != mUIHandler) {
                    // 发送文本信息
                    mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_SEARCH_ERROR, "搜索错误：Auth错误"));
                }
            }

        }

    };

    private IConnectListener mConnectListener = new IConnectListener() {

        @Override
        public void onConnect(final LelinkServiceInfo serviceInfo, final int extra) {
            if (null != mUIHandler) {
                String type = extra == TYPE_LELINK ? "Lelink" : extra == TYPE_DLNA ? "DLNA" : extra == TYPE_NEW_LELINK ? "NEW_LELINK" : "IM";
                String text;
                if (TextUtils.isEmpty(serviceInfo.getName())) {
                    text = "pin码连接" + type + "成功";
                } else {
                    text = serviceInfo.getName() + "连接" + type + "成功";
                }
                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_CONNECT_SUCCESS, text, serviceInfo));
            }
        }

        @Override
        public void onDisconnect(LelinkServiceInfo serviceInfo, int what, int extra) {
            if (what == IConnectListener.CONNECT_INFO_DISCONNECT) {
                if (null != mUIHandler) {
                    String text;
                    if (TextUtils.isEmpty(serviceInfo.getName())) {
                        text = "pin码连接断开";
                    } else {
                        text = serviceInfo.getName() + "连接断开";
                    }
                    mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_DISCONNECT, text));
                }
            } else if (what == IConnectListener.CONNECT_ERROR_FAILED) {
                String text = null;
                if (extra == IConnectListener.CONNECT_ERROR_IO) {
                    text = serviceInfo.getName() + "连接失败";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_WAITTING) {
                    text = serviceInfo.getName() + "等待确认";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_REJECT) {
                    text = serviceInfo.getName() + "连接拒绝";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_TIMEOUT) {
                    text = serviceInfo.getName() + "连接超时";
                } else if (extra == IConnectListener.CONNECT_ERROR_IM_BLACKLIST) {
                    text = serviceInfo.getName() + "连接黑名单";
                }
                if (null != mUIHandler) {
                    mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_CONNECT_FAILURE, text));
                }
            }
        }

    };

    private ILelinkPlayerListener mPlayerListener = new ILelinkPlayerListener() {

        @Override
        public void onLoading() {
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_LOADING, "开始加载"));
            }
        }

        @Override
        public void onStart() {
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_PLAY, "开始播放"));
            }
        }

        @Override
        public void onPause() {
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_PAUSE, "暂停播放"));
            }
        }

        @Override
        public void onCompletion() {
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_COMPLETION, "播放完成"));
            }
        }

        @Override
        public void onStop() {
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_STOP, "播放结束"));
            }
        }

        @Override
        public void onSeekComplete(int pPosition) {
            mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_SEEK, "设置进度", pPosition));
        }

        @Override
        public void onInfo(int what, int extra) {
            String text = null;
            if (what == ILelinkPlayerListener.INFO_SCREENSHOT) {
                if (extra == ILelinkPlayerListener.INFO_SCREENSHOT_COMPLATION) {
                    text = "截图完成";
                } else {
                    text = "截图失败";
                }
                if (null != mUIHandler) {
                    mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_SCREENSHOT, text));
                }
            }
        }

        @Override
        public void onInfo(int i, String s) {

        }

        @Override
        public void onError(int what, int extra) {
            String text = null;
            Log.d(TAG, "onError what:" + what + " extra:" + extra);
            if (what == PUSH_ERROR_INIT) {
                if (extra == PUSH_ERRROR_FILE_NOT_EXISTED) {
                    text = "文件不存在";
                } else if (extra == PUSH_ERROR_IM_OFFLINE) {
                    text = "IM TV不在线";
                } else if (extra == PUSH_ERROR_IMAGE) {

                } else if (extra == PUSH_ERROR_IM_UNSUPPORTED_MIMETYPE) {
                    text = "IM不支持的媒体类型";
                } else {
                    text = "未知";
                }
            } else if (what == MIRROR_ERROR_INIT) {
                if (extra == MIRROR_ERROR_UNSUPPORTED) {
                    text = "不支持镜像";
                } else if (extra == MIRROR_ERROR_REJECT_PERMISSION) {
                    text = "镜像权限拒绝";
                } else if (extra == MIRROR_ERROR_DEVICE_UNSUPPORTED) {
                    text = "设备不支持镜像";
                } else if (extra == NEED_SCREENCODE) {
                    text = "请输入投屏码";
                }
            } else if (what == MIRROR_ERROR_PREPARE) {
                if (extra == MIRROR_ERROR_GET_INFO) {
                    text = "获取镜像信息出错";
                } else if (extra == MIRROR_ERROR_GET_PORT) {
                    text = "获取镜像端口出错";
                } else if (extra == NEED_SCREENCODE) {
                    text = "投屏码模式不支持 ";
                } else if (what == PUSH_ERROR_PLAY) {
                    if (extra == PUSH_ERROR_NOT_RESPONSED) {
                        text = "播放无响应";
                    } else {
                        text = "投屏码模式不支持 ";
                    }
                } else if (what == PUSH_ERROR_STOP) {
                    if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                        text = "退出 播放无响应";
                    }
                } else if (what == PUSH_ERROR_PAUSE) {
                    if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                        text = "暂停无响应";
                    }
                } else if (what == PUSH_ERROR_RESUME) {
                    if (extra == ILelinkPlayerListener.PUSH_ERROR_NOT_RESPONSED) {
                        text = "恢复无响应";
                    }
                }

            } else if (what == MIRROR_PLAY_ERROR) {
                if (extra == MIRROR_ERROR_FORCE_STOP) {
                    text = "接收端断开";
                } else if (extra == MIRROR_ERROR_PREEMPT_STOP) {
                    text = "镜像被抢占";
                }
            } else if (what == MIRROR_ERROR_CODEC) {
                if (extra == MIRROR_ERROR_NETWORK_BROKEN) {
                    text = "镜像网络断开";
                }
            }

            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_PLAY_ERROR, text));
            }
        }

        /**
         * 音量变化回调
         *
         * @param percent 当前音量
         */
        @Override
        public void onVolumeChanged(float percent) {
        }

        /**
         * 进度更新回调
         *
         * @param duration 媒体资源总长度
         * @param position 当前进度
         */
        @Override
        public void onPositionUpdate(long duration, long position) {
            long[] arr = new long[]{duration, position};
            if (null != mUIHandler) {
                mUIHandler.sendMessage(buildMessageDetail(IUIUpdateListener.STATE_POSITION_UPDATE, "进度更新", arr));
            }
        }

    };

    public void browse() {
        LelinkSourceSDK.getInstance().startBrowse();
    }
}