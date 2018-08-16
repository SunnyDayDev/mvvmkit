package me.sunnydaydev.mvvmkit.view.recyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.*
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 08.08.2018.
 * mail: mail@sunnydaydev.me
 */
 
open class DrawableResDividerItemDecoration(
        @DrawableRes private val drawableId: Int
): RecyclerView.ItemDecoration() {

    private lateinit var divider: Drawable

    private val dividerHeight by lazy { divider.intrinsicHeight }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        initDrawable(parent.context)

        parent.forEachIndexed { index, view ->

            val previousView = if (index != 0) parent[index - 1] else null

            val needDrawDividerBefore = needDrawDividerBefore(view, parent, state) &&
                    (previousView == null || !needDrawDividerAfter(previousView, parent, state))

            val needDrawDividerAfter = needDrawDividerAfter(view, parent, state)

            if (needDrawDividerBefore) {

                val top = view.top - view.marginTop
                divider.setBounds(0, top - dividerHeight, parent.width, top)
                divider.draw(c)

            }

            if (needDrawDividerAfter) {

                val top = view.bottom + view.marginBottom
                divider.setBounds(0, top, parent.width, top + dividerHeight)
                divider.draw(c)

            }

        }

    }

    override fun getItemOffsets(outRect: Rect, view: View,
                                parent: RecyclerView, state: RecyclerView.State) {

        initDrawable(parent.context)

        val index = parent.indexOfChild(view)
        val previousView = if (index != 0) parent[index - 1] else null

        val needDrawDividerBefore = needDrawDividerBefore(view, parent, state) &&
                (previousView == null || !needDrawDividerAfter(previousView, parent, state))

        val needDrawDividerAfter = needDrawDividerAfter(view, parent, state)

        outRect.set(
                0, if (needDrawDividerBefore) dividerHeight else 0,
                0, if (needDrawDividerAfter) dividerHeight else 0)

    }

    private fun initDrawable(context: Context) {
        if (!::divider.isInitialized) {
            divider = ContextCompat.getDrawable(context, drawableId)!!
        }
    }

    open fun needDrawDividerAfter(view: View,
                                  parent: RecyclerView,
                                  state: RecyclerView.State): Boolean =
            parent.getChildAdapterPosition(view) != state.itemCount -1 ||
                    view.bottom + view.marginBottom < parent.height

    open fun needDrawDividerBefore(view: View,
                                   parent: RecyclerView,
                                   state: RecyclerView.State): Boolean = false

}