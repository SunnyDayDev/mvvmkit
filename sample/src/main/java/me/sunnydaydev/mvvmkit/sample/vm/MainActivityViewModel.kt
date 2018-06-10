package me.sunnydaydev.mvvmkit.sample.vm

import androidx.databinding.Bindable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import me.sunnydaydev.mvvmkit.observable.MVVMArrayList
import me.sunnydaydev.mvvmkit.observable.MVVMList
import me.sunnydaydev.mvvmkit.observable.MergedMVVMList
import me.sunnydaydev.mvvmkit.observable.bindable
import me.sunnydaydev.mvvmkit.viewModel.MVVMViewModel
import java.util.concurrent.TimeUnit
import kotlin.math.min

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

    val allItems by lazy {
        MergedMVVMList.Builder<MVVMViewModel>()
                .add(black)
                .add(oranges)
                .add(greens)
                .add(blues)
                .build()
    }

    @get:Bindable var refreshing by bindable(false)

    private val orangeFactory: ColorsFactory = OrangeViewModel.Factory()
    private val greenFactory: ColorsFactory  = GreenViewModel.Factory()
    private val blueFactory: ColorsFactory  = BlueViewModel.Factory()
    private val blackFactory: ColorsFactory  = BlackViewModel.Factory()

    private val black = blackFactory.create()
    private val oranges: MVVMList<MVVMViewModel> = MVVMArrayList()
    private val greens: MVVMList<MVVMViewModel> = MVVMArrayList()
    private val blues: MVVMList<MVVMViewModel> = MVVMArrayList()

    fun addOrange() {
        oranges.add(orangeFactory.create())
    }

    fun removeOrange() {
        if (oranges.isNotEmpty()) oranges.removeAt(0)
    }

    fun addBlue() {
        blues.add(blueFactory.create())
    }

    fun removeBlue() {
        if (blues.isNotEmpty()) blues.removeAt(0)
    }

    fun addGreen() {
        greens.add(greenFactory.create())
    }

    fun removeGreen() {
        if (greens.isNotEmpty()) greens.removeAt(0)
    }

    fun setGreen() {
        val items = (1..3).map { greenFactory.create() }
        val startIndex = min(1, greens.size)
        val count = min(2, greens.size - startIndex)
        greens.setAll(items, startIndex, count)
    }

    fun onRefresh() {
        refreshing = true
        Completable.timer(3, TimeUnit.SECONDS, Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { refreshing = false }
    }

    override fun onCleared() {
        super.onCleared()
        black.clearViewModel()
    }

}