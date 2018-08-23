package me.sunnydaydev.mvvmkit.binding

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 19.07.2018.
 * mail: mail@sunnydaydev.me
 */

object ConstraintLayoutBindings: Bindings() {

    @JvmStatic
    @BindingAdapter("layout_constraintDimensionRatio")
    fun bindLayoutConstraintDimensionRatio(view: View, ratio: Float)
            = bindLayoutConstraintDimensionRatio(view, (ratio * 10000).toInt(), 10000)

    @JvmStatic
    @BindingAdapter(
            "layout_constraintDimensionRatioWidth",
            "layout_constraintDimensionRatioHeight"
    )
    fun bindLayoutConstraintDimensionRatio(view: View, width: Int, height: Int) {

        val lp = view.layoutParams as? ConstraintLayout.LayoutParams ?: return

        lp.dimensionRatio = "$width:$height"

        view.requestLayout()

    }

}