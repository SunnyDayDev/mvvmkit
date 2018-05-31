package me.sunnydaydev.mvvmkit.binding

import android.databinding.BindingAdapter
import android.widget.TextView

/**
 * Created by sunny on 04.05.2018.
 * mail: mail@sunnydaydev.me
 */

object TextViewBindingAdapters {

    @JvmStatic
    @BindingAdapter("errorHint")
    fun bindErrorHint(view: TextView, error: String?) {
        view.error = error
    }

}