package me.shetj.custom.layoutManager

import androidx.recyclerview.widget.RecyclerView

/**
 * 1.计算每个ItemView的位置；
 * 2.处理滑动事件；
 * 3. 缓存并重用ItemView；
 */
abstract class BaseLayoutManager : RecyclerView.LayoutManager() {


    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT,
            RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    //region 计算每个ItemView的位置

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (state.itemCount == 0) {
            removeAndRecycleAllViews(recycler)//这个view是被回收了。
            return
        }
        if (itemCount <= 0 || state.isPreLayout) {
            return
        }
        /*
          先把所有的View先从RecyclerView中detach掉，
          然后标记为"Scrap"状态，表示这些View处于可被重用状态(非显示中)。
          实际就是把View放到了Recycler中的一个集合中。
         */
        detachAndScrapAttachedViews(recycler)
        calculateChildrenSite(recycler)
        recycleAndFillView(recycler, state)
    }

    /**
     * 这个方法主要用于计算并保存每个ItemView的位置
     */
    abstract fun calculateChildrenSite(recycler: RecyclerView.Recycler)

    /**
     * 回收和填充，布局
     */
    abstract fun recycleAndFillView(recycler: RecyclerView.Recycler, state: RecyclerView.State)
    //endregion

    //region 处理滑动事件

    override fun canScrollHorizontally(): Boolean {
        return super.canScrollHorizontally()
    }

    override fun canScrollVertically(): Boolean {
        return super.canScrollVertically()
    }


    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        return super.scrollVerticallyBy(dy, recycler, state)
    }


    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        return super.scrollHorizontallyBy(dx, recycler, state)
    }


   open fun getVerticalSpace(): Int {
        // 计算RecyclerView的可用高度，除去上下Padding值
        return height - paddingBottom - paddingTop
    }


    open fun getHorizontalSpace(): Int {
        return width - paddingLeft - paddingRight
    }

    //endregion



}