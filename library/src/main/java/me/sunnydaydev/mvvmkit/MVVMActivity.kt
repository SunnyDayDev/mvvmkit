package me.sunnydaydev.mvvmkit

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnydaydev.me
 */

abstract class MVVMActivity<Binding: ViewDataBinding>: AppCompatActivity() {

    // region Abstract

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModelVariableId: Int

    protected abstract val binding: Binding

    protected abstract fun getViewModel(provider: ViewModelProvider): MVVMViewModel

    // endregion

    private lateinit var vm: MVVMViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        proceedInjection()
        onViewModelCreate(savedInstanceState)
    }

    protected open fun onViewModelCreate(savedInstanceState: Bundle?) {
        vm = getViewModel(ViewModelProviders.of(this, viewModelFactory))
        binding.setVariable(viewModelVariableId, vm)
        lifecycle.addObserver(vm)
    }

    protected open fun proceedInjection() {
        // no-op
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

    override fun onBackPressed() {
        if (!vm.onBackPressed()) {
            super.onBackPressed()
        }
    }

}