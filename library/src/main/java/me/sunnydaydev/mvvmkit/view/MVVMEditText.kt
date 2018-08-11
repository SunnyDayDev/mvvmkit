package me.sunnydaydev.mvvmkit.view

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import androidx.appcompat.widget.AppCompatEditText

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 06.08.2018.
 * mail: mail@sunnydaydev.me
 */
 
class MVVMEditText: AppCompatEditText {

    private var initialized = false
    private val selectionListeners = mutableSetOf<OnSelectionChangedListener>()
            .also { initialized = true }

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int):
            super(context, attrs, defStyleAttr)

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)

        if (!initialized) return

        selectionListeners.forEach {
            it.onSelectionChanged(this, selStart, selEnd)
        }

    }

    fun addOnSelectionChangedListener(listener: MVVMEditText.OnSelectionChangedListener) {
        selectionListeners.add(listener)
    }

    fun removeOnSelectionChangedListener(listener: MVVMEditText.OnSelectionChangedListener) {
        selectionListeners.remove(listener)
    }

    interface OnSelectionChangedListener {

        fun onSelectionChanged(editText: EditText, start: Int, end: Int)

    }

}