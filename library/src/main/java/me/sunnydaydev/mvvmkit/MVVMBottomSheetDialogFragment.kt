package me.sunnydaydev.mvvmkit

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnydaydev.me
 */

abstract class MVVMBottomSheetDialogFragment<Binding: ViewDataBinding>: BottomSheetDialogFragment()  {

    // region Abstract

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModelVariableId: Int

    protected abstract fun onCreateBinding(inflater: LayoutInflater,
                                           container: ViewGroup?,
                                           savedInstanceState: Bundle?): Binding

    protected abstract fun getViewModel(provider: ViewModelProvider): MVVMViewModel

    // endregion

    protected lateinit var viewModel: MVVMViewModel
        private set

    protected var binding: Binding? = null
        private set

    override fun onAttach(context: Context) {
        super.onAttach(context)
        proceedInjection()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onViewModelCreate(savedInstanceState)
    }

    protected open fun onViewModelCreate(savedInstanceState: Bundle?) {
        viewModel = getViewModel(ViewModelProviders.of(this, viewModelFactory))
        onViewModelCreated(viewModel)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            onCreateBinding(inflater, container, savedInstanceState)
                    .apply {
                        setVariable(viewModelVariableId, this)
                    }
                    .also { binding = it }
                    .root

    private fun onViewModelCreated(viewModel: MVVMViewModel) {
        // no-op
    }

    protected open fun proceedInjection() {
        // no-op
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding?.unbind()
        binding = null
    }

}