package me.sunnydaydev.mvvmkit.sample

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel

/**
 * Created by sunny on 01.06.2018.
 * mail: mail@sunnydaydev.me
 */

class MainActivityViewModelFactory: ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = MainActivityViewModel() as T

}

class MainActivityViewModel: MVVMViewModel() {

    val logoUrl: String = "http://www.geognos.com/api/en/countries/flag/RU.png"

}