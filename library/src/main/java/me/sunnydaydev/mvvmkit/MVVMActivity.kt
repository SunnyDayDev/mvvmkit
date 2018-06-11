package me.sunnydaydev.mvvmkit

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import me.sunnydaydev.mvvmkit.util.ViewLifeCycle
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel

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

}

abstract class MVVMActivity<Binding: ViewDataBinding>: BaseMVVMActivity() {

    // region Abstract

    protected abstract val viewModelFactory: ViewModelProvider.Factory

    protected abstract val viewModelVariableId: Int

    protected abstract val binding: Binding

    protected open val viewLifeCycle: ViewLifeCycle? = null

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
                .also {
                    binding.setVariable(viewModelVariableId, it)
                    if (it is OnBackPressedListener) {
                        addOnBackPressedListener(it)
                    }
                }

        viewLifeCycle?.let(lifecycle::addObserver)
    }

    protected open fun proceedInjection() {
        // no-op
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.unbind()
    }

}