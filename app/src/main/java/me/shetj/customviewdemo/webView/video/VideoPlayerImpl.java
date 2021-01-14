package me.shetj.customviewdemo.webView.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;


import java.util.HashSet;
import java.util.Set;

import kotlin.Pair;
import me.shetj.customviewdemo.R;

public class VideoPlayerImpl implements InterVideo, EventInterceptor {

    private Activity mActivity;
    private WebView mWebView;
    private Set<Pair<Integer, Integer>> mFlags;
    private View mMovieView = null;
    private ViewGroup mMovieParentView = null;
    private View progressVideo;
    private WebChromeClient.CustomViewCallback mCallback;
    private VideoWebListener mListener;
    /**
     * 设置是否使用该自定义视频，默认使用
     */
    private boolean isShowCustomVideo = true;

    public VideoPlayerImpl(Activity activity, WebView webView) {
        this.mActivity = activity;
        this.mWebView = webView;
        mFlags = new HashSet<>();
    }

    public static boolean isActivityAlive(final Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }

    public void setListener(VideoWebListener mListener) {
        this.mListener = mListener;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (isShowCustomVideo) {
            if (!isActivityAlive(mActivity)) {
                return;
            }
            mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            // 如果一个视图已经存在，那么立刻终止并新建一个
            if (mMovieView != null) {
                callback.onCustomViewHidden();
                return;
            }
            if (mWebView != null) {
                mWebView.setVisibility(View.GONE);
                if (mListener != null) {
                    mListener.hindWebView();
                }
            }
            //添加view到decorView容齐中
            fullViewAddView(view);
            this.mCallback = callback;
            this.mMovieView = view;
            if (mListener != null) {
                mListener.showVideoFullView();
            }
        }
    }

    /**
     * 添加view到decorView容齐中
     *
     * @param view view
     */
    private void fullViewAddView(View view) {
        if (mMovieParentView == null) {
            FrameLayout mDecorView = (FrameLayout) mActivity.getWindow().getDecorView();
            mMovieParentView = new FullscreenHolder(mActivity);
            Log.i("VideoPlayerImpl", "--Video-----onShowCustomView----添加view到decorView容齐中---");
            mDecorView.addView(mMovieParentView);
        }
        mMovieParentView.addView(view);
        mMovieParentView.setVisibility(View.VISIBLE);
    }


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onHideCustomView() {
        if (isShowCustomVideo) {
            if (mMovieView == null || mActivity == null) {
                // 不是全屏播放状态
                return;
            }
            Log.i("VideoPlayerImpl", "--Video-----onShowCustomView----切换方向---");
            if (mActivity.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            }

            if (mMovieParentView != null && mMovieView != null) {
                mMovieView.setVisibility(View.GONE);
                mMovieParentView.removeView(mMovieView);
            }
            if (mMovieParentView != null) {
                mMovieParentView.setVisibility(View.GONE);
                if (mListener != null) {
                    mListener.hindVideoFullView();
                }
            }
            if (this.mCallback != null) {
                mCallback.onCustomViewHidden();
            }
            this.mMovieView = null;
            if (mWebView != null) {
                mWebView.setVisibility(View.VISIBLE);
                if (mListener != null) {
                    mListener.showWebView();
                }
            }
        }
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (progressVideo == null && mActivity != null) {
            LayoutInflater inflater = LayoutInflater.from(mActivity);
            progressVideo = inflater.inflate(R.layout.view_web_video_progress, null);
        }
        return progressVideo;
    }

    @Override
    public boolean isVideoState() {
        return mMovieView != null;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public boolean event() {
        if (isVideoState()) {
            this.onHideCustomView();
            if (isActivityAlive(mActivity)) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 销毁的时候需要移除一下视频view
     */
    public void removeAllViews() {
        if (mMovieView != null) {
            mMovieParentView.removeAllViews();
        }
    }

    /**
     * 设置是否使用自定义视频视图
     *
     * @param showCustomVideo 是否使用自定义视频视图
     */
    public void setShowCustomVideo(boolean showCustomVideo) {
        this.isShowCustomVideo = showCustomVideo;
    }

}