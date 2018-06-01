package me.sunnydaydev.mvvmkit.sample

import android.arch.lifecycle.ViewModelProvider
import me.sunnydaydev.mvvmkit.MVVMActivity
import me.sunnydaydev.mvvmkit.sample.databinding.ActivityMainBinding
import me.sunnydaydev.mvvmkit.util.setContentBinding
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel
import me.sunnydaydev.mvvmkit.viewModel.get

class MainActivity : MVVMActivity<ActivityMainBinding>() {

    override val viewModelFactory: ViewModelProvider.Factory = MainActivityViewModelFactory()

    override val viewModelVariableId = BR.vm

    override val binding by lazy<ActivityMainBinding> { setContentBinding(R.layout.activity_main) }

    override fun getViewModel(provider: ViewModelProvider): MVVMViewModel =
            provider[MainActivityViewModel::class]

}
