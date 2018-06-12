package me.sunnydaydev.mvvmkit.binding

import android.text.method.MovementMethod
import androidx.databinding.BindingAdapter
import android.view.KeyEvent
import android.view.View
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

    @JvmStatic
    @BindingAdapter("onKeyEvent")
    fun bindOnKeyDown(view: TextView, listener: OnKeyListener?) {

        val viewListener: View.OnKeyListener? = listener?.let {
            View.OnKeyListener { _, keyCode, keyEvent ->
                it.onKey(keyCode, keyEvent)
            }
        }

        view.setOnKeyListener(viewListener)

    }

    @JvmStatic
    @BindingAdapter("movementMethod")
    fun bindMovementMethod(view: TextView, movementMethod: MovementMethod) {
        view.movementMethod = movementMethod
    }

    interface OnKeyListener {
        fun onKey(keyCode: Int, event: KeyEvent): Boolean
    }

}