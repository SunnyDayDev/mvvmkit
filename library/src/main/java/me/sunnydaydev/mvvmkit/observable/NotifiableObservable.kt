package me.sunnydaydev.mvvmkit.observable

import android.databinding.BaseObservable
import android.databinding.Observable

/**
 * Created by sunny on 31.05.2018.
 * mail: mail@sunnydaydev.me
 */

interface NotifiableObservable: Observable {

    fun notifyChange()

    fun notifyPropertyChanged(fieldId: Int)

}

class NotifiableBaseObservable: BaseObservable(), NotifiableObservable