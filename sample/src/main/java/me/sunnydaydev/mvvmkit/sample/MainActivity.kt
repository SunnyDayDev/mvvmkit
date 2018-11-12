package me.sunnydaydev.mvvmkit.sample

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.lifecycle.ViewModelProvider
import me.sunnydaydev.mvvmkit.MVVMActivity
import me.sunnydaydev.mvvmkit.sample.databinding.ActivityMainBinding
import me.sunnydaydev.mvvmkit.sample.vm.MainActivityViewModel
import me.sunnydaydev.mvvmkit.sample.vm.MainActivityViewModelFactory
import me.sunnydaydev.mvvmkit.util.setContentBinding
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel
import me.sunnydaydev.mvvmkit.viewModel.get
import androidx.databinding.library.baseAdapters.BR

class MainActivity : MVVMActivity<ActivityMainBinding>() {

    override val viewModelFactory: ViewModelProvider.Factory by lazy {
        MainActivityViewModelFactory(lifecycle)
    }

    override val viewModelVariableId = BR.vm

    override val binding by lazy<ActivityMainBinding> { setContentBinding(R.layout.activity_main) }

    override fun getViewModel(provider: ViewModelProvider): MVVMViewModel =
            provider[MainActivityViewModel::class]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.webview.webViewClient = object : WebViewClient() {}
    }

}
