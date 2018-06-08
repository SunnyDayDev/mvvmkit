package me.sunnydaydev.mvvmkit.sample.vm

import androidx.databinding.Bindable
import me.sunnydaydev.mvvmkit.observable.bindable
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel

/**
 * Created by sunny on 08.06.2018.
 * mail: mail@sunnydaydev.me
 */

internal class OrangeViewModel(id: Int): MVVMViewModel() {

    @get:Bindable var id by bindable("$id")


    class Factory: ColorsFactory {

        private var autoIncrementId: Int = 0
            get() = ++field

        override fun create(): MVVMViewModel = OrangeViewModel(autoIncrementId)

    }

}