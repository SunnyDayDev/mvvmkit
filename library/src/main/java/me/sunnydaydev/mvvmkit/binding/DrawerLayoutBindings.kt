package me.sunnydaydev.mvvmkit.binding

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.core.view.GravityCompat
import androidx.databinding.BindingAdapter
import androidx.drawerlayout.widget.DrawerLayout
import me.sunnydaydev.mvvmkit.observable.Command

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 03.08.2018.
 * mail: mail@sunnydaydev.me
 */
 
object DrawerLayoutBindings: Bindings() {

    @JvmStatic
    @BindingAdapter(
            value = [
                "openLeft", "openStart", "openRight", "openEnd", "openGravity",
                "closeLeft", "closeStart", "closeRight", "closeEnd", "closeGravity",
                "animateOpenClose"
            ],
            requireAll = false
    )
    @SuppressLint("RtlHardcoded")
    fun bindCloseOpenCommands(view: DrawerLayout,
                              openLeft: Command<Unit>?,
                              openStart: Command<Unit>?,
                              openRight: Command<Unit>?,
                              openEnd: Command<Unit>?,
                              openGravity: Command<Int>?,
                              closeLeft: Command<Unit>?,
                              closeStart: Command<Unit>?,
                              closeRight: Command<Unit>?,
                              closeEnd: Command<Unit>?,
                              closeGravity: Command<Int>?,
                              animateOpenClose: Boolean?) {

        val animate = animateOpenClose == true

        fun open(gravity: Int) {
            view.openDrawer(gravity, animate)
        }

        fun close(gravity: Int) {
            view.closeDrawer(gravity, animate)
        }

        openLeft?.handle { open(Gravity.LEFT) }
        openStart?.handle { open(GravityCompat.START) }
        openRight?.handle { open(Gravity.RIGHT) }
        openEnd?.handle { open(GravityCompat.END) }
        openGravity?.handle(::open)

        closeLeft?.handle { close(Gravity.LEFT) }
        closeStart?.handle { close(GravityCompat.START) }
        closeRight?.handle { close(Gravity.RIGHT) }
        closeEnd?.handle { close(GravityCompat.END) }
        closeGravity?.handle(::close)

    }

}