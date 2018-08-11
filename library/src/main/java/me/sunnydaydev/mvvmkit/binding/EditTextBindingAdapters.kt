package me.sunnydaydev.mvvmkit.binding

import android.widget.EditText
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.databinding.adapters.ListenerUtil
import me.sunnydaydev.mvvmkit.R
import me.sunnydaydev.mvvmkit.view.MVVMEditText
import kotlin.math.min

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 11.08.2018.
 * mail: mail@sunnydaydev.me
 */
 
object EditTextBindingAdapters {

    @JvmStatic
    @BindingAdapter(
            value = ["selection", "selectionAttrChanged"],
            requireAll = false
    )
    fun bindSelection(view: MVVMEditText,
                      position: TextSelection?,
                      inverse: InverseBindingListener?) {

        if (position != null) {
            val textLenght = view.text?.length ?: 0
            view.setSelection(min(position.start, textLenght), min(position.end, textLenght))
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

    }

    @JvmStatic
    @InverseBindingAdapter(attribute = "selection")
    fun bindSelectionInverse(view: MVVMEditText): TextSelection {
        return TextSelection(view.selectionStart, view.selectionEnd)
    }

    private class TextSelectionListener(
            val inverse: InverseBindingListener
    ): MVVMEditText.OnSelectionChangedListener {

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