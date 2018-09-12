package me.sunnydaydev.mvvmkit.util.rx

import io.reactivex.*

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 23.08.2018.
 * mail: mail@sunnydaydev.me
 */

operator fun <T> Emitter<T>.invoke() = onComplete()
operator fun <T> Emitter<T>.invoke(value: T) = onNext(value)
operator fun <T> Emitter<T>.invoke(throwable: Throwable) = onError(throwable)
operator fun <T> ObservableEmitter<T>.invoke(throwable: Throwable) = tryOnError(throwable)

operator fun CompletableEmitter.invoke() = onComplete()
operator fun CompletableEmitter.invoke(error: Throwable) = tryOnError(error)

operator fun <T> SingleEmitter<T>.invoke(value: T) = onSuccess(value)
operator fun <T> SingleEmitter<T>.invoke(error: Throwable) = tryOnError(error)

operator fun <T> MaybeEmitter<T>.invoke() = onComplete()
operator fun <T> MaybeEmitter<T>.invoke(value: T) = onSuccess(value)
operator fun <T> MaybeEmitter<T>.invoke(error: Throwable) = tryOnError(error)
