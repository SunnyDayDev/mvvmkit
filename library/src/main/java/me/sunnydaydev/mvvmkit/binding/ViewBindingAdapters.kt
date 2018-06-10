package me.sunnydaydev.mvvmkit.binding

import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import me.sunnydaydev.mvvmkit.observable.Command
import me.sunnydaydev.mvvmkit.observable.PureCommand
import me.sunnydaydev.mvvmkit.observable.TargetedPureCommand

/**
 * Created by sunny on 30.05.2018.
 * mail: mail@sunnydaydev.me
 */

object ViewBindingAdapters {

    // region Focus

    @JvmStatic
    @InverseBindingAdapter(attribute = "focused")
    fun inverseBindingFocused(view: View): Boolean {
        return view.isFocused
    }

    @JvmStatic
    @BindingAdapter(value = ["focused", "focusedAttrChanged"], requireAll = false)
    fun bindFocused(view: View, focused: Boolean, inverse: InverseBindingListener?) {

        // TODO: ListenerUtil.trackListener(...)

        view.onFocusChangeListener = null

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
    @BindingAdapter(value = ["focusCommand", "focusTarget"])
    fun <T: Any> bindFocusCommand(view: View, focus: TargetedPureCommand<T>, target: T) {

        focus.handle(target) {
            view.requestFocus()
        }

    }

    // endregion

    // region Touch/Click

    @JvmStatic
    @BindingAdapter("onClick")
    fun bindOnClick(view: View, onClickListener: OnClickListener) {
        view.setOnClickListener { onClickListener() }
    }

    @JvmStatic
    @BindingAdapter(value = ["onTouch", "touchActionsFilter"], requireAll = false)
    fun onTouch(view: View, onTouchListener: OnTouchListener?, filter: List<Int>?) = when {

        onTouchListener == null -> view.setOnTouchListener(null)

        filter == null -> view.setOnTouchListener { _, event ->
            onTouchListener.onTouch(event)
        }

        else -> view.setOnTouchListener { _, event ->
            event.takeIf { filter.contains(it.action) } ?: return@setOnTouchListener false
            onTouchListener.onTouch(event)
        }

    }

    // endregion

    // region Visibility

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

    // endregion

    // region TransitionDrawable

    @JvmStatic
    @BindingAdapter(
            value = ["transitionBackgroundCommand", "reverseTransitionBackground"],
            requireAll = false
    )
    fun bindTransitionDrawableCommand(view: View, command: Command<Int>?, reverse: Boolean?) {

        command?.handle {
            drawableStartTransition(view.background, reverse, it)
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    @JvmStatic
    @BindingAdapter(
            value = ["transitionForegroundCommand", "reverseTransitionForeground"],
            requireAll = false
    )
    fun bindTransitionForegroundCommand(view: View, command: Command<Int>?, reverse: Boolean?) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        command?.handle {
            drawableStartTransition(view.foreground, reverse, it)
        }

    }

    @JvmStatic
    @BindingAdapter(
            value = [
                "transitionBackgroundCommand",
                "reverseTransitionBackground",
                "transitionBackgroundDuration"
            ],
            requireAll = false
    )
    fun bindTransitionBackgroundCommand(
            view: View,
            command: PureCommand?,
            reverse: Boolean?,
            duration: Int?
    ) = command?.handle {
        drawableStartTransition(view.background, reverse, duration ?: 300)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @JvmStatic
    @BindingAdapter(
            value = [
                "transitionForegroundCommand",
                "reverseTransitionForeground",
                "transitionForegroundDuration"
            ],
            requireAll = false
    )
    fun bindTransitionForegroundCommand(
            view: View,
            command: PureCommand?,
            reverse: Boolean?,
            duration: Int?
    ) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return

        command?.handle {
            drawableStartTransition(view.foreground, reverse, duration ?: 300)
        }

    }

    private fun drawableStartTransition(drawable: Drawable?, reverse: Boolean?, duration: Int) {

        val transition = drawable as? TransitionDrawable ?: return

        if (reverse == true) {
            transition.startTransition(0)
            transition.reverseTransition(duration)
        } else {
            transition.startTransition(duration)
        }

    }

    // endregion

    // region Classes, interfaces, etc.

    interface OnClickListener {
        operator fun invoke()
    }

    interface OnTouchListener {
        fun onTouch(event: MotionEvent): Boolean
    }

    // endregion

}