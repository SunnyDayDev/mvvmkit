package me.sunnydaydev.mvvmkit.component

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import me.sunnydaydev.mvvmkit.util.Optional
import me.sunnydaydev.mvvmkit.util.PureActivityLifecycleCallbacks
import me.sunnydaydev.mvvmkit.util.rx.invoke

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 12.09.2018.
 * mail: mail@sunnydaydev.me
 */

interface ActivityTracker: Application.ActivityLifecycleCallbacks {

    val inForeground: Observable<Boolean>

    val lastCreatedActivity: Observable<Optional<Activity>>

    val lastStartedActivity: Observable<Optional<Activity>>

    val lastResumedActivity: Observable<Optional<Activity>>

    object Factory {

        fun create(): ActivityTracker = ActivityTrackerImpl()

    }

}

internal class ActivityTrackerImpl: ActivityTracker,
        Application.ActivityLifecycleCallbacks by PureActivityLifecycleCallbacks() {

    private val createdActivities = BehaviorSubject.createDefault<List<Activity>>(emptyList())

    private val startedActivities = BehaviorSubject.createDefault<List<Activity>>(emptyList())

    private val resumedActivities = BehaviorSubject.createDefault<List<Activity>>(emptyList())

    override val lastCreatedActivity: Observable<Optional<Activity>> get() = last(createdActivities)

    override val lastStartedActivity: Observable<Optional<Activity>> get() = last(startedActivities)

    override val lastResumedActivity: Observable<Optional<Activity>> get() = last(resumedActivities)

    override val inForeground: Observable<Boolean> get() =
        lastStartedActivity.map { it.value != null }

    override fun onActivityCreated(activity: Activity, savedState: Bundle?) {
        createdActivities + activity
    }

    override fun onActivityStarted(activity: Activity) {
        startedActivities + activity
    }

    override fun onActivityResumed(activity: Activity) {
        resumedActivities + activity
    }

    override fun onActivityPaused(activity: Activity) {
        resumedActivities - activity
    }

    override fun onActivityStopped(activity: Activity) {
        startedActivities - activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        createdActivities - activity
    }

    private operator fun BehaviorSubject<List<Activity>>.plus(activity: Activity) {
        if (value!!.contains(activity)) return
        this(value!! + activity)
    }

    private operator fun BehaviorSubject<List<Activity>>.minus(activity: Activity) =
            this(value!! - activity)

    private fun last(source: Observable<List<Activity>>): Observable<Optional<Activity>> =
            source.map { Optional(it.lastOrNull()) }

}