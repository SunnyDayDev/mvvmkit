package me.sunnydaydev.mvvmkit.util

/**
 * Created by Aleksandr Tcikin (SunnyDay.Dev) on 12.09.2018.
 * mail: mail@sunnydaydev.me
 */
 
interface PermissionRequestResultHandler {

    fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<out String>,
                                   grantResults: IntArray)

}