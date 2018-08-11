package me.sunnydaydev.mvvmkit.util

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * Created by sunny on 09.06.2018.
 * mail: mail@sunnydaydev.me
 */

class PureActivityLifecycleCallbacks: Application.ActivityLifecycleCallbacks {

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityDestroyed(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

}