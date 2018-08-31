package me.sunnydaydev.mvvmkit.binding

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

/**
 * Created by sunny on 10.06.2018.
 * mail: mail@sunnydaydev.me
 */

object SwipeRefreshLayoutBindings: Bindings() {

    @JvmStatic
    @BindingAdapter("onRefresh")
    fun bindOnRefresh(view: SwipeRefreshLayout, listener: SwipeRefreshLayout.OnRefreshListener?) {
        view.setOnRefreshListener(listener)
    }

    @JvmStatic
    @BindingAdapter("refreshing")
    fun bindRefreshing(view: SwipeRefreshLayout, isRefreshing: Boolean) {
        view.isRefreshing = isRefreshing
    }

    @JvmStatic
    @BindingAdapter("colorSchemeColors")
    fun bindColorsSchema(view: SwipeRefreshLayout, colors: IntArray) {
        view.setColorSchemeColors(*colors)
    }

    @JvmStatic
    @BindingAdapter("colorSchemeResources")
    fun bindColorSchemeResources(view: SwipeRefreshLayout, colors: IntArray) {
        view.setColorSchemeResources(*colors)
    }

}