package me.sunnydaydev.mvvmkit.component

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.MaybeSubject
import me.sunnydaydev.mvvmkit.util.contentView
import me.sunnydaydev.mvvmkit.util.rx.invoke

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 29.08.2018.
 * mail: mail@sunnydaydev.me
 */

interface SnackbarInteractor {

    fun make(
            message: String,
            duration: Int = Snackbar.LENGTH_LONG,
            @IdRes viewId: Int = View.NO_ID
    ): Completable

    fun <T> make(
            message: String,
            action: Action<T>,
            duration: Int = Snackbar.LENGTH_LONG,
            @IdRes viewId: Int = View.NO_ID
    ): Maybe<T>

    class Action<T> internal constructor(
            internal val value: T,
            internal val text: (Context) -> String
    ) {

        companion object {

            fun <T> create(text: String, value: T) = Action(value) { text }

            fun <T> create(@StringRes textId: Int, value: T) = Action(value) { it.getString(textId) }

            fun create(text: String) = create(text, Unit)

            fun create(@StringRes textId: Int) = create(textId, Unit)

        }

    }

    object Factory {

        fun create(activityTracker: ActivityTracker): SnackbarInteractor =
                DefaultSnackBarInteractor(activityTracker)

    }

}
 
internal class DefaultSnackBarInteractor(
        activityTracker: ActivityTracker
): BaseSnackbarInteractor(activityTracker), SnackbarInteractor {

    override fun make(
            message: String,
            duration: Int,
            @IdRes viewId: Int
    ): Completable = completableSnackbar { activity ->

        val view = activity.snackbarView(viewId)

        val subject = CompletableSubject.create()

        val snackbar = multilineSnackbar(view, message, duration).apply {
            addOnDismissedCallback { subject.onComplete() }
        }

        snackbar.show()

        subject.doFinally {
            snackbar.dismiss()
        }

    }

    override fun <T> make(
            message: String,
            action: SnackbarInteractor.Action<T>,
            duration: Int,
            @IdRes viewId: Int
    ): Maybe<T> = actionSnackbar { activity ->

        val view = activity.snackbarView(viewId)

        val subject = MaybeSubject.create<T>()


        val snackbar = multilineSnackbar(view, message, duration).apply {
            setAction(action.text(activity)) { subject(action.value) }
            addOnDismissedCallback { subject() }
        }

        snackbar.show()

        subject.doFinally {
            snackbar.dismiss()
        }

    }

}

open class BaseSnackbarInteractor(
        private val activityTracker: ActivityTracker
) {

    fun completableSnackbar(create: (Activity) -> Completable) =
            activityTracker.lastStartedActivity
                    .firstOrError()
                    .flatMapCompletable { (activity) ->

                        if (activity == null) UIInteractorError.viewNotPresent()

                        Completable.defer { create(activity) }
                                .subscribeOn(AndroidSchedulers.mainThread())

                    }

    fun <T> actionSnackbar(create: (Activity) -> Maybe<T>) =
            activityTracker.lastStartedActivity
                    .firstOrError()
                    .flatMapMaybe { (activity) ->

                        if (activity == null) UIInteractorError.viewNotPresent()

                        Maybe.defer { create(activity) }
                                .subscribeOn(AndroidSchedulers.mainThread())

                    }



    protected val Snackbar.textView: TextView? get() = view
            .findViewById(com.google.android.material.R.id.snackbar_text)

    protected fun Snackbar.addOnDismissedCallback(action: (Int) -> Unit) =
            addCallback(object : Snackbar.Callback() {

                override fun onDismissed(transientBottomBar: Snackbar, event: Int) {
                    action(event)
                }

            })

    protected fun Activity?.snackbarView(@IdRes id: Int): View {
        val activityView = this?.contentView ?: UIInteractorError.viewNotPresent()
        return activityView.findViewById(id) ?: activityView
    }

    protected fun multilineSnackbar(view: View, message: String, duration: Int): Snackbar =
            Snackbar.make(view, message, duration).apply {
                textView?.maxLines = Int.MAX_VALUE
            }

}