package me.sunnydaydev.mvvmkit.binding.internal

import android.os.Handler
import android.os.Looper

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 23.08.2018.
 * mail: mail@sunnydaydev.me
 */
 
internal abstract class BindableCore {

    private val uiHandler = Handler(Looper.getMainLooper())

    private var changesId = 0

    private var executingChanges: Runnable? = null

    @Synchronized
    protected fun notifyChanged(immediately: Boolean = false) {

        executingChanges?.let(uiHandler::removeCallbacks)
        executingChanges = null

        changesId += 1

        if (immediately) {

            applyChanges()

        } else {

            val currentId = changesId

            val request = Runnable {
                executingChanges = null
                if (currentId != changesId) return@Runnable
                applyChanges()
            }

            executingChanges = request
            uiHandler.post(request)

        }

    }

    protected abstract fun applyChanges()

}