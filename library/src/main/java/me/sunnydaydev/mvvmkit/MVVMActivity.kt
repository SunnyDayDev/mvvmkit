package me.sunnydaydev.mvvmkit

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.sunnydaydev.mvvmkit.viewModel.BaseViewModel

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnydaydev.me
 */

abstract class MVVMActivity<Binding: ViewDataBinding>: AppCompatActivity() {

    // region Abstract

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModelVariableId: Int

    protected abstract val binding: Binding

    protected abstract fun <T> getViewModel(provider: ViewModelProvider): T

    // endregion

    private lateinit var vm: BaseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onViewModelCreate(savedInstanceState)
    }

    protected open fun onViewModelCreate(savedInstanceState: Bundle?) {
        vm = getViewModel(ViewModelProviders.of(this, viewModelFactory))
        binding.setVariable(viewModelVariableId, vm)
        lifecycle.addObserver(vm)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

}