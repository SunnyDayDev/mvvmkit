package me.sunnydaydev.mvvmkit.component

import android.annotation.SuppressLint
import android.app.ProgressDialog
import androidx.appcompat.app.AlertDialog
import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import me.sunnydaydev.mvvmkit.R
import me.sunnydaydev.mvvmkit.component.DialogInteractor.Action
import me.sunnydaydev.mvvmkit.component.DialogInteractor.Button
import me.sunnydaydev.mvvmkit.component.DialogInteractor.InputCheckResult
import me.sunnydaydev.mvvmkit.util.rx.invoke

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
            inputViewType: Int = -1,
            validate: (String) -> InputCheckResult = { InputCheckResult.Success },
            cancellable: Boolean = true,
            theme: Int? = null
    ): Maybe<String>

    fun showProgress(
            title: String?,
            message: String?,
            theme: Int? = null
    ): Completable

    enum class Button { POSITIVE, NEUTRAL, NEGATIVE }

    class Action<T> internal constructor(
            internal val value: T,
            internal val textProvider: (ctx: Context, def: String) -> String) {

        companion object {

            fun <T> create(text: String, value: T) = Action(value) { _, _ ->text }

            fun <T> create(textId: Int, value: T) =
                    Action(value) { ctx, _  -> ctx.getString(textId) }

            fun <T> create(value: T, textProvider: () -> String) =
                    Action(value) { _, _ -> textProvider() }

            fun <T> create(value: T) = Action(value) { _, def -> def }

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

        class Builder() {

            private var defaultDialogTheme: Int? = null
            private var defaultProgressDialogTheme: Int? = null

            private var defaultPositiveText: (Context) -> String =
                    { it.getString(android.R.string.ok) }

            private var defaultNeutralText: (Context) -> String =
                    { it.getString(android.R.string.cancel) }

            private var defaultNegativeText: (Context) -> String =
                    { it.getString(android.R.string.no) }

            private val inputViewProvidersMap = mutableMapOf<Int, (Context) -> Pair<View, EditText>>()

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

            fun registerInputView(type: Int, provider: (Context) -> Pair<View, EditText>) {
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
                DialogInteractorImpl(activityTracker, config)

    }

}

fun DialogInteractor.showMessage(
        title: String? = null,
        message: String?,
        negativeAction: Action<Button>? = null,
        neutralAction: Action<Button>? = null,
        positiveAction: Action<Button>? = Action.create(Button.POSITIVE),
        cancellable: Boolean = true,
        theme: Int? = null
): Maybe<Button> = showMessage(
        title, message, negativeAction, neutralAction, positiveAction, cancellable, theme)

internal class DialogInteractorImpl constructor(
        private val activityTracker: ActivityTracker,
        private val config: DialogInteractor.Config
): DialogInteractor {

    override fun <T> showMessage(
            title: String?,
            message: String?,
            negativeAction: Action<T>?,
            neutralAction: Action<T>?,
            positiveAction: Action<T>?,
            cancellable: Boolean,
            theme: Int?
    ) = maybeDialog<T> { context, finalizers ->

        Single.create { emitter ->

            val checkedTheme = theme ?: config.defaultDialogTheme
            val dialogBuilder =
                    if (checkedTheme != null) AlertDialog.Builder(context, checkedTheme)
                    else AlertDialog.Builder(context)

            dialogBuilder
                    .setTitle(title)
                    .setMessage(message)
                    .let {

                        if (negativeAction == null) return@let it

                        val text = negativeAction.textProvider(
                                context,
                                config.defaultNegativeText(context)
                        )
                        it.setNegativeButton(text) { _, _ ->
                            emitter.success(negativeAction.value)
                        }

                    }
                    .let {

                        if (neutralAction == null) return@let it

                        val text = neutralAction.textProvider(
                                context,
                                config.defaultNeutralText(context)
                        )
                        it.setNeutralButton(text) { _, _ ->
                            emitter.success(neutralAction.value)
                        }

                    }
                    .let {

                        if (positiveAction == null) return@let it

                        val text = positiveAction.textProvider(
                                context,
                                config.defaultPositiveText(context)
                        )
                        it.setPositiveButton(text) { _, _ ->
                            emitter.success(positiveAction.value)
                        }

                    }
                    .setCancelable(cancellable)
                    .setOnCancelListener {
                        emitter.cancelled()
                    }
                    .show()
                    .also { finalizers.add { it.dismiss() } }

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
            theme: Int?) =  maybeDialog<String> {
        activity, finalizers ->

        Single.create { emitter ->

            val checkedTheme = theme ?: config.defaultDialogTheme

            val builder =
                    if (checkedTheme != null) AlertDialog.Builder(activity, checkedTheme)
                    else AlertDialog.Builder(activity)

            builder.setTitle(title)
                    .setCancelable(cancellable)
                    .setOnCancelListener {
                        emitter.cancelled()
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

            builder.setOnCancelListener { emitter.cancelled() }

            val dialog = builder.create()

            dialog.setOnShowListener { _ ->

                val button = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

                button.setOnClickListener { _ ->

                    val text = input.text.toString()
                    val checkResult = validate(text)

                    when(checkResult) {
                        is InputCheckResult.Success -> emitter.success(text)
                        is InputCheckResult.NotValid -> input.error = checkResult.message
                    }

                }

                input.requestFocus()
                input.setSelection(input.text?.length ?: 0)

            }

            finalizers.add { dialog.dismiss() }
            dialog.show()

        }

    }

    override fun showProgress(title: String?,
                              message: String?,
                              theme: Int?) = completableDialog { context, finalizers ->
        Single.create { _ ->

            val checkedTheme = theme ?: config.defaultProgressDialogTheme
            val dialog =
                    if (checkedTheme != null) ProgressDialog(context, checkedTheme)
                    else ProgressDialog(context)

            dialog
                    .apply {
                        isIndeterminate = true
                        setCancelable(false)
                        setTitle(title)
                        setMessage(message)

                        show()
                    }
                    .also { finalizers.add { it.dismiss() } }

        }
    }

    protected fun completableDialog(
            waitResumedActivity: Boolean = true,
            dialogSource: (Context, MutableSet<() -> Unit>) -> Single<DialogResult<Any>>
    ): Completable = createDialogSource(waitResumedActivity, dialogSource)
            .ignoreElement()

    @Suppress("unused")
    protected fun <T> maybeDialog(
            waitResumedActivity: Boolean = true,
            dialogSource: (Context, MutableSet<() -> Unit>) -> Single<DialogResult<T>>
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
            dialogSource:(Context, MutableSet<() -> Unit>) -> Single<DialogResult<T>>
    ): Single<T> = createDialogSource(waitResumedActivity, dialogSource)
            .flatMap {
                when(it) {
                    is DialogResult.Cancelled ->
                        Single.error(IllegalStateException("Dialog was cancelled."))
                    is DialogResult.Success -> Single.just(it.result)
                }
            }

    private fun <T> createDialogSource(
            waitResumedActivity: Boolean = true,
            dialogSource: (Context, MutableSet<() -> Unit>) -> Single<DialogResult<T>>
    ): Single<DialogResult<T>> {

        return activityTracker.lastResumedActivity
                .switchMapSingle<DialogResult<T>> { optional ->

                    val context = optional.value
                            ?: return@switchMapSingle if (waitResumedActivity) Single.never()
                            else Single.error(IllegalStateException("No any activity resumed."))

                    val finalizers = mutableSetOf<() -> Unit>()

                    dialogSource(context, finalizers)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .doFinally { finalizers.forEach { it() } }

                }
                .firstOrError()

    }

    protected fun <T> SingleEmitter<DialogResult<T>>.cancelled() = this(DialogResult.Cancelled())
    protected fun <T> SingleEmitter<DialogResult<T>>.success(result: T) =
            this(DialogResult.Success(result))

    @Suppress("unused")
    protected sealed class DialogResult<T> {
        class Cancelled<T>: DialogResult<T>()
        data class Success<T>(val result: T): DialogResult<T>()
    }

}