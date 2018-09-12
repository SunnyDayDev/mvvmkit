package me.sunnydaydev.mvvmkit.component

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import me.sunnydaydev.mvvmkit.util.rx.invoke

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 12.09.2018.
 * mail: mail@sunnydaydev.me
 */

interface PermissionRequest {
    val requestCode: Int
    val permissions: Array<String>
}

interface PermissionsInteractor {

    fun requestPermissionIfNot(request: PermissionRequest): Completable

    fun checkPermission(request: PermissionRequest): Single<Boolean>

    fun waitPermissionCheck(request: PermissionRequest): Single<Boolean>

    class ResultHandler {

        private val resultSubject: Subject<PermissionResultEvent> = PublishSubject.create()

        internal val result = resultSubject.hide()

        fun onRequestPermissionsResult(requestCode: Int,
                                       permissions: Array<out String>,
                                       grantResults: IntArray) {

            val result = PermissionResultEvent(requestCode, permissions, grantResults)

            resultSubject(result)

        }

    }

    object Factory {

        fun create(context: Context,
                   activityTracker: ActivityTracker,
                   resultHandler: ResultHandler): PermissionsInteractor =
                PermissionsInteractorImpl(context, activityTracker, resultHandler)

    }

}

internal class PermissionResultEvent(val requestCode: Int,
                                     val permissions: Array<out String>,
                                     val grantResults: IntArray)

class PermissionsNotGrantedError(vararg val permissions: String):
        Error("Permissions not granted: ${permissions.joinToString()}")

class PermissionsActivityNotStartedError: Error()

internal class PermissionsInteractorImpl(
        private val context: Context,
        private val activityTracker: ActivityTracker,
        private val resultHandler: PermissionsInteractor.ResultHandler
) : PermissionsInteractor {

    private val permissionCheckEvent: Subject<PermissionResultEvent> = PublishSubject.create()

    override fun requestPermissionIfNot(request: PermissionRequest): Completable =
            checkPermission(request).flatMapCompletable { granted ->
                if (granted) Completable.complete()
                else activityTracker.lastResumedActivity
                        .firstElement()
                        .flatMapCompletable { (activity) ->

                            activity ?: throw PermissionsActivityNotStartedError()

                            ActivityCompat.requestPermissions(
                                    activity,
                                    request.permissions,
                                    request.requestCode
                            )

                            resultHandler.result
                                    .filter { it.requestCode == request.requestCode }
                                    .firstOrError()
                                    .doOnSuccess(::checkPermissionResult)
                                    .ignoreElement()

                        }
            }

    override fun checkPermission(request: PermissionRequest): Single<Boolean> =
            Single.fromCallable {

                val results = request.permissions
                        .map { ContextCompat.checkSelfPermission(context, it) }
                        .toIntArray()

                val result = PermissionResultEvent(
                        request.requestCode,
                        request.permissions,
                        results
                )

                result.also(permissionCheckEvent::onNext)

            } .flatMap(::checkPermission)

    override fun waitPermissionCheck(request: PermissionRequest): Single<Boolean> =
            Observable.merge(permissionCheckEvent, resultHandler.result)
                    .filter { request.requestCode == it.requestCode }
                    .firstOrError()
                    .flatMap(::checkPermission)

    private fun checkPermissionResult(result: PermissionResultEvent) {

        val failed = result
                .grantResults.any { it != PackageManager.PERMISSION_GRANTED }

        if (failed) {

            val notGrantedPermissions = result.grantResults
                    .mapIndexed { index, r -> index to r }
                    .filterNot { (_, r) -> r == PackageManager.PERMISSION_GRANTED }
                    .map { (index, _) -> result.permissions[index] }

            throw PermissionsNotGrantedError(*notGrantedPermissions.toTypedArray())

        }

    }

    private fun checkPermission(result: PermissionResultEvent): Single<Boolean> =
            Completable.fromAction { checkPermissionResult(result) }
                    .toSingleDefault(true)
                    .onErrorReturnItem(false)

}