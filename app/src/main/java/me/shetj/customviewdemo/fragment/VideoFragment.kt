package me.shetj.customviewdemo.fragment

import android.os.Bundle
import android.transition.ChangeBounds
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import me.shetj.base.mvp.BaseFragment
import me.shetj.base.mvp.EmptyPresenter
import me.shetj.base.tools.app.FragmentUtils
import me.shetj.customviewdemo.R
import timber.log.Timber


class VideoFragment : BaseFragment<EmptyPresenter>(), FragmentUtils.OnBackClickListener ,LifecycleEventObserver{

    private var videoPlayer: StandardGSYVideoPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = ChangeBounds()
        sharedElementReturnTransition = ChangeBounds()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        videoPlayer = view.findViewById(R.id.videoPlayer)
        val source1 =
            "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
        videoPlayer?.setUp(source1, true, "测试视频")
        videoPlayer?.startPlayLogic()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            VideoFragment()
    }

    override fun initEventAndData() {

    }

    override fun onBackClick(): Boolean {
        FragmentUtils.remove(this)  //no hide  must remove
        return true
    }


    override fun onDestroyView() {
        super.onDestroyView()
        videoPlayer?.release()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
         Timber.tag("onStateChanged").i(event.name)
    }

}