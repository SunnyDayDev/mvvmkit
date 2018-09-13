package me.sunnydaydev.mvvmkit.component

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 13.09.2018.
 * mail: mail@sunnydaydev.me
 */
 
sealed class UIInteractorError: Error() {

    class ViewNotPresent: UIInteractorError()

    class Cancelled: UIInteractorError()

    companion object {

        fun viewNotPresent(): Nothing = throw ViewNotPresent()

    }

}