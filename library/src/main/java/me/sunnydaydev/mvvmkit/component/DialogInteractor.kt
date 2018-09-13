@file:Suppress("DEPRECATION")

package me.sunnydaydev.mvvmkit.component

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.SingleSubject
import me.sunnydaydev.mvvmkit.R
import me.sunnydaydev.mvvmkit.component.DialogInteractor.Action
import me.sunnydaydev.mvvmkit.component.DialogInteractor.InputCheckResult
import me.sunnydaydev.mvvmkit.util.rx.invoke
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 12.09.2018.
 * mail: mail@sunnydaydev.me
 */

interface DialogInteractor {

    fun <T> showMessage(
            title: String? = null,
            message: String?,
            negativeAction: Action<T>? = null,
            neutralAction: Action<T>? = null,
            positiveAction: Action<T>? = null,
            cancellable: Boolean = true,
            theme: Int? = null
    ): Maybe<T>

    fun requestInput(
            title: String? = null,
            initialValue: String? = null,
            inputType: Int = InputType.TYPE_CLASS_TEXT,
            inputViewType: Int = Config.DEFAULT_INPUT_VIEW,
            validate: (String) -> InputCheckResult = { InputCheckResult.Success },
            cancellable: Boolean = true,
            theme: Int? = null
    ): Maybe<String>

    fun showProgress(
            title: String? = null,
            message: String? = null,
            cancellable: Boolean = false,
            theme: Int? = null
    ): Completable

    fun chooseDate(initialDate: Date = Date(),
                   maxDate: Date? = null,
                   minDate: Date? = null,
                   cancellable: Boolean = true,
                   theme: Int? = null): Maybe<Date>

    enum class Button { POSITIVE, NEUTRAL, NEGATIVE }

    class Action<T> internal constructor(
            internal val value: (Button) -> T,
            internal val text: (ctx: Context, def: String) -> String) {

        companion object {

            fun <T> create(text: String, value: T) = Action(
                    value = { value },
                    text = { _, _ -> text }
            )

            fun <T> create(textId: Int, value: T) = Action(
                    value = { value },
                    text = { ctx, _  -> ctx.getString(textId) }
            )

            fun <T> create(value: T, text: () -> String) = Action(
                    value = { value },
                    text = { _, _ -> text() }
            )

            fun <T> create(value: T) = Action(
                    value = { value },
                    text = { _, def -> def }
            )

            fun simple(textId: Int) = Action(
                    text = { ctx, _  -> ctx.getString(textId) },
                    value = { it }
            )

            fun simple(text: String) = Action(
                    text = { _, _  -> text },
                    value = { it }
            )

            fun simple() = Action(
                    text = { _, def  -> def },
                    value = { it }
            )

        }

    }

    sealed class InputCheckResult {

        object Success: InputCheckResult()

        data class NotValid(val message: String): InputCheckResult()

    }

    data class Config(
            internal val defaultDialogTheme: Int?,
            internal val defaultProgressDialogTheme: Int?,
            internal val defaultPositiveText: (Context) -> String,
            internal val defaultNeutralText: (Context) -> String,
            internal val defaultNegativeText: (Context) -> String,
            internal val inputViewProvider: (type: Int, Context) -> Pair<View, EditText>
    ) {

        companion object {

            const val DEFAULT_INPUT_VIEW = -1

        }

        class Builder() {

            private var defaultDialogTheme: Int? = null
            private var defaultProgressDialogTheme: Int? = null

            private var defaultPositiveText: (Context) -> String =
                    { it.getString(android.R.string.ok) }

            private var defaultNeutralText: (Context) -> String =
                    { it.getString(android.R.string.cancel) }

            private var defaultNegativeText: (Context) -> String =
                    { it.getString(android.R.string.no) }

            private val inputViewProvidersMap =
                    mutableMapOf<Int, (Context) -> Pair<View, EditText>>()

            fun defaultDialogTheme(theme: Int) = apply {
                defaultDialogTheme = theme
            }
            
            fun defaultProgressDialogTheme(theme: Int) = apply {
                defaultProgressDialogTheme = theme
            }
            
            fun defaultPositiveText(textId: Int) = apply {
                defaultPositiveText = { it.getString(textId) }
            }
            
            fun defaultPositiveText(text: String) = apply {
                defaultPositiveText = { text }
            }

            fun defaultNeutralText(textId: Int) = apply {
                defaultNeutralText = { it.getString(textId) }
            }

            fun defaultNeutralText(text: String) = apply {
                defaultNeutralText = { text }
            }

            fun defaultNegativeText(textId: Int) = apply {
                defaultNegativeText = { it.getString(textId) }
            }

            fun defaultNegativeText(text: String) = apply {
                defaultNegativeText = { text }
            }

            fun registerInputView(
                    type: Int = Config.DEFAULT_INPUT_VIEW,
                    provider: (Context) -> Pair<View, EditText>) = apply {
                inputViewProvidersMap[type] = provider
            }

            fun build() = Config(
                    defaultDialogTheme = defaultDialogTheme,
                    defaultProgressDialogTheme = defaultProgressDialogTheme,
                    defaultPositiveText = defaultPositiveText,
                    defaultNeutralText = defaultNeutralText,
                    defaultNegativeText = defaultNegativeText,
                    inputViewProvider = { type, context ->
                        val provider = inputViewProvidersMap[type] ?: defaultInputViewProvider
                        provider(context)
                    }
            )

            companion object {

                private val defaultInputViewProvider: (Context) -> Pair<View, EditText> get() = {
                    val editText = EditText(it).apply {
                        id = R.id.dialog_interactor_input_view
                    }
                    editText to editText
                }

            }

        }

    }

    object Factory {

        fun create(activityTracker: ActivityTracker, config: Config): DialogInteractor =
                DefaultDialogInteractor(activityTracker, config)

    }

}

open class DefaultDialogInteractor constructor(
        activityTracker: ActivityTracker,
        private val config: DialogInteractor.Config
): BaseDialogInteractor(activityTracker), DialogInteractor {

    override fun <T> showMessage(
            title: String?,
            message: String?,
            negativeAction: Action<T>?,
            neutralAction: Action<T>?,
            positiveAction: Action<T>?,
            cancellable: Boolean,
            theme: Int?
    ) = maybeDialog{ activity ->

        val subject = subject<T>()

        val checkedTheme = theme ?: config.defaultDialogTheme
        val dialogBuilder =
                if (checkedTheme != null) AlertDialog.Builder(activity, checkedTheme)
                else AlertDialog.Builder(activity)

        val dialog = dialogBuilder
                .setTitle(title)
                .setMessage(message)
                .let {

                    if (negativeAction == null) return@let it

                    val text = negativeAction.text(
                            activity,
                            config.defaultNegativeText(activity)
                    )
                    it.setNegativeButton(text) { _, _ ->
                        subject(negativeAction.value(DialogInteractor.Button.NEGATIVE))
                    }

                }
                .let {

                    if (neutralAction == null) return@let it

                    val text = neutralAction.text(
                            activity,
                            config.defaultNeutralText(activity)
                    )
                    it.setNeutralButton(text) { _, _ ->
                        subject(neutralAction.value(DialogInteractor.Button.NEUTRAL))
                    }

                }
                .let {

                    if (positiveAction == null) return@let it

                    val text = positiveAction.text(
                            activity,
                            config.defaultPositiveText(activity)
                    )
                    it.setPositiveButton(text) { _, _ ->
                        subject(positiveAction.value(DialogInteractor.Button.POSITIVE))
                    }

                }
                .setCancelable(cancellable)
                .setOnCancelListener {
                    subject.cancelled()
                }
                .show()

        subject.doFinally {
            dialog.dismiss()
        }

    }

    @SuppressLint("InflateParams")
    override fun requestInput(
            title: String?,
            initialValue: String?,
            inputType: Int,
            inputViewType: Int,
            validate: (String) -> InputCheckResult,
            cancellable: Boolean,
            theme: Int?) =  maybeDialog { activity ->

        val subject = subject<String>()

        val checkedTheme = theme ?: config.defaultDialogTheme

        val builder =
                if (checkedTheme != null) AlertDialog.Builder(activity, checkedTheme)
                else AlertDialog.Builder(activity)

        builder.setTitle(title)
                .setCancelable(cancellable)
                .setOnCancelListener {
                    subject.cancelled()
                }

        val (container, input) = config.inputViewProvider(inputViewType, activity)

        input.apply {

            this.inputType = inputType

            setText(initialValue)

            addTextChangedListener(object : TextWatcher {

                override fun afterTextChanged(p0: Editable) {}

                override fun beforeTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence, p1: Int, p2: Int, p3: Int) {
                    if (this@apply.error != null) this@apply.error = null
                }

            })

        }

        container.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        )

        builder.setView(container)

        builder.setNegativeButton(config.defaultNegativeText(activity)) { dialog, _ ->
            dialog.cancel()
        }

        builder.setPositiveButton(config.defaultNegativeText(activity), null)

        builder.setOnCancelListener { subject.cancelled() }

        val dialog = builder.create()

        dialog.setOnShowListener { _ ->

            val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

            button.setOnClickListener { _ ->

                val text = input.text.toString()
                val checkResult = validate(text)

                when(checkResult) {
                    is InputCheckResult.Success -> subject(text)
                    is InputCheckResult.NotValid -> input.error = checkResult.message
                }

            }

            input.requestFocus()
            input.setSelection(input.text?.length ?: 0)

        }

        dialog.show()

        subject.doFinally {
            dialog.dismiss()
        }

    }

    override fun showProgress(title: String?,
                              message: String?,
                              cancellable: Boolean,
                              theme: Int?) = completableDialog { activity ->

        val checkedTheme = theme ?: config.defaultProgressDialogTheme
        val dialog =
                if (checkedTheme != null) ProgressDialog(activity, checkedTheme)
                else ProgressDialog(activity)

        dialog.apply {
            isIndeterminate = true
            setCancelable(false)
            setTitle(title)
            setMessage(message)
        }

        dialog.show()

        never<Any>().doFinally {
            dialog.dismiss()
        }

    }

    override fun chooseDate(initialDate: Date,
                            maxDate: Date?,
                            minDate: Date?,
                            cancellable: Boolean,
                            theme: Int?): Maybe<Date> = maybeDialog { activity ->

        val subject = subject<Date>()

        val checkedTheme = theme ?: config.defaultDialogTheme

        val calendar = Calendar.getInstance().apply {
            time = initialDate
        }

        val listener: (picker: DatePicker, year: Int, month: Int, day: Int) -> Unit = {_, y, m, d ->

            calendar.set(Calendar.DAY_OF_MONTH, d)
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.YEAR, y)

            subject(calendar.time)

        }

        val d = calendar.get(Calendar.DAY_OF_MONTH)
        val m = calendar.get(Calendar.MONTH)
        val y = calendar.get(Calendar.YEAR)

        val dialog =
                if (checkedTheme == null) DatePickerDialog(activity, listener, y, m, d)
                else DatePickerDialog(activity, checkedTheme, listener, y, m, d)

        dialog.apply {
            setCancelable(cancellable)
            setOnCancelListener { subject.cancelled() }
        }

        dialog.setOnShowListener {
            if (maxDate != null) dialog.datePicker.maxDate = max(maxDate.time, initialDate.time)
            if (minDate != null) dialog.datePicker.minDate = min(minDate.time, initialDate.time)
        }

        dialog.show()

        subject.doFinally {
            dialog.dismiss()
        }

    }

}

open class BaseDialogInteractor(
        private val activityTracker: ActivityTracker
) {

    protected fun completableDialog(
            waitResumedActivity: Boolean = true,
            dialogSource: (activity: Activity) -> Single<DialogResult<Any>>
    ): Completable = createDialogSource(waitResumedActivity, dialogSource)
            .ignoreElement()

    @Suppress("unused")
    protected fun <T> maybeDialog(
            waitResumedActivity: Boolean = true,
            dialogSource: (activity: Activity) -> Single<DialogResult<T>>
    ): Maybe<T> = createDialogSource(waitResumedActivity, dialogSource)
            .flatMapMaybe {
                when(it) {
                    is DialogResult.Cancelled -> Maybe.empty()
                    is DialogResult.Success -> Maybe.just(it.result)
                }
            }

    @Suppress("unused")
    protected fun <T> singleDialog(
            waitResumedActivity: Boolean = true,
            dialogSource: (activity: Activity) -> Single<DialogResult<T>>
    ): Single<T> = createDialogSource(waitResumedActivity, dialogSource)
            .flatMap {
                when(it) {
                    is DialogResult.Cancelled ->
                        Single.error(UIInteractorError.Cancelled())
                    is DialogResult.Success -> Single.just(it.result)
                }
            }

    private fun <T> createDialogSource(
            waitResumedActivity: Boolean = true,
            dialogSource: (activity: Activity) -> Single<DialogResult<T>>
    ): Single<DialogResult<T>> {

        return activityTracker.lastResumedActivity
                .switchMapSingle<DialogResult<T>> { optional ->

                    val context = optional.value
                            ?: return@switchMapSingle if (waitResumedActivity) Single.never()
                            else Single.error(UIInteractorError.ViewNotPresent())

                    Single.defer { dialogSource(context) }
                            .subscribeOn(AndroidSchedulers.mainThread())

                }
                .firstOrError()

    }

    protected fun <T> SingleSubject<DialogResult<T>>.cancelled() = this(DialogResult.Cancelled())
    operator protected fun <T> SingleSubject<DialogResult<T>>.invoke(result: T) =
            this(DialogResult.Success(result))

    @Suppress("unused")
    protected sealed class DialogResult<T> {
        class Cancelled<T>: DialogResult<T>()
        data class Success<T>(val result: T): DialogResult<T>()
    }

    protected fun <T> subject(): SingleSubject<DialogResult<T>> = SingleSubject.create()

    protected fun <T> never(): Single<DialogResult<T>> = Single.never()

}