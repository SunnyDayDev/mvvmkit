package me.sunnydaydev.mvvmkit.binding

import androidx.databinding.BindingAdapter
import androidx.databinding.adapters.ListenerUtil
import com.google.android.material.appbar.AppBarLayout
import me.sunnydaydev.mvvmkit.R

/**
 * Created by sunny on 12.06.2018.
 * mail: mail@sunnydaydev.me
 */

object AppBarLayoutBindings {

    @JvmStatic
    @BindingAdapter("onOffsetChanged")
    fun bindOnOffsetChanged(view: AppBarLayout,listener: OnOffsetChangedListener?) {

        ListenerUtil.getListener<AppBarLayout.OnOffsetChangedListener>(view,
                R.id.binding_appbarlayout_offset_listener)?.let {
                    view.removeOnOffsetChangedListener(it)
                }

        val appBarLayoutOffsetListener = listener?.let {
            AppBarLayout.OnOffsetChangedListener { _, offset ->
                it.onOffsetChanged(offset, view.height, view.totalScrollRange)
            }
        }

        view.addOnOffsetChangedListener(appBarLayoutOffsetListener)
        ListenerUtil.trackListener(view,appBarLayoutOffsetListener,
                R.id.binding_appbarlayout_offset_listener)

    }

    interface OnOffsetChangedListener {

        fun onOffsetChanged(offset: Int, height: Int, totalScrollRange: Int)

    }

}