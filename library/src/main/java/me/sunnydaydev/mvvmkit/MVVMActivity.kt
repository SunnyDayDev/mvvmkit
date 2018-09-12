package me.sunnydaydev.mvvmkit

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import me.sunnydaydev.mvvmkit.util.LateInitValue
import me.sunnydaydev.mvvmkit.util.PermissionRequestResultHandler
import me.sunnydaydev.mvvmkit.util.lateinit
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel
import timber.log.Timber

/**
 * Created by sunny on 03.05.2018.
 * mail: mail@sunnydaydev.me
 */

abstract class BaseMVVMActivity: AppCompatActivity() {

    private val onBackPressedListeners: MutableSet<OnBackPressedListener> =
            sortedSetOf(Comparator { l1, l2 -> l2.prioritet.compareTo(l1.prioritet) })

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when(item.itemId) {
        android.R.id.home -> onBackPressed().let { true }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val onBackPressedHandled = onBackPressedListeners.any { it.onBackPressed() }
        if (!onBackPressedHandled) {
            super.onBackPressed()
        }
    }

    fun addOnBackPressedListener(listener: OnBackPressedListener) {
        onBackPressedListeners.add(listener)
    }

    fun removeOnBackPressedListener(listener: OnBackPressedListener) {
        onBackPressedListeners.remove(listener)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        (application as? PermissionRequestResultHandler)
                ?.onRequestPermissionsResult(requestCode, permissions, grantResults)
                ?: Timber.i("Application not implement PermissionRequestResultHandler.")
    }

}

abstract class MVVMActivity<Binding: ViewDataBinding>: BaseMVVMActivity() {

    // region Abstract

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModelVariableId: Int

    protected abstract val binding: Binding

    protected abstract fun getViewModel(provider: ViewModelProvider): MVVMViewModel

    // endregion

    private val lateinitViewModelValue = LateInitValue<MVVMViewModel>()
    protected val viewModel: MVVMViewModel by lateinit(lateinitViewModelValue)

    override fun onCreate(savedInstanceState: Bundle?) {
        val injected = proceedInjectionBeforeOnCreate()
        super.onCreate(savedInstanceState)
        if (!injected) proceedInjectionAtOnCreate()
        onViewModelCreate(savedInstanceState)
    }

    protected open fun onViewModelCreate(savedInstanceState: Bundle?) {
        val viewModelValue = getViewModel(ViewModelProviders.of(this, viewModelFactory))
        lateinitViewModelValue.set(viewModelValue)

        with(viewModel) {

            binding.setVariable(viewModelVariableId, this)
            if (this is OnBackPressedListener) {
                addOnBackPressedListener(this)
            }

            onViewModelCreated(viewModel)

        }

    }

    private fun onViewModelCreated(viewModel: MVVMViewModel) {
        // no-op
    }

    protected open fun proceedInjectionBeforeOnCreate(): Boolean = false

    protected open fun proceedInjectionAtOnCreate() {
        // no-op
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()

        with(viewModel) {
            if (this is OnBackPressedListener) {
                removeOnBackPressedListener(this)
            }
        }

    }

}