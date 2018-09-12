package me.sunnydaydev.mvvmkit.util.rx

import io.reactivex.Completable
import io.reactivex.subjects.CompletableSubject
import io.reactivex.subjects.MaybeSubject
import io.reactivex.subjects.SingleSubject
import io.reactivex.subjects.Subject
import me.sunnydaydev.mvvmkit.util.Optional

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 01.08.2018.
 * mail: mail@sunnydaydev.me
 */

operator fun <T> Subject<T>.invoke(value: T) = onNext(value)
operator fun <T> Subject<T>.invoke(value: Throwable) = onError(value)

@JvmName("invokeOptional")
operator fun <T> Subject<Optional<T>>.invoke(value: T?) = onNext(Optional(value))

operator fun Subject<Unit>.invoke() = invoke(Unit)
fun <T> Subject<T>.notifier(value: T): Completable = Completable.fromAction { this(value) }
fun Subject<Unit>.notifier() = notifier(Unit)

operator fun CompletableSubject.invoke() = onComplete()
operator fun CompletableSubject.invoke(error: Throwable) = onError(error)

operator fun <T> MaybeSubject<T>.invoke() = onComplete()
operator fun <T> MaybeSubject<T>.invoke(value: T) = onSuccess(value)
operator fun <T> MaybeSubject<T>.invoke(value: Throwable) = onError(value)

operator fun <T> SingleSubject<T>.invoke(value: T) = onSuccess(value)
operator fun <T> SingleSubject<T>.invoke(value: Throwable) = onError(value)