package me.sunnydaydev.mvvmkit

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.content.Context
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.sunnydaydev.mvvmkit.util.ViewLifeCycle
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnydaydev.me
 */

abstract class MVVMFragment<Binding: ViewDataBinding>: Fragment(), OnBackPressedListener {

    // region Abstract

    protected abstract val viewModelVariableId: Int

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected open val viewLifeCycle: ViewLifeCycle? = null

    protected abstract fun onCreateBinding(inflater: LayoutInflater,
                                           container: ViewGroup?,
                                           savedInstanceState: Bundle?): Binding

    protected abstract fun getViewModel(provider: ViewModelProvider): MVVMViewModel

    // endregion

    private lateinit var vm: MVVMViewModel

    @Suppress("MemberVisibilityCanBePrivate")
    protected var binding: Binding? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        proceedInjection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm = getViewModel(ViewModelProviders.of(this, viewModelFactory))
        viewLifeCycle?.let(lifecycle::addObserver)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        (activity as? BaseMVVMActivity)?.addOnBackPressedListener(this)

        return onCreateBinding(inflater, container, savedInstanceState)
                .apply { setVariable(viewModelVariableId, vm) }
                .root

    }

    override fun onDestroyView() {
        super.onDestroyView()

        (activity as? BaseMVVMActivity)?.removeOnBackPressedListener(this)
        binding?.unbind()
        binding = null

    }

    protected open fun proceedInjection() {
        // no-op
    }

    override fun onBackPressed(): Boolean = (vm as? OnBackPressedListener)?.onBackPressed() ?: false

}