package me.sunnydaydev.mvvmkit

/**
 * Created by sunny on 11.06.2018.
 * mail: mail@sunnydaydev.me
 */

interface OnBackPressedListener {

    val prioritet get() = 0

    fun onBackPressed(): Boolean

}