package me.sunnydaydev.mvvmkit.binding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import android.view.View

/**
 * Created by sunny on 30.05.2018.
 * mail: mail@sunnydaydev.me
 */

object ViewBindingAdapters {

    @JvmStatic
    @BindingAdapter("onClick")
    fun bindOnClick(view: View, onClickListener: OnClickListener) {
        view.setOnClickListener { onClickListener() }
    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "focused")
    fun inverseBindingFocused(view: View): Boolean {
        return view.isFocused
    }

    @JvmStatic
    @BindingAdapter(value = ["focused", "focusedAttrChanged"], requireAll = false)
    fun bindFocused(view: View, focused: Boolean?, inverse: InverseBindingListener?) {

        // TODO: ListenerUtil.trackListener(...)

        view.onFocusChangeListener = null

        if (focused == null) return

        if (focused != view.isFocused) {
            if (focused) {
                view.requestFocus()
            } else {
                view.clearFocus()
            }
        }

        if (inverse != null) {
            view.setOnFocusChangeListener { _, _ -> inverse.onChange() }
        }

    }

    @JvmStatic
    @BindingAdapter(value = ["visible", "goneOnInvisible"], requireAll = false)
    fun bindVisible(view: View, visible: Boolean?, gone: Boolean?) {
        visible ?: return
        view.visibility = when {
            visible -> View.VISIBLE
            gone != false -> View.GONE
            else -> View.INVISIBLE
        }
    }

    interface OnClickListener {
        operator fun invoke()
    }

}