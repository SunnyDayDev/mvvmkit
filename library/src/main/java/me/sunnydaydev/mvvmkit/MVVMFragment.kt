package me.sunnydaydev.mvvmkit

import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnydaydev.me
 */

abstract class MVVMFragment<Binding: ViewDataBinding>: Fragment()  {

    // region Abstract

    protected abstract val viewModelVariableId: Int

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected abstract fun onCreateBinding(inflater: LayoutInflater,
                                           container: ViewGroup?,
                                           savedInstanceState: Bundle?): Binding

    protected abstract fun getViewModel(provider: ViewModelProvider): MVVMViewModel

    // endregion

    private lateinit var vm: MVVMViewModel

    @Suppress("MemberVisibilityCanBePrivate")
    protected var binding: Binding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = getViewModel(ViewModelProviders.of(this, viewModelFactory))
        lifecycle.addObserver(vm)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return onCreateBinding(inflater, container, savedInstanceState)
                .apply { setVariable(viewModelVariableId, vm) }
                .root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
        binding = null
    }

}