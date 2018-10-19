package me.sunnydaydev.mvvmkit.binding

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import me.sunnydaydev.mvvmkit.R
import me.sunnydaydev.mvvmkit.view.MVVMEditText
import me.sunnydaydev.mvvmkit.view.util.OnSelectionChangedListener
import timber.log.Timber
import kotlin.math.min

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 11.08.2018.
 * mail: mail@sunnydaydev.me
 */
 
object EditTextBindings: Bindings() {

    @JvmStatic
    @BindingAdapter(
            value = ["selection", "selectionAttrChanged"],
            requireAll = false
    )
    fun bindSelection(
            view: EditText,
            position: TextSelection?,
            inverse: InverseBindingListener?
    ) {

        if (view is OnSelectionChangedListener.Owner) {

            if (position != null) {
                val textLength = view.text?.length ?: 0
                view.setSelection(min(position.start, textLength), min(position.end, textLength))
            }

            val currentListener: TextSelectionListener? = ListenerUtil.getListener(
                    view, R.id.binding_edittext_listener_selection)

            if (currentListener?.inverse == inverse) {
                return
            }

            currentListener?.let(view::removeOnSelectionChangedListener)
            ListenerUtil.trackListener(view, null, R.id.binding_edittext_listener_selection)

            inverse ?: return

            val listener = TextSelectionListener(inverse)

            view.addOnSelectionChangedListener(listener)

            ListenerUtil.trackListener(view, listener, R.id.binding_edittext_listener_selection)

        } else {

            if (inverse != null) {
                Timber.e("EditText doesn't implement OnSelectionChangedListener.Owner. Inverse binding will be ignored.")
            }

            if (position != null) {

                view.setSelection(position.start, position.end)

            }

        }

    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selection")
    fun bindSelectionInverse(view: MVVMEditText): TextSelection {
        return TextSelection(view.selectionStart, view.selectionEnd)
    }

    private class TextSelectionListener(
            val inverse: InverseBindingListener
    ): OnSelectionChangedListener {

        override fun onSelectionChanged(editText: EditText, start: Int, end: Int) {
            inverse.onChange()
        }

    }

}

data class TextSelection internal constructor(val start: Int, val end: Int) {

    val isSinle get() = start == end

    companion object {
        fun single(position: Int) = TextSelection(position, position)
        fun range(start: Int, end: Int) = TextSelection(start, end)
    }

}