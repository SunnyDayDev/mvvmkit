package me.sunnydaydev.mvvmkit.component

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.annotation.ColorInt
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
            maxLines: Int? = null,
            duration: Int = Snackbar.LENGTH_LONG,
            @IdRes viewId: Int = View.NO_ID
    ): Completable

    fun <T> make(
            message: String,
            action: Action<T>,
            maxLines: Int? = null,
            @ColorInt actionColor: Int? = null,
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

        fun create(activityTracker: ActivityTracker, config: Config): SnackbarInteractor =
                DefaultSnackBarInteractor(activityTracker, config)

    }

    data class Config internal constructor(
            internal val maxLines: Int?,
            internal val actionColor: Int?
    ) {

        class Builder {

            private var maxLines: Int? = null
            private var actionColor: Int? = null

            fun defaultMaxLines(lines: Int) = apply {
                maxLines = lines
            }

            fun defaultActionColor(@ColorInt color: Int) = apply {
                actionColor = color
            }

            fun build() = Config(maxLines, actionColor)

        }

    }

}
 
open class DefaultSnackBarInteractor(
        activityTracker: ActivityTracker,
        private val config: SnackbarInteractor.Config
): BaseSnackbarInteractor(activityTracker), SnackbarInteractor {

    override fun make(
            message: String,
            maxLines: Int?,
            duration: Int,
            @IdRes viewId: Int
    ): Completable = completableSnackbar { activity ->

        val view = activity.snackbarView(viewId)

        val subject = CompletableSubject.create()

        val snackbar = snackbar(view, message, duration, maxLines)
                .apply {
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
            maxLines: Int?,
            actionColor: Int?,
            duration: Int,
            @IdRes viewId: Int
    ): Maybe<T> = actionSnackbar { activity ->

        val view = activity.snackbarView(viewId)

        val subject = MaybeSubject.create<T>()

        val snackbar = snackbar(view, message, duration, maxLines ?: config.maxLines)
                .apply {
                    setAction(action.text(activity)) { subject(action.value) }

                    val checkedColor = actionColor ?: config.actionColor

                    if (checkedColor != null) {
                        setActionTextColor(checkedColor)
                    }

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

    protected fun snackbar(
            view: View,
            message: String,
            duration: Int,
            maxLines: Int?
    ): Snackbar = Snackbar.make(view, message, duration).apply {
        if (maxLines != null) {
            textView?.maxLines = maxLines
        }
    }

}