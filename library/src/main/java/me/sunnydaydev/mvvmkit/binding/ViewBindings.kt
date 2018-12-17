package me.sunnydaydev.mvvmkit.binding

import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.adapters.ListenerUtil
import me.sunnydaydev.mvvmkit.observable.Command
import me.sunnydaydev.mvvmkit.observable.TargetedCommand
import me.sunnydaydev.mvvmkit.R

/**
 * Created by sunny on 30.05.2018.
 * mail: mail@sunnydaydev.me
 */

object ViewBindings: Bindings() {

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
    @BindingAdapter(value = ["focusCommand", "focusTarget"], requireAll = true)
    fun <T: Any> bindFocusCommand(view: View, focus: TargetedCommand<Unit, T>?, target: T?) {
        focus ?: return
        target ?: return

        focus.handle(target) {
            view.requestFocus()
        }

    }

    @JvmStatic
    @BindingAdapter(value = ["focusCommand"])
    fun bindFocusPureCommand(view: View, focus: Command<Unit>?) {
        focus ?: return
        focus.handle { view.requestFocus() }
    }

    // endregion

    // region Touch/Click

    @JvmStatic
    @BindingAdapter("onClick")
    fun bindOnClick(view: View, onClickListener: OnClickListener?) {
        if (onClickListener != null) {
            view.setOnClickListener { onClickListener() }
        } else {
            view.setOnClickListener(null)
        }
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
            command: Command<Unit>?,
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
            command: Command<Unit>?,
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

    // region Tags

    @JvmStatic
    @BindingAdapter("tags")
    fun bindTags(view: View, tags: Map<Int, Any>) {
        tags.forEach {
            view.setTag(it.key, it.value)
        }
    }

    @JvmStatic
    @BindingAdapter("tag")
    fun bindTags(view: View, tag: Any) {
        view.tag = tag
    }

    // endregion

    @JvmStatic
    @BindingAdapter(
            value = [
                "marginStart",
                "marginTop",
                "marginEnd",
                "marginBottom",
                "marginRight",
                "marginLeft"
            ],
            requireAll = false
    )
    fun bindMargins(view: View, start: Int?, top: Int?, end: Int?, bottom: Int?,
                    right: Int?, left: Int?) {
        val lp = view.layoutParams as? ViewGroup.MarginLayoutParams ?: return

        with(lp) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                if (start != null) marginStart = start
                if (end != null) lp.marginEnd = end
            }

            if (top != null) topMargin = top
            if (bottom != null) bottomMargin = bottom
            if (right != null) rightMargin = right
            if (left != null) leftMargin = left

        }

        view.layoutParams = lp

    }

    @JvmStatic
    @BindingAdapter("onLayoutSizeChanged")
    fun bindOnLayoutChanged(view: View, callback: OnLayoutSizeChangedCallback?) {

        val previous: OnLayoutChangedCallbackListener? =
                view.getListener(R.id.binding_layout_size_changed_listener)

        when {
            previous?.callback === callback -> return
            previous != null -> view.removeOnLayoutChangeListener(previous)
        }

        if (callback == null) return

        val new = OnLayoutChangedCallbackListener(callback)
        view.trackListener(R.id.binding_layout_size_changed_listener, new)
        view.addOnLayoutChangeListener(new)

    }

    @JvmStatic
    @BindingAdapter(value = ["onBind", "onEachBind"], requireAll = false)
    fun bindOnBind(view: View, onBind: Runnable?, onEachBind: Boolean?) {

        if (onBind == null) {
            ListenerUtil.trackListener(view, null, R.id.binding_view_on_bind)
            return
        }

        val current = view.getListener<Runnable>(R.id.binding_view_on_bind)

        if (onEachBind != true && current === onBind) return

        onBind.run()

    }

    // region Classes, interfaces, etc.

    interface OnClickListener {
        operator fun invoke()
    }

    interface OnTouchListener {
        fun onTouch(event: MotionEvent): Boolean
    }


    interface OnLayoutSizeChangedCallback {
        fun onLayoutSizeChanged(width: Int, height: Int)
    }

    private class OnLayoutChangedCallbackListener(
            val callback: OnLayoutSizeChangedCallback
    ): View.OnLayoutChangeListener {

        override fun onLayoutChange(
                view: View,
                p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
            callback.onLayoutSizeChanged(view.width, view.height)
        }

    }

    // endregion

}