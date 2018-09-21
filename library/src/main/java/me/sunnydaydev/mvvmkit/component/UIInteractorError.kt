package me.sunnydaydev.mvvmkit.component

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 13.09.2018.
 * mail: mail@sunnydaydev.me
 */
 
sealed class UIInteractorError: Error {

    constructor(): super()

    constructor(message: String): super(message)

    class ViewNotPresent: UIInteractorError()

    class UnsuitableView(reason: String): UIInteractorError(reason)

    class Cancelled: UIInteractorError()

    companion object {

        fun viewNotPresent(): Nothing = throw ViewNotPresent()

        fun canceled(): Nothing = throw Cancelled()

        fun unsuitableView(
                reason: String = "View is not suitable for the required action."
        ): Nothing = throw UnsuitableView(reason)

    }

}